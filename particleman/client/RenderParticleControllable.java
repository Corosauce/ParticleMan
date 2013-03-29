package particleman.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import particleman.entities.EntityParticleControllable;

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
        GL11.glPushMatrix();
        
        RenderEngine re = Minecraft.getMinecraft().renderEngine;
        EntityParticleControllable particle = (EntityParticleControllable)var1;
        
        re.bindTexture("/particles.png");
                
        Tessellator tessellator = Tessellator.instance;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        tessellator.startDrawingQuads();

        int particleTextureIndexX = 0;
        int particleTextureIndexY = 0;
        
        if (particle.type == 0) {
        	particleTextureIndexY = 3;
        } else {
        	particleTextureIndexX = 6;
        }
        
        float f6 = (float)particleTextureIndexX / 16.0F;
        float f7 = f6 + 0.0624375F;
        float f8 = (float)particleTextureIndexY / 16.0F;
        float f9 = f8 + 0.0624375F;
        float f10 = 0.5F/* 0.1F * particle.particleScale*/;

        float f11 = (float)(particle.prevPosX + (particle.posX - particle.prevPosX) * (double)par2);
        float f12 = (float)(particle.prevPosY + (particle.posY - particle.prevPosY) * (double)par2);
        float f13 = (float)(particle.prevPosZ + (particle.posZ - particle.prevPosZ) * (double)par2);
        float f14 = 1.0F;
        
        float f1 = ActiveRenderInfo.rotationX;
        float f2 = ActiveRenderInfo.rotationZ;
        float f3 = ActiveRenderInfo.rotationYZ;
        float f4 = ActiveRenderInfo.rotationXY;
        float f5 = ActiveRenderInfo.rotationXZ;
        
        //f1, f5, f2, f3, f4
        //par3, par4, par5, par6, par7
        
        tessellator.setColorRGBA_F(this.particleRed * f14, this.particleGreen * f14, this.particleBlue * f14, this.particleAlpha);
        tessellator.addVertexWithUV((double)(f11 - f1 * f10 - f3 * f10), (double)(f12 - f5 * f10), (double)(f13 - f2 * f10 - f4 * f10), (double)f7, (double)f9);
        tessellator.addVertexWithUV((double)(f11 - f1 * f10 + f3 * f10), (double)(f12 + f5 * f10), (double)(f13 - f2 * f10 + f4 * f10), (double)f7, (double)f8);
        tessellator.addVertexWithUV((double)(f11 + f1 * f10 + f3 * f10), (double)(f12 + f5 * f10), (double)(f13 + f2 * f10 + f4 * f10), (double)f6, (double)f8);
        tessellator.addVertexWithUV((double)(f11 + f1 * f10 - f3 * f10), (double)(f12 - f5 * f10), (double)(f13 + f2 * f10 - f4 * f10), (double)f6, (double)f9);
        
        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        
        GL11.glPopMatrix();
    }
}
