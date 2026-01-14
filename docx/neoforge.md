这是NeoForged 1.21 - 1.21.1的文档，该版本已停止维护。
有关最新文档，请参阅最新版本（1.21.9 - 1.21.10 ）。
概念活动
版本：1.21 - 1.21.1
活动
NeoForge 的主要特性之一是事件系统。游戏中发生的各种事件都会触发事件。例如，玩家右键点击、玩家或其他实体跳跃、方块渲染、游戏加载等等都会触发事件。模组制作者可以为每个事件订阅事件处理程序，并在这些事件处理程序中执行所需的行为。

事件会在各自的事件总线上触发。最重要的总线是游戏总线NeoForge.EVENT_BUS，也称为游戏主总线。此外，在启动过程中，会为每个已加载的模组生成一个模组总线，并将其传递给模组的构造函数。许多模组总线事件并行触发（与始终在同一线程上运行的主总线事件不同），从而显著提高启动速度。更多信息请参见下文。

注册事件
注册事件处理程序的方法有很多种。所有这些方法的共同点是，每个事件处理程序都是一个带有单个事件参数且没有结果（即返回类型void）的方法。

IEventBus#addListener
注册方法处理程序的最简单方法是注册它们的方法引用，如下所示：

@Mod("yourmodid")
public class YourMod {
public YourMod(IEventBus modBus) {
NeoForge.EVENT_BUS.addListener(YourMod::onLivingJump);
}

    // Heals an entity by half a heart every time they jump.
    private static void onLivingJump(LivingJumpEvent event) {
        Entity entity = event.getEntity();
        // Only heal on the server side
        if (!entity.level().isClientSide()) {
            entity.heal(1);
        }
    }
}

@SubscribeEvent
或者，可以通过创建事件处理程序方法并使用 `@EventHandler` 注解来驱动事件处理程序@SubscribeEvent。然后，您可以将包含类的实例传递给事件总线，注册@SubscribeEvent该实例的所有带有 `@EventHandler` 注解的事件处理程序：

public class EventHandler {
@SubscribeEvent
public void onLivingJump(LivingJumpEvent event) {
Entity entity = event.getEntity();
if (!entity.level().isClientSide()) {
entity.heal(1);
}
}
}

@Mod("yourmodid")
public class YourMod {
public YourMod(IEventBus modBus) {
NeoForge.EVENT_BUS.register(new EventHandler());
}
}

你也可以采用静态方式。只需将所有事件处理程序设为静态，然后传入类本身而不是类实例即可：

public class EventHandler {
@SubscribeEvent
public static void onLivingJump(LivingJumpEvent event) {
Entity entity = event.getEntity();
if (!entity.level().isClientSide()) {
entity.heal(1);
}
}
}

@Mod("yourmodid")
public class YourMod {
public YourMod(IEventBus modBus) {
NeoForge.EVENT_BUS.register(EventHandler.class);
}
}

@EventBusSubscriber
最新的
[21.1.0,21.1.180]
我们还可以更进一步，@EventBusSubscriber使用 `@EventHandler` 注解来标记事件处理程序类。NeoForge 会自动识别此注解，从而允许您从模组构造函数中移除所有与事件相关的代码。本质上，这相当于在模组构造函数的末尾调用 `@EventHandler`NeoForge.EVENT_BUS.register(EventHandler.class)和modBus.register(EventHandler.class)`@EventHandler`。这意味着所有处理程序也必须是静态的。

虽然并非必须，但强烈建议modid在注释中指定参数，以便于调试（尤其是在模块冲突的情况下）。

@EventBusSubscriber(modid = "yourmodid")
public class EventHandler {
@SubscribeEvent
public static void onLivingJump(LivingJumpEvent event) {
Entity entity = event.getEntity();
if (!entity.level().isClientSide()) {
entity.heal(1);
}
}
}

活动
字段和
字段和方法可能是事件中最显而易见的部分。大多数事件都包含事件处理程序需要使用的上下文信息，例如触发事件的实体或事件发生的层级。

为了利用继承的优势，某些事件并非直接继承自Event某个类，而是继承自其子类，例如BlockEvent`BlockContext`（包含块相关事件的块上下文）或EntityEvent`EntityContext`（类似地包含实体上下文），以及其子类 `EntityContext` LivingEvent（用于LivingEntity特定上下文）和`EntityContext` PlayerEvent（用于Player特定上下文）。这些提供上下文的父事件是abstract不可监听的。

危险
如果你监听abstract事件本身，游戏会崩溃，因为这绝不是你想要的结果。你应该始终监听子事件。

可取消
某些事件实现了该ICancellableEvent接口。这些事件可以使用 `cancel()` 方法取消#setCanceled(boolean canceled)，并且可以使用 `check()` 方法检查取消状态#isCanceled()。如果事件被取消，则该事件的其他事件处理程序将不会运行，并且会启用与“取消”相关的某些行为。例如，取消事件LivingJumpEvent将阻止跳转。

事件处理程序可以选择显式接收已取消的事件。这可以通过receiveCanceled将布尔参数IEventBus#addListener（或@SubscribeEvent，取决于您附加事件处理程序的方式）设置为 true 来实现。

三州联盟及
有些事件有三种可能的返回状态，分别用 `return` TriState、`return` 或Result事件类上的枚举类型表示。返回状态通常可以取消事件正在处理的操作（`cancel` TriState#FALSE）、强制执行操作（`force` TriState#TRUE）或执行默认的 Vanilla 行为（`execute` TriState#DEFAULT）。

具有三种可能返回状态的事件可以通过某种set*方法来设定期望的结果。

// In some event handler class

@SubscribeEvent // on the game event bus
public static void renderNameTag(RenderNameTagEvent event) {
// Uses TriState to set the return state
event.setCanRender(TriState.FALSE);
}

@SubscribeEvent // on the game event bus
public static void mobDespawn(MobDespawnEvent event) {
// Uses a Result enum to set the return state
event.setResult(MobDespawnEvent.Result.DENY);
}

事件处理程序可以选择性地分配优先级。EventPriority枚举值包含五个：HIGHEST`A`、 ` HIGHB`、NORMAL`C`（默认值）LOW和LOWEST`D`。事件处理程序按优先级从高到低执行。如果优先级相同，则它们在主总线上按注册顺序触发，这大致与模组加载顺序相关；在模组总线上则完全按照模组加载顺序触发（见下文）。

可以通过设置 `on`或 ` priorityon` 中的参数来定义优先级，具体取决于您如何附加事件处理程序。请注意，对于并行触发的事件，优先级将被忽略。IEventBus#addListener@SubscribeEvent

边
有些事件只会在客户端触发。常见的例子包括各种渲染事件，这些事件只会在客户端触发。由于客户端专属事件通常需要访问 Minecraft 代码库中其他客户端专属部分，因此需要进行相应的注册。

使用事件处理程序应通过或您创建的 mod 构造函数中的参数IEventBus#addListener()检查当前的物理侧，并在单独的仅客户端类中添加监听器，如关于sides的文章中所述。FMLEnvironment.distDist

使用事件处理程序@EventBusSubscriber可以将 side 指定为value注解的参数，例如@EventBusSubscriber(value = Dist.CLIENT, modid = "yourmodid")。

活动
虽然大多数事件都发布在主平台上NeoForge.EVENT_BUS，但有些事件会发布在模组事件总线上。这些事件通常被称为模组总线事件。模组总线事件可以通过它们的超级接口与常规事件区分开来IModBusEvent。

最新的
[21.1.0,21.1.180]
模组事件总线作为参数传递给模组构造函数，然后您可以将模组总线事件订阅到该总线。如果您使用 `@get_event_bus()` @EventBusSubscriber，事件将自动订阅到正确的总线。

模组
大多数模组总线事件都是所谓的生命周期事件。生命周期事件在每个模组的生命周期中启动时运行一次。许多事件会通过子类化并行触发ParallelDispatchEvent；如果您想在主线程上运行这些事件中的代码，请使用将其加入队列#enqueueWork(Runnable runnable)。

生命周期通常遵循以下顺序：

模块构造函数已被调用。请在此处或下一步注册您的事件处理程序。
所有的@EventBusSubscribers 都被调用。
FMLConstructModEvent被解雇了。
注册表事件被触发，其中包括NewRegistryEvent，DataPackRegistryEvent.NewRegistry并且对于每个注册表，RegisterEvent。
FMLCommonSetupEvent启动。接下来会进行各种其他设置。
如果在物理客户端上，则触发侧向设置；如果在物理服务器上，则触发侧向设置。FMLClientSetupEventFMLDedicatedServerSetupEvent
InterModComms处理方式如下。
FMLLoadCompleteEvent被解雇了。
InterModComms
InterModComms这是一个允许模组制作者向其他模组发送消息以实现兼容性功能的系统。该类保存模组消息，所有方法都是线程安全的。该系统主要由两个事件驱动：InterModEnqueueEvent和InterModProcessEvent。

在开发过程中InterModEnqueueEvent，您可以使用InterModComms#sendTo这些方法向其他模组发送消息。这些方法接受以下参数：要发送消息的模组 ID、与消息数据关联的键（用于区分不同的消息）以及Supplier包含消息数据的数组。发送者也可以选择性地指定。

然后，在处理过程中InterModProcessEvent，您可以使用它InterModComms#getMessages来获取所有已接收消息的对象流IMCMessage。这些对象包含数据的发送者、数据的预期接收者、数据键以及实际数据的提供者。

其他改装巴士
除了生命周期事件之外，模组事件总线上还会触发一些其他事件，这主要是出于历史原因。这些事件通常用于注册、设置或初始化各种对象。与生命周期事件不同，这些事件大多不会并行运行。以下是一些示例：

RegisterColorHandlersEvent
ModelEvent.BakingCompleted
TextureAtlasStitchedEvent