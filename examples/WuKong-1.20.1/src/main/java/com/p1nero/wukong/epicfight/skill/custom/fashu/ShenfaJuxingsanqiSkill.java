package com.p1nero.wukong.epicfight.skill.custom.fashu;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.entity.CloudStepLeftEntity;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import com.p1nero.wukong.network.PacketHandler;
import com.p1nero.wukong.network.PacketRelay;
import com.p1nero.wukong.network.packet.client.AddEntityAfterImageWithTextureParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
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
 * 法术：聚气化形
 */
public class ShenfaJuxingsanqiSkill extends Skill {

    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0252ac114513");
    public static final int MAX_TIME = 200;//10s
    protected StaticAnimationProvider deriveAnimation1;
    protected StaticAnimationProvider deriveAnimation2;
    protected StaticAnimationProvider deriveAnimation3;
    public static final int MAX_TRANSPARENT_TIMER = 200;
    ItemStack currentWeapon;

    public static Builder create() {
        return new Builder().setCategory(WukongSkillCategories.SHENFA_STYLE).setResource(Resource.NONE);
    }


    public ShenfaJuxingsanqiSkill(Builder builder) {
        super(builder);
        deriveAnimation1 = builder.derive1;
        deriveAnimation2 = builder.derive2;
        deriveAnimation3 = builder.derive3;
    }

    /**
     *  {@link ShenfaJuxingsanqiSkill#updateContainer(SkillContainer)}
     */
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        SkillContainer container = executer.getSkill(WukongSkillSlots.SHENFA_SKILL_SLOT);
        SkillDataManager dataManager = container.getDataManager();
        ServerPlayer player = executer.getOriginal();
        if(dataManager.getDataValue(WukongSkillDataKeys.JXSQ_COOLING_ATTACK.get())){
            dataManager.setDataSync(WukongSkillDataKeys.JXSQ_COOLING_ATTACK.get(),false, player);
            dataManager.setDataSync(WukongSkillDataKeys.JXSQ_YINGSHEN_ZT.get(),false, player);
            dataManager.setDataSync(WukongSkillDataKeys.JXSQ_YINGSHEN_TIMER.get(), MAX_TRANSPARENT_TIMER, player);
            dataManager.setDataSync(WukongSkillDataKeys.JXSQ_COOLING_TIMER.get(),700, player);
            executer.playSound(WuKongSounds.SHENFA_JXSQ_CAST.get(), 0.0F, 0.0F);
            executer.playAnimationSynchronized(deriveAnimation1.get(), 0F);
            PacketRelay.sendToAll(PacketHandler.INSTANCE, new AddEntityAfterImageWithTextureParticle(executer.getOriginal().getId()));
            executer.getOriginal().level().addFreshEntity(new CloudStepLeftEntity(executer));
        }else{
            player.sendSystemMessage(Component.literal("聚形散气冷却中。"));
        }


        super.executeOnServer(executer, args);
    }




    @Override
    public void onInitiate(SkillContainer container) {

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID, (event) -> {
            if(!event.getPlayerPatch().isLogicalClient()){
                PlayerPatch<?> executer = event.getPlayerPatch();
                ServerPlayer player = (ServerPlayer) executer.getOriginal();// 获取玩家的位置
                Vec3 playerPos = player.position();// 设置探测范围，比如一个半径为 10 的球形范围
                double radius = 10.0;
                AABB range = new AABB(playerPos.subtract(radius, radius, radius), playerPos.add(radius, radius, radius));// 获取周围的实体（怪物、动物等）
                List<Entity> nearbyEntities = player.level().getEntitiesOfClass(Entity.class, range, entity -> entity instanceof Monster); // 只获取怪物// 打印找到的怪物数量
              //  WukongMoveset.LOGGER.info("聚气化形: {}", nearbyEntities.size());

                //平A换成破隐
                if(event.getSkillContainer().getSkill().getCategory().equals(SkillCategories.BASIC_ATTACK)){
                    if(container.getDataManager().getDataValue(WukongSkillDataKeys.JXSQ_YINGSHEN_TIMER.get())> 10){
                        event.setCanceled(true);

                        if (nearbyEntities.size() > 0) {
                            event.getPlayerPatch().playAnimationSynchronized(deriveAnimation2.get(), 0.0F);
                        }else{
                            event.getPlayerPatch().playAnimationSynchronized(deriveAnimation3.get(), 0.0F);
                        }
                        container.getDataManager().setDataSync(WukongSkillDataKeys.JXSQ_YINGSHEN_ZT.get(), true, player);
                        container.getDataManager().setDataSync(WukongSkillDataKeys.JXSQ_YINGSHEN_TIMER.get(), 0, ((ServerPlayer) event.getPlayerPatch().getOriginal()));
                    }
                }
            }
            //重置加伤计时器
            if(event.getSkillContainer().getSkill().equals(this) && !event.getPlayerPatch().isLogicalClient()){
                container.getDataManager().setDataSync(WukongSkillDataKeys.JXSQ_YINGSHEN_TIMER.get(), MAX_TIME, ((ServerPlayer) event.getPlayerPatch().getOriginal()));
            }
        });

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_POST, EVENT_UUID, (event) -> {
            if(container.getDataManager().getDataValue(WukongSkillDataKeys.JXSQ_YINGSHEN_TIMER.get()) > 10){
                container.getDataManager().setDataSync(WukongSkillDataKeys.JXSQ_YINGSHEN_TIMER.get(), 0, event.getPlayerPatch().getOriginal());
            }
        });

        //有攻击则重置计时
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
            PlayerPatch<?> executer = event.getPlayerPatch();
            ServerPlayer player = (ServerPlayer) executer.getOriginal();
            if(container.getDataManager().getDataValue(WukongSkillDataKeys.JXSQ_YINGSHEN_TIMER.get()) > 10){
                container.getDataManager().setDataSync(WukongSkillDataKeys.JXSQ_YINGSHEN_TIMER.get(), 0, event.getPlayerPatch().getOriginal());
            }
        });

        super.onInitiate(container);
    }
    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);

        /*PlayerPatch<?> executer = container.getExecuter();
        if (executer.getOriginal() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) executer.getOriginal();
            SkillDataManager dataManager = container.getDataManager();
            // 重置技能状态
            dataManager.setDataSync(WukongSkillDataKeys.JXSQ_COOLING_ATTACK.get(), true, player);
            dataManager.setDataSync(WukongSkillDataKeys.JXSQ_YINGSHEN_ZT.get(), false, player);
            dataManager.setDataSync(WukongSkillDataKeys.JXSQ_YINGSHEN_TIMER.get(), 0, player);
            dataManager.setDataSync(WukongSkillDataKeys.JXSQ_COOLING_TIMER.get(), 0, player);
        }*/

        // 清理所有监听器
        PlayerEventListener listener = container.getExecuter().getEventListener();
        listener.removeListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.HURT_EVENT_POST, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
    }
    public void restoreArmor(ServerPlayer player, ItemStack[] savedArmor) {
        player.setItemInHand(InteractionHand.MAIN_HAND, currentWeapon);
        for (int i = 0; i < 4; i++) {
            player.setItemSlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, i), savedArmor[i]);
        }
        for (int i = 0; i < 4; i++) {
            player.setItemSlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, i), savedArmor[i]);
        }
    }



    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager dataManager = container.getDataManager();
        if(container.getExecuter().isLogicalClient()){

        } else {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();

            if (!dataManager.getDataValue(WukongSkillDataKeys.JXSQ_YINGSHEN_ZT.get())) {
                dataManager.setDataSync(WukongSkillDataKeys.JXSQ_YINGSHEN_TIMER.get(), dataManager.getDataValue(WukongSkillDataKeys.JXSQ_YINGSHEN_TIMER.get()) - 1, serverPlayer);
                if (dataManager.getDataValue(WukongSkillDataKeys.JXSQ_YINGSHEN_TIMER.get()) == 0)
                    dataManager.setDataSync(WukongSkillDataKeys.JXSQ_YINGSHEN_ZT.get(), true, serverPlayer);
            }

            if (!dataManager.getDataValue(WukongSkillDataKeys.JXSQ_COOLING_ATTACK.get())) {
                dataManager.setDataSync(WukongSkillDataKeys.JXSQ_COOLING_TIMER.get(), dataManager.getDataValue(WukongSkillDataKeys.JXSQ_COOLING_TIMER.get()) - 1, serverPlayer);
                if (dataManager.getDataValue(WukongSkillDataKeys.JXSQ_COOLING_TIMER.get()) == 0)
                    dataManager.setDataSync(WukongSkillDataKeys.JXSQ_COOLING_ATTACK.get(), true, serverPlayer);
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
     * 取消原本的绘制在 {@link com.p1nero.wukong.mixin.BattleModeGuiMixin}
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
        ResourceLocation styleTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/skills/spell_jxsq.png");
        if (container.getDataManager().getDataValue(WukongSkillDataKeys.JXSQ_COOLING_ATTACK.get())) {
            alpha = 255;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha / 255.0f);
        guiGraphics.blit(styleTexture, pos.x - 52, pos.y -20, 20, 20, 0.0f, 0f, 1, 1, 1, 1);
        if (!container.getDataManager().getDataValue(WukongSkillDataKeys.JXSQ_COOLING_ATTACK.get()) ) {
            float second = (container.getDataManager().getDataValue(WukongSkillDataKeys.JXSQ_COOLING_TIMER.get()) / 20.0F);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 255.0f);
            guiGraphics.drawString(
                    gui.font,
                    String.format("%.1f", second),
                    pos.x - 52 + (20 -  gui.font.width(String.format("%.1f", second))) / 2,pos.y - 20 + (20 -  gui.font.lineHeight) / 2,
                    16777215
            );
        }
    }


    // 构建器，用于创建技能实例
    public static class Builder extends Skill.Builder<ShenfaJuxingsanqiSkill> {
        protected StaticAnimationProvider[] animationProviders;
        protected StaticAnimationProvider derive1;
        protected StaticAnimationProvider derive2;
        protected StaticAnimationProvider derive3;
        public Builder() {
        }

        public Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public Builder setActivateType(Skill.ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public Builder setResource(Skill.Resource resource) {
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
        public Builder setDeriveAnimations(StaticAnimationProvider derive1, StaticAnimationProvider derive2, StaticAnimationProvider derive3) {
            this.derive1 = derive1;
            this.derive2 = derive2;
            this.derive3 = derive3;
            return this;
        }

    }
}
