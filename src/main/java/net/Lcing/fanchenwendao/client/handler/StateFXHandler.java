package net.Lcing.fanchenwendao.client.handler;

import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.client.animation.FCAnimations;
import net.Lcing.fanchenwendao.client.fx.BoneExecutor;
import net.Lcing.fanchenwendao.client.fx.VisualConfig;
import net.Lcing.fanchenwendao.gongfa.GongFaManager;
import net.Lcing.fanchenwendao.jingjie.JingJieData;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = FanChenWenDao.MODID, value = Dist.CLIENT)
public class StateFXHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateFXHandler.class);

    // 获取主修功法ID
    private record ActiveSession(ResourceLocation gongfaID, List<BoneExecutor> executors) {
    }

    // 登记簿 <玩家ID, 正在播放的特效控制器>——防止重复创建特效，停止后销毁特效
    private static final Map<Integer, ActiveSession> ACTIVE_FX = new HashMap<>();

    // 每帧更新时间
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        Player player = event.getEntity();

        // 只在客户端运行
        if (!player.level().isClientSide)
            return;

        int entityId = player.getId();
        JingJieData data = player.getData(ModAttachments.JINGJIE_DATA);

        boolean isXiulian = data.isXiulian();
        ResourceLocation mainGongFa = data.getMainGongFaID(); // 主修功法

        // 触发器逻辑
        if (isXiulian && mainGongFa != null) {
            ActiveSession currentSession = ACTIVE_FX.get(entityId);

            // 若未播放特效/中途切换了主修功法
            if (currentSession == null || !mainGongFa.equals(currentSession.gongfaID())) {
                // 先关掉旧的
                stopFX(entityId);
                // 播放新特效
                startFX(player, mainGongFa);
            }
        } else {
            // 若未修炼，确保特效关闭
            if (ACTIVE_FX.containsKey(entityId)) {
                stopFX(entityId);
            }
        }
    }

    // 开启特效
    private static void startFX(Player player, ResourceLocation gongfaID) {

        // LOGGER.info("准备为玩家 {} 播放功法特效: {}", player.getName().getString(), gongfaID);

        List<BoneExecutor> executors = new ArrayList<>();

        // 尝试获取功法数据
        var gongfaOpt = GongFaManager.getGongFa(gongfaID);
        if (gongfaOpt.isEmpty()) {
            LOGGER.error("严重警告: 客户端未找到功法数据 [{}]，可能是数据包未同步！", gongfaID); // [调试]
        }

        // 功法管理器获取功法数据
        gongfaOpt.ifPresent(gongfa -> {
            // 获取功法的特效数据
            VisualConfig config = gongfa.getDisplay().getXiulianVisual();

            // 检查特效ID
            if (config.fxID().isEmpty()) {
                // LOGGER.warn("功法 [{}] 未配置特效 ID (fx_id)", gongfaID); // [调试]
            }

            // 检查特效ID
            config.fxID().ifPresent(fxLoc -> {
                FX fx = FXHelper.getFX(fxLoc);
                if (fx != null) {

                    // LOGGER.info("成功加载特效文件: {}", fxLoc); // [调试]

                    // 遍历所有骨骼依次绑定特效
                    for (String joint : config.joints()) {
                        BoneExecutor executor = new BoneExecutor(fx, player.level(), player, joint,
                                config.followRotation());
                        executor.start();
                        executors.add(executor);
                    }

                } else {
                    LOGGER.error("找不到特效文件 [{}]，请检查 assets 路径！", fxLoc); // [调试]
                }
            });
        });

        // 登记,方便后续销毁
        ACTIVE_FX.put(player.getId(), new ActiveSession(gongfaID, executors));
        LOGGER.info("特效 Session 已注册，包含 {} 个执行器", executors.size()); // [调试]

    }

    // 关闭特效
    private static void stopFX(int entityId) {

        ActiveSession currentSession = ACTIVE_FX.remove(entityId);
        if (currentSession != null) {
            for (BoneExecutor executor : currentSession.executors()) {
                if (executor != null && executor.getRuntime() != null) {
                    executor.getRuntime().destroy(false);
                }
            }
        }
    }
}
