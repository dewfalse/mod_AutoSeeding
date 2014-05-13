package autoseeding;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy  {

    void init() {
        FMLCommonHandler.instance().bus().register(new ConnectionHandler());
    }

}
