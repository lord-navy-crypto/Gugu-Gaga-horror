package io.github.lordnavycrypto.gugugaga.pathogens;

import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Per-player, read-only, optional PARTICLE VIEW into one selected 4x4
 * Minecraft numerical field. Does not create ANY special blocks, mobs,
 * particle entities or independent pathogen state. Every request uses
 * vanilla ServerLevel#sendParticles(viewer,...) targeted at its owner.
 */
public final class FieldParticleOverlayRuntime {
    private FieldParticleOverlayRuntime(){}
    private static final int INTERVAL=60, MAX_VIEWERS_PER_FRAME=12;
    private static final int MAX_PARTICLES_PER_VIEWER=12;
    private static final Map<MinecraftServer,Map<UUID,Session>> SESSIONS=
            new WeakHashMap<>();

    private static final class Session {
        FieldParticleSampling.Choice selected=
                FieldParticleSampling.choose("potato_virus_y");
        String dimension="";
        Map<PathogenSpatialFieldMath.Cell,Integer> last=Map.of();
    }
    public static boolean isEnabled(ServerPlayer player){
        var byPlayer=SESSIONS.get(player.serverLevel().getServer());
        return byPlayer!=null && byPlayer.containsKey(player.getUUID());
    }
    public static String selection(ServerPlayer player){
        var byPlayer=SESSIONS.get(player.serverLevel().getServer());
        var session=byPlayer==null?null:byPlayer.get(player.getUUID());
        return session==null?"none":session.selected.taxon();
    }
    public static void enable(ServerPlayer player) {
        SESSIONS.computeIfAbsent(player.serverLevel().getServer(),
                ignored->new HashMap<>()).computeIfAbsent(player.getUUID(),
                ignored->new Session());
    }
    public static void select(ServerPlayer player,String taxon){
        var selected=FieldParticleSampling.choose(taxon);
        if(!selected.worldVisualAllowed())
            throw new IllegalArgumentException("Reference has no vanilla world visual");
        enable(player);
        var s=SESSIONS.get(player.serverLevel().getServer()).get(player.getUUID());
        s.selected=selected;
        s.dimension="";
        s.last=Map.of();
    }
    public static void disable(ServerPlayer player){
        MinecraftServer server=player.serverLevel().getServer();
        var membership=SESSIONS.get(server);
        if(membership==null)return;
        membership.remove(player.getUUID());
        if(membership.isEmpty())SESSIONS.remove(server);
    }
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event){
        if(event.getEntity() instanceof ServerPlayer player)disable(player);
    }
    public static void onServerStopped(ServerStoppedEvent event){
        SESSIONS.remove(event.getServer());
    }

    private static ParticleOptions style(FieldParticleSampling.Choice choice){
        // Five highlighted examples have distinct vanilla art; other IDs
        // fall back to their biological IDENTITY CATEGORY, not kinetics.
        return switch(choice.taxon()){
            case "influenza_a_virus" -> ParticleTypes.ENCHANT;
            case "potato_virus_y" -> ParticleTypes.HAPPY_VILLAGER;
            case "salmonella_enterica" -> ParticleTypes.CLOUD;
            case "puccinia_graminis" -> ParticleTypes.COMPOSTER;
            case "varroa_destructor" -> ParticleTypes.END_ROD;
            default -> switch(choice.family()){
                case VIRUS -> ParticleTypes.ENCHANT;
                case BACTERIUM -> ParticleTypes.CLOUD;
                case OOMYCETE,FUNGUS -> ParticleTypes.COMPOSTER;
                case PARASITE -> ParticleTypes.END_ROD;
                case PHAGE -> ParticleTypes.ENCHANT;
            };
        };
    }

    /** Cosmetic positions only: no game-field write or mob status changes. */
    private record Anchor(double x,double y,double z) {}

    private static Anchor anchor(ServerPlayer player,
            FieldParticleSampling.Marker marker,List<Animal> nearbyAnimals){
        var level=player.serverLevel();
        var cell=marker.cell();
        int x0=cell.x()*PathogenSpatialFieldMath.CELL_EDGE_BLOCKS;
        int z0=cell.z()*PathogenSpatialFieldMath.CELL_EDGE_BLOCKS;
        int cx=x0+2,cz=z0+2;
        // No data load or height queries on a chunk that is not already loaded.
        if(!level.getChunkSource().hasChunk(cx>>4,cz>>4))return null;
        double observerY=player.getY();
        int surfaceY=level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,cx,cz);

        // Ground projections are decorative, not a claim of biological hosts.
        // Check at most sixteen columns, in the chosen 4x4 cell only.
        if(FieldParticleViewPolicy.surfaceInView(observerY,surfaceY)){
            for(int dz=0;dz<4;dz++)for(int dx=0;dx<4;dx++){
                int px=x0+dx,pz=z0+dz;
                if(!level.getChunkSource().hasChunk(px>>4,pz>>4))continue;
                int py=level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px,pz);
                if(!FieldParticleViewPolicy.surfaceInView(observerY,py))continue;
                for(int offset=0;offset<=1;offset++){
                    int y=py-offset;
                    if(y<level.getMinBuildHeight())continue;
                    if(level.getBlockState(new BlockPos(px,y,pz)).getBlock()
                            instanceof CropBlock)
                        return new Anchor(px+0.5,y+0.72,pz+0.5);
                }
            }
        }

        // Nearby vanilla animals can provide visual landmarks only. This
        // deliberately never sets a status, effect or infection on an animal.
        for(Animal animal:nearbyAnimals){
            if(!animal.isAlive())continue;
            BlockPos pos=animal.blockPosition();
            if(FieldParticleViewPolicy.insideCell(pos.getX(),pos.getZ(),
                    cell.x(),cell.z())
                    &&FieldParticleViewPolicy.animalInView(observerY,animal.getY()))
                return new Anchor(animal.getX(),
                        animal.getY()+animal.getBbHeight()+0.2,animal.getZ());
        }

        if(FieldParticleViewPolicy.surfaceInView(observerY,surfaceY))
            return new Anchor(cx+0.5,surfaceY+0.3,cz+0.5);

        // Deep caves should not draw markers on a mountain far above the
        // observer. Find nearby air without breaking blocks or loading chunks.
        int baseY=player.blockPosition().getY();
        for(int dy=0;dy<=2;dy++){
            int y=baseY+dy;
            if(y<level.getMinBuildHeight()||y>=level.getMaxBuildHeight())continue;
            if(level.getBlockState(new BlockPos(cx,y,cz)).isAir())
                return new Anchor(cx+0.5,y+0.5,cz+0.5);
        }
        return null; // Skip solid occluded cells instead of particle clipping.
    }

    private static void render(ServerPlayer player,Session s){
        var level=player.serverLevel();
        String dimension=level.dimension().location().toString();
        if(!dimension.equals(s.dimension)){
            s.last=Map.of();
            s.dimension=dimension;
        }
        var center=PathogenSpatialFieldRuntime.cell(player.blockPosition());
        var choice=s.selected;
        var original=choice.store()==FieldParticleSampling.Store.ORIGINAL_15
                ?PathogenSpatialFieldSavedData.forLevel(level):null;
        var extension=choice.store()==FieldParticleSampling.Store.EXTENSION_8
                ?EcologyExtendedSavedData.forLevel(level):null;
        var scan=FieldParticleSampling.scan(center,choice,
            cell->level.getChunkSource().hasChunk(cell.x()>>2,cell.z()>>2),
            cell->original!=null?original.get(cell,choice.channel())
                    :extension.get(cell,choice.channel()),s.last);
        s.last=scan.current();
        int sent=0;
        ParticleOptions particle=style(choice);
        // Single bounded vanilla-entity lookup per observer, not per marker.
        List<Animal> nearbyAnimals=scan.markers().isEmpty()?List.of():
                level.getEntitiesOfClass(Animal.class,
                    player.getBoundingBox().inflate(16.0,8.0,16.0),
                    Animal::isAlive);
        for(var marker:scan.markers()){
            if(sent>=MAX_PARTICLES_PER_VIEWER)break;
            Anchor at=anchor(player,marker,nearbyAnimals);
            if(at==null)continue;
            if(level.sendParticles(player,particle,false,at.x(),at.y(),at.z(),
                    1,0.15,0.12,0.15,0.0))sent++;
            if(marker.change()==FieldParticleSampling.Change.NEW_OR_INCREASED
                    &&sent<MAX_PARTICLES_PER_VIEWER) {
                if(level.sendParticles(player,ParticleTypes.END_ROD,false,
                        at.x(),at.y()+0.25,at.z(),
                        1,0.08,0.08,0.08,0.0))sent++;
            }
        }
    }
    public static void onTick(ServerTickEvent.Post event){
        if(!NightwatchConfig.FIELD_PARTICLE_OVERLAY_ENABLED.get())return;
        var server=event.getServer();
        long tick=server.overworld().getGameTime();
        if(tick%INTERVAL!=0)return;
        var byPlayer=SESSIONS.get(server);
        if(byPlayer==null||byPlayer.isEmpty())return;
        List<ServerPlayer> watchers=new ArrayList<>();
        for(ServerPlayer player:server.getPlayerList().getPlayers())
            if(byPlayer.containsKey(player.getUUID()))watchers.add(player);
        if(watchers.isEmpty())return;
        for(int index:FieldParticleViewPolicy.viewerSlots(watchers.size(),
                MAX_VIEWERS_PER_FRAME,tick/INTERVAL)){
            ServerPlayer player=watchers.get(index);
            render(player,byPlayer.get(player.getUUID()));
        }
    }
}
