package net.Lcing.fanchenwendao.network.packet;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.gongfa.GongFaDefine;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record SyncGongFaPayload(Map<ResourceLocation, GongFaDefine> gongfas) implements CustomPacketPayload {

    //定义Type
    public static final Type<SyncGongFaPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FanChenWenDao.MODID, "sync_gongfa")
    );

    //定义StreamCodec
    public static final StreamCodec<FriendlyByteBuf, SyncGongFaPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ResourceLocation.STREAM_CODEC,
                    GongFaDefine.STREAM_CODEC
            ),
            SyncGongFaPayload::gongfas, //Getter
            SyncGongFaPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
