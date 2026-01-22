package net.Lcing.fanchenwendao.registry;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.client.handler.ClientPayloadHandler;
import net.Lcing.fanchenwendao.network.packet.SpawnBurstPayload;
import net.Lcing.fanchenwendao.network.packet.SyncFashuPayload;
import net.Lcing.fanchenwendao.network.packet.SyncGongFaPayload;
import net.Lcing.fanchenwendao.network.packet.SyncJingJiePayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

//注册网络数据包
public class ModNetwork {

    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        // 获取 PayloadRegistrar，设置网络版本号
        // 版本号用于兼容性检查，当客户端/服务端版本不一致时会提示
        final PayloadRegistrar registrar = event.registrar(FanChenWenDao.MODID).versioned("1.0.0");

        //SpwanBurstPayload
        registrar.playToClient(
                SpawnBurstPayload.TYPE,
                SpawnBurstPayload.STREAM_CODEC,
                ((payload, context) -> {
                    if (FMLEnvironment.dist.isClient()) {
                        net.Lcing.fanchenwendao.client.handler.ClientPayloadHandler.handleSpawnBurst(payload, context);
                    }
                })
        );

        //法术同步包
        registrar.playToClient(
                SyncFashuPayload.TYPE,
                SyncFashuPayload.STREAM_CODEC,
                ((payload, context) -> {
                    if (FMLEnvironment.dist.isClient()) {
                        net.Lcing.fanchenwendao.client.handler.ClientPayloadHandler.handleSyncFashu(payload, context);
                    }
                })
        );

        //境界同步包
        registrar.playToClient(
                SyncJingJiePayload.TYPE,
                SyncJingJiePayload.STREAM_CODEC,
                ((payload, context) -> {
                    if (FMLEnvironment.dist.isClient()) {
                        net.Lcing.fanchenwendao.client.handler.ClientPayloadHandler.handleSyncJingJie(payload, context);
                    }
                })
        );

        //功法定义数据同步包
        registrar.playToClient(
                SyncGongFaPayload.TYPE,
                SyncGongFaPayload.STREAM_CODEC,
                ((payload, context) -> {
                    //只在客户端执行
                    if (FMLEnvironment.dist.isClient()) {
                        ClientPayloadHandler.handleSyncGongFa(payload, context);
                    }
                })
        );

        FanChenWenDao.LOGGER.info("网络数据包注册完成 (Network payloads registered)");
    }

}
