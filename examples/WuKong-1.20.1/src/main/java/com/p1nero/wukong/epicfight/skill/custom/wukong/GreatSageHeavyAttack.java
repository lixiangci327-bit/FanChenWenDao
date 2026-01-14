package com.p1nero.wukong.epicfight.skill.custom.wukong;

import com.mojang.blaze3d.platform.Window;
import com.p1nero.wukong.Config;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import com.p1nero.wukong.epicfight.skill.custom.avatar.HeavyAttack;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationProvider;
import yesman.epicfight.api.animation.StaticAnimationProvider;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.config.EpicFightOptions;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.*;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.EpicFightDamageType;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;

import static com.mojang.text2speech.Narrator.LOGGER;
import static yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch.STAMINA;

/**
 * 大圣模型
 */

public class GreatSageHeavyAttack extends WeaponInnateSkill implements HeavyAttack {

    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a02b-0242ac114585");
    @NotNull
    protected final StaticAnimationProvider[] animations;//0~4共有五种重击
    protected final StaticAnimationProvider[] animatione;//0~4共有五种重击
    @NotNull

    @Override
    public  List<StaticAnimationProvider> getHeavyAttacks(){
        List<StaticAnimationProvider> staticAnimations =new java.util.ArrayList<>(List.of(animations));
        return staticAnimations;
    }


    public static Builder createChargedAttack(){
        return new Builder().setCategory(SkillCategories.WEAPON_INNATE).setResource(Resource.NONE);
    }

    public GreatSageHeavyAttack(Builder builder) {
        super(builder);
        this.animations = builder.animationProviders;
        this.animatione = builder.animationProvidere;
    }

    /**
     * 在计时周期内使用技能才算使用衍生，否则视为重击
     * 长按循环第一段衍生的判断在{@link GreatSageHeavyAttack#updateContainer(SkillContainer)}
     */
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        SkillContainer container = executer.getSkill(SkillSlots.WEAPON_INNATE);
        SkillDataManager dataManager = container.getDataManager();
        ServerPlayer player = executer.getOriginal();


        if(dataManager.getDataValue(WukongSkillDataKeys.CAN_SECOND_TIMER.get()) > 0 ) {
            WukongMoveset.LOGGER.info("重击二");
            dataManager.setData(WukongSkillDataKeys.CAN_SECOND_TIMER.get(), 0);
            executer.playAnimationSynchronized(animatione[dataManager.getDataValue(WukongSkillDataKeys.GREATSAGE_NUMBER.get()) ].get(), 0F);
        }else{
            WukongMoveset.LOGGER.info("重击一");
            if(dataManager.getDataValue(WukongSkillDataKeys.CAN_FIRST_TIMER.get()) > 0 ) {
                dataManager.setData(WukongSkillDataKeys.CAN_FIRST_TIMER.get(), 0);
                executer.playAnimationSynchronized(animations[dataManager.getDataValue(WukongSkillDataKeys.GREATSAGE_NUMBER.get()) ].get(), 0F);
                dataManager.setData(WukongSkillDataKeys.CAN_SECOND_TIMER.get(), 20);
            }

        }

        dataManager.setDataSync(WukongSkillDataKeys.GREATSAGE_STARS_CONSUMED.get(), container.getStack(), player);
        super.executeOnServer(executer, args);
    }


    @Override
    public void onInitiate(SkillContainer container) {
        SkillDataManager dataManager = container.getDataManager();
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event -> {
            ServerPlayerPatch serverPlayerPatch = event.getPlayerPatch();
            ServerPlayer player = serverPlayerPatch.getOriginal();
            CapabilityItem capabilityItem = EpicFightCapabilities.getItemStackCapability(player.getMainHandItem());
            if(!WukongWeaponCategories.isWeaponValid(event.getPlayerPatch())){
                return;
            }
            List<AnimationProvider<?>> autoAnimations = capabilityItem.getAutoAttckMotion(event.getPlayerPatch());
            for(int i = 0; i < autoAnimations.size(); i++){
                if(autoAnimations.get(i).get().equals(event.getAnimation())){
                    WukongMoveset.LOGGER.info(String.valueOf(i));
                    container.getDataManager().setDataSync(WukongSkillDataKeys.CAN_FIRST_TIMER.get(), 30, player);
                    container.getDataManager().setDataSync(WukongSkillDataKeys.GREATSAGE_NUMBER.get(), i, player);
                    container.getDataManager().setDataSync(WukongSkillDataKeys.CAN_SECOND_TIMER.get(), 0, player);

                    return;
                }

            }
        }));
        super.onInitiate(container);
    }
    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        PlayerEventListener listener = container.getExecuter().getEventListener();
        listener.removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_ATTACK, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);

    }
    public void createRepelForAttackTarget(ServerPlayer player, Entity target, double knockbackStrength) {
        Vec3 playerPos = player.position();
        if (target instanceof LivingEntity) {
            Vec3 targetPos = target.position();
            double deltaX = targetPos.x - playerPos.x;
            double deltaZ = targetPos.z - playerPos.z;
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            if (distance > 0.1) {
                deltaX /= distance;
                deltaZ /= distance;
                target.push(deltaX * knockbackStrength, 0.0, deltaZ * knockbackStrength);
                target.setSecondsOnFire(10);
            }
        }
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
        if (container.getExecuter().isLogicalClient()) {
            boolean isKeyDown = EpicFightKeyMappings.WEAPON_INNATE_SKILL.isDown();
          //  dataManager.setDataSync(WukongSkillDataKeys.Thrust_KEY_PRESSING.get(), isKeyDown, ((LocalPlayer) container.getExecuter().getOriginal()));
        } else {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();
            if (dataManager.getDataValue(WukongSkillDataKeys.CAN_FIRST_TIMER.get())!=0){
                dataManager.setDataSync(WukongSkillDataKeys.CAN_FIRST_TIMER.get(), dataManager.getDataValue(WukongSkillDataKeys.CAN_FIRST_TIMER.get())-1, serverPlayer);//
            }

            if (container.getStack() < 1 && container.getResource() > container.getMaxResource() * 0.3) {
                breakProgress(serverPlayerPatch, container);
            } else if (container.getStack() < 2 && container.getResource() > container.getMaxResource() * 0.5) {
                breakProgress(serverPlayerPatch, container);
            } else if (container.getStack() < 3 && container.getResource() > container.getMaxResource() * 0.7) {
                breakProgress(serverPlayerPatch, container);
            }
            int current = dataManager.getDataValue(WukongSkillDataKeys.GREATSAGE_CHARGED4_TIMER.get());
            if(current > 0){
                dataManager.setDataSync(WukongSkillDataKeys.GREATSAGE_CHARGED4_TIMER.get(), current - 1, serverPlayer);
            }
            float consumption = Config.CHARGING_SPEED.get().floatValue() / 5;
            if(current == 1 && container.isFull()){
                this.setStackSynchronize(serverPlayerPatch, 3);
                this.setConsumptionSynchronize(serverPlayerPatch, container.getMaxResource() - consumption);
            }
            if(current == 0 && container.getStack() >= 3 && container.getResource() > consumption + 0.1){
                this.setConsumptionSynchronize(serverPlayerPatch, container.getResource() - consumption);
            }
        }
    }

    public void breakProgress(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        this.setConsumptionSynchronize(serverPlayerPatch, 0.1F);
        this.setStackSynchronize(serverPlayerPatch, container.getStack() + 1);
    }

    /**
     * 根据棍式和星级画图
     * 取消原本的绘制在 {@link com.p1nero.wukong.mixin.BattleModeGuiMixin}
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
        int stack = container.getStack();
        int style = container.getExecuter().getHoldingItemCapability(InteractionHand.MAIN_HAND).getStyle(container.getExecuter()).universalOrdinal() - WukongStyles.SMASH.universalOrdinal();
        float cooldownRatio = !container.isFull() && !container.isActivated() ? container.getResource(1.0F) : 1.0F;
        int progress = ((int) Math.ceil(cooldownRatio * 40));
        EpicFightOptions config = EpicFightMod.CLIENT_CONFIGS;
        Window sr = Minecraft.getInstance().getWindow();
        int width = sr.getGuiScaledWidth();
        int height = sr.getGuiScaledHeight();
        Vec2i pos = config.getWeaponInnatePosition(width, height);
        ResourceLocation progressTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/progress/" + progress + ".png");
        ResourceLocation styleTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/stance/greatsage_style.png");
        ResourceLocation stackBgTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/stack/ui" + stack + ".png");
        ResourceLocation stackTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/stack/stack" + stack + ".png");
        ResourceLocation goldenLightTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/light/gold.png");
        ResourceLocation whiteLightTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/light/white.png");
        ResourceLocation redLightTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/light/red.png");
        guiGraphics.blit(progressTexture, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);
        drawTexture(guiGraphics,styleTexture, pos.x - 12, pos.y - 12);
        drawTexture(guiGraphics,stackBgTexture,pos.x - 12, pos.y - 12);
        Vec2i light1 = new Vec2i(pos.x - 14, pos.y + 3);
        Vec2i light2 = new Vec2i(pos.x - 5, pos.y + 1);
        Vec2i light3 = new Vec2i(pos.x + 4, pos.y - 5);
        List<Vec2i> lightList = List.of(light1, light2, light3);

        if (container.isFull()) {
            for (Vec2i lightPos : lightList) {
                drawTexture(guiGraphics,goldenLightTexture, lightPos.x, lightPos.y);
            }
        }
        if (container.getDataManager().getDataValue(WukongSkillDataKeys.RED_TIMER.get()) > 0) {
            int star = Math.min(container.getDataManager().getDataValue(WukongSkillDataKeys.STARS_CONSUMED.get()), 3);
            if (star > 0) {
                for (int i = 0; i < star; i++) {
                    Vec2i lightPos = lightList.get(i);
                    drawTexture(guiGraphics,redLightTexture, lightPos.x, lightPos.y);
                }
            }
        }

        if (stack > 0) {
            for (int i = 0; i < Math.min(stack, 3); i++) {
                Vec2i lightPos = lightList.get(i);
                drawTexture(guiGraphics,whiteLightTexture, lightPos.x, lightPos.y);
            }
            drawTexture(guiGraphics,stackTexture, pos.x - 12, pos.y - 12);
        }


    }
    public void drawTexture(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y) {
        guiGraphics.blit(texture, x, y, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);
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

    public static class Builder extends Skill.Builder<GreatSageHeavyAttack> {
        protected StaticAnimationProvider[] animationProviders;
        protected StaticAnimationProvider[] animationProvidere;
        StaticAnimationProvider pre;

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


        /**
         * 0~4星重击
         */
        public Builder setHeavyAttacks(StaticAnimationProvider... animationProviders) {
            this.animationProviders = animationProviders;
            return this;
        }
        public Builder setHeavyAttacke(StaticAnimationProvider... animationProviders) {
            this.animationProvidere = animationProviders;
            return this;
        }


    }

}
