package net.Lcing.fanchenwendao.client.fx;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import com.lowdragmc.photon.client.fx.FXRuntime;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import net.Lcing.fanchenwendao.FanChenWenDao;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * 可控特效执行器
 * <p>
 * 扩展 EntityEffectExecutor，添加手动停止功能
 * 用于需要与动画同步的特效播放
 */
@OnlyIn(Dist.CLIENT)
public class ControllableEntityFXExecutor extends EntityEffectExecutor {

    /**
     * 创建可控特效执行器
     *
     * @param fx         特效模板
     * @param level      世界
     * @param entity     绑定的实体
     * @param autoRotate 自动旋转模式
     */
    public ControllableEntityFXExecutor(FX fx, Level level, Entity entity, AutoRotate autoRotate) {
        super(fx, level, entity, autoRotate);
    }

    /**
     * 从特效路径创建执行器
     *
     * @param fxPath 特效路径 (例如 "xiulian01")
     * @param entity 绑定的实体
     * @return 执行器，如果特效不存在返回null
     */
    @Nullable
    public static ControllableEntityFXExecutor create(String fxPath, Entity entity) {
        return create(fxPath, entity, AutoRotate.NONE);
    }

    /**
     * 从特效路径创建执行器
     *
     * @param fxPath     特效路径
     * @param entity     绑定的实体
     * @param autoRotate 自动旋转模式
     * @return 执行器，如果特效不存在返回null
     */
    @Nullable
    public static ControllableEntityFXExecutor create(String fxPath, Entity entity, AutoRotate autoRotate) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(FanChenWenDao.MODID, fxPath);
        FX fx = FXHelper.getFX(id);
        if (fx == null) {
            FanChenWenDao.LOGGER.warn("无法找到特效: {}", fxPath);
            return null;
        }
        return new ControllableEntityFXExecutor(fx, entity.level(), entity, autoRotate);
    }

    /**
     * 设置特效位置偏移
     *
     * @param x X偏移
     * @param y Y偏移
     * @param z Z偏移
     * @return this (链式调用)
     */
    public ControllableEntityFXExecutor withOffset(float x, float y, float z) {
        this.setOffset(new Vector3f(x, y, z));
        return this;
    }

    /**
     * 停止并销毁特效
     */
    public void stop() {
        if (runtime != null) {
            runtime.destroy(true);
            // 从缓存中移除
            var effects = CACHE.get(entity);
            if (effects != null) {
                effects.remove(this);
                if (effects.isEmpty()) {
                    CACHE.remove(entity);
                }
            }
            FanChenWenDao.LOGGER.debug("停止实体 {} 上的特效", entity.getId());
        }
    }

    /**
     * 检查特效是否正在播放
     *
     * @return 是否正在播放
     */
    public boolean isPlaying() {
        return runtime != null && runtime.isAlive();
    }

    /**
     * 获取 FXRuntime（用于高级操作）
     *
     * @return FXRuntime 或 null
     */
    @Nullable
    public FXRuntime getRuntime() {
        return runtime;
    }
}
