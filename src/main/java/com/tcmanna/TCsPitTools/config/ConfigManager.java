package com.tcmanna.TCsPitTools.config;

import com.tcmanna.TCsPitTools.TCsPitTools;
import com.tcmanna.TCsPitTools.inGameEvent.EditHudPositionScreen;
import com.tcmanna.TCsPitTools.inGameEvent.PitEventHUD;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.tcmanna.TCsPitTools.TCsPitTools.configFile;

public class ConfigManager {
    public static Property config_tierColor_enable;

    //event
    public static Property config_event_enable;
    public static Property config_event_maxShowEvent;
    public static Property config_event_reverseOrderSort;
    public static Property config_event_dropShadow;
    public static Property config_event_disMode;
    public static Property config_event_pos;
    public static Property config_event_editButton;
    public static Property config_event_scale;
    public static Property config_event_item;

    //goldCheck
    public static Property config_gold_ci;
    public static Property config_gold_ciDelay;
    public static Property config_gold_daDelay;
    public static Property config_gold_ignoreLev;

    public static List<Property> generalList = new ArrayList<>();
    public static List<Property> eventConfigList = new ArrayList<>();
    public static List<Property> goldConfigList = new ArrayList<>();

    //Category
    public static ConfigCategory generalCategory;
    public static ConfigCategory eventConfigCategory;
    public static ConfigCategory goldConfigCategory;

    public ConfigManager(File file) {
        configFile = new Configuration(file);
        configFile.load();
        syncConfig();
    }

    public static void syncConfig() {
        generalList.clear();
        eventConfigList.clear();
        goldConfigList.clear();
        generalCategory = configFile.getCategory("general");
        eventConfigCategory = configFile.getCategory("showevent");
        eventConfigCategory.setComment("Event Render Settings");
        goldConfigCategory = configFile.getCategory("getgold");
        goldConfigCategory.setComment("Gold List Settings");

        eventShowConfig();
        getGoldConfig();
        addTooltipsConfig();

        if (configFile.hasChanged()) {
            configFile.save();
        }
    }

    private static void eventShowConfig() {
        config_event_enable = configFile.get("showevent", "Enable Show", true, null);
        config_event_enable.setLanguageKey("tcpt.event.enable");
        eventConfigList.add(config_event_enable);

        config_event_maxShowEvent = configFile.get("showevent", "Max Show Event", 6, "Set the max event display.", 1, 20);
        config_event_maxShowEvent.setLanguageKey("tcpt.event.max");
        config_event_maxShowEvent.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
        eventConfigList.add(config_event_maxShowEvent);

        config_event_reverseOrderSort = configFile.get("showevent", "Reverseorder Sort", false, null);
        config_event_reverseOrderSort.setLanguageKey("tcpt.event.sort");
        eventConfigList.add(config_event_reverseOrderSort);

        config_event_dropShadow = configFile.get("showevent", "Drop shadow", false, null);
        config_event_dropShadow.setLanguageKey("tcpt.event.shadow");
        eventConfigList.add(config_event_dropShadow);

        config_event_pos = configFile.get("Show Position", "showevent", new int[]{5, 50});
        config_event_pos.setLanguageKey("tcpt.event.pos");
        eventConfigList.add(config_event_pos);

        config_event_disMode = configFile.get("showevent", "Display Mode", 4, "Setting Show Color Mode", 1, 4);
        config_event_disMode.setLanguageKey("tcpt.event.mode");
        config_event_disMode.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
        eventConfigList.add(config_event_disMode);

        config_event_editButton = configFile.get("showevent", "Edit Pos", "Open GUI", "", new String[]{"Open GUI", "Opened GUI"});
        config_event_editButton.setLanguageKey("tcpt.event.opengui");
        eventConfigList.add(config_event_editButton);

        config_event_scale = configFile.get("showevent", "Render Scale", 1d, null, 0.2d, 2d);
        eventConfigList.add(config_event_scale);

        config_event_item = configFile.get("showevent", "Render Item", true);
        eventConfigList.add(config_event_item);

        PitEventHUD.setHudX(config_event_pos.getIntList()[0]);
        PitEventHUD.setHudY(config_event_pos.getIntList()[1]);

        if (config_event_enable.getBoolean()) TCsPitTools.pitEventHUD.enable();
        else TCsPitTools.pitEventHUD.disable();

        eventConfigCategory.keySet().removeIf(key -> eventConfigList.stream().noneMatch(property -> property.getName().equals(key)));
    }

    public static void changeEventPos(int[] intArray) {
        config_event_pos.set(intArray);
        if (configFile.hasChanged()) {
            configFile.save();
        }
    }

    private static void getGoldConfig() {
        config_gold_ci = configFile.get("getgold", "Once Count", 8, "设置每轮查询人数", 1, 10);
        config_gold_ci.setLanguageKey("tcpt.gold.ci");
        config_gold_ci.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
        goldConfigList.add(config_gold_ci);

        config_gold_ciDelay = configFile.get("getgold", "Once Delay", 5000, "设置每轮间隔延迟", 2000, 10000);
        config_gold_ciDelay.setLanguageKey("tcpt.gold.cidelay");
        goldConfigList.add(config_gold_ciDelay);

        config_gold_daDelay = configFile.get("getgold", "Single Delay", 400, "设置单次间隔延迟", 200, 1000);
        config_gold_daDelay.setLanguageKey("tcpt.gold.dadelay");
        goldConfigList.add(config_gold_daDelay);

        config_gold_ignoreLev = configFile.get("getgold", "Ignore Level", 100, "设置忽略此等级以下的玩家", 0, 120);
        config_gold_ignoreLev.setLanguageKey("tcpt.gold.ignore");
        config_gold_ignoreLev.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
        goldConfigList.add(config_gold_ignoreLev);

        goldConfigCategory.keySet().removeIf(key -> goldConfigList.stream().noneMatch(property -> property.getName().equals(key)));
    }

    private static void addTooltipsConfig() {
        config_tierColor_enable = configFile.get("general", "MysticColorShow", true, null);
        config_tierColor_enable.setLanguageKey("tcpt.t3color.enable");
        generalList.add(config_tierColor_enable);

        generalCategory.keySet().removeIf(key -> generalList.stream().noneMatch(property -> property.getName().equals(key)));
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.modID.equals(TCsPitTools.MODID)) {
            syncConfig();
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
