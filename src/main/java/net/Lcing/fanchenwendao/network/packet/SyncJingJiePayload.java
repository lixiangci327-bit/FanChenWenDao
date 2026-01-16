package net.Lcing.fanchenwendao.network.packet;

import io.netty.buffer.ByteBuf;
import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.jingjie.JingJieData;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncJingJiePayload(int entityId, CompoundTag dataTag) implements CustomPacketPayload {

    //定义Type
    public static final Type<SyncJingJiePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FanChenWenDao.MODID, "sync_jingjie")
    );

    //定义StreamCodec
    public static final StreamCodec<ByteBuf, SyncJingJiePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncJingJiePayload::entityId,//读写Id
            ByteBufCodecs.COMPOUND_TAG, SyncJingJiePayload::dataTag,
            SyncJingJiePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    //收包处理逻辑
    public static void handle(final SyncJingJiePayload payload, final IPayloadContext context) {
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
