package com.p1nero.wukong.epicfight.skill.custom.fashu;


import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.p1nero.wukong.Config;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;

import com.p1nero.wukong.epicfight.skill.custom.wukong.ThrustHeavyAttack;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
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
 * 法术：安身法
 */
public class FashuAnshenfaSkill extends Skill {

    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0142ac114510");

    protected StaticAnimationProvider deriveAnimation1;
    protected StaticAnimationProvider deriveAnimation2;
    public int MAX_TICKS = 15;//15s

    Vec3 playerPos ;

    public static Builder create() {
        return new Builder().setCategory(WukongSkillCategories.FASHU_STYLE).setResource(Resource.NONE);
    }


    public FashuAnshenfaSkill(Builder builder) {
        super(builder);
        deriveAnimation1 = builder.derive;

    }

    /**
     *  {@link FashuAnshenfaSkill#updateContainer(SkillContainer)}
     */
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        //WukongMoveset.LOGGER.info("安身法: {}", "executeOnServer");
        SkillContainer container = executer.getSkill(WukongSkillSlots.FASHU_SKILL_SLOT);
        SkillDataManager dataManager = container.getDataManager();
        ServerPlayer player = executer.getOriginal();

        if (dataManager.getDataValue(WukongSkillDataKeys.ASF_COOLING_ATTACK.get()) && dataManager.getDataValue(WukongSkillDataKeys.ASF_YINGSHEN_ZT.get())) {
            executer.playSound(WuKongSounds.FASHU_ASS.get(), 0.0F, 0.0F);
            executer.playAnimationSynchronized(deriveAnimation1.get(), 0F);
            dataManager.setDataSync(WukongSkillDataKeys.ASF_YINGSHEN_ZT.get(), false, player);
            dataManager.setDataSync(WukongSkillDataKeys.ASF_DERIVE_TIMER.get(), 510, player);
            dataManager.setDataSync(WukongSkillDataKeys.ASF_COOLING_TIMER.get(), 1000, player);
            dataManager.setDataSync(WukongSkillDataKeys.ASF_COOLING_ATTACK.get(), false, player);
            playerPos = player.position();MAX_TICKS = 15;
        } else {
            player.sendSystemMessage(Component.literal("安身术冷却中。"));
        }

        super.executeOnServer(executer, args);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
    }
    @Override
    public void onRemoved(SkillContainer container) {
       /* PlayerPatch<?> executer = container.getExecuter();
        if (executer.getOriginal() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) container.getExecuter().getOriginal();
            SkillDataManager dataManager = container.getDataManager();
            dataManager.setDataSync(WukongSkillDataKeys.ASF_YINGSHEN_ZT.get(), false, player);
            dataManager.setDataSync(WukongSkillDataKeys.ASF_DERIVE_TIMER.get(), 0, player);
            dataManager.setDataSync(WukongSkillDataKeys.ASF_COOLING_TIMER.get(), 0, player);
            dataManager.setDataSync(WukongSkillDataKeys.ASF_COOLING_ATTACK.get(), false, player);
        }*/
        PlayerEventListener listener = container.getExecuter().getEventListener();
        //listener.removeListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID);
        super.onRemoved(container);
    }

    private void createFireCircles(ServerPlayer player,Vec3 position,SkillDataManager dataManager) {
        ServerLevel level = (ServerLevel) player.getCommandSenderWorld();
        double radius = 4.7;
        int particleCount = 360;
        for (int i = 0; i < particleCount; i++) {
            double angle = i * (Math.PI * 2 / particleCount);
            double x = position.x + radius * Math.cos(angle);
            double z = position.z + radius * Math.sin(angle);
            double y = position.y + 0;
            level.sendParticles(ParticleTypes.FLAME, x, y, z, 10, 0, 0, 0, 0);
            if (dataManager.getDataValue(WukongSkillDataKeys.ASF_YINGSHEN_ZT.get())) {
                break;
            }
        }
    }
    public void  createRepelCircle(ServerPlayer player,Vec3 position,SkillDataManager dataManager) {
        double range = 4.7;
        double knockbackStrength = 0.24;
        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(150), entity -> entity != player && entity.isAlive());
        for (LivingEntity entity : nearbyEntities) {
            if (dataManager.getDataValue(WukongSkillDataKeys.ASF_YINGSHEN_ZT.get())) {
                break;
            }
            Vec3 monsterPos = entity.position();
            double deltaX = monsterPos.x - position.x;
            double deltaY = monsterPos.y - position.y;
            double deltaZ = monsterPos.z - position.z;
            if (entity instanceof Monster) {
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                if (distance <= range) {
                    entity.setSecondsOnFire(1);
                    if (Math.abs(deltaZ) > Math.abs(deltaX)) {
                        if (deltaZ > 0) {
                            entity.push(0, 0, knockbackStrength);
                        } else {
                            entity.push(0, 0, -knockbackStrength);
                        }
                    } else {
                        if (deltaX > 0) {
                            entity.push(knockbackStrength, 0, 0);
                        } else {
                            entity.push(-knockbackStrength, 0, 0);
                        }
                    }
                }
            }
        }
    }
    public void Replytohealthvolume(ServerPlayer player,Vec3 position,ServerPlayerPatch serverPlayerPatch){
        double radius = 4.7;
        if (Math.pow(player.getX() - position.x, 2) + Math.pow( player.getZ() - position.z, 2) <= Math.pow(radius, 2)){
            Skill waponSkill = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getSkill();
            waponSkill.setConsumptionSynchronize(serverPlayerPatch,  serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getResource() + Config.CHARGING_SPEED.get().floatValue());

            player.heal(0.2f);}

    }
    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager dataManager = container.getDataManager();
        if (!container.getExecuter().isLogicalClient()) {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();
            if (!dataManager.getDataValue(WukongSkillDataKeys.ASF_YINGSHEN_ZT.get())) {
                dataManager.setDataSync(WukongSkillDataKeys.ASF_DERIVE_TIMER.get(), dataManager.getDataValue(WukongSkillDataKeys.ASF_DERIVE_TIMER.get()) - 1, serverPlayer);
                if (MAX_TICKS == 0) {
                    createFireCircles(serverPlayer, playerPos,dataManager);
                    createRepelCircle(serverPlayer, playerPos,dataManager);
                    Replytohealthvolume(serverPlayer,playerPos,serverPlayerPatch);

                    //serverPlayer.heal(0.2f);
                } else {
                    MAX_TICKS--;
                }
                if (dataManager.getDataValue(WukongSkillDataKeys.ASF_DERIVE_TIMER.get()) == 0) {
                    dataManager.setDataSync(WukongSkillDataKeys.ASF_YINGSHEN_ZT.get(), true, serverPlayer);
                }
            }

            if (!dataManager.getDataValue(WukongSkillDataKeys.ASF_COOLING_ATTACK.get())) {
                dataManager.setDataSync(WukongSkillDataKeys.ASF_COOLING_TIMER.get(),dataManager.getDataValue(WukongSkillDataKeys.ASF_COOLING_TIMER.get()) - 1, serverPlayer);
                if (dataManager.getDataValue(WukongSkillDataKeys.ASF_COOLING_TIMER.get()) == 0) {
                    dataManager.setDataSync(WukongSkillDataKeys.ASF_COOLING_ATTACK.get(), true, serverPlayer);
                }
            }
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
        ResourceLocation styleTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/skills/spell_asf.png");
        if (container.getDataManager().getDataValue(WukongSkillDataKeys.ASF_COOLING_ATTACK.get()) && container.getDataManager().getDataValue(WukongSkillDataKeys.ASF_YINGSHEN_ZT.get())) {
            alpha = 255;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha / 255.0f);
        guiGraphics.blit(styleTexture, pos.x - 32, pos.y -20, 20, 20, 0.0f, 0f, 1, 1, 1, 1);
        if (!container.getDataManager().getDataValue(WukongSkillDataKeys.ASF_COOLING_ATTACK.get()) ) {
            float second = (container.getDataManager().getDataValue(WukongSkillDataKeys.ASF_COOLING_TIMER.get()) / 20.0F);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 255.0f);
            guiGraphics.drawString(
                    gui.font,
                    String.format("%.1f", second),
                    pos.x - 32 + (20 -  gui.font.width(String.format("%.1f", second))) / 2,pos.y - 20 + (20 -  gui.font.lineHeight) / 2,
                    16777215
            );
        }
    }


    public static class Builder extends Skill.Builder<FashuAnshenfaSkill> {
        protected StaticAnimationProvider[] animationProviders;
        protected StaticAnimationProvider derive;
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
        public Builder setDeriveAnimations(StaticAnimationProvider derive) {
            this.derive = derive;
            return this;
        }

    }
}
