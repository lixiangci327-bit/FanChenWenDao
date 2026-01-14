package com.p1nero.wukong.client.keymapping;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.realmsclient.gui.screens.UploadResult;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.skill.custom.wukong.UpdateWeaponInnatePacket;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import com.p1nero.wukong.network.PacketHandler;
import com.p1nero.wukong.network.PacketRelay;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.input.CombatKeyMapping;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import net.minecraftforge.client.event.InputEvent;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.Collection;
import java.util.Set;

import static net.minecraftforge.fml.loading.FMLEnvironment.dist;

@Mod.EventBusSubscriber(value = {Dist.CLIENT},bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongKeyMappings {
    public static final MyKeyMapping W = new MyKeyMapping("key.wukong.w", GLFW.GLFW_KEY_W, "key.wukong.category");
    public static final MyKeyMapping JIAO_ZHEN = new MyKeyMapping("key.wukong.jiao_zhen", GLFW.GLFW_MOUSE_BUTTON_1, "key.wukong.category");

    //身法
    public static final MyKeyMapping MAGICARTS_SHENFA = new MyKeyMapping("key.wukong.magicarts_shenfa", GLFW.GLFW_KEY_UP, "key.wukong.category");
    //法术
    public static final MyKeyMapping MAGICARTS_FASHU = new MyKeyMapping("key.wukong.magicarts_fashu", GLFW.GLFW_KEY_DOWN, "key.wukong.category");
    public static final MyKeyMapping MAGICARTS_HAOMAO = new MyKeyMapping("key.wukong.magicarts_haomao", GLFW.GLFW_KEY_LEFT, "key.wukong.category");


    public static final MyKeyMapping SMASH_STYLE = new MyKeyMapping("key.wukong.smash_stance", GLFW.GLFW_KEY_Z, "key.wukong.category");
    public static final MyKeyMapping PILLAR_STYLE = new MyKeyMapping("key.wukong.pillar_stance", GLFW.GLFW_KEY_X, "key.wukong.category");
    public static final MyKeyMapping THRUST_STYLE = new MyKeyMapping("key.wukong.thrust_stance", GLFW.GLFW_KEY_C, "key.wukong.category");
    public static final KeyMapping STAFF_FLOWER = new CombatKeyMapping("key.wukong.staff_spin", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.wukong.category");

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(JIAO_ZHEN);
        event.register(MAGICARTS_SHENFA);
        event.register(MAGICARTS_FASHU);
        event.register(MAGICARTS_HAOMAO);
        event.register(SMASH_STYLE);
        event.register(PILLAR_STYLE);
        event.register(THRUST_STYLE);
        event.register(STAFF_FLOWER);
    }

    @Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID,value = Dist.CLIENT)
    public static class HandleClientTick{

        /**
         * 按键切换棍势，确保学过才可以用按键切换。
         */
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (localPlayer == null) {
                return;
            }
            if (localPlayer.getHealth() <= 0 || !localPlayer.isAlive()) {
                return;
            }
            LocalPlayerPatch patch = EpicFightCapabilities.getEntityPatch(localPlayer, LocalPlayerPatch.class);
            if (patch.getEntityState().inaction() || patch.getEntityState().attacking()) {
                return;
            }

            localPlayer.getCapability(EpicFightCapabilities.CAPABILITY_SKILL).ifPresent(capabilitySkill -> {
                Collection<Skill> styles = capabilitySkill.getLearnedSkills(WukongSkillCategories.STAFF_STYLE);
                if (styles == null) {
                    return;
                }

                Skill skill = null;
                if (patch.getSkill(SkillSlots.WEAPON_INNATE) != null) {
                    Skill weaponSkill = patch.getSkill(SkillSlots.WEAPON_INNATE).getSkill();
                    if (weaponSkill == WukongSkills.SMASH_HEAVY_ATTACK) {
                        if (patch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager() != null && patch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(WukongSkillDataKeys.IS_CHARGING.get())) {
                            return;
                        }
                    } else if (weaponSkill == WukongSkills.PILLAR_HEAVY_ATTACK) {
                        if (patch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager() != null && patch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(WukongSkillDataKeys.IS_CHARGING.get())) {
                            return;
                        }
                    } else if (weaponSkill == WukongSkills.THRUST_HEAVY_ATTACK) {
                        if (patch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager() != null && patch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(WukongSkillDataKeys.Thrust_IS_CHARGING.get())) {
                            return;
                        }
                    }
                }
                // 按键释放和技能选择
                if (WukongKeyMappings.SMASH_STYLE.isRelease() && styles.contains(WukongSkills.SMASH_STYLE)) {
                    skill = WukongSkills.SMASH_STYLE;
                } else if (WukongKeyMappings.THRUST_STYLE.isRelease() && styles.contains(WukongSkills.THRUST_STYLE)) {
                    skill = WukongSkills.THRUST_STYLE;
                } else if (WukongKeyMappings.PILLAR_STYLE.isRelease() && styles.contains(WukongSkills.PILLAR_STYLE)) {
                    skill = WukongSkills.PILLAR_STYLE;
                } else {
                    return;
                }
                // 设置并同步技能
                SkillContainer skillContainer = patch.getSkill(WukongSkillSlots.STAFF_STYLE);
                if (skillContainer != null) {
                    skillContainer.setSkill(skill);
                    capabilitySkill.addLearnedSkill(skill);
                    Component styleChangeMessage = Component.translatable("tips.wukong.style_change").append(skill.getDisplayName());
                    localPlayer.displayClientMessage(styleChangeMessage, true);
                    EpicFightNetworkManager.sendToServer(new CPChangeSkill(WukongSkillSlots.STAFF_STYLE.universalOrdinal(), -1, skill.toString(), false));
                    PacketRelay.sendToServer(PacketHandler.INSTANCE, new UpdateWeaponInnatePacket());
                }
            });

        }
        @SubscribeEvent
        public static void onMouseInput(InputEvent.MouseButton event) {
            if(Minecraft.getInstance().player != null && Minecraft.getInstance().screen == null){
                if (event.getButton() == WukongKeyMappings.JIAO_ZHEN.getKey().getValue()) {

                    LocalPlayer player = Minecraft.getInstance().player;
                    LocalPlayerPatch patch = EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
                    if (player.isAlive() && patch != null && patch.getSkill(SkillSlots.WEAPON_INNATE) != null) {
                        SkillDataManager manager = patch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        if (manager.hasData(WukongSkillDataKeys.IS_ATTACK_KEY_DOWN.get())) {
                            if (event.getAction() == 1) {
                                manager.setDataSync(WukongSkillDataKeys.IS_ATTACK_KEY_DOWN.get(), true, player);
                            } else if (event.getAction() == 0) {
                                manager.setDataSync(WukongSkillDataKeys.IS_ATTACK_KEY_DOWN.get(), false, player);
                            }

                        }
                    }
                }
            }



        }
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (Minecraft.getInstance().player != null) {
                if (event.getKey() == WukongKeyMappings.MAGICARTS_SHENFA.getKey().getValue()) {
                    ShenfaAttackKeyPressed(event.getAction());
                }
                if (event.getKey() == WukongKeyMappings.MAGICARTS_FASHU.getKey().getValue()) {
                    FashuAttackKeyPressed(event.getAction());
                }
                if (event.getKey() == WukongKeyMappings.MAGICARTS_HAOMAO.getKey().getValue()) {
                    HaoMaoKeyPressed(event.getAction());
                }


            }
        }
        public static void ShenfaAttackKeyPressed(int action) {
            if (action == 1) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null && EpicFightCapabilities.getItemStackCapability(player.getMainHandItem()).getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF)) {
                    LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
                    localPlayerPatch.getSkill(WukongSkillSlots.SHENFA_SKILL_SLOT).sendExecuteRequest(localPlayerPatch, ClientEngine.getInstance().controllEngine);
                }
            }
        }
        public static void FashuAttackKeyPressed(int action) {
            if (action == 1) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null && EpicFightCapabilities.getItemStackCapability(player.getMainHandItem()).getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF)) {
                    LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
                    localPlayerPatch.getSkill(WukongSkillSlots.FASHU_SKILL_SLOT).sendExecuteRequest(localPlayerPatch, ClientEngine.getInstance().controllEngine);
                }
            }
        }
        public static void HaoMaoKeyPressed(int action) {
            if (action == 1) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null && EpicFightCapabilities.getItemStackCapability(player.getMainHandItem()).getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF)) {
                    LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
                    localPlayerPatch.getSkill(WukongSkillSlots.HAO_MAO).sendExecuteRequest(localPlayerPatch, ClientEngine.getInstance().controllEngine);
                }
            }
        }

        public static void heavyAttackKeyPressed(int action) {
            if (action == 1) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null && EpicFightCapabilities.getItemStackCapability(player.getMainHandItem()).getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF)) {
                    LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
                    localPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).sendExecuteRequest(localPlayerPatch, ClientEngine.getInstance().controllEngine);
                }
            }
        }


    }


}
