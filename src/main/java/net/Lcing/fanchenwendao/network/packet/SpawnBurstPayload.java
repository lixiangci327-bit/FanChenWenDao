package net.Lcing.fanchenwendao.network.packet;

import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import io.netty.buffer.ByteBuf;
import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.client.fx.ExampleExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Minecart;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SpawnBurstPayload(double x, double y, double z) implements CustomPacketPayload {


    //定义唯一类型ID
    public static final Type<SpawnBurstPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FanChenWenDao.MODID, "spawn_burst")
    );

    //定义编解码器：打包、拆包这三个数字（坐标）
    public static final StreamCodec<ByteBuf, SpawnBurstPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, SpawnBurstPayload::x,
            ByteBufCodecs.DOUBLE, SpawnBurstPayload::y,
            ByteBufCodecs.DOUBLE, SpawnBurstPayload::z,
            SpawnBurstPayload::new
    );

    @Override
    public Type<? extends  CustomPacketPayload> type() {
        return TYPE;
    }
}
