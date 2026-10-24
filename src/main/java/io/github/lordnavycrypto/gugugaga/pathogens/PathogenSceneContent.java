package io.github.lordnavycrypto.gugugaga.pathogens;
import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** No blocks: just an instrument that binds gameplay states to real hosts. */
public final class PathogenSceneContent {
    private PathogenSceneContent(){}
    public static final DeferredRegister.Items ITEMS=
            DeferredRegister.createItems(GuguGagaMod.MOD_ID);
    public static final DeferredItem<PathogenFieldScannerItem> SCANNER=
            ITEMS.registerItem("pathogen_field_scanner",PathogenFieldScannerItem::new,
                    new Item.Properties().stacksTo(1));
    public static void onCreativeTab(BuildCreativeModeTabContentsEvent event){
        if(event.getTabKey()==CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(SCANNER.get());
    }
}
