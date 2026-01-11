package net.Lcing.fanchenwendao.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.Lcing.fanchenwendao.client.animation.FCAnimations;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class FCAnimationCommand {

    //注册指令方法
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sitdown")
                .executes(FCAnimationCommand::excute));
    }

    //执行方法
    private static int excute(CommandContext<CommandSourceStack> context) {

        try {
            //获取发送指令的玩家
            ServerPlayer player = context.getSource().getPlayerOrException();

            //获取Epic玩家补丁
            ServerPlayerPatch patch = EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);

            if (patch != null) {
                //播放动画
                patch.playAnimationSynchronized(FCAnimations.SITDOWN, 0.7f); //过渡时间
                return 1;//播放成功
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }
}
