package com.tcmanna.TCsPitTools;

import com.tcmanna.TCsPitTools.config.ConfigManager;
import com.tcmanna.TCsPitTools.hotkey.HotkeyManager;
import com.tcmanna.TCsPitTools.feature.inGameEvent.PitEventHUD;
import com.tcmanna.TCsPitTools.feature.inGameEvent.PitEventManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = TCsPitTools.MODID,
        version = TCsPitTools.VERSION,
        name = TCsPitTools.NAME,
        acceptedMinecraftVersions = TCsPitTools.Versions,
		clientSideOnly = true,
        guiFactory = "com.tcmanna.TCsPitTools.config.ConfigGuiFactory"
)

public class TCsPitTools
{
	public static final String MODID = "tcs_pittools";
    public static final String VERSION = "1.3.0";
    public static final String NAME = "TCsPitTools";
    public static final String Versions = "[1.8.9]";

	@SidedProxy(
            clientSide = "com.tcmanna.TCsPitTools.ClientProxy",
            serverSide = "com.tcmanna.TCsPitTools.CommonProxy"
    )
	public static CommonProxy proxy;

    public static ConfigManager configManager;
    public static HotkeyManager hotkeyManager;
    public static PitEventManager pitEventManager;
    public static PitEventHUD pitEventHUD;

    public static boolean enableConfigGui = false;
    public static Configuration configFile;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    public static void toggleConfigGui() {
        enableConfigGui = true;
    }

}
