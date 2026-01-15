package net.Lcing.fanchenwendao.item;


import net.Lcing.fanchenwendao.client.animation.FCAnimations;
import net.Lcing.fanchenwendao.jingjie.JingJieHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import yesman.epicfight.gameasset.Animations;
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

            //获取当前修炼状态
            boolean isXiulian = JingJieHelper.isXiulian(serverPlayer);

            if (isXiulian) {
                //正在修炼。那么右键为停止
                JingJieHelper.setXiulian(serverPlayer, false);
                serverPlayer.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);

                ServerPlayerPatch patch = EpicFightCapabilities.getEntityPatch(serverPlayer, ServerPlayerPatch.class);
                if (patch != null) {
                    patch.playAnimationSynchronized(Animations.BIPED_IDLE, 0.4f);
                }

            } else {
                //未修炼。那么右键开始修炼
                JingJieHelper.setXiulian(serverPlayer, true);
                //禁锢玩家
                serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 255, false, false));

                //获取补丁
                ServerPlayerPatch patch = EpicFightCapabilities.getEntityPatch(serverPlayer, ServerPlayerPatch.class);

                if (patch != null) {
                    //播放打坐动画
                    patch.playAnimationSynchronized(FCAnimations.SITDOWN, 0.6f);

                }
            }

        }

        return InteractionResultHolder.success(stack); //告诉游戏动作已完成，不要触发原版其他行为

    }
}
