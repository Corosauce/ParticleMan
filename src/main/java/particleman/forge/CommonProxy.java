package particleman.forge;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import particleman.entities.EntityParticleControllable;
import particleman.items.ItemParticleGlove;

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
        
        pMod.itemGlove = (new ItemParticleGlove()).setCreativeTab(CreativeTabs.MISC);
        //pMod.itemGlove.setUnlocalizedName(ParticleMan.modID + ":particleglove");
        //pMod.itemGlove.setTextureName(ParticleMan.modID + ":particleglove");
        
        //GameRegistry.registerItem(pMod.itemGlove, "particleglove");

        registerItem(pMod.itemGlove, "particleglove");
        
        //LanguageRegistry.addName(pMod.itemGlove, "Particle Glove");
        GameRegistry.addRecipe(new ItemStack(pMod.itemGlove), new Object[] {" LL", "LRR", "LRD", Character.valueOf('L'), Items.LEATHER, Character.valueOf('R'), Items.REDSTONE, Character.valueOf('D'), Items.DIAMOND});

    	EntityRegistry.registerModEntity(EntityParticleControllable.class, "EntityParticleControllable", entityId++, pMod, 32, 3, true);

        SoundRegistry.init();
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

    private Item registerItem(Item item, String name) {
        item.setUnlocalizedName(getNamePrefixed(name));
        item.setRegistryName(new ResourceLocation(ParticleMan.modID, name));

        GameRegistry.register(item);
        //item.setCreativeTab(tab);
        registerItemVariantModel(item, name, 0);

        return item;
    }

    public void registerItemVariantModel(Item item, String name, int metadata) {}
    public void registerItemVariantModel(Item item, String registryName, int metadata, String variantName) {}

    public String getNamePrefixed(String name) {
        return ParticleMan.modID + "." + name;
    }

}
