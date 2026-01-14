package com.p1nero.wukong.epicfight.animation;


import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.epicfight.animation.custom.BasicMultipleAttackAnimation;
import com.p1nero.wukong.epicfight.animation.custom.WukongScaleStaffAttackAnimation;
import com.p1nero.wukong.epicfight.weapon.WukongColliders;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

import java.util.List;

import static com.p1nero.wukong.epicfight.animation.WukongAnimations.append;
import static com.p1nero.wukong.epicfight.animation.WukongAnimations.getScaleEvents;



@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongGreatSageAnimations {

    public static StaticAnimation BROKEN_STICK_STYLE;
    public static StaticAnimation FENG_YUN_SAO_STYLE;//凤云扫
    public static StaticAnimation SAO_CHUO_SHI_STYLE;//扫戳式




    // 轻击 1~5
    public static StaticAnimation greatsage_staff_auto1_dash;
    public static StaticAnimation STAFF_AUTO1_1;
    public static StaticAnimation STAFF_AUTO1_2;
    public static StaticAnimation STAFF_AUTO1_3;
    public static StaticAnimation STAFF_AUTO1;
    public static StaticAnimation STAFF_AUTO2_1;
    public static StaticAnimation STAFF_AUTO2_2;
    public static StaticAnimation STAFF_AUTO2_3;
    public static StaticAnimation STAFF_AUTO3_3;
    public static StaticAnimation STAFF_AUTO3;
    public static StaticAnimation STAFF_AUTO4;
    public static StaticAnimation STAFF_AUTO5;
    public static StaticAnimation STAFF_CS;


    public WukongGreatSageAnimations() {
    }

    static void build() {
        WukongMoveset.LOGGER.info("WukongGreatSageAnimations注册");
        HumanoidArmature biped = Armatures.BIPED;
        BROKEN_STICK_STYLE =new WukongScaleStaffAttackAnimation(0.1F, 1.666F, 1.8333F, 2.33F, null, biped.toolR,  "biped/greatsage/broken_stick_style", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                    if (elapsedTime >= 1.33f && elapsedTime <= 2f) {
                        return 2.5F;
                    }
                    return 2F;
                });
        FENG_YUN_SAO_STYLE = new WukongScaleStaffAttackAnimation(0.1F, 0.5F, 0.66F, 1.33F, null, biped.toolR,  "biped/greatsage/feng_yun_sao", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F));

        SAO_CHUO_SHI_STYLE = new BasicMultipleAttackAnimation(0F,  "biped/greatsage/sao_chuo_shi", biped,
                new AttackAnimation.Phase(0.0F, 0.5F, 0.66F, 3F, 0.76F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F)),
                new AttackAnimation.Phase(0.76F, 2f, 2.433F, 3F, 3F, biped.toolR, WukongColliders.PILLAR_HEAVY_RIVERSEAFLIP)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(4.48f)))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                    if (elapsedTime >= 1f && elapsedTime <= 1.9f) {
                        return 2.3F;
                    }else if (elapsedTime >= 1.9f && elapsedTime <= 2.5f) {
                        return 2.6F;
                    }
                    return 1.8F;
                });




        STAFF_AUTO1_1 = new ActionAnimation(0F,  "biped/greatsage/auto1_1", biped)
                 .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)  // 禁用垂直移动
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)  // 禁用取消移动
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F));
        STAFF_AUTO1_2 = new ActionAnimation(0F, 0F,  "biped/greatsage/auto1_2", biped)
                 .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F));
        STAFF_AUTO1_3 = new ActionAnimation(0F,  "biped/greatsage/auto1_3", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F));
        STAFF_AUTO2_1 = new WukongScaleStaffAttackAnimation(0.1F, 0F, 2F, 5.23F, null, biped.toolR, "biped/greatsage/auto2_1", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.5F));
        STAFF_AUTO2_2 = new ActionAnimation(0F,  "biped/greatsage/auto2_2", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.8F));

        STAFF_AUTO2_3 =  new BasicMultipleAttackAnimation(0.5F,  "biped/greatsage/auto2_3", biped,
                new AttackAnimation.Phase(0.0F, 0.966F, 1.166f, 6.33f, 1.2f, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F)),
                new AttackAnimation.Phase(1.2f, 2.3f, 2.6666F, 6.33f, 2.7F, biped.toolR, WukongColliders.PILLAR_HEAVY_RIVERSEAFLIP)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(4.48f)),
                new AttackAnimation.Phase(2.7F,4.9f, 5F, 6.33f, 6.33f, biped.toolR, WukongColliders.PILLAR_HEAVY_RIVERSEAFLIP)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(4.48f)))
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(3.33F,4.533f))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                    if (elapsedTime > 0f && elapsedTime < 1.166f) {
                        return 3F;
                    }else if (elapsedTime >5f ) {
                        return 3F;
                    }
                    return 2.3F;
                });
        STAFF_AUTO3_3 = new ActionAnimation(0F,  "biped/greatsage/auto3_3", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(3.83F,6.66f))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 2F));

        STAFF_CS = new ActionAnimation(0F,  "biped/greatsage/auto3_3", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.5F));

        // 为 STAFF_AUTO1 添加自定义攻击动画
        STAFF_AUTO1 = new ActionAnimation(0F,  "biped/greatsage/auto3", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 5.66F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)  // 禁用垂直移动
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)  // 禁用取消移动
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                    if (elapsedTime > 1.83f && elapsedTime < 3.36f) {
                        return 1.5F;
                    }else if (elapsedTime >4.33f ) {
                        return 2F;
                    }
                    return 1.2F;
                });
        List<AnimationEvent.TimeStampedEvent> staff_auto1 = append(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                getScaleEvents(WukongAnimations.ScaleTime.of(0F, 1F, 1F, 1F, 0F, 0F, 0F),
                        WukongAnimations.ScaleTime.of(0.666F, 1.2F, 1F, 1.2F, 0F, 0F, 0F),
                        WukongAnimations.ScaleTime.of(2.333F, 1.3F, 1.8F, 1.3F, 0F, 0F, 0F),
                        WukongAnimations.ScaleTime.of(4.33F, 1.3F, 1.8F, 1.3F, 0F, 0F, 0F),
                        WukongAnimations.ScaleTime.of(4.66F, 1.3F, 1.8F, 1.3F, 0F, 1.7F, 0F),
                        WukongAnimations.ScaleTime.of(8.66F, 1.3F, 1.8F, 1.3F, 0F, 1.7F, 0F)));
         STAFF_AUTO1.addEvents(staff_auto1.toArray(new AnimationEvent.TimeStampedEvent[0]));
    }

//    @SubscribeEvent
//    public static void onClientSetup(FMLClientSetupEvent event) {
//        WukongGreatSageAnimations.build();
//    }
//    @SubscribeEvent
//    public static void onCommonSetup(FMLCommonSetupEvent event) {
//        WukongGreatSageAnimations.build();
//    }
}