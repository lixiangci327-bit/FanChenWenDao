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

}
