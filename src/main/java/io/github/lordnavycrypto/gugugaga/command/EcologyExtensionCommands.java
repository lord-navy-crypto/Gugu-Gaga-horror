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

/** Taxonomy & invented numeric art field, no additional Minecraft world blocks. */
public final class EcologyExtensionCommands {
    private EcologyExtensionCommands(){}
    public static void register(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("gugugaga")
            .then(Commands.literal("ecology")
                .then(Commands.literal("list").executes(ctx->listing(ctx.getSource())))
                .then(Commands.literal("status").executes(ctx->status(ctx.getSource())))
                .then(Commands.literal("seed").requires(src->src.hasPermission(2))
                    .then(Commands.argument("taxon",StringArgumentType.word())
                        .suggests((ctx,builder)->SharedSuggestionProvider.suggest(
                            EcologyExtendedTaxa.ENTRIES.stream().map(
                                EcologyExtendedTaxa.Entry::id).toList(),builder))
                        .executes(ctx->change(ctx.getSource(),
                            StringArgumentType.getString(ctx,"taxon"),12))))
                .then(Commands.literal("clear").requires(src->src.hasPermission(2))
                    .then(Commands.argument("taxon",StringArgumentType.word())
                        .suggests((ctx,builder)->SharedSuggestionProvider.suggest(
                            EcologyExtendedTaxa.ENTRIES.stream().map(
                                EcologyExtendedTaxa.Entry::id).toList(),builder))
                        .executes(ctx->change(ctx.getSource(),
                            StringArgumentType.getString(ctx,"taxon"),0))))));
    }
    private static int listing(CommandSourceStack source){
        for(var item:EcologyExtendedTaxa.ENTRIES)
            source.sendSuccess(()->Component.literal(item.id()+" - "+
                item.scientificName()+" ("+item.group()+")"+
                (item.artProxy()?" [Minecraft visual proxy]":"")+
                (item.virtualOnly()?" [virtual microbe data only]":"")),false);
        return EcologyExtendedTaxa.ENTRIES.size();
    }
    private static int status(CommandSourceStack source){
        if(!(source.getEntity() instanceof ServerPlayer player)){
            source.sendFailure(Component.literal("Run this as an in-world player."));
            return 0;
        }
        var data=EcologyExtendedSavedData.forLevel(player.serverLevel());
        var at=EcologyExtendedWorldRuntime.cell(player.blockPosition());
        source.sendSuccess(()->Component.literal(
            "Extended ecology at "+at.x()+","+at.z()+
            " (4x4 Minecraft blocks; 1 block ~ 1 m):"),false);
        int count=0;
        for(int i=0;i<EcologyExtendedTaxa.ENTRIES.size();i++){
            int number=data.get(at,i);
            if(number<=0)continue;
            var entry=EcologyExtendedTaxa.ENTRIES.get(i);
            source.sendSuccess(()->Component.literal(
                entry.scientificName()+" | game display value "+number+"/15"),false);
            count++;
        }
        if(count==0)source.sendSuccess(()->Component.literal(
            "No active extension game signals here."),false);
        return 1;
    }
    private static int change(CommandSourceStack source,String id,int number){
        if(!NightwatchConfig.ECOLOGY_EXTENSIONS_ENABLED.get()){
            source.sendFailure(Component.literal("Extended ecology is disabled in config."));
            return 0;
        }
        if(!(source.getEntity() instanceof ServerPlayer player)){
            source.sendFailure(Component.literal("Run this as an in-world player."));
            return 0;
        }
        int channel;
        try{channel=EcologyExtendedTaxa.index(id);}
        catch(IllegalArgumentException invalid){
            source.sendFailure(Component.literal("Unknown real taxon reference: "+id));
            return 0;
        }
        var data=EcologyExtendedSavedData.forLevel(player.serverLevel());
        var cell=EcologyExtendedWorldRuntime.cell(player.blockPosition());
        if(!data.set(cell,channel,number)){
            source.sendFailure(Component.literal("Game data cap reached (256 regions)."));
            return 0;
        }
        source.sendSuccess(()->Component.literal(
            "Virtual ecology signal "+(number==0?"cleared":"recorded")+
            " for "+id+"; not a biological infection."),true);
        return 1;
    }
}
