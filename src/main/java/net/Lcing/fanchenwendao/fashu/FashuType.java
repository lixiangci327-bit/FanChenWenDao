package net.Lcing.fanchenwendao.fashu;

import net.Lcing.fanchenwendao.fashu.ningqi.Fireball;
import net.Lcing.fanchenwendao.fashu.ningqi.ThrownSword;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;


//法术类型枚举
public enum FashuType {
    FIREBALL_SHOOT(0, "火球术", new Fireball()),
    THROWN_SWORD(1, "离手剑", new ThrownSword());

    private final int id;
    private final String name;
    private final IFashuCast fashu;

    //构造函数，初始化属性
    FashuType(int id, String name, IFashuCast fashu) {
        this.id = id;
        this.name = name;
        this.fashu = fashu;
    }

    //直接执行法术
    public void cast(Player player) {
        if (this.fashu != null) {
            this.fashu.cast(player);
        }else {
            player.sendSystemMessage(Component.literal("该法术未实装"));
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    //通过id找回枚举
    public static FashuType getById(int id) {
        for (FashuType fashuType : FashuType.values()) {
            if (fashuType.getId() == id) {
                return fashuType;
            }
        }
        //设置默认值防止崩溃
        return FIREBALL_SHOOT;
    }
}
