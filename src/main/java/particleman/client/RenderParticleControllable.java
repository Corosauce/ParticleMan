package particleman.client;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(Entity entity) {
		// TODO Auto-generated method stub
		return null;
	}
}
