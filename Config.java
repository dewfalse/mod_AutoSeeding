package autoseeding;

import java.io.File;
import java.util.logging.Level;

import net.minecraft.network.INetworkManager;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLLog;

public class Config {
	public static final String channel = "as";
	public static int distance = 1;
	public static boolean use_inventory = true;
	public static EnumMode mode = EnumMode.ON;

	public void load(File file) {
		Configuration cfg = new Configuration(file);
		try {
			cfg.load();
			use_inventory = cfg.get(Configuration.CATEGORY_GENERAL, "use_inventory", true).getBoolean(true);
			if(cfg.get(Configuration.CATEGORY_GENERAL, "mode", true).getBoolean(true)) {
				mode = EnumMode.ON;
			}
			else {
				mode = EnumMode.OFF;
			}
			cfg.save();
		} catch (Exception e) {
			FMLLog.log(Level.SEVERE, e, "AutoSeeding load config exception");
		} finally {
			cfg.save();
		}
	}

	public void toggleMode() {
		for(int i = 0; i < EnumMode.values().length; ++i) {
			if(EnumMode.values()[i].equals(mode)) {
				i = (i + 1) % EnumMode.values().length;
				mode = EnumMode.values()[i];
				break;
			}
		}
	}

	public EnumMode getMode() {
		return mode;
	}

	public void sendTargetToPlayer(INetworkManager manager) {
	}
}
