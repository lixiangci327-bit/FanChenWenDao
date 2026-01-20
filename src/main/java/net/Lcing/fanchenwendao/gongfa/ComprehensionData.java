package net.Lcing.fanchenwendao.gongfa;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

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
}
