package particleman.forge;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import particleman.entities.EntityParticleControllable;
import particleman.items.ItemParticleGlove;

@Mod.EventBusSubscriber(modid = ParticleMan.modID)
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
        

        //pMod.itemGlove.setUnlocalizedName(ParticleMan.modID + ":particleglove");
        //pMod.itemGlove.setTextureName(ParticleMan.modID + ":particleglove");
        
        //GameRegistry.registerItem(pMod.itemGlove, "particleglove");

    	EntityRegistry.registerModEntity(new ResourceLocation(pMod.modID, "entity_particle_controllable"), EntityParticleControllable.class, "EntityParticleControllable", entityId++, pMod, 32, 3, true);

        SoundRegistry.init();
    }

    public void postInit(ParticleMan pMod)
    {
        ResourceLocation group = new ResourceLocation(pMod.modID, "particle_man");

        //LanguageRegistry.addName(pMod.itemGlove, "Particle Glove");
        GameRegistry.addShapedRecipe(new ResourceLocation(pMod.modID, "glove"), group,
                new ItemStack(pMod.itemGlove), new Object[] {" LL", "LRR", "LRD",
                        Character.valueOf('L'), Items.LEATHER,
                        Character.valueOf('R'), Items.REDSTONE,
                        Character.valueOf('D'), Items.DIAMOND});
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Item itemGlove = (new ItemParticleGlove()).setCreativeTab(CreativeTabs.MISC);
        ParticleMan.proxy.registerItem(itemGlove, "particleglove");
        event.getRegistry().register(itemGlove);
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

        //GameRegistry.register(item);
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
