package net.Lcing.fanchenwendao.fashu.logic;


import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

//法术逻辑注册表：管理法术行为模板
public class FaShuLogics {

    //存储ID
    private static final Map<ResourceLocation, IFaShuLogic> REGISTRY = new HashMap<>();

    //预定义的逻辑
    public static final ResourceLocation PROJECTILE = ResourceLocation.parse("fanchenwendao:projectile");

    //初始化注册
    public static void init() {
        register(PROJECTILE, new ProjectileSpellLogic());
    }

    //注册方法
    public static void register(ResourceLocation id, IFaShuLogic logic) {
        REGISTRY.put(id, logic);
    }

    //获取方法
    public static IFaShuLogic getLogic(ResourceLocation id) {
        //若Json里面为不存在的逻辑id
        return REGISTRY.getOrDefault(id, (caster, level, define) -> {
            //可加日志
            System.err.println("CRITICAL: Attempted to use UNREGISTERED logic ID: " + id);
        });
    }
}
