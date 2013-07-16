package particleman.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import particleman.items.ItemParticleGlove;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PMKeybindHandler extends KeyHandler {

	public static KeyBinding keyParticleMode = new KeyBinding("PM_ParticleMode", Keyboard.KEY_K);
	public static KeyBinding keyShieldToggle = new KeyBinding("PM_ShieldToggle", Keyboard.KEY_L);

    public PMKeybindHandler() {
            //the first value is an array of KeyBindings, the second is whether or not the call
            //keyDown should repeat as long as the key is down
            super(new KeyBinding[]{keyParticleMode, keyShieldToggle}, new boolean[]{false, false});
    }

    @Override
    public String getLabel() {
            return "Particle Man Keybinds";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb,
                    boolean tickEnd, boolean isRepeat) {
            //do whatever
    	/*if (ZCClientTicks.iMan != null) {
    		ZCClientTicks.iMan.keyEvent(kb, false);
		}*/
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
    	
    	int commandID = -1;
    	
    	//default
    	//-------
    	//toggle mode
    	//deploy shield
    	
    	//shift modifiers
    	//---------------
    	//toggle fire rate type - charge and release, constant fire......... maybe dont do this, see how charge and release works
    	//retract shield fully
    	
    	if (tickEnd) return;
    	
    	if (kb.keyDescription.equals("PM_ParticleMode")) {
    		commandID = 0;
    	} else if (kb.keyDescription.equals("PM_ShieldToggle")) {
    		if (!Minecraft.getMinecraft().gameSettings.keyBindSneak.isPressed()) {
    			commandID = 1;
        	} else {
        		commandID = 2;
        	}
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
        //do whatever
    	//System.out.println(FMLCommonHandler.instance().getEffectiveSide());
    	//System.out.println(kb.keyDescription);
    	
    	/*if (ZCClientTicks.iMan != null) {
    		ZCClientTicks.iMan.keyEvent(kb, true);
		}*/
    }

    @Override
    public EnumSet<TickType> ticks() {
            return EnumSet.of(TickType.CLIENT);
    }
    
    public static void sendPacket(int parCommand, int parSlot) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(Integer.SIZE * 2);
    	DataOutputStream outputStream = new DataOutputStream(bos);
    	try {
    		outputStream.writeInt(parCommand);
    		outputStream.writeInt(parSlot);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	Packet250CustomPayload packet = new Packet250CustomPayload();
    	packet.channel = "PMGloveCommand";
    	packet.data = bos.toByteArray();
    	packet.length = bos.size();
    	
    	FMLClientHandler.instance().getClient().thePlayer.sendQueue.addToSendQueue(packet);
	}
}
