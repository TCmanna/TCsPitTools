package com.tcmanna.TCsPitTools.hotkey;

public class SendMsgKey extends KeyBase {

    String textString;

    public SendMsgKey(String description, int DefKeyboard, String textString) {
        super(description, DefKeyboard);
        this.textString = textString;
    }

    @Override
    public void execute() {
        mc.thePlayer.sendChatMessage(this.textString);
    }
}
