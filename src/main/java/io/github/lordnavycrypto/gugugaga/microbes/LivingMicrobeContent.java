package io.github.lordnavycrypto.gugugaga.microbes;
import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

/** Items only; live microbial simulation is owned by animal entities. */
public final class LivingMicrobeContent {
    private LivingMicrobeContent(){}
    public static final DeferredRegister.Items ITEMS=
            DeferredRegister.createItems(GuguGagaMod.MOD_ID);
    public static final DeferredItem<LivingMicrobeScannerItem> FIELD_SCANNER=
            ITEMS.registerItem("living_microbe_scanner",
                    LivingMicrobeScannerItem::new,
                    new Item.Properties().stacksTo(1));
    public static void onCreativeTab(BuildCreativeModeTabContentsEvent event){
        if(event.getTabKey()==CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(FIELD_SCANNER.get());
    }
}
