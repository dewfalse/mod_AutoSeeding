package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.src.mod_DropTorch.EnumMode;

import org.lwjgl.input.Keyboard;

public class mod_AutoSeeding extends BaseMod {

	@MLProp
	public static int mode_key = 21;

	public enum EnumMode {off, on};
	public static EnumMode enumMode = EnumMode.on;
	@MLProp
	public static String mode = "on";

	@MLProp
	public static boolean use_inventory = true;

	public static int key_push_times = 0;


	@Override
	public String getVersion() {
		return "[1.2.5] AutoSeeding v.0.0.2";
	}

	@Override
	public void load() {
		enumMode = mode.equalsIgnoreCase("on") ? EnumMode.on : EnumMode.off;
		ModLoader.setInGameHook(this, true, true);
	}

	public void printMode(Minecraft minecraft) {
		String s = "AutoSeeding mode : ";
		s += enumMode.toString();
		minecraft.ingameGUI.addChatMessage(s);
	}

	@Override
	public boolean onTickInGame(float f, Minecraft minecraft) {
		if(Keyboard.isKeyDown(mode_key)) {
			key_push_times++;
		}
		else if(key_push_times > 0) {
			enumMode = EnumMode.values()[(enumMode.ordinal() + 1) % EnumMode.values().length];
			key_push_times = 0;
			printMode(minecraft);
		}

		if(enumMode == EnumMode.off) {
			return true;
		}

		int x = (int) minecraft.thePlayer.posX;
		int y = (int) minecraft.thePlayer.posY;
		int z = (int) minecraft.thePlayer.posZ;

		int max = use_inventory ? minecraft.thePlayer.inventory.mainInventory.length : 9;
		for(int iInventory = 0; iInventory < max; iInventory++) {
			ItemStack itemStack = minecraft.thePlayer.inventory.mainInventory[iInventory];
			if(itemStack == null) {
				continue;
			}
			if(itemStack.itemID != Item.seeds.shiftedIndex && itemStack.itemID != Item.pumpkinSeeds.shiftedIndex && itemStack.itemID != Item.melonSeeds.shiftedIndex) {
				continue;
			}

			int width = 1;

			for(int i = -width; i <= width; i++) {
				for(int j = -2; j <= -2; j++) {
					for(int k = -width; k <= width; k++) {
						int blockId = minecraft.theWorld.getBlockId(x + i, y + j, z + k);
						//if(blockId > 0 && Block.blocksList[blockId].blockActivated(minecraft.theWorld, x + i + x_plus, y + j, z + k + z_plus, minecraft.thePlayer)) {
						//	continue;
						//}
						if(blockId < 0 || blockId >= Block.blocksList.length) {
							continue;
						}
						if(Block.blocksList[blockId] == null) {
							continue;
						}
						if(Block.torchWood.canPlaceBlockOnSide(minecraft.theWorld, x + i, y + j, z + k, 1)) {
							boolean b = itemStack.useItem(minecraft.thePlayer, minecraft.theWorld, x + i, y + j, z + k, 1);
							if(b) {
				                if (itemStack.stackSize == 0)
				                {
				                	minecraft.thePlayer.inventory.mainInventory[iInventory] = null;
									minecraft.thePlayer.inventory.onInventoryChanged();
				                }

								return true;
							}
						}
					}
				}
			}
		}

		return true;
	}

}
