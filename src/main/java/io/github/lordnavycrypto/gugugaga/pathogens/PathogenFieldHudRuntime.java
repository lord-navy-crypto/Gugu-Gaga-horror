package io.github.lordnavycrypto.gugugaga.pathogens;

import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import java.util.UUID;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Optional, server-authoritative action-bar HUD. Disabled for every player
 * until that PLAYER explicitly runs '/gugugaga field hud on'.
 *
 * We use the native vanilla action bar, not custom payloads, textures,
 * pathogen blocks or client-only class loading on dedicated servers.
 */
public final class PathogenFieldHudRuntime {
    private PathogenFieldHudRuntime(){}
    private static final int HUD_INTERVAL=40; // Two seconds in nominal game time
    private static final PathogenHudSubscriptions ENABLED=new PathogenHudSubscriptions();

    public static boolean isEnabled(MinecraftServer server,UUID player){
        return ENABLED.isEnabled(server,player);
    }

    public static void setEnabled(ServerPlayer player,boolean enable){
        MinecraftServer server=player.serverLevel().getServer();
        ENABLED.set(server,player.getUUID(),enable);
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event){
        if(!(event.getEntity() instanceof ServerPlayer player))return;
        setEnabled(player,false);
    }

    public static void onServerStopped(ServerStoppedEvent event){
        ENABLED.clearServer(event.getServer());
    }

    public static void onTick(ServerTickEvent.Post event){
        var server=event.getServer();
        if(!NightwatchConfig.PATHOGEN_FIELD_ENABLED.get() ||
                server.overworld().getGameTime()%HUD_INTERVAL!=0L)return;
        for(ServerPlayer player:server.getPlayerList().getPlayers()){
            if(!isEnabled(server,player.getUUID()))continue;
            var world=player.serverLevel();
            var at=PathogenSpatialFieldRuntime.cell(player.blockPosition());
            var saved=PathogenSpatialFieldSavedData.forLevel(world);
            int[] values=new int[PathogenHudTextPolicy.CHANNEL_COUNT];
            for(int i=0;i<values.length;i++)
                values[i]=saved.get(at,i);
            player.displayClientMessage(
                Component.literal(PathogenHudTextPolicy.format(at,values)),true);
        }
    }
}
