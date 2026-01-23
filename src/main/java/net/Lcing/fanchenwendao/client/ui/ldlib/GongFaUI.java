package net.Lcing.fanchenwendao.client.ui.ldlib;

import com.lowdragmc.lowdraglib2.editor.resource.FilePath;
import com.lowdragmc.lowdraglib2.editor.resource.UIResource;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UITemplate;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import net.Lcing.fanchenwendao.gongfa.GongFaDefine;
import net.Lcing.fanchenwendao.gongfa.GongFaManager;
import net.Lcing.fanchenwendao.jingjie.JingJieData;
import net.Lcing.fanchenwendao.jingjie.JingJieHelper;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.appliedenergistics.yoga.YogaDisplay;

import java.util.Optional;

public class GongFaUI {

    public static ModularUI buildUI(Player player, ResourceLocation gongfaID) {

        //获取模板
        ResourceLocation uiLocation = ResourceLocation.parse("fanchenwendao:ui/gongfa01.ui.nbt");

        UI ui = Optional.ofNullable(UIResource.INSTANCE.getResourceInstance()
                .getResource(new FilePath(uiLocation)))
                .map(UITemplate::createUI)
                .orElseGet(UI::empty);

        //获取功法数据
        Optional<GongFaDefine> defineOpt = GongFaManager.getGongFa(gongfaID);
        if (defineOpt.isEmpty()) return new ModularUI(UI.empty());  //空指针检测
        GongFaDefine define = defineOpt.get();

        //第一页数据
        //名称
        ui.selectRegex("name").findFirst().ifPresent(element -> {
            if (element instanceof Label label) {
                label.setText(define.getName());
            }
        });

        //品阶
        ui.selectRegex("level").findFirst().ifPresent(element -> {
            if (element instanceof Label label) {
                label.setText("功法品阶：" + define.getLevel());
            }
        });

        //功法属性
        ui.selectRegex("attribute").findFirst().ifPresent(element -> {
            if (element instanceof Label label) {
                label.setText("功法属性：" + define.getAttribute());
            }
        });

        //功法描述
        ui.selectRegex("description").findFirst().ifPresent(element -> {
            if (element instanceof Label label) {
                label.setText(define.getDescription());
            }
        });

        //功法需求
        ui.selectRegex("demand").findFirst().ifPresent(element -> {
            if (element instanceof Label label) {
                String reqText = String.format("修炼需求:\n经验消耗: %d\n环境灵气: %.1f",
                        define.getComprehension().getRequirements().getXpConsume(),
                        define.getComprehension().getRequirements().getMinLingQi());
                label.setText(reqText);
            }
        });



        //第二页数据
        //基础修炼速度
        ui.selectRegex("basespeed").findFirst().ifPresent(element -> {
            if (element instanceof Label label) {
                label.setText("功法基础修炼速度:" + define.getXiulian().getBaseSpeed());
            }
        });

        //修为转化率
        ui.selectRegex("efficiency").findFirst().ifPresent(element -> {
            if (element instanceof Label label) {
                label.setText(String.format("功法修为转化率：%.0f%%", define.getXiulian().getEfficiency() * 100));
            }
        });

        //设为主修按钮
        ui.selectRegex("setmain").findFirst().ifPresent(element -> {
            if (element instanceof Button button) {
                //获取修仙数据
                JingJieData data = player.getData(ModAttachments.JINGJIE_DATA);
                ResourceLocation currentMain = data.getMainGongFaID();  //当前主修

                boolean isMain = currentMain != null && currentMain.equals(gongfaID);

                if (isMain) {

                    button.setDisplay(YogaDisplay.NONE);

                } else {
                    button.setText("设为主修功法");
                    //LDlib提供数据同步
                    button.setOnServerClick((event) -> {
                        // Debug: 打印触发信息
                        net.Lcing.fanchenwendao.FanChenWenDao.LOGGER.info("UI: 'Set Main' button clicked [Server Logic Start]");

                        // 优先使用闭包中的 player (在服务端构建 UI 时，此 player 即为 ServerPlayer)
                        if (player instanceof ServerPlayer serverPlayer) {
                            net.Lcing.fanchenwendao.FanChenWenDao.LOGGER.info("UI: Valid ServerPlayer found: {}", serverPlayer.getName().getString());

                            JingJieData serverdata = serverPlayer.getData(ModAttachments.JINGJIE_DATA);

                            //检测是否学习了功法
                            if (serverdata.getLearnedGongFas().containsKey(gongfaID)) {
                                serverdata.setMainGongFaID(gongfaID);
                                net.Lcing.fanchenwendao.FanChenWenDao.LOGGER.info("UI: Main GongFa set to {}", gongfaID);

                                //同步数据
                                JingJieHelper.syncToClient(serverPlayer);
                                //反馈
                                button.setText("已设为主修");
                                button.setActive(false);
                            } else {
                                net.Lcing.fanchenwendao.FanChenWenDao.LOGGER.warn("UI: Player has NOT learned GongFa: {}", gongfaID);
                            }
                        } else {
                            // 备用方案：尝试从 ModularUI 获取 (以防闭包捕获了错误的 ClientPlayer)
                            net.Lcing.fanchenwendao.FanChenWenDao.LOGGER.warn("UI: Closure player is NOT ServerPlayer ({}). Attempting fallback...", player.getClass().getSimpleName());

                            ModularUI modularUI = event.target.getModularUI();
                            if (modularUI != null && modularUI.player instanceof ServerPlayer fallbackPlayer) {
                                net.Lcing.fanchenwendao.FanChenWenDao.LOGGER.info("UI: Fallback ServerPlayer found via ModularUI: {}", fallbackPlayer.getName().getString());
                                
                                JingJieData serverdata = fallbackPlayer.getData(ModAttachments.JINGJIE_DATA);
                                if (serverdata.getLearnedGongFas().containsKey(gongfaID)) {
                                    serverdata.setMainGongFaID(gongfaID);
                                    JingJieHelper.syncToClient(fallbackPlayer);
                                    button.setText("已设为主修");
                                    button.setActive(false);
                                }
                            } else {
                                net.Lcing.fanchenwendao.FanChenWenDao.LOGGER.error("UI: CRITICAL - Could not resolve ServerPlayer context!");
                            }
                        }
                    });
                }
            }
        });

        return ModularUI.of(ui, player);

    }
}
