package io.github.lordnavycrypto.gugugaga;

import io.github.lordnavycrypto.gugugaga.entity.GugugagaEntity;
import io.github.lordnavycrypto.gugugaga.entity.RedactorEntity;
import io.github.lordnavycrypto.gugugaga.entity.YellowSmilerEntity;
import io.github.lordnavycrypto.gugugaga.command.OllamaCommands;
import io.github.lordnavycrypto.gugugaga.command.NightwatchCommands;
import io.github.lordnavycrypto.gugugaga.command.GuguTalkCommands;
import io.github.lordnavycrypto.gugugaga.command.GuguStoryCommands;
import io.github.lordnavycrypto.gugugaga.command.GuguAdvancedCommands;
import io.github.lordnavycrypto.gugugaga.command.GuguPlayCommands;
import io.github.lordnavycrypto.gugugaga.ai.HauntGameplayRuntime;
import io.github.lordnavycrypto.gugugaga.ai.NightwatchOnboarding;
import io.github.lordnavycrypto.gugugaga.network.NightwatchActionPacket;
import io.github.lordnavycrypto.gugugaga.ai.NightwatchDirector;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import io.github.lordnavycrypto.gugugaga.microbes.MicrobeWorldContent;
import io.github.lordnavycrypto.gugugaga.microbes.PathogenWorldContent;
import io.github.lordnavycrypto.gugugaga.microbes.PathogenGameplayRuntime;
import io.github.lordnavycrypto.gugugaga.microbes.LivingMicrobeContent;
import io.github.lordnavycrypto.gugugaga.microbes.LivingMicrobeRuntime;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(GuguGagaMod.MOD_ID)
public final class GuguGagaMod {
    public static final String MOD_ID = "gugugaga";

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<GugugagaEntity>> OBSERVER =
            ENTITIES.register("observer", () -> EntityType.Builder
                    .of(GugugagaEntity::new, MobCategory.CREATURE)
                    .sized(0.95f, 1.50f).clientTrackingRange(8)
                    .updateInterval(3).build("gugugaga:observer"));

    public static final DeferredHolder<EntityType<?>,EntityType<RedactorEntity>> REDACTOR =
            ENTITIES.register("redactor", () -> EntityType.Builder
                    .of(RedactorEntity::new,MobCategory.CREATURE)
                    .sized(0.9f,2.5f).clientTrackingRange(8)
                    .updateInterval(3).build("gugugaga:redactor"));

    public static final DeferredHolder<EntityType<?>,EntityType<YellowSmilerEntity>> YELLOW_SMILER =
            ENTITIES.register("yellow_smiler", () -> EntityType.Builder
                    .of(YellowSmilerEntity::new,MobCategory.CREATURE)
                    .sized(0.8f,1.95f).clientTrackingRange(8)
                    .updateInterval(3).build("gugugaga:yellow_smiler"));

    public GuguGagaMod(IEventBus modBus, ModContainer container) {
        ENTITIES.register(modBus);
        MicrobeWorldContent.BLOCKS.register(modBus);
        MicrobeWorldContent.ITEMS.register(modBus);
        PathogenWorldContent.BLOCKS.register(modBus);
        PathogenWorldContent.ITEMS.register(modBus);
        LivingMicrobeContent.ITEMS.register(modBus);
        modBus.addListener(LivingMicrobeContent::onCreativeTab);
        modBus.addListener(PathogenWorldContent::onCreativeTab);
        modBus.addListener(MicrobeWorldContent::onCreativeTab);
        modBus.addListener(GuguGagaMod::onAttributes);
        modBus.addListener(NightwatchActionPacket::register);
        container.registerConfig(ModConfig.Type.SERVER, NightwatchConfig.SPEC);
        NeoForge.EVENT_BUS.addListener(OllamaCommands::register);
        NeoForge.EVENT_BUS.addListener(NightwatchCommands::register);
        NeoForge.EVENT_BUS.addListener(GuguTalkCommands::register);
        NeoForge.EVENT_BUS.addListener(GuguStoryCommands::register);
        NeoForge.EVENT_BUS.addListener(GuguAdvancedCommands::register);
        NeoForge.EVENT_BUS.addListener(GuguPlayCommands::register);
        NeoForge.EVENT_BUS.addListener(NightwatchDirector::onTick);
        NeoForge.EVENT_BUS.addListener(HauntGameplayRuntime::onTick);
        NeoForge.EVENT_BUS.addListener(PathogenGameplayRuntime::onTick);
        NeoForge.EVENT_BUS.addListener(LivingMicrobeRuntime::onTick);
        NeoForge.EVENT_BUS.addListener(NightwatchOnboarding::onLogin);
        // Dedicated-server smoke must observe real mod construction, not just compilation.
        System.out.println("GUGUGAGA_RUNTIME_INIT_OK");
    }

    private static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(OBSERVER.get(), GugugagaEntity.createAttributes().build());
        event.put(REDACTOR.get(),RedactorEntity.createAttributes().build());
        event.put(YELLOW_SMILER.get(),YellowSmilerEntity.createAttributes().build());
    }
}
