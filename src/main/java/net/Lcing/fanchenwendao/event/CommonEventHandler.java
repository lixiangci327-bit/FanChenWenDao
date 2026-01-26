package net.Lcing.fanchenwendao.event;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.command.FCAnimationCommand;
import net.Lcing.fanchenwendao.fashu.FaShuDefine;
import net.Lcing.fanchenwendao.fashu.FaShuManager;
import net.Lcing.fanchenwendao.fashu.FashuHandler;
import net.Lcing.fanchenwendao.gongfa.GongFaDefine;
import net.Lcing.fanchenwendao.gongfa.GongFaManager;
import net.Lcing.fanchenwendao.network.packet.SyncFaShuPayload;
import net.Lcing.fanchenwendao.network.packet.SyncGongFaPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;


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

    //玩家登陆时同步功法
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            //获取服务端加载的功法数据
            Map<ResourceLocation, GongFaDefine> allGongFas = GongFaManager.getAll();
            //获取服务端加载的法术数据
            Map<ResourceLocation, FaShuDefine> allFaShus = FaShuManager.getAll();

            // 功法同步逻辑
            if (allGongFas.isEmpty()) {
                FanChenWenDao.LOGGER.warn("玩家 {} 登录，但服务器尚未加载功法数据。", serverPlayer.getName().getString());
            } else {
                SyncGongFaPayload gongFaPayload = new SyncGongFaPayload(allGongFas);
                PacketDistributor.sendToPlayer(serverPlayer, gongFaPayload);
            }

            // 法术同步逻辑
            if (allFaShus.isEmpty()) {
                FanChenWenDao.LOGGER.warn("玩家 {} 登录，但服务器尚未加载法术数据。", serverPlayer.getName().getString());
            } else {
                SyncFaShuPayload fashuPayload = new SyncFaShuPayload(allFaShus);
                PacketDistributor.sendToPlayer(serverPlayer, fashuPayload);
            }

            FanChenWenDao.LOGGER.info("已尝试同步 {} 个功法，{}个法术定义至玩家 {}", allGongFas.size(), allFaShus.size(),
                    serverPlayer.getName().getString());

        }
    }
}
