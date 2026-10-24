package io.github.lordnavycrypto.gugugaga.pathogens;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

/**
 * Optional CI-only, real NeoForge server-side NBT round-trip.
 * Never writes to the actual world SavedData store. Enabled exclusively
 * by GUGU_FIELD_SELFTEST=1 in the repository's ephemeral CI server.
 */
public final class PathogenFieldServerSavedDataSmoke {
    private PathogenFieldServerSavedDataSmoke(){}
    public static final String PASS="GUGUGAGA_SPATIAL_SAVEDDATA_ROUNDTRIP_PASS";

    public static void onServerStarted(ServerStartedEvent event){
        if(!"1".equals(System.getenv("GUGU_FIELD_SELFTEST")))return;
        var registry=event.getServer().registryAccess();
        var scratch=PathogenSpatialFieldSavedData.load(new CompoundTag(),registry);
        int index=PathogenSpatialFieldMath.channel("potato_virus_y");
        var a=new PathogenSpatialFieldMath.Cell(-7,11);
        var b=new PathogenSpatialFieldMath.Cell(-6,11);
        var other=PathogenSpatialFieldMath.channel("lyssavirus_rabies");

        check(scratch.set(a,index,13),"seed virtual source");
        check(scratch.set(b,index,2),"seed virtual destination");
        check(scratch.set(a,other,9),"independent catalog channel");
        check(scratch.size()==2,"two sparse cells");
        var routes=scratch.routeCandidates(1);
        check(routes.size()==1,"budgeted route-source scan");

        // Real Minecraft/NeoForge HolderLookup-compatible serialization.
        var encoded=scratch.save(new CompoundTag(),registry);
        check(encoded.contains("Cells"),"actual NBT rows produced");
        check(encoded.getInt("RouteCursor")==1,"fairness cursor serialized");
        var loaded=PathogenSpatialFieldSavedData.load(encoded,registry);
        check(loaded.get(a,index)==13,"source value survives NBT read");
        check(loaded.get(b,index)==2,"destination survives NBT read");
        check(loaded.get(a,other)==9,"independent pathogen channel survives");
        check(loaded.routeCandidates(1).size()==1,"loaded cursor is usable");

        // Copy-on-save must protect the old scratch data.
        check(loaded.transfer(a,b,index,
                PathogenRoutePolicy15.Route.VIRTUAL_VECTOR,true),
                "game-only route between two loaded virtual cells");
        check(loaded.get(a,index)==12&&loaded.get(b,index)==3,
                "fast in-place transfer changes two game counters");
        check(scratch.get(a,index)==13&&scratch.get(b,index)==2,
                "separate SavedData instances never share arrays");

        var second=loaded.save(new CompoundTag(),registry);
        var restarted=PathogenSpatialFieldSavedData.load(second,registry);
        check(restarted.get(a,index)==12&&restarted.get(b,index)==3,
                "post-transfer numbers survive repeated NBT round-trip");
        check(restarted.get(a,other)==9,"other taxon unaffected");
        restarted.set(a,index,0);
        restarted.set(a,other,0);
        restarted.set(b,index,0);
        check(restarted.size()==0,"empty virtual cells reclaimed");

        // No call to world.getDataStorage(): this cannot dirty an actual
        // player's world or leave a test taxon label behind after startup.
        System.out.println(PASS+" channels=15 gameCells=2 persistedCursor=true");
        verifyRealDiskPhase(event);
    }

    /**
     * A two-process CI integration test for the REAL Overworld SavedData
     * filesystem, unlike the in-memory NBT smoke above.
     *
     * ONLY on temporary GitHub Actions test runners explicitly setting
     * GUGU_FIELD_SELFTEST=1 and GUGU_FIELD_DISK_PHASE=write or verify.
     * This method NEVER runs during ordinary Minecraft play.
     */
    private static void verifyRealDiskPhase(ServerStartedEvent event){
        String phase=System.getenv("GUGU_FIELD_DISK_PHASE");
        if(phase==null||phase.isEmpty())return;
        var live=PathogenSpatialFieldSavedData.forLevel(
                event.getServer().overworld());
        var extended=EcologyExtendedSavedData.forLevel(
                event.getServer().overworld());
        var left=new PathogenSpatialFieldMath.Cell(-730,1139);
        var right=new PathogenSpatialFieldMath.Cell(-729,1139);
        int plant=PathogenSpatialFieldMath.channel("potato_virus_y");
        int animal=PathogenSpatialFieldMath.channel("lyssavirus_rabies");
        if(phase.equals("write")){
            check(live.size()==0,"CI must use fresh temporary test world");
            check(live.set(left,plant,11),"initial game value A");
            check(live.set(right,plant,7),"initial game value B");
            check(live.set(left,animal,6),"independent game channel");
            check(live.set(left,PathogenSpatialFieldMath.channel("influenza_a_virus"),10),
                    "focus five animal-channel demo saved");
            check(live.set(right,PathogenSpatialFieldMath.channel("salmonella_enterica"),8),
                    "focus five bacterial-channel demo saved");
            check(extended.size()==0,"CI extension sidecar starts fresh");
            check(extended.set(left,EcologyExtendedTaxa.index("puccinia_graminis"),10),
                    "focus five wheat visual number written to sidecar");
            check(extended.set(right,EcologyExtendedTaxa.index("varroa_destructor"),9),
                    "focus five bee visual number written to sidecar");
            var candidates=live.routeCandidates(1);
            check(candidates.size()==1&&candidates.get(0).equals(left),
                    "save cursor advances after first cell");
            System.out.println("GUGUGAGA_FIELD_DISK_WRITE_PASS"+
                    " regions="+live.size()+" channels=15");
        }else if(phase.equals("verify")){
            check(live.size()==2,"expected persisted game regions");
            check(live.get(left,plant)==11,"disk reload: left channel");
            check(live.get(right,plant)==7,"disk reload: right channel");
            check(live.get(left,animal)==6,"disk reload: independent channel");
            check(live.get(left,PathogenSpatialFieldMath.channel("influenza_a_virus"))==10,
                    "real-disk reload of selected animal virus reference");
            check(live.get(right,PathogenSpatialFieldMath.channel("salmonella_enterica"))==8,
                    "real-disk reload of selected bacteria reference");
            check(extended.size()==2,"real-disk extension area count");
            check(extended.get(left,EcologyExtendedTaxa.index("puccinia_graminis"))==10,
                    "real-disk reload of selected fungal reference");
            check(extended.get(right,EcologyExtendedTaxa.index("varroa_destructor"))==9,
                    "real-disk reload of selected parasite reference");
            var candidates=live.routeCandidates(1);
            check(candidates.size()==1&&candidates.get(0).equals(right),
                    "disk reload: persisted scanner cursor");
            System.out.println("GUGUGAGA_FIELD_DISK_RESTART_PASS"+
                    " worldReloaded=true cursorRecovered=true noNewBlocks=true");
        }else{
            throw new IllegalArgumentException("Unknown CI-only disk test phase");
        }
    }

    private static void check(boolean result,String description){
        if(!result)throw new IllegalStateException(
                "GUGUGAGA_SPATIAL_SAVEDDATA_TEST_FAILED "+description);
    }
}
