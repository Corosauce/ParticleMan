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
        
    }
}
