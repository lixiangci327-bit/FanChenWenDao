package net.Lcing.fanchenwendao.gongfa.item;

import com.lowdragmc.lowdraglib2.gui.factory.HeldItemUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import net.Lcing.fanchenwendao.client.ui.ldlib.GongFaUI;
import net.Lcing.fanchenwendao.gongfa.GongFaDefine;
import net.Lcing.fanchenwendao.gongfa.GongFaManager;
import net.Lcing.fanchenwendao.gongfa.Requirements;
import net.Lcing.fanchenwendao.jingjie.JingJieData;
import net.Lcing.fanchenwendao.jingjie.JingJieHelper;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.Lcing.fanchenwendao.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class GongFaBook_01 extends Item implements HeldItemUIMenuType.HeldItemUI {

    public GongFaBook_01(Properties properties) {
        super(properties);
    }

    //动态物品名，根据功法id渲染物品名称
    @Override
    public Component getName(ItemStack stack) {
        //从物品的Data Component中读取GONGFA_ID
        ResourceLocation id = stack.get(ModDataComponents.GONGFA_ID);

        if (id != null) {
            //根据ID去Manager中找到功法的数据
            Optional<GongFaDefine> define = GongFaManager.getGongFa(id);
            if (define.isPresent()) {   //功法存在
                return Component.literal(define.get().getName()).withStyle(ChatFormatting.GOLD);
            }
        }
        //ID找不到
        return Component.translatable("item.fanchenwendao.gongfabook_01");

    }

    //实现createUI接口方法
    @Override
    public ModularUI createUI(HeldItemUIMenuType.HeldItemUIHolder holder) {
        ItemStack stack = holder.itemStack;
        ResourceLocation id = stack.get(ModDataComponents.GONGFA_ID);

        if (id != null) {
            return GongFaUI.buildUI(holder.player, id);
        }
        //若数据存在问题
        return new ModularUI(UI.empty());
    }

    //右键逻辑
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usehand) {
        ItemStack stack = player.getItemInHand(usehand);

        //逻辑在服务端处理
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            //获取ID
            ResourceLocation id = stack.get(ModDataComponents.GONGFA_ID);
            if (id == null) {
                return InteractionResultHolder.fail(stack);
            }

            //shift+右键——学习/参悟功法
            if (player.isShiftKeyDown()) {
                boolean isLearned = serverPlayer.getData(ModAttachments.JINGJIE_DATA).getLearnedGongFas().containsKey(id);

                //获取玩家的境界数据
                JingJieData data = serverPlayer.getData(ModAttachments.JINGJIE_DATA);

                if (isLearned) {
                    player.displayClientMessage(Component.literal("§e你已习得此功法，无需再次参悟。"), false);
                    return InteractionResultHolder.success(stack);
                }


                Optional<GongFaDefine> defineOpt = GongFaManager.getGongFa(id);
                if (defineOpt.isEmpty()) return InteractionResultHolder.fail(stack);

                GongFaDefine define = defineOpt.get();
                Requirements requirements = define.getComprehension().getRequirements();

                //获取环境灵气
                float currentLingQi = level.getChunk(player.blockPosition())
                        .getData(ModAttachments.LINGQI_CHUNK_DATA)
                                .getCurrentLingQi();

                //检测要求是否满足
                if (player.experienceLevel < requirements.getXpConsume() || currentLingQi < requirements.getMinLingQi()) {
                    player.displayClientMessage(Component.literal(String.format("§c未满足要求无法习得功法")),false);
                    return InteractionResultHolder.fail(stack);
                }

                //扣除要求
                player.giveExperienceLevels(-requirements.getXpConsume());
                data.learnGongFa(id);
                player.displayClientMessage(Component.literal("§a参悟成功！已习得：").append(define.getName()), false);
                JingJieHelper.syncToClient(serverPlayer);

            } else {
                //右键打开UI
                HeldItemUIMenuType.openUI(serverPlayer, usehand);
                }
        }
        return InteractionResultHolder.success(stack);
    }


    //物品提示
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§7[右键] 研读功法"));
        tooltipComponents.add(Component.literal("§7[Shift+右键] 参悟学习"));

        //如为高级提示模式，显示内部ID
        if (tooltipFlag.isAdvanced()) {
            ResourceLocation id = stack.get(ModDataComponents.GONGFA_ID);
            if (id != null) {
                tooltipComponents.add(Component.literal(id.toString()).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }


}
