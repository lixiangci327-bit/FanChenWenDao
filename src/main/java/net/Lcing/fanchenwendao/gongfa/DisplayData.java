package net.Lcing.fanchenwendao.gongfa;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

//功法外观视觉数据
public class DisplayData {

    private final ResourceLocation icon;    //功法图标
    private final int color;    //功法主题色

    public DisplayData(ResourceLocation icon, int color) {
        this.icon = icon;
        this.color = color;
    }

    //Getter
    public ResourceLocation getIcon() { return icon; }
    public int getColor() { return color; }

    //Codec
    public static final Codec<DisplayData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("icon", ResourceLocation.parse("fannchenwendao:gongfa_book")).forGetter(DisplayData::getIcon),
            Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(DisplayData::getColor)
    ).apply(instance, DisplayData::new));
}
