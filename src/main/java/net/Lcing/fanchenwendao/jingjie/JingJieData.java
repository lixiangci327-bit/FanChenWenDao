package net.Lcing.fanchenwendao.jingjie;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;


//境界数据类——存储玩家的修仙境界信息
public class JingJieData implements INBTSerializable<CompoundTag> {

    //存储修仙数据
    private int level;
    private float experience;//修为
    private float lingli;   //灵力
    private boolean isXiulian;  //是否修炼的开关
    private String mainGongFaName;  //主修功法ID

    //默认构造函数：玩家刚生成时为0
    public JingJieData() {
        this.level = 0;
        this.experience = 0;
        this.lingli = 0;
        this.isXiulian = false;
        this.mainGongFaName = "none";   //默认无功法
    }

    //带参数的构造函数，由Codec使用
    public JingJieData(int level, float experience, float lingli, boolean isXiulian, String mainGongFaName) {
        this.level = level;
        this.experience = experience;
        this.lingli = lingli;
        this.isXiulian = isXiulian;
        this.mainGongFaName = mainGongFaName;
    }

    //Getter
    public int getLevel() {return level;}

    public float getExperience() {return experience;}

    public float getLingli() {return lingli;}

    public boolean isXiulian() {return isXiulian;}

    public String getMainGongFaName() {return mainGongFaName;}



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

    public void setMainGongFaName(String mainGongFaName) {
        this.mainGongFaName = mainGongFaName;
    }




    public void addExperience(float amount) {
        this.experience += amount;
    }

    public void addLingli(float amount) {
        this.lingli += amount;
        if (this.lingli < 0) {
            this.lingli = 0;    //防止为负数
        }
    }

    //计算升级所需的修为
    public float getMaxExperience() {
        if (level == 0 ) return 100.0f;//凡人到凝气一层
        return (float) (100.0 * Math.pow(1.5, level));
    }

    //升级方法
    public void levelUp() {
        setLevel(level + 1);
        setExperience(0);//升级后的修为，目前暂定为0，后续改为溢出值，以及连续破境的逻辑
    }




    //Codec jingjiedata - int 相互转换
    public static final Codec<JingJieData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("level").forGetter(JingJieData::getLevel),
                    Codec.FLOAT.optionalFieldOf("experience", 0.0f).forGetter(JingJieData::getExperience),
                    Codec.FLOAT.optionalFieldOf("lingli", 0.0f).forGetter(JingJieData::getLingli),
                    Codec.BOOL.optionalFieldOf("is_xiulian", false).forGetter(JingJieData::isXiulian),
                    Codec.STRING.optionalFieldOf("maingongfaname", "none").forGetter(JingJieData::getMainGongFaName)
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
        tag.putString("maingongfaname", this.mainGongFaName == null ? "none" : this.mainGongFaName);    //若null则存为“none”
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        //读取NBT：从NBT标签tag里面拿出level赋值给当前对象
        if (tag.contains("level")) {this.level = tag.getInt("level");}
        if (tag.contains("experience")) {this.experience = tag.getFloat("experience");}
        if (tag.contains("lingli")) {this.lingli = tag.getFloat("lingli");}
        if (tag.contains("is_xiulian")) {this.isXiulian = tag.getBoolean("is_xiulian");}
        if (tag.contains("maingongfaname")) {this.mainGongFaName = tag.getString("maingongfaname");}
    }

    //debug
    @Override
    public String toString() {
        return "JingJieData{level=" + level + ", exp=" + experience + ", gongfa=" + mainGongFaName + "}";
    }
}
