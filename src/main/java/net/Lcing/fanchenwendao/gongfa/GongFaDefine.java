package net.Lcing.fanchenwendao.gongfa;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

//定义功法
public class GongFaDefine {

    //功法基础信息
    private final ResourceLocation id;  //id
    private final String name;  //功法名称
    private final String description;   //功法描述
    private final String attribute; //属性（水，火）
    private final String level;    //品阶
    private final XiuLianData xiulian;  //功法修炼模块
    private final ComprehensionData comprehension;  //功法参悟模块
    private final DisplayData display;  //外观数据

    //构造函数
    public GongFaDefine(ResourceLocation id, String name, String description, String attribute,
                       String level, XiuLianData xiulian, ComprehensionData comprehension, DisplayData display) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.attribute = attribute;
        this.level = level;
        this.xiulian = xiulian;
        this.comprehension = comprehension;
        this.display = display;
    }

    //Getter
    public ResourceLocation getId() {return id;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public String getAttribute() {return attribute;}
    public String getLevel() {return level;}
    public XiuLianData getXiulian() {return xiulian;}
    public ComprehensionData getComprehension() {return comprehension;}
    public DisplayData getDisplay() {return display;}


    //Codec
    public static final Codec<GongFaDefine> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("id", ResourceLocation.parse("fanchenwendao:unknown")).forGetter(GongFaDefine::getId),
            Codec.STRING.fieldOf("name").forGetter(GongFaDefine::getName),
            Codec.STRING.optionalFieldOf("description", "").forGetter(GongFaDefine::getDescription),
            Codec.STRING.fieldOf("attribute").forGetter(GongFaDefine::getAttribute),
            Codec.STRING.fieldOf("level").forGetter(GongFaDefine::getLevel),
            XiuLianData.CODEC.fieldOf("xiulian").forGetter(GongFaDefine::getXiulian),
            ComprehensionData.CODEC.fieldOf("comprehension").forGetter(GongFaDefine::getComprehension),
            DisplayData.CODEC.optionalFieldOf("display", new DisplayData(ResourceLocation.parse("minecraft:book"), 0xFFFFFF)).forGetter(GongFaDefine::getDisplay)
    ).apply(instance, GongFaDefine::new));


    //StreamCodec
    public static final StreamCodec<FriendlyByteBuf, GongFaDefine> STREAM_CODEC = StreamCodec.of(
            //编码器，把对象写入buffer
            (buf, gongfa) -> {
                ResourceLocation.STREAM_CODEC.encode(buf, gongfa.getId());
                ByteBufCodecs.STRING_UTF8.encode(buf, gongfa.getName());
                ByteBufCodecs.STRING_UTF8.encode(buf, gongfa.getDescription());
                ByteBufCodecs.STRING_UTF8.encode(buf, gongfa.getAttribute());
                ByteBufCodecs.STRING_UTF8.encode(buf, gongfa.getLevel());
                XiuLianData.STREAM_CODEC.encode(buf, gongfa.getXiulian());
                ComprehensionData.STREAM_CODEC.encode(buf, gongfa.getComprehension());
                DisplayData.STREAM_CODEC.encode(buf, gongfa.getDisplay());
            },
            //解码器
            (buf) -> new GongFaDefine(
                    ResourceLocation.STREAM_CODEC.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    XiuLianData.STREAM_CODEC.decode(buf),
                    ComprehensionData.STREAM_CODEC.decode(buf),
                    DisplayData.STREAM_CODEC.decode(buf)
            )
    );
}
