package com.p1nero.wukong.epicfight.skill.custom.fashu;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.skill.EntitySpeedData;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import com.p1nero.wukong.mixin.BattleModeGuiMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.StaticAnimationProvider;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.config.EpicFightOptions;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.*;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import java.util.List;
import java.util.UUID;

/**
 * 法术：定身术
 */

public class FashuDingshenfaSkill extends Skill {

    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0442ac114510");
    protected StaticAnimationProvider deriveAnimation1;
    public int MAX_CHARGED4_TICKS = 30;//30s
    Vec3 playerPos ;

    public static Builder create() {
        return new Builder().setCategory(WukongSkillCategories.FASHU_STYLE).setResource(Resource.NONE);
    }

    public FashuDingshenfaSkill(Builder builder) {
        super(builder);
        deriveAnimation1 = builder.derive1;

    }

    /**
     *  {@link FashuDingshenfaSkill#updateContainer(SkillContainer)}
     */
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        SkillContainer container = executer.getSkill(WukongSkillSlots.FASHU_SKILL_SLOT);
        SkillDataManager dataManager = container.getDataManager();
        ServerPlayer player = executer.getOriginal();
        if(dataManager.getDataValue(WukongSkillDataKeys.DSF_COOLING_ATTACK.get()) ){
            executer.playSound(WuKongSounds.FASHU_DSS.get(), 0.0F, 0.0F);
            executer.playAnimationSynchronized(deriveAnimation1.get(), 0F);
            playerPos = player.position();

        }else{
            player.sendSystemMessage(Component.literal("定身术冷却中。"));
        }

        super.executeOnServer(executer, args);
    }


    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
//        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.ANIMATION_BEGIN_EVENT, EVENT_UUID, (event -> {
//            if (event.getAnimation().equals(WukongAnimations.FASHU_MAGICARTS_DSF_START)) {
//                if (container.getDataManager().getDataValue(WukongSkillDataKeys.DSF_ENEMY_ATTACK.get())) {
//                    container.getDataManager().setData(WukongSkillDataKeys.DSF_ENEMY_ATTACK.get(), false);
//                    if (container.getExecuter() instanceof ServerPlayerPatch) {
//                        ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch) container.getExecuter();
//                        Dingshenshu_traverse(serverPlayerPatch.getOriginal(),container);;
//                        container.getDataManager().setData(WukongSkillDataKeys.DSF_COOLING_ATTACK.get(), false);
//                    }
//                }
//            }
//        }));

    }

    public void Dingshenshu_txiaox(ServerPlayer player) {
        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(300), entity -> entity != player && entity.isAlive());
        for (LivingEntity entity : nearbyEntities) {
            if (entity.getTags().contains("ding")) {
                if (entity.level() instanceof ServerLevel serverLevel) { // 确保是 ServerLevel
                    serverLevel.sendParticles(ParticleTypes.WAX_OFF, entity.getX(), entity.getY() + 1, entity.getZ(), 10, 0, 0, 0, 10);
                }
            }

        }
    }

    public void Dingshenshu_lift(ServerPlayer player) {
        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(300), entity -> entity != player && entity.isAlive());
        for (LivingEntity entity : nearbyEntities) {
            if (entity.getTags().contains("ding")){
                player.sendSystemMessage(Component.literal("解除定身！"));
                EntitySpeedData.restoreOriginalSpeed(entity);
            }
            if (entity instanceof EnderDragon enderDragon) {        // 处理末影龙
                if (enderDragon.getTags().contains("ding")){
                    enderDragon.removeTag("ding");
                    enderDragon.setNoAi(false);
                    enderDragon.setAggressive(true);
                    // player.sendSystemMessage(Component.literal("解除定身！"));
                }
            } else if (entity instanceof Monster monster) {
                //  WukongMoveset.LOGGER.info("monstery解除定身 {}", monster.getTags().contains("ding"));
                if (monster.getTags().contains("ding")){
                    monster.removeTag("ding");
                    monster.setNoAi(false);  // 恢复怪物的AI
                    monster.setAggressive(true);  // 恢复攻击状态
                    monster.setTarget(player);
                    // player.sendSystemMessage(Component.literal("解除定身！"));
                }
            }

        }


    }




    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        PlayerPatch<?> executer = container.getExecuter();
        /*if (executer.getOriginal() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) container.getExecuter().getOriginal();
            SkillDataManager dataManager = container.getDataManager();
            // 重置定身状态
            dataManager.setDataSync(WukongSkillDataKeys.DSF_YINGSHEN_ZT.get(), false, player);
            // 重置冷却状态
            dataManager.setDataSync(WukongSkillDataKeys.DSF_COOLING_ATTACK.get(), true, player);
            dataManager.setDataSync(WukongSkillDataKeys.DSF_COOLING_TIMER.get(), 0, player);
            dataManager.setDataSync(WukongSkillDataKeys.DSF_DERIVE_TIMER.get(), 0, player);
            // 解除定身
            Dingshenshu_lift(player);
        }*/

        PlayerEventListener listener = container.getExecuter().getEventListener();
        //listener.removeListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID);

    }



    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager dataManager = container.getDataManager();

        if (container.getExecuter().isLogicalClient()) {
            // 客户端执行的逻辑
        } else {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();
            if (dataManager.getDataValue(WukongSkillDataKeys.DSF_YINGSHEN_ZT.get())) {
                dataManager.setDataSync(WukongSkillDataKeys.DSF_DERIVE_TIMER.get(), Math.max(dataManager.getDataValue(WukongSkillDataKeys.DSF_DERIVE_TIMER.get()) - 1, 0), serverPlayer);
                if (dataManager.getDataValue(WukongSkillDataKeys.DSF_DERIVE_TIMER.get()) == 0) {
                    dataManager.setDataSync(WukongSkillDataKeys.DSF_YINGSHEN_ZT.get(), false, serverPlayer);
                    Dingshenshu_lift(serverPlayer);
                }
                Dingshenshu_txiaox(serverPlayer);
            }
            if (!dataManager.getDataValue(WukongSkillDataKeys.DSF_COOLING_ATTACK.get())) {
                dataManager.setDataSync(WukongSkillDataKeys.DSF_COOLING_TIMER.get(), Math.max(dataManager.getDataValue(WukongSkillDataKeys.DSF_COOLING_TIMER.get()) - 1, 0), serverPlayer);
                if (dataManager.getDataValue(WukongSkillDataKeys.DSF_COOLING_TIMER.get()) == 0) {
                    dataManager.setDataSync(WukongSkillDataKeys.DSF_COOLING_ATTACK.get(), true, serverPlayer);
                }
            }
        }
    }
    /**
     * 根据棍式和星级画图
     * 取消原本的绘制在 {@link BattleModeGuiMixin}
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
        EpicFightOptions config = EpicFightMod.CLIENT_CONFIGS;
        Window sr = Minecraft.getInstance().getWindow();
        int width = sr.getGuiScaledWidth();
        int height = sr.getGuiScaledHeight();
        int alpha = 128; // 50% 透明度
        Vec2i pos = config.getWeaponInnatePosition(width, height);
        ResourceLocation styleTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/skills/spell_dsf.png");
        if (container.getDataManager().getDataValue(WukongSkillDataKeys.DSF_COOLING_ATTACK.get())) {
            alpha = 255;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc(); // 使用默认的透明度混合模式
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha / 255.0f);
        guiGraphics.blit(styleTexture, pos.x - 32, pos.y -20, 20, 20, 0.0f, 0f, 1, 1, 1, 1);
        if (!container.getDataManager().getDataValue(WukongSkillDataKeys.DSF_COOLING_ATTACK.get()) ) {
            float second = (container.getDataManager().getDataValue(WukongSkillDataKeys.DSF_COOLING_TIMER.get()) / 20.0F);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 255.0f);
            guiGraphics.drawString(
                    gui.font,
                    String.format("%.1f", second),
                    pos.x - 32 + (20 -  gui.font.width(String.format("%.1f", second))) / 2,pos.y - 20 + (20 -  gui.font.lineHeight) / 2,
                    16777215
            );
        }
    }



    @Override
    public boolean shouldDraw(SkillContainer container) {
        return WukongWeaponCategories.isWeaponValid(container.getExecuter());
    }
    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        return null;
    }
    @Override
    public boolean canExecute(PlayerPatch<?> executer) {
        return super.canExecute(executer);
    }


    public static class Builder extends Skill.Builder<FashuDingshenfaSkill> {
        protected StaticAnimationProvider[] animationProviders;
        protected StaticAnimationProvider derive1;
        protected StaticAnimationProvider derive2;
        public Builder() {
        }

        public Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public Builder setActivateType(ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public Builder setResource(Resource resource) {
            this.resource = resource;
            return this;
        }

        public Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

        public Builder setAnimations(StaticAnimationProvider... animationProviders) {
            this.animationProviders = animationProviders;
            return this;
        }
        public Builder setDeriveAnimations(StaticAnimationProvider derive1) {
            this.derive1 = derive1;
            return this;
        }

    }
}
