package net.Lcing.fanchenwendao.gongfa;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//从资源包下的json文件加载数据
public class GongFaManager extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();  //日志记录器
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()    //调整格式
            .disableHtmlEscaping()  //禁止HTML转义
            .create();

    //存储所有加载好的功法
    private static final Map<ResourceLocation, GongFaDefine> GONGFAS = new HashMap<>();

    public GongFaManager() {
        super(GSON, "gongfa");  //"gongfa"是文件夹名字
    }

    //把所有Json文件打包为一个Map
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {

        //每次重载前先清空数据
        GONGFAS.clear();
        LOGGER.info("开始加载功法数据...");

        object.forEach((location, json) -> {
            try {
                //使用Codec解析JSON
                GongFaDefine.CODEC.parse(JsonOps.INSTANCE, json)
                        .resultOrPartial(error -> LOGGER.error("解析功法{}失败：{}", location, error))
                        .ifPresent(gongfa -> {
                            GONGFAS.put(location, gongfa);
                            LOGGER.debug("成功加载功法：{}", location);
                        });
            } catch (Exception e) {
                LOGGER.error("加载功法文件出错：{}", location, e);
            }
        });

        LOGGER.info("功法数据加载完成，共加载{}个功法。", GONGFAS.size());

    }

    //对外提供API，通过ID获取功法
    public static Optional<GongFaDefine> getGongFa(ResourceLocation id) {
        return Optional.ofNullable(GONGFAS.get(id));
    }

    //获取所有功法ID，用于遍历
    public static Map<ResourceLocation, GongFaDefine> getAll() {
        return GONGFAS;
    }

}
