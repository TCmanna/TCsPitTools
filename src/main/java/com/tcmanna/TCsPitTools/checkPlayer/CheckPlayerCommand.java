package com.tcmanna.TCsPitTools.checkPlayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CheckPlayerCommand  extends CommandBase {

    public static IInventory checkPlayerInv;

    @Override
    public String getCommandName() {
        return "check";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/check <username>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }
        String inputName = args[0];
        Minecraft mc = Minecraft.getMinecraft();
        for (EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
            if (inputName.equalsIgnoreCase(entityPlayer.getName())) {
                ClientEvent.toggleCheckArmorGui();
                checkPlayerInv = new InventoryBasic(entityPlayer.getDisplayName(), 9);
                List<ItemStack> list = new ArrayList<>(Arrays.asList(entityPlayer.inventory.armorInventory));
                Collections.reverse(list);
                for (int i = 0; i < list.size(); i++) {
                    checkPlayerInv.setInventorySlotContents(i, list.get(i));
                    checkPlayerInv.setInventorySlotContents(8, entityPlayer.getHeldItem());
                }
                return;
            }
        }
        sender.addChatMessage(new ChatComponentText("未找到玩家"));

    }
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length > 1)
            return null;
        List<String> list = sender.getEntityWorld().playerEntities.stream()
                .filter(entityPlayer -> !ClientEvent.bot(entityPlayer))
                .map(EntityPlayer::getName)
                .collect(Collectors.toList());
        if (!args[0].equals("")) {
            for (String s : list) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase()))
                    return new ArrayList<>(Collections.singletonList(s));
            }
        }
        else return list;
        if (list.contains(args[0])) {
            list.subList(0, list.indexOf(args[0]));
            return list;
        }
        return null;
    }


    public static class GuiCheckPlayerInv extends GuiContainer {
        static IInventory checkInventory;

        private final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
        private final int inventoryRows;

        public GuiCheckPlayerInv(EntityPlayer player, IInventory checkInv) {
            super(new ContainerEmptyBox(player, checkInv));
            checkInventory = checkInv;
            int i = 222;
            int j = i - 108;
            this.inventoryRows = checkInventory.getSizeInventory() / 9;
            this.ySize = j + this.inventoryRows * 18;
        }

        @Override
        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
            this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton){}

        @Override
        protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {}

        @Override
        protected void mouseReleased(int mouseX, int mouseY, int state) {}

        @Override
        protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {}

        @Override
        public void onGuiClosed() {}
    }

    public static class ContainerEmptyBox extends Container {

        public ContainerEmptyBox(EntityPlayer player, IInventory showInv) {
            int numRows = showInv.getSizeInventory() / 9;
            int i = (numRows - 4) * 18;

            for (int j = 0; j < numRows; ++j)
            {
                for (int k = 0; k < 9; ++k)
                {
                    this.addSlotToContainer(new SlotLocked(showInv, k + j * 9, 8 + k * 18, 18 + j * 18));
                }
            }

            // 添加玩家的槽位
            for (int l = 0; l < 3; ++l)
            {
                for (int j1 = 0; j1 < 9; ++j1)
                {
                    this.addSlotToContainer(new SlotLocked(player.inventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
                }
            }

            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.addSlotToContainer(new SlotLocked(player.inventory, i1, 8 + i1 * 18, 161 + i));
            }
        }

        @Override
        public boolean canInteractWith(EntityPlayer player) {
            return false;
        }

    }

    public static class SlotLocked extends Slot {

        public SlotLocked(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        // 重写 Slot 的所有方法，并使其不执行任何操作，即禁用槽位的所有操作
        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack decrStackSize(int amount) {
            return null;
        }

        @Override
        public void putStack(ItemStack stack) {}

        @Override
        public boolean canTakeStack(EntityPlayer playerIn) {
            return false;
        }
    }
}
