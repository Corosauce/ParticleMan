package particleman.items;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import particleman.element.Element;
import particleman.entities.EntityParticleControllable;
import particleman.forge.ParticleMan;
import CoroUtil.util.CoroUtilBlock;
import CoroUtil.util.CoroUtilEntity;
import particleman.forge.SoundRegistry;

public class ItemParticleGlove extends Item {

	
	//Internal storage
	
	//pm_fireMode - represents one of the numbers below, matches spawn type
	//pm_storage_0
	//pm_storage_1
	//pm_storage_2
	
	//Fields stored in player instance for ease of handling via packets - actually no, client doesnt auto sync, use itemstack...... so for packets, pass the slot # item is in for convenience
	//particlemode
	
	
	//new left click, does the active selected particles effect directly on click target, with a little extra damage and more hunger used
	
	
	
	
	
	public static HashMap<String, List<EntityParticleControllable>> playerParticles = new HashMap<String, List<EntityParticleControllable>>();
	//public static HashMap<String, Integer> playerParticleMode = new HashMap<String, Integer>();
	public static HashMap<String, Integer> playerWasSneaking = new HashMap<String, Integer>();
	
	public static int fillRate = 5;
	public static int depleteRate = 5;
	public static int maxStorage = 300;
	
	public ItemParticleGlove() {
		super();
	}
	
	public void check(String parUser) {
		if (!playerParticles.containsKey(parUser)) {
			playerParticles.put(parUser, new LinkedList<EntityParticleControllable>());
			//playerParticleMode.put(parUser, 0);
			playerWasSneaking.put(parUser, 0);
		}
	}
	
	public void shieldRetract(EntityPlayer player) {
		for (int i = 0; i < playerParticles.get(CoroUtilEntity.getName(player)).size(); i++) {
			EntityParticleControllable particle = playerParticles.get(CoroUtilEntity.getName(player)).get(i);
			//if (particle.getDistanceToEntity(player) < 10D) {
				//playerParticles.get(player.username).remove(particle);
				//playerParticles.get(player.username).add(particle);
				
				//particle.state = 1;
				particle.moveMode = 0; //call them in
				
			//}
		}
	}
	
	public int createParticleFromInternal(EntityPlayer player, ItemStack item, boolean forShield, int createCount) {

		int fireMode = item.getTagCompound().getInteger("pm_fireMode");
		if (!player.world.isRemote) {
			
			int curAmount = item.getTagCompound().getInteger("pm_storage_" + fireMode);
			
			//int shieldDeployRateFactor = 5;
			
			if (curAmount > 0) {
				
				int deplete = depleteRate;
				
				if (fireMode == 2) deplete /= 3;
				
				item.getTagCompound().setInteger("pm_storage_" + fireMode, Math.max(curAmount - (deplete * createCount), 0));
				
				for (int i = 0; i < createCount; i++) {
					EntityParticleControllable particle = new EntityParticleControllable(player.world, CoroUtilEntity.getName(player), fireMode);
					
					//TEMP POS, PUT AT HAND LATER FOR MORE ACCURACY
					particle.setPosition(player.posX, player.posY + 1, player.posZ);
					particle.index = playerParticles.get(CoroUtilEntity.getName(player)).size();
					player.world.spawnEntity(particle);
					
					if (forShield) {
						playerParticles.get(CoroUtilEntity.getName(player)).add(particle);
						particle.moveMode = 1;
						//player.world.playSoundEffect(player.posX, player.posY, player.posZ, ParticleMan.modID+":fire_shoot", 0.9F, player.world.rand.nextFloat());
						player.world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.get("fire_shoot"), SoundCategory.PLAYERS, 0.9F, player.world.rand.nextFloat());
					} else {
						fireParticle(player, particle);
						//player.world.playSoundEffect(player.posX, player.posY, player.posZ, /*particle.type == 0 ? "fire_shoot" : */ParticleMan.modID+":redstone_shoot", 0.9F, player.world.rand.nextFloat());
						player.world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.get("redstone_shoot"), SoundCategory.PLAYERS, 0.9F, player.world.rand.nextFloat());
					}
				}
			}
		}
		return fireMode;
	}
	
	public void fireParticle(EntityPlayer player, EntityParticleControllable particle) {
		particle.state = 1;
		
		float speed = 0.8F;
		float look = -2F;
		

        //position fix
        double adjAngle = 30D;
		double dist = 2.5D;
		
		//adjAngle = 0D;
		//dist = 0D;
		
		//this code runs but doesnt do shit, wtf?
		/*Entity center = player;
		
		double posX = (center.posX - Math.cos((-center.rotationYaw + adjAngle) * 0.01745329D) * dist);
		double posY = (center.posY - Math.sin((center.rotationPitch) / 180.0F * 3.1415927F) * dist);
		double posZ = (center.posZ + Math.sin((-center.rotationYaw + adjAngle) * 0.01745329D) * dist);
		
		particle.setPosition(posX, posY + 5, posZ);*/
    	
		int pitchOffset = 0;
		
		if (particle.type == 2) pitchOffset = 90;
		
    	double vecX = (double)(-Math.sin((player.rotationYaw+look) / 180.0F * 3.1415927F) * Math.cos((player.rotationPitch+pitchOffset) / 180.0F * 3.1415927F));
    	double vecY = -Math.sin((player.rotationPitch+pitchOffset) / 180.0F * 3.1415927F);
    	double vecZ = (double)(Math.cos((player.rotationYaw+look) / 180.0F * 3.1415927F) * Math.cos((player.rotationPitch+pitchOffset) / 180.0F * 3.1415927F));
        
        //double var9 = (double)Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
        particle.motionX += vecX * speed;
        particle.motionY += vecY * speed;
        particle.motionZ += vecZ * speed;
        
        if (particle.type == 2) player.fallDistance = 0;
        
	}
	
	public int shootParticle(EntityPlayer player) {
		for (int i = 0; i < playerParticles.get(CoroUtilEntity.getName(player)).size(); i++) {
			EntityParticleControllable particle = playerParticles.get(CoroUtilEntity.getName(player)).get(i);
			if (particle.getDistanceToEntity(player) < 6D) {
				//player.world.playSoundEffect(player.posX, player.posY, player.posZ, /*particle.type == 0 ? "fire_shoot" : */ParticleMan.modID+":redstone_shoot", 0.9F, player.world.rand.nextFloat());
				player.world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.get("redstone_shoot"), SoundCategory.PLAYERS, 0.9F, player.world.rand.nextFloat());
				//EntityParticleControllable particle = playerParticles.get(CoroUtilEntity.getName(player)).get(0);
				
				playerParticles.get(CoroUtilEntity.getName(player)).add(particle);
				
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
		        
		        //wont work for motion, server side
		        if (particle.type == 2) {
		        	for (int j = 0; j < playerParticles.get(CoroUtilEntity.getName(player)).size(); j++) {
		    			EntityParticleControllable particle2 = playerParticles.get(CoroUtilEntity.getName(player)).get(j);
		    			//if (particle.getDistanceToEntity(player) > 4D) {
		    			if (particle2 != particle) {
		    				particle2.setPosition(player.posX, player.posY, player.posZ);
		    			}
		        	}
		        	/*player.motionX -= vecX * speed;
		        	player.motionY -= vecY * speed;
		        	player.motionZ -= vecZ * speed;*/
		        	particle.health -= 2;
		        	//System.out.println("HURT");
		        	if (particle.health <= 0) {
		        		particle.setDead();
		        		playerParticles.get(CoroUtilEntity.getName(player)).remove(particle);
		        	}
		        }
		        return particle.type;
			}
		}
		return -1;
	}
	
	
	
	public void makeShockwave(EntityPlayer player) {
		for (int i = 0; i < playerParticles.get(CoroUtilEntity.getName(player)).size(); i++) {
			EntityParticleControllable particle = playerParticles.get(CoroUtilEntity.getName(player)).get(i);
			if (particle.getDistanceToEntity(player) < 10D) {
				//playerParticles.get(CoroUtilEntity.getName(player)).remove(particle);
				//playerParticles.get(CoroUtilEntity.getName(player)).add(particle);
				
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
		
		if (!player.capabilities.isCreativeMode) player.getFoodStats().addExhaustion(3F);
		//player.world.playSoundEffect(player.posX, player.posY, player.posZ, ParticleMan.modID+":shockwave_echo_loud", 0.7F, 1F - (player.world.rand.nextFloat() * 0.2F));
		player.world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.get("shockwave_echo_loud"), SoundCategory.PLAYERS, 0.7F, 1F - (player.world.rand.nextFloat() * 0.2F));
		
		NBTTagCompound plData = player.getEntityData();
		if (plData == null) plData = new NBTTagCompound();
		if (plData.getInteger("particleMode") == 0) {
			plData.setInteger("particleMode", 1);
		} else {
			plData.setInteger("particleMode", 0);
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer par2EntityPlayer, World par3World, BlockPos posHit, EnumHand hand, EnumFacing facing, float par8, float par9, float par10)
    {
		if (hand == EnumHand.MAIN_HAND) {
			ItemStack par1ItemStack = par2EntityPlayer.getHeldItemMainhand();
			if (par1ItemStack.getTagCompound() == null) par1ItemStack.setTagCompound(new NBTTagCompound());
			//if (!par3World.isRemote) {
			if (par2EntityPlayer.getFoodStats().getFoodLevel() >= 6 || par2EntityPlayer.isCreative()) {
				check(CoroUtilEntity.getName(par2EntityPlayer));

					/*Block id = par3World.getBlock(par4, par5, par6);
					Block id2 = par3World.getBlock(par4, par5+1, par6);*/
				BlockPos pos = posHit;//new BlockPos(par4, par5, par6);
				BlockPos pos2 = posHit.add(0, 1, 0);//new BlockPos(par4, par5+1, par6);
				IBlockState state = par3World.getBlockState(pos);
				IBlockState state2 = par3World.getBlockState(pos2);
				if (!CoroUtilBlock.isAir(state.getBlock())) {
					int spawnType = -1;
					if (state.getBlock() == Blocks.TORCH || state2.getBlock() == Blocks.FIRE) {
						spawnType = 0;
						//par3World.playSoundEffect(par2EntityPlayer.posX, par2EntityPlayer.posY, par2EntityPlayer.posZ, ParticleMan.modID+":fire_grab", 0.9F, par3World.rand.nextFloat());
						par3World.playSound(null, par2EntityPlayer.posX, par2EntityPlayer.posY, par2EntityPlayer.posZ, SoundRegistry.get("fire_grab"), SoundCategory.PLAYERS, 0.9F, par3World.rand.nextFloat());
					} else if (state.getBlock() == Blocks.REDSTONE_TORCH || state.getBlock() == Blocks.REDSTONE_ORE || state.getBlock() == Blocks.REDSTONE_WIRE) {
						spawnType = 1;
						//par3World.playSoundEffect(par2EntityPlayer.posX, par2EntityPlayer.posY, par2EntityPlayer.posZ, ParticleMan.modID+":redstone_grab", 0.9F, par3World.rand.nextFloat());
						par3World.playSound(null, par2EntityPlayer.posX, par2EntityPlayer.posY, par2EntityPlayer.posZ, SoundRegistry.get("redstone_grab"), SoundCategory.PLAYERS, 0.9F, par3World.rand.nextFloat());
					} else if (state2.getMaterial() == Material.WATER) {
						spawnType = 2;
						//par3World.playSoundEffect(par2EntityPlayer.posX, par2EntityPlayer.posY, par2EntityPlayer.posZ, ParticleMan.modID+":redstone_grab", 0.9F, par3World.rand.nextFloat());
						par3World.playSound(null, par2EntityPlayer.posX, par2EntityPlayer.posY, par2EntityPlayer.posZ, SoundRegistry.get("redstone_grab"), SoundCategory.PLAYERS, 0.9F, par3World.rand.nextFloat());
					}

					if (spawnType != -1) {
						if (!par3World.isRemote) {

							int curAmount = par1ItemStack.getTagCompound().getInteger("pm_storage_" + spawnType);

							if (curAmount < maxStorage) {
								par1ItemStack.getTagCompound().setInteger("pm_storage_" + spawnType, Math.min(curAmount + fillRate, maxStorage));
								if (!par2EntityPlayer.capabilities.isCreativeMode)
									par2EntityPlayer.getFoodStats().addExhaustion(1F);
							}

							//check for max! (variable)

							//add to internal storage instead of spawning


								/*EntityParticleControllable particle = new EntityParticleControllable(par3World, par2EntityPlayer.username, spawnType);
								particle.setPosition(par4+0.5F, par5+0.6F, par6+0.5F);
								particle.index = playerParticles.get(par2EntityPlayer.username).size();
								playerParticles.get(par2EntityPlayer.username).add(particle);
								par3World.spawnEntityInWorld(particle);*/


						}

						return EnumActionResult.SUCCESS;
					} else {
						onItemRightClick(par3World, par2EntityPlayer, EnumHand.MAIN_HAND);
						return EnumActionResult.SUCCESS;
					}
				}
			}
			return EnumActionResult.FAIL;
			//} else {
			//onItemRightClick(par1ItemStack, par3World, par2EntityPlayer);
			//}

			//return true;
		} else {
			return EnumActionResult.PASS;
		}
    }
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		if (par1ItemStack.getTagCompound() == null) par1ItemStack.setTagCompound(new NBTTagCompound());
		
		/*if (!par2World.isRemote && par3Entity instanceof EntityPlayer) {
			
			if (playerParticles.get(((EntityPlayer)par3Entity).username) != null && playerParticles.get(((EntityPlayer)par3Entity).username).size() > 0) {
				EntityParticleControllable particle = playerParticles.get(((EntityPlayer)par3Entity).username).get(0);
				if (particle.getDistanceToEntity(par3Entity) < 6D) {
					par1ItemStack.getTagCompound().setInteger("pm_nextParticleInList", particle.type);
					if (par3Entity.motionY > -0.2D) {
						par3Entity.fallDistance = 0;
					}
				} else {
					par1ItemStack.getTagCompound().setInteger("pm_nextParticleInList", -1);
				}
			} else {
				par1ItemStack.getTagCompound().setInteger("pm_nextParticleInList", -1);
			}
		}*/
		
		if (par3Entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)par3Entity;
			if (!par2World.isRemote) {
				check(CoroUtilEntity.getName(player));

				NBTTagCompound plData = player.getEntityData();
				
				if (plData == null) plData = new NBTTagCompound();

				if (par5) {
					player.capabilities.allowFlying = true;
					par1ItemStack.getTagCompound().setBoolean("wasFlying", true);
				} else {
					if (par1ItemStack.getTagCompound().getBoolean("wasFlying")) {
						player.capabilities.allowFlying = false;
						par1ItemStack.getTagCompound().setBoolean("wasFlying", false);
					}
				}

				if (player.isSneaking() && par5) {
					if (playerWasSneaking.get(CoroUtilEntity.getName(player)) == 0) {
						//System.out.println("mode toggle");
						if (plData.getInteger("particleMode") == 0) {
							plData.setInteger("particleMode", 1);
						} else {
							plData.setInteger("particleMode", 0);
						}
					}
					playerWasSneaking.put(CoroUtilEntity.getName(player), 1);
				} else {
					playerWasSneaking.put(CoroUtilEntity.getName(player), 0);
				}
				
				for (int i = 0; i < playerParticles.get(CoroUtilEntity.getName(player)).size(); i++) {
					EntityParticleControllable particle = playerParticles.get(CoroUtilEntity.getName(player)).get(i);
					
					if (particle == null || particle.isDead || par3Entity.getDistanceToEntity(particle) > 50) {
						playerParticles.get(CoroUtilEntity.getName(player)).remove(particle);
					} else {
						//ParticleMan.spinAround(particle, player, 10F, 0.5F, 2F, particle.index, 0.02F, 1);
						particle.decayTime = 0;
					}
				}
			} else {
				boolean charging = false;
				if (par5) createParticleHandEffect(par1ItemStack, par2World, par3Entity, par4, par5, charging);
				if (par3Entity instanceof EntityPlayerSP) {
					//((EntityPlayerSP)player).movementInput.moveStrafe *= 5;
					//((EntityPlayerSP)player).movementInput.moveForward *= 5;
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World par2World, EntityPlayer par3EntityPlayer, EnumHand hand) {

		ItemStack par1ItemStack = par3EntityPlayer.getHeldItemMainhand();

		if (par1ItemStack.getTagCompound() == null) par1ItemStack.setTagCompound(new NBTTagCompound());

		if (hand == EnumHand.MAIN_HAND) {
			if (!par2World.isRemote) {
				check(CoroUtilEntity.getName(par3EntityPlayer));
				if (par3EntityPlayer.isSneaking()) {
					if (par3EntityPlayer.getFoodStats().getFoodLevel() >= 6) makeShockwave(par3EntityPlayer);
				} else {
					int chargeAmount = 1;
					//par1ItemStack.getTagCompound().setInteger("pm_shootType", );
					//System.out.println("hand: " + hand);
					createParticleFromInternal(par3EntityPlayer, par1ItemStack, false, chargeAmount);
				}
			} else {
				//client side prediction, and client side movement for player
				//int nextParticle = par1ItemStack.getTagCompound().getInteger("pm_nextParticleInList");
				int fireMode = par1ItemStack.getTagCompound().getInteger("pm_fireMode");
				if (fireMode == 2) {
					int curAmount = par1ItemStack.getTagCompound().getInteger("pm_storage_" + fireMode);
					if (curAmount > 0) {
						if (par3EntityPlayer.isSneaking()) {

						} else {
							float speed = 0.35F;
							float look = 0F;

							double vecX = (double) (-Math.sin((par3EntityPlayer.rotationYaw + look) / 180.0F * 3.1415927F) * Math.cos((par3EntityPlayer.rotationPitch + 90F) / 180.0F * 3.1415927F));
							double vecY = -Math.sin((par3EntityPlayer.rotationPitch + 90F) / 180.0F * 3.1415927F);
							double vecZ = (double) (Math.cos((par3EntityPlayer.rotationYaw + look) / 180.0F * 3.1415927F) * Math.cos((par3EntityPlayer.rotationPitch + 90F) / 180.0F * 3.1415927F));

							//System.out.println("pitch: " + par3EntityPlayer.rotationPitch + " becomes: " + vecY);

							//double var9 = (double)Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
							par3EntityPlayer.motionX -= vecX * speed;
							par3EntityPlayer.motionY -= vecY * speed;
							par3EntityPlayer.motionZ -= vecZ * speed;
						}
					}
				}
			}
		}

		return super.onItemRightClick(par2World, par3EntityPlayer, hand);
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
		int fireMode = stack.getTagCompound().getInteger("pm_fireMode");
		if (!player.world.isRemote) {
			int curAmount = stack.getTagCompound().getInteger("pm_storage_" + fireMode);
			if (curAmount > 0) {
				stack.getTagCompound().setInteger("pm_storage_" + fireMode, Math.max(curAmount - depleteRate, 0));
				Element.affectEntity(entity, player, fireMode);
				if (!player.capabilities.isCreativeMode) player.getFoodStats().addExhaustion(1F);
				//player.world.playSoundEffect(player.posX, player.posY, player.posZ, /*particle.type == 0 ? "fire_shoot" : */ParticleMan.modID+":redstone_shoot", 0.9F, player.world.rand.nextFloat());
				player.world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.get("redstone_shoot"), SoundCategory.PLAYERS, 0.9F, player.world.rand.nextFloat());
			}
		}
        return false;
    }
	
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 72000;
    }
	
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.BOW;
    }
	
	@SideOnly(Side.CLIENT)
	public void createParticleHandEffect(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5, boolean charging) {
		double adjAngle = 30D;
		double dist = 0.5D;
		
		//adjAngle = 0D;
		//dist = 0D;
		
		Entity center = par3Entity;
		
		double posX = (center.posX - Math.cos((-center.rotationYaw + adjAngle) * 0.01745329D) * dist);
		double posY = (center.posY + 0.5D - Math.sin((center.rotationPitch) / 180.0F * 3.1415927F) * dist);
		double posZ = (center.posZ + Math.sin((-center.rotationYaw + adjAngle) * 0.01745329D) * dist);
		
		Random rand = new Random();
		float speed = 0.05F;
		Particle entFX = null;

		Minecraft mc = Minecraft.getMinecraft();
		
		int fireMode = par1ItemStack.getTagCompound().getInteger("pm_fireMode");
		
		int chance = 1;
		if (!charging) {
			chance = 3;
			if (fireMode == 0) {
				chance = 5;
			}
		}
		
		if (rand.nextInt(chance) != 0) return;
		
		int curAmount = par1ItemStack.getTagCompound().getInteger("pm_storage_" + fireMode);
		if (curAmount > 0) {
			if (fireMode == 0) {
				//entFX = new EntityFlameFX(par2World, posX, posY, posZ, (rand.nextFloat()-rand.nextFloat()) * speed, (rand.nextFloat()-rand.nextFloat()) * speed, (rand.nextFloat()-rand.nextFloat()) * speed);
				int id = EnumParticleTypes.FLAME.getParticleID();
				entFX = mc.effectRenderer.spawnEffectParticle(id, posX, posY, posZ, (rand.nextFloat()-rand.nextFloat()) * speed, (rand.nextFloat()-rand.nextFloat()) * speed, (rand.nextFloat()-rand.nextFloat()) * speed);
			} else if (fireMode == 1) {
				//entFX = new EntityReddustFX(par2World, posX, posY, posZ, 1F, 1F, 0F, 0F);
				int id = EnumParticleTypes.REDSTONE.getParticleID();
				entFX = mc.effectRenderer.spawnEffectParticle(id, posX, posY, posZ, 1F, 1F, 0F);
				entFX.setRBGColorF(0.5F + (float)(Math.random() * 0.5F), 0, 0);
			} else if (fireMode == 2) {
				//entFX = new EntityReddustFX(par2World, posX, posY, posZ, 1F, 1F, 0F, 0F);
				int id = EnumParticleTypes.REDSTONE.getParticleID();
				entFX = mc.effectRenderer.spawnEffectParticle(id, posX, posY, posZ, 1F, 1F, 0F);
				entFX.setRBGColorF(0, 0, 0.5F + (float)(Math.random() * 0.5F));
			}
		}
		
		if (entFX != null) {
			//entFX.motionX
			Minecraft.getMinecraft().effectRenderer.addEffect(entFX);
			//particles.add(entFX);
		}
	}

}
