package net.Lcing.fanchenwendao.fashu.logic;


import net.Lcing.fanchenwendao.client.fx.VisualConfig;
import net.Lcing.fanchenwendao.fashu.FaShuDefine;
import net.Lcing.fanchenwendao.network.packet.SpawnInstantFXPayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class InstantSpellLogic implements IFaShuLogic{

    @Override
    public void cast(LivingEntity caster, Level level, FaShuDefine define) {
        //服务端处理
        if (level.isClientSide()) return;

        //获取施法参数
        float range = define.getParamFloat("range", 5.0f);


        //射线检测
        Vec3 look = caster.getLookAngle();
        Vec3 origin = caster.getEyePosition();
        Vec3 targetPos = origin.add(look.x * range, look.y * range, look.z * range);

        //方块碰撞检测
        BlockHitResult rayTraceResult = level.clip(new ClipContext(
                origin,
                targetPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                caster
        ));

        Vec3 hitPos = rayTraceResult.getLocation(); //经过射线判断的最终位置

        //System.out.println("DEBUG: InstantSpell target=" + targetPos + ", caster=" + caster.position());

        //立刻执行视觉特效
        triggerVisual(level, caster, hitPos, define);

        //延迟逻辑
        int delayTicks = define.getParamInt("delay_ticks", 0);

        if (delayTicks > 0) {
            //如果有延迟，把action打包给调度器
            FaShuDelayScheduler.schedule(delayTicks, () -> {
                //延迟结束后回调
                delayAction(level, caster, hitPos, define);
            });
        } else {
            //无延迟立刻执行
            delayAction(level, caster, hitPos, define);
        }

    }

    //发送视觉数据包
    private void triggerVisual(Level level, LivingEntity caster, Vec3 hitPos, FaShuDefine define) {
        //视觉效果处理
        //hit阶段
        VisualConfig hitVisual = define.getVisual("hit");

        if (hitVisual.fxID().isEmpty()) {
            //System.err.println("DEBUG: 'hit' visual config NOT FOUND or FX ID empty for spell: " + define.getId());
        }


        //若配置了fxID
        hitVisual.fxID().ifPresent(fxId -> {
            //System.out.println("DEBUG: Sending instant FX packet: " + fxId);

            SpawnInstantFXPayload payload = new SpawnInstantFXPayload(
                    hitPos.x, hitPos.y, hitPos.z,
                    fxId
            );

            //发送给周围玩家
            if (caster instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, payload);
            } else {
                //怪物施法
                PacketDistributor.sendToPlayersTrackingEntity(caster, payload);
            }
        });
    }


    //延迟行为逻辑
    private void delayAction(Level level, LivingEntity caster, Vec3 hitPos, FaShuDefine define) {
        //安全检测，若施法者死亡，依然执行逻辑，伤害来源变为系统
        boolean isCasterValid = (caster != null && !caster.isRemoved());

        //环境effect
        applyEnvironmentEffect(level, isCasterValid ? caster : null, hitPos, define);

        float damage = define.getDamage();
        float radius = define.getParamFloat("radius", 3.0f);    //法术效果半径

        //范围伤害(个体效果)
        if (define.getDamage() > 0 || hasEntityEffects(define)) {
            AABB area = new AABB(hitPos, hitPos).inflate(radius);

            level.getEntitiesOfClass(LivingEntity.class, area, target -> {
                //若施法者还在，要做防误伤检测
                if (isCasterValid) {
                    if (target == caster) return false;
                    if (target.isAlliedTo(caster)) return false;    //禁止误伤盟友
                }

                return target.distanceToSqr(hitPos) <= radius*radius;   //圆形范围

            }).forEach(target -> {
                //造成伤害
                if (define.getDamage() > 0) {
                    var damageSource = isCasterValid
                            ? caster.damageSources().magic()
                            : level.damageSources().magic();    //伤害来源修正，防止玩家下线后依然以玩家伤害来源

                    target.hurt(damageSource, damage);
                }

                //额外效果
                applyExtraEffects(target, define);
            });
        }
    }

    //额外效果逻辑
    private void applyExtraEffects(LivingEntity target, FaShuDefine define) {
        //特殊效果
        if (define.getParamBool("fire", false)) {
            target.igniteForSeconds(define.getParamInt("fire_seconds", 3));
        }

        //药水效果
        String effectId = define.getParam("apply_effect", "");
        if (!effectId.isEmpty()) {
            ResourceLocation id = ResourceLocation.tryParse(effectId);
            if (id != null) {
                BuiltInRegistries.MOB_EFFECT.getHolder(id).ifPresent(holder -> {
                    int duration = define.getParamInt("duration", 60);
                    int amplifier = define.getParamInt("amplifier", 0);
                    target.addEffect(new MobEffectInstance(holder, duration, amplifier));
                });
            }
        }

    }

    //环境效果
    private void applyEnvironmentEffect(Level level, LivingEntity caster, Vec3 pos, FaShuDefine define) {
        if (define.getParamBool("explode", false)) {
            float power = define.getParamFloat("explode_power", 5.0f);  //爆炸范围
            boolean showVanilla = define.getParamBool("explode_visual", false); //原版爆炸特效

            Level.ExplosionInteraction interaction = showVanilla
                    ? Level.ExplosionInteraction.BLOCK
                    : Level.ExplosionInteraction.NONE;

            level.explode(
                    caster,
                    pos.x, pos.y, pos.z,
                    power,
                    interaction
            );
        }
    }

    //辅助检测：没有特效且没有伤害，就不搜索范围
    private boolean hasEntityEffects(FaShuDefine define) {
        return define.getParamBool("fire", false)
                || !define.getParam("apply_effect", "").isEmpty();
    }


}
