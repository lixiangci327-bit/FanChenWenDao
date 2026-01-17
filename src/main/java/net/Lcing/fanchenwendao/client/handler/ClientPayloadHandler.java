package net.Lcing.fanchenwendao.client.handler;

import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.Lcing.fanchenwendao.client.fx.ExampleExecutor;
import net.Lcing.fanchenwendao.fashu.FashuData;
import net.Lcing.fanchenwendao.fashu.FashuType;
import net.Lcing.fanchenwendao.jingjie.JingJieData;
import net.Lcing.fanchenwendao.network.packet.SpawnBurstPayload;
import net.Lcing.fanchenwendao.network.packet.SyncFashuPayload;
import net.Lcing.fanchenwendao.network.packet.SyncJingJiePayload;
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


    //处理法术同步
    public static void handleSyncFashu(final SyncFashuPayload payload, final IPayloadContext context) {

        //enqueueWork 把任务排队到主线程
        context.enqueueWork(() -> {
            //客户端逻辑
            //获取客户端世界
            var level = Minecraft.getInstance().level;

            if (level != null) {
                //根据ID查找实体
                Entity targetEntity = level.getEntity(payload.entityId());

                //确保是玩家
                if (targetEntity instanceof Player player) {
                    //拿到客户端的法术数据
                    FashuData data = player.getData(ModAttachments.FASHU_DATA);

                    //同步客户端数据
                    data.setCurrentFashu(FashuType.getById(payload.fashuId()));
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

}
