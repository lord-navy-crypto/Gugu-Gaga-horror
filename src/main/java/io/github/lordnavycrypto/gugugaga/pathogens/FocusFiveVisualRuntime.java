package io.github.lordnavycrypto.gugugaga.pathogens;

import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Five distinct cosmetic game observers, reading original vanilla objects.
 * No custom Block/Entity, symptoms, actual dose or infectious conditions.
 * At most 24 mobs and 64 plants sampled every 120 server game ticks.
 */
public final class FocusFiveVisualRuntime {
    private FocusFiveVisualRuntime(){}
    private static final int INTERVAL=120, MAX_ANIMALS=24, MAX_PLANTS=64;
    private static ParticleOptions particle(FocusFiveProfiles.Art art){
        return switch(art){
            case BIRD_AURA -> ParticleTypes.ENCHANT;
            case POTATO_MOSAIC -> ParticleTypes.HAPPY_VILLAGER;
            case FARM_TRACE -> ParticleTypes.CLOUD;
            case WHEAT_GLOW -> ParticleTypes.COMPOSTER;
            case BEE_SPARKLE -> ParticleTypes.END_ROD;
        };
    }
    private static FocusFiveReadout.Reading observe(ServerLevel level,
            BlockPos pos,String target,boolean animal){
        var at=PathogenSpatialFieldRuntime.cell(pos);
        var core=PathogenSpatialFieldSavedData.forLevel(level);
        var extended=EcologyExtendedSavedData.forLevel(level);
        int[] standard=new int[15],additional=new int[8];
        for(int i=0;i<standard.length;i++)standard[i]=core.get(at,i);
        for(int i=0;i<additional.length;i++)additional[i]=extended.get(at,i);
        var readings=FocusFiveReadout.compatible(target,animal,standard,additional);
        return readings.isEmpty()?null:readings.get(0);
    }
    private static boolean visible(FocusFiveReadout.Reading reading){
        return reading!=null &&
                (reading.tier()==FocusFiveReadout.Tier.VISIBLE||
                 reading.tier()==FocusFiveReadout.Tier.HIGHLIGHT);
    }
    public static void onTick(ServerTickEvent.Post event){
        if(!NightwatchConfig.FOCUS_FIVE_VISUALS_ENABLED.get())return;
        var server=event.getServer();
        long time=server.overworld().getGameTime();
        if(time%INTERVAL!=0)return;
        int animalChecks=0,plantChecks=0;
        for(ServerLevel level:server.getAllLevels()){
            if(PathogenSpatialFieldSavedData.forLevel(level).size()==0 &&
                    EcologyExtendedSavedData.forLevel(level).size()==0)continue;
            Set<UUID> seenAnimals=new HashSet<>();
            Set<Long> seenBlocks=new HashSet<>();
            for(var person:level.players()){
                if(!(person instanceof ServerPlayer player))continue;
                if(animalChecks<MAX_ANIMALS){
                    for(Animal animal:level.getEntitiesOfClass(Animal.class,
                            player.getBoundingBox().inflate(12),Animal::isAlive)){
                        if(animalChecks>=MAX_ANIMALS)break;
                        if(!seenAnimals.add(animal.getUUID()))continue;
                        animalChecks++;
                        String id=BuiltInRegistries.ENTITY_TYPE
                                .getKey(animal.getType()).toString();
                        var value=observe(level,animal.blockPosition(),id,true);
                        if(!visible(value))continue;
                        level.sendParticles(particle(value.profile().art()),
                            animal.getX(),animal.getY()+animal.getBbHeight()+0.1,
                            animal.getZ(),1,0.08,0.09,0.08,0.0);
                    }
                }
                if(plantChecks<MAX_PLANTS){
                    BlockPos center=player.blockPosition();
                    int begin=(int)Math.floorMod((time/INTERVAL)*17L,81L);
                    for(int n=0;n<81&&plantChecks<MAX_PLANTS;n++){
                        int slot=(begin+n)%81;
                        int dx=slot%9-4,dz=slot/9-4;
                        for(int dy=-1;dy<=1&&plantChecks<MAX_PLANTS;dy++){
                            BlockPos pos=center.offset(dx,dy,dz);
                            if(!level.hasChunkAt(pos))continue;
                            if(!seenBlocks.add(pos.asLong()))continue;
                            plantChecks++;
                            String id=BuiltInRegistries.BLOCK.getKey(
                                level.getBlockState(pos).getBlock()).toString();
                            // Fast skip: only potatoes and wheat are in the
                            // five-focus plant profiles.
                            if(!"minecraft:potatoes".equals(id)&&
                                    !"minecraft:wheat".equals(id))continue;
                            var value=observe(level,pos,id,false);
                            if(!visible(value))continue;
                            level.sendParticles(particle(value.profile().art()),
                                pos.getX()+0.5,pos.getY()+0.6,pos.getZ()+0.5,
                                1,0.05,0.1,0.05,0.0);
                        }
                    }
                }
                if(animalChecks>=MAX_ANIMALS&&plantChecks>=MAX_PLANTS)break;
            }
        }
    }
}
