package net.Lcing.fanchenwendao.network.packet;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record SyncSelectFaShuPayload(int entityId, ResourceLocation fashuId) implements CustomPacketPayload {

    // 定义 Type
    public static final Type<SyncSelectFaShuPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FanChenWenDao.MODID, "sync_select_fashu")
    );

    // 定义 StreamCodec
    public static final StreamCodec<FriendlyByteBuf, SyncSelectFaShuPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncSelectFaShuPayload::entityId,     // 玩家实体 ID
            ResourceLocation.STREAM_CODEC, SyncSelectFaShuPayload::fashuId, // 选中的法术资源 ID
            SyncSelectFaShuPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}