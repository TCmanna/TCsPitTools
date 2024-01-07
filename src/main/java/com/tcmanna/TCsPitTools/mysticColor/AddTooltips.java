package com.tcmanna.TCsPitTools.mysticColor;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.tcmanna.TCsPitTools.TCsPitTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class AddTooltips {
    @SubscribeEvent
    public void onTooltipsShow(ItemTooltipEvent event) {
        if (!TCsPitTools.config_t3color_enable)
            return;

        ItemStack itemStack = event.itemStack;
        if (hasExtraAttributes(itemStack)) {
            NBTTagCompound extraAttributes = itemStack.getTagCompound().getCompoundTag("ExtraAttributes");
            if (extraAttributes.hasKey("Nonce") && extraAttributes.getInteger("Nonce") > 20) {
                if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode()))
                {
                    int nonce = extraAttributes.getInteger("Nonce");
                    event.toolTip.add("");
                    event.toolTip.add(I18n.format("tcpt.tooltips.nonce") + ": " + "§f" + nonce);
                    if (getUpTier(extraAttributes) < 3 && itemStack.getItem() != Items.leather_leggings) {
                        event.toolTip.add("T3" + I18n.format("tcpt.tooltips.requires") + getPantsColorText(nonce % 5) + "§7" + I18n.format("tcpt.tooltips.pants"));
                    }
                } else
                {
                    event.toolTip.add(I18n.format("tcpt.tooltips.press") + "[§f" +Keyboard.getKeyName(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode()) + "§7]" + I18n.format("tcpt.tooltips.showmore"));
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
            case 0: return ChatFormatting.RED + " Red ";
            case 1: return ChatFormatting.YELLOW + " Yellow ";
            case 2: return ChatFormatting.BLUE + " Blue ";
            case 3: return ChatFormatting.GOLD + " Orange ";
            case 4: return ChatFormatting.GREEN + " Green ";
            default: return " no? ";
        }
    }
}
