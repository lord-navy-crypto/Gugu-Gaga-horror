import io.github.lordnavycrypto.gugugaga.pathogens.PathogenHudSubscriptions;
import java.util.UUID;
public final class PathogenHudSubscriptionsSmoke {
    private static int checks;
    private static void ok(boolean condition,String message){
        checks++;if(!condition)throw new AssertionError(message);
    }
    public static void main(String[] args){
        var state=new PathogenHudSubscriptions();
        Object serverA=new Object(),serverB=new Object();
        UUID one=UUID.fromString("00000000-0000-4000-8000-000000000011");
        UUID two=UUID.fromString("00000000-0000-4000-8000-000000000022");
        for(Object server:new Object[]{serverA,serverB}){
            ok(!state.isEnabled(server,one),"never enable on join by default");
            ok(!state.isEnabled(server,two),"other player defaults off");
            ok(state.count(server)==0,"no implicit subscription allocation");
        }
        state.set(serverA,one,true);
        ok(state.isEnabled(serverA,one),"player one enabled server A");
        ok(!state.isEnabled(serverA,two),"player two cannot see player one HUD");
        ok(!state.isEnabled(serverB,one),"no permission transfer to server B");
        state.set(serverA,two,true);
        state.set(serverB,one,true);
        ok(state.count(serverA)==2,"both may independently opt in");
        ok(state.count(serverB)==1,"server B separate membership");
        state.set(serverA,one,false);
        ok(!state.isEnabled(serverA,one),"logout or off only revokes one user");
        ok(state.isEnabled(serverA,two),"other user unaffected");
        ok(state.isEnabled(serverB,one),"other server unaffected");
        state.set(serverA,two,false);
        ok(state.count(serverA)==0,"empty membership removed");
        state.clearServer(serverB);
        ok(state.count(serverB)==0,"server teardown clears memory");
        state.set(serverB,one,false);
        ok(!state.isEnabled(serverB,one),"repeated disabling idempotent");
        for(int i=0;i<100;i++){
            Object server=i%2==0?serverA:serverB;
            UUID user=i%3==0?one:two;
            state.set(server,user,true);
            ok(state.isEnabled(server,user),"personal opt-in");
            state.set(server,user,false);
            ok(!state.isEnabled(server,user),"personal opt-out");
        }
        System.out.println("GUGUGAGA_HUD_MULTIPLAYER_ISOLATION_PASS checks="+checks);
    }
}
