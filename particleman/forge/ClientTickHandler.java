package particleman.forge;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import particleman.items.ItemParticleGlove;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler
{
	public ResourceLocation resTank = new ResourceLocation(ParticleMan.modID + ":textures/gui/tank.png");
	public ResourceLocation resTerrain = TextureMap.locationBlocksTexture;
	
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {}

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        if (type.equals(EnumSet.of(TickType.RENDER)))
        {
            onRenderTick();
        }
        else if (type.equals(EnumSet.of(TickType.CLIENT)))
        {
            GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;

            if (guiscreen != null)
            {
                onTickInGUI(guiscreen);
            }
            else
            {
                onTickInGame();
            }
        }
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.RENDER, TickType.CLIENT);
        // In my testing only RENDER, CLIENT, & PLAYER did anything on the client side.
        // Read 'cpw.mods.fml.common.TickType.java' for a full list and description of available types
    }

    @Override
    public String getLabel()
    {
        return null;
    }

    public void onRenderTick()
    {
    	Minecraft mc = FMLClientHandler.instance().getClient();
    	
    	boolean debug = false;
    	
    	if (mc != null && mc.thePlayer != null && (mc.currentScreen == null || debug))
        {
    		ItemStack is = mc.thePlayer.getCurrentEquippedItem();
    		
    		if (is != null && is.getItem() instanceof ItemParticleGlove && is.hasTagCompound()) {
    			int fireMode = is.stackTagCompound.getInteger("pm_fireMode");
    			int val0 = is.stackTagCompound.getInteger("pm_storage_0");
    			int val1 = is.stackTagCompound.getInteger("pm_storage_1");
    			int val2 = is.stackTagCompound.getInteger("pm_storage_2");
    			
    			ScaledResolution var8 = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
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
    			mc.ingameGUI.drawString(mc.fontRenderer, "", width/2 + 95 + 0, height - 10, 0xFFFFFF);
    			mc.ingameGUI.drawTexturedModalRect(width/2 + 95 - 4, height - (2+yOffset+6*3), 0, 0, 72, 22);
    			
    			//mc.renderEngine.bindTexture("/terrain.png");
    			mc.getTextureManager().bindTexture(resTerrain);
    			if (fireMode == 0) {
	    			mc.ingameGUI.drawString(mc.fontRenderer, "", width/2 + 95 + 0, height - 10, 0xCCCCCC);
	    			mc.ingameGUI.drawTexturedModelRectFromIcon(width/2 + 95 + 0, height - (4+yOffset+ySize*3), Block.blockClay.getIcon(0, 0), 65, ySize);
    			}
    			mc.ingameGUI.drawString(mc.fontRenderer, "", width/2 + 95 + 0, height - 10, fireMode == 0 ? 0xFFFFFF : 0xAAAAAA);
    			mc.ingameGUI.drawTexturedModelRectFromIcon(width/2 + 95 + 0, height - (4+yOffset+ySize*3), Block.lavaStill.getIcon(0, 0), (int)(val0 / 4.6D), ySize);
    			if (fireMode == 1) {
	    			mc.ingameGUI.drawString(mc.fontRenderer, "", width/2 + 95 + 0, height - 10, 0xCCCCCC);
	    			mc.ingameGUI.drawTexturedModelRectFromIcon(width/2 + 95 + 0, height - (2+yOffset+ySize*2), Block.blockClay.getIcon(0, 0), 65, ySize);
    			}
    			mc.ingameGUI.drawString(mc.fontRenderer, "", width/2 + 95 + 0, height - 10, fireMode == 1 ? 0xFF0000 : 0xAA0000);
    			mc.ingameGUI.drawTexturedModelRectFromIcon(width/2 + 95 + 0, height - (2+yOffset+ySize*2), Block.lavaMoving.getIcon(0, 0), (int)(val1 / 4.6D), ySize);
    			if (fireMode == 2) {
	    			mc.ingameGUI.drawString(mc.fontRenderer, "", width/2 + 95 + 0, height - 10, 0xCCCCCC);
	    			mc.ingameGUI.drawTexturedModelRectFromIcon(width/2 + 95 + 0, height - (yOffset+ySize), Block.blockClay.getIcon(0, 0), 65, ySize);
    			}
    			mc.ingameGUI.drawString(mc.fontRenderer, "", width/2 + 95 + 0, height - 10, fireMode == 2 ? 0xFFFFFF : 0xAAAAAA);
    			mc.ingameGUI.drawTexturedModelRectFromIcon(width/2 + 95 + 0, height - (yOffset+ySize), Block.waterStill.getIcon(0, 0), (int)(val2 / 4.6D), ySize);
    			
    			//mc.ingameGUI.drawString(mc.fontRenderer, "Mode: " + fireMode, width/2 + 95 + 0, height - 40, 0xFFFFFF);
    		}
        }
    	
    	int var6 = 0;
    	int var7 = 0;
    	
    	//GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/gui/gui.png"));
    	
    	//drawTexturedModalRect(200, 80, 0, 0, 21, 22);
        //System.out.println("onRenderTick");
        //TODO: Your Code Here
    }

    public void onTickInGUI(GuiScreen guiscreen)
    {
        onTickInGame();
        //System.out.println("onTickInGUI");
        //TODO: Your Code Here
    }

    Field curPlayingStr = null;

    public void onTickInGame()
    {
    	
    }

    static void getField(Field field, Object newValue) throws Exception
    {
        field.setAccessible(true);
        // remove final modifier from field
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }
}
