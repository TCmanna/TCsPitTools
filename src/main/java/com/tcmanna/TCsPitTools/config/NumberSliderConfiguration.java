package com.tcmanna.TCsPitTools.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfigEntries;

import java.io.File;

public class NumberSliderConfiguration extends Configuration {
    public NumberSliderConfiguration(File file) {
        super(file);
    }

    public int getInt(String name, String category, int defaultValue, int minValue, int maxValue, String comment, boolean useNumberSlider, String langKey)
    {
        Property prop = this.get(category, name, defaultValue);
        prop.setLanguageKey(langKey);
        prop.comment = comment + " [range: " + minValue + " ~ " + maxValue + ", default: " + defaultValue + "]";
        prop.setMinValue(minValue);
        prop.setMaxValue(maxValue);
        if (useNumberSlider)
            prop.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
        return prop.getInt(defaultValue) < minValue ? minValue : (prop.getInt(defaultValue) > maxValue ? maxValue : prop.getInt(defaultValue));
    }

}
