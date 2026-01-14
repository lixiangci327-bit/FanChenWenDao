package com.p1nero.wukong.epicfight.skill.custom.wukong;


import com.mojang.blaze3d.platform.Window;
import com.p1nero.wukong.Config;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import com.p1nero.wukong.epicfight.skill.custom.avatar.HeavyAttack;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationProvider;
import yesman.epicfight.api.animation.StaticAnimationProvider;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.events.engine.ControllEngine;
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
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *立棍重击
 */
public class PillarHeavyAttack extends WeaponInnateSkill implements HeavyAttack {

    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac114513");
     public static final int MAX_ANGLE_FOV = 74;
    public static final int MAX_FOVLJ = 0;
     protected final StaticAnimationProvider[] start;//立起来
     protected final StaticAnimationProvider[] up;//增高，0就是0to1，1就是1to2
     protected final StaticAnimationProvider[] heavy;

    protected StaticAnimationProvider deriveAnimation1;
    protected StaticAnimationProvider deriveAnimation2;
    protected StaticAnimationProvider hotwheel;
    protected StaticAnimationProvider deriveEnd;

    @Override
    public  List<StaticAnimationProvider> getHeavyAttacks(){
        List<StaticAnimationProvider> staticAnimations =new java.util.ArrayList<>(List.of(heavy));
        staticAnimations.add(deriveAnimation2);
        return staticAnimations;
    }

    public static Builder createChargedAttack(){
        return new Builder().setCategory(SkillCategories.WEAPON_INNATE).setResource(Resource.NONE);

    }

    public PillarHeavyAttack(Builder builder) {
        super(builder);
        this.start = builder.start;
        this.up = builder.up;
        this.heavy = builder.heavy;

        deriveAnimation1 = builder.derive1;
        deriveAnimation2 = builder.derive2;
        hotwheel = builder.hotwheel;
        deriveEnd= builder.deriveEnd;

    }


    /**
     * 在计时周期内使用技能才算使用衍生，否则视为重击
     * 长按循环第一段衍生的判断在{@link PillarHeavyAttack#updateContainer(SkillContainer)}
     */
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {

        SkillContainer container = executer.getSkill(SkillSlots.WEAPON_INNATE);
        SkillDataManager dataManager = container.getDataManager();
        ServerPlayer player = executer.getOriginal();
        dataManager.setDataSync(WukongSkillDataKeys.STARS_CONSUMED.get(), container.getStack(), player);//0星也是星！
        boolean stackConsumed = container.getStack() > 0;
        if ( container.getStack()==4){
            dataManager.setData(WukongSkillDataKeys.PROTECT_NEXT_FALL.get(), true);
            if (container.getStack()>0){
                CameraOperationFov(7F, 31,container.getStack());
            }
            executer.playAnimationSynchronized(start[container.getStack()].get(), 0F);
        }else if(dataManager.getDataValue(WukongSkillDataKeys.PILLAR_FASHU_TIMER.get()) > 0 ) {
            executer.playAnimationSynchronized(start[container.getStack()].get(), 0F);
            dataManager.setData(WukongSkillDataKeys.PILLAR_FASHU_TIMER.get(), 0);
            this.setStackSynchronize(executer, container.getStack() - 1);
        }else if(dataManager.getDataValue(WukongSkillDataKeys.DERIVE_TIMER.get()) > 0 && stackConsumed ){
            dataManager.setData(WukongSkillDataKeys.PILLAR_FENG_YU_ZHUAN.get(), true);
            executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);
            dataManager.setData(WukongSkillDataKeys.PROTECT_NEXT_FALL.get(), true);
            executer.playAnimationSynchronized(deriveAnimation1.get(), 0F);
            dataManager.setData(WukongSkillDataKeys.DERIVE_TIMER.get(), 0);
            this.setStackSynchronize(executer, container.getStack() - 1);
        }else if(dataManager.getDataValue(WukongSkillDataKeys.PILLAR_JIANGHAIFAN_TIMER.get()) > 0  && stackConsumed ){
            dataManager.setDataSync(WukongSkillDataKeys.PILLAR_JIANGHAIFAN_TIMER.get(), 0, player);
            executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);
            this.setStackSynchronize(executer, container.getStack() - 1);
            executer.playAnimationSynchronized(deriveAnimation2.get(), 0F);
        }else {
            //重击开始蓄力
            if(!dataManager.getDataValue(WukongSkillDataKeys.IS_CHARGING.get()) && checkSpace(player, container.getStack() * 2)){
                dataManager.setData(WukongSkillDataKeys.PROTECT_NEXT_FALL.get(), true);
                if (container.getStack()>0){
                    CameraOperationFov(7F, 31,container.getStack());
                }
                executer.playAnimationSynchronized(start[container.getStack()].get(), 0F);
            }
        }
        super.executeOnServer(executer, args);

    }
    public void CameraOperationFov(float increaseAmount, int durationTicks, int repeatTimes) {
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

    public void CameraResetFov(int durationTicks) {
        Minecraft MC = Minecraft.getInstance();
        float currentFov = MC.options.fov().get();
        float targetFov = 70F;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        AtomicInteger tickCount = new AtomicInteger(0);

        Runnable task = () -> {
            int currentTick = tickCount.incrementAndGet();
            if (currentTick > durationTicks) {
                scheduler.shutdown();
                MC.options.fov().set((int) targetFov);
                return;
            }
            float progress = (float) currentTick / durationTicks;
            float newFov = currentFov + (targetFov - currentFov) * progress;
            MC.options.fov().set((int) newFov);
        };
        scheduler.scheduleAtFixedRate(task, 0, 10, TimeUnit.MILLISECONDS);
    }


    private void resetConsumption(SkillContainer container, ServerPlayerPatch executer){
        if(container.getStack() > 0){
            int cnt = container.getStack();

        } else {
            //  container.getDataManager().setDataSync(MoreBattlesSkillDataKeys.PLAY_SOUND.get(), true, executer.getOriginal());
        }
        //  container.getDataManager().setDataSync(MoreBattlesSkillDataKeys.RED_TIMER.get(), Config.DERIVE_CHECK_TIME.get().intValue(), executer.getOriginal());//通知客户端该亮红灯了
        this.setStackSynchronize(executer, 0);
        this.setConsumptionSynchronize(executer, 1);
    }
    @Override
    public void onInitiate(SkillContainer container) {
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_ATTACK, EVENT_UUID, (event -> {
            if(event.getPlayerPatch().getAnimator().getPlayerFor(null).getAnimation().equals(WukongAnimations.PILLAR_HEAVY_FENGYUNZHUAN.get())){
                if (container.getStack()<4){
                    container.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), container.getResource() + Config.CHARGING_SPEED.get().floatValue());
                }
            }
        }));

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event -> {
            //长按期间禁止跳跃
            if (event.getPlayerPatch().isBattleMode() && EpicFightKeyMappings.WEAPON_INNATE_SKILL.isDown()) {
                Input input = event.getMovementInput();
                input.jumping = false;
            }
            //蓄力期间禁用移动
            if (event.getPlayerPatch().isBattleMode() && EpicFightKeyMappings.WEAPON_INNATE_SKILL.isDown()) {
                Input input = event.getMovementInput();
                input.forwardImpulse = 0.0F;
                input.leftImpulse = 0.0F;
                input.down = false;
                input.up = false;
                input.left = false;
                input.right = false;
                input.jumping = false;
                input.shiftKeyDown = false;
                LocalPlayer clientPlayer = event.getPlayerPatch().getOriginal();
                clientPlayer.setSprinting(false);
                clientPlayer.sprintTriggerTime = -1;
                Minecraft mc = Minecraft.getInstance();
                ControllEngine.setKeyBind(mc.options.keySprint, false);
            }
        }));
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event -> {
            if (event.getDamageSource().is(DamageTypes.FALL) && container.getDataManager().getDataValue(WukongSkillDataKeys.PROTECT_NEXT_FALL.get())) {
                event.setAmount(0);
                event.setCanceled(true);
                event.setResult(AttackResult.ResultType.MISSED);
                event.getPlayerPatch().getOriginal().resetFallDistance();
                container.getDataManager().setData(WukongSkillDataKeys.THRUST_PROTECT_NEXT_FALL.get(), false);
            }
            //霸体减伤
            if(event.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource && epicFightDamageSource.is(EpicFightDamageType.PARTIAL_DAMAGE))
                return;
            float damageReduce = container.getDataManager().getDataValue(WukongSkillDataKeys.DAMAGE_REDUCE.get());
            if(damageReduce > 0){
                if(event.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource){
                    epicFightDamageSource.setStunType(StunType.NONE);
                }
                event.setAmount(event.getAmount() * (1 - damageReduce));
                LivingEntityPatch<?> attackerPatch = EpicFightCapabilities.getEntityPatch(event.getDamageSource().getEntity(), LivingEntityPatch.class);
                this.processDamage(event.getPlayerPatch(), event.getDamageSource(), AttackResult.ResultType.SUCCESS,(1 - damageReduce) * event.getAmount(), attackerPatch);
                event.setResult(AttackResult.ResultType.MISSED);
                event.setCanceled(true);
            }
            //防止坠机

            if (event.getDamageSource().is(DamageTypes.FALL) && container.getDataManager().getDataValue(WukongSkillDataKeys.PROTECT_NEXT_FALL.get())) {
                LivingEntityPatch<?> attackerPatch = EpicFightCapabilities.getEntityPatch(event.getDamageSource().getEntity(), LivingEntityPatch.class);
                this.processDamage(event.getPlayerPatch(), event.getDamageSource(), AttackResult.ResultType.SUCCESS, event.getAmount() * 0.4F, attackerPatch);
                event.setResult(AttackResult.ResultType.BLOCKED);
                event.setCanceled(true);
                container.getDataManager().setData(WukongSkillDataKeys.PROTECT_NEXT_FALL.get(), false);
            }
            event.getPlayerPatch().getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
                if (wkPlayer.getDamageReduce() > 0) {
                    if (event.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource) {
                        epicFightDamageSource.setStunType(StunType.NONE);
                    }
                    LivingEntityPatch<?> attackerPatch = EpicFightCapabilities.getEntityPatch(event.getDamageSource().getEntity(), LivingEntityPatch.class);
                    this.processDamage(event.getPlayerPatch(), event.getDamageSource(), AttackResult.ResultType.SUCCESS, event.getAmount() * (1 - wkPlayer.getDamageReduce()), attackerPatch);
                    event.setResult(AttackResult.ResultType.BLOCKED);
                    event.setCanceled(true);
                }
            });
        }));
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event -> {
                    ServerPlayerPatch serverPlayerPatch = event.getPlayerPatch();
                    ServerPlayer player = serverPlayerPatch.getOriginal();
                    CapabilityItem capabilityItem = EpicFightCapabilities.getItemStackCapability(player.getMainHandItem());
                    if(!WukongWeaponCategories.isWeaponValid(event.getPlayerPatch())){
                        return;
                    }
                    //普攻后立即右键可以衍生
                    List<AnimationProvider<?>> autoAnimations = capabilityItem.getAutoAttckMotion(event.getPlayerPatch());
                    for(int i = 0; i < autoAnimations.size(); i++){
                        if(autoAnimations.get(i).get().equals(event.getAnimation()) && i < 4){
                            container.getDataManager().setDataSync(WukongSkillDataKeys.DERIVE_TIMER.get(), Config.DERIVE_CHECK_TIME.get().intValue(), player);
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
        listener.removeListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_ATTACK, EVENT_UUID);

    }
    /**
     * 判断空间是否足够立
     * @param height 以玩家脚底开始往上需要几格
     */
    public static boolean checkSpace(ServerPlayer serverPlayer, int height) {
        // 获取玩家所在的服务器世界
        ServerLevel serverLevel = serverPlayer.serverLevel();
        // 循环检查玩家头顶 `height` 高度内的每个位置
        for (int i = 1; i <= height; i++) {
            // 检查玩家当前所在位置上方 `i` 个单位的方块状态
            if (!serverLevel.getBlockState(serverPlayer.getOnPos().above(i)).is(Blocks.AIR)) {
                // 如果不是空气，返回 false
                return false;
            }
        }

        // 如果检查完所有高度后都为空气，则返回 true
        return true;
    }


    /**
     * copy from {@link yesman.epicfight.events.EntityEvents#attackEvent(LivingAttackEvent)}
     */
    public void processDamage(PlayerPatch<?> playerPatch, DamageSource damageSource, AttackResult.ResultType attackResult, float amount, @Nullable LivingEntityPatch<?> attackerPatch){
        AttackResult result = playerPatch != null ? AttackResult.of(attackResult, amount) : AttackResult.success(amount);
        if (attackerPatch != null) {
            attackerPatch.setLastAttackResult(result);
        }
        EpicFightDamageSource deflictedDamage = (damageSource instanceof EpicFightDamageSource epicFightDamageSource)? epicFightDamageSource : EpicFightDamageSources.copy(damageSource);
        deflictedDamage.addRuntimeTag(EpicFightDamageType.PARTIAL_DAMAGE);
        if(playerPatch != null){
            playerPatch.getOriginal().hurt(deflictedDamage, result.damage);
        }
    }
    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager dataManager = container.getDataManager();
        if(container.getExecuter().isLogicalClient()){
            //KEY_PRESSING用于服务端判断是否继续播动画
            boolean isKeyDown = EpicFightKeyMappings.WEAPON_INNATE_SKILL.isDown();
            dataManager.setDataSync(WukongSkillDataKeys.KEY_PRESSING.get(), isKeyDown, ((LocalPlayer) container.getExecuter().getOriginal()));
        } else {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();
            //层数变化检测以播音效
            if(container.getStack() > dataManager.getDataValue(WukongSkillDataKeys.LAST_STACK.get())){
                serverPlayerPatch.playSound(WuKongSounds.XULI_LEVEL.get(container.getStack() - 1).get(), 1, 1);
                dataManager.setDataSync(WukongSkillDataKeys.PLAY_SOUND.get(), false, serverPlayer);
                if(dataManager.getDataValue(WukongSkillDataKeys.IS_CHARGING.get())){
                    if (!dataManager.getDataValue(WukongSkillDataKeys.PILLAR_FENG_YU_ZHUAN.get())&& dataManager.getDataValue(WukongSkillDataKeys.LAST_STACK.get())<3){
                        serverPlayerPatch.playAnimationSynchronized(up[dataManager.getDataValue(WukongSkillDataKeys.LAST_STACK.get())].get(), 0.1F);
                    }
                }
            }
            dataManager.setData(WukongSkillDataKeys.LAST_STACK.get(), container.getStack());
            //更新计时器
            dataManager.setDataSync(WukongSkillDataKeys.PILLAR_FASHU_TIMER.get(), Math.max(dataManager.getDataValue(WukongSkillDataKeys.PILLAR_FASHU_TIMER.get()) - 1, 0), serverPlayer);//派生重击
            dataManager.setDataSync(WukongSkillDataKeys.RED_TIMER.get(), Math.max(dataManager.getDataValue(WukongSkillDataKeys.RED_TIMER.get()) - 1, 0), serverPlayer);//使用技能星数显示

            if(dataManager.getDataValue(WukongSkillDataKeys.PILLAR_FASHU_STACK.get()) ){
                if(container.getStack() < 3){
                    this.setStackSynchronize(serverPlayerPatch, container.getStack() + 2);
                    serverPlayerPatch.playSound(WuKongSounds.XULI_LEVEL.get(container.getStack() - 1).get(), 1, 1);
                    dataManager.setData(WukongSkillDataKeys.LAST_STACK.get(), container.getStack());
                }
                dataManager.setDataSync(WukongSkillDataKeys.PILLAR_FASHU_TIMER.get(), 18, serverPlayer);
                dataManager.setDataSync(WukongSkillDataKeys.PILLAR_FASHU_STACK.get(), false, serverPlayer);
            }

            dataManager.setDataSync(WukongSkillDataKeys.RED_TIMER.get(), Math.max(dataManager.getDataValue(WukongSkillDataKeys.RED_TIMER.get()) - 1, 0), serverPlayer);//使用技能星数显示
            if(dataManager.getDataValue(WukongSkillDataKeys.DERIVE_TIMER.get()) > 0){
                dataManager.setDataSync(WukongSkillDataKeys.DERIVE_TIMER.get(), dataManager.getDataValue(WukongSkillDataKeys.DERIVE_TIMER.get()) - 1, serverPlayer);//切手技有效时间计算
            }
            if(dataManager.getDataValue(WukongSkillDataKeys.PILLAR_JIANGHAIFAN_TIMER.get()) > 0){
                dataManager.setDataSync(WukongSkillDataKeys.PILLAR_JIANGHAIFAN_TIMER.get(), dataManager.getDataValue(WukongSkillDataKeys.PILLAR_JIANGHAIFAN_TIMER.get()) - 1, serverPlayer);//切手技有效时间计算
            }

            if(dataManager.getDataValue(WukongSkillDataKeys.IS_CHARGING.get())){
                //防止切物品产生的bug
                if(!WukongWeaponCategories.isWeaponValid(serverPlayerPatch)){
                    dataManager.setDataSync(WukongSkillDataKeys.IS_CHARGING.get(), false, serverPlayer);
                    this.setConsumptionSynchronize(serverPlayerPatch, 1);
                    this.setStackSynchronize(serverPlayerPatch, 0);
                    return;
                }
                //蓄力的加条
                if(container.getStack() < 3&&dataManager.getDataValue(WukongSkillDataKeys.IS_CHARGING.get())){
                    this.setConsumptionSynchronize(serverPlayerPatch, container.getResource() + Config.CHARGING_SPEED.get().floatValue());
                }
                WukongMoveset.LOGGER.info("PILLAR_KEY_PRESSING: {}",dataManager.getDataValue(WukongSkillDataKeys.KEY_PRESSING.get())) ;
                if(!dataManager.getDataValue(WukongSkillDataKeys.KEY_PRESSING.get())){

                    dataManager.setDataSync(WukongSkillDataKeys.IS_CHARGING.get(), false, serverPlayer);
                    dataManager.setData(WukongSkillDataKeys.PROTECT_NEXT_FALL.get(), true);//MAN
                    serverPlayerPatch.playSound(WuKongSounds.XULI_ATTACK_4.get(), 2, 2);

                    serverPlayerPatch.playAnimationSynchronized(heavy[container.getStack()].get(), 0.0F);//有几星就几星重击
                    dataManager.setDataSync(WukongSkillDataKeys.STARS_CONSUMED.get(), container.getStack(), serverPlayer);//设置消耗星数，方便客户端绘制
                    resetConsumption(container, serverPlayerPatch);
                }

            }

            // WukongMoveset.LOGGER.info("立棍PILLAR_JIANGHAIFAN_TIMER: {}",dataManager.getDataValue(WukongSkillDataKeys.JIANGHAIFAN_TIMER.get())) ;

            if(dataManager.getDataValue(WukongSkillDataKeys.PILLAR_FENG_YU_ZHUAN.get())){
                if(!dataManager.getDataValue(WukongSkillDataKeys.KEY_PRESSING.get())){
                    dataManager.setDataSync(WukongSkillDataKeys.PILLAR_FENG_YU_ZHUAN.get(), false, serverPlayer);
                    serverPlayerPatch.playAnimationSynchronized(deriveEnd.get(), 0.0F);
                }
            }

            //破条则加stack清空蓄力条
            if (container.getStack() < 1 && container.getResource() > container.getMaxResource() * 0.3) {
                breakProgress(serverPlayerPatch, container);
            } else if (container.getStack() < 2 && container.getResource() > container.getMaxResource() * 0.5) {
                breakProgress(serverPlayerPatch, container);
            } else if (container.getStack() < 3 && container.getResource() > container.getMaxResource() * 0.7) {
                breakProgress(serverPlayerPatch, container);
            }
            //四蓄的掉棍势时间判断
            int current = dataManager.getDataValue(WukongSkillDataKeys.CHARGED4_TIMER.get());
            if(current > 0){
                dataManager.setDataSync(WukongSkillDataKeys.CHARGED4_TIMER.get(), current - 1, serverPlayer);
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
    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldDraw(SkillContainer container) {
        return WukongWeaponCategories.isWeaponValid(container.getExecuter());
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
        ResourceLocation styleTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/stance/" + style + (stack == 4?"_1":"_0") + ".png");
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
    public WeaponInnateSkill registerPropertiesToAnimation() {
        return null;
    }

    public static class Builder extends Skill.Builder<PillarHeavyAttack> {
        protected StaticAnimationProvider[] start;
        protected StaticAnimationProvider[] up;
        protected StaticAnimationProvider[] heavy;
        protected StaticAnimationProvider derive1;
        protected StaticAnimationProvider derive2;
        protected StaticAnimationProvider deriveLoop;
        protected StaticAnimationProvider deriveEnd;
        protected StaticAnimationProvider hotwheel;

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

        public Builder setChargePreAnimation(StaticAnimationProvider pre) {
            this.pre = pre;
            return this;
        }



        /**
         * 立棍蓄力前摇
         */
        public Builder setStartAnimations(StaticAnimationProvider... animationProviders) {
            this.start = animationProviders;
            return this;
        }
        /**
         * 立棍蓄力0到1豆蹬腿
         */
        public Builder setUpAnimations(StaticAnimationProvider... animationProviders) {
            this.up = animationProviders;
            return this;
        }
        /**
         * 0~4星重击
         */
        public Builder setHeavyAttacks(StaticAnimationProvider... animationProviders) {
            this.heavy = animationProviders;
            return this;
        }
        public Builder setDeriveAnimations(StaticAnimationProvider derivePre, StaticAnimationProvider deriveLoop, StaticAnimationProvider deriveEnd, StaticAnimationProvider derive2) {
            this.derive1 = derivePre;
            this.deriveLoop = deriveLoop;
            this.deriveEnd = deriveEnd;
            this.derive2 = derive2;
            return this;
        }


    }

}
