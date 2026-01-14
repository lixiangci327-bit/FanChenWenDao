package com.p1nero.wukong.epicfight.animation;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.entity.FakeWukongEntity;
import com.p1nero.wukong.epicfight.animation.custom.*;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import com.p1nero.wukong.epicfight.skill.custom.BattleUnit;
import com.p1nero.wukong.epicfight.weapon.WukongColliders;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import com.p1nero.wukong.item.WukongItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongAnimations {

    public static StaticAnimation IDLE;
    public static StaticAnimation WALK;
    public static StaticAnimation RUN_F;
    public static StaticAnimation RUN;
    public static StaticAnimation DASH;
    public static StaticAnimation JUMP;
    public static StaticAnimation FALL;

    public static StaticAnimation JUMP_ATTACK_LIGHT;
    public static StaticAnimation JUMP_ATTACK_LIGHT_HIT;
    public static StaticAnimation JUMP_ATTACK_HEAVY;
    public static StaticAnimation DODGE_F1;
    public static StaticAnimation DODGE_F2;
    public static StaticAnimation DODGE_F3;
    public static StaticAnimation DODGE_FP;
    public static StaticAnimation DODGE_B1;
    public static StaticAnimation DODGE_B2;
    public static StaticAnimation DODGE_B3;
    public static StaticAnimation DODGE_BP;
    public static StaticAnimation DODGE_L1;
    public static StaticAnimation DODGE_L2;
    public static StaticAnimation DODGE_L3;
    public static StaticAnimation DODGE_LP;
    public static StaticAnimation DODGE_R1;
    public static StaticAnimation DODGE_R2;
    public static StaticAnimation DODGE_R3;
    public static StaticAnimation DODGE_RP;
    //棍花
    public static StaticAnimation STAFF_SPIN_ONE_HAND_LOOP;
    public static StaticAnimation STAFF_SPIN_TWO_HAND_LOOP;

    //轻击 1~5
    public static StaticAnimation STAFF_AUTO1_DASH;
    public static StaticAnimation STAFF_AUTO1;
    public static StaticAnimation STAFF_AUTO2;
    public static StaticAnimation STAFF_AUTO3;
    public static StaticAnimation STAFF_AUTO4;
    public static StaticAnimation STAFF_AUTO5;

    //劈棍
    //衍生 1 2
    public static StaticAnimation SMASH_DERIVE1;
    public static StaticAnimation SMASH_DERIVE2;
    public static StaticAnimation SMASH_CHARGING_PRE;
    public static StaticAnimation SMASH_CHARGING_LOOP;
    public static StaticAnimation SMASH_CHARGING_LOOP_STAND;
    //不同星级的重击
    public static StaticAnimation SMASH_CHARGED0;
    public static StaticAnimation SMASH_CHARGED1;
    public static StaticAnimation SMASH_CHARGED2;
    public static StaticAnimation SMASH_CHARGED3;
    public static StaticAnimation SMASH_CHARGED4;

    //戳棍
    //衍生 1 2

    public static StaticAnimation THRUST_JUESICK_FENGCHUANHUA;
    public static StaticAnimation THRUST_JUESICK_END;
    public static StaticAnimation THRUST_JUESICK_START;
    public static StaticAnimation THRUST_JUESICK_LOOP;
    public static StaticAnimation THRUST_FOOTAGE;
    public static StaticAnimation THRUST_RETREAT;
    public static StaticAnimation THRUST_XULI_START;
    public static StaticAnimation THRUST_XULI_LOOP;

    //不同星级的重击
    public static StaticAnimation THRUST_CHARGED0;
    public static StaticAnimation THRUST_CHARGED1;
    public static StaticAnimation THRUST_CHARGED2;
    public static StaticAnimation THRUST_CHARGED3;

    //立棍
    //不同星级的重击
    public static StaticAnimation PILLAR_START0;
    public static StaticAnimation PILLAR_START1;
    public static StaticAnimation PILLAR_START2;
    public static StaticAnimation PILLAR_START3;
    public static StaticAnimation PILLAR_START4;
    public static StaticAnimation PILLAR_LOOP0;
    public static StaticAnimation PILLAR_CHARGED_LOOP1;
    public static StaticAnimation PILLAR_CHARGED_LOOP2;
    public static StaticAnimation PILLAR_CHARGED_LOOP3;
    public static StaticAnimation PILLAR_CHARGED_LOOP4;
    public static StaticAnimation PILLAR_CHARGED_LOOP0TOP1;
    public static StaticAnimation PILLAR_CHARGED_LOOP1TOP2;
    public static StaticAnimation PILLAR_CHARGED_LOOP2TOP3;
    public static StaticAnimation PILLAR_HEAVY0;
    public static StaticAnimation PILLAR_HEAVY1;
    public static StaticAnimation PILLAR_HEAVY2;
    public static StaticAnimation PILLAR_HEAVY3;
    public static StaticAnimation PILLAR_HEAVY4;
    public static StaticAnimation PILLAR_UP4;
    public static StaticAnimation PILLAR_UP;

    public static StaticAnimation PILLAR_HEAVY3_SAGE;
    public static StaticAnimation PILLAR_HEAVY_RIVERSEAFLIP;
    public static StaticAnimation PILLAR_HEAVY_FENGYUNZHUAN;
    public static StaticAnimation PILLAR_HEAVY_FENGYUNZHUANEND;
    //法术
    public static StaticAnimation FASHU_MAGICARTS_ASF_START;
    public static StaticAnimation FASHU_MAGICARTS_DSF_START;
    //身法
    public static StaticAnimation SHENFA_MAGICARTS_JQSQ_START;
    public static StaticAnimation SHENFA_MAGICARTS_JQSQ_END;
    public static StaticAnimation SHENFA_MAGICARTS_JQSQ_DISPLACEMENT_END;
    public static StaticAnimation SHENFA_MAGICARTS_TTTB_START;
    public static StaticAnimation SHENFA_MAGICARTS_TTTB_FAIL;

    public static StaticAnimation HAOMAO_MAGICARTS_FS;//分身
    public static StaticAnimation EVISCERATE_FIRST;//分身


    @SubscribeEvent
    public static void registerAnimations(AnimationRegistryEvent event) {
        event.getRegistryMap().put(WukongMoveset.MOD_ID, () -> {
            WukongAnimations.build();
            WukongGreatSageAnimations.build();
        });
    }

    private static void build() {

        HumanoidArmature biped = Armatures.BIPED;
        //专治各种因为移动导致的动画取消
        AnimationEvent.TimePeriodEvent allStopMovement = AnimationEvent.TimePeriodEvent.create(0.00F, Float.MAX_VALUE, ((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof LocalPlayerPatch localPlayerPatch) {
                Input input = localPlayerPatch.getOriginal().input;
                input.forwardImpulse = 0.0F;
                input.leftImpulse = 0.0F;
                input.down = false;
                input.up = false;
                input.left = false;
                input.right = false;
                input.jumping = false;
                input.shiftKeyDown = false;
                LocalPlayer clientPlayer = localPlayerPatch.getOriginal();
                clientPlayer.setSprinting(false);
            }
        }), AnimationEvent.Side.CLIENT);


        HAOMAO_MAGICARTS_FS = new ActionAnimation(0F, "biped/fashu/haomao_fenshen", biped)
                .addEvents(
                        AnimationEvent.TimeStampedEvent.create(0.1F, (livingEntityPatch, staticAnimation, objects) -> {livingEntityPatch.playSound(WuKongSounds.HAOMAO_SWSF.get(), 2, 2); // 播放音效
                                }, AnimationEvent.TimeStampedEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(3.2F, (livingEntityPatch, staticAnimation, objects) -> {
                           // BattleUnit.fenshen(livingEntityPatch);
                            Vec3 startPos = livingEntityPatch.getTarget() == null ? livingEntityPatch.getOriginal().position() : livingEntityPatch.getTarget().position();
                            Vec3 particleOrigin = startPos.subtract(0, 1, 0);
//                            if(livingEntityPatch.getOriginal() instanceof ServerPlayer serverPlayer){
//                                ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
//                                int particleCount = 7;
//                                float radius = 5F;
//                                float angleIncrement = (float) Math.PI * 2 / particleCount;
//                                for (int i = 0; i < particleCount; i++) {
//                                    float angle = i * angleIncrement;
//                                    float xOffset = radius * (float) Math.cos(angle);
//                                    float zOffset = radius * (float) Math.sin(angle);
//                                    Vec3 particlePos = particleOrigin.add(xOffset, 0, zOffset);
//                                    serverLevel.sendParticles(ParticleTypes.POOF, particlePos.x, particlePos.y + 2, particlePos.z, 20, 0, 0, 0, 0.1);
//                                    FakeWukongEntity fakeWukongEntity = new FakeWukongEntity(serverPlayer);
//                                    fakeWukongEntity.setPos(particleOrigin.add(xOffset, 1, zOffset));  // 设置位置
//                                    serverLevel.getLevel().addFreshEntity(fakeWukongEntity);
//                                    WukongMoveset.LOGGER.info(String.valueOf(fakeWukongEntity.getId()));
//
//                                    serverLevel.getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> wkPlayer.addFakeWukongId(fakeWukongEntity.getId()));
//
//
//                                }
//                                serverLevel.getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
//                                    for (Integer id : wkPlayer.getFakeWukongIds()) {
//                                        System.out.println("FakeWukongEntity ID: " + id);
//                                    }

//                                });

//                            }
                            if (livingEntityPatch.getOriginal() instanceof ServerPlayer serverPlayer) {
                                ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
                                int particleCount = 7;
                                float radius = 5F;
                                float angleIncrement = (float) Math.PI * 2 / particleCount;
                                serverPlayer.getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
                                    List<Integer> spawnedIds = new ArrayList<>(); // 用于存放新生成的实体 ID
                                    for (int i = 0; i < particleCount; i++) {
                                        float angle = i * angleIncrement;
                                        float xOffset = radius * (float) Math.cos(angle);
                                        float zOffset = radius * (float) Math.sin(angle);
                                        Vec3 particlePos = serverPlayer.position().add(xOffset, 0, zOffset);
                                        serverLevel.sendParticles(ParticleTypes.POOF, particlePos.x, particlePos.y + 2, particlePos.z, 20, 0, 0, 0, 0.1);
                                        FakeWukongEntity fakeWukongEntity = new FakeWukongEntity(serverPlayer);
                                        fakeWukongEntity.setPos(particlePos.add(0, 1, 0));
                                        serverLevel.addFreshEntity(fakeWukongEntity);
                                        int entityId = fakeWukongEntity.getId();
                                        wkPlayer.addFakeWukongId(entityId);
                                        spawnedIds.add(entityId);
                                    }

                                });
                            }




                        }, AnimationEvent.TimeStampedEvent.Side.SERVER)
                )
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.5F); // 设置播放速度


        FASHU_MAGICARTS_DSF_START = (new SpecialActionAnimation(0F, 0.14F, "biped/fashu/fashu_magicarts_dsf_start", biped))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2.0F))
                .addProperty(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, new AnimationEvent.TimeStampedEvent[]{
                        AnimationEvent.TimeStampedEvent.create(0F, (livingEntityPatch, staticAnimation, objects) -> BattleUnit.ding(livingEntityPatch), AnimationEvent.TimeStampedEvent.Side.SERVER)})
                .addEvents(AnimationEvent.TimeStampedEvent.create(0F, ((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.playSound(WuKongSounds.XULI_DING_SOU.get(), 1, 1);
        }), AnimationEvent.Side.SERVER));


        FASHU_MAGICARTS_ASF_START = new ActionAnimation(0F, "biped/fashu/fashu_magicarts_asf_start", biped).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.5F));
        SHENFA_MAGICARTS_TTTB_START = new AttackAnimation(0F, 0F, 0F, 0F, 0.5F, WukongColliders.JUMP_ATTACK_LIGHT, biped.toolR, "biped/fashu/shenfa_magicarts_tttb_start", biped).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.5F));
        SHENFA_MAGICARTS_TTTB_FAIL = new ActionAnimation(0F, "biped/fashu/shenfa_magicarts_tttb_fail", biped).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.5F));

        SHENFA_MAGICARTS_JQSQ_START = new ActionAnimation(0F, "biped/fashu/magicarts_cfda_start_s1", biped).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2.5F));
        SHENFA_MAGICARTS_JQSQ_END = new AttackAnimation(0.15F, 0.6F, 0.15F, 0.8F, 1.6F, WukongColliders.JUMP_ATTACK_LIGHT, biped.toolR, "biped/fashu/magicarts_cfda_end", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_UPDATE_TIME, TimePairList.create(0.0F, 0.6F))
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_DEST_LOCATION_BEGIN)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_DEST_LOCATION)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_GET, MoveCoordFunctions.WORLD_COORD)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.5F))
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.playSound(WuKongSounds.SHENFA_JXSQ_END.get(), 0, 0);
        }), AnimationEvent.Side.SERVER));
        SHENFA_MAGICARTS_JQSQ_DISPLACEMENT_END = new AttackAnimation(0.15F, 0.5F, 0.15F, 0.8333F, 1.5F, WukongColliders.JUMP_ATTACK_LIGHT, biped.toolR, "biped/fashu/magicarts_cfda_displacement_end", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.5F))
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {livingEntityPatch.playSound(WuKongSounds.SHENFA_JXSQ_END.get(), 0, 0);}), AnimationEvent.Side.SERVER));


        PILLAR_START0 = new ActionAnimation(0.5F, "biped/pillar/pillar_start0", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.7F,1.7f))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        livingEntityPatch.reserveAnimation(PILLAR_LOOP0);
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.IS_CHARGING.get(), true);
                    }
                }), AnimationEvent.Side.SERVER))
                .addEvents(getScaleEvents(
                        ScaleTime.of(0.6F, 1F, 1F, 1F, 0F, 0F, 0F),
                        ScaleTime.of(0.83F, 1F, 1F, 1F, 0F, 1.6F, 0F),
                        ScaleTime.of(1.7f, 1F, 1F, 1F, 0F, 1.6F, 0F)));
        PILLAR_START1 = new ActionAnimation(0F, "biped/pillar/pillar_start1", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.7F,1.7f))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch ) {
                        livingEntityPatch.reserveAnimation(PILLAR_LOOP0);
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.IS_CHARGING.get(), true);
                    }
                }), AnimationEvent.Side.SERVER))
                .addEvents(getScaleEvents(
                        ScaleTime.of(0.6F, 1F, 1F, 1F, 0F, 0F, 0F),
                        ScaleTime.of(0.83F, 1F, 1.4F, 1F, 0F, 1.8F, 0F),
                        ScaleTime.of(1.7f, 1F, 1.4F, 1F, 0F, 1.8F, 0F)));

        PILLAR_START2 = new ActionAnimation(0F, "biped/pillar/pillar_start2", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.7F,1.7f))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch ) {
                        livingEntityPatch.reserveAnimation(PILLAR_LOOP0);
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.IS_CHARGING.get(), true);
                    }
                }), AnimationEvent.Side.SERVER))
                .addEvents(getScaleEvents(
                        ScaleTime.of(0.6F, 1F, 1F, 1F, 0F, 0F, 0F),
                        ScaleTime.of(1.33F, 1F, 1.7F, 1F, 0F, 1.8F, 0F),
                        ScaleTime.of(1.7f, 1F, 1.7F, 1F, 0F, 1.8F, 0F)));

        PILLAR_START3 = new ActionAnimation(0F, "biped/pillar/pillar_start3", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.7F,1.7f))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch ) {
                        livingEntityPatch.reserveAnimation(PILLAR_LOOP0);
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.IS_CHARGING.get(), true);
                    }
                }), AnimationEvent.Side.SERVER))
                .addEvents(getScaleEvents(
                        ScaleTime.of(0.6F, 1F, 1F, 1F, 0F, 0F, 0F),
                        ScaleTime.of(1.33F, 1F, 2.2F, 1F, 0F, 1.9F, 0F),
                        ScaleTime.of(1.7f, 1F, 2.2F, 1F, 0F, 1.9F, 0F)));

        PILLAR_LOOP0 = new ActionAnimation(0F ,"biped/pillar/pillar_loop0", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 4F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(PILLAR_LOOP0);
                }), AnimationEvent.Side.SERVER));


        PILLAR_UP = new ActionAnimation(0F ,"biped/pillar/pillar_up_1", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 1F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) ->1.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(PILLAR_LOOP0);
                }), AnimationEvent.Side.SERVER));
        List<AnimationEvent.TimeStampedEvent> pillar_up = append(AnimationEvent.TimeStampedEvent.create(0.3F, ((livingEntityPatch, anim, obj) ->
                        livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                getScaleEvents(
                        ScaleTime.of(0F, 1F, 1F, 1F, 0F, 1.6F, 0F),
                        ScaleTime.of(0.33F, 1F, 1F, 1F, 0F, 1.8F, 0F),
                        ScaleTime.of(0.83F, 1F, 1.4F, 1F, 0F, 1.8F, 0F),
                        ScaleTime.of(1f, 1F, 1.4F, 1F, 0F, 1.8F, 0F)));
        PILLAR_UP.addEvents(pillar_up.toArray(new AnimationEvent.TimeStampedEvent[0]));



        PILLAR_HEAVY0 = new WukongScaleStaffAttackAnimation(0F, 0.833F, 1F, 3.166F, null, biped.toolR, "biped/pillar/charged_heavy0", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.75F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 0.33f))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2.2F))
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);
                }), AnimationEvent.Side.SERVER));
        PILLAR_HEAVY1 = new WukongScaleStaffAttackAnimation(0F, 1.1F, 1.3F, 3.166F, WukongColliders.PILLAR_HEAVY1, biped.toolR, "biped/pillar/charged_heavy1", biped).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.75F)).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 0.33f)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2.5F)).addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);
        }), AnimationEvent.Side.SERVER)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
        }), AnimationEvent.Side.SERVER)).addEvents(getScaleEvents(ScaleTime.of(0F, 1F, 1.2F, 1F, 0F, 0.25F, 0F), ScaleTime.of(2.9F, 1F, 1.2F, 1F, 0F, 0.25F, 0F), ScaleTime.of(2.933F, 1F, 1F, 1F, 0F, 0F, 0F), ScaleTime.reset(2.933F)));
        PILLAR_HEAVY2 = new WukongScaleStaffAttackAnimation(0F, 1F, 1.5F, 3.166F, WukongColliders.PILLAR_HEAVY2, biped.toolR, "biped/pillar/charged_heavy2", biped).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(5.75F)).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 0.5f)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 3F)).addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);
                }), AnimationEvent.Side.SERVER)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                }), AnimationEvent.Side.SERVER))
                .addEvents(getScaleEvents(ScaleTime.of(0F, 1F, 1.5F, 1F, 0F, 0.45F, 0F),
                        ScaleTime.of(2.9F, 1F, 1.5F, 1F, 0F, 0.45F, 0F),
                        ScaleTime.of(3F, 1F, 1F, 1F, 0F, 0F, 0F), ScaleTime.reset(3F)));
        PILLAR_HEAVY3 = new WukongScaleStaffAttackAnimation(0F, 1F, 1.5F, 3.6F, WukongColliders.PILLAR_HEAVY3, biped.toolR, "biped/pillar/charged_heavy3", biped).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(7.75F)).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 0.66f)).addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);
        }), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
            double originalYVelocity = ((LivingEntity) entitypatch.getOriginal()).getDeltaMovement().y;
            if (elapsedTime >= 0.73f && elapsedTime <= 1.1f) {
                double extraGravity = 1.7F;
                entitypatch.getOriginal().setDeltaMovement(entitypatch.getOriginal().getDeltaMovement().x, originalYVelocity - extraGravity, entitypatch.getOriginal().getDeltaMovement().z);
            } else {
                entitypatch.getOriginal().setDeltaMovement(entitypatch.getOriginal().getDeltaMovement().x, originalYVelocity, entitypatch.getOriginal().getDeltaMovement().z);
            }
            return 3F;
        });
        List<AnimationEvent.TimeStampedEvent> PILLAR_Heavy3 = append(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, anim, obj) ->
                livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                getScaleEvents(ScaleTime.of(0F, 1F, 1.5F, 1F, 0F, 1.5F, 0F), ScaleTime.of(2.9F, 1F, 1.5F, 1F, 0F, 1F, 0F), ScaleTime.of(3F, 1F, 1F, 1F, 0F, 0F, 0F), ScaleTime.reset(3F)));
        PILLAR_Heavy3.add(AnimationEvent.TimeStampedEvent.create(1.2333F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, 0F, -6F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 3D, 0.01F));
        PILLAR_HEAVY3.addEvents(PILLAR_Heavy3.toArray(new AnimationEvent.TimeStampedEvent[0]));


        PILLAR_START4 = new WukongScaleStaffAttackAnimation(0F,  1.666F,1.5F, 3.6F, WukongColliders.PILLAR_HEAVY4, biped.toolR,  "biped/pillar/charged_start4", biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.7F, 1.66F)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2.0F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(PILLAR_CHARGED_LOOP4);
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.IS_CHARGING.get(), true, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER))
                .addEvents(getScaleEvents(ScaleTime.of(0F, 1F, 1F, 1F, 0F, 0F, 0F),
                        ScaleTime.of(0.7F, 1F, 1F, 1F, 0F, 0.45F, 0F),
                        ScaleTime.of(1f, 1, 1.5F, 1F, 0F, 1.5F, 0F),
                        ScaleTime.of(1.3f, 1, 1.8F, 1F, 0F, 1.5F, 0F),
                        ScaleTime.of(1.66F, 1F, 1.8F, 1F, 0F, 1.5F, 0F),
                        ScaleTime.of(2F, 1F, 1.8F, 1F, 0F, 1.5F, 0F)));

        PILLAR_CHARGED_LOOP4 = new ActionAnimation(0F,  1F,  "biped/pillar/charged_loop4", biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 5.766F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(PILLAR_CHARGED_LOOP4);
                }), AnimationEvent.Side.SERVER)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.3F))

                .addEvents(getScaleEvents(ScaleTime.of(0F, 1F, 1.8F, 1F, 0F, 1.5F, 0F), ScaleTime.of(0F, 1F, 1.8F, 1F, 0F, 1.5F, 0F), ScaleTime.of(4.766F, 1F, 1.8F, 1F, 0F, 1.5F, 0F)));
        PILLAR_HEAVY4 = new WukongScaleStaffAttackAnimation(0F, 0.9F, 2F, 3.6F, WukongColliders.PILLAR_HEAVY4, biped.toolR, "biped/pillar/charged_heavy4", biped).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(11f)).addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F)).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, false).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 0.73f)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
            double originalYVelocity = ((LivingEntity) entitypatch.getOriginal()).getDeltaMovement().y;
            if (elapsedTime >= 0.73f && elapsedTime <= 1.1f) {
                double extraGravity = 1.5F;
                entitypatch.getOriginal().setDeltaMovement(entitypatch.getOriginal().getDeltaMovement().x, originalYVelocity - extraGravity, entitypatch.getOriginal().getDeltaMovement().z);
            } else {
                entitypatch.getOriginal().setDeltaMovement(entitypatch.getOriginal().getDeltaMovement().x, originalYVelocity, entitypatch.getOriginal().getDeltaMovement().z);
            }
            return 2.5F;
        }).addProperty(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, new AnimationEvent.TimeStampedEvent[]{});
        List<AnimationEvent.TimeStampedEvent> pillar_heavy4 = append(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER), getScaleEvents(ScaleTime.of(0F, 1F, 1.8F, 1F, 0F, 1.5F, 0F), ScaleTime.of(2.9F, 1F, 1.8F, 1F, 0F, 1.5F, 0F), ScaleTime.of(3F, 1F, 1F, 1F, 0F, 0F, 0F), ScaleTime.reset(3F)));
        // PILLAR_Heavy4.add( AnimationEvent.TimeStampedEvent.create(1.2666F, (livingEntityPatch, staticAnimation, objects) -> BattleUnit.CHARGED_HEAVY4(livingEntityPatch), AnimationEvent.TimeStampedEvent.Side.SERVER));
        pillar_heavy4.add(AnimationEvent.TimeStampedEvent.create(1.2333F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, 0F, -8F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 5D, 0.01F));
        PILLAR_HEAVY4.addEvents(pillar_heavy4.toArray(new AnimationEvent.TimeStampedEvent[0]));



/*

        //立住在摇头晃脑
        PILLAR_CHARGED_LOOP1 = new ActionAnimation(0F, "biped/pillar/charged_loop1", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 4.766F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.3F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS,
                        AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {livingEntityPatch.reserveAnimation(PILLAR_CHARGED_LOOP1);}), AnimationEvent.Side.SERVER))
                .addEvents(getScaleEvents(ScaleTime.of(0F, 1F, 1.2F, 1F, 0F, 0.7F, 0F),
                        ScaleTime.of(4.766F, 1F, 1.2F, 1F, 0F, 0.7F, 0F)))
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.03F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        if (serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(WukongSkillDataKeys.KEY_PRESSING.get())) {
                            livingEntityPatch.playSound(WuKongSounds.XULI_LEVEL_RISE03.get(), 0, 0);
                        }
                    }}), AnimationEvent.Side.SERVER)).
                addEvents(AnimationEvent.TimePeriodEvent.create(0.01F, 4.766F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch playerPatch && WukongWeaponCategories.isWeaponValid(playerPatch)) {
                        playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.DAMAGE_REDUCE.get(), 0.7F);//设置减伤
                         }}), AnimationEvent.Side.SERVER));
        PILLAR_CHARGED_LOOP2 = new ActionAnimation(0F, "biped/pillar/charged_loop2", biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 4.766F)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.3F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.reserveAnimation(PILLAR_CHARGED_LOOP2);
        }), AnimationEvent.Side.SERVER)).addEvents(getScaleEvents(ScaleTime.of(0F, 1F, 1.5F, 1F, 0F, 0.9F, 0F), ScaleTime.of(4.766F, 1F, 1.5F, 1F, 0F, 0.9F, 0F))).addEvents(AnimationEvent.TimeStampedEvent.create(0.03F, ((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                if (serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(WukongSkillDataKeys.KEY_PRESSING.get())) {
                    livingEntityPatch.playSound(WuKongSounds.XULI_LEVEL_RISE03.get(), 0, 0);
                    //     serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.DAMAGE_REDUCE.get(), 18, serverPlayerPatch.getOriginal());
                }
            }
        }), AnimationEvent.Side.SERVER)).addEvents(AnimationEvent.TimePeriodEvent.create(0.01F, 4.766F, ((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch playerPatch && WukongWeaponCategories.isWeaponValid(playerPatch)) {
                playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.DAMAGE_REDUCE.get(), 0.7F);//设置减伤
            }
        }), AnimationEvent.Side.SERVER));
        PILLAR_CHARGED_LOOP3 = new ActionAnimation(0F, "biped/pillar/charged_loop3", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 4.766F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.3F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(PILLAR_CHARGED_LOOP3);
                }), AnimationEvent.Side.SERVER))
                .addEvents(getScaleEvents(ScaleTime.of(0F, 1F, 1.5F, 1F, 0F, 1.5F, 0F),
                        ScaleTime.of(4.766F, 1F, 1.5F, 1F, 0F, 1.5F, 0F)));

        PILLAR_CHARGED_LOOP0TOP1 = new ActionAnimation(0F, "biped/pillar/charged_loop0top1", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 1.33F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.3F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.reserveAnimation(PILLAR_CHARGED_LOOP1);
        }), AnimationEvent.Side.SERVER)).addEvents(getScaleEvents(ScaleTime.of(0.2F, 1F, 1F, 1F, 0F, 0F, 0F), ScaleTime.of(0.5F, 1, 1.15F, 1F, 0F, 0.1F, 0F), ScaleTime.of(1.33F, 1F, 1.2F, 1F, 0F, 0.7F, 0F)));//蹬腿0到1
        PILLAR_CHARGED_LOOP1TOP2 = new ActionAnimation(0F, "biped/pillar/charged_loop1top2", biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 1.33F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.3F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.reserveAnimation(PILLAR_CHARGED_LOOP2);
        }), AnimationEvent.Side.SERVER)).addEvents(getScaleEvents(ScaleTime.of(0F, 1F, 1.2F, 1F, 0F, 0.7F, 0F), ScaleTime.of(0.2F, 1F, 1.2F, 1F, 0F, 0.7F, 0F), ScaleTime.of(0.5F, 1, 1.4F, 1F, 0F, 0.8F, 0F), ScaleTime.of(1.33F, 1F, 1.5F, 1F, 0F, 0.9F, 0F)));//蹬腿1到2
        PILLAR_CHARGED_LOOP2TOP3 = new ActionAnimation(0F, "biped/pillar/charged_loop2top3", biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 1.33F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.3F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.reserveAnimation(PILLAR_CHARGED_LOOP3);
        }), AnimationEvent.Side.SERVER)).addEvents(getScaleEvents(ScaleTime.of(0F, 1F, 1.5F, 1F, 0F, 0.7F, 0F),
                        ScaleTime.of(0.2F, 1F, 1.5F, 1F, 0F, 0.7F, 0F),
                        ScaleTime.of(0.5F, 1, 1.5F, 1F, 0F, 0.8F, 0F),
                        ScaleTime.of(1.33F, 1F, 1.5F, 1F, 0F, 1.5F, 0F)));
        ;//蹬腿2到3




        PILLAR_START1 = new WukongScaleStaffAttackAnimation(0.5F,  1.666F, biped.toolR,"biped/pillar/charged_start1", biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.7F, 1.66F)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.reserveAnimation(PILLAR_CHARGED_LOOP1);
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.IS_CHARGING.get(), true, serverPlayerPatch.getOriginal());
            }
        }), AnimationEvent.Side.SERVER)).addEvents(getScaleEvents(ScaleTime.of(0.7F, 1F, 1F, 1F, 0F, 0F, 0F), ScaleTime.of(1F, 1, 1.15F, 1F, 0F, 0.1F, 0F), ScaleTime.of(1.66F, 1F, 1.2F, 1F, 0F, 0.7F, 0F)));

        PILLAR_START2 = new WukongScaleStaffAttackAnimation(0F,  1.666F, biped.toolR, "biped/pillar/charged_start2", biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.7F, 1.66F)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.reserveAnimation(PILLAR_CHARGED_LOOP2);
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.IS_CHARGING.get(), true, serverPlayerPatch.getOriginal());
            }
        }), AnimationEvent.Side.SERVER)).addEvents(getScaleEvents(ScaleTime.of(0.7F, 1F, 1F, 1F, 0F, 0F, 0F), ScaleTime.of(1F, 1F, 1.2F, 1F, 0F, 0.5F, 0F), ScaleTime.of(1.66F, 1F, 1.5F, 1F, 0F, 0.9F, 0F)));

        PILLAR_START3 = new WukongScaleStaffAttackAnimation(0F,  1.666F, biped.toolR, "biped/pillar/charged_start3", biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.7F, 1.66F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.reserveAnimation(PILLAR_CHARGED_LOOP3);
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.IS_CHARGING.get(), true, serverPlayerPatch.getOriginal());
            }
        }), AnimationEvent.Side.SERVER)).addEvents(getScaleEvents(ScaleTime.of(0.7F, 1F, 1F, 1F, 0F, 0.45F, 0F),
                        ScaleTime.of(1f, 1, 1.3F, 1F, 0F, 0.8F, 0F),
                        ScaleTime.of(1.66F, 1F, 1.5F, 1F, 0F, 1.5F, 0F)));




*/

        PILLAR_HEAVY_RIVERSEAFLIP = new BasicMultipleAttackAnimation(0F, "biped/pillar/stick_heavy_riverseaflip", biped,
                new AttackAnimation.Phase(0.0F, 0.43333F, 0.6666F, 3.03333F, 0.7666F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F)),
                new AttackAnimation.Phase(0.7666F, 2.23333f, 2.43333F, 3.03333F, 4.7F, biped.toolR, WukongColliders.PILLAR_HEAVY_RIVERSEAFLIP)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(4.48f)))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                   if (elapsedTime > 0.4333f && elapsedTime < 0.6f) {
                         return 2.8F;
                   }else if (elapsedTime >2.1f && elapsedTime < 2.4333f) {
                        return 2.8F ;
                    }
                   return 2.3F ;});
        List<AnimationEvent.TimeStampedEvent> riverseaflip = append(AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                getScaleEvents(ScaleTime.of(0F, 1, 1F, 1F, 0F, 0F, 0F),
                        ScaleTime.of(2.23333f, 1, 1F, 1F, 0F, 0F, 0F),
                        ScaleTime.reset(3F)));
        PILLAR_HEAVY_RIVERSEAFLIP.addEvents(riverseaflip.toArray(new AnimationEvent.TimeStampedEvent[0]));

        PILLAR_HEAVY_FENGYUNZHUAN = new WukongBasicMultipleAttackAnimation(0F, "biped/pillar/stick_heavy_fengyunzhuan", biped,
                new AttackAnimation.Phase(0.0F, 0.2F, 0.4F, 1.1F, 0.4F, biped.toolR, WukongColliders.PILLAR_FENGYUNZHUAN)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.12F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4f))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, WuKongSounds.PERFECT_FYZ.get()),
                new AttackAnimation.Phase(0.4F, 0.5f, 0.6333F, 1.1F, 0.6333F, biped.toolR, WukongColliders.PILLAR_FENGYUNZHUAN).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.12F)).addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4f)).addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, WuKongSounds.PERFECT_FYZ.get()), new AttackAnimation.Phase(0.6333F, 0.766f, 0.8666F, 1.1F, 0.8666F, biped.toolR, WukongColliders.PILLAR_FENGYUNZHUAN).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.12F)).addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4f)).addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, WuKongSounds.PERFECT_FYZ.get()), new AttackAnimation.Phase(0.8666F, 0.96666f, 1.1F, 1.1F, 1.1F, biped.toolR, WukongColliders.PILLAR_FENGYUNZHUAN).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.12F)).addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4f)).addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, WuKongSounds.PERFECT_FYZ.get()))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)  // 禁用垂直移动
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)  // 禁用取消移动
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.PROTECT_NEXT_FALL.get(), true, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch.getOriginal() instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) livingEntityPatch.getOriginal();  // 转换类型
                        livingEntity.setPos(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());  // 保持位置
                    }
                    livingEntityPatch.reserveAnimation(PILLAR_HEAVY_FENGYUNZHUAN);
                }), AnimationEvent.Side.SERVER)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F));  // 修改播放速度

        PILLAR_HEAVY_FENGYUNZHUANEND = new BasicAttackAnimation(0F, 0, 0, 1.93333F, null, biped.toolR, "biped/pillar/stick_heavy_fengyunzhuanend", biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 0.5333F)).addEvents(AnimationEvent.TimePeriodEvent.create(0F, 1.93333F, (livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.PILLAR_JIANGHAIFAN_TIMER.get(), Config.DERIVE_CHECK_TIME.get().intValue(), serverPlayerPatch.getOriginal());
                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.PROTECT_NEXT_FALL.get(), false, serverPlayerPatch.getOriginal());
            }
        }, AnimationEvent.Side.SERVER)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2.3F));



        THRUST_XULI_LOOP = new ActionAnimation(0F, "biped/thrust/thrust_xuli_loop", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2.0F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS,
                        AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {livingEntityPatch.reserveAnimation(THRUST_XULI_LOOP);}), AnimationEvent.Side.SERVER));
        THRUST_XULI_START = new ActionAnimation(0F, "biped/thrust/thrust_xuli_start", biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2.5F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.reserveAnimation(THRUST_XULI_LOOP);
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.Thrust_IS_CHARGING.get(), true, serverPlayerPatch.getOriginal());

            }
        }), AnimationEvent.Side.SERVER)).addEvents(AnimationEvent.TimeStampedEvent.create(0.2F, ((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                if (serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(WukongSkillDataKeys.Thrust_KEY_PRESSING.get()))
                    livingEntityPatch.playSound(WuKongSounds.XULI_LEVEL_RISE03.get(), 0, 0);
            }
        }), AnimationEvent.Side.SERVER));
        THRUST_CHARGED0 = new WukongScaleStaffAttackAnimation(0F, 0.5F, 0.733F, 2.766F, WukongColliders.STACK_0_1, biped.toolR, "biped/thrust/thrust_heavy0", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2F))
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN,WukongMoveCoordFunctions.TRACE_LOCROT_TARGETO)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, WukongMoveCoordFunctions.TRACE_LOCROT_TARGETO)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.FIXED_HEAD_ROTATION, true)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {WukongMoveCoordFunctions.reseTSjzt();}), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F))
                .addEvents(allStopMovement).addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);}), AnimationEvent.Side.SERVER));
        THRUST_CHARGED1 = new WukongScaleStaffAttackAnimation(0F, 0.5F, 0.733F, 2.766F, WukongColliders.STACK_0_1, biped.toolR, "biped/thrust/thrust_heavy1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(4F))
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN,WukongMoveCoordFunctions.TRACE_LOCROT_TARGETO)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, WukongMoveCoordFunctions.TRACE_LOCROT_TARGETO)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.FIXED_HEAD_ROTATION, true)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {WukongMoveCoordFunctions.reseTSjzt();}), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F))
                .addEvents(allStopMovement).addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);}), AnimationEvent.Side.SERVER));
        THRUST_CHARGED2 = new WukongScaleStaffAttackAnimation(0F, 0.6F, 0.733F, 2.766F, WukongColliders.THRUST_CHARGED2, biped.toolR, "biped/thrust/thrust_heavy2", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(6.25F))
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN,WukongMoveCoordFunctions.TRACE_LOCROT_TARGETO)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, WukongMoveCoordFunctions.TRACE_LOCROT_TARGETO)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.FIXED_HEAD_ROTATION, true)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {WukongMoveCoordFunctions.reseTSjzt();}), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F))
                .addEvents(allStopMovement).addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);}), AnimationEvent.Side.SERVER));
        List<AnimationEvent.TimeStampedEvent> charged2 = append(AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER), getScaleEvents(ScaleTime.of(0F, 1, 1F, 1F, 0F, 0F, 0F), ScaleTime.of(0.2F, 1, 1F, 1F, 0F, 0F, 0F), ScaleTime.of(0.66F, 1, 2F, 1F, 0F, -1F, 0F), ScaleTime.of(1F, 1, 2F, 1F, 0F, -1F, 0F), ScaleTime.of(1.333F, 1, 1F, 1F, 0F, 0F, 0F), ScaleTime.reset(1.333F)));
        THRUST_CHARGED2.addEvents(charged2.toArray(new AnimationEvent.TimeStampedEvent[0]));

        THRUST_CHARGED3 = new WukongScaleStaffAttackAnimation(0F, 0.56F, 0.833F, 3.33F, WukongColliders.THRUST_CHARGED3, biped.toolR, "biped/thrust/thrust_heavy3", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(8F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(50f))
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN,WukongMoveCoordFunctions.TRACE_LOCROT_TARGETO)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, WukongMoveCoordFunctions.TRACE_LOCROT_TARGETO)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.FIXED_HEAD_ROTATION, true)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {WukongMoveCoordFunctions.reseTSjzt();}), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2.4F));
        List<AnimationEvent.TimeStampedEvent> charged3 = append(AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER), getScaleEvents(ScaleTime.of(0F, 1, 1F, 1F, 0F, 0F, 0F), ScaleTime.of(0.266F, 1, 1.5F, 1F, 0F, 1F, 0F), ScaleTime.of(0.333F, 1, 2F, 1F, 0F, 0F, 0F), ScaleTime.of(0.5F, 1, 3F, 1F, 0F, 0F, 0F), ScaleTime.of(1F, 1, 3F, 1F, 0F, 0F, 0F), ScaleTime.of(2F, 1, 1F, 1F, 0F, 0F, 0F), ScaleTime.reset(2F)));
        THRUST_CHARGED3.addEvents(charged3.toArray(new AnimationEvent.TimeStampedEvent[0]));


        THRUST_JUESICK_FENGCHUANHUA = new WukongScaleStaffAttackAnimation(0F, 0.56F, 0.833F, 3.33F, WukongColliders.THRUST_FENGCHUANHUA, biped.toolR, "biped/thrust/thrust_heavy4_fengchuanhua", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(15.6F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(50F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(50f))
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.2F, 0.766F))
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN,WukongMoveCoordFunctions.TRACE_LOCROT_TARGETO)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, WukongMoveCoordFunctions.TRACE_LOCROT_TARGETO)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.FIXED_HEAD_ROTATION, true)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {WukongMoveCoordFunctions.reseTSjzt();}), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, ((dynamicAnimation, pose, livingEntityPatch, v, v1) -> {
        })).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2F));
        List<AnimationEvent.TimeStampedEvent> fengchuanhua = append(AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER), getScaleEvents(ScaleTime.of(0F, 1, 1F, 1F, 0F, 0F, 0F), ScaleTime.of(0.266F, 1, 1.5F, 1F, 0F, 1F, 0F), ScaleTime.of(0.333F, 1, 2F, 1F, 0F, 1F, 0F), ScaleTime.of(0.5F, 1, 4F, 1F, 0F, 0F, 0F), ScaleTime.of(1F, 1, 4F, 1F, 0F, 0F, 0F), ScaleTime.of(2F, 1, 1F, 1F, 0F, 0F, 0F), ScaleTime.reset(2F)));
        fengchuanhua.add(AnimationEvent.TimeStampedEvent.create(0.3F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, 0F, -5F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 1D, 0.01F));
        fengchuanhua.add(AnimationEvent.TimeStampedEvent.create(0.4F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, 0F, -8F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 2D, 0.01F));
        fengchuanhua.add(AnimationEvent.TimeStampedEvent.create(0.5F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, 0F, -13F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 3D, 0.01F));
        THRUST_JUESICK_FENGCHUANHUA.addEvents(fengchuanhua.toArray(new AnimationEvent.TimeStampedEvent[0]));

        //退寸
        THRUST_RETREAT = new WukongScaleStaffAttackAnimation(0.1F, 0F, 0F, 0.5F, null, biped.toolR, "biped/thrust/thrust_retreat", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 0.5F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.Thrust_CAN_FIRST_DERIVE.get(), false, serverPlayerPatch.getOriginal());}}), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[]{
                // AnimationEvent.TimeStampedEvent.create(0.3333F, (livingEntityPatch, staticAnimation, objects) -> BattleUnit.CUNTUI_jiesuo(livingEntityPatch), AnimationEvent.TimeStampedEvent.Side.SERVER),
                AnimationEvent.TimeStampedEvent.create(0.366F, (livingEntityPatch, staticAnimation, objects) -> BattleUnit.CUNTUI_JIESUO(livingEntityPatch), AnimationEvent.TimeStampedEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(1.166F, (livingEntityPatch, staticAnimation, objects) -> BattleUnit.CUNTUI_SHANGSUO(livingEntityPatch), AnimationEvent.TimeStampedEvent.Side.SERVER)});

        //进尺
        THRUST_FOOTAGE = new WukongScaleStaffAttackAnimation(0F, 1.866F, 2.033F, 3.566f, WukongColliders.THRUST_FOOTAGE, biped.toolR, "biped/thrust/thrust_footage", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.92F))//3.92
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(50f))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.133F, 1.33F))
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN,WukongMoveCoordFunctions.TRACE_LOCROT_TARGETO)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, WukongMoveCoordFunctions.TRACE_LOCROT_TARGETO)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {WukongMoveCoordFunctions.reseTSjzt();}), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, ((dynamicAnimation, pose, livingEntityPatch, v, v1) -> {
                })).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 3F)).addEvents(getScaleEvents(ScaleTime.of(0F, 1, 1F, 1F, 0F, 0F, 0F), ScaleTime.of(1.866F, 1, 1.8F, 1F, 0F, 0.3F, 0F), ScaleTime.of(2.033F, 1, 3F, 1F, 0F, 1F, 0F), ScaleTime.of(2.033F, 1, 2.5F, 1F, 0F, 1F, 0F), ScaleTime.of(3.5F, 1, 1F, 1F, 0F, 0F, 0F), ScaleTime.reset(3.5F)));

        THRUST_JUESICK_LOOP = new BasicAttackAnimation(0.15F, "biped/thrust/thrust_juesick_loop", biped, new AttackAnimation.Phase(0.0F, 0F, 0.333F, 1.333F, 0.333F, biped.toolR, null).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.672f)), new AttackAnimation.Phase(0.333F, 0.333F, 0.666F, 1.333F, 0.666F, biped.toolR, null).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.672f)), new AttackAnimation.Phase(0.666F, 0.666F, 1F, 1.333F, 1F, biped.toolR, null).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.672f)), new AttackAnimation.Phase(1F, 1F, 1.333F, 1.333F, 1.333F, biped.toolR, null).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.672f))).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F)).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, true).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(THRUST_JUESICK_LOOP);
                }), AnimationEvent.Side.SERVER));


        THRUST_JUESICK_START = new AttackAnimation(0F, "biped/thrust/thrust_juesick_start", biped, new AttackAnimation.Phase(0.0F, 0.866F, 1F, 1.6666F, 1.6666F, biped.toolR, WukongColliders.THRUST_JUESICK_START).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.68f))).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, true).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true).addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 3F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.reserveAnimation(THRUST_JUESICK_LOOP);
        }), AnimationEvent.Side.SERVER));

        THRUST_JUESICK_END = new WukongScaleStaffAttackAnimation(0F, 0F, 0F, 0.9f, null, biped.toolR, "biped/thrust/thrust_juesick_end", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.Thrust_JUESICK_BACK.get(), false, serverPlayerPatch.getOriginal());
            }
        }), AnimationEvent.Side.SERVER));


        PILLAR_HEAVY3_SAGE = new BasicMultipleAttackAnimation(0F, "biped/pillar/stick_heavy_sage", biped, new AttackAnimation.Phase(0.0F, 1.3666F, 1.5666F, 5.93333F, 1.6666F, biped.toolR, WukongColliders.PILLAR_HEAVY3_SAGE).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2f)), new AttackAnimation.Phase(1.6666F, 2.6666F, 2.7666F, 5.93333F, 2.8333F, biped.toolR, WukongColliders.PILLAR_HEAVY3_SAGE).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2f)), new AttackAnimation.Phase(2.8333F, 3f, 3.16666F, 5.93333F, 3.26666F, biped.toolR, WukongColliders.PILLAR_HEAVY3_SAGE).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2f)), new AttackAnimation.Phase(3.26666F, 3.933f, 4F, 5.93333F, 5.93333F, biped.toolR, WukongColliders.PILLAR_HEAVY3_SAGE).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(13f))).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 4.4666F)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
            if (elapsedTime > 1.6333F && elapsedTime < 2.2f) {
                return 3F * speed;
            }
            return 1.2F * speed;
        }).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.2F));
        List<AnimationEvent.TimeStampedEvent> scList5 = append(AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER), getScaleEvents(ScaleTime.of(0F, 1, 1F, 1F, 0F, 0F, 0F), ScaleTime.of(1.3333F, 1, 1.2F, 1F, 0F, -0.2F, 0F), ScaleTime.of(2.13333F, 1, 2.0F, 1F, 0F, -1.2F, 0F), ScaleTime.reset(5.93333F)));
        scList5.add(AnimationEvent.TimeStampedEvent.create(1.5666F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, 0F, 0F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 3D, 0.01F));
        scList5.add(AnimationEvent.TimeStampedEvent.create(2.8333F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, -5F, -5F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 2D, 0.01F));
        scList5.add(AnimationEvent.TimeStampedEvent.create(3.066F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, -5F, -5F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 4D, 0.01F));
        scList5.add(AnimationEvent.TimeStampedEvent.create(3.966666F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, -5F, -5F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 7D, 0.01F));
        PILLAR_HEAVY3_SAGE.addEvents(scList5.toArray(new AnimationEvent.TimeStampedEvent[0]));


        IDLE = new StaticAnimation(true, "biped/idle", biped);
        WALK = new StaticAnimation(true, "biped/walk", biped).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.2F));
        RUN_F = new StaticAnimation(true, "biped/run_f", biped);
        RUN = new SelectiveAnimation((entityPatch) -> {
            Vec3 view = entityPatch.getOriginal().getViewVector(1.0F);
            Vec3 move = entityPatch.getOriginal().getDeltaMovement();
            double dot = view.dot(move);
            return dot < 0.0 ? 1 : 0;
        }, "biped/run", RUN_F, WALK);
        DASH = new StaticAnimation(true, "biped/dash", biped);
        JUMP = new StaticAnimation(0.15F, false, "biped/jump", biped).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.2F));
        FALL = new StaticAnimation(0.15F, true, "biped/fall", biped);
        DODGE_F1 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_f1", 0.6F, 0.8F, biped);
        DODGE_B1 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_b1", 0.6F, 0.8F, biped);
        DODGE_R1 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_r1", 0.6F, 0.8F, biped);
        DODGE_L1 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_l1", 0.6F, 0.8F, biped);
        DODGE_F2 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_f2", 0.6F, 0.8F, biped);
        DODGE_B2 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_b2", 0.6F, 0.8F, biped);
        DODGE_R2 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_r2", 0.6F, 0.8F, biped);
        DODGE_L2 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_l2", 0.6F, 0.8F, biped);
        DODGE_F3 = new WukongDodgeAnimation(0.1F, 0.6F, "biped/dodge/dodge_f3", 0.6F, 1.35F, biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true);
        DODGE_B3 = new WukongDodgeAnimation(0.1F, 0.6F, "biped/dodge/dodge_b3", 0.6F, 1.35F, biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true);
        DODGE_R3 = new WukongDodgeAnimation(0.1F, 0.6F, "biped/dodge/dodge_r3", 0.6F, 1.35F, biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true);
        DODGE_L3 = new WukongDodgeAnimation(0.1F, 0.6F, "biped/dodge/dodge_l3", 0.6F, 1.35F, biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true);
        DODGE_FP = new WukongDodgeAnimation(0.1F, 0.63F, "biped/dodge/dodge_fp", 0.6F, 1.35F, biped, true);
        DODGE_BP = new WukongDodgeAnimation(0.1F, 0.63F, "biped/dodge/dodge_bp", 0.6F, 1.35F, biped, true);
        DODGE_RP = new WukongDodgeAnimation(0.1F, 0.63F, "biped/dodge/dodge_rp", 0.6F, 1.35F, biped, true);
        DODGE_LP = new WukongDodgeAnimation(0.1F, 0.63F, "biped/dodge/dodge_lp", 0.6F, 1.35F, biped, true);

        STAFF_AUTO1_DASH = new BasicAttackAnimation(0.15F, 0.2916F, 0.5000F, 0.51F, null, biped.toolR, "biped/auto_1_dash", biped).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                //冲刺攻击重置普攻计数器
                BasicAttack.setComboCounterWithEvent(ComboCounterHandleEvent.Causal.ANOTHER_ACTION_ANIMATION, serverPlayerPatch, serverPlayerPatch.getSkill(SkillSlots.BASIC_ATTACK), staticAnimation, 1);
            }
        }), AnimationEvent.Side.SERVER));
        STAFF_AUTO1 = new BasicAttackAnimation(0.15F, 0.2916F, 0.5000F, 0.51F, null, biped.toolR, "biped/auto_1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F))
                .addEvents(AnimationEvent.TimeStampedEvent.create(0F, ((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.playSound(WuKongSounds.STAFF1.get(), 1, 1);
        }), AnimationEvent.Side.SERVER)).addEvents(getScaleEvents(ScaleTime.of(0F, 1F, 1F, 1F, 0F, 0F, 0F), ScaleTime.reset(0.2F)));


        STAFF_AUTO2 = new BasicAttackAnimation(0.15F, 0.6667F, 0.875F, 0.875F, null, biped.toolR, "biped/auto_2", biped).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F)).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F)).addEvents(AnimationEvent.TimeStampedEvent.create(0F, ((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.playSound(WuKongSounds.STAFF2.get(), 1, 1);
        }), AnimationEvent.Side.SERVER)).addEvents(getScaleEvents(ScaleTime.of(0F, 1F, 1F, 1F, 0F, 0F, 0F), ScaleTime.reset(0.2F)));

        STAFF_AUTO3 = new BasicMultipleAttackAnimation(0.15F, "biped/auto_3", biped, new AttackAnimation.Phase(0.0F, 0.25F, 0.4583F, 0.4583F, 0.4583F, biped.toolR, null).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F)).addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(4.0F)), new AttackAnimation.Phase(0.4583F, 0.4583F, 0.7083F, 0.7083F, 3.3333F, biped.toolR, null).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F)).addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(4.0F))).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.2F)).addEvents(getScaleEvents(ScaleTime.of(0F, 1F, 1F, 1F, 0F, 0F, 0F), ScaleTime.reset(0.2F)));
        STAFF_AUTO4 = new BasicMultipleAttackAnimation(0.15F, "biped/auto_4", biped, new AttackAnimation.Phase(0.0F, 0.1F, 0.2F, 0.2F, 0.2F, biped.toolR, null).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F)).addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD.get()), new AttackAnimation.Phase(0.2F, 0.2F, 0.4F, 0.4F, 0.4F, biped.toolR, null).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F)).addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD.get()), new AttackAnimation.Phase(0.4F, 0.4F, 0.6F, 0.6F, 0.6F, biped.toolR, null).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F)).addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD.get()), new AttackAnimation.Phase(0.6F, 0.6F, 0.8F, 0.8F, 0.8F, biped.toolR, null).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F)).addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD.get()), new AttackAnimation.Phase(0.8F, 1.0416F, 1.125F, 1.2583F, 2.5F, biped.toolR, null).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F)).addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5F))).addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.2F)).addEvents(AnimationEvent.TimeStampedEvent.create(1.125F, ((livingEntityPatch, staticAnimation, objects) -> {
            LivingEntity self = livingEntityPatch.getOriginal();
            if (self.getMainHandItem().is(WukongItems.KANG_JIN.get())) {
                if (livingEntityPatch.getTarget() != null && self.level() instanceof ServerLevel serverLevel) {
                    EntityType.LIGHTNING_BOLT.spawn(serverLevel, livingEntityPatch.getTarget().getOnPos(), MobSpawnType.TRIGGERED);
                }
            }
        }), AnimationEvent.Side.SERVER)).addEvents(AnimationEvent.TimeStampedEvent.create(0F, ((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.playSound(WuKongSounds.STAFF4.get(), 1, 1);
        }), AnimationEvent.Side.SERVER)).addEvents(getScaleEvents(ScaleTime.of(0F, 1F, 1F, 1F, 0F, 0F, 0F), ScaleTime.reset(0.2F)));

        STAFF_AUTO5 = new BasicAttackAnimation(0.01F, 0.9166F, 1.15F, 1.9833F, null, biped.toolR, "biped/auto_5", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.9833F))
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOCROT_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOCROT_TARGET)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.2F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(EpicFightSounds.ENTITY_MOVE.get(), 1, 1)), AnimationEvent.Side.SERVER)).addEvents(AnimationEvent.TimePeriodEvent.create(0.01F, 1.9833F, ((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch playerPatch && WukongWeaponCategories.isWeaponValid(playerPatch)) {
                playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.DAMAGE_REDUCE.get(), 0.5F);//设置减伤
                playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.THRUST_PROTECT_NEXT_FALL.get(), true);//设置减伤
                playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.PROTECT_NEXT_FALL.get(), true);//设置减伤
            }
        }), AnimationEvent.Side.SERVER));

        List<AnimationEvent.TimeStampedEvent> Attack5 = append(AnimationEvent.TimeStampedEvent.create(1F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.wave5.get(), 1, 1)), AnimationEvent.Side.SERVER), getScaleEvents(ScaleTime.of(1F, 1, 1F, 1F, 0F, 0F, 0F), ScaleTime.reset(1F)));
        Attack5.add(AnimationEvent.TimeStampedEvent.create(1F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, 0F, -2.3F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 1.5D, 0.01F));
        STAFF_AUTO5.addEvents(Attack5.toArray(new AnimationEvent.TimeStampedEvent[0]));

        JUMP_ATTACK_LIGHT = new WukongJumpAttackAnimation(0.10F, 0.13F, 0.40F, 0.50F, WukongColliders.JUMP_ATTACK_LIGHT, biped.toolR, "biped/jump_attack/jump_light_pre", biped).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.45F)).addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))//最多踹一个
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.10F)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F));
        JUMP_ATTACK_LIGHT_HIT = new ActionAnimation(0.15F, "biped/jump_attack/jump_light_hit", biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.3F)).addState(EntityState.CAN_SKILL_EXECUTION, true)//为了可以用重击取消后摇
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F));
        JUMP_ATTACK_HEAVY = new WukongScaleStaffAttackAnimation(0.01F, 0.54F, 0.67F, 1.25F, null, biped.toolR, "biped/jump_attack/jump_heavy", biped).addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG).addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get()).addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F)).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.67F)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(EpicFightSounds.ROLL.get(), 1, 1)), AnimationEvent.Side.SERVER)).addEvents(AnimationEvent.TimePeriodEvent.create(0.01F, 0.5F, ((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch playerPatch && WukongWeaponCategories.isWeaponValid(playerPatch)) {
                playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.DAMAGE_REDUCE.get(), 0.5F);//设置减伤
            }
        }), AnimationEvent.Side.SERVER));
        ;

        STAFF_SPIN_ONE_HAND_LOOP = new StaffSpinAttackAnimation(1.25F, biped, "biped/staff_spin/staff_spin_one_hand", 0.05F, false);
        STAFF_SPIN_TWO_HAND_LOOP = new StaffSpinAttackAnimation(0.83F, biped, "biped/staff_spin/staff_spin_two_hand", 0.08F, true);

        //劈start
        //前摇完自动接下一个动作
        SMASH_CHARGING_PRE = new ActionAnimation(0.15F, "biped/smash/smash_charge_pre", biped).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.reserveAnimation(SMASH_CHARGING_LOOP_STAND);
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.IS_CHARGING.get(), true, serverPlayerPatch.getOriginal());
            }
        }), AnimationEvent.Side.SERVER)).addEvents(AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.playSound(EpicFightSounds.WHOOSH_ROD.get(), 1, 1);
        }), AnimationEvent.Side.SERVER), AnimationEvent.TimeStampedEvent.create(0.2F, ((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                if (serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(WukongSkillDataKeys.KEY_PRESSING.get()))
                    livingEntityPatch.playSound(WuKongSounds.XULI_LEVEL_RISE03.get(), 0, 0);
            }
        }), AnimationEvent.Side.SERVER), AnimationEvent.TimeStampedEvent.create(0.3F, ((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.playSound(EpicFightSounds.WHOOSH_ROD.get(), 1, 1);
        }), AnimationEvent.Side.SERVER));
        SMASH_CHARGING_LOOP_STAND = new StaticAnimation(0.15F, true, "biped/smash/smash_charging", biped);


        SMASH_CHARGED0 = new WukongScaleStaffAttackAnimation(0.15F, 0.75F, 0.92F, 1.67F, WukongColliders.STACK_0_1, biped.toolR, "biped/smash/smash_heavy0", biped).addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG).addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F)).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.6F)).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.75F)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)).addEvents(allStopMovement).addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);
        }), AnimationEvent.Side.SERVER));
        SMASH_CHARGED1 = new WukongScaleStaffAttackAnimation(0.15F, 0.75F, 0.92F, 1.67F, WukongColliders.STACK_0_1, biped.toolR, "biped/smash/smash_heavy1", biped).addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F)).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(5.6F)).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.75F)).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)).addEvents(allStopMovement).addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);
        }), AnimationEvent.Side.SERVER));
        SMASH_CHARGED2 = new WukongScaleStaffAttackAnimation(0.15F, 1.30F, 1.55F, 2.5F, WukongColliders.STACK_2, biped.toolR, "biped/smash/smash_heavy2", biped).addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(4.0F)).addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get()).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(8.8F)).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.30F)).newTimePair(0, 2.5F).addState(EntityState.TURNING_LOCKED, true).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)).addEvents(AnimationEvent.TimePeriodEvent.create(0.01F, 2.5F, ((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch playerPatch && WukongWeaponCategories.isWeaponValid(playerPatch)) {
                playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.DAMAGE_REDUCE.get(), 0.5F);//设置减伤
            }
        }), AnimationEvent.Side.SERVER));
        ;
        List<AnimationEvent.TimeStampedEvent> sc2List = append(AnimationEvent.TimeStampedEvent.create(0.292F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER), getScaleEvents(ScaleTime.reset(1.30F), ScaleTime.of(1.45F, 1, 1.8F, 1, 0F, 0F, 0F), ScaleTime.of(2.13F, 1, 1.8F, 1, 0F, 0F, 0F), ScaleTime.reset(2.29F)));
        sc2List.add(AnimationEvent.TimeStampedEvent.create(0.833F, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER));
        SMASH_CHARGED2.addEvents(sc2List.toArray(new AnimationEvent.TimeStampedEvent[0]));

        SMASH_CHARGED3 = new WukongScaleStaffAttackAnimation(0.15F, 1.792F, 1.958F, 2.667F, WukongColliders.STACK_3, biped.toolR, "biped/smash/smash_heavy3", biped).addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(6.0F)).addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.NEUTRALIZE).addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get()).addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get()).addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.5F)).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(11)).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.30F)).newTimePair(0, 2.667F).addState(EntityState.TURNING_LOCKED, true).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)).addEvents(AnimationEvent.TimePeriodEvent.create(0.01F, 2.6667F, ((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch playerPatch && WukongWeaponCategories.isWeaponValid(playerPatch)) {
                playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.DAMAGE_REDUCE.get(), 0.7F);//设置减伤
            }
        }), AnimationEvent.Side.SERVER));
        List<AnimationEvent.TimeStampedEvent> sc3List = append(AnimationEvent.TimeStampedEvent.create(0.292F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER), getScaleEvents(ScaleTime.reset(1.667F), ScaleTime.of(1.792F, 1, 2.4F, 1, 0F, 0F, 0F), ScaleTime.of(1.958F, 1, 2.4F, 1, 0F, 0F, 0F), ScaleTime.of(2.667F, 1, 2F, 1, 0F, 0F, 0F), ScaleTime.reset(2.8F)));
        sc3List.add(AnimationEvent.TimeStampedEvent.create(0.833F, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER));
        sc3List.add(AnimationEvent.TimeStampedEvent.create(1.125F, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER));
        SMASH_CHARGED3.addEvents(sc3List.toArray(new AnimationEvent.TimeStampedEvent[0]));

        SMASH_CHARGED4 = new WukongScaleStaffAttackAnimation(0.15F, 2.63F, 2.8F, 3.3F, WukongColliders.STACK_4, biped.toolR, "biped/smash/smash_heavy4", biped).addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(2.0F)).addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.NEUTRALIZE).addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get()).addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD.get()).addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F)).addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(15.5F)).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 2.75F)).newTimePair(0, 3.3F).addState(EntityState.TURNING_LOCKED, true).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.3F)).addEvents(AnimationEvent.TimePeriodEvent.create(0.01F, 3.3F, ((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch playerPatch && WukongWeaponCategories.isWeaponValid(playerPatch)) {
                playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.DAMAGE_REDUCE.get(), 0.9F);//设置减伤
            }
        }), AnimationEvent.Side.SERVER));
        ;
        List<AnimationEvent.TimeStampedEvent> sc4List = append(AnimationEvent.TimeStampedEvent.create(0.208F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER), getScaleEvents(ScaleTime.of(2.4167F, 1, 1, 1, 0F, 0F, 0F), ScaleTime.of(2.5833F, 1F, 1.15F, 1F, 0F, 0F, 0F), ScaleTime.of(2.7083F, 1.5F, 3.15F, 1.15F, 0F, 0F, 0F), ScaleTime.of(3.3333F, 1.5F, 3.15F, 1.5F, 0F, 0F, 0F), ScaleTime.of(3.5833F, 1, 1, 1, 0F, 0F, 0F)));
        sc4List.add(AnimationEvent.TimeStampedEvent.create(2.8F, ((livingEntityPatch, staticAnimation, objects) -> {
            LivingEntity entity = livingEntityPatch.getOriginal();
            Vec3 viewVec = entity.getViewVector(1.0F);
            Vec3 hVec = viewVec.add(0, -viewVec.y, 0);
            Vec3 target = entity.position().add(hVec.normalize().scale(4)).add(0, -2, 0);
            LevelUtil.circleSlamFracture(entity, entity.level(), target, 3.0);
        }), AnimationEvent.Side.SERVER));
        SMASH_CHARGED4.addEvents(sc4List.toArray(new AnimationEvent.TimeStampedEvent[0]));

        SMASH_DERIVE1 = new WukongScaleStaffAttackAnimation(0.15F, 0.63F, 0.75F, 1.20F, null, biped.toolR, "biped/smash/smash_special1", biped).addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD).addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F)).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                dataManager.setDataSync(WukongSkillDataKeys.CAN_FIRST_DERIVE.get(), false, serverPlayerPatch.getOriginal());
                dataManager.setDataSync(WukongSkillDataKeys.IS_IN_SPECIAL_ATTACK.get(), true, serverPlayerPatch.getOriginal());
                dataManager.setDataSync(WukongSkillDataKeys.IS_SPECIAL_SUCCESS.get(), false, serverPlayerPatch.getOriginal());
            }
        }), AnimationEvent.Side.SERVER)).addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(0.75F, ((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.IS_IN_SPECIAL_ATTACK.get(), false, serverPlayerPatch.getOriginal());
            }
        }), AnimationEvent.Side.SERVER)).addEvents(getScaleEvents(ScaleTime.of(0.625F, 1, 1.8F, 1, 0F, 0F, 0F), ScaleTime.of(1.125F, 1, 1.8F, 1, 0F, 0F, 0F), ScaleTime.reset(1.25F)));

        SMASH_DERIVE2 = new WukongScaleStaffAttackAnimation(0.15F, 1.04F, 1.71F, 2.30F, null, biped.toolR, "biped/smash/smash_special2", biped).addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG).addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.0F)).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true).addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false).addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.04F)).newTimePair(0.01F, 1.71F).addState(EntityState.ATTACK_RESULT, (damageSource) -> AttackResult.ResultType.MISSED).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)).addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.CAN_SECOND_DERIVE.get(), false, serverPlayerPatch.getOriginal());
            }
        }), AnimationEvent.Side.SERVER)).addEvents(append(AnimationEvent.TimeStampedEvent.create(0.042F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER), getScaleEvents(ScaleTime.of(0.042F, 1, 1.583F, 1, 0F, 0F, 0F), ScaleTime.of(0.083F, 1, 1.758F, 1, 0F, 0F, 0F), ScaleTime.of(0.167F, 1, 1.952F, 1, 0F, 0F, 0F), ScaleTime.of(0.208F, 1, 2, 1, 0F, 0F, 0F), ScaleTime.of(1.458F, 1, 2, 1, 0F, 0F, 0F), ScaleTime.reset(1.460F))).toArray(new AnimationEvent.TimeStampedEvent[0]));
        //劈end

    }

    public void applyKnockback(ServerPlayer player, Entity target, double knockbackStrength) {
        if (target instanceof LivingEntity) {
            double directionX = target.getX() - player.getX();
            double directionZ = target.getZ() - player.getZ();
            double distance = Math.sqrt(directionX * directionX + directionZ * directionZ);

            if (distance > 0.1) {
                directionX /= distance;  // 归一化方向
                directionZ /= distance;
                target.push(directionX * knockbackStrength, 0.0, directionZ * knockbackStrength);
            }
        }
    }
    public static void CameraOperationFov(float increaseAmount, int durationTicks, int repeatTimes) {
        Minecraft MC = Minecraft.getInstance();
        float startFov = MC.options.fov().get();
        float targetFov = Math.min(startFov + increaseAmount, 97F);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        AtomicInteger tickCount = new AtomicInteger(0);
        AtomicInteger repeatCount = new AtomicInteger(0);
        Runnable task = new Runnable() {
            private float currentStartFov = startFov;
            @Override
            public void run() {
                int currentTick = tickCount.incrementAndGet();
                if (currentTick > durationTicks) {
                    tickCount.set(0);
                    repeatCount.incrementAndGet();
                    if (repeatCount.get() >= repeatTimes) {
                        scheduler.shutdown();
                        return;
                    }
                    currentStartFov = MC.options.fov().get();
                }
                float progress = (float) currentTick / durationTicks;
                float newFov = currentStartFov + (targetFov - currentStartFov) * progress;
                MC.options.fov().set((int) newFov);
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 10, TimeUnit.MILLISECONDS);
    }
    public static void addItemEffectTimer(ServerPlayer serverPlayer, int leftTime) {
        serverPlayer.getMainHandItem().getCapability(EpicFightCapabilities.CAPABILITY_ITEM).ifPresent((capabilityItem -> {
            if (capabilityItem.getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF)) {
                serverPlayer.getMainHandItem().getOrCreateTag().putInt(WukongMoveset.ITEM_HAS_EFFECT_TIMER_KEY, leftTime);
            }
        }));
    }

    public static List<AnimationEvent.TimeStampedEvent> append(AnimationEvent.TimeStampedEvent e, AnimationEvent.TimeStampedEvent... oldArr) {
        List<AnimationEvent.TimeStampedEvent> list = new ArrayList<>(List.of(oldArr));
        list.add(e);
        return list;
    }

    /**
     * 添加物品缩放，并插值
     * 用途：{@link com.p1nero.wukong.mixin.ItemRendererMixin}
     */
    public static AnimationEvent.TimeStampedEvent[] getScaleEvents(ScaleTime... ticks) {
        int lastTick = ticks[ticks.length - 1].tick;
        AnimationEvent.TimeStampedEvent[] timeStampedEvents = new AnimationEvent.TimeStampedEvent[lastTick];
        ticks = interpolate(ticks, lastTick);

        // First event, no scaling, no translation
        timeStampedEvents[0] = AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
            if (!WukongWeaponCategories.isWeaponValid(livingEntityPatch)) {
                return;
            }
            CompoundTag tag = livingEntityPatch.getOriginal().getMainHandItem().getOrCreateTag();
            tag.putBoolean("WK_shouldScaleItem", false);
            tag.putBoolean("WK_shouldTranslateItem", false);
        }), AnimationEvent.Side.CLIENT);

        // Last event, no scaling, no translation
        timeStampedEvents[lastTick - 1] = AnimationEvent.TimeStampedEvent.create(0.05F * lastTick, ((livingEntityPatch, staticAnimation, objects) -> {
            if (!WukongWeaponCategories.isWeaponValid(livingEntityPatch)) {
                return;
            }
            CompoundTag tag = livingEntityPatch.getOriginal().getMainHandItem().getOrCreateTag();
            tag.putBoolean("WK_shouldScaleItem", false);
            tag.putBoolean("WK_shouldTranslateItem", false);
        }), AnimationEvent.Side.CLIENT);

        // Interpolated events
        for (int i = 1; i < lastTick - 1; i++) {
            float x = ticks[i].x;
            float y = ticks[i].y;
            float z = ticks[i].z;
            float tx = ticks[i].tx;
            float ty = ticks[i].ty;
            float tz = ticks[i].tz;

            timeStampedEvents[i] = AnimationEvent.TimeStampedEvent.create(0.05F * i, ((livingEntityPatch, staticAnimation, objects) -> {
                if (!WukongWeaponCategories.isWeaponValid(livingEntityPatch)) {
                    return;
                }
                CompoundTag tag = livingEntityPatch.getOriginal().getMainHandItem().getOrCreateTag();
                tag.putBoolean("WK_shouldScaleItem", true);
                tag.putBoolean("WK_shouldTranslateItem", true);
                tag.putFloat("WK_XScale", x);
                tag.putFloat("WK_YScale", y);
                tag.putFloat("WK_ZScale", z);
                tag.putFloat("WK_XTranslation", tx);
                tag.putFloat("WK_YTranslation", ty);
                tag.putFloat("WK_ZTranslation", tz);
            }), AnimationEvent.Side.CLIENT);
        }

        return timeStampedEvents;
    }


    /**
     * 插值处理
     *
     * @param scaleTimes 需要插值的时间点，按tick算！
     * @param lastTick   最后一个tick，将对0~lastTick的每个tick插值处理
     * @return 插值后的数组
     */
    public static ScaleTime[] interpolate(ScaleTime[] scaleTimes, int lastTick) {
        ScaleTime[] results = new ScaleTime[lastTick + 1];

        // Fill known values
        for (ScaleTime scaleTime : scaleTimes) {
            if (scaleTime.tick <= lastTick) {
                results[scaleTime.tick] = scaleTime;
            }
        }

        // Perform linear interpolation
        for (int i = 0; i <= lastTick; i++) {
            if (results[i] == null) {
                // Find the two surrounding points
                ScaleTime before = null;
                ScaleTime after = null;

                for (int j = i - 1; j >= 0; j--) {
                    if (results[j] != null) {
                        before = results[j];
                        break;
                    }
                }

                for (int j = i + 1; j <= lastTick; j++) {
                    if (results[j] != null) {
                        after = results[j];
                        break;
                    }
                }

                if (before != null && after != null) {
                    // Linear interpolation for scale and translation
                    float t = (float) (i - before.tick) / (after.tick - before.tick);
                    float x = before.x + t * (after.x - before.x);
                    float y = before.y + t * (after.y - before.y);
                    float z = before.z + t * (after.z - before.z);
                    float tx = before.tx + t * (after.tx - before.tx);
                    float ty = before.ty + t * (after.ty - before.ty);
                    float tz = before.tz + t * (after.tz - before.tz);
                    results[i] = new ScaleTime(i, x, y, z, tx, ty, tz);
                }
            }
        }

        // Fill in nulls with the closest known value (forward filling)
        for (int i = 0; i <= lastTick; i++) {
            if (results[i] == null) {
                if (i > 0) {
                    results[i] = results[i - 1]; // Copy the last known value
                } else {
                    results[i] = new ScaleTime(i, 1, 1, 1, 0, 0, 0);
                }
            }
        }

        return results;
    }

    public record ScaleTime(int tick, float x, float y, float z, float tx, float ty, float tz) {
        public static ScaleTime of(float time, float x, float y, float z, float tx, float ty, float tz) {
            return new ScaleTime(((int) (time * 20)), x, y, z, tx, ty, tz);
        }

        public static ScaleTime reset(float time) {
            return new ScaleTime(((int) (time * 20)), 1, 1, 1, 0, 0, 0);
        }
    }


    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getMainHandItem().getCapability(EpicFightCapabilities.CAPABILITY_ITEM).ifPresent((capabilityItem -> {
                if (capabilityItem.getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF)) {
                    CompoundTag mainHandItem = serverPlayer.getMainHandItem().getOrCreateTag();
                    mainHandItem.putInt(WukongMoveset.ITEM_HAS_EFFECT_TIMER_KEY, Math.max(0, mainHandItem.getInt(WukongMoveset.ITEM_HAS_EFFECT_TIMER_KEY) - 1));
                }
            }));
        }
    }

}
