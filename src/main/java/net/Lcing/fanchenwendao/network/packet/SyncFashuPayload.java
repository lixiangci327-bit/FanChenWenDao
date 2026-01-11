package net.Lcing.fanchenwendao.network.packet;

import io.netty.buffer.ByteBuf;
import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.fashu.FashuData;
import net.Lcing.fanchenwendao.fashu.FashuType;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncFashuPayload(int entityId, int fashuId) implements CustomPacketPayload {

    //定义数据包的Type
    public static final Type<SyncFashuPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FanChenWenDao.MODID, "current_fashu")
    );

    //定义打包/拆包规则
    public static final StreamCodec<ByteBuf, SyncFashuPayload> STREAM_CODEC = StreamCodec.composite(
            //规则
            ByteBufCodecs.INT,SyncFashuPayload::entityId,
            ByteBufCodecs.INT,SyncFashuPayload::fashuId,//调用fashuId()方法拿数据

            SyncFashuPayload::new
    );

    //亮出Type
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    //客户端收包后逻辑
    public static void handle(final SyncFashuPayload payload, final IPayloadContext context) {

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
}
