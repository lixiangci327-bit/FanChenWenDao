package net.Lcing.fanchenwendao.gongfa;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

//参悟功法需求
public class Requirements {

    private final int xpConsume;    //消耗的经验
    private final float minLingQi;  //最低灵气需求

    public Requirements(int xpConsume, float minLingQi) {
        this.xpConsume = xpConsume;
        this.minLingQi = minLingQi;
    }

    //Getter
    public int getXpConsume() { return xpConsume; }
    public float getMinLingQi() { return minLingQi; }

    //Codec
    public static final Codec<Requirements> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("xp_consume", 0).forGetter(Requirements::getXpConsume),
            Codec.FLOAT.optionalFieldOf("min_lingqi", 0.0f).forGetter(Requirements::getMinLingQi)
    ).apply(instance, Requirements::new));

    //StreamCodec
    public static final StreamCodec<FriendlyByteBuf, Requirements> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, Requirements::getXpConsume,
            ByteBufCodecs.FLOAT, Requirements::getMinLingQi,
            Requirements::new
    );
}
