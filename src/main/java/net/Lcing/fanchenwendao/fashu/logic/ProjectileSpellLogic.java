package net.Lcing.fanchenwendao.fashu.logic;

import net.Lcing.fanchenwendao.entity.FaShuProjectileEntity;
import net.Lcing.fanchenwendao.fashu.FaShuDefine;
import net.Lcing.fanchenwendao.registry.ModEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


/**
 * 投掷物类法术逻辑执行
 * 计算法术生成的阵型，生成顺序（延迟），初始属性
 */
public class ProjectileSpellLogic implements IFaShuLogic {

    @Override
    public void cast(LivingEntity caster, Level level, FaShuDefine define) {
        //只在服务端生成实体
        if (level.isClientSide) return;

        int count = define.getParamInt("count", 1);  //飞行物数量
        float speed = define.getParamFloat("speed", 1.0f);   //飞行物速度
        String pattern = define.getParam("pattern", "single");  //排列模式，比如single,circle,fan
        float radius = define.getParamFloat("radius", 1.0f);

        //时序参数
        int delay = define.getParamInt("launch_delay", 2);   //发射延迟时间，默认0.1s
        int interval = define.getParamInt("launch_interval", 0);    //生成实体间隔，默认同时生成

        //相对偏移参数
        float offsetForward = define.getParamFloat("offset_forward", 0.5f);  //默认身前0.5格
        float offsetUp = define.getParamFloat("offset_up", 0.5f);    //默认离头顶0.5格
        float offsetRight = define.getParamFloat("offset_right", 0.0f);


        //构建局部坐标系
        Vec3 lookVec = caster.getLookAngle();  //获取玩家的视线方向
        Vec3 worldUp = new Vec3(0, 1, 0);   //世界的向上向量
        Vec3 rightVec = lookVec.cross(worldUp);   //计算右向量，视线向量与Y轴叉乘

        //特殊情况处理：玩家看向天空/地板，叉乘失败
        if (rightVec.lengthSqr() < 1.0E-4D) {
            rightVec = new Vec3(1, 0, 0);   //指向x轴
        }
        rightVec = rightVec.normalize();    //归一化，向量长度变为1，保留方向信息

        Vec3 upVec = rightVec.cross(lookVec).normalize();   //右叉乘视线方向

        //基础位置：眼睛位置 + 前后偏移 + 左右偏移 + 上下偏移
        Vec3 basePos = caster.getEyePosition()
                .add(lookVec.scale(offsetForward))
                .add(rightVec.scale(offsetRight))
                .add(upVec.scale(offsetUp));


        //循环生成
        for (int i = 0; i < count; i++) {
            FaShuProjectileEntity projectile = new FaShuProjectileEntity(
                    ModEntities.FASHU_PROJECTILE.get(), level, caster
            );

            //法术ID存入实体内
            projectile.setFashuId(define.getId());

            //存储实体的偏移位置，用来排列阵型
            Vec3 patternOffset = Vec3.ZERO;


            //阵型算法
            switch (pattern) {
                case "row": //横排阵型
                    if (count > 1) {
                        double totalWidth = radius * 2; //总宽度
                        double step = totalWidth / (count - 1); //步长
                        //从左侧(-radius)开始增加到radius
                        double currentX = -radius + (i * step);
                        patternOffset = rightVec.scale(currentX);   //沿着右向量偏移（从左到右）
                    }
                    break;

                case "ring":    //环形
                    if (count > 1) {
                        //画圆逻辑：围绕basepos（基准点）在水平面上展开
                        double angle = i * (2 * Math.PI / count);

                        //水平面xz平面的圆
                        double offsetX = Math.cos(angle) * radius;
                        double offsetY = Math.sin(angle) * radius;
                        patternOffset = rightVec.scale(offsetX).add(upVec.scale(offsetY));
                    }
                    break;

                case "helix":   //螺旋
                    if (count > 1) {
                        double angle = i * 0.8;
                        double forwardDist = i * (radius / count);

                        Vec3 circlePart = rightVec.scale(Math.cos(angle) * 0.5)
                                .add(upVec.scale(Math.sin(angle) * 0.5));
                        Vec3 forwardPart = lookVec.scale(forwardDist);

                        patternOffset = circlePart.add(forwardPart);
                    }
                    break;

                case "random":  //随机分布
                    patternOffset = new Vec3(
                            (level.random.nextFloat() - 0.5) * radius * 2,
                            (level.random.nextFloat() - 0.5) * radius * 2,
                            (level.random.nextFloat() - 0.5) * radius * 2
                    );
                    break;

                //TODO:可以增加更多pattern
            }



            //计算排列偏移
            Vec3 finalPos = basePos.add(patternOffset);

            //设置初始位置
            projectile.setPos(finalPos.x, finalPos.y, finalPos.z);

            //动画时序，实体逐个生成
            int aniDelay = delay + (i * interval);

            //延迟发射
            projectile.setDelayedLaunch(aniDelay, caster, speed);
            //加入实体
            level.addFreshEntity(projectile);
        }

    }
}