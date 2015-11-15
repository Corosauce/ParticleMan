package particleman.forge;

import net.minecraftforge.common.MinecraftForge;
import particleman.client.RenderParticleControllable;
import particleman.entities.EntityParticleControllable;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
        
        RenderingRegistry.registerEntityRenderingHandler(EntityParticleControllable.class, new RenderParticleControllable());
    }
    
}
