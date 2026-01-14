package net.Lcing.fanchenwendao.client.ui.screen;

import net.Lcing.fanchenwendao.menu.PlayerPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PlayerPanelScreen extends AbstractContainerScreen<PlayerPanelMenu> {

    public PlayerPanelScreen(PlayerPanelMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void init() {
        this.imageHeight = (int) getMenu().getModularUI().getHeight();
        this.imageWidth = (int) getMenu().getModularUI().getWidth();

        super.init();

        //挂载ModularUI
        getMenu().getModularUI().setScreenAndInit(this);
        //添加渲染组件
        this.addRenderableWidget(getMenu().getModularUI().getWidget());
    }

    //渲染循环
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        //渲染物品悬停提示
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

    }

    //渲染背景层
    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {

    }

    //覆盖父类文字绘制
    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {

    }
}
