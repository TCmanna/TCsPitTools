package com.tcmanna.TCsPitTools;

import com.tcmanna.TCsPitTools.checkPlayer.CheckPlayerCommand;
import com.tcmanna.TCsPitTools.checkPlayer.ClientEvent;
import com.tcmanna.TCsPitTools.config.NumberSliderConfiguration;
import com.tcmanna.TCsPitTools.getGold.GetGoldCommand;
import com.tcmanna.TCsPitTools.hotkey.HotkeyManager;
import com.tcmanna.TCsPitTools.inGameEvent.PitEventHUD;
import com.tcmanna.TCsPitTools.inGameEvent.PitEventManager;
import com.tcmanna.TCsPitTools.mysticColor.AddTooltips;
import com.tcmanna.TCsPitTools.config.GuiConfigCommand;
import com.tcmanna.TCsPitTools.config.ConfigAboutListener;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    public ClientProxy() {
        super();
    }
	
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        TCsPitTools.pitEventManager = new PitEventManager();
        TCsPitTools.pitEventHUD = new PitEventHUD();
        TCsPitTools.hotkeyManager = new HotkeyManager();

        MinecraftForge.EVENT_BUS.register(new AddTooltips());
        MinecraftForge.EVENT_BUS.register(new ConfigAboutListener());
        MinecraftForge.EVENT_BUS.register(new ClientEvent());
        MinecraftForge.EVENT_BUS.register(TCsPitTools.hotkeyManager);
        TCsPitTools.configFile = new NumberSliderConfiguration(e.getSuggestedConfigurationFile());
        TCsPitTools.configFile.load();
        TCsPitTools.syncConfig();
    }

    public void init(FMLInitializationEvent e) {
        super.init(e);
        ClientCommandHandler.instance.registerCommand(new GuiConfigCommand());
        ClientCommandHandler.instance.registerCommand(new GetGoldCommand());
        ClientCommandHandler.instance.registerCommand(new CheckPlayerCommand());
        TCsPitTools.hotkeyManager.registerKeys();
    }
	
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
}
