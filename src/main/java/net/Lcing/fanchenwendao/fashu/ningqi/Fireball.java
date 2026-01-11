package net.Lcing.fanchenwendao.fashu.ningqi;

import net.Lcing.fanchenwendao.entity.FireballProjectileEntity;
import net.Lcing.fanchenwendao.registry.ModEntities;
import net.Lcing.fanchenwendao.fashu.IFashuCast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Fireball implements IFashuCast {

    @Override
    public void cast(Player player) {
        FireballProjectileEntity projectile = new FireballProjectileEntity(
                ModEntities.FIREBALL_PROJECTILE.get(),
                player.level(),
                player
        );

        //获取眼睛位置
        Vec3 eyePos = player.getEyePosition();
        //获取视线向量
        Vec3 lookvec = player.getLookAngle();
        //计算特效产生坐标
        Vec3 spawnPos = eyePos.add(lookvec.scale(1.0));
        //使用新坐标
        projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

        //发射参数（延迟，速度）
        projectile.setDelayedLaunch(5, player, 1.2F);

        //生成实体
        player.level().addFreshEntity(projectile);
    }
}
