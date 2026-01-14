package net.Lcing.fanchenwendao.client.ui.ldlib;


import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ProgressBar;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import net.Lcing.fanchenwendao.menu.PlayerPanelMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.appliedenergistics.yoga.YogaFlexDirection;



public class PlayerPanelUI {

    public static ModularUI buildUI(Player player, PlayerPanelMenu menu) {

        //创建根节点
        var root = new UIElement();

        //修为数据
        var experience = DataBindingBuilder.floatVal(
                menu::getExp,   //getter
                menu::setExp
        ).build();

        //图片
        var image = new UIElement()
                //使用lss
                .lss("width", 50)
                .lss("height", 50)
                .lss("background", "sprite(fanchenwendao:textures/gui/bagua.png)");

        //进度条(只读)
        var progressBinding = DataBindingBuilder.floatVal(
                () -> {
                    float current = menu.getExp();
                    float max = menu.getMaxExp();
                    //防止除以0崩溃
                    if (max <= 0) return 0f;
                    return current / max;
                },
                val -> {}   //进度条不可拖拽，setter为空
        ).build();

        //修为显示
        var expshow = new UIElement()
                .layout(l -> l.flexDirection(YogaFlexDirection.ROW).gapAll(5))
                .addChildren(
                        //修为展示
                        new Label().bind(DataBindingBuilder.componentS2C(() -> {
                            float current = menu.getExp();
                            float max = menu.getMaxExp();
                            return Component.literal(String.format("修为：%.1f / %.1f", current, max));
                        }).build())
                );

        //自定义按钮
        var buttonContainer = new UIElement()
                .layout(layout -> layout.flexDirection(YogaFlexDirection.ROW).gapAll(5))    //横向排列，间距5
                .addChildren(
                        new Button().setText("-45°")
                                .setOnClick(event -> {
                                    //点击逻辑事件
                                    image.transform(t -> t.rotation(t.rotation() - 45));
                                }),
                        new Button().setText("+45°")
                                .setOnClick(event -> {
                                    image.transform(t -> t.rotation(t.rotation() + 45));
                                })
                );

        //添加子元素
        root.addChildren(

                //标签
                new Label().setText("修仙面板")
                        .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER)),  //文本水平居中

                //按钮
                new Button().setText("踏入仙途"),
                image,
                buttonContainer,


                new Label().bind(DataBindingBuilder.componentS2C(() ->
                        Component.literal("境界：凝气" + menu.getLevel() + "层")
                ).build()),

                expshow,

                new ProgressBar()
                        .bind(progressBinding)
                        .layout(l -> l.height(10).widthPercent(100)),

                //玩家背包槽位
                new InventorySlots()


        );

        //根节点布局
        //padding-上下左右留出7像素的空白；gapadd-里面的子元素都自动隔开5像素
        root.layout(layout -> layout.paddingAll(7).gapAll(5));

        root.style(s -> s.background(Sprites.BORDER));

        //打包为UI对象
        var ui = UI.of(root, StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.GDP));

        //创建并返回ModularUI
        return ModularUI.of(ui, player);
    }

}
