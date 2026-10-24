package io.github.lordnavycrypto.gugugaga.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Science sources + bounded Minecraft-only observation surveys.
 * No sampling, real laboratory procedures, medical diagnosis, treatment,
 * contagion or real pathogen reproduction is simulated.
 */
public final class OneHealthCommands {
    private OneHealthCommands(){}
    public static void register(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("gugugaga")
            .then(Commands.literal("health")
                .then(Commands.literal("evidence")
                    .then(Commands.argument("taxon",StringArgumentType.word())
                        .suggests((context,builder)->SharedSuggestionProvider.suggest(
                            FocusFiveProfiles.FIVE.stream().map(
                                FocusFiveProfiles.Profile::id).toList(),builder))
                        .executes(context->evidence(context.getSource(),
                            StringArgumentType.getString(context,"taxon")))))
                .then(Commands.literal("survey")
                    .then(Commands.argument("taxon",StringArgumentType.word())
                        .suggests((context,builder)->SharedSuggestionProvider.suggest(
                            FocusFiveProfiles.FIVE.stream().map(
                                FocusFiveProfiles.Profile::id).toList(),builder))
                        .executes(context->survey(context.getSource(),
                            StringArgumentType.getString(context,"taxon")))))
                .then(Commands.literal("biology")
                    .executes(context->biology(context.getSource())))
                .then(Commands.literal("environment")
                    .executes(context->environment(context.getSource())))
                .then(Commands.literal("journal")
                    .executes(context->journal(context.getSource())))));
    }
    private static int biology(CommandSourceStack src){
        for(var concept:BiologyConcepts.ENTRIES){
            src.sendSuccess(()->Component.literal(
                "[Science] "+concept.category()+" — "+concept.fact()),false);
            src.sendSuccess(()->Component.literal(
                "Reference: "+concept.sourceUrl()),false);
        }
        return BiologyConcepts.ENTRIES.size();
    }
    private static OneHealthEvidence5.Fact known(CommandSourceStack src,String id){
        try{return OneHealthEvidence5.byId(id);}
        catch(IllegalArgumentException invalid){
            src.sendFailure(Component.literal("No evidence card for selected five-focus taxon."));
            return null;
        }
    }
    private static int evidence(CommandSourceStack src,String id){
        var science=known(src,id);
        if(science==null)return 0;
        src.sendSuccess(()->Component.literal("One Health ["+
                science.domain()+"] "+id),false);
        src.sendSuccess(()->Component.literal("Verified concept: "+
                science.verifiedConcept()),false);
        src.sendSuccess(()->Component.literal("Observation limitation: "+
                science.observationCaution()),false);
        src.sendSuccess(()->Component.literal("Responsible response: "+
                science.responsibleAction()),false);
        src.sendSuccess(()->Component.literal("Public reference: "+
                science.sourceUrl()),false);
        return 1;
    }
    private static int survey(CommandSourceStack src,String id){
        var source=known(src,id);
        if(source==null)return 0;
        if(!(src.getEntity() instanceof ServerPlayer player)){
            src.sendFailure(Component.literal("Run survey from a player in a Minecraft world."));
            return 0;
        }
        var profile=FocusFiveProfiles.byId(id);
        var level=player.serverLevel();
        BlockPos center=player.blockPosition();
        int count=0;
        if(!profile.entityIds().isEmpty()){
            int sampled=0;
            for(Animal animal:level.getEntitiesOfClass(Animal.class,
                    player.getBoundingBox().inflate(10),Animal::isAlive)){
                if(sampled++>=OneHealthObservation.MAX_HOSTS_SCANNED)break;
                String animalId=BuiltInRegistries.ENTITY_TYPE.getKey(
                        animal.getType()).toString();
                if(profile.matches(animalId,true))count++;
            }
        }else{
            int sampled=0;
            for(int dx=-4;dx<=4 && sampled<OneHealthObservation.MAX_CROPS_SCANNED;dx++){
                for(int dz=-4;dz<=4 && sampled<OneHealthObservation.MAX_CROPS_SCANNED;dz++){
                    BlockPos pos=center.offset(dx,0,dz);
                    sampled++;
                    if(!level.hasChunkAt(pos))continue;
                    String blockId=BuiltInRegistries.BLOCK.getKey(
                            level.getBlockState(pos).getBlock()).toString();
                    if(profile.matches(blockId,false))count++;
                }
            }
        }
        var region=PathogenSpatialFieldRuntime.cell(center);
        int gameScore=profile.store()==FocusFiveProfiles.Store.ORIGINAL_15
                ?PathogenSpatialFieldSavedData.forLevel(level).get(region,profile.channel())
                :EcologyExtendedSavedData.forLevel(level).get(region,profile.channel());
        var observation=OneHealthObservation.interpret(id,count,gameScore);
        OneHealthObservationLog.forLevel(level).append(
                new OneHealthObservationLog.Record(id,region.x(),region.z(),
                    Math.max(0,level.getGameTime()),count,gameScore));
        src.sendSuccess(()->Component.literal("Minecraft-only survey: "+id+
                " | vanilla objects="+observation.vanillaObjects()+
                " | invented game score="+observation.virtualGameScore()+
                "/15 | "+observation.state()),false);
        src.sendSuccess(()->Component.literal(observation.message()),false);
        src.sendSuccess(()->Component.literal(
                "This is not an infection test or patient record. "+
                "Use /gugugaga health evidence "+id+" for source context."),false);
        return 1;
    }
    private static int environment(CommandSourceStack src){
        if(!(src.getEntity() instanceof ServerPlayer player)){
            src.sendFailure(Component.literal("Run environment inspection as a player."));
            return 0;
        }
        var sample=MinecraftHabitatRuntime.sample(
                player.serverLevel(),player.blockPosition());
        var result=MinecraftHabitatScience.interpret(sample);
        src.sendSuccess(()->Component.literal(
                "[Minecraft environment] "+result.environmentTag()+
                " | sampled cell "+sample.region().x()+","+
                sample.region().z()+" | "+result.areaBlocks()+
                " world blocks/cell"),false);
        src.sendSuccess(()->Component.literal(result.observationSummary()),false);
        src.sendSuccess(()->Component.literal(
                "These are measurements of vanilla Minecraft game state, "+
                "not real-world climate or infection estimates."),false);
        return 1;
    }

    private static int journal(CommandSourceStack src){
        if(!(src.getEntity() instanceof ServerPlayer player)){
            src.sendFailure(Component.literal("Run journal as an in-game player."));
            return 0;
        }
        var history=OneHealthObservationLog.forLevel(player.serverLevel());
        src.sendSuccess(()->Component.literal("Recent Minecraft object surveys "+
                "(not clinical cases): total retained="+history.size()),false);
        List<OneHealthObservationLog.Record> recent=history.recent(5);
        if(recent.isEmpty()){
            src.sendSuccess(()->Component.literal("No in-game surveys recorded."),false);
            return 1;
        }
        for(var record:recent){
            src.sendSuccess(()->Component.literal(
                record.taxonId()+" @ "+record.cellX()+","+record.cellZ()+
                " | vanilla objects="+record.vanillaObjects()+
                " | virtual score="+record.virtualScore()+
                " | game tick="+record.worldTime()),false);
        }
        return 1;
    }
}
