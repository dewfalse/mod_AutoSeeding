package autoseeding;

import java.util.logging.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AutoSeeding.modid, name = AutoSeeding.name, version = "1.0")
public class AutoSeeding {
    public static final String modid = "AutoSeeding";
    public static final String name = "AutoSeeding";
	@SidedProxy(clientSide = "autoseeding.ClientProxy", serverSide = "autoseeding.CommonProxy")
	public static CommonProxy proxy;

	@Instance("AutoSeeding")
	public static AutoSeeding instance;

    public static final PacketPipeline packetPipeline = new PacketPipeline();
	public static Logger logger = Logger.getLogger("Minecraft");

	public static Config config = new Config();

    @Mod.EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.init();
        packetPipeline.init(AutoSeeding.modid);
        packetPipeline.registerPacket(PacketHandler.class);
	}

    @Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config.load(event.getSuggestedConfigurationFile());
	}

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        packetPipeline.postInit();
    }
}
