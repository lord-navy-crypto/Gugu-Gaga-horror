package io.github.lordnavycrypto.gugugaga.pathogens;

import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import java.util.HashSet;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * 15 authentic names + explicit Minecraft game cases on selected vanilla mobs/crops.
 * NO real-world pathogen dispersal, infection, replication or contagious item systems.
 * All scenario updates are bounded to loaded, observed game objects.
 */
public final class PathogenSceneRuntime {
    private PathogenSceneRuntime(){}
    private static final String ENTITY_ROOT="GuguGagaVanillaScene15";
    private static final int INTERVAL=80,MAX_ANIMALS=96,MAX_PLANTS_PER_LEVEL=64;

    private static String mobId(LivingEntity mob){
        return BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
    }
    private static String blockId(ServerLevel level,BlockPos pos){
        return BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString();
    }
    private static PathogenGameSceneEngine.Scene read(LivingEntity target){
        CompoundTag root=target.getPersistentData();
        if(!root.contains(ENTITY_ROOT))return null;
        CompoundTag row=root.getCompound(ENTITY_ROOT);
        try{
            return new PathogenGameSceneEngine.Scene(row.getString("Taxon"),
                row.getString("Mob"),row.getInt("Frame"),row.getInt("Age"));
        }catch(IllegalArgumentException ignored){
            root.remove(ENTITY_ROOT);
            return null;
        }
    }
    private static void save(LivingEntity target,PathogenGameSceneEngine.Scene scene){
        if(scene==null){target.getPersistentData().remove(ENTITY_ROOT);return;}
        CompoundTag row=new CompoundTag();
        row.putString("Taxon",scene.pathogenId());
        row.putString("Mob",scene.minecraftTargetId());
        row.putInt("Frame",scene.frame());
        row.putInt("Age",scene.gameAge());
        target.getPersistentData().put(ENTITY_ROOT,row);
    }

    public static boolean inspectMob(ServerPlayer player,LivingEntity target,boolean preview){
        if(!NightwatchConfig.PATHOGEN_SCENE_ENGINE_ENABLED.get() ||
                !(target instanceof Animal) || !target.isAlive())return false;
        String id=mobId(target);
        var before=read(target);
        if(preview && before!=null && before.frame()==3){
            save(target,null);
            player.displayClientMessage(Component.translatable(
                "message.gugugaga.pathogen_scenes.cleared"),true);
            return true;
        }
        var next=preview && before!=null && before.minecraftTargetId().equals(id)
                ?PathogenGameSceneEngine.previewAdvance(before)
                :PathogenGameSceneEngine.chooseNext(before,id,true);
        if(next==null)return false;
        save(target,next);
        var world=player.serverLevel();
        if(!preview)PathogenSpatialFieldRuntime.set(world,target.blockPosition(),next.pathogenId(),12);
        world.sendParticles(particle(next),target.getX(),
                target.getY()+target.getBbHeight()+0.1,target.getZ(),
                2,0.1,0.1,0.1,0);
        describe(player,next,target.getName().getString());
        return true;
    }

    public static boolean inspectPlant(ServerPlayer player,BlockPos pos,boolean preview){
        if(!NightwatchConfig.PATHOGEN_SCENE_ENGINE_ENABLED.get())return false;
        ServerLevel level=player.serverLevel();
        if(!level.hasChunkAt(pos))return false;
        String id=blockId(level,pos);
        var store=PathogenCropSceneSavedData.forLevel(level);
        var old=store.get(pos);
        if(preview&&old!=null&&old.frame()==3){
            store.remove(pos);
            player.displayClientMessage(Component.translatable(
                "message.gugugaga.pathogen_scenes.cleared"),true);
            return true;
        }
        boolean same=old!=null&&VanillaHostMapping15.keepsHost(old.pathogenId(),
                old.minecraftTargetId(),id);
        var next=preview&&same
                ?PathogenGameSceneEngine.previewAdvance(
                    new PathogenGameSceneEngine.Scene(old.pathogenId(),id,
                        old.frame(),old.gameAge()))
                :PathogenGameSceneEngine.chooseNext(old,id,false);
        if(next==null)return false;
        if(!store.put(pos,next)){
            // Only on storage saturation, remove invalid cases in LOADED chunks.
            for(var row:store.snapshot()){
                BlockPos other=BlockPos.of(row.getKey());
                if(!level.hasChunkAt(other))continue;
                if(!VanillaHostMapping15.keepsHost(row.getValue().pathogenId(),
                        row.getValue().minecraftTargetId(),blockId(level,other)))
                    store.remove(other);
            }
            if(!store.put(pos,next)){
                player.displayClientMessage(Component.translatable(
                    "message.gugugaga.pathogen_scenes.plant_limit"),true);
                return false;
            }
        }
        if(!preview)PathogenSpatialFieldRuntime.set(level,pos,next.pathogenId(),12);
        level.sendParticles(particle(next),pos.getX()+0.5,pos.getY()+0.6,
                pos.getZ()+0.5,2,0.12,0.1,0.12,0);
        describe(player,next,id);
        return true;
    }

    private static ParticleOptions particle(PathogenGameSceneEngine.Scene scene){
        return switch(PathogenGameCuePolicy.describe(scene).visual()){
            case NONE, LEAF_WILT -> ParticleTypes.CLOUD;
            case MOSAIC -> ParticleTypes.WITCH;
            case PLANT_BLIGHT -> ParticleTypes.ASH;
            case BACTERIAL_TRACE -> ParticleTypes.HAPPY_VILLAGER;
            case ANIMAL_EVENT -> ParticleTypes.ENCHANT;
            case LIVESTOCK_EVENT -> ParticleTypes.END_ROD;
        };
    }
    private static void describe(ServerPlayer player,
            PathogenGameSceneEngine.Scene scene,String host){
        player.displayClientMessage(Component.translatable(
            "message.gugugaga.pathogen_scenes.inspect",host,
            PathogenRoster15.byId(scene.pathogenId()).scientificName(),
            PathogenGameSceneEngine.stage(scene).name()),true);
    }

    /** Optional, reversible crop game effect on manually selected plants. */
    public static void onCropGrow(CropGrowEvent.Pre event){
        if(!(event.getLevel() instanceof ServerLevel level))return;
        String current=blockId(level,event.getPos());
        if(NightwatchConfig.ECOLOGY_CROP_GAME_FEEDBACK_ENABLED.get()
                &&"minecraft:wheat".equals(current)
                &&level.hasChunkAt(event.getPos())){
            int decorative=EcologyExtendedSavedData.forLevel(level).get(
                    PathogenSpatialFieldRuntime.cell(event.getPos()),
                    EcologyExtendedTaxa.index("puccinia_graminis"));
            if(VanillaCropGameFeedback.growth(current,decorative,
                    event.getPos().getX(),event.getPos().getZ(),
                    level.getGameTime())
                    ==VanillaCropGameFeedback.Growth.SHORT_GAME_PAUSE){
                event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
                return;
            }
        }
        if(PathogenSpatialFieldRuntime.pauseCrop(level,event.getPos(),current)){
            event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
            return;
        }
        if(!NightwatchConfig.PATHOGEN_SCENE_ENGINE_ENABLED.get()
                ||!NightwatchConfig.PATHOGEN_SCENE_CROP_PAUSE.get())return;
        var scene=PathogenCropSceneSavedData.forLevel(level).get(event.getPos());
        if(scene==null)return;
        if(VanillaHostMapping15.keepsHost(scene.pathogenId(),
                scene.minecraftTargetId(),current)
                &&PathogenGameSceneEngine.effect(scene)
                ==PathogenGameSceneEngine.Effect.CROP_GROWTH_PAUSED)
            event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
    }

    public static void onServerTick(ServerTickEvent.Post event){
        if(!NightwatchConfig.PATHOGEN_SCENE_ENGINE_ENABLED.get())return;
        var server=event.getServer();
        if(server.overworld().getGameTime()%INTERVAL!=0)return;
        HashSet<UUID> seen=new HashSet<>();
        int processed=0;
        for(ServerPlayer viewer:server.getPlayerList().getPlayers()){
            if(processed>=MAX_ANIMALS)break;
            ServerLevel world=viewer.serverLevel();
            for(Animal target:world.getEntitiesOfClass(Animal.class,
                    viewer.getBoundingBox().inflate(16),Animal::isAlive)){
                if(processed>=MAX_ANIMALS)break;
                if(!seen.add(target.getUUID()))continue;
                var before=read(target);
                if(before==null || !before.minecraftTargetId().equals(mobId(target)))
                    continue;
                var next=PathogenGameSceneEngine.tick(before);
                save(target,next);
                processed++;
                if(NightwatchConfig.PATHOGEN_SCENE_ANIMAL_CUES.get()
                        &&PathogenGameCuePolicy.describe(next).animalMovementCue()
                        &&!target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
                    target.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,80,0,true,false,false));
                if(next.frame()>0 && next.gameAge()%8==0)
                    world.sendParticles(particle(next),target.getX(),
                        target.getY()+target.getBbHeight()+0.1,target.getZ(),
                        1,0.15,0.1,0.15,0);
            }
        }
        for(ServerLevel world:server.getAllLevels()){
            var store=PathogenCropSceneSavedData.forLevel(world);
            int processedPlants=0;
            for(var entry:store.candidates(MAX_PLANTS_PER_LEVEL)){
                if(processedPlants>=MAX_PLANTS_PER_LEVEL)break;
                BlockPos pos=BlockPos.of(entry.getKey());
                if(!world.hasChunkAt(pos))continue;
                if(world.players().stream().noneMatch(p->
                        p.blockPosition().distSqr(pos)<=24*24))continue;
                processedPlants++;
                var previous=entry.getValue();
                String id=blockId(world,pos);
                if(!VanillaHostMapping15.keepsHost(previous.pathogenId(),
                        previous.minecraftTargetId(),id)){
                    store.remove(pos);
                    continue;
                }
                if(!previous.minecraftTargetId().equals(id))
                    previous=new PathogenGameSceneEngine.Scene(previous.pathogenId(),
                            id,previous.frame(),previous.gameAge());
                var next=PathogenGameSceneEngine.tick(previous);
                store.put(pos,next);
                if(next.frame()>0&&next.gameAge()%8==0)
                    world.sendParticles(particle(next),pos.getX()+0.5,
                            pos.getY()+0.6,pos.getZ()+0.5,1,0.1,0.1,0.1,0);
            }
        }
    }
}
