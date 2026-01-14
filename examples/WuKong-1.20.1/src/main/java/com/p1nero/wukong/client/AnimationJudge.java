package com.p1nero.wukong.client;


import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import net.minecraft.client.Minecraft;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

import java.util.Arrays;

public record AnimationJudge() {
    static final StaticAnimation[] ZERO_STOR;
    static final StaticAnimation[] ONE_STOR;
    static final StaticAnimation[] TWO_STOR;
    static final StaticAnimation[] THREE_STOR;
    static final StaticAnimation[] FOUR_STOR;
    static final StaticAnimation[] Glow;
    static final StaticAnimation[] GH;


    static {
        Glow = new StaticAnimation[] {
                WukongAnimations.SMASH_CHARGED0,
                WukongAnimations.SMASH_CHARGED1,
                WukongAnimations.SMASH_CHARGED2,
                WukongAnimations.SMASH_CHARGED3,
                WukongAnimations.SMASH_CHARGED4,
                WukongAnimations.THRUST_CHARGED0,
                WukongAnimations.THRUST_CHARGED1,
                WukongAnimations.THRUST_CHARGED2,
                WukongAnimations.THRUST_CHARGED3,
                WukongAnimations.THRUST_JUESICK_FENGCHUANHUA,
                WukongAnimations. PILLAR_HEAVY0,
                WukongAnimations. PILLAR_HEAVY1,
                WukongAnimations. PILLAR_HEAVY2,
                WukongAnimations. PILLAR_HEAVY3,
                WukongAnimations. PILLAR_HEAVY4,

        };
        ZERO_STOR = new StaticAnimation[] {
                WukongAnimations.SMASH_CHARGED0,
                WukongAnimations.THRUST_CHARGED0,
                WukongAnimations. PILLAR_HEAVY0,
        };
        ONE_STOR = new StaticAnimation[] {
                WukongAnimations.SMASH_CHARGED1,
                WukongAnimations.THRUST_CHARGED1,
                WukongAnimations. PILLAR_HEAVY1,
        };
        TWO_STOR = new StaticAnimation[] {
                WukongAnimations.SMASH_CHARGED2,
                WukongAnimations.THRUST_CHARGED2,
                WukongAnimations. PILLAR_HEAVY2,
        };
        THREE_STOR = new StaticAnimation[] {
                WukongAnimations.SMASH_CHARGED3,
                WukongAnimations.THRUST_CHARGED3,
                WukongAnimations. PILLAR_HEAVY3,
        };
        FOUR_STOR = new StaticAnimation[] {
                WukongAnimations.SMASH_CHARGED4,
                WukongAnimations.THRUST_JUESICK_FENGCHUANHUA,
                WukongAnimations. PILLAR_HEAVY4,
        };


        GH = new StaticAnimation[] {
                WukongAnimations.STAFF_SPIN_ONE_HAND_LOOP,
                WukongAnimations.STAFF_SPIN_TWO_HAND_LOOP,
        };


    }
    public static boolean isGh(StaticAnimation staticAnimation) {
        return Arrays.asList(GH).contains(staticAnimation);
    }
    public static boolean isGlow(StaticAnimation staticAnimation) {
       // WukongMoveset.LOGGER.error("isGlow:"+staticAnimation);
      return Arrays.asList(Glow).contains(staticAnimation);
    }
    public static boolean isQie(StaticAnimation staticAnimation) {
        return Arrays.asList(ONE_STOR).contains(staticAnimation);
    }
    public static boolean isTwo(StaticAnimation staticAnimation) {
        return Arrays.asList(TWO_STOR).contains(staticAnimation);
    }
    public static boolean isThree(StaticAnimation staticAnimation) {
        return Arrays.asList(THREE_STOR).contains(staticAnimation);
    }
    public static boolean isFour(StaticAnimation staticAnimation) {
        return Arrays.asList(FOUR_STOR).contains(staticAnimation);
    }
    public static boolean isCharging(LocalPlayerPatch lpp) {
        return lpp.getSkill(SkillSlots.WEAPON_INNATE) != null && (
                (lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().hasData(WukongSkillDataKeys.IS_CHARGING.get()) && lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(WukongSkillDataKeys.IS_CHARGING.get()))
                        || (lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().hasData(WukongSkillDataKeys.IS_CHARGING.get()) && (lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(WukongSkillDataKeys.IS_CHARGING.get())))
                        || (lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().hasData(WukongSkillDataKeys.Thrust_IS_CHARGING.get()) && (lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(WukongSkillDataKeys.Thrust_IS_CHARGING.get()))));
    }


}
