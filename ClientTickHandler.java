package autoseeding;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.IPlantable;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {

	int prev_blockHitWait = 0;
	int targetBlockId = 0;
	int targetBlockMetadata = 0;
	Coord blockCoord = new Coord();
	int sideHit = 0;

	Queue<Coord> nextTarget = new LinkedList<Coord>();
	Set<Coord> vectors = new LinkedHashSet();

	int count = 0;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.CLIENT))) {
			GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;
			if (guiscreen == null) {
				onTickInGame();
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	private int getSeedIndex() {
		Minecraft mc = Minecraft.getMinecraft();
		int max = AutoSeeding.config.use_inventory ? mc.thePlayer.inventory.mainInventory.length : 9;
		for(int iInventory = 0; iInventory < max; iInventory++) {
			ItemStack itemStack = mc.thePlayer.inventory.mainInventory[iInventory];
			if(itemStack == null) {
				continue;
			}
			int itemID = itemStack.itemID;
			Item item = Item.itemsList[itemID];
			if(item == null) {
				continue;
			}
			if(item instanceof IPlantable) {
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
					int blockID = mc.theWorld.getBlockId(x, y, z);
					int underBlockID = mc.theWorld.getBlockId(x, y-1, z);
					if(blockID != 0) continue;
					if(underBlockID != Block.tilledField.blockID) continue;
					Block block = Block.blocksList[underBlockID];
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
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);
		try {
			stream.writeUTF(command.toString());
			stream.writeInt(index);
			stream.writeInt(pos.x);
			stream.writeInt(pos.y);
			stream.writeInt(pos.z);

			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = Config.channel;
			packet.data = bytes.toByteArray();
			packet.length = packet.data.length;
			Minecraft mc = Minecraft.getMinecraft();
			mc.thePlayer.sendQueue.addToSendQueue(packet);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
