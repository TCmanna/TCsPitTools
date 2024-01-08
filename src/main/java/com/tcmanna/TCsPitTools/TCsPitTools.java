package com.tcmanna.TCsPitTools;

import com.tcmanna.TCsPitTools.config.NumberSliderConfiguration;
import com.tcmanna.TCsPitTools.inGameEvent.PitEventHUD;
import com.tcmanna.TCsPitTools.inGameEvent.PitEventManager;
import net.minecraftforge.common.config.Property;
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
    public static final String VERSION = "1.2.2";
    public static final String NAME = "TCsPitTools";
    public static final String Versions = "[1.8.9]";

	@SidedProxy(
            clientSide = "com.tcmanna.TCsPitTools.ClientProxy",
            serverSide = "com.tcmanna.TCsPitTools.CommonProxy"
    )
	public static CommonProxy proxy;

    public static PitEventManager pitEventManager;
    public static PitEventHUD pitEventHUD;

    public static boolean enableConfigGui = false;
    public static NumberSliderConfiguration configFile;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        pitEventManager = new PitEventManager();
        pitEventHUD = new PitEventHUD();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }
	
	public static void syncConfig() {
        eventShowConfig();
        getGoldConfig();
        addTooltipsConfig();

        if (configFile.hasChanged()) {
            configFile.save();
        }
	}

    public static boolean config_event_enable;
    public static int config_event_maxShowEvent;
    public static boolean config_event_reverseOrderSort;
    public static boolean config_event_dropShadow;
    public static int config_event_disMode;
    public static Property config_event_pos;
    private static void eventShowConfig() {
        config_event_enable = configFile.getBoolean("Enable Show", "showevent", true, null, "tcpt.event.enable");
        config_event_maxShowEvent = configFile.getInt("Max Show Event", "showevent", 6, 1, 20, "Set the max event display.", true, "tcpt.event.max");
        config_event_reverseOrderSort = configFile.getBoolean("Reverseorder Sort", "showevent", false, null, "tcpt.event.sort");
        config_event_dropShadow = configFile.getBoolean("Drop shadow", "showevent", false, null, "tcpt.event.shadow");
        config_event_pos = configFile.get("showevent", "Show Position", new int[]{5, 50}).setLanguageKey("tcpt.event.pos");
        config_event_disMode = configFile.getInt("Display Mode", "showevent", 1, 1, 3, "Setting Show Color Mode", true, "tcpt.event.mode");


        configFile.getString("Edit Pos", "showevent", "Open GUI", "", new String[]{"Open GUI", "Opened GUI"}, "tcpt.event.opengui");

        PitEventHUD.setHudX(config_event_pos.getIntList()[0]);
        PitEventHUD.setHudY(config_event_pos.getIntList()[1]);

        if (config_event_enable)
            pitEventHUD.enable();
        else
            pitEventHUD.disable();
    }

    public static void changeEventPos(int[] intArray) {
        config_event_pos.set(intArray);
        if (configFile.hasChanged()) {
            configFile.save();
        }
    }

    public static void toggleConfigGui() {
        enableConfigGui = true;
    }

    public static int config_gold_ci;
    public static int config_gold_ciDelay;
    public static int config_gold_daDelay;
    public static int config_gold_ignoreLev;
    private static void getGoldConfig() {
        config_gold_ci = configFile.getInt("Once Count", "getgold", 8, 1, 10, "设置每轮查询人数", true, "tcpt.gold.ci");
        config_gold_ciDelay = configFile.getInt("Once Delay", "getgold", 5000, 2000, 10000, "设置每轮间隔延迟", "tcpt.gold.cidelay");
        config_gold_daDelay = configFile.getInt("Single Delay", "getgold", 400, 200, 1000, "设置单次间隔延迟", "tcpt.gold.dadelay");
        config_gold_ignoreLev = configFile.getInt("Ignore Level", "getgold", 100, 0, 120, "设置忽略此等级以下的玩家", true, "tcpt.gold.ignore");
    }

    public static boolean config_t3color_enable;
    private static void addTooltipsConfig() {
        config_t3color_enable = configFile.getBoolean("Enable Color Show", "general", true, null, "tcpt.t3color.enable");
    }

}
