package net.Lcing.fanchenwendao.client.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.Lcing.fanchenwendao.FanChenWenDao;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = FanChenWenDao.MODID, value = Dist.CLIENT)
public class EntityPoseDebugger {

    public static boolean enabled = false;

    public static float scale = 1.5f;
    public static float rotX = 0.0f;
    public static float rotY = 0.0f;
    public static float rotZ = 0.0f;
    public static float transX = 0.0f;
    public static float transY = 0.0f;
    public static float transZ = 0.0f;

    private static int selectedIndex = 0;
    private static float step = 1.0f;
    private static final String[] LABELS = {
            "Scale (缩放)", "Rot X (X轴旋转)", "Rot Y (Y轴旋转)", "Rot Z (Z轴旋转)",
            "Trans X (X位移)", "Trans Y (Y位移)", "Trans Z (Z位移)"
    };

    public static void apply(PoseStack poseStack) {
        if (!enabled) return;
        poseStack.translate(transX, transY, transZ);
        poseStack.mulPose(Axis.XP.rotationDegrees(rotX));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotZ));
        poseStack.scale(scale, scale, scale);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (Minecraft.getInstance().screen != null) return;

        if (event.getAction() == GLFW.GLFW_PRESS) {
            if (event.getKey() == GLFW.GLFW_KEY_KP_0 || event.getKey() == GLFW.GLFW_KEY_INSERT) {
                enabled = !enabled;
                message(enabled ? "调试器: 开启" : "调试器: 关闭");
            }

            if (enabled) {
                if (event.getKey() == GLFW.GLFW_KEY_TAB) {
                    selectedIndex++;
                    if (selectedIndex >= LABELS.length) selectedIndex = 0;
                }

                float change = 0.0f;
                if (event.getKey() == GLFW.GLFW_KEY_UP) change = step;
                if (event.getKey() == GLFW.GLFW_KEY_DOWN) change = -step;
                if (event.getKey() == GLFW.GLFW_KEY_RIGHT) change = 0.1f;
                if (event.getKey() == GLFW.GLFW_KEY_LEFT) change = -0.1f;

                if (event.getKey() == GLFW.GLFW_KEY_KP_ADD || event.getKey() == GLFW.GLFW_KEY_EQUAL) {
                    if (step < 1.0f) step = 1.0f; else if (step < 5.0f) step = 5.0f; else step = 45.0f;
                    message("步长: " + step);
                }
                if (event.getKey() == GLFW.GLFW_KEY_KP_SUBTRACT || event.getKey() == GLFW.GLFW_KEY_MINUS) {
                    step = 0.1f;
                    message("步长: " + step);
                }

                if (event.getKey() == GLFW.GLFW_KEY_ENTER || event.getKey() == GLFW.GLFW_KEY_KP_ENTER) printConfig();
                if (event.getKey() == GLFW.GLFW_KEY_BACKSPACE || event.getKey() == GLFW.GLFW_KEY_DELETE) resetValue(selectedIndex);
                if (change != 0.0f) updateValue(selectedIndex, change);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!enabled) return;
        var graphics = event.getGuiGraphics();
        var font = Minecraft.getInstance().font;
        int x = 10;
        int y = 50;
        int lineHeight = 12;

        graphics.drawString(font, "== 实体姿态调试模式 ==", x, y, 0xFFFFFF);
        y += lineHeight;
        graphics.drawString(font, "INS开关 | TAB切换 | Enter输出", x, y, 0xAAAAAA);
        y += lineHeight * 1.5f;

        for (int i = 0; i < LABELS.length; i++) {
            int color = (i == selectedIndex) ? 0xFFFF00 : 0xFFFFFF;
            String prefix = (i == selectedIndex) ? "> " : "  ";
            float val = getValue(i);
            graphics.drawString(font, String.format("%s%s: %.2f", prefix, LABELS[i], val), x, y, color);
            y += lineHeight;
        }
    }

    private static void updateValue(int index, float change) {
        switch (index) {
            case 0 -> scale += change;
            case 1 -> rotX += change;
            case 2 -> rotY += change;
            case 3 -> rotZ += change;
            case 4 -> transX += change;
            case 5 -> transY += change;
            case 6 -> transZ += change;
        }
    }
    private static void resetValue(int index) {
        switch (index) {
            case 0 -> scale = 1.0f;
            case 1 -> rotX = 0.0f;
            case 2 -> rotY = 0.0f;
            case 3 -> rotZ = 0.0f;
            case 4 -> transX = 0.0f;
            case 5 -> transY = 0.0f;
            case 6 -> transZ = 0.0f;
        }
    }
    private static float getValue(int index) {
        return switch (index) {
            case 0 -> scale;
            case 1 -> rotX;
            case 2 -> rotY;
            case 3 -> rotZ;
            case 4 -> transX;
            case 5 -> transY;
            case 6 -> transZ;
            default -> 0;
        };
    }
    private static void message(String msg) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(Component.literal(msg), true);
        }
    }
    private static void printConfig() {
        System.out.println("============== 调试器参数输出 ==============");
        System.out.printf("poseStack.translate(%.2fF, %.2fF, %.2fF);%n", transX, transY, transZ);
        System.out.printf("poseStack.mulPose(Axis.XP.rotationDegrees(%.2fF));%n", rotX);
        System.out.printf("poseStack.mulPose(Axis.YP.rotationDegrees(%.2fF));%n", rotY);
        System.out.printf("poseStack.mulPose(Axis.ZP.rotationDegrees(%.2fF));%n", rotZ);
        System.out.printf("poseStack.scale(%.2fF, %.2fF, %.2fF);%n", scale, scale, scale);
        System.out.println("============================================");
        message("参数已输出到控制台！");
    }
}
