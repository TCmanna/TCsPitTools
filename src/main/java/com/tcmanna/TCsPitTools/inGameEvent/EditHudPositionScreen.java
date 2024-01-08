package com.tcmanna.TCsPitTools.inGameEvent;

import com.tcmanna.TCsPitTools.TCsPitTools;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditHudPositionScreen extends GuiScreen {
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
        this.marginX = PitEventHUD.getHudX();
        this.marginY = PitEventHUD.getHudY();
        sr = new ScaledResolution(mc);
        PitEventHUD.positionMode = PitEventHUD.getPostitionMode(marginX, marginY, sr.getScaledWidth(), sr.getScaledHeight());
    }

    public void drawScreen(int mX, int mY, float pt) {
        drawRect(0, 0, this.width, this.height, -1308622848);
        drawRect(0, this.height /2, this.width, this.height /2 + 1, 0x9936393f);
        drawRect(this.width /2, 0, this.width /2 + 1, this.height, 0x9936393f);
        int textBoxStartX = this.marginX;
        int textBoxStartY = this.marginY;

        long currentTimestamp = System.currentTimeMillis();

        List<EventData> eventDataList = new ArrayList<>(PitEventManager.filterByTimestampAndSort(TCsPitTools.pitEventManager.getEventList(), currentTimestamp));
        if (!eventDataList.isEmpty()) {
            this.drawArrayList(eventDataList, currentTimestamp);
        }
        this.textBoxStartX = textBoxStartX - 1;
        this.textBoxStartY = textBoxStartY - 1;
        if (eventDataList.size() > TCsPitTools.config_event_maxShowEvent) {
            eventDataList = eventDataList.subList(0, TCsPitTools.config_event_maxShowEvent);
        }
        int textBoxWidth = PitEventHUD.getLongestActiveModule(mc.fontRendererObj, eventDataList);
        int textBoxHeight = PitEventHUD.getBoxHeight(mc.fontRendererObj, 2, eventDataList);
        this.textBoxEndX = textBoxStartX + textBoxWidth + 1;
        this.textBoxEndY = textBoxStartY + textBoxHeight + 1;
        PitEventHUD.setHudX(textBoxStartX);
        PitEventHUD.setHudY(textBoxStartY);
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
                fontRenderer.drawString(String.valueOf(c), (float)leftOffset, (float)topOffset, PitEventHUD.astolfoColorsDraw((int)colourParam1, (int)colourControl, 2900F), rect);
                leftOffset += fontRenderer.getCharWidth(c);
                if (c != ' ') {
                    colourControl -= 90L;
                }
            }
        }

    }

    private void drawArrayList(List<EventData> eventDataList, long currentTimestamp) {

        PitEventHUD.addEventText(eventDataList, currentTimestamp);

        drawHorizontalLine(textBoxStartX, textBoxEndX, textBoxStartY, 0xFFFFFFFF);
        drawHorizontalLine(textBoxStartX, textBoxEndX, textBoxEndY, 0xFFFFFFFF);
        drawVerticalLine(textBoxStartX, textBoxStartY, textBoxEndY, 0xFFFFFFFF);
        drawVerticalLine(textBoxEndX, textBoxStartY, textBoxEndY, 0xFFFFFFFF);
    }

    protected void mouseClickMove(int mousePosX, int mousePosY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mousePosX, mousePosY, clickedMouseButton, timeSinceLastClick);
        if (clickedMouseButton == 0) {
            if (this.mouseDown) {
                this.marginX = this.lastMousePosX + (mousePosX - this.sessionMousePosX);
                this.marginY = this.lastMousePosY + (mousePosY - this.sessionMousePosY);
                sr = new ScaledResolution(mc);
                PitEventHUD.positionMode = PitEventHUD.getPostitionMode(marginX, marginY,sr.getScaledWidth(), sr.getScaledHeight());

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
            this.marginX = 5;
            PitEventHUD.setHudX(5);
            this.marginY = 50;
            PitEventHUD.setHudY(50);
        }
        if (b == this.confirmButton) {
            TCsPitTools.changeEventPos(new int[] {PitEventHUD.getHudX(), PitEventHUD.getHudY()});
            mc.displayGuiScreen(null);
        }

    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void onGuiClosed()
    {
        TCsPitTools.changeEventPos(new int[] {PitEventHUD.getHudX(), PitEventHUD.getHudY()});
    }
}
