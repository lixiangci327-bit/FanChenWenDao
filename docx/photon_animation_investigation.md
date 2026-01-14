# Photon 模组动画系统调查报告

根据对 Photon 模组源代码的分析，特别是 `examples\Photon-1.21` 目录下的代码，以下是关于如何控制和实现特效动画的调查结果。

## 1. 核心控制接口：IEffectExecutor

这是实现自定义动画逻辑最直接的方式。正如 `集成.md` 中提到的，通过实现 `IEffectExecutor` 接口，你可以介入特效对象的更新循环。

*   **文件位置**: `com.lowdragmc.photon.client.fx.IEffectExecutor.java`
*   **工作机制**:
    *   特效的基类 `FXObject` 会将其更新逻辑委托给 `IEffectExecutor`。
    *   **`updateFXObjectTick(IFXObject fxObject)`**: 每 tick (1/20秒) 调用一次。适用于低频逻辑，例如检查特效是否应该结束、状态切换等。
    *   **`updateFXObjectFrame(IFXObject fxObject, float partialTicks)`**: 每一帧渲染时调用。适用于高频平滑动画，例如插值更新位置、旋转或缩放。

**应用场景**:
如果你需要特效跟随实体、响应游戏内事件或执行复杂的路径逻辑，应自定义 `IEffectExecutor`。

## 2. 基于数学表达式的动画：Function Shape

在调查中发现了一个强大的功能类 `Function`，它允许通过字符串形式的数学表达式来定义粒子的运动轨迹，无需为每个效果编写 Java 代码。

*   **文件位置**: `src\main\java\com\lowdragmc\photon\client\gameobject\emitter\data\shape\Function.java`
*   **功能**:
    *   使用模组内置的表达式解析器 (`src\main\java\expr`)。
    *   可以分别定义 **位置 (Position)** (`x`, `y`, `z`) 和 **速度 (Velocity)** (`speedX`, `speedY`, `speedZ`) 的公式。
*   **可用变量**:
    *   `t`: 归一化的生命周期时间 (0.0 到 1.0)。
    *   `PI`: 圆周率。
    *   `randomA` 到 `randomE`: 5个随机变量，用于产生随机变化。
*   **示例**:
    *   设置 `y = "sin(t * PI * 2)"` 可以让粒子在生命周期内做正弦波动的上下运动。

**应用场景**:
适用于纯视觉的轨迹控制，如螺旋、波浪、函数曲线等几何形态的特效。

## 3. 关键类参考

| 类名 | 位置 | 作用 |
| :--- | :--- | :--- |
| **FXObject** | `client\gameobject\FXObject.java` | 特效基类，继承自 `Particle`。包含 `tick()` 和 `render()` 方法，并将其委托给 Executor。 |
| **IEffectExecutor** | `client\fx\IEffectExecutor.java` | 接口，用于管理特效生命周期和每帧/每 tick 的更新。 |
| **Emitter** | `client\gameobject\emitter\Emitter.java` | 发射器类，处理粒子生成。包含变量 `t` (0-1) 表示生命周期进度。 |
| **Function** | `client\gameobject\emitter\data\shape\Function.java` | `IShape` 的实现，展示了如何利用数学表达式系统驱动粒子运动。 |

## 4. 总结

*   对于**程序化、逻辑复杂**的控制（如游戏机制交互），请实现 `IEffectExecutor`。
*   对于**视觉化、数学规律**的运动（如特定形状、轨迹），利用 `Function` 形状和表达式系统会更高效且易于配置。
