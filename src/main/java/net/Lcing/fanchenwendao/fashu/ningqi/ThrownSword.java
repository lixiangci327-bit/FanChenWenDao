package net.Lcing.fanchenwendao.fashu.ningqi;

import net.Lcing.fanchenwendao.entity.ThrownSwordEntity;
import net.Lcing.fanchenwendao.fashu.IFashuCast;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class ThrownSword implements IFashuCast {

    @Override
    public void cast(Player player) {
        //获取主手物品
        ItemStack handStack = player.getItemInHand(InteractionHand.MAIN_HAND);

        //检测是不是sword
        if(handStack.getItem() instanceof SwordItem) {

            //服务端生成实体
            if (!player.level().isClientSide()) {
                //创建实体 copy数据给实体
                ThrownSwordEntity swordEntity = new ThrownSwordEntity(player.level(), player, handStack.copy());

                //设置发射参数 （发射者，俯仰角，偏航角，额外偏角，速度，散步）
                swordEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.2F, 0.0F);

                //生成实体
                player.level().addFreshEntity(swordEntity);

                //消耗物品
                handStack.setCount(0);

                //播放音效
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);

            }
        } else {
            if (!player.level().isClientSide()) {
                player.sendSystemMessage(Component.literal("你需要拿着一把剑才能施展此术"));
            }



        }
    }
}
