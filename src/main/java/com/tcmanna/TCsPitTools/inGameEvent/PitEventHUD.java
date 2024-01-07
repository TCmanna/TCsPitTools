package com.tcmanna.TCsPitTools.inGameEvent;

import com.tcmanna.TCsPitTools.TCsPitTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.io.IOException;
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
            if (mc.currentScreen != null || mc.gameSettings.showDebugInfo) {
                return;
            }

            int margin = 2;
            int y = hudY;
            int del = 0;

            long currentTimestamp = System.currentTimeMillis();

            List<EventData> eventDataList = new ArrayList<>(PitEventManager.filterByTimestampAndSort(TCsPitTools.pitEventManager.getEventList(), currentTimestamp));
            if (eventDataList.isEmpty()) {
                return;
            }

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
                switch (positionMode) {
                    //左侧
                    case UPRIGHT:
                    case DOWNRIGHT: {
                        String showString = event.getEvent() + " - " + calculateCountdown(event.getTimestamp(), currentTimestamp);

                        int showX = hudX + (textBoxWidth - mc.fontRendererObj.getStringWidth(showString));

                        if (event.getTimestamp() - currentTimestamp < 60000) {
                            Color color = event.getColor();
                            int colorRGB = new Color(color.getRed(), color.getGreen(), color.getBlue(), loopColorAlpha(currentTimestamp)).getRGB();
                            Gui.drawRect(
                                    showX - 1,
                                    y - 1,
                                    hudX + textBoxWidth + 1,
                                    y + mc.fontRendererObj.FONT_HEIGHT,
                                    colorRGB
                            );
                            mc.fontRendererObj.drawString(showString, (float) showX, (float) y, new Color(0xFFFFFF).getRGB(), TCsPitTools.config_event_dropShadow);
                            y += mc.fontRendererObj.FONT_HEIGHT + margin;
                        }
                        else {

                            if (event.getType().equals("major")) {
                                Color color = event.getColor();
                                int colorRGB = new Color(color.getRed(), color.getGreen(), color.getBlue(), 102).getRGB();
                                Gui.drawRect(
                                        showX - 1,
                                        y - 1,
                                        hudX + textBoxWidth + 1,
                                        y + mc.fontRendererObj.FONT_HEIGHT,
                                        colorRGB
                                );
                            }

                            mc.fontRendererObj.drawString(showString, (float) showX, (float) y, astolfoColorsDraw(10, del, 2900F), TCsPitTools.config_event_dropShadow);
                            y += mc.fontRendererObj.FONT_HEIGHT + margin;
                            del -= 120;

                        }
                        break;
                    }

                    //右侧
                    case UPLEFT:
                    case DOWNLEFT: {
                        String showString = calculateCountdown(event.getTimestamp(), currentTimestamp) + " - " + event.getEvent();

                        if (event.getTimestamp() - currentTimestamp < 60000) {
                            Color color = event.getColor();
                            int colorRGB = new Color(color.getRed(), color.getGreen(), color.getBlue(), loopColorAlpha(currentTimestamp)).getRGB();
                            Gui.drawRect(
                                    hudX - 1,
                                    y - 1,
                                    hudX + mc.fontRendererObj.getStringWidth(showString) + 1,
                                    y + mc.fontRendererObj.FONT_HEIGHT,
                                    colorRGB
                            );
                            mc.fontRendererObj.drawString(showString, (float) hudX, (float) y, new Color(0xffffff).getRGB(), TCsPitTools.config_event_dropShadow);
                            y += mc.fontRendererObj.FONT_HEIGHT + margin;
                        } else {

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
                        break;
                    }
                    default:
                        break;
                }
            }
        }

    }

    private int getLongestActiveModule(FontRenderer fr, List<EventData> eventDataList) {
        int length = 0;
        long currentTimestamp = System.currentTimeMillis();
        for(EventData eventData : eventDataList) {
            String showString = eventData.getEvent();
            if(fr.getStringWidth(showString) > length) {
                length = fr.getStringWidth(showString);

            }
        }
        return length;
    }

    private int getBoxHeight(FontRenderer fr, int margin, List<EventData> eventDataList) {
        int length = 0;
        for(EventData eventData : eventDataList) {
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

    private static PositionMode getPostitionMode(int marginX, int marginY, double height, double width) {
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

    private int loopColorAlpha(long time) {
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

    public static class EditHudPositionScreen extends GuiScreen {
        final String hudTextExample = "This is an-Example-HUD";
        GuiButtonExt resetPosButton;
        GuiButtonExt confirmButton;
        boolean mouseDown = false;
        int textBoxStartX = 0;
        int textBoxStartY = 0;
        ScaledResolution sr;
        int textBoxEndX = 0;
        int textBoxEndY = 0;
        int marginX = 5;
        int marginY = 50;
        int lastMousePosX = 0;
        int lastMousePosY = 0;
        int sessionMousePosX = 0;
        int sessionMousePosY = 0;

        public void initGui() {
            super.initGui();
            this.buttonList.add(this.resetPosButton = new GuiButtonExt(1, this.width - 90, 5, 85, 20, I18n.format("tcpt.button.resetpos")));
            this.buttonList.add(this.confirmButton = new GuiButtonExt(2, this.width - 90, this.height - 50, 85, 20, I18n.format("tcpt.button.confirm")));
            this.marginX = PitEventHUD.hudX;
            this.marginY = PitEventHUD.hudY;
            sr = new ScaledResolution(mc);
            PitEventHUD.positionMode = getPostitionMode(marginX, marginY, sr.getScaledWidth(), sr.getScaledHeight());
        }

        public void drawScreen(int mX, int mY, float pt) {
            drawRect(0, 0, this.width, this.height, -1308622848);
            drawRect(0, this.height /2, this.width, this.height /2 + 1, 0x9936393f);
            drawRect(this.width /2, 0, this.width /2 + 1, this.height, 0x9936393f);
            int textBoxStartX = this.marginX;
            int textBoxStartY = this.marginY;
            int textBoxEndX = textBoxStartX + 50;
            int textBoxEndY = textBoxStartY + 32;
            this.drawArrayList(this.mc.fontRendererObj, this.hudTextExample);
            this.textBoxStartX = textBoxStartX;
            this.textBoxStartY = textBoxStartY;
            this.textBoxEndX = textBoxEndX;
            this.textBoxEndY = textBoxEndY;
            PitEventHUD.hudX = textBoxStartX;
            PitEventHUD.hudY = textBoxStartY;
            ScaledResolution res = new ScaledResolution(this.mc);
            int descriptionOffsetX = res.getScaledWidth() / 2 - 84;
            int descriptionOffsetY = res.getScaledHeight() / 2 - 20;
            drawColouredText("Edit the HUD position by dragging.", '-', descriptionOffsetX, descriptionOffsetY, 2L, 0L, true, this.mc.fontRendererObj);

            try {
                this.handleInput();
            } catch (IOException var12) {
            }

            super.drawScreen(mX, mY, pt);
        }

        private void drawColouredText(String text, char lineSplit, int leftOffset, int topOffset, long colourParam1, long shift, boolean rect, FontRenderer fontRenderer) {
            int bX = leftOffset;
            int l = 0;
            long colourControl = 0L;

            for(int i = 0; i < text.length(); ++i) {
                char c = text.charAt(i);
                if (c == lineSplit) {
                    ++l;
                    leftOffset = bX;
                    topOffset += fontRenderer.FONT_HEIGHT + 5;
                    //reseting text colour?
                    colourControl = shift * (long)l;
                } else {
                    fontRenderer.drawString(String.valueOf(c), (float)leftOffset, (float)topOffset, astolfoColorsDraw((int)colourParam1, (int)colourControl, 2900F), rect);
                    leftOffset += fontRenderer.getCharWidth(c);
                    if (c != ' ') {
                        colourControl -= 90L;
                    }
                }
            }

        }

        private void drawArrayList(FontRenderer fr, String t) {
            int x = this.textBoxStartX;
            int gap = this.textBoxEndX - this.textBoxStartX;
            int y = this.textBoxStartY;
            double marginY = fr.FONT_HEIGHT + 2;
            String[] var4 = t.split("-");
            ArrayList<String> var5 = toArrayList(var4);
            if (PitEventHUD.positionMode == PositionMode.UPLEFT || PitEventHUD.positionMode == PositionMode.UPRIGHT) {
                var5.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2) - mc.fontRendererObj.getStringWidth(o1));
            }
            else if(PitEventHUD.positionMode == PositionMode.DOWNLEFT || PitEventHUD.positionMode == PositionMode.DOWNRIGHT) {
                var5.sort(Comparator.comparingInt(o2 -> mc.fontRendererObj.getStringWidth(o2)));
            }

            if(PitEventHUD.positionMode == PositionMode.DOWNRIGHT || PitEventHUD.positionMode == PositionMode.UPRIGHT) {
                for (String s : var5) {
                    fr.drawString(s, (float) x + (gap - fr.getStringWidth(s)), (float) y, Color.white.getRGB(), TCsPitTools.config_event_dropShadow);
                    y += marginY;
                }
            } else {
                for (String s : var5) {
                    fr.drawString(s, (float) x, (float) y, Color.white.getRGB(), TCsPitTools.config_event_dropShadow);
                    y += marginY;
                }
            }
        }
        private ArrayList<String> toArrayList(String[] fakeList){
            return new ArrayList<>(Arrays.asList(fakeList));
        }

        protected void mouseClickMove(int mousePosX, int mousePosY, int clickedMouseButton, long timeSinceLastClick) {
            super.mouseClickMove(mousePosX, mousePosY, clickedMouseButton, timeSinceLastClick);
            if (clickedMouseButton == 0) {
                if (this.mouseDown) {
                    this.marginX = this.lastMousePosX + (mousePosX - this.sessionMousePosX);
                    this.marginY = this.lastMousePosY + (mousePosY - this.sessionMousePosY);
                    sr = new ScaledResolution(mc);
                    PitEventHUD.positionMode = getPostitionMode(marginX, marginY,sr.getScaledWidth(), sr.getScaledHeight());

                    //in the else if statement, we check if the mouse is clicked AND inside the "text box"
                } else if (mousePosX > this.textBoxStartX && mousePosX < this.textBoxEndX && mousePosY > this.textBoxStartY && mousePosY < this.textBoxEndY) {
                    this.mouseDown = true;
                    this.sessionMousePosX = mousePosX;
                    this.sessionMousePosY = mousePosY;
                    this.lastMousePosX = this.marginX;
                    this.lastMousePosY = this.marginY;
                }

            }
        }

        protected void mouseReleased(int mX, int mY, int state) {
            super.mouseReleased(mX, mY, state);
            if (state == 0) {
                this.mouseDown = false;
            }

        }

        public void actionPerformed(GuiButton b) {
            if (b == this.resetPosButton) {
                this.marginX = PitEventHUD.hudX = 5;
                this.marginY = PitEventHUD.hudY = 50;
            }
            if (b == this.confirmButton) {
                TCsPitTools.changeEventPos(new int[] {PitEventHUD.hudX, PitEventHUD.hudY});
                mc.displayGuiScreen(null);
            }

        }

        public boolean doesGuiPauseGame() {
            return false;
        }
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
