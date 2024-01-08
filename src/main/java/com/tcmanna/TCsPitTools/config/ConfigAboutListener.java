package com.tcmanna.TCsPitTools.config;

import com.tcmanna.TCsPitTools.TCsPitTools;
import com.tcmanna.TCsPitTools.inGameEvent.EditHudPositionScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
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
        if (event.phase == TickEvent.Phase.END) {
            if (TCsPitTools.enableConfigGui) {
                TCsPitTools.enableConfigGui = false;
                Minecraft.getMinecraft().displayGuiScreen(new ConfigGui(Minecraft.getMinecraft().currentScreen));
            }

            if (Minecraft.getMinecraft().currentScreen instanceof GuiConfig) {
                GuiConfig configGui = (GuiConfig)Minecraft.getMinecraft().currentScreen;
                for (GuiConfigEntries.IConfigEntry entry : configGui.entryList.listEntries) {
                    if (entry.getName().equals("Edit Pos") && entry.getCurrentValue().equals("Opened GUI")) {
                        entry.setToDefault();
                        Minecraft.getMinecraft().displayGuiScreen(new EditHudPositionScreen());
                    }
                }
            }
        }
    }
}
