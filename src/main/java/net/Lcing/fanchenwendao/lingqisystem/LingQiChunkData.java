package net.Lcing.fanchenwendao.lingqisystem;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

//区块chunk 灵气数据
public class LingQiChunkData {

    private float currentLingQi;    //当前灵气
    private float maxLingQi;    //最大灵气
    private long lastUpdateTick;    //上次更新时间戳，用于惰性计算

    //默认构造函数
    public LingQiChunkData() {
        this.currentLingQi = 0.0f;
        this.maxLingQi = 0.0f;
        this.lastUpdateTick = 0;
    }

    //全参构造函数：用于从存档读取数据时使用
    public LingQiChunkData(float currentLingQi, float maxLingQi, long lastUpdateTick) {
        this.currentLingQi = currentLingQi;
        this.maxLingQi = maxLingQi;
        this.lastUpdateTick = lastUpdateTick;
    }

    //Getters
    public float getCurrentLingQi() {
        return currentLingQi;
    }

    public float getMaxLingQi() {
        return maxLingQi;
    }

    public long getLastUpdateTick() {
        return lastUpdateTick;
    }



    //Setters
    public void setCurrentLingQi(float currentLingQi) {
        this.currentLingQi = Math.max(0, Math.min(currentLingQi, maxLingQi));   //保证灵气不小于0，不大于max
    }

    public void setMaxLingQi(float maxLingQi) {
        this.maxLingQi = maxLingQi;
        //若上限改变，当前灵气也不能超过新的上限
        if (this.currentLingQi > this.maxLingQi) {
            this.currentLingQi = maxLingQi;
        }
    }

    public void setLastUpdateTick(long lastUpdateTick) {
        this.lastUpdateTick = lastUpdateTick;
    }



    //常用方法
    public void addLingQi(float amount) {
        setCurrentLingQi(this.currentLingQi + amount);
    }

    //灵气消耗逻辑
    public float consumeLingQi(float amount) {
        //灵气充足
        if (this.currentLingQi >= amount) {
            this.currentLingQi -= amount;   //扣除灵气
            return amount;  //返回玩家需要的数量
        } else {
            //灵气不足，无法吸收
            return 0.0f;
        }

    }

    //debug
    @Override
    public String toString() {
        return "LingQiChunkData{" +
                "current=" + currentLingQi +
                ", max=" + maxLingQi +
                '}';
    }



    //Codec
    public static final Codec<LingQiChunkData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            //把currentLingQi存为float类型，名字为"current_lingqi"
            Codec.FLOAT.fieldOf("current_lingqi").forGetter(LingQiChunkData::getCurrentLingQi),
            Codec.FLOAT.fieldOf("max_lingqi").forGetter(LingQiChunkData::getMaxLingQi),
            Codec.LONG.fieldOf("last_update_tick").forGetter(LingQiChunkData::getLastUpdateTick)
    ).apply(instance, LingQiChunkData::new));




}
