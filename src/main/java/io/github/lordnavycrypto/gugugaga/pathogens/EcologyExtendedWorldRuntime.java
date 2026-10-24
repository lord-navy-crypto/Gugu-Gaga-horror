package io.github.lordnavycrypto.gugugaga.pathogens;

import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Bounded Minecraft ART cues for real taxon identities. No real infection,
 * disease effects, creation of pathogen mobs or additional terrain blocks.
 */
public final class EcologyExtendedWorldRuntime {
    private EcologyExtendedWorldRuntime(){}
    private static final int INTERVAL=100,ANIMAL_BUDGET=24,PLANT_BUDGET=72;

    public static PathogenSpatialFieldMath.Cell cell(BlockPos block){
        return PathogenSpatialFieldMath.fromBlock(block.getX(),block.getZ());
    }
    private static boolean available(ServerLevel level,List<ServerPlayer> watchers,
            PathogenSpatialFieldMath.Cell cell){
        if(!level.getChunkSource().hasChunk(cell.x()>>2,cell.z()>>2))return false;
        double x=cell.x()*4.0+2,z=cell.z()*4.0+2;
        for(ServerPlayer watcher:watchers){
            double dx=watcher.getX()-x,dz=watcher.getZ()-z;
            if(dx*dx+dz*dz<=32*32)return true;
        }
        return false;
    }
    private static boolean visible(EcologyExtendedSavedData stored,
            PathogenSpatialFieldMath.Cell cell,String vanillaId,boolean animal){
        for(int channel=0;channel<EcologyExtendedTaxa.ENTRIES.size();channel++){
            var entry=EcologyExtendedTaxa.ENTRIES.get(channel);
            boolean matches=animal?entry.matchesEntity(vanillaId):entry.matchesBlock(vanillaId);
            if(matches&&stored.get(cell,channel)>=6)return true;
        }
        return false;
    }
    public static void onTick(ServerTickEvent.Post event){
        if(!NightwatchConfig.ECOLOGY_EXTENSIONS_ENABLED.get())return;
        var server=event.getServer();
        long tick=server.overworld().getGameTime();
        if(tick%INTERVAL!=0)return;
        for(ServerLevel level:server.getAllLevels()){
            List<ServerPlayer> watchers=new ArrayList<>();
            for(var player:level.players())
                if(player instanceof ServerPlayer sp)watchers.add(sp);
            if(watchers.isEmpty())continue;
            var stored=EcologyExtendedSavedData.forLevel(level);
            if(stored.size()==0)continue;
            stored.advance(tick/INTERVAL,c->available(level,watchers,c));
            int animals=0,plants=0;
            for(ServerPlayer viewer:watchers){
                if(animals<ANIMAL_BUDGET){
                    for(Animal mob:level.getEntitiesOfClass(Animal.class,
                            viewer.getBoundingBox().inflate(12),Animal::isAlive)){
                        if(animals++>=ANIMAL_BUDGET)break;
                        String id=BuiltInRegistries.ENTITY_TYPE.getKey(
                                mob.getType()).toString();
                        if(visible(stored,cell(mob.blockPosition()),id,true))
                            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                    mob.getX(),mob.getY()+mob.getBbHeight(),
                                    mob.getZ(),1,0.1,0.1,0.1,0);
                    }
                }
                if(plants<PLANT_BUDGET){
                    BlockPos base=viewer.blockPosition();
                    for(int dx=-3;dx<=3&&plants<PLANT_BUDGET;dx++){
                        for(int dz=-3;dz<=3&&plants<PLANT_BUDGET;dz++){
                            for(int dy=-1;dy<=0&&plants<PLANT_BUDGET;dy++){
                                BlockPos pos=base.offset(dx,dy,dz);
                                plants++;
                                if(!level.hasChunkAt(pos))continue;
                                String id=BuiltInRegistries.BLOCK.getKey(
                                        level.getBlockState(pos).getBlock()).toString();
                                if(visible(stored,cell(pos),id,false))
                                    level.sendParticles(ParticleTypes.ENCHANT,
                                            pos.getX()+0.5,pos.getY()+0.5,
                                            pos.getZ()+0.5,1,0.1,0.1,0.1,0);
                            }
                        }
                    }
                }
                if(animals>=ANIMAL_BUDGET&&plants>=PLANT_BUDGET)break;
            }
        }
    }
}
