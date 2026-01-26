package net.Lcing.fanchenwendao.network.packet;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.fashu.FaShuDefine;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record SyncFaShuPayload(Map<ResourceLocation, FaShuDefine> fashus) implements CustomPacketPayload {

    //定义Type
    public static final Type<SyncFaShuPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FanChenWenDao.MODID, "sync_fashu")
    );

    //定义StreamCodec
    public static final StreamCodec<FriendlyByteBuf, SyncFaShuPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ResourceLocation.STREAM_CODEC,
                    FaShuDefine.STREAM_CODEC
            ),
            SyncFaShuPayload::fashus, //Getter
            SyncFaShuPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}