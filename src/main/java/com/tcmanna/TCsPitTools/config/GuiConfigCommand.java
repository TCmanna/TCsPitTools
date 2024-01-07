package com.tcmanna.TCsPitTools.config;

import com.tcmanna.TCsPitTools.TCsPitTools;
import com.tcmanna.TCsPitTools.config.ConfigGui;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class GuiConfigCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "pittools";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/pittools";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        TCsPitTools.toggleConfigGui();
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

}
