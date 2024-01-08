package com.tcmanna.TCsPitTools.config;

import com.tcmanna.TCsPitTools.TCsPitTools;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class ConfigGui extends GuiConfig {

    public ConfigGui(GuiScreen parent) {
        super(
                parent,
                getConfigElements(),
                TCsPitTools.MODID,
                false,
                false,
                I18n.format("tcpt.gui")
        );
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();

        ConfigCategory showEvent = TCsPitTools.configFile.getCategory("showevent");
        showEvent.setLanguageKey("tcpt.event.gui");
        list.add(new ConfigElement(showEvent));

        ConfigCategory getGold = TCsPitTools.configFile.getCategory("getgold");
        getGold.setLanguageKey("tcpt.gold.gui");
        list.add(new ConfigElement(getGold));
        list.addAll((new ConfigElement(TCsPitTools.configFile.getCategory("general"))).getChildElements());

        return list;
    }

}
