package net.Lcing.fanchenwendao.entity;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.Lcing.fanchenwendao.fashu.FaShuDefine;
import net.Lcing.fanchenwendao.fashu.FaShuManager;
import net.Lcing.fanchenwendao.network.packet.SpawnBurstPayload;
import net.Lcing.fanchenwendao.client.fx.ControllableEntityFXExecutor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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

import org.joml.Vector3f;

public class FaShuProjectileEntity extends ThrowableItemProjectile {

    // 法术ID
    private static final EntityDataAccessor<String> FASHU_ID = SynchedEntityData.defineId(FaShuProjectileEntity.class,
            EntityDataSerializers.STRING);
    // 法术定义缓存，避免每帧都去map查找
    private FaShuDefine cachedDefine;
    // 确保只在客户端初始化一次特效
    private boolean isEffectInitialized = false;
    // 特效控制器
    private ControllableEntityFXExecutor effectExecutor;
    // 延迟发射
    private int launchDelay = 0;
    private Vec3 storedVelocity = Vec3.ZERO;
    // 飞行物寿命
    private int lifetime = 100;

    // 构造方法，服务端生成实体时使用
    public FaShuProjectileEntity(EntityType<? extends ThrowableItemProjectile> Type, Level level) {
        super(Type, level);
    }

    // 构造函数：逻辑代码里生成实体使用
    public FaShuProjectileEntity(EntityType<? extends ThrowableItemProjectile> Type, Level level,
            LivingEntity shooter) {
        super(Type, shooter, level);
    }

    // 实现抽象方法
    @Override
    protected Item getDefaultItem() {
        return Items.FIRE_CHARGE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        // 初始化同步数据，默认为空
        builder.define(FASHU_ID, "");
    }

    // 设置法术ID
    public void setFashuId(ResourceLocation id) {
        // System.out.println("DEBUG: setFashuId called with: " + id);
        this.entityData.set(FASHU_ID, id.toString());
        // 提前加载物理属性
        this.cachedDefine = FaShuManager.getFaShu(id).orElse(null);
        if (this.cachedDefine != null) {
            // 读取重力配置
            boolean hasGravity = this.cachedDefine.getParamBool("gravity", true);
            this.setNoGravity(!hasGravity); // 如果json里面有重力，则返回true,取反则是无重力为false
            // 读取寿命
            this.lifetime = this.cachedDefine.getParamInt("lifetime", 60);
        }
    }

    // 获取法术定义，拿到当前的FaShuDefine对象
    private FaShuDefine getDefine() {
        // 懒加载模式，缓存为null时才去查找
        if (this.cachedDefine == null) {
            String idStr = this.entityData.get(FASHU_ID); // 从同步的数据里读取字符串

            // ID为空，说明没同步或法术内没设置，则不操作
            if (!idStr.isEmpty()) {
                // 管理器查表
                this.cachedDefine = FaShuManager.getFaShu(ResourceLocation.parse(idStr)).orElse(null);
            }
        }
        return this.cachedDefine;
    }

    // 设置延迟发射
    public void setDelayedLaunch(int delayTicks, LivingEntity shooter, float velocity) {
        this.launchDelay = delayTicks;

        // 关闭重力
        this.setNoGravity(true);
        // 计算飞行向量
        this.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, velocity, 1.0F);
        // 把设置好的速度拿出来存储
        this.storedVelocity = this.getDeltaMovement();
        // 速度归零
        this.setDeltaMovement(Vec3.ZERO);
    }

    // tick方法，控制特效跟随实体
    @Override
    public void tick() {
        // 处理延迟发射逻辑
        if (launchDelay > 0) {
            launchDelay--;

            // 再次归零，防止移动
            this.setDeltaMovement(Vec3.ZERO);
            // 倒计时结束，开始运动
            if (launchDelay <= 0) {
                // 获得速度
                this.setDeltaMovement(this.storedVelocity);
                FaShuDefine define = getDefine();
                // 回复重力
                if (define != null) {
                    this.setNoGravity(!define.getParamBool("gravity", true));
                }
                // 播放发射音效
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.GHAST_SHOOT, SoundSource.NEUTRAL, 1.0f, 1.0f);

            }
        }
        // 调用父类tick处理物理运动
        super.tick();

        // 实体寿命检查，服务端负责销毁
        if (!this.level().isClientSide() && this.tickCount > lifetime) {
            this.discard();
        }

        // 处理客户端特效
        if (this.level().isClientSide() && !isEffectInitialized) {

            FaShuDefine define = getDefine();

            // 拿到法术定义且有特效配置时
            if (define != null) {
                if (define.getProjectileVisual() != null) {
                    // 读取特效id
                    define.getProjectileVisual().fxID().ifPresent(fxID -> {
                        FX fx = FXHelper.getFX(fxID);
                        if (fx != null) {
                            // 创建特效执行器并绑定到当前实体This
                            this.effectExecutor = new ControllableEntityFXExecutor(fx, this.level(), this,
                                    EntityEffectExecutor.AutoRotate.NONE);
                            this.effectExecutor.start();
                        }
                    });
                }
                // 只有成功加载了Define才锁定初始化状态，防止因同步延迟导致永久失效
                isEffectInitialized = true;
            }
        }
    }





    // 重写remove方法清除客户端特效
    @Override
    public void remove(RemovalReason reason) {
        // 客户端移除实体时强制清除特效
        if (this.level().isClientSide() && this.effectExecutor != null) {
            if (this.effectExecutor.getRuntime() != null) {
                this.effectExecutor.setOffset(new Vector3f(0, -99999, 0));
                this.effectExecutor.stop();
            }
            this.effectExecutor = null;
        }

        super.remove(reason);
    }

    // 碰撞白名单
    @Override
    protected boolean canHitEntity(Entity target) {
        // 获取发射者
        Entity owner = this.getOwner();

        // 防止碰撞发射者
        if (target == owner) {
            return false;
        }

        // 同阵营白名单,使用原版isAlliedTo
        if (owner != null && target.isAlliedTo(owner)) {
            return false;
        }

        // 其他情况
        return super.canHitEntity(target);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);


        FaShuDefine define = getDefine();
        // 如果法术定义为空，直接销毁实体
        if (define == null) {
            this.discard();
            return;
        }

        if (!this.level().isClientSide()) {

            if (define.getParamBool("explode", false)) {
                // 物理爆炸效果
                this.level().explode(
                        this,
                        this.getX(), this.getY(), this.getZ(),
                        2.0F,
                        Level.ExplosionInteraction.BLOCK);
            }

            // 发包（实体的当前坐标）
            PacketDistributor.sendToPlayersTrackingEntity(
                    this,
                    new SpawnBurstPayload(
                            this.getX(),
                            this.getY(),
                            this.getZ()));

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE,
                    SoundSource.NEUTRAL, 1.0F, 1.0F);

            // 伤害
            if (result instanceof EntityHitResult entityHit) {
                float damage = define.getDamage();
                // 伤害来源标记：投掷物 + 发射者
                entityHit.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), damage);
            }

            this.discard();
        }
    }

    // NBT数据保存与读取
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        // FASHU_ID存进存档文件
        compound.putString("FaShuId", this.entityData.get(FASHU_ID));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("FaShuId")) {
            this.entityData.set(FASHU_ID, compound.getString("FaShuId"));
        }
    }
}
