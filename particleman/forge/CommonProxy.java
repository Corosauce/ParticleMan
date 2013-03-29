package particleman.forge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler
{
    public World mainWorld;
    private int entityId = 0;

    public ParticleMan mod;

    public CommonProxy()
    {
    }

    public void init(ParticleMan pMod)
    {
        mod = pMod;
        //TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
        
    	//EntityRegistry.registerModEntity(EntityScent.class, "EntityScent", entityId++, pMod, 32, 20, false);
    	
    }

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}

}
