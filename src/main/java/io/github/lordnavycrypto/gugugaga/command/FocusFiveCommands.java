package io.github.lordnavycrypto.gugugaga.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import io.github.lordnavycrypto.gugugaga.pathogens.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Five in-game reference examples. Admin DEMO changes invented game indicators
 * in the user's existing world-data field; not an infection or disease event.
 */
public final class FocusFiveCommands {
    private FocusFiveCommands(){}
    public static void register(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("gugugaga")
          .then(Commands.literal("focus")
            .then(Commands.literal("list").executes(ctx->listing(ctx.getSource())))
            .then(Commands.literal("status").executes(ctx->status(ctx.getSource())))
            .then(Commands.literal("guide")
                .then(Commands.argument("taxon",StringArgumentType.word())
                    .suggests((ctx,b)->suggest(b))
                    .executes(ctx->guide(ctx.getSource(),
                        StringArgumentType.getString(ctx,"taxon")))))
            .then(Commands.literal("demo").requires(s->s.hasPermission(2))
                .then(Commands.argument("taxon",StringArgumentType.word())
                    .suggests((ctx,b)->suggest(b))
                    .executes(ctx->change(ctx.getSource(),
                        StringArgumentType.getString(ctx,"taxon"),12))))
            .then(Commands.literal("clear").requires(s->s.hasPermission(2))
                .then(Commands.argument("taxon",StringArgumentType.word())
                    .suggests((ctx,b)->suggest(b))
                    .executes(ctx->change(ctx.getSource(),
                        StringArgumentType.getString(ctx,"taxon"),0))))));
    }
    private static java.util.concurrent.CompletableFuture<
            com.mojang.brigadier.suggestion.Suggestions> suggest(
            com.mojang.brigadier.suggestion.SuggestionsBuilder builder){
        return SharedSuggestionProvider.suggest(
                FocusFiveProfiles.FIVE.stream().map(
                    FocusFiveProfiles.Profile::id).toList(),builder);
    }
    private static int listing(CommandSourceStack source){
        source.sendSuccess(()->Component.literal(
            "Gugu-Gaga Five Focus: game-only signals, NOT infection measurements"),false);
        for(var profile:FocusFiveProfiles.FIVE){
            source.sendSuccess(()->Component.literal(
                profile.id()+" | "+profile.displayName()+" | "+profile.group()
                +" | "+(profile.store()==FocusFiveProfiles.Store.ORIGINAL_15
                         ?"original-15":"extended-8")),false);
        }
        return 5;
    }
    private static FocusFiveProfiles.Profile profile(
            CommandSourceStack src,String id){
        try{return FocusFiveProfiles.byId(id);}
        catch(IllegalArgumentException exception){
            src.sendFailure(Component.literal("Not among the five focus labels: "+id));
            return null;
        }
    }
    private static int guide(CommandSourceStack src,String id){
        var entry=profile(src,id);
        if(entry==null)return 0;
        src.sendSuccess(()->Component.literal(entry.displayName()+
             " | "+entry.context()),false);
        src.sendSuccess(()->Component.literal("Minecraft animal proxies: "+
                entry.entityIds()+"; crop proxies: "+entry.blockIds()),false);
        src.sendSuccess(()->Component.literal("Public source: "+entry.sourceUrl()),false);
        return 1;
    }
    private static int status(CommandSourceStack src){
        if(!(src.getEntity() instanceof ServerPlayer player)){
            src.sendFailure(Component.literal("Run /gugugaga focus status as a player."));
            return 0;
        }
        var cell=PathogenSpatialFieldRuntime.cell(player.blockPosition());
        var core=PathogenSpatialFieldSavedData.forLevel(player.serverLevel());
        var extra=EcologyExtendedSavedData.forLevel(player.serverLevel());
        src.sendSuccess(()->Component.literal("Five-focus game indicators at cell "+
                cell.x()+","+cell.z()+" (one block ~ one metre)"),false);
        for(var p:FocusFiveProfiles.FIVE){
            int value=p.store()==FocusFiveProfiles.Store.ORIGINAL_15
                    ?core.get(cell,p.channel()):extra.get(cell,p.channel());
            var stage=FocusFiveReadout.tier(value);
            src.sendSuccess(()->Component.literal(
                p.displayName()+" = "+value+"/15; game art="+stage),false);
        }
        return 1;
    }
    private static int change(CommandSourceStack src,String id,int score){
        var entry=profile(src,id);
        if(entry==null)return 0;
        if(!(src.getEntity() instanceof ServerPlayer player)){
            src.sendFailure(Component.literal("A player must choose an in-world location."));
            return 0;
        }
        if(entry.store()==FocusFiveProfiles.Store.ORIGINAL_15 &&
                !NightwatchConfig.PATHOGEN_FIELD_ENABLED.get()){
            src.sendFailure(Component.literal("Original game field disabled."));
            return 0;
        }
        if(entry.store()==FocusFiveProfiles.Store.EXTENDED_8 &&
                !NightwatchConfig.ECOLOGY_EXTENSIONS_ENABLED.get()){
            src.sendFailure(Component.literal("Extension game field disabled."));
            return 0;
        }
        var cell=PathogenSpatialFieldRuntime.cell(player.blockPosition());
        boolean success=entry.store()==FocusFiveProfiles.Store.ORIGINAL_15
                ?PathogenSpatialFieldSavedData.forLevel(player.serverLevel())
                    .set(cell,entry.channel(),score)
                :EcologyExtendedSavedData.forLevel(player.serverLevel())
                    .set(cell,entry.channel(),score);
        if(!success){
            src.sendFailure(Component.literal("Bounded numeric world field is full."));
            return 0;
        }
        src.sendSuccess(()->Component.literal(entry.displayName()+
            ": "+(score==0?"game marker cleared":"demonstration game marker recorded")
            +"; no real infection or biological spread."),true);
        return 1;
    }
}
