package net.Lcing.fanchenwendao.jingjie;


//境界相关指令

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class JingJieCommand {

    //注册逻辑
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        //注册主指令/jingjie
        dispatcher.register(Commands.literal("jingjie")
                .requires(source -> source.hasPermission(2))//权限设置
                .then(Commands.literal("get")
                        .executes(JingJieCommand::handleGet))//子指令1：获取境界

                .then(Commands.literal("levelup")
                        .executes(JingJieCommand::handleLevelup))
        );
    }

    //执行逻辑
    private static int handleGet(final CommandContext<CommandSourceStack> context) {
        try {
            //获取指令发送者——必须是玩家
            ServerPlayer player = context.getSource().getPlayerOrException();

            //调用Helper获取数据
            int currentLevel = JingJieHelper.getLevel(player);
            float currentexperience = JingJieHelper.getExperience(player);

            context.getSource().sendSuccess(() -> Component.literal("当前境界层数为：" + currentLevel + "当前修为：" + currentexperience), false);

            return 1;//表示成功

        } catch (Exception e) {
            //不是玩家输入指令
            return 0;
        }
    }

    private static int handleLevelup(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();

            //调用helper的levelup方法
            int newlevel = JingJieHelper.levelup(player);

            context.getSource().sendSuccess(() -> Component.literal("境界提升成功，当前境界为：" + newlevel + "层"), true);

            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

}
