# 修仙 Mod 开发总结：Epic Fight 与 Photon 特效集成框架

## 1. 项目概述
本项目旨在 NeoForge 1.21.1 环境下，通过集成 **Epic Fight (动作系统)** 和 **Photon (特效系统)**，构建一套高表现力的修仙战斗框架。核心需求包括自定义修仙动作（如打坐）、法术状态管理以及精确的骨骼绑定特效。

---

## 2. 核心技术实现

### 2.1 动画系统 (Epic Fight)
*   **注册机制**：通过监听 `AnimationRegistryEvent`，利用 `AnimationAccessor` 动态加载 `.json` 动画文件。
*   **资源路径**：遵循标准 `assets/<modid>/animmodels/animations/` 路径规范。
*   **同步播放**：调用 `patch.playAnimationSynchronized()` 确保服务端与所有客户端动作同步。
*   **避坑指南**：
    *   **时长限制**：动画文件不能只有第 0.0s 的单帧，否则会导致插值计算 NaN 导致模型消失。建议使用 `[0.0, 1.0]` 双帧或将关键帧设在 `0.1s` 后。
    *   **骨架映射**：玩家动画必须绑定到 `Armatures.BIPED`。

### 2.2 骨骼绑定特效逻辑 (BoneExecutor)
由于 Photon 与 Epic Fight 是独立的系统，我们开发了 `BoneExecutor` 担任“搬运工”角色：
*   **原理**：每帧通过 `armature.getBoundTransformFor(pose, joint)` 获取骨骼在模型空间（Model Space）的变换矩阵。
*   **坐标转换**：
    1.  提取局部位移（`toTranslationVector`）。
    2.  应用身体旋转：利用 `OpenMatrix4f.createRotatorDeg(-yRot, Vec3f.Y_AXIS)`。
    3.  平滑插值：使用 `Mth.lerp` 处理实体坐标，确保特效移动不“掉帧”。
*   **兼容性**：针对玩家使用 `ClothSimulatable.getAccurateYRot` 获取动画修正后的旋转，针对普通生物回退到原版 `yBodyRot`。

### 2.3 法术系统与数据同步
*   **数据存储**：利用 NeoForge `Attachment` 系统创建 `FashuData`，持久化存储玩家当前选中的法术类型。
*   **网络架构**：
    *   **服务端驱动**：玩家动作（如木棍右键）触发服务端逻辑，改变 `FashuData`。
    *   **广播同步**：自定义 `SyncFashuPayload`，通过 `PacketDistributor.sendToPlayersTrackingEntityAndSelf` 将状态广播给周围所有人。
*   **意义**：这是实现“多人可见特效”的基础，确保每个客户端看到的其他玩家法术状态都是准确的。

---

## 3. 特效处理器 (Handlers) 设计
为了保持代码解耦，我们设计了两类处理器：

### 3.1 动作触发型 (`StateFXHandler`)
*   **逻辑**：盯着“动作”。
*   **场景**：打坐。只要当前播放的动画是 `SITDOWN`，胸口就出现光圈；玩家一动（切到走路动画），特效自动销毁。

### 3.2 状态持续型 (`PersistentFXHandler`)
*   **逻辑**：盯着“数据”。
*   **场景**：火焰拳。只要玩家的法术数据是 `FIREBALL_SHOOT`，右手就一直冒火，不管玩家是在跑步、跳跃还是攻击。

---

## 4. 文件结构参考
*   `client.animation.FCAnimations`: 动画注册中心。
*   `client.fx.BoneExecutor`: 核心骨骼绑定器。
*   `client.handler.StateFXHandler`: 动作关联特效管理。
*   `client.handler.PersistentFXHandler`: 状态关联特效管理。
*   `fashu.FashuHandler`: 法术业务逻辑（服务端）。
*   `network.packet.SyncFashuPayload`: 多人状态同步包。

---

## 5. 后续开发建议
1.  **多特效支持**：目前的 Map 结构已支持单个玩家身上挂载多个不同类型的特效（通过 `playerID + key` 组合）。
2.  **性能优化**：在 `onPlayerTick` 中增加距离判断，远距离玩家可以不渲染高精度骨骼绑定。
3.  **动画事件**：利用 Epic Fight 的 `InTimeEvent`，在动画播放到特定帧（如挥掌瞬间）通过网络包触发 Photon 的 `burst` 爆炸特效。

---

## 6. LDLib UI 开发与数据同步 (2026年1月12日更新)

### 6.1 功能概述
基于 LDLib (LowDragLib) 框架，实现了一套支持服务器双向同步的“修仙属性面板”。
*   **UI 架构**：采用 ModularUI (模块化 UI) + Yoga Layout (Flex 布局)。
*   **网络架构**：标准 Menu (Container) + Screen 模式，支持多人游戏数据隔离与同步。

### 6.2 核心实现细节
1.  **分层架构**：
    *   **View 层 (`PlayerPanelUI`)**：纯净的 UI 构建器，负责布局、样式 (LSS/GDP Theme) 和组件定义。
    *   **Controller 层 (`PlayerPanelMenu`)**：继承 `AbstractContainerMenu`，持有 `Player` 和 `JingJieData`，提供数据访问接口。
    *   **Render 层 (`PlayerPanelScreen`)**：继承 `AbstractContainerScreen`，负责挂载 ModularUI 并处理背景渲染。
2.  **数据绑定 (Data Binding)**：
    *   利用 `DataBindingBuilder` 实现 **JingJieData (Server) <-> ModularUI (Client)** 的自动同步。
    *   实现了 `float` 类型的进度条（修为百分比）和数值文本的实时更新。
    *   使用了 `bind(DataBindingBuilder.componentS2C)` 实现单向只读数据（如境界名称）。
3.  **交互设计**：
    *   **八卦图旋转**：通过 `image.transform()` 和 Lambda 表达式实现图片的动态旋转。
    *   **Flex 布局**：利用 `flex(1)` 实现自适应的左右布局，使界面更专业。

### 6.3 遇到的困难与解决方案
1.  **架构模式选择错误**：
    *   *问题*：初期混淆了 LDLib 的“快速工厂模式” (`PlayerUIMenuType`) 和“标准 Menu 模式”。
    *   *解决*：最终确立了严格遵循 **Tutorial 6 Method A** 的标准路线：手动编写 `Menu` 和 `Screen` 类，并在 NeoForge 注册表中注册标准 `MenuType`。
2.  **接口实现与泛型冲突**：
    *   *问题*：尝试让 Menu 继承 `ModularUIContainerMenu` 时遭遇泛型不匹配；尝试实现 `IModularUIHolderMenu` 时遇到 Mixin 方法缺失报错。
    *   *解决*：回归基础，Menu 仅继承 `AbstractContainerMenu`，通过 `instanceof` 检查和强制类型转换来触发 LDLib 的 Mixin 绑定逻辑，避开了编译器的类型检查陷阱。
3.  **渲染异常**：
    *   *问题*：背景全黑无法看清游戏世界；UI 上出现多余的“Inventory”文字。
    *   *解决*：在 Screen 类中重写 `renderBackground`（使用 `fill` 绘制自定义半透明遮罩）和 `renderLabels`（留空以禁用默认文字）。
4.  **逻辑 Bug：进度条永远满值**：
    *   *问题*：`PlayerPanelMenu.getExp()` 错误地调用了 `getMaxExperience()`。
    *   *解决*：修正为调用 `getExperience()`。
5.  **数据类型精度丢失**：
    *   *问题*：早期使用 `long` 存储修为，导致百分比计算繁琐且网络包类型不匹配。
    *   *解决*：全面重构 `JingJieData`、`JingJieHelper` 和网络包 `SyncJingJiePayload`，统一使用 `float` 类型，支持了更精确的修仙数值系统。