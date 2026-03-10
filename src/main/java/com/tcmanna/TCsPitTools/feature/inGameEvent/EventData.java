package com.tcmanna.TCsPitTools.feature.inGameEvent;

import com.google.gson.annotations.SerializedName;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class EventData {
    @SerializedName("event")
    private String event;
    @SerializedName("timestamp")
    private long timestamp;
    @SerializedName("type")
    private String type;

    public String getEvent() {
        return event;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public Color getColor() {
        return PitEventHUD.PitEvent.fromName(getEvent()).getColor();
    }

    public ItemStack getItemStack() {
        return PitEventHUD.PitEvent.fromName(getEvent()).createItemStack();
    }

}
