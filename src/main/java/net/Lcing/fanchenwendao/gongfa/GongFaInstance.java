package net.Lcing.fanchenwendao.gongfa;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

//功法实例类
//代表玩家学会的任意一本功法
public class GongFaInstance {

    //引用功法的静态ID
    private final ResourceLocation gongfaID;
    private int layer;  //功法层数
    private float mastery;  //功法熟练度

    public GongFaInstance(ResourceLocation gongfaID, int layer, float mastery) {
        this.gongfaID = gongfaID;
        this.layer = layer;
        this.mastery = mastery;
    }

    //初始化构造函数，学会时默认为0层0熟练度
    public GongFaInstance(ResourceLocation gongfaID) {
        this(gongfaID, 0, 0.0f);
    }

    //Getter
    public ResourceLocation getGongfaID() { return gongfaID; }
    public int getLayer() { return layer; }
    public float getMastery() { return mastery; }

    //Setter(用于修炼升级)
    public void setLayer(int layer) { this.layer = layer; }
    public void setMastery(float mastery) { this.mastery = mastery; }

    //增加熟练度
    public void addMastery(float amount) {
        this.mastery += amount;
    }

    //NBT序列化
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", this.gongfaID.toString());
        tag.putInt("layer", this.layer);
        tag.putFloat("mastery", this.mastery);
        return tag;
    }

    //NBT反序列化：从硬盘读取，静态工厂方法，直接从NBT生成对象
    public static GongFaInstance loadFromNBT(CompoundTag tag) {
        ResourceLocation id = ResourceLocation.parse(tag.getString("id"));
        int layer = tag.getInt("layer");
        float mastery = tag.getFloat("mastery");
        return new GongFaInstance(id, layer, mastery);
    }

    //Codec：用于网络通过同步
    public static final Codec<GongFaInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(GongFaInstance::getGongfaID),
            Codec.INT.optionalFieldOf("layer", 0).forGetter(GongFaInstance::getLayer),
            Codec.FLOAT.optionalFieldOf("mastery", 0.0f).forGetter(GongFaInstance::getMastery)
    ).apply(instance, GongFaInstance::new));

    //StreamCodec
    public static final StreamCodec<FriendlyByteBuf, GongFaInstance> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, GongFaInstance::getGongfaID,
            ByteBufCodecs.VAR_INT, GongFaInstance::getLayer,
            ByteBufCodecs.FLOAT, GongFaInstance::getMastery,
            GongFaInstance::new
    );

    //DEBUG
    @Override
    public String toString() {
        return "GongFaInstance{id=" + gongfaID + ", layer=" + layer + ", mastery=" + mastery + "}";
    }


}
