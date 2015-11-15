package particleman.forge;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.input.Keyboard;

import particleman.items.ItemParticleGlove;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PMKeybindHandler {

	public static KeyBinding keyParticleMode = new KeyBinding("PM_ParticleMode", Keyboard.KEY_K, "key.categories.gameplay");
	public static KeyBinding keyShieldToggle = new KeyBinding("PM_ShieldToggle", Keyboard.KEY_L, "key.categories.gameplay");
	
	public static boolean wasKeyPressed_keyParticleMode = false;
	public static boolean wasKeyPressed_keyShieldToggle = false;

    public PMKeybindHandler() {
            //the first value is an array of KeyBindings, the second is whether or not the call
            //keyDown should repeat as long as the key is down
            //super(new KeyBinding[]{keyParticleMode, keyShieldToggle}, new boolean[]{false, false});
    	
    		
    }
    
    public static void init() {
    	ClientRegistry.registerKeyBinding(keyParticleMode);
		ClientRegistry.registerKeyBinding(keyShieldToggle);
    }
    
    public static void tickClient() {
    	int commandID = -1;
    	
    	//default
    	//-------
    	//toggle mode
    	//deploy shield
    	
    	//shift modifiers
    	//---------------
    	//toggle fire rate type - charge and release, constant fire......... maybe dont do this, see how charge and release works
    	//retract shield fully
    	
    	//key release management
    	if (keyParticleMode.getIsKeyPressed()) {
    		wasKeyPressed_keyParticleMode = true;
    	} else {
    		if (wasKeyPressed_keyParticleMode) {
    			commandID = 0;
    		}
    		wasKeyPressed_keyParticleMode = false;
    	}
    	
    	if (keyShieldToggle.getIsKeyPressed()) {
    		wasKeyPressed_keyShieldToggle = true;
    	} else {
    		if (wasKeyPressed_keyShieldToggle) {
    			if (!Minecraft.getMinecraft().gameSettings.keyBindSneak.isPressed()) {
        			commandID = 1;
            	} else {
            		commandID = 2;
            	}
    		}
    		wasKeyPressed_keyShieldToggle = false;
    	}
    	
    	if (commandID != -1) {
    		Minecraft mc = FMLClientHandler.instance().getClient();
        	
        	if (mc != null && mc.thePlayer != null && mc.currentScreen == null)
            {
        		ItemStack is = mc.thePlayer.getCurrentEquippedItem();
        		
        		if (is != null && is.getItem() instanceof ItemParticleGlove/* && is.hasTagCompound()*/) {
        			sendPacket(commandID, mc.thePlayer.inventory.currentItem);
        		}
            }
    		
    	}
    }
    
    public static FMLProxyPacket getNBTPacket(NBTTagCompound parNBT, String parChannel) {
        ByteBuf byteBuf = Unpooled.buffer();
        
        try {
        	//byteBuf.writeBytes(CompressedStreamTools.compress(parNBT));
        	ByteBufUtils.writeTag(byteBuf, parNBT);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }

        return new FMLProxyPacket(byteBuf, parChannel);
    }
    
    public static void sendPacket(int parCommand, int parSlot) {
    	ByteBuf byteBuf = Unpooled.buffer();
    	
    	try {
    		byteBuf.writeInt(parCommand);
    		byteBuf.writeInt(parSlot);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	FMLProxyPacket packet = new FMLProxyPacket(byteBuf, ParticleMan.eventChannelName);
    	
    	ParticleMan.eventChannel.sendToServer(packet);
	}
}
