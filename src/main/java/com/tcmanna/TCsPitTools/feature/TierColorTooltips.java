package com.tcmanna.TCsPitTools.feature;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.tcmanna.TCsPitTools.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class TierColorTooltips {
    @SubscribeEvent
    public void onTooltipsShow(ItemTooltipEvent event) {
        if (!ConfigManager.config_tierColor_enable.getBoolean()) return;

        ItemStack itemStack = event.itemStack;
        if (hasExtraAttributes(itemStack)) {
            NBTTagCompound extraAttributes = itemStack.getTagCompound().getCompoundTag("ExtraAttributes");
            if (extraAttributes.hasKey("Nonce") && extraAttributes.getInteger("Nonce") > 20) {
                if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())) {
                    int nonce = extraAttributes.getInteger("Nonce");
                    event.toolTip.add("");
                    event.toolTip.add(I18n.format("tcpt.tooltips.nonce") + ": §f" + nonce);
                    if (getUpTier(extraAttributes) < 3 && itemStack.getItem() != Items.leather_leggings) {
                        String i18n = I18n.format("tcpt.tooltips.requires");
                        event.toolTip.add(i18n.replace("{{string}}", getPantsColorText(nonce % 5)));
                    }
                }
                else {
                    String i18n = I18n.format("tcpt.tooltips.showmore");
                    event.toolTip.add(i18n.replace("{{string}}", Keyboard.getKeyName(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())));
                }
            }

        }
    }

    private boolean hasExtraAttributes(ItemStack itemStack) {
        return itemStack.hasTagCompound() && itemStack.getTagCompound().getCompoundTag("ExtraAttributes") != null;
    }

    private int getUpTier(NBTTagCompound extraAttributes) {
        return extraAttributes.hasKey("UpgradeTier") ? extraAttributes.getInteger("UpgradeTier") : -1;
    }

    private String getPantsColorText(int color) {
        switch (color) {
            case 0: return ChatFormatting.RED + "Red";
            case 1: return ChatFormatting.YELLOW + "Yellow";
            case 2: return ChatFormatting.BLUE + "Blue";
            case 3: return ChatFormatting.GOLD + "Orange";
            case 4: return ChatFormatting.GREEN + "Green";
            default: return " no? ";
        }
    }
}
