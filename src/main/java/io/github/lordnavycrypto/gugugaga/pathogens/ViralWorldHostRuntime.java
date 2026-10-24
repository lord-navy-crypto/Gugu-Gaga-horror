package io.github.lordnavycrypto.gugugaga.pathogens;

import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import java.util.HashSet;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Read-only vanilla host observer of the numerical Minecraft world field.
 * Does not register, create, convert or infect any entity or block. All
 * visuals are lightweight vanilla particles. No world changes except a
 * short, reversible optional Minecraft animal movement effect.
 */
public final class ViralWorldHostRuntime {
    private ViralWorldHostRuntime(){}
    private static final int INTERVAL=80;
    private static final int MAX_ANIMALS=64;
    private static final int MAX_CROP_BLOCK_SAMPLES=128;
    private static final int CROP_PARTICLES=1;

    private static int[] signals(ServerLevel level,BlockPos pos){
        var field=PathogenSpatialFieldSavedData.forLevel(level);
        var cell=PathogenSpatialFieldRuntime.cell(pos);
        int[] values=new int[15];
        for(int i=0;i<15;i++)values[i]=field.get(cell,i);
        return values;
    }

    private static void observeAnimals(ServerLevel level,ServerPlayer viewer,
                                       HashSet<UUID> seen,int[] budget){
        if(budget[0]>=MAX_ANIMALS)return;
        for(Animal animal:level.getEntitiesOfClass(Animal.class,
                viewer.getBoundingBox().inflate(14),Animal::isAlive)){
            if(budget[0]>=MAX_ANIMALS)break;
            if(!seen.add(animal.getUUID()))continue; // two viewers, one animal
            budget[0]++;
            String vanillaId=BuiltInRegistries.ENTITY_TYPE.getKey(
                    animal.getType()).toString();
            var observation=ViralHostVisualPolicy.forAnimal(
                    vanillaId,signals(level,animal.blockPosition()));
            if(observation.cue()==ViralHostVisualPolicy.Cue.NONE)continue;

            // Only one particle per qualifying animal per simulation update.
            level.sendParticles(ParticleTypes.ENCHANT,animal.getX(),
                    animal.getY()+animal.getBbHeight()+0.1,animal.getZ(),
                    1,0.12,0.1,0.12,0);
            if(observation.cue()==ViralHostVisualPolicy.Cue.ANIMAL_MOVEMENT_CUE
                    &&!animal.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)){
                animal.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,60,0,true,false,false));
            }
        }
    }

    private static void observePlants(ServerLevel level,ServerPlayer viewer,
                                      int[] budget,long frame){
        if(budget[0]>=MAX_CROP_BLOCK_SAMPLES)return;
        BlockPos player=viewer.blockPosition();
        // Rotate around the player so a fixed left/top subset is not always
        // sampled. 13*13 horizontal samples, 3 adjacent vertical levels.
        int first=(int)Math.floorMod(frame*29L,169L);
        for(int n=0;n<169&&budget[0]<MAX_CROP_BLOCK_SAMPLES;n++){
            int cell=(first+n)%169;
            int dx=cell%13-6,dz=cell/13-6;
            for(int dy=-1;dy<=1&&budget[0]<MAX_CROP_BLOCK_SAMPLES;dy++){
                BlockPos pos=player.offset(dx,dy,dz);
                budget[0]++;
                if(!level.hasChunkAt(pos))continue;
                String block=BuiltInRegistries.BLOCK.getKey(
                        level.getBlockState(pos).getBlock()).toString();
                if(!ViralHostVisualPolicy.isViralCrop(block))continue;
                var observation=ViralHostVisualPolicy.forCrop(
                        block,signals(level,pos));
                if(observation.cue()==ViralHostVisualPolicy.Cue.NONE)continue;
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        pos.getX()+0.5,pos.getY()+0.65,pos.getZ()+0.5,
                        CROP_PARTICLES,0.10,0.10,0.10,0);
            }
        }
    }

    public static void onTick(ServerTickEvent.Post event){
        if(!NightwatchConfig.PATHOGEN_FIELD_ENABLED.get()
                ||!NightwatchConfig.VIRAL_HOST_GAME_EFFECTS_ENABLED.get())
            return;
        var server=event.getServer();
        long time=server.overworld().getGameTime();
        if(time%INTERVAL!=0)return;
        HashSet<UUID> seen=new HashSet<>();
        int[] animals={0},plants={0};
        for(ServerLevel level:server.getAllLevels()){
            if(PathogenSpatialFieldSavedData.forLevel(level).size()==0)continue;
            for(ServerPlayer viewer:level.players().stream()
                    .filter(p->p instanceof ServerPlayer)
                    .map(p->(ServerPlayer)p).toList()){
                if(animals[0]<MAX_ANIMALS)
                    observeAnimals(level,viewer,seen,animals);
                if(plants[0]<MAX_CROP_BLOCK_SAMPLES)
                    observePlants(level,viewer,plants,time/INTERVAL);
                if(animals[0]>=MAX_ANIMALS
                        &&plants[0]>=MAX_CROP_BLOCK_SAMPLES)return;
            }
        }
    }
}
