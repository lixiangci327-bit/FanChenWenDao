package net.Lcing.fanchenwendao.client.handler;


import com.lowdragmc.photon.client.PhotonParticleManager;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.client.animation.FCAnimations;
import net.Lcing.fanchenwendao.client.fx.BoneExecutor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = FanChenWenDao.MODID, value = Dist.CLIENT)
public class StateFXHandler {

    //登记簿 <玩家ID, 正在播放的特效控制器>——防止重复创建特效，停止后销毁特效
    private static final Map<Integer, BoneExecutor> SITTING_PLAYERS = new HashMap<>();

    //每帧更新时间
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        Player player = event.getEntity();
        int entityId = player.getId();

        //只在客户端运行
        if (!player.level().isClientSide) return;


        //检查打坐状态
        boolean isSitting = false;
        LivingEntityPatch<?> patch = EpicFightCapabilities.getEntityPatch(player, LivingEntityPatch.class);

        if (patch != null) {

            //获取当前动画
            var currentAnim = patch.getAnimator().getPlayerFor(null).getAnimation();

            if (currentAnim != null && FCAnimations.SITDOWN.registryName().equals(currentAnim.registryName())) {
                isSitting = true;
            }
        }

        //状态机
        if (isSitting) {
            //如果坐下的玩家名单内没有这个ID
            if (!SITTING_PLAYERS.containsKey(entityId)) {
                startSitFX(player);
            }
        } else {
            if (SITTING_PLAYERS.containsKey(entityId)) {
                stopSitFX(entityId);
            }
        }
    }

    //开启特效
    private static void startSitFX(Player player) {
        ResourceLocation fxLoc = ResourceLocation.fromNamespaceAndPath(FanChenWenDao.MODID,"xiulian01");

        FX fx = FXHelper.getFX(fxLoc);
        if (fx == null) return;

        BoneExecutor executor = new BoneExecutor(
                fx, player.level(), player, "Torso", true
        );
        executor.start();

        SITTING_PLAYERS.put(player.getId(), executor);
    }

    //关闭特效
    private static void stopSitFX(int entityId) {

        //移除登记簿的信息
        BoneExecutor executor = SITTING_PLAYERS.remove(entityId);

        //检查是否有特效运行
        if (executor != null && executor.getRuntime() != null) {
            executor.getRuntime().destroy(false);//销毁特效
        }
    }
}
