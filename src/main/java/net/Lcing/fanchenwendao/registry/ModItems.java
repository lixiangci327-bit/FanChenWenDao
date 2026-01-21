package net.Lcing.fanchenwendao.registry;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.gongfa.item.GongFaBook_01;
import net.Lcing.fanchenwendao.item.YinQiJueItem;
import net.Lcing.fanchenwendao.lingqisystem.item.LingQiDetectorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items  ITEMS = DeferredRegister.createItems(FanChenWenDao.MODID);

    public static final DeferredItem<Item> YINQIJUE = ITEMS.register("yinqijue",
            () -> new YinQiJueItem(new Item.Properties()));

    //灵气探测器
    public static final DeferredItem<LingQiDetectorItem> LINGQI_DETECTOR = ITEMS.register("lingqi_detector",
            LingQiDetectorItem::new
    );

    //功法书1
    public static final DeferredItem<GongFaBook_01> GONGFA_BOOK_01 = ITEMS.register("gongfabook_01",
            () -> new GongFaBook_01(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}