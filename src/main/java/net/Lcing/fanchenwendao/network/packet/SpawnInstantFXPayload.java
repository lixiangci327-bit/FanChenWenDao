package net.Lcing.fanchenwendao.network.packet;


import io.netty.buffer.ByteBuf;
import net.Lcing.fanchenwendao.FanChenWenDao;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

//瞬发特效数据包
public record SpawnInstantFXPayload(double x, double y, double z, ResourceLocation fxId) implements CustomPacketPayload {

    public static final Type<SpawnInstantFXPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FanChenWenDao.MODID, "spawn_instant_fx")
    );


    //StreamCodec
    public static final StreamCodec<ByteBuf, SpawnInstantFXPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, SpawnInstantFXPayload::x,
            ByteBufCodecs.DOUBLE, SpawnInstantFXPayload::y,
            ByteBufCodecs.DOUBLE, SpawnInstantFXPayload::z,
            ResourceLocation.STREAM_CODEC, SpawnInstantFXPayload::fxId,
            SpawnInstantFXPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
