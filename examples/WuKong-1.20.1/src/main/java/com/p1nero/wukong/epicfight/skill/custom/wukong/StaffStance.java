package com.p1nero.wukong.epicfight.skill.custom.wukong;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;



public class StaffStance extends Skill {

    public final WukongStyles style; // 客户端无效，所以得有datakey

    public StaffStance(Builder builder) {
        super(builder);
        this.style = builder.style;
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        if (container.getExecuter().getOriginal() instanceof ServerPlayer serverPlayer) {
            if (container.getSkill() instanceof PillarHeavyAttack) {
                container.getDataManager().setData(WukongSkillDataKeys.STANCE.get(), WukongStyles.PILLAR.ordinal());
            } else if (container.getSkill() instanceof SmashHeavyAttack) {
                container.getDataManager().setData(WukongSkillDataKeys.STANCE.get(), WukongStyles.SMASH.ordinal());
            }else if (container.getSkill() instanceof ThrustHeavyAttack) {
                container.getDataManager().setData(WukongSkillDataKeys.STANCE.get(), WukongStyles.THRUST.ordinal());
            }

            if (!container.getDataManager().hasData(WukongSkillDataKeys.STANCE.get())) {
                String currentSkill = container.getSkill().toString();  // 假设这是技能标识符
                WukongStyles currentStyle;
                if (currentSkill.equals("wukong:pillar_style")) {
                    currentStyle = WukongStyles.PILLAR;
                } else if (currentSkill.equals("wukong:smash_style")) {
                    currentStyle = WukongStyles.SMASH; // 设置为SMASH风格
                } else if (currentSkill.equals("wukong:thrust_style")) {
                    currentStyle = WukongStyles.THRUST; // 设置为THRUST风格
                } else if (currentSkill.equals("wukong:greatsage_style")) {
                    currentStyle = WukongStyles.GREATSAGE; // GREATSAGE_STYLE
                }else {
                    currentStyle = WukongStyles.SMASH; // 默认风格
                }
                container.getDataManager().registerData(WukongSkillDataKeys.STANCE.get());
                container.getDataManager().setData(WukongSkillDataKeys.STANCE.get(), currentStyle.ordinal());
            }
        }
    }



    public static Builder createStaffStyle() {
        return new Builder().setCategory(WukongSkillCategories.STAFF_STYLE).setResource(Resource.NONE);
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        String currentSkill = container.getSkill().toString();  // 假设这是技能标识符

        WukongStyles currentStyle;
        if (currentSkill.equals("wukong:pillar_style")) {
            currentStyle = WukongStyles.PILLAR;
            container.getDataManager().setData(WukongSkillDataKeys.STANCE.get(), WukongStyles.PILLAR.ordinal());
        } else if (currentSkill.equals("wukong:smash_style")) {
            currentStyle = WukongStyles.SMASH; // 设置为SMASH风格
            container.getDataManager().setData(WukongSkillDataKeys.STANCE.get(), WukongStyles.SMASH.ordinal());
        }else if (currentSkill.equals("wukong:thrust_style")) {
            currentStyle = WukongStyles.THRUST; // 设置为THRUST风格
            container.getDataManager().setData(WukongSkillDataKeys.STANCE.get(), WukongStyles.THRUST.ordinal());
        }else if (currentSkill.equals("wukong:greatsage_style")) {
            currentStyle = WukongStyles.GREATSAGE; // 设置为greatsage_style风格
            container.getDataManager().setData(WukongSkillDataKeys.STANCE.get(), WukongStyles.GREATSAGE.ordinal());
        } else {
            currentStyle = WukongStyles.SMASH; // 默认风格
            container.getDataManager().setData(WukongSkillDataKeys.STANCE.get(), WukongStyles.SMASH.ordinal());
        }
       container.getDataManager().setData(WukongSkillDataKeys.STANCE.get(), currentStyle.ordinal());

    }


    /**
     * 得根据key返回，不然客户端不同步。。很奇怪，onInitiate里面setDataSync会出错
     */
    public WukongStyles getStyle(SkillContainer container) {
        if (!container.getDataManager().hasData(WukongSkillDataKeys.STANCE.get())) {
            container.getDataManager().registerData(WukongSkillDataKeys.STANCE.get());
            container.getDataManager().setData(WukongSkillDataKeys.STANCE.get(), WukongStyles.SMASH.ordinal()); // 默认值
        }

        // 获取当前风格值
        Integer stanceValue = container.getDataManager().getDataValue(WukongSkillDataKeys.STANCE.get());
        // 如果没有值，设置为默认姿势
        if (stanceValue == null) {
            stanceValue = WukongStyles.SMASH.ordinal(); // 默认姿势
        }

        // 检查技能类型并根据技能选择风格
        Skill currentSkill = container.getSkill();
        if (currentSkill != null) {
            // 你可以根据当前技能返回不同的风格
            if (currentSkill.equals(WukongSkills.PILLAR_STYLE)) {
                stanceValue = WukongStyles.PILLAR.ordinal();
            } else if (currentSkill.equals(WukongSkills.THRUST_STYLE)) {
                stanceValue = WukongStyles.THRUST.ordinal();
            } else if (currentSkill.equals(WukongSkills.SMASH_STYLE)) {
                stanceValue = WukongStyles.SMASH.ordinal();
            }else if (currentSkill.equals(WukongSkills.GREATSAGE_STYLE)) {
                stanceValue = WukongStyles.GREATSAGE.ordinal();
            }

        }

        // 将整数值转换为风格
        WukongStyles style = WukongStyles.values()[stanceValue];

        return style;
    }


    public static class Builder extends Skill.Builder<StaffStance> {
        protected WukongStyles style;

        public Builder setStyle(WukongStyles style) {
            this.style = style;
            return this;
        }

        @Override
        public Builder setResource(Resource resource) {
            this.resource = resource;
            return this;
        }

        @Override
        public Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        @Override
        public Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }
    }
}
