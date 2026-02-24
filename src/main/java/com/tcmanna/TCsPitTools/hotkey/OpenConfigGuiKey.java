package com.tcmanna.TCsPitTools.hotkey;

import com.tcmanna.TCsPitTools.config.ConfigGui;

public class OpenConfigGuiKey extends KeyBase {

    public OpenConfigGuiKey(String description, int DefKeyboard) {
        super(description, DefKeyboard);
    }

    @Override
    public void execute() {
        mc.displayGuiScreen(new ConfigGui(mc.currentScreen));
    }
}
