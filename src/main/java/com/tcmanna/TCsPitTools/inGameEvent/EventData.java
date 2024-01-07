package com.tcmanna.TCsPitTools.inGameEvent;

import com.google.gson.annotations.SerializedName;

import java.awt.*;

public class EventData {
    @SerializedName("event")
    private String event;
    @SerializedName("timestamp")
    private long timestamp;
    @SerializedName("type")
    private String type;

    public Color getColor() {
        switch (getEvent()) {
            case "Squads": return new Color(0x206DC5);
            case "Dragon Egg": return new Color(209, 29, 176);
            case "Pizza": return new Color(204, 76, 76);
            case "KOTH": return new Color(96, 208, 200);
            case "Raffle": return new Color(0xC0C020);
            case "KOTL": return new Color(110, 201, 124);
            case "Blockhead": return new Color(0x2323C5);
            case "2x Rewards": return new Color(13, 145, 33);
            case "Spire": return new Color(0xC520C3);
            case "Care Package": return new Color(215, 158, 53);
            case "Team Deathmatch": return new Color(0x7320C5);
            case "Auction": return new Color(212, 152, 58);
            case "Robbery": return new Color(0xC5C320);
            case "Quick Maths": return new Color(219, 7, 235);
            case "Rage Pit": return new Color(0xC52320);
            case "Giant Cake": return new Color(203, 73, 205);
            case "Beast": return new Color(0x20C541);
            case "All bounty": return new Color(210, 139, 26);
        }
        return new Color(0xffffff);
    }

    public String getEvent() {
        return event;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

}
