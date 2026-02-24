package com.tcmanna.TCsPitTools.inGameEvent;

import com.tcmanna.TCsPitTools.TCsPitTools;
import com.tcmanna.TCsPitTools.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

public class PitEventHUD {
    private static int hudX = 5;
    private static int hudY = 50;

    public static int textBoxWidth = 0;
    public static int textBoxHeight = 0;

    public static PositionMode positionMode;
    private static final List<EventRenderData> renderCache = new ArrayList<>();
    private static long tickCount = 0;
    public static FontRenderer fontRendererObj;

    private static final Minecraft mc = Minecraft.getMinecraft();

    public void enable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void disable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null) return;
        if (tickCount > 0) {
            tickCount--;
            return;
        }
        tickCount = 19;
        rebuildCache(System.currentTimeMillis());
    }

    private static void rebuildCache(long now) {
        renderCache.clear();
        List<EventData> sorted =
                PitEventManager.filterByTimestampAndSort(
                        TCsPitTools.pitEventManager.getEventList(),
                        now
                );
        if (sorted.isEmpty()) return;
        int limit = Math.min(sorted.size(), ConfigManager.config_event_maxShowEvent.getInt());
        for (int i = 0; i < limit; i++) {
            renderCache.add(new EventRenderData(sorted.get(i)));
        }

        if (ConfigManager.config_event_reverseOrderSort.getBoolean()) {
            Collections.reverse(renderCache);
        }
    }

    @SubscribeEvent
    public void onRenderTick(RenderGameOverlayEvent.Text event) {
        if (mc.thePlayer == null) return;
        if (!(mc.currentScreen == null || mc.currentScreen instanceof GuiChat)) return;

        if (renderCache.isEmpty()) return;

        GlStateManager.pushMatrix();
        double scale = ConfigManager.config_event_scale.getDouble();
        GlStateManager.scale(scale, scale, scale);
        drawCachedList(false);
        GlStateManager.popMatrix();


    }

    public static void drawCachedList(boolean drawBox) {

        final int margin = 2;
        int y = hudY;
        int del = 0;

        ScaledResolution sr = new ScaledResolution(mc);
        positionMode = getPostitionMode(hudX, hudY, sr.getScaledWidth(), sr.getScaledHeight());

        if (positionMode == null) return;

        textBoxWidth = getMaxWidth();
        textBoxHeight = renderCache.size() * (fontRendererObj.FONT_HEIGHT + margin);

        List<EventRenderData> drawList = renderCache;

        if (positionMode == PositionMode.DOWNLEFT || positionMode == PositionMode.DOWNRIGHT) {
            drawList = new ArrayList<>(renderCache);
            Collections.reverse(drawList);
        }

        int colour = ConfigManager.config_event_disMode.getInt();

        if (drawBox || colour == 4) drawBackgroundBox();

        for (EventRenderData data : drawList) {
            double lastTime = data.source.getTimestamp() - System.currentTimeMillis();

            if (positionMode == PositionMode.UPRIGHT || positionMode == PositionMode.DOWNRIGHT)
                y = drawRight(data, y, margin, del, lastTime);
            else
                y = drawLeft(data, y, margin, del, lastTime);

            del -= 120;
        }
    }

    private static int drawRight(EventRenderData data, int y, int margin, int del, double lastTime) {
        String text = data.rightText;
        int width = data.widthRight;
        int startX = hudX - textBoxWidth + (textBoxWidth - width);
        renderEventMain(data, y, del, text, width, startX, lastTime, true);
        return y + fontRendererObj.FONT_HEIGHT + margin;
    }

    private static int drawLeft(EventRenderData data, int y, int margin, int del, double lastTime) {
        String text = data.leftText;
        int width = data.widthLeft;
        renderEventMain(data, y, del, text, width, hudX, lastTime, false);
        return y + fontRendererObj.FONT_HEIGHT + margin;
    }

    private static void renderEventMain(
            EventRenderData data,
            int y,
            int del,
            String text,
            int stringWidth,
            int startX,
            double lastTime,
            boolean isRight
    ) {

        int colour = ConfigManager.config_event_disMode.getInt();
        boolean shadow = ConfigManager.config_event_dropShadow.getBoolean();
        Color baseColor = data.source.getColor();
        boolean isMajor = "major".equals(data.source.getType());

        double ratio = getProgressRatio(lastTime);
        boolean active = lastTime > 0 && lastTime < 60000;

        int endX = startX + stringWidth + 1;
        int height = fontRendererObj.FONT_HEIGHT;

        if (ConfigManager.config_event_item.getBoolean()) {
            float scale = 0.7f;
            RenderItem renderItem = mc.getRenderItem();
            int baseX = isRight ? hudX + 1 : hudX - 17;
            if (colour == 4)
                baseX = isRight ? hudX - textBoxWidth - 17 : hudX + textBoxWidth;
            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.translate(baseX + 8, y + 8, 0);
            GlStateManager.scale(scale, scale, 1);
            GlStateManager.translate(-8, -14, 0);
            renderItem.renderItemIntoGUI(data.source.getItemStack(), 0, 0);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }

        switch (colour) {

            case 1: {
                if (isMajor) drawBackground(startX, y, endX, height, withAlpha(baseColor, 102));

                int renderTextColor = astolfoColorsDraw(10, del, 2900F);

                if (active) {
                    drawBackground(startX, y, endX, height, withAlpha(baseColor, loopColorAlpha()));
                    drawProgressBar(startX - 1, endX, y, ratio, isRight);
                    renderTextColor = 0xFFFFFF;
                }
                fontRendererObj.drawString(text, startX, y, renderTextColor, shadow);
                break;
            }

            case 2: {
                if (active) {
                    drawBackground(startX, y, endX, height, withAlpha(baseColor, loopColorAlpha()));
                    drawProgressBar(startX - 1, endX, y, ratio, isRight);
                }
                else drawBackground(startX, y, endX, height, withAlpha(baseColor, 102));
                fontRendererObj.drawString(text, startX, y, 0xFFFFFF, shadow);
                break;
            }

            case 3: {
                if (isMajor) drawBackground(startX, y, endX, height, 0x4DFFFFFF);
                int renderTextColor = baseColor.getRGB();
                if (active) {
                    drawBackground(startX, y, endX, height, withAlpha(baseColor, loopColorAlpha()));
                    drawProgressBar(startX - 1, endX, y, ratio, isRight);
                    renderTextColor = 0xFFFFFF;
                }
                fontRendererObj.drawString(text, startX, y, renderTextColor, shadow);
                break;
            }

            case 4: {
                renderModeFour(data, y, lastTime, isRight, shadow);
                break;
            }
        }
    }

    private static void renderModeFour(
            EventRenderData data,
            int y,
            double lastTime,
            boolean isRight,
            boolean shadow
    ) {
        Color baseColor = data.source.getColor();
        String event = data.source.getEvent();
        String cd = calculateCountdown(data.source.getTimestamp());

        int renderTextColor = baseColor.getRGB();
        int height = fontRendererObj.FONT_HEIGHT;

        boolean active = lastTime > 0 && lastTime < 60000;

        int boxStartX;
        int boxEndX;

        if (isRight) {
            boxStartX = hudX - textBoxWidth - 1;
            boxEndX = hudX + 1;
        } else {
            boxStartX = hudX - 1;
            boxEndX = hudX + textBoxWidth + 1;
        }
        boolean isMajor = "major".equals(data.source.getType());
        if (isMajor) {
            drawBackground(boxStartX, y, boxEndX, height, withAlpha(baseColor, 102));
            renderTextColor = 0xFFFFFF;
        }

        if (active) {

            double ratio = getProgressRatio(lastTime);

            int bg = withAlpha(baseColor, loopColorAlpha());

            Gui.drawRect(boxStartX, y - 1, boxEndX, y + height, bg);

            drawProgressBar(boxStartX, boxEndX, y, ratio, isRight);

            renderTextColor = 0xFFFFFF;
        }

        if (isRight) {
            int startX = hudX - textBoxWidth;
            int cdWidth = fontRendererObj.getStringWidth(cd);
            int cdX = startX + textBoxWidth - cdWidth;

            fontRendererObj.drawString(event, startX, y, renderTextColor, shadow);
            fontRendererObj.drawString(cd, cdX, y, renderTextColor, shadow);


        } else {
            int eventWidth = fontRendererObj.getStringWidth(event);
            int eventX = hudX + textBoxWidth - eventWidth;

            fontRendererObj.drawString(cd, hudX, y, renderTextColor, shadow);
            fontRendererObj.drawString(event, eventX, y, renderTextColor, shadow);

        }

    }

    private static void drawBackground(int startX, int y, int endX, int height, int color) {
        Gui.drawRect(startX, y - 1, endX, y + height, color);
    }

    private static int withAlpha(Color color, int alpha) {
        return (alpha << 24)
                | (color.getRed() << 16)
                | (color.getGreen() << 8)
                | color.getBlue();
    }

    private static double getProgressRatio(double lastTime) {
        double ratio = lastTime / 60000d;
        return Math.max(0d, Math.min(1d, ratio));
    }

    private static void drawProgressBar(
            int startX,
            int endX,
            int y,
            double ratio,
            boolean isRight
    ) {
        double distance = endX - startX;

        double renderStartX = isRight
                ? startX + (distance - (distance * ratio))
                : startX;

        double renderEndX = isRight
                ? endX
                : endX - (distance - (distance * ratio));

        drawRectDouble(
                renderStartX,
                y + fontRendererObj.FONT_HEIGHT - 1,
                renderEndX,
                y + fontRendererObj.FONT_HEIGHT,
                0xFFFFFFFF
        );
    }

    private static void drawBackgroundBox() {

        switch (positionMode) {

            case UPRIGHT:
            case DOWNRIGHT:
                Gui.drawRect(
                        hudX - textBoxWidth - 1,
                        hudY - 1,
                        hudX + 1,
                        hudY + textBoxHeight,
                        0x32000000
                );
                break;

            case UPLEFT:
            case DOWNLEFT:
                Gui.drawRect(
                        hudX - 2,
                        hudY - 1,
                        hudX + textBoxWidth + 1,
                        hudY + textBoxHeight,
                        0x32000000
                );
                break;
        }
    }

    private static int getMaxWidth() {
        int max = 0;
        for (EventRenderData d : renderCache) {
            max = Math.max(max, d.widthRight);
        }
        return max;
    }

    public static String calculateCountdown(long target) {

        long diff = target - System.currentTimeMillis();
        if (diff < 0) diff = 0;

        long totalSeconds = diff / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder(8);
        if (hours > 0) sb.append(hours).append("h");
        if (minutes > 0) sb.append(minutes).append("m");
        sb.append(seconds).append("s");

        return sb.toString();
    }

    public static PositionMode getPostitionMode(int marginX, int marginY, double height, double width) {
        int halfHeight = (int)(height / 4);
        int halfWidth = (int) width;
        PositionMode positionMode = null;
        // up left

        if(marginY < halfHeight) {
            if(marginX < halfWidth) {
                positionMode = PositionMode.UPLEFT;
            }
            if(marginX > halfWidth) {
                positionMode = PositionMode.UPRIGHT;
            }
        }

        if(marginY > halfHeight) {
            if(marginX < halfWidth) {
                positionMode = PositionMode.DOWNLEFT;
            }
            if(marginX > halfWidth) {
                positionMode = PositionMode.DOWNRIGHT;
            }
        }

        return positionMode;
    }

    public static int loopColorAlpha() {
        long cycleTime = 1200;
        long elapsedTime = System.currentTimeMillis() % cycleTime;
        int alpha;
        if (elapsedTime < cycleTime / 2) {
            alpha = (int) ((elapsedTime * 160) / (cycleTime / 2));
        } else {
            alpha = (int) (160 - ((elapsedTime - cycleTime / 2) * 160) / (cycleTime / 2));
        }
        return 255 - alpha;
    }

    public static int astolfoColorsDraw(int yOffset, int yTotal, float speed) {
        float hue = (float) (System.currentTimeMillis() % (int)speed) + ((yTotal - yOffset) * 9);
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5F - (hue - 0.5f);
        }
        hue += 0.5F;
        return Color.HSBtoRGB(hue, 0.5f, 1F);
    }
    public static void drawRectDouble(double left, double top, double right, double bottom, int color)
    {
        if (left < right)
        {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            double j = top;
            top = bottom;
            bottom = j;
        }
        GlStateManager.pushMatrix();
        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static class EventRenderData {

        final EventData source;
        final String rightText;
        final String leftText;
        final int widthRight;
        final int widthLeft;

        EventRenderData(EventData data) {
            this.source = data;
            String cd = calculateCountdown(data.getTimestamp());
            rightText = data.getEvent() + " - " + cd;
            leftText = cd + " - " + data.getEvent();
            widthRight = fontRendererObj.getStringWidth(rightText);
            widthLeft = fontRendererObj.getStringWidth(leftText);
        }
    }

    public enum PositionMode {
        UPLEFT,
        UPRIGHT,
        DOWNLEFT,
        DOWNRIGHT
    }

    public enum PitEvent {

        SQUADS("Squads", new Color(0x3396FF), () -> new ItemStack(Items.banner, 1, 12)),
        DRAGON_EGG("Dragon Egg", new Color(255, 36, 215), () -> new ItemStack(Blocks.dragon_egg)),
        PIZZA("Pizza", new Color(255, 100, 100), PitEvent::createPizzaHead),
        KOTH("KOTH", new Color(117, 255, 247), () -> new ItemStack(Blocks.beacon)),
        RAFFLE("Raffle", new Color(0xFFFF29), () -> new ItemStack(Items.name_tag)),
        KOTL("KOTL", new Color(140, 255, 158), () -> new ItemStack(Blocks.ladder)),
        BLOCKHEAD("Blockhead", new Color(0x2C2CFF), () -> new ItemStack(Blocks.brick_block)),
        DOUBLE_REWARDS("2x Rewards", new Color(19, 227, 51), () -> new ItemStack(Items.emerald)),
        SPIRE("Spire", new Color(0xF829F5), () -> new ItemStack(Items.magma_cream)),
        CARE_PACKAGE("Care Package", new Color(255, 189, 65), () -> new ItemStack(Blocks.chest)),
        TEAM_DEATHMATCH("Team Deathmatch", new Color(0x9428FF), () -> new ItemStack(Blocks.wool, 1, 10)),
        AUCTION("Auction", new Color(255, 182, 68), () -> new ItemStack(Items.golden_horse_armor)),
        ROBBERY("Robbery", new Color(0xFFFD29), () -> new ItemStack(Items.gold_nugget)),
        QUICK_MATHS("Quick Maths", new Color(238, 6, 255), () -> new ItemStack(Items.writable_book)),
        RAGE_PIT("Rage Pit", new Color(0xFF2925), () -> new ItemStack(Items.baked_potato)),
        GIANT_CAKE("Giant Cake", new Color(253, 90, 255), () -> new ItemStack(Items.cake)),
        BEAST("Beast", new Color(0x29FF53), () -> new ItemStack(Items.diamond_chestplate)),
        ALL_BOUNTY("All bounty", new Color(255, 168, 31), () -> new ItemStack(Items.gold_ingot));

        private final String displayName;
        private final Color color;
        private final Supplier<ItemStack> itemSupplier;

        PitEvent(String displayName, Color color, Supplier<ItemStack> itemSupplier) {
            this.displayName = displayName;
            this.color = color;
            this.itemSupplier = itemSupplier;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Color getColor() {
            return color;
        }

        public ItemStack createItemStack() {
            return itemSupplier.get();
        }

        public static PitEvent fromName(String name) {
            for (PitEvent event : values()) {
                if (event.displayName.equalsIgnoreCase(name)) {
                    return event;
                }
            }
            return SQUADS;
        }

        private static ItemStack createPizzaHead() {
            try {
                NBTTagCompound tag = JsonToNBT.getTagFromJson(
                        "{display:{Name:\"Pizza Pile\"},SkullOwner:{Id:\"9f4a6c53-336f-4a31-bbba-f766a11cc984\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkzMGU2NTZiNDgzOGVjZWRiZDFkMWQzOTMxZWFkMGYyN2YxNjc5OGE1OTJmZWZkMWQxYjZmMTE2NzM0MTcifX19\"}]}}}"
                );
                ItemStack stack = new ItemStack(Items.skull, 1, 3);
                stack.setTagCompound(tag);
                return stack;
            } catch (Exception e) {
                return new ItemStack(Items.skull, 1, 3);
            }
        }
    }

    public static int getHudX() {
        return hudX;
    }

    public static int getHudY() {
        return hudY;
    }

    public static void setHudX(int hudX) {
        PitEventHUD.hudX = hudX;
    }

    public static void setHudY(int hudY) {
        PitEventHUD.hudY = hudY;
    }
}
