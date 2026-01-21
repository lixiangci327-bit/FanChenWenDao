package net.Lcing.fanchenwendao.registry;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {

    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, FanChenWenDao.MODID);


    //注册功法ID组件
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> GONGFA_ID = REGISTRAR.registerComponentType(
            "gongfa_id",
            builder -> builder
                    .persistent(ResourceLocation.CODEC) //读写硬盘
                    .networkSynchronized(ResourceLocation.STREAM_CODEC) //读写网络
    );

    //暴露给主类的注册方法
    public static void register(IEventBus eventbus) {
        REGISTRAR.register(eventbus);
    }
}
