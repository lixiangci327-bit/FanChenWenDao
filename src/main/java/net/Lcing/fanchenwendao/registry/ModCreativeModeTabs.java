package net.Lcing.fanchenwendao.registry;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FanChenWenDao.MODID);


    public static final Supplier<CreativeModeTab> GONG_FA_TAB = CREATIVE_MODE_TABS.register("gongfa_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.YINQIJUE.get()))
                    .title(Component.translatable("creativetab.fanchenwendao.gongfa"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.YINQIJUE.get());


                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
