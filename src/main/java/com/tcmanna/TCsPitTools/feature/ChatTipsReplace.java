package com.tcmanna.TCsPitTools.feature;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatTipsReplace {

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        replaceCommand(event.message, "/socialoptions ", "/view ");
        addCommandTips(event.message);
    }

    private static void replaceCommand(IChatComponent component, String target, String replacement) {
        replaceProcess(component, target, replacement);

        for (IChatComponent sibling : component.getSiblings()) {
            replaceCommand(sibling, target, replacement);
        }
    }

    private static void replaceProcess(IChatComponent component, String target, String replacement) {

        ChatStyle style = component.getChatStyle();
        if (style == null) return;

        ClickEvent click = style.getChatClickEvent();
        if (click == null) return;

        if (click.getAction() != ClickEvent.Action.RUN_COMMAND &&
                click.getAction() != ClickEvent.Action.SUGGEST_COMMAND) {
            return;
        }

        String command = click.getValue();

        if (command.startsWith(target)) {

            String args = command.substring(target.length());
            String newCommand = replacement + args;

            style.setChatClickEvent(
                    new ClickEvent(click.getAction(), newCommand)
            );
        }

        if (command.equals("/pickupstash")) {
            style.setChatClickEvent(null);
            style.setChatHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§bpickupstash has canceled by TCsPitTools"))
            );
        }
    }

    private static boolean addCommandTips(IChatComponent component) {

        ChatStyle style = component.getChatStyle();
        if (style != null) {
            ClickEvent click = style.getChatClickEvent();
            if (click != null &&
                    (click.getAction() == ClickEvent.Action.RUN_COMMAND ||
                            click.getAction() == ClickEvent.Action.SUGGEST_COMMAND)) {

                String command = click.getValue();
                HoverEvent hover = style.getChatHoverEvent();

                if (hover != null && hover.getAction() == HoverEvent.Action.SHOW_TEXT) {

                    IChatComponent hoverText = hover.getValue().createCopy();
                    hoverText.appendText("\n§7Command: §e" + command);
                    style.setChatHoverEvent(
                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)
                    );

                } else {
                    IChatComponent newHover =
                            new ChatComponentText("§7Command: §e" + command);
                    style.setChatHoverEvent(
                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, newHover)
                    );
                }

                return true;
            }
        }

        for (IChatComponent sibling : component.getSiblings()) {
            if (addCommandTips(sibling)) {
                return true;
            }
        }
        return false;
    }

}
