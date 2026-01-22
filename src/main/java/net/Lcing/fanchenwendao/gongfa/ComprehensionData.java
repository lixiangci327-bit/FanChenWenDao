package net.Lcing.fanchenwendao.gongfa;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

//参悟功法数据
public class ComprehensionData {

    private final String comprehendType;   //参悟方式类型
    private final Requirements requirements;    //参悟消耗

    public ComprehensionData(String comprehendType, Requirements requirements) {
        this.comprehendType = comprehendType;
        this.requirements = requirements;
    }

    //Getter
    public String getComprehendType() { return comprehendType; }
    public Requirements getRequirements() { return requirements; }

    //Codec
    public static final Codec<ComprehensionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("comprehend_type", "default").forGetter(ComprehensionData::getComprehendType),
            Requirements.CODEC.fieldOf("requirements").forGetter(ComprehensionData::getRequirements)
    ).apply(instance, ComprehensionData::new));

    //StreamCodec
    public static final StreamCodec<FriendlyByteBuf, ComprehensionData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ComprehensionData::getComprehendType,
            Requirements.STREAM_CODEC, ComprehensionData::getRequirements,
            ComprehensionData::new
    );
}
