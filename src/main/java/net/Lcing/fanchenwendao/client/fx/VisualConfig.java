package net.Lcing.fanchenwendao.client.fx;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record VisualConfig(
        Optional<ResourceLocation> animation,
        Optional<ResourceLocation> fxID,
        List<String> joints,
        boolean followRotation
) {

    //默认空对象
    public static final VisualConfig EMPTY = new VisualConfig(Optional.empty(), Optional.empty(), new ArrayList<>(), true);

    //Codec
    public static final Codec<VisualConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            //动画
            ResourceLocation.CODEC.optionalFieldOf("animation").forGetter(VisualConfig::animation),
            //特效id
            ResourceLocation.CODEC.optionalFieldOf("fx_id").forGetter(VisualConfig::fxID),
            //骨骼列表
            Codec.STRING.listOf().optionalFieldOf("joints", List.of()).forGetter(VisualConfig::joints),
            //是否跟随旋转
            Codec.BOOL.optionalFieldOf("follow_rotation", true).forGetter(VisualConfig::followRotation)
    ).apply(instance, VisualConfig::new));  //拼起来new一个新对象

    //StreamCodec：压缩为二进制流，从服务端发送到客户端
    public static final StreamCodec<FriendlyByteBuf, VisualConfig> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional), VisualConfig::animation,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional), VisualConfig::fxID,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), VisualConfig::joints,
            ByteBufCodecs.BOOL, VisualConfig::followRotation,
            VisualConfig::new
    );
}
