package com.tcmanna.TCsPitTools.feature.inGameEvent;

import com.tcmanna.TCsPitTools.config.ConfigManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.io.IOException;

public class EditHudPositionScreen extends GuiScreen {
    GuiButtonExt resetPosButton;
    GuiButtonExt confirmButton;
    boolean mouseDown = false;
    int textBoxStartX = 0;
    int textBoxStartY = 0;
    int textBoxEndX = 0;
    int textBoxEndY = 0;
    int marginX = 5;
    int marginY = 50;
    int lastMousePosX = 0;
    int lastMousePosY = 0;
    PitEventHUD.PositionMode lastPositionMode = PitEventHUD.PositionMode.DOWNLEFT;
    int sessionMousePosX = 0;
    int sessionMousePosY = 0;

    public void initGui() {
        super.initGui();
        this.buttonList.add(this.resetPosButton = new GuiButtonExt(1, this.width - 90, 5, 85, 20, "RESET"));
        this.buttonList.add(this.confirmButton = new GuiButtonExt(2, this.width - 90, this.height - 50, 85, 20, "CONFIRM"));
        this.marginX = PitEventHUD.getHudX();
        this.marginY = PitEventHUD.getHudY();
        ScaledResolution sr = new ScaledResolution(mc);
        PitEventHUD.positionMode = PitEventHUD.getPostitionMode(marginX, marginY, sr.getScaledWidth(), sr.getScaledHeight());
    }

    public void drawScreen(int mX, int mY, float pt) {
        drawRect(0, 0, this.width, this.height, 0x32000000);
        drawRect(0, this.height /2, this.width, this.height /2 + 1, 0x9936393f);
        drawRect(this.width /2, 0, this.width /2 + 1, this.height, 0x9936393f);
        int textBoxStartX = this.marginX;
        int textBoxStartY = this.marginY;
        this.textBoxStartX = textBoxStartX - 1;
        this.textBoxStartY = textBoxStartY - 1;

        this.textBoxEndX = textBoxStartX + PitEventHUD.textBoxWidth + 1;
        this.textBoxEndY = textBoxStartY + PitEventHUD.textBoxHeight + 1;

        PitEventHUD.setHudX(textBoxStartX);
        PitEventHUD.setHudY(textBoxStartY);
        ScaledResolution res = new ScaledResolution(mc);
        int descriptionOffsetX = res.getScaledWidth() / 2 - 84;
        int descriptionOffsetY = res.getScaledHeight() / 2 - 20;

        this.drawArrayList();

        drawColouredText("Edit the HUD position by dragging.", '-', descriptionOffsetX, descriptionOffsetY, 2L, 0L, true, mc.fontRendererObj);

        try {
            this.handleInput();
        } catch (IOException ignored) {
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

    private void drawArrayList() {
        if (PitEventHUD.positionMode == null) return;
        GlStateManager.pushMatrix();
        double scale = ConfigManager.config_event_scale.getDouble();
        GlStateManager.scale(scale, scale, scale);
        PitEventHUD.drawCachedList(true);
        GlStateManager.popMatrix();
    }

    protected void mouseClickMove(int mousePosX, int mousePosY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mousePosX, mousePosY, clickedMouseButton, timeSinceLastClick);
        double scale = ConfigManager.config_event_scale.getDouble();
        mousePosX = (int) (mousePosX / scale);
        mousePosY = (int) (mousePosY / scale);
        if (clickedMouseButton == 0) {
            if (this.mouseDown) {
                this.marginX = this.lastMousePosX + (mousePosX - this.sessionMousePosX);
                this.marginY = this.lastMousePosY + (mousePosY - this.sessionMousePosY);
                ScaledResolution sr = new ScaledResolution(mc);
                PitEventHUD.positionMode = PitEventHUD.getPostitionMode(marginX, marginY, sr.getScaledWidth(), sr.getScaledHeight());
                if (lastPositionMode == PitEventHUD.PositionMode.UPRIGHT || lastPositionMode == PitEventHUD.PositionMode.DOWNRIGHT) {
                    if (PitEventHUD.positionMode == PitEventHUD.PositionMode.UPLEFT || PitEventHUD.positionMode == PitEventHUD.PositionMode.DOWNLEFT) {
                        this.marginX -= PitEventHUD.textBoxWidth;
                    }
                }
                if (lastPositionMode == PitEventHUD.PositionMode.UPLEFT || lastPositionMode == PitEventHUD.PositionMode.DOWNLEFT) {
                    if (PitEventHUD.positionMode == PitEventHUD.PositionMode.UPRIGHT || PitEventHUD.positionMode == PitEventHUD.PositionMode.DOWNRIGHT) {
                        this.marginX += PitEventHUD.textBoxWidth;
                    }
                }
            } else {
                if (PitEventHUD.positionMode == null) return;
                switch (PitEventHUD.positionMode) {
                    case UPRIGHT:
                    case DOWNRIGHT: {
                        if (mousePosX > textBoxStartX - (textBoxEndX - textBoxStartX) && mousePosX < textBoxStartX &&
                                mousePosY > this.textBoxStartY && mousePosY < this.textBoxEndY) {
                            this.mouseDown = true;
                            this.sessionMousePosX = mousePosX;
                            this.sessionMousePosY = mousePosY;
                            this.lastMousePosX = this.marginX;
                            this.lastMousePosY = this.marginY;
                        }
                        break;
                    }
                    case UPLEFT:
                    case DOWNLEFT: {
                        if (mousePosX > this.textBoxStartX && mousePosX < this.textBoxEndX &&
                                mousePosY > this.textBoxStartY && mousePosY < this.textBoxEndY) {
                            this.mouseDown = true;
                            this.sessionMousePosX = mousePosX;
                            this.sessionMousePosY = mousePosY;
                            this.lastMousePosX = this.marginX;
                            this.lastMousePosY = this.marginY;
                        }
                        break;
                    }
                }
                this.lastPositionMode = PitEventHUD.positionMode;
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
            ConfigManager.changeEventPos(new int[] {PitEventHUD.getHudX(), PitEventHUD.getHudY()});
            mc.displayGuiScreen(null);
        }

    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void onGuiClosed() {
        ConfigManager.changeEventPos(new int[] {PitEventHUD.getHudX(), PitEventHUD.getHudY()});
    }

}
