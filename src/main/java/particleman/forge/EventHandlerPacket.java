package particleman.forge;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import particleman.items.ItemParticleGlove;
import CoroUtil.packet.PacketHelper;
import CoroUtil.util.CoroUtilEntity;

public class EventHandlerPacket {
	
	//1.6.4 original usage was PMGloveCommand channel, but we only have 1 type of packet, so a packetCommand lookup isnt needed
	
	@SubscribeEvent
	public void onPacketFromServer(FMLNetworkEvent.ClientCustomPacketEvent event) {
		
		try {
			NBTTagCompound nbt = PacketHelper.readNBTTagCompound(event.getPacket().payload());
			
			String packetCommand = nbt.getString("packetCommand");
			
			if (packetCommand.equals("")) {
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	@SubscribeEvent
	public void onPacketFromClient(FMLNetworkEvent.ServerCustomPacketEvent event) {
		EntityPlayerMP entP = ((NetHandlerPlayServer)event.getHandler()).playerEntity;
		
		try {
			
			ByteBuf buffer = event.packet.payload();
			
	        //if ("PMGloveCommand".equals(packet.channel)) {
	        	int commandID = buffer.readInt();
	        	int slotID = buffer.readInt();
	        	
	        	ItemStack is = entP.inventory.getStackInSlot(slotID);
	        	
	        	if (is != null && is.getItem() instanceof ItemParticleGlove) {
	        		if (is.getTagCompound() == null) is.setTagCompound(new NBTTagCompound());
	        		if (commandID == 0) {
	        			int fireMode = is.getTagCompound().getInteger("pm_fireMode") + 1;
	        			if (fireMode >= 3) fireMode = 0;
	        			is.getTagCompound().setInteger("pm_fireMode", fireMode);
	        			
		        	} else if (commandID == 1) {
		        		((ItemParticleGlove)is.getItem()).createParticleFromInternal(entP, is, true, 5);
		        	} else if (commandID == 2) {
		        		((ItemParticleGlove)is.getItem()).shieldRetract(entP);
		        	}
	        	}
			//}
        	
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
    
    @SideOnly(Side.CLIENT)
    public String getSelfUsername() {
    	return CoroUtilEntity.getName(Minecraft.getMinecraft().thePlayer);
    }
	
}
