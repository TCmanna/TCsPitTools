package com.tcmanna.TCsPitTools.inGameEvent;

import com.tcmanna.TCsPitTools.TCsPitTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PitEventHUD {
    private static int hudX = 5;
    private static int hudY = 50;
    public static PositionMode positionMode;

    private Minecraft mc;

    public void enable() {
        mc = Minecraft.getMinecraft();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void disable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void a(TickEvent.RenderTickEvent ev) {
        if (ev.phase == TickEvent.Phase.END && mc.thePlayer != null) {
            if (!(mc.currentScreen instanceof GuiChat)) {
                if (mc.currentScreen != null || mc.gameSettings.showDebugInfo) {
                    return;
                }
            }

            long currentTimestamp = System.currentTimeMillis();
            List<EventData> eventDataList = new ArrayList<>(PitEventManager.filterByTimestampAndSort(TCsPitTools.pitEventManager.getEventList(), currentTimestamp));
            if (eventDataList.isEmpty()) {
                return;
            }
            addEventText(eventDataList, currentTimestamp);
        }

    }

    public static void addEventText(List<EventData> eventDataList, long currentTimestamp) {
        Minecraft mc = Minecraft.getMinecraft();
        int margin = 2;
        int y = hudY;
        int del = 0;

        ScaledResolution sr = new ScaledResolution(mc);
        PitEventHUD.positionMode = getPostitionMode(hudX, hudY, sr.getScaledWidth(), sr.getScaledHeight());


        if (eventDataList.size() > TCsPitTools.config_event_maxShowEvent) {
            eventDataList = eventDataList.subList(0, TCsPitTools.config_event_maxShowEvent);
        }

        int textBoxWidth = getLongestActiveModule(mc.fontRendererObj, eventDataList);
        int textBoxHeight = getBoxHeight(mc.fontRendererObj, margin, eventDataList);

        if (hudX < 0) {
            hudX = margin;
        }
        if (hudY < 0) {
            hudY = margin;
        }

//         if (hudX + textBoxWidth > mc.displayWidth / 2) {
//            hudX = mc.displayWidth / 2 - textBoxWidth - margin;
//         }

        if (hudY + textBoxHeight > mc.displayHeight / 2) {
            hudY = mc.displayHeight / 2 - textBoxHeight;
        }

        //draw
        if (PitEventHUD.positionMode == PositionMode.DOWNRIGHT || PitEventHUD.positionMode == PositionMode.DOWNLEFT)
            Collections.reverse(eventDataList);
        if (TCsPitTools.config_event_reverseOrderSort)
            Collections.reverse(eventDataList);

        for (EventData event : eventDataList) {
            PositionMode positionMode = PitEventHUD.positionMode;
            if (positionMode == null)
                break;
            long lastTime = event.getTimestamp() - currentTimestamp;
            switch (positionMode) {
                //右侧
                case UPRIGHT:
                case DOWNRIGHT: {
                    String showString = event.getEvent() + " - " + calculateCountdown(event.getTimestamp(), currentTimestamp);
                    int startX = hudX + (textBoxWidth - mc.fontRendererObj.getStringWidth(showString));

                    //last60s
                    if (lastTime < 60000) {
                        Color color = event.getColor();
                        int colorRGB = new Color(color.getRed(), color.getGreen(), color.getBlue(), loopColorAlpha(currentTimestamp)).getRGB();
                        int endX = hudX + textBoxWidth;
                        Gui.drawRect(
                                startX - 1,
                                y - 1,
                                endX + 1,
                                y + mc.fontRendererObj.FONT_HEIGHT,
                                colorRGB
                        );
                        int distance = endX - startX;
                        drawRectDouble(
                                startX + (distance - (distance * ((double)lastTime/(double)60000))),
                                y + mc.fontRendererObj.FONT_HEIGHT - 1,
                                endX + 1,
                                y + mc.fontRendererObj.FONT_HEIGHT,
                                0xFFFFFFFF
                        );
                        mc.fontRendererObj.drawString(showString, (float) startX, (float) y, new Color(0xFFFFFF).getRGB(), TCsPitTools.config_event_dropShadow);
                        y += mc.fontRendererObj.FONT_HEIGHT + margin;
                    }
                    else {
                        switch (TCsPitTools.config_event_disMode) {
                            case 1: {
                                if (event.getType().equals("major")) {
                                    Color color = event.getColor();
                                    int colorRGB = new Color(color.getRed(), color.getGreen(), color.getBlue(), 102).getRGB();
                                    Gui.drawRect(
                                            startX - 1,
                                            y - 1,
                                            hudX + textBoxWidth + 1,
                                            y + mc.fontRendererObj.FONT_HEIGHT,
                                            colorRGB
                                    );
                                }
                                mc.fontRendererObj.drawString(showString, (float) startX, (float) y, astolfoColorsDraw(10, del, 2900F), TCsPitTools.config_event_dropShadow);
                                y += mc.fontRendererObj.FONT_HEIGHT + margin;
                                del -= 120;
                                break;
                            }
                            case 2: {
                                Color color = event.getColor();
                                int colorRGB = new Color(color.getRed(), color.getGreen(), color.getBlue(), 102).getRGB();
                                Gui.drawRect(
                                        startX - 1,
                                        y - 1,
                                        hudX + textBoxWidth + 1,
                                        y + mc.fontRendererObj.FONT_HEIGHT,
                                        colorRGB
                                );
                                mc.fontRendererObj.drawString(showString, (float) startX, (float) y, 0xFFFFFF, TCsPitTools.config_event_dropShadow);
                                y += mc.fontRendererObj.FONT_HEIGHT + margin;

                                break;
                            }
                            case 3: {
                                Color color = event.getColor();
                                int colorRGB = new Color(color.getRed(), color.getGreen(), color.getBlue(), 110).getRGB();
                                if (event.getType().equals("major")) {
                                    Gui.drawRect(
                                            startX - 1,
                                            y - 1,
                                            hudX + textBoxWidth + 1,
                                            y + mc.fontRendererObj.FONT_HEIGHT,
                                            new Color(0x4DFFFFFF, true).getRGB()
                                    );
                                }
                                mc.fontRendererObj.drawString(showString, (float) startX, (float) y, colorRGB, TCsPitTools.config_event_dropShadow);
                                y += mc.fontRendererObj.FONT_HEIGHT + margin;

                                break;
                            }
                        }

                    }
                    break;
                }

                //左侧
                case UPLEFT:
                case DOWNLEFT: {
                    String showString = calculateCountdown(event.getTimestamp(), currentTimestamp) + " - " + event.getEvent();

                    //last60s
                    if (lastTime < 60000) {
                        Color color = event.getColor();
                        int colorRGB = new Color(color.getRed(), color.getGreen(), color.getBlue(), loopColorAlpha(currentTimestamp)).getRGB();
                        int endX = hudX + mc.fontRendererObj.getStringWidth(showString);
                        Gui.drawRect(
                                hudX - 1,
                                y - 1,
                                endX + 1,
                                y + mc.fontRendererObj.FONT_HEIGHT,
                                colorRGB
                        );
                        int distance = endX - hudX;
                        drawRectDouble(
                                hudX - 1,
                                y + mc.fontRendererObj.FONT_HEIGHT - 1,
                                endX - (distance - (distance * ((double)lastTime/(double)60000))),
                                y + mc.fontRendererObj.FONT_HEIGHT,
                                0xFFFFFFFF
                        );
                        mc.fontRendererObj.drawString(showString, (float) hudX, (float) y, new Color(0xffffff).getRGB(), TCsPitTools.config_event_dropShadow);
                        y += mc.fontRendererObj.FONT_HEIGHT + margin;
                    } else {

                        switch (TCsPitTools.config_event_disMode) {
                            case 1: {
                                if (event.getType().equals("major")) {
                                    Color color = event.getColor();
                                    int colorRGB = new Color(color.getRed(), color.getGreen(), color.getBlue(), 102).getRGB();
                                    Gui.drawRect(
                                            hudX - 1,
                                            y - 1,
                                            hudX + mc.fontRendererObj.getStringWidth(showString) + 1,
                                            y + mc.fontRendererObj.FONT_HEIGHT,
                                            colorRGB
                                    );
                                }
                                mc.fontRendererObj.drawString(showString, (float) hudX, (float) y, astolfoColorsDraw(10, del, 2900F), TCsPitTools.config_event_dropShadow);
                                y += mc.fontRendererObj.FONT_HEIGHT + margin;
                                del -= 120;
                                break;
                            }
                            case 2: {
                                Color color = event.getColor();
                                int colorRGB = new Color(color.getRed(), color.getGreen(), color.getBlue(), 102).getRGB();
                                Gui.drawRect(
                                        hudX - 1,
                                        y - 1,
                                        hudX + mc.fontRendererObj.getStringWidth(showString) + 1,
                                        y + mc.fontRendererObj.FONT_HEIGHT,
                                        colorRGB
                                );
                                mc.fontRendererObj.drawString(showString, (float) hudX, (float) y, 0xFFFFFF, TCsPitTools.config_event_dropShadow);
                                y += mc.fontRendererObj.FONT_HEIGHT + margin;

                                break;
                            }
                            case 3: {
                                Color color = event.getColor();
                                int colorRGB = new Color(color.getRed(), color.getGreen(), color.getBlue(), 120).getRGB();
                                if (event.getType().equals("major")) {
                                    Gui.drawRect(
                                            hudX - 1,
                                            y - 1,
                                            hudX + mc.fontRendererObj.getStringWidth(showString) + 1,
                                            y + mc.fontRendererObj.FONT_HEIGHT,
                                            new Color(0x4DFFFFFF, true).getRGB()
                                    );
                                }
                                mc.fontRendererObj.drawString(showString, (float) hudX, (float) y, colorRGB, TCsPitTools.config_event_dropShadow);
                                y += mc.fontRendererObj.FONT_HEIGHT + margin;

                                break;
                            }
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    public static int getLongestActiveModule(FontRenderer fr, List<EventData> eventDataList) {
        int length = 0;
        long currentTimestamp = System.currentTimeMillis();
        for(EventData eventData : eventDataList) {
            String showString = eventData.getEvent() + " - " + calculateCountdown(eventData.getTimestamp(), currentTimestamp);
            if(fr.getStringWidth(showString) > length) {
                length = fr.getStringWidth(showString);

            }
        }
        return length;
    }

    public static int getBoxHeight(FontRenderer fr, int margin, List<EventData> eventDataList) {
        int length = 0;
        for(EventData ignored : eventDataList) {
            length += fr.FONT_HEIGHT + margin;
        }
        return length;
    }

    public static String calculateCountdown(long targetTimestampMillis, long currentTimestampMillis) {
        // 计算时间差（毫秒）
        long timeDifferenceMillis = targetTimestampMillis - currentTimestampMillis;

        // 将时间差转换为小时、分钟和秒
        long hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis) % 60;

        // 以"*h*m*s"格式表示结果
        return hours + "h" + minutes + "m" + seconds + "s";
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

    public static int loopColorAlpha(long time) {
        long cycleTime = 1200;
        long elapsedTime = time % cycleTime;
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
    }


    public enum PositionMode {
        UPLEFT,
        UPRIGHT,
        DOWNLEFT,
        DOWNRIGHT
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
