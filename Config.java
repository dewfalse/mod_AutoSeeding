package autoseeding;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

import java.io.File;

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
			FMLLog.log(Level.ERROR, e, "AutoSeeding load config exception");
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

}
