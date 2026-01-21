package net.Lcing.fanchenwendao.jingjie;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.Lcing.fanchenwendao.gongfa.GongFaInstance;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


//境界数据类——存储玩家的修仙境界信息
public class JingJieData implements INBTSerializable<CompoundTag> {

    //存储修仙数据
    private int level;
    private float experience;//修为
    private float lingli;   //灵力
    private boolean isXiulian;  //是否修炼的开关
    private ResourceLocation mainGongFaID;  //主修功法ID
    private Map<ResourceLocation, GongFaInstance> learnedGongFas;   //已经学会的功法库

    //默认构造函数：玩家刚生成时为0
    public JingJieData() {
        this.level = 0;
        this.experience = 0;
        this.lingli = 0;
        this.isXiulian = false;
        this.mainGongFaID = null;   //默认无功法
        this.learnedGongFas = new HashMap<>();  //初始化Map,防止get/put调用时空指针报错
    }

    //带参数的构造函数，由Codec使用
    public JingJieData(int level, float experience, float lingli, boolean isXiulian,
                       Optional<ResourceLocation> mainGongFaID, Map<ResourceLocation, GongFaInstance> learnedGongFas) {
        this.level = level;
        this.experience = experience;
        this.lingli = lingli;
        this.isXiulian = isXiulian;
        this.mainGongFaID = mainGongFaID.orElse(null);
        this.learnedGongFas = learnedGongFas;
    }

    //Getter
    public int getLevel() {return level;}

    public float getExperience() {return experience;}

    public float getLingli() {return lingli;}

    public boolean isXiulian() {return isXiulian;}

    public ResourceLocation getMainGongFaID() {return mainGongFaID;}

    public Map<ResourceLocation, GongFaInstance> getLearnedGongFas() { return learnedGongFas; }

    //获取当前主修功法的功法实例，若无则返回null
    public GongFaInstance getMainGongFaInstance() {
        if (this.mainGongFaID == null) return null;
        return learnedGongFas.get(mainGongFaID);
    }



    //Setter
    public void setLevel(int level) {
        this.level = Math.max(0, Math.min(level, 50));
    }

    public void setExperience(float experience) {
        this.experience = Math.max(0, experience);
    }

    public void setLingli(float lingli) {
        this.lingli = Math.max(0, lingli);  //防止灵力为复数
    }

    public void setXiulian(boolean isXiulian) {
        this.isXiulian = isXiulian;
    }

    public void setMainGongFaID(ResourceLocation id) {
        if (this.learnedGongFas.containsKey(id)) {
            this.mainGongFaID = id;
        }
    }



    //内部Helper（不依赖外部环境）
    public void addExperience(float amount) {
        this.experience += amount;
    }

    public void addLingli(float amount) {
        this.lingli += amount;
        if (this.lingli < 0) {
            this.lingli = 0;    //防止为负数
        }
    }


    //升级方法
    public void levelUp() {
        setLevel(level + 1);
        setExperience(0);//升级后的修为，目前暂定为0，后续改为溢出值，以及连续破境的逻辑
    }

    //学习新功法
    public GongFaInstance learnGongFa(ResourceLocation id) {
        //先确认有没有这本书
        GongFaInstance instance = this.learnedGongFas.get(id);

        //如果没有说明没有学习过
        if (instance == null) {
            //创建一个新的实例放进Map
            instance = new GongFaInstance(id);
            this.learnedGongFas.put(id, instance);
        }
        return instance;    //返回这本功法，不管是否学习过
    }




    //Codec jingjiedata - int 相互转换
    public static final Codec<JingJieData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("level").forGetter(JingJieData::getLevel),
                    Codec.FLOAT.optionalFieldOf("experience", 0.0f).forGetter(JingJieData::getExperience),
                    Codec.FLOAT.optionalFieldOf("lingli", 0.0f).forGetter(JingJieData::getLingli),
                    Codec.BOOL.optionalFieldOf("is_xiulian", false).forGetter(JingJieData::isXiulian),
                    ResourceLocation.CODEC.optionalFieldOf("main_gongfa_id").forGetter(data -> Optional.ofNullable(data.getMainGongFaID())),
                    Codec.unboundedMap(ResourceLocation.CODEC, GongFaInstance.CODEC).optionalFieldOf("learned_gongfas", new HashMap<>()).forGetter(JingJieData::getLearnedGongFas)
            ).apply(instance, JingJieData::new)
    );

    //NBT读写
    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        //保存NBT：创建一个新的标签，放入level
        CompoundTag tag = new CompoundTag();
        tag.putInt("level", this.level);
        tag.putFloat("experience", this.experience);
        tag.putFloat("lingli", this.lingli);
        tag.putBoolean("is_xiulian", this.isXiulian);

        if (this.mainGongFaID != null) {
            tag.putString("main_gongfa_id", this.mainGongFaID.toString());
        }

        //功法Map
        ListTag listTag = new ListTag();    //创建列表容器
        for (GongFaInstance instance : this.learnedGongFas.values()) {  //遍历所有功法，对所有的instance做操作
            listTag.add(instance.saveToNBT());  //对象转为NBT
        }
        tag.put("learned_gongfas", listTag);    //贴上标识放入tag

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        //读取NBT：从NBT标签tag里面拿出level赋值给当前对象
        if (tag.contains("level")) {this.level = tag.getInt("level");}
        if (tag.contains("experience")) {this.experience = tag.getFloat("experience");}
        if (tag.contains("lingli")) {this.lingli = tag.getFloat("lingli");}
        if (tag.contains("is_xiulian")) {this.isXiulian = tag.getBoolean("is_xiulian");}

        if (tag.contains("main_gongfa_id")) {
            this.mainGongFaID = ResourceLocation.parse(tag.getString("main_gongfa_id"));
        } else {
            this.mainGongFaID = null;
        }

        if (tag.contains("learned_gongfas")) {  //防止读空
            this.learnedGongFas.clear();    //清空当前内存
            ListTag listTag = tag.getList("learned_gongfas", ListTag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag instanceTag = listTag.getCompound(i);
                GongFaInstance instance = GongFaInstance.loadFromNBT(instanceTag);  //NBT转为对象
                this.learnedGongFas.put(instance.getGongfaID(), instance);  //放回map
            }

        }
    }

    //debug
    @Override
    public String toString() {
        return "JingJieData{level=" + level + ", exp=" + experience + ", main=" + mainGongFaID + ", count=" + learnedGongFas.size() + "}";
    }
}
