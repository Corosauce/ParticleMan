package particleman.forge;

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
        
        RenderingRegistry.registerEntityRenderingHandler(EntityParticleControllable.class, new RenderParticleControllable());
    }
    
}
