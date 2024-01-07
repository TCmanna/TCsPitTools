package com.tcmanna.TCsPitTools.config;

import com.tcmanna.TCsPitTools.TCsPitTools;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ConfigAboutListener {
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.modID.equals(TCsPitTools.MODID)) {
            TCsPitTools.syncConfig();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && TCsPitTools.enableConfigGui) {
            TCsPitTools.enableConfigGui = false;
            Minecraft.getMinecraft().displayGuiScreen(new ConfigGui(Minecraft.getMinecraft().currentScreen));
        }
    }
}
