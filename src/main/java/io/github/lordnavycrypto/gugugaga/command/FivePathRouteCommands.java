package io.github.lordnavycrypto.gugugaga.command;

import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * On-demand NON-MUTATING comparison of five source-backed ecology concepts
 * and five schematic Minecraft field update proposals. No infection/rates.
 */
public final class FivePathRouteCommands {
    private FivePathRouteCommands(){}
    private static final int DISPLAY_LIMIT=12;
    private static final int OBSERVE_RADIUS=48;

    public static void register(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("gugugaga")
            .then(Commands.literal("fivepaths")
                .then(Commands.literal("list")
                    .executes(ctx->list(ctx.getSource())))
                .then(Commands.literal("preview")
                    .executes(ctx->preview(ctx.getSource())))));
    }

    private static int list(CommandSourceStack source){
        source.sendSuccess(()->Component.literal(
            "Five reference routes: qualitative educational categories, "
            +"NOT verified real-world rates"),false);
        for(var p:FivePathGameRoutes.FIVE){
            source.sendSuccess(()->Component.literal(
                p.id()+" | "+p.route()+" | "+p.scientificCategory()),false);
            source.sendSuccess(()->Component.literal(
                "  Evidence: "+p.sourceUrl()),false);
        }
        return 5;
    }

    private static boolean loadedClose(ServerLevel level,ServerPlayer player,
            PathogenSpatialFieldMath.Cell c){
        if(!level.getChunkSource().hasChunk(c.x()>>2,c.z()>>2))return false;
        double dx=player.getX()-(c.x()*4.0+2);
        double dz=player.getZ()-(c.z()*4.0+2);
        return dx*dx+dz*dz<=OBSERVE_RADIUS*OBSERVE_RADIUS;
    }

    private static FivePathGameRoutes.GameTags tags(ServerLevel level,
            ServerPlayer player,PathogenSpatialFieldMath.Cell cell) {
        // Called only for already loaded, nearby game areas in an explicit
        // user command; never at background tick speed and never loads chunks.
        int minX=cell.x()*4,minZ=cell.z()*4;
        int y=player.blockPosition().getY();
        boolean potato=false,wheat=false,water=false;
        for(int dx=0;dx<4;dx++)for(int dz=0;dz<4;dz++){
            for(int dy=-1;dy<=1;dy++){
                BlockPos at=new BlockPos(minX+dx,y+dy,minZ+dz);
                var state=level.getBlockState(at);
                if(state.is(Blocks.POTATOES))potato=true;
                if(state.is(Blocks.WHEAT))wheat=true;
                if(state.is(Blocks.WATER))water=true;
            }
        }
        boolean bird=false,farm=false,bee=false;
        var box=new AABB(minX,y-8,minZ,minX+4,y+9,minZ+4);
        int checked=0;
        for(Animal animal:level.getEntitiesOfClass(
                Animal.class,box,Animal::isAlive)){
            if(checked++>=16)break;
            String id=BuiltInRegistries.ENTITY_TYPE.getKey(
                    animal.getType()).toString();
            if(id.equals("minecraft:chicken")||id.equals("minecraft:parrot"))
                bird=true;
            if(id.equals("minecraft:chicken")||id.equals("minecraft:pig")||
                    id.equals("minecraft:cow"))farm=true;
            if(id.equals("minecraft:bee"))bee=true;
        }
        BlockPos center=new BlockPos(minX+2,y,minZ+2);
        return new FivePathGameRoutes.GameTags(
            bird,potato,farm,water,wheat,bee,
            level.getBrightness(LightLayer.SKY,center)>0,
            level.isRainingAt(center));
    }

    private static int preview(CommandSourceStack src){
        if(!(src.getEntity() instanceof ServerPlayer player)){
            src.sendFailure(Component.literal("Run fivepaths preview in-game."));
            return 0;
        }
        ServerLevel level=player.serverLevel();
        var original=PathogenSpatialFieldSavedData.forLevel(level).snapshot();
        var extension=EcologyExtendedSavedData.forLevel(level).snapshot();
        Map<PathogenSpatialFieldMath.Cell,FivePathGameRoutes.GameTags> cache=
                new HashMap<>();
        long frame=Math.max(0,level.getGameTime()/100L);
        var proposals=FivePathGameTransport.plan(
            original,extension,frame,
            c->loadedClose(level,player,c),
            (profile,c)->cache.computeIfAbsent(c,
                k->tags(level,player,k)).accepts(profile.route()));
        src.sendSuccess(()->Component.literal(
            "Read-only fivepaths preview | old15 regions="+original.size()+
            " | extra8 regions="+extension.size()+
            " | checked="+proposals.scanned()+
            " | theoretical game proposals="+proposals.proposals().size()),false);
        int shown=0;
        for(var item:proposals.proposals()){
            if(shown++>=DISPLAY_LIMIT)break;
            var localTags=cache.get(item.destination());
            String context=localTags==null?"none":
                    localTags.label(item.profile().route());
            src.sendSuccess(()->Component.literal(
                item.profile().id()+" | "+context+" | "+
                item.origin().x()+","+item.origin().z()+" -> "+
                item.destination().x()+","+item.destination().z()+" | "+
                item.oldDestination()+" -> "+item.newDestination()+"/15"),false);
        }
        src.sendSuccess(()->Component.literal(
            "Preview only: the five-route prototype does not write to the "
            +"world. Existing generic game field updates continue separately."),false);
        return 1;
    }
}
