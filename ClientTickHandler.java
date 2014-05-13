package autoseeding;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class ClientTickHandler {

	int prev_blockHitWait = 0;
	int targetBlockId = 0;
	int targetBlockMetadata = 0;
	Coord blockCoord = new Coord();
	int sideHit = 0;

	Queue<Coord> nextTarget = new LinkedList<Coord>();
	Set<Coord> vectors = new LinkedHashSet();

	int count = 0;
    @SubscribeEvent
    public void tickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !FMLClientHandler.instance().isGUIOpen(GuiChat.class)) {
            onTickInGame();
        }
    }

	private int getSeedIndex() {
		Minecraft mc = Minecraft.getMinecraft();
        if(mc == null) return -1;
        if(mc.thePlayer == null) return -1;

		int max = AutoSeeding.config.use_inventory ? mc.thePlayer.inventory.mainInventory.length : 9;
		for(int iInventory = 0; iInventory < max; iInventory++) {
			ItemStack itemStack = mc.thePlayer.inventory.mainInventory[iInventory];
			if(itemStack == null) {
				continue;
			}
            Item item = itemStack.getItem();
            if(item != null && item instanceof ItemSeeds) {
                return iInventory;
            }
		}
		return -1;
	}

	public void onTickInGame() {
		if(AutoSeeding.config.getMode() == EnumMode.OFF) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();

		int index = getSeedIndex();
		if(index == -1) {
			return;
		}

		int distance = AutoSeeding.config.distance;
		int width = distance / 2 + 1;
		int posX = (int)Math.round(mc.thePlayer.posX);
		int posY = (int)Math.round(mc.thePlayer.posY);
		int posZ = (int)Math.round(mc.thePlayer.posZ);
		for(int x = posX - width; x <= posX + width; ++x) {
			for(int z = posZ - width; z <= posZ + width; ++z) {
				for(int y = posY - 3; y <= posY + 1; ++y) {
                    Block underBlock = mc.theWorld.getBlock(x, y - 1, z);
					if(!mc.theWorld.isAirBlock(x, y, z)) continue;
					if(underBlock != Block.getBlockFromName("farmland")) continue;
					ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(index);
					Item itemSeeds = itemStack.getItem();
					if(itemSeeds instanceof ItemSeeds) {
						if(itemSeeds.onItemUse(itemStack, mc.thePlayer, mc.theWorld, x, y-1, z, 1, 0, 0, 0)) {
							sendPacket(EnumCommand.SEED, index, new Coord(x,y,z));
						}
					}
				}
			}
		}
	}

	private void sendPacket(EnumCommand command, int index, Coord pos) {
        AutoSeeding.packetPipeline.sendPacketToServer(new PacketHandler(command, index, pos));
	}

}
