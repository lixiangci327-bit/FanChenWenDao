package net.Lcing.fanchenwendao.menu;

import com.lowdragmc.lowdraglib2.gui.holder.IModularUIHolderMenu;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import net.Lcing.fanchenwendao.client.ui.ldlib.PlayerPanelUI;
import net.Lcing.fanchenwendao.jingjie.JingJieData;
import net.Lcing.fanchenwendao.jingjie.JingJieHelper;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.Lcing.fanchenwendao.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;


public class PlayerPanelMenu extends AbstractContainerMenu {

    private final ModularUI modularUI;

    private final Player player;

    //构造函数
    //containerId-窗口的id；playerInventory-玩家背包，拿到玩家对象player
    public PlayerPanelMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.PLAYER_PANEL_MENU.get(), containerId);

        this.player = playerInventory.player;

        //调用PlayerPanelUI的方法，传入player对象，生成一套完整的UI树
        this.modularUI = PlayerPanelUI.buildUI(playerInventory.player, this);

        //将UI绑定到菜单容器上
        if (this instanceof IModularUIHolderMenu holder) {
            holder.setModularUI(this.modularUI);
        }

    }

    public ModularUI getModularUI() {
        return modularUI;
    }

    //处理shift+点击。该动作容易崩服，暂时返回empty
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    //检查玩家是否还能打开菜单
    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

    //境界数据处理
    //获取player境界数据
    public JingJieData getJingJieData() {
        return player.getData(ModAttachments.JINGJIE_DATA);
    }

    public float getExp() {
        return getJingJieData().getExperience();
    }

    public void setExp(float exp) {
        getJingJieData().setExperience(exp);
    }

    public float getMaxExp() {
        return JingJieHelper.getMaxExperience(this.player);
    }

    public long getLevel() {
        return getJingJieData().getLevel();
    }

    public void setLevel(int level) {
        getJingJieData().setLevel(level);
    }



}
