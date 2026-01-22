package net.Lcing.fanchenwendao.gongfa;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;


//功法修炼数据
public class XiuLianData {

    private final float baseSpeed;  //基础修炼速度
    private final float baseMastery;    //基础参悟速度
    private final float efficiency; //灵气转化率

    //构造函数
    public XiuLianData(float baseSpeed, float baseMastery, float efficiency) {
        this.baseSpeed = baseSpeed;
        this.baseMastery = baseMastery;
        this.efficiency = efficiency;
    }

    //Getter
    public float getBaseSpeed() { return baseSpeed; }
    public float getBaseMastery() { return baseMastery; }
    public float getEfficiency() { return efficiency; }


    //Codec
    public static final Codec<XiuLianData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("base_speed").forGetter(XiuLianData::getBaseSpeed),
            Codec.FLOAT.fieldOf("base_mastery").forGetter(XiuLianData::getBaseMastery),
            Codec.FLOAT.fieldOf("efficiency").forGetter(XiuLianData::getEfficiency)
    ).apply(instance, XiuLianData::new));

    //StreamCodec
    public static final StreamCodec<FriendlyByteBuf, XiuLianData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, XiuLianData::getBaseSpeed,
            ByteBufCodecs.FLOAT, XiuLianData::getBaseMastery,
            ByteBufCodecs.FLOAT, XiuLianData::getEfficiency,
            XiuLianData::new
    );
}
