package net.Lcing.fanchenwendao.client.ui.ldlib;

import com.lowdragmc.lowdraglib2.editor.resource.FilePath;
import com.lowdragmc.lowdraglib2.editor.resource.UIResource;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UITemplate;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class GongFaUI {

    public static ModularUI buildUI(Player player, String name, String level, String description) {

        //获取模板
        ResourceLocation uiLocation = ResourceLocation.parse("fanchenwendao:ui/gongfa01.ui.nbt");

        //使用容器Optional，放在空指针报错
        Optional<UI> uiOptional = Optional.ofNullable(UIResource.INSTANCE.getResourceInstance()
                .getResource(new FilePath(uiLocation)))
                .map(resource -> resource.createUI());    //拿到了就转为UI对象

        UI ui = Optional.ofNullable(UIResource.INSTANCE.getResourceInstance()
                .getResource(new FilePath(uiLocation)))
                .map(UITemplate::createUI)
                .orElseGet(UI::empty);

        //名称
        ui.selectRegex("name").findFirst().ifPresent(element -> {
            if (element instanceof Label label) {
                label.setText(name);
            }
        });

        //品阶
        ui.selectRegex("level").findFirst().ifPresent(element -> {
            if (element instanceof Label label) {
                label.setText("功法品阶：" + level);
            }
        });

        //功法描述
        ui.selectRegex("description").findFirst().ifPresent(element -> {
            if (element instanceof Label label) {
                label.setText(description);
            }
        });

        return ModularUI.of(ui, player);

    }
}
