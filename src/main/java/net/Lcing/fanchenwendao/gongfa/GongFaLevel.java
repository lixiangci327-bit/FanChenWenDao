package net.Lcing.fanchenwendao.gongfa;


//功法品阶 枚举，定义四大基本功法品阶
public enum GongFaLevel {

    //规则：（x, y） x——基础吞吐速度；y——转化率

    //黄阶
    HUANG(5.0f, 0.6f),
    //玄阶
    XUAN(20.0f, 0.8f),
    //地阶
    DI(50.0f, 1.0f),
    //天阶
    TIAN(100.0f, 1.2f);

    private final float baserate;   //基础吸收速率
    private final float efficiency; //灵气 -> 修为 转换率

    //构造函数
    GongFaLevel(float baserate, float efficiency) {
        this.baserate = baserate;
        this.efficiency = efficiency;
    }

    //Getters
    public float getBaserate() { return baserate; }
    public float getEfficiency() { return efficiency; }
}
