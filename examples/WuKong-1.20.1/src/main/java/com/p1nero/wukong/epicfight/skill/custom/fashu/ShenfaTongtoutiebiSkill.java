package com.p1nero.wukong.epicfight.skill.custom.fashu;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import com.p1nero.wukong.epicfight.skill.custom.wukong.StaffStance;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.StaticAnimationProvider;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.config.EpicFightOptions;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.*;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.EpicFightDamageType;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.UUID;

import static yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch.STAMINA;

/**
 * 法术：铜头铁臂
 */
public class ShenfaTongtoutiebiSkill extends Skill {
    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0272ac114513");
    protected StaticAnimationProvider deriveAnimation1;
    protected StaticAnimationProvider deriveAnimation2;

    public static Builder create() {
        return new Builder().setCategory(WukongSkillCategories.SHENFA_STYLE).setResource(Resource.NONE);
    }


    public ShenfaTongtoutiebiSkill(Builder builder) {
        super(builder);
        deriveAnimation1 = builder.derive1;
        deriveAnimation2 = builder.derive2;
    }

    /**
     *  {@link ShenfaTongtoutiebiSkill#updateContainer(SkillContainer)}
     */
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        SkillContainer container = executer.getSkill(WukongSkillSlots.SHENFA_SKILL_SLOT);
        SkillDataManager dataManager = container.getDataManager();
        ServerPlayer player = executer.getOriginal();
        if( dataManager.getDataValue(WukongSkillDataKeys.TTTB_COOLING_ATTACK.get()) ){
            dataManager.setDataSync(WukongSkillDataKeys.TTTB_COOLING_ATTACK.get(),false, player);
            dataManager.setDataSync(WukongSkillDataKeys.TTTB_RESTORE_ZT.get(),true, player);
            dataManager.setDataSync(WukongSkillDataKeys.TTTB_RESTORE_TIMER.get(),18, player);
            dataManager.setDataSync(WukongSkillDataKeys.TTTB_COOLING_TIMER.get(),300, player);
            dataManager.setDataSync(WukongSkillDataKeys.TTTB_INVINCIBLE_TIMER.get(),0, player);
            executer.playAnimationSynchronized(deriveAnimation1.get(), 0F);
        }else{
            player.sendSystemMessage(Component.literal("铜头铁臂冷却中。"));
        }
        super.executeOnServer(executer, args);
    }


    @Override
    public void onInitiate(SkillContainer container) {
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE , EVENT_UUID, (event) -> {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            if (container.getDataManager().getDataValue(WukongSkillDataKeys.TTTB_INVINCIBLE_TIMER.get())>0) {
                event.setResult(AttackResult.ResultType.MISSED);
                event.setAmount(0);
                event.setCanceled(true);
                return;
            }else if (container.getDataManager().getDataValue(WukongSkillDataKeys.TTTB_RESTORE_ZT.get())){
                container.getDataManager().setDataSync(WukongSkillDataKeys.TTTB_RESTORE_ZT.get(), false, event.getPlayerPatch().getOriginal());
                if(container.getDataManager().getDataValue(WukongSkillDataKeys.TTTB_RESTORE_TIMER.get()) > 0 ){
                    container.getDataManager().setDataSync(WukongSkillDataKeys.TTTB_INVINCIBLE_TIMER.get(), 30, event.getPlayerPatch().getOriginal());
                    container.getDataManager().setDataSync(WukongSkillDataKeys.TTTB_RESTORE_TIMER.get(), 0, event.getPlayerPatch().getOriginal());
                    serverPlayerPatch.playSound(WuKongSounds.SHENFA_TTTB.get(), 1, 1);

                    SkillContainer containe = serverPlayerPatch.getSkill(WukongSkillSlots.STAFF_STYLE);
                    if (containe != null && containe.getSkill() instanceof StaffStance style) {
                        modifyStamina(event.getPlayerPatch().getOriginal(), 5.0F);
                        //WukongMoveset.LOGGER.info("重击: {}",  style.getStyle(containe));
                        if (style.getStyle(containe) ==  WukongStyles.SMASH){
                           // serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.THRUST_FASHU_STACK.get(), true, serverPlayerPatch.getOriginal());
                        }else if (style.getStyle(containe) ==  WukongStyles.PILLAR){
                            serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.PILLAR_FASHU_STACK.get(), true, serverPlayerPatch.getOriginal());
                        }else if (style.getStyle(containe) ==  WukongStyles.THRUST){
                            serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.THRUST_FASHU_STACK.get(), true, serverPlayerPatch.getOriginal());
                        }
                    }

                  //  serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(WukongSkillDataKeys.PILLAR_FASHU_STACK.get(), true, serverPlayerPatch.getOriginal());
                    event.setResult(AttackResult.ResultType.MISSED);
                    event.setAmount(0);
                    event.setCanceled(true);
                    return;
                }else{
                     container.getDataManager().setDataSync(WukongSkillDataKeys.TTTB_RESTORE_TIMER.get(), 0, event.getPlayerPatch().getOriginal());
                     event.getPlayerPatch().playAnimationSynchronized(deriveAnimation2.get(), 0.0F);
                }
            }
        });



        super.onInitiate(container);
    }
    @Override
    public void onRemoved(SkillContainer container) {

       /* PlayerPatch<?> executer = container.getExecuter();
        if (executer.getOriginal() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) executer.getOriginal();
            SkillDataManager dataManager = container.getDataManager();
            // 重置技能状态数据
            dataManager.setDataSync(WukongSkillDataKeys.TTTB_COOLING_ATTACK.get(), true, player);
            dataManager.setDataSync(WukongSkillDataKeys.TTTB_RESTORE_ZT.get(), false, player);
            dataManager.setDataSync(WukongSkillDataKeys.TTTB_RESTORE_TIMER.get(), 0, player);
            dataManager.setDataSync(WukongSkillDataKeys.TTTB_COOLING_TIMER.get(), 0, player);
            dataManager.setDataSync(WukongSkillDataKeys.TTTB_INVINCIBLE_TIMER.get(), 0, player);
        }*/

        PlayerEventListener listener = container.getExecuter().getEventListener();
        // 移除事件监听器
        listener.removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);

        super.onRemoved(container);

    }

    public void modifyStamina(LivingEntity livingentity, float staminaChange) {
        float currentStamina = livingentity.getEntityData().get(STAMINA);
        float newStamina = Math.max(0.0F, currentStamina + staminaChange);
        livingentity.getEntityData().set(STAMINA, newStamina);
    }


    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager dataManager = container.getDataManager();
        if(container.getExecuter().isLogicalClient()){

        } else {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();

            if(dataManager.getDataValue(WukongSkillDataKeys.TTTB_INVINCIBLE_TIMER.get()) != 0){
                dataManager.setDataSync(WukongSkillDataKeys.TTTB_INVINCIBLE_TIMER.get(), dataManager.getDataValue(WukongSkillDataKeys.TTTB_INVINCIBLE_TIMER.get()) - 1, serverPlayer);
            }
            if(dataManager.getDataValue(WukongSkillDataKeys.TTTB_RESTORE_ZT.get()) ) {
                dataManager.setDataSync(WukongSkillDataKeys.TTTB_RESTORE_TIMER.get(), dataManager.getDataValue(WukongSkillDataKeys.TTTB_RESTORE_TIMER.get()) - 1, serverPlayer);
                if(dataManager.getDataValue(WukongSkillDataKeys.TTTB_RESTORE_TIMER.get()) == 0){
                    container.getDataManager().setDataSync(WukongSkillDataKeys.TTTB_RESTORE_ZT.get(), false,serverPlayer);
                }
            }
            if(!dataManager.getDataValue(WukongSkillDataKeys.TTTB_COOLING_ATTACK.get())){
                dataManager.setDataSync(WukongSkillDataKeys.TTTB_COOLING_TIMER.get(), Math.max(dataManager.getDataValue(WukongSkillDataKeys.TTTB_COOLING_TIMER.get()) - 1, 0), serverPlayer);
                if (dataManager.getDataValue(WukongSkillDataKeys.TTTB_COOLING_TIMER.get()) == 0)
                    dataManager.setDataSync(WukongSkillDataKeys.TTTB_COOLING_ATTACK.get(), true, serverPlayer);
            }



        }
    }
    /**
     * 根据棍式和星级画图
     * 取消原本的绘制在 {@link com.p1nero.wukong.mixin.BattleModeGuiMixin}
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
        EpicFightOptions config = EpicFightMod.CLIENT_CONFIGS;
        Window sr = Minecraft.getInstance().getWindow();
        int width = sr.getGuiScaledWidth();
        int height = sr.getGuiScaledHeight();
        int alpha = 128;
        Vec2i pos = config.getWeaponInnatePosition(width, height);
        ResourceLocation styleTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/skills/spell_tttb.png");
        if (container.getDataManager().getDataValue(WukongSkillDataKeys.TTTB_COOLING_ATTACK.get())) {
            alpha = 255;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc(); // 使用默认的透明度混合模式
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha / 255.0f);
        guiGraphics.blit(styleTexture, pos.x - 52, pos.y -20, 20, 20, 0.0f, 0f, 1, 1, 1, 1);
        if (!container.getDataManager().getDataValue(WukongSkillDataKeys.TTTB_COOLING_ATTACK.get()) ) {
            float second = (container.getDataManager().getDataValue(WukongSkillDataKeys.TTTB_COOLING_TIMER.get()) / 20.0F);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 255.0f);
            guiGraphics.drawString(
                    gui.font,
                    String.format("%.1f", second),
                    pos.x - 52 + (20 -  gui.font.width(String.format("%.1f", second))) / 2,pos.y - 20 + (20 -  gui.font.lineHeight) / 2,
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

    // 构建器，用于创建技能实例
    public static class Builder extends Skill.Builder<ShenfaTongtoutiebiSkill> {
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
        public Builder setDeriveAnimations(StaticAnimationProvider derive1, StaticAnimationProvider derive2) {
            this.derive1 = derive1;
            this.derive2 = derive2;
            return this;
        }

    }
}
