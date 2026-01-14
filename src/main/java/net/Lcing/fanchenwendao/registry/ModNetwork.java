package net.Lcing.fanchenwendao.registry;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.network.packet.SpawnBurstPayload;
import net.Lcing.fanchenwendao.network.packet.SyncFashuPayload;
import net.Lcing.fanchenwendao.network.packet.SyncJingJiePayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

//注册网络数据包
public class ModNetwork {

    /**
     * 注册所有 Payloads
     * 监听 MOD 总线上的 RegisterPayloadHandlersEvent 事件
     */
    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        // 获取 PayloadRegistrar，设置网络版本号
        // 版本号用于兼容性检查，当客户端/服务端版本不一致时会提示
        final PayloadRegistrar registrar = event.registrar(FanChenWenDao.MODID).versioned("1.0.0");

        //SpwanBurstPayload
        registrar.playToClient(
                SpawnBurstPayload.TYPE,
                SpawnBurstPayload.STREAM_CODEC,
                SpawnBurstPayload::handle
        );

        //法术同步包
        registrar.playToClient(
                SyncFashuPayload.TYPE,
                SyncFashuPayload.STREAM_CODEC,
                SyncFashuPayload::handle
        );

        //境界同步包
        registrar.playToClient(
                SyncJingJiePayload.TYPE,
                SyncJingJiePayload.STREAM_CODEC,
                SyncJingJiePayload::handle
        );

        FanChenWenDao.LOGGER.info("网络数据包注册完成 (Network payloads registered)");
    }

}
