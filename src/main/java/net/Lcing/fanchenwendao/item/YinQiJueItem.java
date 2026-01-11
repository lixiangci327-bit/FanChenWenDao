package net.Lcing.fanchenwendao.item;


import net.Lcing.fanchenwendao.entity.FireballProjectileEntity;
import net.Lcing.fanchenwendao.registry.ModEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class YinQiJueItem extends Item {
    public YinQiJueItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {

        ItemStack stack = player.getItemInHand(usedHand);

        //发射逻辑在服务端运行
        if (!level.isClientSide()) {
            FireballProjectileEntity projectile = new FireballProjectileEntity(
                    ModEntities.FIREBALL_PROJECTILE.get(),
                    level,
                    player
            );

            //延迟发射
            projectile.setDelayedLaunch(10, player, 1.0F);

            //加入世界
            level.addFreshEntity(projectile);

        }

        return InteractionResultHolder.success(stack);
    }
}
