package net.Lcing.fanchenwendao.event;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.command.FCAnimationCommand;
import net.Lcing.fanchenwendao.fashu.FashuHandler;
import net.Lcing.fanchenwendao.gongfa.GongFaDefine;
import net.Lcing.fanchenwendao.gongfa.GongFaManager;
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

            //若数据为空，发送警告
            if (allGongFas.isEmpty()) {
                FanChenWenDao.LOGGER.warn("玩家 {} 登录，但服务器尚未加载功法数据。", serverPlayer.getName().getString());
                return;
            }

            //发送数据包到客户端
            SyncGongFaPayload payload = new SyncGongFaPayload(allGongFas);
            PacketDistributor.sendToPlayer(serverPlayer, payload);

            FanChenWenDao.LOGGER.info("已同步 {} 个功法定义至玩家 {}", allGongFas.size(), serverPlayer.getName().getString());

        }
    }
}
