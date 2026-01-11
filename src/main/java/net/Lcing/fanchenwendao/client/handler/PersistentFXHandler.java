package net.Lcing.fanchenwendao.client.handler;


import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.client.fx.BoneExecutor;
import net.Lcing.fanchenwendao.fashu.FashuData;
import net.Lcing.fanchenwendao.fashu.FashuType;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = FanChenWenDao.MODID, value = Dist.CLIENT)
public class PersistentFXHandler {

    //登记簿：玩家ID，特效类型，特效控制器
    private static final Map<String, BoneExecutor> ACTIVE_EFFECTS = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide) return;

        //读取同步过来的数据
        FashuData data = player.getData(ModAttachments.FASHU_DATA);
        FashuType currentType = data.getCurrentFashu();

        //定义状态逻辑——即配置特效
        boolean isFireFistActive = (currentType == FashuType.FIREBALL_SHOOT);
        //更新特效
        updateEffect(player, "fire_fist", isFireFistActive, "Tool_R", "firefist");
    }

    //更新特效方法：生成、销毁特效
    private static void updateEffect(Player player, String key, boolean isActive, String joint, String fxName) {

        //生成唯一的Map Key：玩家ID——特效key
        String mapkey = player.getId() + "_" + key;

        if (isActive) {
            //如果处于Active，且Map无记录，说明是刚切换到该法术
            if (!ACTIVE_EFFECTS.containsKey(mapkey)) {

                ResourceLocation fxLoc = ResourceLocation.fromNamespaceAndPath(FanChenWenDao.MODID, fxName);
                FX fx = FXHelper.getFX(fxLoc);

                if (fx != null) {
                    //创建并启动特效
                    BoneExecutor exector = new BoneExecutor(
                            fx, player.level(), player, joint, true //true-跟随旋转
                    );
                    exector.start();

                    //登记在map
                    ACTIVE_EFFECTS.put(mapkey, exector);
                }
            }
        } else {

            //如果应该关闭，且Map内有记录，说明是刚切换走该法术
            if (ACTIVE_EFFECTS.containsKey(mapkey)) {
                //取出数据并且销毁
                BoneExecutor executor = ACTIVE_EFFECTS.remove(mapkey);
                if (executor != null && executor.getRuntime() != null) {
                    executor.getRuntime().destroy(false);
                }
            }
        }
    }
}
