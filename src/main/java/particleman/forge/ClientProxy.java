package particleman.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import particleman.client.RenderParticleControllable;
import particleman.entities.EntityParticleControllable;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{

    public ClientProxy()
    {
    	
    }
    
    @Override
    public void preInit(ParticleMan pMod)
    {
    	super.preInit(pMod);
    	PMKeybindHandler.init();
    }

    @Override
    public void init(ParticleMan pMod)
    {
        super.init(pMod);
        
        RenderingRegistry.registerEntityRenderingHandler(EntityParticleControllable.class, new RenderParticleControllable(Minecraft.getMinecraft().getRenderManager()));
    }

    public void registerItem(Item item, int meta, ModelResourceLocation location) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, location);
    }

    @Override
    public void registerItemVariantModel(Item item, String name, int metadata) {
        if (item != null) {
            ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation(ParticleMan.modID + ":" + name, "inventory"));
        }
    }

    @Override
    public void registerItemVariantModel(Item item, String registryName, int metadata, String variantName) {
        if (item != null) {
            ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation(ParticleMan.modID + ":" + variantName, "inventory"));
        }
    }
    
}
