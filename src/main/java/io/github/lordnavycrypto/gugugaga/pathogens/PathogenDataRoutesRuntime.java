package io.github.lordnavycrypto.gugugaga.pathogens;

import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * World-data-only ROUTING of 15 signals on vanilla Minecraft game events.
 *
 * Air/rain/vector "routes" are deliberately imaginary game cues. Animal,
 * player and held-item movement act as bounded SIGNAL trail events, not
 * biological contagion or item decontamination state.
 *
 * No pathogens have their own Block or Entity; no infected mobs are created.
 * No real transmission rates, viral replication, infection probabilities,
 * airborne dispersion, dose thresholds or clinical effects.
 */
public final class PathogenDataRoutesRuntime {
    private PathogenDataRoutesRuntime(){}
    private static final int TICK_INTERVAL=80;
    private static final int MAX_AMBIENT_TRANSFERS=24;
    private static final int MAX_AMBIENT_SOURCES_SCANNED=64;
    private static final int MAX_MOVING_ANIMALS=64;
    private static final int MAX_MOVING_PLAYERS=64;
    private static final int MAX_TRACKED_ANIMALS=256;
    private record Location(String dimension,PathogenSpatialFieldMath.Cell cell){}
    private static final class History {
        final Map<UUID,Location> players=new HashMap<>();
        // Bounded, access-ordered location history; no server/world objects stored.
        final Map<UUID,Location> animals=new LinkedHashMap<>(256,0.75f,true);
    }
    private static final Map<MinecraftServer,History> HISTORIES=new WeakHashMap<>();

    private static boolean loadedAndClose(ServerLevel level,List<ServerPlayer> watchers,
            PathogenSpatialFieldMath.Cell cell){
        // Four game cells = one vanilla 16-block chunk in X/Z.
        if(!level.getChunkSource().hasChunk(cell.x()>>2,cell.z()>>2))return false;
        double midX=cell.x()*4.0+2,midZ=cell.z()*4.0+2;
        for(ServerPlayer watcher:watchers){
            double dx=watcher.getX()-midX,dz=watcher.getZ()-midZ;
            if(dx*dx+dz*dz<=48*48)return true;
        }
        return false;
    }
    private static boolean moveFor(ServerLevel world,List<ServerPlayer> watchers,
            PathogenSpatialFieldSavedData field,
            PathogenSpatialFieldMath.Cell from,PathogenSpatialFieldMath.Cell to,
            PathogenRoutePolicy15.Route route,String mobId){
        if(from.equals(to) ||
                !loadedAndClose(world,watchers,from)||
                !loadedAndClose(world,watchers,to))return false;
        for(int channel=0;channel<15;channel++){
            if(field.get(from,channel)<1 ||
                    !PathogenRoutePolicy15.allows(channel,route))continue;
            String taxon=PathogenRoster15.FIRST_FIFTEEN.get(channel).id();
            if(mobId!=null&&!VanillaHostMapping15.supportsEntity(taxon,mobId))
                continue; // animal movement only for its mapped vanilla category
            if(field.transfer(from,to,channel,route,true))return true;
        }
        return false;
    }
    private static int environmentalGameEvents(ServerLevel world,
            List<ServerPlayer> watchers,PathogenSpatialFieldSavedData field,
            long frame){
        // Snapshot prevents this frame's newly filled cells from acting
        // immediately. A maximum of 24 event moves per dimension/update.
        var snapshot=field.snapshot();
        var cells=field.routeCandidates(MAX_AMBIENT_SOURCES_SCANNED);
        int attempted=0,applied=0;
        boolean rain=world.isRaining();
        for(var from:cells){
            if(attempted>=MAX_AMBIENT_TRANSFERS)break;
            if(!loadedAndClose(world,watchers,from))continue;
            int[] levels=snapshot.get(from);
            for(int offset=0;offset<15;offset++){
                int channel=(PathogenRouteFairScheduler.startChannel(from,frame)
                        +offset)%15;
                if(levels[channel]<=0)continue;
                PathogenRoutePolicy15.Route mode=rain
                        ?PathogenRoutePolicy15.Route.RAIN_VISUAL
                        :PathogenRoutePolicy15.Route.AIR_VISUAL;
                if(!PathogenRoutePolicy15.allows(channel,mode)){
                    mode=PathogenRoutePolicy15.Route.VIRTUAL_VECTOR;
                    if(!PathogenRoutePolicy15.allows(channel,mode))continue;
                }
                var dest=from.adjacent((int)Math.floorMod(
                        frame+channel+from.x()*3L+from.z()*5L,4));
                if(!loadedAndClose(world,watchers,dest))continue;
                attempted++;
                if(field.transfer(from,dest,channel,mode,true)){
                    applied++;
                    if(applied<=8){
                        double px=dest.x()*4.0+2,pz=dest.z()*4.0+2;
                        for(ServerPlayer viewer:watchers){
                            double dx=viewer.getX()-px,dz=viewer.getZ()-pz;
                            if(dx*dx+dz*dz<=24*24){
                                world.sendParticles(ParticleTypes.ENCHANT,
                                    px,viewer.getY()+0.3,pz,1,0.1,0.1,0.1,0.0);
                                break;
                            }
                        }
                    }
                }
                break; // At most one route event per source cell / frame.
            }
        }
        return applied;
    }
    public static void onTick(ServerTickEvent.Post event){
        if(!NightwatchConfig.PATHOGEN_FIELD_ENABLED.get()
                ||!NightwatchConfig.PATHOGEN_ROUTE_EVENTS_ENABLED.get())return;
        MinecraftServer server=event.getServer();
        long time=server.overworld().getGameTime();
        if(time%TICK_INTERVAL!=0L)return;
        History history=HISTORIES.computeIfAbsent(server,s->new History());
        Set<UUID> online=new HashSet<>();
        Set<UUID> observedAnimals=new HashSet<>();
        int playerBudget=0,animalBudget=0;
        for(ServerLevel world:server.getAllLevels()){
            List<ServerPlayer> watchers=world.players().stream()
                    .filter(p->p instanceof ServerPlayer)
                    .map(p->(ServerPlayer)p).toList();
            if(watchers.isEmpty())continue;
            var field=PathogenSpatialFieldSavedData.forLevel(world);
            String dimension=world.dimension().location().toString();
            for(ServerPlayer player:watchers){
                online.add(player.getUUID());
                if(playerBudget>=MAX_MOVING_PLAYERS)continue;
                playerBudget++;
                var here=PathogenSpatialFieldRuntime.cell(player.blockPosition());
                var previous=history.players.put(player.getUUID(),
                        new Location(dimension,here));
                if(previous==null||!dimension.equals(previous.dimension()))continue;
                if(!player.getMainHandItem().isEmpty() &&
                        moveFor(world,watchers,field,previous.cell(),here,
                            PathogenRoutePolicy15.Route.HELD_ITEM_MOTION,null))
                    continue;
                moveFor(world,watchers,field,previous.cell(),here,
                    PathogenRoutePolicy15.Route.PLAYER_MOTION,null);
            }
            if(field.size()==0)continue;
            for(ServerPlayer viewer:watchers){
                if(animalBudget>=MAX_MOVING_ANIMALS)break;
                for(Animal animal:world.getEntitiesOfClass(Animal.class,
                        viewer.getBoundingBox().inflate(16),Animal::isAlive)){
                    if(animalBudget>=MAX_MOVING_ANIMALS)break;
                    if(!observedAnimals.add(animal.getUUID()))continue;
                    animalBudget++;
                    var here=PathogenSpatialFieldRuntime.cell(animal.blockPosition());
                    var previous=history.animals.put(animal.getUUID(),
                            new Location(dimension,here));
                    if(previous==null||!dimension.equals(previous.dimension()))continue;
                    String mobId=BuiltInRegistries.ENTITY_TYPE
                            .getKey(animal.getType()).toString();
                    moveFor(world,watchers,field,previous.cell(),here,
                            PathogenRoutePolicy15.Route.ANIMAL_MOTION,mobId);
                }
            }
            environmentalGameEvents(world,watchers,field,time/TICK_INTERVAL);
        }
        history.players.keySet().retainAll(online);
        // Do not discard trails for an animal just because the 64-event budget
        // did not sample it on this one frame; evict ONLY oldest observations.
        while(history.animals.size()>MAX_TRACKED_ANIMALS){
            Iterator<UUID> oldest=history.animals.keySet().iterator();
            oldest.next();
            oldest.remove();
        }
    }
}
