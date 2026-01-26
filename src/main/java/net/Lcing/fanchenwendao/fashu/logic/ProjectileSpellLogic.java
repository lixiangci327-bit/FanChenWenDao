package net.Lcing.fanchenwendao.fashu.logic;

import net.Lcing.fanchenwendao.entity.FaShuProjectileEntity;
import net.Lcing.fanchenwendao.fashu.FaShuDefine;
import net.Lcing.fanchenwendao.registry.ModEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ProjectileSpellLogic implements IFaShuLogic {

    @Override
    public void cast(LivingEntity caseter, Level level, FaShuDefine define) {
        //只在服务端生成实体
        if (level.isClientSide) return;

        int count = define.getParamInt("count", 1);  //飞行物数量
        float speed = define.getParamFloat("speed", 1.0f);   //飞行物速度
        String pattern = define.getParam("pattern", "single");  //排列模式，比如single,circle,fan
        float radius = define.getParamFloat("radius", 1.0f);
        int delay = define.getParamInt("launch_delay", 2);   //发射延迟时间，默认0.1s
        //相对偏移参数
        float offsetForward = define.getParamFloat("offset_forward", 0.5f);  //默认身前0.5格
        float offsetUp = define.getParamFloat("offset_up", 0.5f);    //默认离头顶0.5格
        float offsetRight = define.getParamFloat("offset_right", 0.0f);


        //计算基准生成点
        Vec3 lookVec = caseter.getLookAngle();  //获取玩家的视线方向
        Vec3 rightVec = lookVec.cross(new Vec3(0, 1, 0)).normalize();   //计算右向量，视线向量与Y轴叉乘
        Vec3 upVec = new Vec3(0, 1, 0); //这里简单用Y轴作为up。TODO：使用右向量与视线向量叉乘

        //基础位置：眼睛位置 + 前后偏移 + 左右偏移 + 上下偏移
        Vec3 basePos = caseter.getEyePosition()
                .add(lookVec.scale(offsetForward))
                .add(rightVec.scale(offsetRight))
                .add(upVec.scale(offsetUp));

        //循环生成
        for (int i = 0; i < count; i++) {
            FaShuProjectileEntity projectile = new FaShuProjectileEntity(
                    ModEntities.FASHU_PROJECTILE.get(), level, caseter
            );

            //注入法术ID
            projectile.setFashuId(define.getId());

            //计算排列偏移
            Vec3 finalPos = basePos;

            if ("circle".equals(pattern) && count > 1) {
                //画圆逻辑：围绕basepos（基准点）在水平面上展开
                double angle = i * (2 * Math.PI / count);

                //水平面xz平面的圆
                double pX = Math.cos(angle) * radius;
                double pZ = Math.sin(angle) * radius;
                finalPos = finalPos.add(pX, 0, pZ);
            }

            //TODO:可以增加更多pattern

            //设置初始位置
            projectile.setPos(finalPos.x, finalPos.y, finalPos.z);
            //延迟发射
            projectile.setDelayedLaunch(delay, caseter, speed);
            //加入实体
            level.addFreshEntity(projectile);
        }

    }
}