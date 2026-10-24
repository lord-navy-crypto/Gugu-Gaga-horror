package io.github.lordnavycrypto.gugugaga.microbes;

import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import io.github.lordnavycrypto.gugugaga.microbes.LivingMicrobePopulation.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A real, SERVER-SIDE per-mob Minecraft simulation with persisted NBT.
 * Only voluntarily enrolled, LOADED animals near an online player are ticked.
 * Nothing is ever inferred from nearby wild animals. No autonomous infection,
 * real epidemic contact graph, forced chunk loads, or global entity traversal.
 */
public final class LivingMicrobeRuntime {
    private LivingMicrobeRuntime() {}
    private static final String ROOT="GuguGagaLivingMicrobeSimulation";
    private static final int MOB_BUDGET_PER_UPDATE=128;

    public static Host hostOf(LivingEntity target) {
        if(target instanceof Chicken||target instanceof Parrot)return Host.BIRD;
        if(target instanceof Cow||target instanceof Pig||target instanceof Sheep
                ||target instanceof Wolf||target instanceof Fox
                ||target instanceof Cat||target instanceof Horse)return Host.MAMMAL;
        return null;
    }

    public static Colony read(LivingEntity mob) {
        CompoundTag data=mob.getPersistentData();
        if(!data.contains(ROOT))return null;
        CompoundTag info=data.getCompound(ROOT);
        try {
            return new Colony(info.getInt("Bacteria"),info.getInt("Phage"),
                    info.getInt("Virus"),Mode.valueOf(info.getString("Mode")),
                    info.getInt("Steps"));
        }catch(IllegalArgumentException ex) {
            data.remove(ROOT);
            return null;
        }
    }

    private static void save(LivingEntity mob,Colony state) {
        CompoundTag data=mob.getPersistentData();
        if(state==null){data.remove(ROOT);return;}
        CompoundTag record=new CompoundTag();
        record.putInt("Bacteria",state.bacteria());
        record.putInt("Phage",state.phageSignal());
        record.putInt("Virus",state.virusSignal());
        record.putInt("Steps",state.gameSteps());
        record.putString("Mode",state.mode().name());
        data.put(ROOT,record);
    }

    public static void inspect(ServerPlayer player,LivingEntity mob,boolean cycleScenario) {
        if(!NightwatchConfig.LIVING_MICROBE_SIMULATION.get()) {
            player.displayClientMessage(Component.translatable(
                    "message.gugugaga.living_microbes.disabled"),false);
            return;
        }
        Host host=hostOf(mob);
        if(host==null) {
            player.displayClientMessage(Component.translatable(
                    "message.gugugaga.living_microbes.unsupported"),false);
            return;
        }
        Colony current=read(mob);
        if(current==null) {
            // First use starts a virtual ecosystem on THIS particular mob only.
            current=LivingMicrobePopulation.newStudy();
        }else if(cycleScenario) {
            current=LivingMicrobePopulation.cycleMode(current);
        }
        save(mob,current);
        VirusContext virus=LivingMicrobePopulation.documentedScenario(host);
        player.sendSystemMessage(Component.translatable(
                "message.gugugaga.living_microbes.report",
                mob.getName(),current.mode().name(),current.bacteria(),
                current.phageSignal(),current.virusSignal(),virus.name()));
    }

    public static void onTick(ServerTickEvent.Post event) {
        if(!NightwatchConfig.LIVING_MICROBE_SIMULATION.get())return;
        var server=event.getServer();
        if(server.overworld().getGameTime()%80!=0)return;
        Set<UUID> visited=new HashSet<>();
        int updated=0;
        for(ServerPlayer observer:server.getPlayerList().getPlayers()) {
            if(updated>=MOB_BUDGET_PER_UPDATE)break;
            ServerLevel level=observer.serverLevel();
            for(Animal animal:level.getEntitiesOfClass(Animal.class,
                    observer.getBoundingBox().inflate(16.0),Animal::isAlive)) {
                if(updated>=MOB_BUDGET_PER_UPDATE)break;
                if(!visited.add(animal.getUUID()))continue;
                Colony current=read(animal);
                Host host=hostOf(animal);
                if(current==null||host==null)continue;
                boolean damp=level.isRainingAt(animal.blockPosition());
                Colony next=LivingMicrobePopulation.step(current,host,damp);
                save(animal,next);
                updated++;
                // One colored native cue occasionally, attached to a REAL mob.
                if(next.gameSteps()%4==0) {
                    var particle=next.mode()==Mode.BACTERIA?ParticleTypes.HAPPY_VILLAGER
                            :next.mode()==Mode.PHAGE_HOST_DISPLAY?ParticleTypes.ENCHANT
                            :ParticleTypes.END_ROD;
                    level.sendParticles(particle,animal.getX(),animal.getY()+animal.getBbHeight()+0.2,
                            animal.getZ(),1,0.1,0.05,0.1,0.0);
                }
            }
        }
    }
}
