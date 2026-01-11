package net.Lcing.fanchenwendao.fashu;

import net.Lcing.fanchenwendao.network.packet.SyncFashuPayload;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class FashuHandler {

    //木棍 的右键交互
    public static void rightclick(Player player, boolean isClientSide) {
        //确保只在服务端处理
        if (isClientSide) return;

        //获取玩家身上的法术数据
        FashuData data = player.getData(ModAttachments.FASHU_DATA);

        //切换模式（shift+右键）
        if(player.isShiftKeyDown()) {
            data.cycleNext();
            player.sendSystemMessage(Component.literal("§a法术切换为：" + data.getCurrentFashu().getName()));

            //同步法术数据
            if (player instanceof ServerPlayer serverPlayer) {
                //创建包发送玩家ID和法术ID
                var packet = new SyncFashuPayload(serverPlayer.getId(), data.getCurrentFashu().getId());

                //发送给追踪该玩家的所有人以及自己
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, packet);
            }

            return;
        }

        //施法（无shift+右键）
        data.getCurrentFashu().cast(player);

    }

}
