package net.Lcing.fanchenwendao.event;

import net.Lcing.fanchenwendao.command.FCAnimationCommand;
import net.Lcing.fanchenwendao.fashu.FashuHandler;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


public class CommonEventHandler {

    //设置木棍为法术测试道具
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if(event.getItemStack().is(Items.STICK)) {
            //调用Hanlder
            FashuHandler.rightclick(event.getEntity(), event.getLevel().isClientSide());

            //阻止木棍其他逻辑
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    //指令注册
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        FCAnimationCommand.register(event.getDispatcher());
    }
}
