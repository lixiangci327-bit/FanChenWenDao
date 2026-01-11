package net.Lcing.fanchenwendao.client;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.client.renderer.entity.ThrownSwordRenderer;
import net.Lcing.fanchenwendao.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.minecraft.client.renderer.entity.NoopRenderer;


/**
 * 客户端代理类
 * <p>
 * 这个类不会在专用服务端加载。在这里访问客户端侧代码是安全的。
 * 主要负责客户端生命周期的初始化工作。
 */
@Mod(value = FanChenWenDao.MODID, dist = Dist.CLIENT)
public class FanChenWenDaoClient {

    /**
     * 客户端模组构造函数
     *
     * @param container   模组容器，用于注册配置屏幕等
     * @param modEventBus 模组事件总线 (Mod Bus)，用于注册模组生命周期事件
     */
    public FanChenWenDaoClient(ModContainer container, IEventBus modEventBus) {
        // 允许 NeoForge 为此模组创建配置界面
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // 注册模组总线监听器 (Mod Bus)
        modEventBus.addListener(this::onClientSetup);

        //监听渲染器注册事件
        modEventBus.addListener(this::registerRenderers);

    }


    //初始化客户端 Minecraft 客户端已经基本加载完毕，可以安全地初始化渲染器、动画系统等。
    private void onClientSetup(FMLClientSetupEvent event) {

    }

    //渲染器注册
    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        //火球
        event.registerEntityRenderer(ModEntities.FIREBALL_PROJECTILE.get(), NoopRenderer::new);

        //离手剑
        event.registerEntityRenderer(ModEntities.THROWN_SWORD.get(), ThrownSwordRenderer::new);

    }


}
