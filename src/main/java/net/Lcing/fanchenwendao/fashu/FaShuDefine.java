package net.Lcing.fanchenwendao.fashu;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.Lcing.fanchenwendao.client.fx.VisualConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

//法术定义类
public class FaShuDefine {

    //基础元数据
    private final ResourceLocation id;
    private final String name;  //显示名称
    private final String description;   //法术描述

    //战斗属性
    private final int cooldown; //冷却时间
    private final float cost;   //灵气消耗
    private final float damage; //基础法术伤害

    //逻辑指向：对应哪种法术模板
    private final ResourceLocation logicType;
    private final Map<String, String> logicParams;

    //VFX：Key-法术逻辑类型
    private final Map<String, VisualConfig> visuals;

    //构造函数
    public FaShuDefine(
            ResourceLocation id, String name, String description,
            int cooldown, float cost, float damage,
            ResourceLocation logicType, Map<String, String> logicParams,
            Map<String, VisualConfig> visuals
    ){
        this.id = id;
        this.name = name;
        this.description = description;
        this.cooldown = cooldown;
        this.cost = cost;
        this.damage = damage;
        this.logicType = logicType;
        this.logicParams = logicParams;
        this.visuals = visuals;
    }

    //Getter
    public ResourceLocation getId() {  return id;  }
    public String getName() {  return name;  }
    public String getDescription() { return description;  }
    public int getCooldown() { return cooldown;  }
    public float getCost() { return cost;  }
    public float getDamage() { return damage;  }
    public ResourceLocation getLogicType() { return logicType;  }
    public Map<String, String> getLogicParams() { return logicParams;  }
    public Map<String, VisualConfig> getVisuals() { return visuals;  }

    //Helper：从Map中读取数值
    public String getParam(String key, String defaultValue) {
        return logicParams.getOrDefault(key, defaultValue);
    }
    //获取int
    public int getParamInt(String key, int defaultValue) {
        try {
            //字符串转为int
            return Integer.parseInt(logicParams.getOrDefault(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            //转换失败，比如"abc"
            return defaultValue;
        }
    }
    //获取float
    public float getParamFloat(String key, float defaultValue) {
        try {
            //字符串转为float
            return Float.parseFloat(logicParams.getOrDefault(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            //转换失败，比如"abc"
            return defaultValue;
        }
    }
    //获取boolean
    public boolean getParamBool(String key, boolean defaultValue) {
        String val = logicParams.get(key);
        if(val == null) return defaultValue;
        return Boolean.parseBoolean(val);
    }

    //获取指定阶段的视觉配置
    public VisualConfig getVisual(String phase) {
        return visuals.getOrDefault(phase, VisualConfig.EMPTY);
    }




    //Codec
    public static final Codec<FaShuDefine> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("id", ResourceLocation.parse("fanchenwendao:unknown")).forGetter(FaShuDefine::getId),
            Codec.STRING.fieldOf("name").forGetter(FaShuDefine::getName),
            Codec.STRING.optionalFieldOf("description", "").forGetter(FaShuDefine::getDescription),
            Codec.INT.optionalFieldOf("cooldown", 20).forGetter(FaShuDefine::getCooldown),
            Codec.FLOAT.optionalFieldOf("cost", 10.0f).forGetter(FaShuDefine::getCost),
            Codec.FLOAT.optionalFieldOf("damage", 1.0f).forGetter(FaShuDefine::getDamage),
            ResourceLocation.CODEC.fieldOf("logic_type").forGetter(FaShuDefine::getLogicType),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("logic_params", new HashMap<>()).forGetter(FaShuDefine::getLogicParams),
            Codec.unboundedMap(Codec.STRING, VisualConfig.CODEC).optionalFieldOf("visuals", new HashMap<>()).forGetter(FaShuDefine::getVisuals)
    ).apply(instance, FaShuDefine::new));


    //定义Map的StreamCodec常量
    private static final StreamCodec<FriendlyByteBuf, Map<String, String>> MAP_STREAM_CODEC =
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8);
    private static final StreamCodec<FriendlyByteBuf, Map<String, VisualConfig>> VISUALS_STREAM_CODEC =
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, VisualConfig.STREAM_CODEC);

    //StreamCodec
    public static final StreamCodec<FriendlyByteBuf, FaShuDefine> STREAM_CODEC = StreamCodec.of(
            (buf, val) -> {
                ResourceLocation.STREAM_CODEC.encode(buf, val.id);
                ByteBufCodecs.STRING_UTF8.encode(buf, val.name);
                ByteBufCodecs.STRING_UTF8.encode(buf, val.description);
                ByteBufCodecs.INT.encode(buf, val.cooldown);
                ByteBufCodecs.FLOAT.encode(buf, val.cost);
                ByteBufCodecs.FLOAT.encode(buf, val.damage);
                ResourceLocation.STREAM_CODEC.encode(buf, val.logicType);
                MAP_STREAM_CODEC.encode(buf, val.logicParams);
                VISUALS_STREAM_CODEC.encode(buf, val.visuals);
            },
            (buf) -> new FaShuDefine(
                    ResourceLocation.STREAM_CODEC.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.FLOAT.decode(buf),
                    ByteBufCodecs.FLOAT.decode(buf),
                    ResourceLocation.STREAM_CODEC.decode(buf),
                    MAP_STREAM_CODEC.decode(buf),
                    VISUALS_STREAM_CODEC.decode(buf)
            )
    );
}
