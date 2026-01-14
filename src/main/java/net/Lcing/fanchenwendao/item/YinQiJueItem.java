package net.Lcing.fanchenwendao.item;


import net.Lcing.fanchenwendao.client.animation.FCAnimations;
import net.Lcing.fanchenwendao.entity.FireballProjectileEntity;
import net.Lcing.fanchenwendao.jingjie.JingJieHelper;
import net.Lcing.fanchenwendao.registry.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class YinQiJueItem extends Item {

    //构造函数
    public YinQiJueItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {

        ItemStack stack = player.getItemInHand(usedHand);

        //在服务端操作
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            //获取玩家当前的境界
            int currentlevel = JingJieHelper.getLevel(serverPlayer);

            ServerPlayerPatch patch = EpicFightCapabilities.getEntityPatch(serverPlayer, ServerPlayerPatch.class);

            if (patch != null) {
                //播放打坐动画
                patch.playAnimationSynchronized(FCAnimations.SITDOWN, 0.7f);

                //状态栏提示
                serverPlayer.sendSystemMessage(
                        Component.literal("§e你感受到一股微弱的气流涌入丹田，请保持打坐状态..."),
                        true//true表示文本显示在ActionBar，false则是在聊天框
                );
            }
        }

        return InteractionResultHolder.success(stack); //告诉游戏动作已完成，不要触发原版其他行为

    }
}
