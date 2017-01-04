package particleman.forge;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;

import particleman.items.ItemParticleGlove;

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
    	if (keyParticleMode.isKeyDown()) {
    		wasKeyPressed_keyParticleMode = true;
    	} else {
    		if (wasKeyPressed_keyParticleMode) {
    			commandID = 0;
    		}
    		wasKeyPressed_keyParticleMode = false;
    	}
    	
    	if (keyShieldToggle.isKeyDown()) {
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
        		ItemStack is = mc.thePlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        		
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

        return new FMLProxyPacket(new PacketBuffer(byteBuf), parChannel);
    }
    
    public static void sendPacket(int parCommand, int parSlot) {
    	ByteBuf byteBuf = Unpooled.buffer();
    	
    	try {
    		byteBuf.writeInt(parCommand);
    		byteBuf.writeInt(parSlot);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	FMLProxyPacket packet = new FMLProxyPacket(new PacketBuffer(byteBuf), ParticleMan.eventChannelName);
    	
    	ParticleMan.eventChannel.sendToServer(packet);
	}
}
