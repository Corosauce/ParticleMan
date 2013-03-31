package particleman.items;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import particleman.entities.EntityParticleControllable;

public class ItemParticleGlove extends Item {

	public static HashMap<String, List<EntityParticleControllable>> playerParticles = new HashMap<String, List<EntityParticleControllable>>();
	//public static HashMap<String, Integer> playerParticleMode = new HashMap<String, Integer>();
	public static HashMap<String, Integer> playerWasSneaking = new HashMap<String, Integer>();
	
	public ItemParticleGlove(int par1) {
		super(par1);
		// TODO Auto-generated constructor stub
	}
	
	public void check(String parUser) {
		if (!playerParticles.containsKey(parUser)) {
			playerParticles.put(parUser, new LinkedList<EntityParticleControllable>());
			//playerParticleMode.put(parUser, 0);
			playerWasSneaking.put(parUser, 0);
		}
	}
	
	public void shootParticle(EntityPlayer player) {
		for (int i = 0; i < playerParticles.get(player.username).size(); i++) {
			EntityParticleControllable particle = playerParticles.get(player.username).get(i);
			if (particle.getDistanceToEntity(player) < 6D) {
				//EntityParticleControllable particle = playerParticles.get(player.username).get(0);
				playerParticles.get(player.username).remove(particle);
				playerParticles.get(player.username).add(particle);
				
				particle.state = 1;
				
				float speed = 0.8F;
				float look = -2F;
		    	
		    	double vecX = (double)(-Math.sin((player.rotationYaw+look) / 180.0F * 3.1415927F) * Math.cos(player.rotationPitch / 180.0F * 3.1415927F));
		    	double vecY = -Math.sin(player.rotationPitch / 180.0F * 3.1415927F);
		    	double vecZ = (double)(Math.cos((player.rotationYaw+look) / 180.0F * 3.1415927F) * Math.cos(player.rotationPitch / 180.0F * 3.1415927F));
		        
		        //double var9 = (double)Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
		        particle.motionX += vecX * speed;
		        particle.motionY += vecY * speed;
		        particle.motionZ += vecZ * speed;
		        break;
			}
		}
	}
	
	public void makeShockwave(EntityPlayer player) {
		for (int i = 0; i < playerParticles.get(player.username).size(); i++) {
			EntityParticleControllable particle = playerParticles.get(player.username).get(i);
			if (particle.getDistanceToEntity(player) < 10D) {
				//playerParticles.get(player.username).remove(particle);
				//playerParticles.get(player.username).add(particle);
				
				particle.state = 1;
				
				float speed2 = 0.8F;

				double vecX = particle.posX - player.posX;
				double vecY = particle.posY - player.posY;
				double vecZ = particle.posZ - player.posZ;

				double dist2 = (double)Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
				particle.motionX += vecX / dist2 * speed2;
				//particle.motionY += vecY / dist2 * speed2;
				particle.motionZ += vecZ / dist2 * speed2;
			}
		}
	}
	
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
		if (!par3World.isRemote) {
			check(par2EntityPlayer.username);
			int id = par3World.getBlockId(par4, par5, par6);
			
			if (id != 0) {
				int spawnType = -1;
				if (id == Block.torchWood.blockID) {
					spawnType = 0;
				} else if (id == Block.torchRedstoneActive.blockID) {
					spawnType = 1;
				}
				
				if (spawnType != -1) {
					EntityParticleControllable particle = new EntityParticleControllable(par3World, par2EntityPlayer.username, spawnType);
					particle.setPosition(par4+0.5F, par5+0.6F, par6+0.5F);
					particle.index = playerParticles.get(par2EntityPlayer.username).size();
					playerParticles.get(par2EntityPlayer.username).add(particle);
					par3World.spawnEntityInWorld(particle);
					return true;
				}
			}
			return false;
		}
		
		return true;
    }
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		//System.out.println(par5);
		if (par3Entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)par3Entity;
			if (!par2World.isRemote) {
				check(player.username);

				NBTTagCompound plData = player.getEntityData();
				
				if (plData == null) plData = new NBTTagCompound();
				
				if (player.isSneaking()) {
					if (playerWasSneaking.get(player.username) == 0) {
						System.out.println("mode toggle");
						if (plData.getInteger("particleMode") == 0) {
							plData.setInteger("particleMode", 1);
						} else {
							plData.setInteger("particleMode", 0);
						}
					}
					playerWasSneaking.put(player.username, 1);
				} else {
					playerWasSneaking.put(player.username, 0);
				}
				
				for (int i = 0; i < playerParticles.get(player.username).size(); i++) {
					EntityParticleControllable particle = playerParticles.get(player.username).get(i);
					
					if (particle == null || particle.isDead || par3Entity.getDistanceToEntity(particle) > 50) {
						playerParticles.get(player.username).remove(particle);
					} else {
						//ParticleMan.spinAround(particle, player, 10F, 0.5F, 2F, particle.index, 0.02F, 1);
						particle.decayTime = 0;
					}
				}
			} else {
				if (par3Entity instanceof EntityPlayerSP) {
					((EntityPlayerSP)player).movementInput.moveStrafe *= 5;
					((EntityPlayerSP)player).movementInput.moveForward *= 5;
				}
			}
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote) {
			check(par3EntityPlayer.username);
			if (par3EntityPlayer.isSneaking()) {
				makeShockwave(par3EntityPlayer);
			} else {
				shootParticle(par3EntityPlayer);
			}
		}
		
		return par1ItemStack;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 72000;
    }
	
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

}
