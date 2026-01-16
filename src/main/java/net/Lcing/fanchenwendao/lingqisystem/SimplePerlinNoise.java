package net.Lcing.fanchenwendao.lingqisystem;

/**
 * 简单的2D柏林噪声实现 (Gradient Noise)
 * 用于生成平滑的连续随机数值
 */
public class SimplePerlinNoise {

    /**
     * 计算2D噪声值
     * @param x x坐标
     * @param y z坐标 (在Minecraft中通常使用x, z作为2D平面)
     * @param seed 世界种子，用于打乱哈希表
     * @return 范围通常在 [-1.0, 1.0] 之间的噪声值
     */
    public static double noise(double x, double y, long seed) {
        // 找到输入点所在的单位方块的左下角坐标 (向下取整)
        int X = floor(x) & 255;
        int Y = floor(y) & 255;

        // 获取点在单位方块内的相对位置 (0.0 ~ 1.0)
        x -= floor(x);
        y -= floor(y);

        // 计算平滑曲线值 (Fade)，用于平滑插值，消除方块感
        double u = fade(x);
        double v = fade(y);

        // 哈希计算：结合坐标和种子，算出方块4个顶点的伪随机哈希值
        // 这些哈希值决定了每个顶点的梯度向量方向
        int A = p(X, seed) + Y;
        int AA = p(A, seed);
        int AB = p(A + 1, seed);
        int B = p(X + 1, seed) + Y;
        int BA = p(B, seed);
        int BB = p(B + 1, seed);

        // 梯度混合 (Lerp)：
        // 1. 计算4个顶点的梯度值 grad()
        // 2. 利用平滑曲线值 u, v 进行双线性插值
        return lerp(v, lerp(u, grad(p(AA, seed), x, y), grad(p(BA, seed), x - 1, y)),
                       lerp(u, grad(p(AB, seed), x, y - 1), grad(p(BB, seed), x - 1, y - 1)));
    }

    // 改进的平滑函数 (Ken Perlin's Improved Fade Function): 6t^5 - 15t^4 + 10t^3
    private static double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    // 线性插值函数
    private static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    // 梯度函数：根据哈希值的低4位，从12个特定方向中选一个，并计算点积
    private static double grad(int hash, double x, double y) {
        int h = hash & 15;
        // 将哈希值转换为梯度向量 (u, v)
        // 这里的逻辑是从12条边中选一条作为梯度方向
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : 0;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    // 向下取整 (比 Math.floor 快一点的写法)
    private static int floor(double x) {
        int xi = (int)x;
        return x < xi ? xi - 1 : xi;
    }

    // 伪随机哈希/置换函数
    // 传统的Perlin噪声使用预计算的 permutation[] 数组 (512 ints)
    // 这里为了支持任意 seed 且不消耗内存，使用简单的位运算混淆算法代替查表
    private static int p(int i, long seed) {
        long hash = i + seed;
        hash ^= (hash << 13);
        hash ^= (hash >>> 17);
        hash ^= (hash << 5);
        return (int) (hash & 255);
    }
}
