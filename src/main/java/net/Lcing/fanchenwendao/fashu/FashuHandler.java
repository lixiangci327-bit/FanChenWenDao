package net.Lcing.fanchenwendao.fashu;

import net.Lcing.fanchenwendao.fashu.logic.FaShuLogics;
import net.Lcing.fanchenwendao.fashu.logic.IFaShuLogic;
import net.Lcing.fanchenwendao.network.packet.SyncSelectFaShuPayload;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public class FashuHandler {

    //定义空id常量
    private static final ResourceLocation EMPTY_FASHU = ResourceLocation.parse("fanchenwendao:empty");

    //木棍 的右键交互
    public static void rightclick(Player player, boolean isClientSide) {
        //确保只在服务端处理
        if (isClientSide) return;

        //获取玩家身上的法术数据
        FashuData data = player.getData(ModAttachments.FASHU_DATA);
        ResourceLocation currentId = data.getCurrentFaShuId();

        //切换模式（shift+右键）
        if(player.isShiftKeyDown()) {
            data.cycleNext();
            Optional<FaShuDefine> defineOpt = FaShuManager.getFaShu(data.getCurrentFaShuId());
            //map操作，获取法术名称
            String name = defineOpt.map(FaShuDefine::getName).orElse("未知法术");
            player.sendSystemMessage(Component.literal("§a法术切换为：" + name));

            if (player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                        serverPlayer,
                        new SyncSelectFaShuPayload(serverPlayer.getId(), data.getCurrentFaShuId())
                );
            }



            return;
        }

        //施法（无shift+右键）
        if (currentId.equals(EMPTY_FASHU)) {
            player.sendSystemMessage(Component.literal("§c你还没有学会或选择任何法术"));
            return;
        }

        //查表：从Manager内获取json数据
        Optional<FaShuDefine> defineOpt = FaShuManager.getFaShu(currentId);

        if(defineOpt.isPresent()) {
            FaShuDefine define = defineOpt.get();
            //找到logic_type
            IFaShuLogic logic = FaShuLogics.getLogic(define.getLogicType());    //匹配如projectile
            //把定义数据传给logic
            logic.cast(player, player.level(), define);
        } else {
            //若ID存在，但是json丢失
            player.sendSystemMessage(Component.literal("§c法术定义丢失，无法施法：" + currentId));
        }

    }

}