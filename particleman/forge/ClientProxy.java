package particleman.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ModLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    public static Minecraft mc;

    public ClientProxy()
    {
        mc = ModLoader.getMinecraftInstance();
    }

    @Override
    public void init(ParticleMan pMod)
    {
        super.init(pMod);
        
        //RenderingRegistry.registerEntityRenderingHandler(EntityScent.class, new RenderScent());
    }
    
}
