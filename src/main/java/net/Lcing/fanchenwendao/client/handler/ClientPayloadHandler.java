package net.Lcing.fanchenwendao.client.handler;

import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.Lcing.fanchenwendao.client.fx.ExampleExecutor;
import net.Lcing.fanchenwendao.fashu.FaShuManager;
import net.Lcing.fanchenwendao.gongfa.GongFaManager;
import net.Lcing.fanchenwendao.jingjie.JingJieData;
import net.Lcing.fanchenwendao.network.packet.*;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {


    //爆炸效果处理
    public static void handleSpawnBurst(final SpawnBurstPayload payload, final IPayloadContext context){
        //将逻辑放入主线程队列，确保线程安全
        context.enqueueWork(() -> {
            //获取客户端世界
            var level = Minecraft.getInstance().level;
            if (level != null) {
                //加载爆炸特性
                FX fx = FXHelper.getFX(ResourceLocation.parse("photon:huoqiu01burst"));
                if (fx != null) {
                    //在指定坐标播放世界特性
                    ExampleExecutor executor = new ExampleExecutor(fx, level);
                    executor.emit();
                    executor.setPosition(payload.x(), payload.y(), payload.z());
                }
            }
        });
    }




    //处理境界同步
    public static void handleSyncJingJie(final SyncJingJiePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var clientLevel = Minecraft.getInstance().level;
            if (clientLevel != null) {
                //客户端找对应实体
                Entity entity = clientLevel.getEntity(payload.entityId());
                //只处理玩家数据
                if (entity instanceof Player player) {
                    //获取玩家身上的jingjiedata
                    JingJieData data = player.getData(ModAttachments.JINGJIE_DATA);
                    //更新收到的新数据
                    //直接调用data自己的反序列化方法
                    data.deserializeNBT(clientLevel.registryAccess(), payload.dataTag());
                }
            }
        });
    }

    //处理功法数据同步
    public static void handleSyncGongFa(final SyncGongFaPayload payload, final IPayloadContext context) {
        //切回主线程执行（网络包在网络线程接收，不能直接操作游戏数据）
        context.enqueueWork(() -> {
            GongFaManager.syncFromServer(payload.gongfas());
        });
    }

    //处理法术同步
    public static void handleSyncFaShu(final SyncFaShuPayload payload, final IPayloadContext context) {

        //enqueueWork 把任务排队到主线程
        context.enqueueWork(() -> {
            FaShuManager.syncFromServer(payload.fashus());  //调用Manager同步
        });
    }

    //处理法术切换同步
    public static void handleSelectFaShuSync(final SyncSelectFaShuPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = Minecraft.getInstance().level;
            if (level != null) {
                Entity entity = level.getEntity(payload.entityId());
                if (entity instanceof Player player) {
                    player.getData(ModAttachments.FASHU_DATA).setCurrentFaShuId(payload.fashuId());
                }
            }
        });
    }

    //处理瞬发特效
    public static void handleInstantFX(final SpawnInstantFXPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = Minecraft.getInstance().level;
            if (level != null) {

                //System.out.println("DEBUG: Client received FX packet. ID=" + payload.fxId() + " Pos=" + payload.x() + "," + payload.y() + "," + payload.z());

                FX fx = FXHelper.getFX(payload.fxId()); //获取特效ID
                if (fx != null) {
                    ExampleExecutor executor = new ExampleExecutor(fx, level);

                    //设置位置
                    executor.emit();    //播放特效
                    executor.setPosition(payload.x(), payload.y(), payload.z());

                    //System.out.println("DEBUG: FX resource found, emitting...");
                } else {
                    System.err.println("找不到特效 ID: " + payload.fxId());
                }
            }
        });
    }

}
