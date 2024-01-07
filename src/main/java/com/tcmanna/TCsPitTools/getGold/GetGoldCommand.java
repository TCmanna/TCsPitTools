package com.tcmanna.TCsPitTools.getGold;

import com.tcmanna.TCsPitTools.TCsPitTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;

public class GetGoldCommand extends CommandBase {
    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "getgold";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/getgold";
    }

    public static boolean onEnable = false;

    private static List<EntityPlayer> entityPlayers;
    private static Map<String, String> playerGoldMap;
    private static int ignorePlayer = 0;

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!onEnable) {
            onEnable = true;
            this.enable(sender);
        }
        else this.disable();
    }

    private void enable(ICommandSender sender) {
        MinecraftForge.EVENT_BUS.register(this);
        playerGoldMap = new HashMap<>();
        ignorePlayer = 0;
        new Thread(() -> {
            sender.addChatMessage(new ChatComponentText("§b正在查询全房gold排行..§8(inGame)"));
            try {
                entityPlayers = Minecraft.getMinecraft().theWorld.playerEntities;
                List<EntityPlayer> safeEntityPlayers = Collections.synchronizedList(new ArrayList<>(entityPlayers));
                int i = 0;
                for (EntityPlayer playerEntity : safeEntityPlayers) {
                    if (!onEnable) {
                        break;
                    }
                    if (!isLowLevPlayer(playerEntity.getDisplayName().getFormattedText())){
                        String name = playerEntity.getName();
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("/view " + name);
                        i++;
                        if (i % TCsPitTools.config_gold_ci == 0) {
                            Thread.sleep(TCsPitTools.config_gold_ciDelay);
                        } else Thread.sleep(TCsPitTools.config_gold_daDelay);
                    }
                }

                Map<String, String> sortedMap = playerGoldMap.entrySet().stream()
                        .sorted(Comparator.comparing(entry -> Integer.parseInt(entry.getValue().replaceAll(",", "")))
                        )
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new));
                sortedMap.forEach((k, v) -> {
                    if (k.contains(sender.getName())) {
                        sender.addChatMessage(new ChatComponentText("§6§l" + v + "§7 - " + k));
                    }else sender.addChatMessage(new ChatComponentText(v + "§7 - " + k));
                });
                int allPlayer = safeEntityPlayers.size();

                sender.addChatMessage(
                        new ChatComponentText(
                                "房间内有" + allPlayer +
                                        "位玩家 本次查询了" + sortedMap.keySet().size() +
                                        "位玩家 忽略了" + ignorePlayer +
                                        "位玩家"));

                this.disable();
            } catch (InterruptedException e) {
                this.disable();
            }
        }).start();
    }

    private void disable() {
        if (onEnable) {
            onEnable = false;
        }
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    private boolean isLowLevPlayer(String disName) {
        if (disName.contains("§7[") && !disName.contains("#")) {
            String cleanedName = disName.replaceAll("§.", "");
            String subLevel = cleanedName.substring(cleanedName.indexOf("[") + 1, cleanedName.indexOf("]"));

            int disLevel = Integer.parseInt(subLevel);
            if(disLevel < TCsPitTools.config_gold_ignoreLev) {
                ignorePlayer++;
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiChest) {
            GuiChest gui = (GuiChest) event.gui;
            String name = gui.inventorySlots.getSlot(0).inventory.getName();
            if (name.equals("Profile Viewer")) {

                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 100; i++) {
                            try {
                                Slot slot = Minecraft.getMinecraft().thePlayer.openContainer.getSlot(i);
                                ItemStack itemStack = slot.getStack();

                                if (itemStack != null && itemStack.getItem() == Items.name_tag) {
                                    String lore = itemStack.getTagCompound().getCompoundTag("display").toString();
                                    String gold;
                                    try {
                                        gold = lore.substring(lore.indexOf("金币： §6§6") + 8, lore.indexOf("g\",1:\""));
                                    } catch (StringIndexOutOfBoundsException e) {
                                        gold = "0";
                                    }
                                    playerGoldMap.put(itemStack.getDisplayName(), gold);
                                    break;
                                }
                            } catch (IndexOutOfBoundsException ignored) {}
                        }
                    }
                }, 100);
            }
        }else if (event.gui instanceof GuiChat || event.gui instanceof GuiIngameMenu)
            this.disable();
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (event.gui instanceof GuiChest) {
            GuiChest gui = (GuiChest) event.gui;
            String name = gui.inventorySlots.getSlot(0).inventory.getName();
            if (name.equals("Profile Viewer")) {
                Minecraft.getMinecraft().setIngameFocus();
                event.setCanceled(true);
            }
        }
    }
}
