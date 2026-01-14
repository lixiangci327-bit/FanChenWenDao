package net.Lcing.fanchenwendao.registry;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.entity.FireballProjectileEntity;
import net.Lcing.fanchenwendao.entity.ThrownSwordEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
            .create(BuiltInRegistries.ENTITY_TYPE, FanChenWenDao.MODID);

    //火球实体注册
    public static final DeferredHolder<EntityType<?>, EntityType<FireballProjectileEntity>> FIREBALL_PROJECTILE =
            ENTITY_TYPES.register("fireball_projectile",
                    () -> EntityType.Builder.<FireballProjectileEntity>of(FireballProjectileEntity::new, MobCategory.MISC)
                            .sized(0.5f,0.5f)
                            .clientTrackingRange(4)
                            .build("fireball_projectile")
            );

    //离手剑实体注册
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownSwordEntity>> THROWN_SWORD =
            ENTITY_TYPES.register("thrown_sword",
                    () -> EntityType.Builder.<ThrownSwordEntity>of(ThrownSwordEntity::new, MobCategory.MISC)
                            .sized(0.5f,0.5f)
                            .clientTrackingRange(4)//视距优化
                            .updateInterval(20)//同步频率
                            .build("thrown_sword")
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
