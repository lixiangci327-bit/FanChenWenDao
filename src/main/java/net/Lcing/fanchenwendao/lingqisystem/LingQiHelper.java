package net.Lcing.fanchenwendao.lingqisystem;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

//灵气辅助类
public class LingQiHelper {

    //根据群系计算基础灵气值
    public static float getBaseLingQi(Holder<Biome> biome) {

        //1.高山/深海——300.0
        if (biome.is(Biomes.JAGGED_PEAKS) || biome.is(Biomes.DEEP_OCEAN)) {
            return 300.0f;
        }

        //2.蘑菇岛/原始松木林——600.0
        if (biome.is(Biomes.MUSHROOM_FIELDS) || biome.is(Biomes.OLD_GROWTH_PINE_TAIGA)) {
            return 600.0f;
        }

        //3.荒地/沙漠/地狱——20.0
        if (biome.is(Biomes.DESERT) || biome.is(Biomes.BADLANDS) || biome.is(Biomes.NETHER_WASTES)) {
            return 20.0f;
        }

        //其余默认
        return 100.0f;  //TODO；对群系base进行更细分
    }


    //计算上限maxLingQi：群系基础 * 噪声系数
    public static float calculatemaxLingQi(Level level, BlockPos pos) {
        //获取当前群系
        Holder<Biome> biome = level.getBiome(pos);
        float baseLingQi = getBaseLingQi(biome);

        //噪声修正
        //暂时使用随机数模拟。TODO：使用2D柏林噪声
        double noise = 0.8 + Math.random() * 0.4;

        return (float) (baseLingQi * noise);
    }
}
