package particleman.forge;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import particleman.items.ItemParticleGlove;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PMPacketHandler implements IPacketHandler
{
    public PMPacketHandler()
    {
    }

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
    	Side side = FMLCommonHandler.instance().getEffectiveSide();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));
        try
        {
        	if (player instanceof EntityPlayer) {
        		EntityPlayer entP = (EntityPlayer)player;
		        if ("PMGloveCommand".equals(packet.channel)) {
		        	int commandID = dis.readInt();
		        	int slotID = dis.readInt();
		        	
		        	ItemStack is = entP.inventory.getStackInSlot(slotID);
		        	
		        	if (is != null && is.getItem() instanceof ItemParticleGlove) {
		        		if (is.stackTagCompound == null) is.stackTagCompound = new NBTTagCompound();
		        		if (commandID == 0) {
		        			int fireMode = is.stackTagCompound.getInteger("pm_fireMode") + 1;
		        			if (fireMode >= 3) fireMode = 0;
		        			is.stackTagCompound.setInteger("pm_fireMode", fireMode);
		        			
			        	} else if (commandID == 1) {
			        		((ItemParticleGlove)is.getItem()).createParticleFromInternal(entP, is, true, 5);
			        	} else if (commandID == 2) {
			        		((ItemParticleGlove)is.getItem()).shieldRetract(entP);
			        	}
		        	}
				}
        	}
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
