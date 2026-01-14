入门
如果您是一名模组开发者，想要添加 Epic Fight 兼容性，本指南将帮助您入门。

设置 Gradle 构建
要通过 Gradle 将 EpicFight 集成到您的 mod 项目中并启用自动下载，请添加 Modrinth Maven 仓库。这样您就可以直接在您的build.gradle（或build.gradle.kts）中将 EpicFight mod 声明为依赖项。

添加 Modrinth Maven 仓库
NeoForge 1.21.1
Forge 1.20.1
提示

存储库是 Gradle 项目使用 Maven 风格的坐标获取库的存储位置（group:artifact:version）。

您也可以使用Curse Forge 仓库代替Modrinth 仓库。

添加 Epic Fight 模组依赖
NeoForge 1.21.1
Forge 1.20.1
如何选择版本……
要查看所有可用的 EpicFight 版本，请参阅Modrinth上的列表。

提示

为了更轻松地设置依赖项，您可以点击 Modrinth 上想要使用的版本，然后将版本号或版本 ID复制为 Epic Fight 版本。

例如，21.12.5；然后将其包含在gradle.properties：


epicfight_version=21.12.5
Modrinth教程
Forge / NeoForge 活动
史诗级战斗活动包（按版本划分）：

版本	API 路径
1.20.1（Forge）	epicfight/api/forgeevent
1.21.1（NeoForge）	epicfight/api/neoevent
提示

以上路径已简化。实际的包根目录是 `/etc/package.json` yesman/epicfight/api/...，但为了清晰起见，我们将其写成 `/etc/package.json` epicfight/api/...。

注册自定义动画
您可以下载Epic Fight 玩家动画绑定，并在[Blender]
中使用它来创建 Epic Fight 玩家的动画。完成后，使用Epic Fight Blender Exporter 插件导出动画。

使用Epic Fight Player Animation Rig制作的动画可以应用于任何双足（人形）实体。
但是，它们与非人形实体（例如末影龙）不兼容。

提示

更多信息，请参阅
从 Blender 2.79 开始。

要将动画导入到您的模组/资源包中，请按照资源导入页面上的说明进行操作。

以下是注册自定义动画的示例：


import yesman.epicfight.api.animation.AnimationManager.AnimationRegistryEvent;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;

@EventBusSubscriber(modid = YourMod.MOD_ID)
public class Animations {
@SubscribeEvent
public static void registerAnimations(AnimationRegistryEvent event) {
event.newBuilder(YourMod.MOD_ID, Animations::build);
}

    // Animation accessors for different animation types
    public static AnimationAccessor<StaticAnimation> BIPED_IDLE;
    public static AnimationAccessor<MovementAnimation> BIPED_WALK;
    public static AnimationAccessor<StaticAnimation> BIPED_FLYING;

    public static AnimationAccessor<ComboAttackAnimation> TRIDENT_AUTO1;
    public static AnimationAccessor<ComboAttackAnimation> TRIDENT_AUTO2;
    public static AnimationAccessor<ComboAttackAnimation> TRIDENT_AUTO3;

    // Define the actual animations and their properties
    private static void build(AnimationManager.AnimationBuilder builder) {
        ArmatureAccessor<HumanoidArmature> armatureAccessor = Armatures.BIPED;

        BIPED_IDLE = builder.nextAccessor("biped/living/idle", (accessor) -> new StaticAnimation(true, accessor, armatureAccessor));
        BIPED_WALK = builder.nextAccessor("biped/living/walk", (accessor) -> new MovementAnimation(true, accessor, armatureAccessor));
        BIPED_FLYING = builder.nextAccessor("biped/living/fly", (accessor) -> new StaticAnimation(true, accessor, armatureAccessor));

        TRIDENT_AUTO1 = builder.nextAccessor("biped/combat/trident_auto1", (accessor) -> new ComboAttackAnimation(0.3F, 0.05F, 0.16F, 0.45F, null, armatureAccessor.get().toolR, accessor, armatureAccessor));
        TRIDENT_AUTO2 = builder.nextAccessor("biped/combat/trident_auto2", (accessor) -> new ComboAttackAnimation(0.05F, 0.25F, 0.36F, 0.55F, null, armatureAccessor.get().toolR, accessor, armatureAccessor));
        TRIDENT_AUTO3 = builder.nextAccessor("biped/combat/trident_auto3", (accessor) -> new ComboAttackAnimation(0.2F, 0.3F, 0.46F, 0.9F, null, armatureAccessor.get().toolR, accessor, armatureAccessor));
    }
}
所有可用的动画类型都列在epicfight/api/animation/types中。

提示

本教程假设您已了解如何在 Forge/NeoForge 中注册事件。
上述示例仅适用于NeoForge。

在Forge上，你需要使用@Mod.EventBusSubscriber而不是@EventBusSubscriber
并明确指定总线——在这种情况下，它必须是MOD 总线。

播放动画
在对实体播放 Epic Fight 动画之前，必须先使用 Epic Fight 对该实体进行修补。
（有关如何修补实体的详细信息，请参见下一节。）

以下示例展示了当玩家右键点击已打上 Epic Fight 补丁的实体时，如何播放该实体的静态跳跃动画：


public class YourEntity extends PathfinderMob {
// ...

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        final boolean isEpicFightModLoaded = ModList.get().isLoaded("epicfight");
        if (isEpicFightModLoaded) {
            final LivingEntityPatch<?> entityPatch = EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
            entityPatch.playAnimationInstantly(Animations.BIPED_JUMP);
        }
        return super.mobInteract(player, hand);
    }
}
请确保在mods.toml文件中将Epic Fight声明为必需依赖项，或者像上面那样在运行时检查它是否存在。这样可以防止因模组未安装而导致的崩溃。

修补自定义实体
本教程演示如何为 Epic Fight 修改自定义人形实体。
它假设您已经在原版 Minecraft 中设置好了该实体，包括其注册信息、渲染器和属性。

您需要注册三项不同的内容：

已修补的实体。
该实体的骨架类型。
已打补丁的渲染器。
修补实体
一个已修补的实体类示例：


public class YourEntityPatch extends HumanoidMobPatch<YourEntity> {

    public YourEntityPatch(YourEntity original) {
        super(original, Factions.VILLAGER);
    }

    @Override
    public void updateMotion(boolean b) {
        super.commonMobUpdateMotion(b);
    }

    @Override
    protected void initAI() {
        super.initAI();

        this.original.goalSelector.addGoal(
                1,
                new AnimatedAttackGoal<>(this, new CombatBehaviors.Builder<>().build(this))
        );
        this.original.goalSelector.addGoal(2, new TargetChasingGoal(this, this.getOriginal(), 1.2f, true));
        this.original.goalSelector.addGoal(3, new RandomStrollGoal(original, 1.0f));

        this.original.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(original, Player.class, true));
    }

    public void initAnimator(Animator animator) {
        super.initAnimator(animator);

        // All available living motions are listed in this enum: https://github.com/Epic-Fight/epicfight/blob/1.21.1/src/main/java/yesman/epicfight/api/animation/LivingMotions.java#L4-L6
        animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
        animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
        animator.addLivingAnimation(LivingMotions.RUN, Animations.BIPED_RUN);
        animator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
        animator.addLivingAnimation(LivingMotions.SIT, Animations.BIPED_SIT);
        animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
        animator.addLivingAnimation(LivingMotions.JUMP, Animations.BIPED_JUMP);
        animator.addLivingAnimation(LivingMotions.SLEEP, Animations.BIPED_SLEEPING);
        animator.addLivingAnimation(LivingMotions.AIM, Animations.BIPED_BOW_AIM);
        animator.addLivingAnimation(LivingMotions.SHOT, Animations.BIPED_BOW_SHOT);
        animator.addLivingAnimation(LivingMotions.DRINK, Animations.BIPED_DRINK);
        animator.addLivingAnimation(LivingMotions.EAT, Animations.BIPED_EAT);
    }
}
然后，注册你修补过的实体，EntityPatchRegistryEvent并注册它的骨架，以避免运行时崩溃：


@EventBusSubscriber(modid = YourMod.MOD_ID)
public class YourModEvents {
@SubscribeEvent
public static void registerPatchedEntities(EntityPatchRegistryEvent event) {
event.getTypeEntry().put(YourModEntities.THE_ENTITY.get(), entity -> new YourEntityPatch((YourEntity) entity));
}

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(YourModEvents::registerEntityTypeArmatures);
    }

    private static void registerEntityTypeArmatures() {
        Armatures.registerEntityTypeArmature(YourModEntities.THE_ENTITY.get(), Armatures.BIPED);
    }
}
提示

在 Forge 1.20.1 中，您必须手动指定MOD总线EntityPatchRegistryEvent。

修补渲染器
一个已修补的实体渲染器示例


public class YourEntityPatchRenderer extends PHumanoidRenderer<YourEntity, YourEntityPatch, HumanoidModel<YourEntity>, YourEntityRenderer, HumanoidMesh> {
public DummyEntityRendererEfPatch(EntityRendererProvider.Context context, EntityType<?> entityType) {
super(Meshes.BIPED, context, entityType);
}
}
然后，仅使用MOD 总线PatchedRenderersEvent.Add上的事件在客户端注册它：


@EventBusSubscriber(modid = YourMod.MOD_ID, value = Dist.CLIENT)
public class EpicFightClientEvents {
@SubscribeEvent
public static void registerPatchedEntityRenderers(PatchedRenderersEvent.Add event) {
event.addPatchedEntityRenderer(YourModEntities.THE_ENTITY.get(), entityType -> new YourEntityPatchRenderer(
event.getContext(),
entityType
)
);
}
}
获取已修补的实体
您可以使用以下方法从原始实体中检索已修补的实体实例：


final YourEntity entity = ...;
final YourEntityPatch entityPatch = EpicFightCapabilities.getEntityPatch(entity, YourEntityPatch.class);
注册自定义技能槽
本示例解释了如何通过模组添加新的技能槽并在自定义技能中使用它们来扩展Epic Fight 。

首先，定义一个枚举类型，用于声明你的模组为《史诗战斗》引入的额外技能槽位。
每个枚举常量代表一个不同的槽位——例如，一个额外的被动技能槽位或身份技能槽位：


public enum YourModSkillSlots implements SkillSlot {
PASSIVE4(SkillCategories.PASSIVE),
PASSIVE5(SkillCategories.PASSIVE),
IDENTITY2(SkillCategories.IDENTITY),
;

    final SkillCategory category;
    final int id;

    YourModSkillSlots(SkillCategory category) {
        this.category = category;
        id = SkillSlot.ENUM_MANAGER.assign(this);
    }

    @Override
    public SkillCategory category() {
        return category;
    }

    @Override
    public int universalOrdinal() {
        return this.id;
    }
}
请确保枚举名称全局唯一，且不与其他模组共享。
如果两个模组定义了同名槽位（例如 `<slot_name> Passive5`），游戏会在启动时因冲突而崩溃。此外，请避免使用已有的史诗战斗技能槽位名称来命名枚举，
因为这些内置槽位始终可用，不应重新定义。

定义枚举后，将其注册到你的模组构造函数中，以便 Epic Fight 能够识别新的槽位：


@Mod(YourMod.MOD_ID)
public class YourMod {
public static final String MOD_ID = "your_mod_id";

    public YourMod() {
        SkillSlot.ENUM_MANAGER.registerEnumCls(MOD_ID, MoreSkillSlots.class);
    }
}
接下来，在文件中为这些槽位添加翻译assets/your_mod_id/lang/en_us.json。
这些条目控制槽位名称在游戏界面中的显示方式。


{
"epicfight.skill_slot.passive4": "Passive 4",
"epicfight.skill_slot.passive5": "Passive 5",
"epicfight.skill_slot.identity2": "Identity 2"
}
完成后，启动游戏以验证自定义技能槽是否已正确集成。``
然后，您可以将技能分配到这些新槽位，并在您的模组功能中使用它们。

有关注册技能类别或实施自定义技能槽位的更多信息，请参阅软件包
yesman.epicfight.skill。