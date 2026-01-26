package net.Lcing.fanchenwendao.fashu;

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
public class FaShuManager extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    //存储所有加载好的法术
    private static final Map<ResourceLocation, FaShuDefine> FASHUS = new HashMap<>();

    public FaShuManager() {
        super(GSON, "fashu");  //"fashu"是data/modid/fashu/文件夹
    }

    //把所有Json文件打包为一个Map
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {

        //每次重载前先清空数据
        FASHUS.clear();
        LOGGER.info("开始加载法术数据...");

        object.forEach((location, json) -> {
            try {
                //使用Codec解析JSON
                FaShuDefine.CODEC.parse(JsonOps.INSTANCE, json)
                        .resultOrPartial(error -> LOGGER.error("解析法术{}失败：{}", location, error))
                        .ifPresent(fashu -> {
                            FASHUS.put(location, fashu);
                            LOGGER.debug("成功加载法术：{}", location);
                        });
            } catch (Exception e) {
                LOGGER.error("加载法术文件出错：{}", location, e);
            }
        });

        LOGGER.info("法术数据加载完成，共加载{}个法术。", FASHUS.size());

    }

    //对外提供API，通过ID获取法术
    public static Optional<FaShuDefine> getFaShu(ResourceLocation id) {
        return Optional.ofNullable(FASHUS.get(id));
    }

    //获取所有法术，用于遍历
    public static Map<ResourceLocation, FaShuDefine> getAll() {
        return FASHUS;
    }

    //从服务端同步数据
    public static void syncFromServer(Map<ResourceLocation, FaShuDefine> fashus) {
        FASHUS.clear();
        FASHUS.putAll(fashus);
        LOGGER.info("已从服务端同步 {} 个法术定义。", fashus.size());
    }

}