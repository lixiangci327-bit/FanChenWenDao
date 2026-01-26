package net.Lcing.fanchenwendao;

import net.Lcing.fanchenwendao.client.animation.FCAnimations;
import net.Lcing.fanchenwendao.event.CommonEventHandler;
import net.Lcing.fanchenwendao.fashu.FaShuManager;
import net.Lcing.fanchenwendao.fashu.logic.FaShuLogics;
import net.Lcing.fanchenwendao.gongfa.GongFaManager;
import net.Lcing.fanchenwendao.registry.*;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(FanChenWenDao.MODID)
public class FanChenWenDao {

    public static final String MODID = "fanchenwendao";

    public static final Logger LOGGER = LogUtils.getLogger();

    public FanChenWenDao(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onClientSetup);
        // 注册网络包 register payloads
        modEventBus.addListener(ModNetwork::onRegisterPayloads);


        NeoForge.EVENT_BUS.register(this);
        // 注册通用游戏事件 (CommonEventHandler)
        NeoForge.EVENT_BUS.register(CommonEventHandler.class);


        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModDataComponents.register(modEventBus);

        //注册动画事件
        modEventBus.addListener(FCAnimations::registerAnimations);

        // Register our mod's ModConfigSpec so that FML can create and load the config
        // file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        //逻辑注册放入线程安全的队列中
        event.enqueueWork(() -> {
            FaShuLogics.init();
        });

    }

    private void onClientSetup(FMLClientSetupEvent event) {

    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    //资源重载方法
    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new GongFaManager());
        event.addListener(new FaShuManager());
    }
}
