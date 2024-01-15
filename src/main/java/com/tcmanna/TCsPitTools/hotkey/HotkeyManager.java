package com.tcmanna.TCsPitTools.hotkey;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class HotkeyManager {
    List<KeyBase> keyList = new ArrayList<>();

    public HotkeyManager() {
        addKey(new SendMsgKey("tcpt.key.respawn", Keyboard.KEY_NONE, "/respawn"));
        addKey(new SendMsgKey("tcpt.key.oof", Keyboard.KEY_NONE, "/oof"));
        addKey(new OpenConfigGuiKey("tcpt.key.opencfg", Keyboard.KEY_NONE));
    }

    private void addKey(KeyBase key) {
        keyList.add(key);
    }

    public void registerKeys() {
        if (keyList.isEmpty())
            return;
        for (KeyBase key : keyList) {
            key.registerKey();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (keyList.isEmpty())
            return;
        for (KeyBase key : keyList) {
            if (key.getKeyBinding().isPressed()) {
                key.execute();
                break;
            }
        }
    }
}
