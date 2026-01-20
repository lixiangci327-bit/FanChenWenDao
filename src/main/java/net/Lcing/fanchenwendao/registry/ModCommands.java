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

}
