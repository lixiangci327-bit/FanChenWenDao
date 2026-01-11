package net.Lcing.fanchenwendao.entity;

import net.Lcing.fanchenwendao.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;


//法术02：离手剑
public class ThrownSwordEntity extends Projectile implements ItemSupplier {

    //实现接口方法
    @Override
    public ItemStack getItem() {
        return this.getSwordStack();
    }

    //定义同步标识符，传输ItemStack数据，渲染玩家手中的剑
    private static final EntityDataAccessor<ItemStack> SWORD_STACK = SynchedEntityData.defineId(ThrownSwordEntity.class, EntityDataSerializers.ITEM_STACK);

    //默认的构造函数，让系统知道实体的类型和level
    public ThrownSwordEntity(EntityType<? extends Projectile> entityType, Level level){
        super(entityType, level);
    }

    //自定义构造函数，施法时调用，将数据（shooter,itemstack等）都传入
    public ThrownSwordEntity(Level level, LivingEntity shooter, ItemStack sword){
        super(ModEntities.THROWN_SWORD.get(), level);
        this.setOwner(shooter);
        //从发射者眼睛稍低的位置的发射
        this.setPos(shooter.getX(), shooter.getEyePosition().y - 0.1, shooter.getZ());
        this.setSwordStack(sword);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        //注册同步数据，默认给木剑防止空指针
        builder.define(SWORD_STACK, new ItemStack(Items.WOODEN_SWORD));
    }

    //设置同步数据
    public void setSwordStack(ItemStack stack) {
        this.getEntityData().set(SWORD_STACK, stack.copy());
    }
    //获取同步数据
    public ItemStack getSwordStack() {
        return this.getEntityData().get(SWORD_STACK);
    }


    //每帧逻辑更新
    @Override
    public void tick() {
        super.tick();

        //射线检测
        //获取当前速度，计算下一帧位置
        Vec3 movement = this.getDeltaMovement();

        //探测飞行路径上是否碰撞
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult); //Projectile方法，父类内部会进行判断是block还是entity
        }

        //更新坐标
        this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
        //自动调整实体的朝向，使其始终指向飞行方向
        ProjectileUtil.rotateTowardsMovement(this, 0.5F);
        //若长时间无碰撞自动清理
        if (!this.level().isClientSide && this.tickCount > 200) {
            this.discard();
        }
    }


    //实体碰撞逻辑
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if(!this.level().isClientSide) {
            Entity target = result.getEntity();
            Entity owner = this.getOwner();

            //伤害设置
            float damage = 10.0F;
            target.hurt(this.damageSources().thrown(this, owner), damage);

            //撞击后掉落物品并销毁实体
            this.spawnAtLocation(getSwordStack());
            this.discard();
        }
    }

    //方块碰撞逻辑
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            //撞墙直接变为掉落物
            this.spawnAtLocation(getSwordStack());
            this.discard();
        }
    }


    //NBT写入，保证数据持久化，让实体存活期间数据不丢失
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        //将ItemStack保存到NBT
        tag.put("SwordStack", this.getSwordStack().save(this.registryAccess()));
    }

    //读取
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("SwordStack")) {
            ItemStack stack = ItemStack.parse(this.registryAccess(), tag.getCompound("SwordStack"))
                    .orElse(new ItemStack(Items.WOODEN_SWORD));
            this.setSwordStack(stack);
        }
    }
}
