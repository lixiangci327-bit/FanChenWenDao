package net.Lcing.fanchenwendao.registry;


import net.Lcing.fanchenwendao.FanChenWenDao;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = FanChenWenDao.MODID, value = Dist.CLIENT)
public class ModClientCommands {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {

    }
}
