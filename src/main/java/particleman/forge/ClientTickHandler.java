package particleman.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import particleman.items.ItemParticleGlove;

public class ClientTickHandler
{
	public static ResourceLocation resTank = new ResourceLocation(ParticleMan.modID + ":textures/gui/tank.png");
	public static ResourceLocation resTerrain = TextureMap.LOCATION_BLOCKS_TEXTURE;
	
    public static void onRenderTick()
    {
    	Minecraft mc = FMLClientHandler.instance().getClient();
    	
    	boolean debug = false;
    	
    	if (mc != null && mc.thePlayer != null && (mc.currentScreen == null || debug))
        {
    		ItemStack is = mc.thePlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
    		
    		if (is != null && is.getItem() instanceof ItemParticleGlove && is.hasTagCompound()) {
    			int fireMode = is.getTagCompound().getInteger("pm_fireMode");
    			int val0 = is.getTagCompound().getInteger("pm_storage_0");
    			int val1 = is.getTagCompound().getInteger("pm_storage_1");
    			int val2 = is.getTagCompound().getInteger("pm_storage_2");
    			
    			ScaledResolution var8 = new ScaledResolution(mc/*, mc.displayWidth, mc.displayHeight*/);
    	        int width = var8.getScaledWidth();
    	        int height = var8.getScaledHeight();
    			
    			//mc.ingameGUI.drawString(mc.fontRenderer, "Fire: " + val0, width/2 + 95 + 0, height - 30, fireMode == 0 ? 0xFF8000 : 0xFFFFFF);
    			//mc.ingameGUI.drawString(mc.fontRenderer, "Redstone: " + val1, width/2 + 95 + 0, height - 20, fireMode == 1 ? 0xFF0000 : 0xFFFFFF);
    			//mc.ingameGUI.drawString(mc.fontRenderer, "Water: " + val2, width/2 + 95 + 0, height - 10, fireMode == 2 ? 0x8888FF : 0xFFFFFF);
    	        
    	        //weird way to set the texture brightness
    			
    	        int ySize = 4;
    	        int yOffset = 2;
    			
    			
    			//mc.renderEngine.bindTexture("/mods/ParticleMan/textures/gui/tank.png");
    			mc.getTextureManager().bindTexture(resTank);
    			mc.ingameGUI.drawString(mc.fontRendererObj, "", width/2 + 95 + 0, height - 10, 0xFFFFFF);
    			mc.ingameGUI.drawTexturedModalRect(width/2 + 95 - 4, height - (2+yOffset+6*3), 0, 0, 72, 22);
    			
    			//mc.renderEngine.bindTexture("/terrain.png");
    			mc.getTextureManager().bindTexture(resTerrain);
    			if (fireMode == 0) {
	    			mc.ingameGUI.drawString(mc.fontRendererObj, "", width/2 + 95 + 0, height - 10, 0xCCCCCC);
	    			mc.ingameGUI.drawTexturedModelRectFromIcon(width/2 + 95 + 0, height - (4+yOffset+ySize*3), Blocks.CLAY.getIcon(0, 0), 65, ySize);
    			}
    			mc.ingameGUI.drawString(mc.fontRendererObj, "", width/2 + 95 + 0, height - 10, fireMode == 0 ? 0xFFFFFF : 0xAAAAAA);
    			mc.ingameGUI.drawTexturedModelRectFromIcon(width/2 + 95 + 0, height - (4+yOffset+ySize*3), Blocks.LAVA.getIcon(0, 0), (int)(val0 / 4.6D), ySize);
    			if (fireMode == 1) {
	    			mc.ingameGUI.drawString(mc.fontRendererObj, "", width/2 + 95 + 0, height - 10, 0xCCCCCC);
	    			mc.ingameGUI.drawTexturedModelRectFromIcon(width/2 + 95 + 0, height - (2+yOffset+ySize*2), Blocks.CLAY.getIcon(0, 0), 65, ySize);
    			}
    			mc.ingameGUI.drawString(mc.fontRendererObj, "", width/2 + 95 + 0, height - 10, fireMode == 1 ? 0xFF0000 : 0xAA0000);
    			mc.ingameGUI.drawTexturedModelRectFromIcon(width/2 + 95 + 0, height - (2+yOffset+ySize*2), Blocks.LAVA.getIcon(0, 0), (int)(val1 / 4.6D), ySize);
    			if (fireMode == 2) {
	    			mc.ingameGUI.drawString(mc.fontRendererObj, "", width/2 + 95 + 0, height - 10, 0xCCCCCC);
	    			mc.ingameGUI.drawTexturedModelRectFromIcon(width/2 + 95 + 0, height - (yOffset+ySize), Blocks.CLAY.getIcon(0, 0), 65, ySize);
    			}
    			mc.ingameGUI.drawString(mc.fontRendererObj, "", width/2 + 95 + 0, height - 10, fireMode == 2 ? 0xFFFFFF : 0xAAAAAA);
    			mc.ingameGUI.drawTexturedModelRectFromIcon(width/2 + 95 + 0, height - (yOffset+ySize), Blocks.WATER.getIcon(0, 0), (int)(val2 / 4.6D), ySize);
    			
    			//mc.ingameGUI.drawString(mc.fontRenderer, "Mode: " + fireMode, width/2 + 95 + 0, height - 40, 0xFFFFFF);
    		}
        }
    	
    	int var6 = 0;
    	int var7 = 0;
    	
    	//GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/gui/gui.png"));
    	
    	//drawTexturedModalRect(200, 80, 0, 0, 21, 22);
        //System.out.println("onRenderTick");
    }
}
