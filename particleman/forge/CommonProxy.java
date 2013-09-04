package particleman.forge;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import particleman.entities.EntityParticleControllable;
import particleman.items.ItemParticleGlove;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy implements IGuiHandler
{
    public World mainWorld;
    private int entityId = 0;

    public ParticleMan mod;

    public CommonProxy()
    {
    }

    public void preInit(ParticleMan pMod)
    {
    	
    }
    
    public void init(ParticleMan pMod)
    {
        mod = pMod;
        
        pMod.itemGlove = (new ItemParticleGlove(pMod.itemIDStart)).setCreativeTab(CreativeTabs.tabMisc);
        pMod.itemGlove.setUnlocalizedName(ParticleMan.modID + ":particleglove");
        pMod.itemGlove.func_111206_d(ParticleMan.modID + ":particleglove");
        
        LanguageRegistry.addName(pMod.itemGlove, "Particle Glove");
        GameRegistry.addRecipe(new ItemStack(pMod.itemGlove), new Object[] {" LL", "LRR", "LRD", Character.valueOf('L'), Item.leather, Character.valueOf('R'), Item.redstone, Character.valueOf('D'), Item.diamond});
        
        
        //TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
        
    	EntityRegistry.registerModEntity(EntityParticleControllable.class, "EntityParticleControllable", entityId++, pMod, 32, 3, true);
    	
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
