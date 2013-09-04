package particleman.client;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderParticleControllable extends Render
{

	public float particleRed;
	public float particleGreen;
	public float particleBlue;
	public float particleAlpha;
	
    public void doRender(Entity var1, double par2, double var4, double var6, float var8, float var9)
    {
        
    }

	@Override
	protected ResourceLocation func_110775_a(Entity entity) {
		// TODO Auto-generated method stub
		return null;
	}
}
