package net.Lcing.fanchenwendao.registry;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.jingjie.JingJieCommand;
import net.Lcing.fanchenwendao.menu.PlayerPanelMenu;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = FanChenWenDao.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {

        //境界指令
        JingJieCommand.register(event.getDispatcher());

        //openpanel
        event.getDispatcher().register(Commands.literal("openpanel")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    player.openMenu(new SimpleMenuProvider(
                            (containerId, playerInventory, player1) -> new PlayerPanelMenu(containerId, playerInventory),
                            Component.literal("修仙面板")
                    ));

                    return 1;
                })
        );
    }

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        // 注册一个客户端指令 /testgongfa
        event.getDispatcher().register(Commands.literal("testgongfa")
                .executes(context -> {
                    // 获取当前的玩家（客户端玩家）和 Minecraft 实例
                    net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();

                    // 1. 调用你写好的 GongFaUI 类来构建 UI 树
                    // 传入测试数据：名称、等级、描述
                    var mui = net.Lcing.fanchenwendao.client.ui.ldlib.GongFaUI.buildUI(
                            mc.player,
                            "青元剑诀",
                            "练气三层",
                            "这本剑诀由青元子所创，共分九层，修炼至圆满可幻化出青元剑气，威力惊人。"
                    );

                    // 2. 使用 LDLib 提供的通用屏幕类 ModularUIScreen 来显示这个 UI
                    // Component.empty() 表示屏幕不需要额外的标题文字
                    mc.setScreen(new com.lowdragmc.lowdraglib2.gui.holder.ModularUIScreen(mui, Component.empty()));

                    return 1; // 返回 1 表示指令执行成功
                })
        );
    }
}
