package com.tcmanna.TCsPitTools.checkPlayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;

public class ClientEvent {
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (!newEnt.isEmpty()) {
            long now = System.currentTimeMillis();
            newEnt.values().removeIf((e) -> e < now - 4000L);
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer  == null) return;

        if (isCheckArmorToggled) {
            isCheckArmorToggled = false;
            if (CheckPlayerCommand.checkPlayerInv != null) {
                Minecraft.getMinecraft().displayGuiScreen(new CheckPlayerCommand.GuiCheckPlayerInv(Minecraft.getMinecraft().thePlayer, CheckPlayerCommand.checkPlayerInv));
                CheckPlayerCommand.checkPlayerInv = null;
            }
        }
    }

    private static final HashMap<EntityPlayer, Long> newEnt = new HashMap<>();

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer  == null) return;
        if (event.entity instanceof EntityPlayer && event.entity != mc.thePlayer) {
            newEnt.put((EntityPlayer)event.entity, System.currentTimeMillis());
        }

    }

    private static boolean isCheckArmorToggled = false;
    public static void toggleCheckArmorGui() {
        isCheckArmorToggled = true;
    }

    public static boolean bot(EntityPlayer en) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiChat)) {
            if (mc.thePlayer == null || mc.currentScreen != null) return false;
        }

            if (!newEnt.isEmpty() && newEnt.containsKey(en)) {
                return true;
            } else if (en.getName().startsWith("§c")) {
                return true;
            } else {
                String n = en.getDisplayName().getUnformattedText();
                if (n.contains("§")) {
                    return n.contains("[NPC] ");
                } else {
                    if (n.isEmpty() && en.getName().isEmpty()) {
                        return true;
                    }

                    if (n.length() == 10) {
                        int num = 0;
                        int let = 0;
                        char[] var4 = n.toCharArray();

                        for (char c : var4) {
                            if (Character.isLetter(c)) {
                                if (Character.isUpperCase(c)) {
                                    return false;
                                }

                                ++let;
                            } else {
                                if (!Character.isDigit(c)) {
                                    return false;
                                }

                                ++num;
                            }
                        }

                        return num >= 2 && let >= 2;
                    }
                }

                return false;
            }

    }

}
