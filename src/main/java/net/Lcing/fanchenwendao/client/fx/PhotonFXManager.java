package net.Lcing.fanchenwendao.client.fx;

import com.lowdragmc.photon.client.fx.BlockEffectExecutor;
import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.Lcing.fanchenwendao.FanChenWenDao;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Photon2 特效管理器
 * <p>
 * 封装 Photon2 API，提供简单的特效播放接口
 * <p>
 * 注意：Photon2 的内置 Executor（BlockEffectExecutor / EntityEffectExecutor）
 * 会自动管理特效的生命周期，特效播放完毕后会自动消失。
 * 如需手动控制特效停止，请使用 CustomFXExecutor 自定义实现。
 */
public class PhotonFXManager {

    // 缓存已加载的 FX 模板，避免重复加载
    private static final Map<ResourceLocation, FX> fxCache = new HashMap<>();

    /**
     * 获取特效模板（带缓存）
     *
     * @param fxPath 特效路径，例如 "fire" 对应 assets/fanchenwendao/fx/fire.fx
     * @return FX对象，如果不存在返回null
     */
    @Nullable
    public static FX getFX(String fxPath) {
        if (fxPath.contains(":")) {
            return getFX(ResourceLocation.parse(fxPath));
        }
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(FanChenWenDao.MODID, fxPath);
        return getFX(id);
    }

    /**
     * 获取特效模板（带缓存）
     *
     * @param id 完整的ResourceLocation
     * @return FX对象，如果不存在返回null
     */
    @Nullable
    public static FX getFX(ResourceLocation id) {
        if (fxCache.containsKey(id)) {
            return fxCache.get(id);
        }

        FX fx = FXHelper.getFX(id);
        if (fx != null) {
            fxCache.put(id, fx);
        }
        return fx;
    }

    // ==================== 实体特效 ====================

    /**
     * 在实体上播放特效
     * <p>
     * 特效会跟随实体移动，播放完毕后自动消失
     *
     * @param entity 目标实体
     * @param fxPath 特效路径 (例如 "sword_slash")
     * @return 是否成功播放
     */
    public static boolean playOnEntity(Entity entity, String fxPath) {
        return playOnEntity(entity, fxPath, EntityEffectExecutor.AutoRotate.NONE);
    }

    /**
     * 在实体上播放特效（带自动旋转）
     *
     * @param entity     目标实体
     * @param fxPath     特效路径
     * @param autoRotate 自动旋转模式 (NONE / YAW / YAW_PITCH)
     * @return 是否成功播放
     */
    public static boolean playOnEntity(Entity entity, String fxPath, EntityEffectExecutor.AutoRotate autoRotate) {
        FX fx = getFX(fxPath);
        if (fx == null || entity == null) {
            FanChenWenDao.LOGGER.warn("无法播放特效: {} - FX或实体为null", fxPath);
            return false;
        }

        // 创建并启动特效，会自动跟随实体
        new EntityEffectExecutor(fx, entity.level(), entity, autoRotate).start();
        FanChenWenDao.LOGGER.debug("在实体 {} 上播放特效: {}", entity.getId(), fxPath);
        return true;
    }

    /**
     * 在实体上播放特效（使用完整ResourceLocation）
     *
     * @param entity 目标实体
     * @param fxId   特效的完整ResourceLocation (例如 "photon:fire")
     * @return 是否成功播放
     */
    public static boolean playOnEntity(Entity entity, ResourceLocation fxId) {
        FX fx = getFX(fxId);
        if (fx == null || entity == null) {
            return false;
        }

        new EntityEffectExecutor(fx, entity.level(), entity, EntityEffectExecutor.AutoRotate.NONE).start();
        return true;
    }

    // ==================== 方块特效 ====================

    /**
     * 在方块位置播放特效
     * <p>
     * 特效会固定在该位置，播放完毕后自动消失
     *
     * @param level  世界
     * @param pos    方块位置
     * @param fxPath 特效路径
     * @return 是否成功播放
     */
    public static boolean playOnBlock(Level level, BlockPos pos, String fxPath) {
        FX fx = getFX(fxPath);
        if (fx == null) {
            FanChenWenDao.LOGGER.warn("无法播放特效: {} - FX为null", fxPath);
            return false;
        }

        new BlockEffectExecutor(fx, level, pos).start();
        FanChenWenDao.LOGGER.debug("在方块 {} 上播放特效: {}", pos, fxPath);
        return true;
    }

    /**
     * 在方块位置播放特效（使用完整ResourceLocation）
     *
     * @param level 世界
     * @param pos   方块位置
     * @param fxId  特效的完整ResourceLocation
     * @return 是否成功播放
     */
    public static boolean playOnBlock(Level level, BlockPos pos, ResourceLocation fxId) {
        FX fx = getFX(fxId);
        if (fx == null) {
            return false;
        }

        new BlockEffectExecutor(fx, level, pos).start();
        return true;
    }

    // ==================== 工具方法 ====================

    /**
     * 清除特效缓存（用于资源重载时）
     */
    public static void clearCache() {
        fxCache.clear();
        FanChenWenDao.LOGGER.info("已清除特效缓存");
    }

    /**
     * 检查特效是否存在
     *
     * @param fxPath 特效路径
     * @return 是否存在
     */
    public static boolean exists(String fxPath) {
        return getFX(fxPath) != null;
    }
}
