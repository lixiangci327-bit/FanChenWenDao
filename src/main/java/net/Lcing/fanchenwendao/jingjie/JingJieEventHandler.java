package net.Lcing.fanchenwendao.jingjie;


import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.network.packet.SyncJingJiePayload;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;


//监听游戏事件
@EventBusSubscriber(modid = FanChenWenDao.MODID)
public class JingJieEventHandler {

    //玩家登录同步
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        //只在服务端执行同步逻辑
        if (event.getEntity() instanceof ServerPlayer player) {
            JingJieHelper.syncToClient(player);
        }
    }

    //玩家重生同步
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            JingJieHelper.syncToClient(player);
        }
    }

    //切换维度同步
    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            JingJieHelper.syncToClient(player);
        }
    }

}
