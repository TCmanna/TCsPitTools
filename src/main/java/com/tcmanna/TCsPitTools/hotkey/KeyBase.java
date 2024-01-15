package com.tcmanna.TCsPitTools.hotkey;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBase {
    protected static Minecraft mc;

    private final KeyBinding keyBinding;

    public KeyBase(String description, int DefKeyboard) {
        this.keyBinding = new KeyBinding(description, DefKeyboard, "TCsPitTools");
        mc = Minecraft.getMinecraft();
    }

    public void execute() {
    }

    public void registerKey() {
        ClientRegistry.registerKeyBinding(this.keyBinding);
    }

    public KeyBinding getKeyBinding() {
        return this.keyBinding;
    }
}
