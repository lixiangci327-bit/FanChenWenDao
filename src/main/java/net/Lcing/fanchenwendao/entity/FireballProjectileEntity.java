package net.Lcing.fanchenwendao.entity;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.Lcing.fanchenwendao.network.packet.SpawnBurstPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class FireballProjectileEntity extends ThrowableItemProjectile {

    //确保只在客户端初始化一次特效
    private boolean isEffectInitialized = false;
    //特效控制器
    private EntityEffectExecutor effectExecutor;
    //延迟发射
    private int launchDelay = 0;
    private Vec3 storedVelocity = Vec3.ZERO;
    //火球寿命
    private static final int lifetime = 60;

    //构造方法
    public FireballProjectileEntity(EntityType<? extends ThrowableItemProjectile> Type, Level level) {
        super(Type, level);
    }

    public FireballProjectileEntity(EntityType<? extends ThrowableItemProjectile> Type, Level level, LivingEntity shooter) {
        super(Type, shooter, level);
    }

    //实现抽象方法
    @Override
    protected Item getDefaultItem() {
        return Items.FIRE_CHARGE;
    }


    //设置延迟发射
    public void setDelayedLaunch(int delayTicks, LivingEntity shooter, float velocity) {
        this.launchDelay = delayTicks;

        //关闭重力
        this.setNoGravity(true);
        //计算飞行向量
        this.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, velocity, 1.0F);
        //把设置好的速度拿出来存储
        this.storedVelocity = this.getDeltaMovement();
        //速度归零
        this.setDeltaMovement(Vec3.ZERO);
    }

    //tick方法，控制特效跟随实体
    @Override
    public void tick() {
        //处理延迟发射逻辑
        if (launchDelay > 0) {
            launchDelay--;

            //再次归零，防止移动
            this.setDeltaMovement(Vec3.ZERO);
            //倒计时结束，开始运动
            if (launchDelay <= 0) {
                //获得速度
                this.setDeltaMovement(this.storedVelocity);
                //播放发射音效
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.GHAST_SHOOT, SoundSource.NEUTRAL, 1.0f, 1.0f
                );

            }
        }
        //调用父类tick处理物理运动
        super.tick();

        //实体寿命检查，服务端负责销毁
        if (!this.level().isClientSide() && this.tickCount > lifetime) {
            this.discard();
        }

        //处理客户端特效
        if (this.level().isClientSide()) {
            //初始化
            if (!isEffectInitialized) {
                //加载特效文件
                FX fx = FXHelper.getFX(ResourceLocation.parse("photon:huoqiu01"));

                if (fx != null) {
                    //实例化控制器
                    this.effectExecutor = new EntityEffectExecutor(fx, this.level(), this, EntityEffectExecutor.AutoRotate.NONE);
                    //启动特效
                    this.effectExecutor.start();
                }

                //完成初始化
                isEffectInitialized = true;

                }
            }
        }


    @Override
    protected boolean canHitEntity(Entity target) {
        //获取发射者
        Entity owner = this.getOwner();

        //防止碰撞发射者
        if(target == owner) {
            return false;
        }

        //同阵营白名单,使用原版isAlliedTo
        if(owner != null && target.isAlliedTo(owner)){
            return false;
        }

        //其他情况
        return super.canHitEntity(target);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        //碰撞后马上清除火球，客户端清理
        if (this.level().isClientSide()) {
            //检查控制器和运行时是否存在
            if (this.effectExecutor != null && this.effectExecutor.getRuntime() != null) {
                //使用destroy清除
                this.effectExecutor.getRuntime().destroy(true);

                //清除后将引用置空
                this.effectExecutor = null;
            }
            return;
        }

        if (!this.level().isClientSide()) {

            //物理爆炸效果
            this.level().explode(
                    this,
                    this.getX(), this.getY(), this.getZ(),
                    2.0F,
                    Level.ExplosionInteraction.BLOCK
            );

            //发包（火球的当前坐标）
            PacketDistributor.sendToPlayersTrackingEntity(
                    this,
                    new SpawnBurstPayload(
                            this.getX(),
                            this.getY(),
                            this.getZ()
                    )
            );

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE,
                    SoundSource.NEUTRAL, 1.0F, 1.0F
            );

            //伤害
            if (result instanceof EntityHitResult entityHit) {
                entityHit.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 5.0F);
            }

            this.discard();
        }
    }
}
