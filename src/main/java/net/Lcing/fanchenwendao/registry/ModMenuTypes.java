package net.Lcing.fanchenwendao.registry;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.menu.PlayerPanelMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {

    //创建延迟注册器，专门用于注册MenuType
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, FanChenWenDao.MODID);

    //注册playerpanelmenu
    public static final DeferredHolder<MenuType<?>, MenuType<PlayerPanelMenu>> PLAYER_PANEL_MENU =
            MENU_TYPES.register("player_panel_menu",
                    () -> new MenuType<>(PlayerPanelMenu::new, FeatureFlags.VANILLA_SET)
            );

    //主类中调用的注册方法
    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
