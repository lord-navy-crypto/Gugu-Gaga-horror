package io.github.lordnavycrypto.gugugaga.pathogens;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Server- and UUID-scoped permission to display local Minecraft game data.
 * No automatic subscription, no player-to-player broadcast and no world saves.
 * Strong values hold ONLY UUIDs; server keys remain weakly reachable.
 */
public final class PathogenHudSubscriptions {
    private final Map<Object,Set<UUID>> enabled = new WeakHashMap<>();

    public boolean isEnabled(Object server,UUID user){
        Objects.requireNonNull(server);Objects.requireNonNull(user);
        var members=enabled.get(server);
        return members!=null && members.contains(user);
    }
    public void set(Object server,UUID user,boolean on){
        Objects.requireNonNull(server);Objects.requireNonNull(user);
        if(on) enabled.computeIfAbsent(server,key->new HashSet<>()).add(user);
        else {
            var members=enabled.get(server);
            if(members!=null){
                members.remove(user);
                if(members.isEmpty())enabled.remove(server);
            }
        }
    }
    public void clearServer(Object server){enabled.remove(Objects.requireNonNull(server));}
    public int count(Object server){
        var members=enabled.get(Objects.requireNonNull(server));
        return members==null?0:members.size();
    }
}
