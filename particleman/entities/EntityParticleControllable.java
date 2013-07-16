package particleman.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import particleman.element.Element;
import particleman.forge.ParticleMan;
import particleman.items.ItemParticleGlove;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityParticleControllable extends Entity implements IEntityAdditionalSpawnData {

	public String owner = "";
	public int ownerEntityID = -1;
	public int type = 0; //0 = fire, 1 = redstone, 2 = water
	public int state = 0; //0 = being grabbed, 1 = free
	public int lastMode = 0; //0 = close to hand, 1 = shield - used on both client and server side based on the crouching state toggle
	public int moveMode = 0; //0 = reabsorb, 1 = shield, respects state field by only absorbing when being grabbed
	public int health = 0;
	
	public int index = 0;
	
	public int decayTime = 0;
	public int decayTimeMax = 300; //this isnt needed for removing particles anymore, so set super high so particles can come back to me!
	
	public int regrabDelay = 0;
	public int regrabDelayMax = 20;
	
	public int awayFromOwnerTicks = 0;
	
	@SideOnly(Side.CLIENT)
	public List<EntityFX> particles;
	
	public EntityParticleControllable(World par1World) {
		super(par1World);
		
		this.setSize(0.5F, 0.5F);
	}
	
	public EntityParticleControllable(World par1World, String parOwner, int parType) {
		super(par1World);
		type = parType;
		owner = parOwner;
		if (type == 0) {
			//health = 5;
			health = 2;
		} else {
			health = 3;
		}
	}

	@Override
	protected void entityInit() {
		
		this.dataWatcher.addObject(16, Byte.valueOf((byte)state));

	}
	
	@Override
	public boolean canTriggerWalking() {
		return false;
	}
	
	public void influenceParticle(float parMotionX, float parMotionY, float parMotionZ) {
		decayTime = 0;
		motionX += parMotionX;
		motionY += parMotionY;
		motionZ += parMotionZ;
	}
	
	@Override
	public void onUpdate()
    {
		super.onUpdate();
		
		isImmuneToFire = true;
		
		//Server logic
		if (!worldObj.isRemote) {

			decayTime++;
			
			if (type == 2) {
				decayTimeMax = 40;
			} else {
				decayTimeMax = 300;
			}
			
			if (decayTime > decayTimeMax) {
				setDead();
			}
			
			//System.out.println(posX + " - " + posY + " - " + posZ);
			
		//Client logic
		} else {
			manageParticles();
			
		}
		if (worldObj.isRemote) {
			state = this.dataWatcher.getWatchableObjectByte(16);
		} else {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)state));
		}
		if (!worldObj.isRemote) {
			if (state == 0) {
				EntityPlayer player = null;
				if (worldObj.playerEntities.size() > 0) {
					player = worldObj.getPlayerEntityByName(owner);
				}
				if (player != null) {
					lastMode = player.getEntityData().getInteger("particleMode");
					/*if (worldObj.isRemote) {
						System.out.println(player.getEntityData().getInteger("particleMode"));
					}*/
					//this nbt lookup doesnt work (for client)
					/*if (worldObj.isRemote) */ParticleMan.spinAround(this, player, 10F, 0.5F, 2F, index, 0.02F, 1, moveMode);
					
					if (player.getDistanceToEntity(this) > 15) {
						awayFromOwnerTicks++;
					} else {
						awayFromOwnerTicks = 0;
					}
					
					if (awayFromOwnerTicks > 20*5) {
						awayFromOwnerTicks = 0;
						this.setPosition(player.posX, player.posY, player.posZ);
					}
					
				} else if (ownerEntityID != -1) {
					Entity ent = worldObj.getEntityByID(ownerEntityID);
					if (ent != null) {
						ParticleMan.spinAround(this, ent, 10F, 0.5F, 2F, index, 0.02F, 1, 1);
					}
				}
				
			} else {
				if (!worldObj.isRemote) {
					if (regrabDelay++ > regrabDelayMax) {
						regrabDelay = 0;
						state = 0;
					}
				}
			}
		}
		
		//Movement
		float speedSlowing = 0.98F;
		float gravity = 0F;
		
		if (isInWater()) speedSlowing *= 0.8F;
		
		this.motionX *= (double)speedSlowing;
        this.motionY *= (double)speedSlowing;
        this.motionZ *= (double)speedSlowing;
        this.motionY -= (double)gravity;
        
        /*this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;*/
        
        //this.onGround = false;
        
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        
        if (!worldObj.isRemote) {
        	if (isInWater()) {
	        	if (type == 0) {
	        		setDead();
	        		worldObj.playSoundEffect(posX, posY, posZ, "random.fizz", 0.5F, 2.6F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.8F);
	        	}
        	}
        	
        	int id = worldObj.getBlockId((int)posX, (int)posY, (int)posZ);
        	
        	if (type == 2) {
        		if (id == Block.fire.blockID) {
            		worldObj.setBlock((int)posX, (int)posY, (int)posZ, 0);
            		health--;
            	}
        	}
        	
        }
        
        //this.setPosition(posX, posY, posZ);
        
        
        
        if (!worldObj.isRemote) {
        	double size = 0.5D;
	        List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(size, size, size));
	        
	        for (int i = 0; entities != null && i < entities.size(); ++i)
	        {
	            Entity var10 = (Entity)entities.get(i);
	            
	            if (var10 != null && !var10.isDead && (worldObj.getEntityByID(ownerEntityID) != var10) && ((var10 instanceof EntityPlayer && ((EntityPlayer)var10).username != owner && MinecraftServer.getServer().isPVPEnabled()) || (var10 instanceof EntityLiving && ((EntityLiving)var10).health > 0 && !(var10 instanceof EntityPlayer || owner.equals(""))))) {
	            	Random rand = new Random();
	            	
	            	if (!(var10 instanceof EntityAnimal) || ParticleMan.hurtAnimals) {
	            		Element.affectEntity(var10, this, type);
		            	health--;
	            	}
	            	
	            	//this.motionX *= (0.95F + (rand.nextFloat() * 0.05F));
	            	//this.motionY *= rand.nextFloat();
	            	//this.motionZ *= (0.95F + (rand.nextFloat() * 0.05F));
	            	
	            } else if (var10 instanceof EntityParticleControllable) {
	            	float speed2 = 0.003F;

					double vecX = posX - var10.posX;
					double vecY = posY - var10.posY;
					double vecZ = posZ - var10.posZ;

					double dist2 = (double)Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
					if (lastMode == 1 && (double)Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) < 0.2) {
						motionX += vecX / dist2 * speed2;
						//particle.motionY += vecY / dist2 * speed2;
						motionZ += vecZ / dist2 * speed2;
					}
					//break;
	            } else if (var10 instanceof EntityPlayer && ((EntityPlayer)var10).username.equals(owner)) {
	            	EntityPlayer entP = (EntityPlayer)var10;
	            	if (type == 2) {
	            		var10.extinguish();
	            	}
	            	if (state == 0 && moveMode == 0) {
	            		ItemStack is = entP.getCurrentEquippedItem();
			        	
			        	if (is != null && is.getItem() instanceof ItemParticleGlove) {
			        		if (is.stackTagCompound == null) is.stackTagCompound = new NBTTagCompound();
			        		int curAmount = is.stackTagCompound.getInteger("pm_storage_" + type);
							
							if (curAmount < ItemParticleGlove.maxStorage) {
								is.stackTagCompound.setInteger("pm_storage_" + type, Math.min(curAmount + ItemParticleGlove.depleteRate, ItemParticleGlove.maxStorage));
							}
							
							this.setDead();
			        	}
	            	}
	            } else if (var10 instanceof EntityLiving) {
	            	if (type == 2) {
	            		var10.extinguish();
	            	}
	            }
	            
	        }
        }
        if (!worldObj.isRemote) {
	        if (health <= 0) {
	    		setDead();
	    	}
        }
		
    }
	
	@SideOnly(Side.CLIENT)
	public void manageParticles() {
		Random rand = new Random();
		float speed = 0.05F;
		EntityFX entFX = null;
		
		//System.out.println("particles.size(): " + particles.size());
		
		if (particles == null) particles = new LinkedList<EntityFX>();
		
		if (particles.size() < 15) {
			if (type == 0) {
				entFX = new EntityFlameFX(worldObj, posX, posY, posZ, (rand.nextFloat()-rand.nextFloat()) * speed, (rand.nextFloat()-rand.nextFloat()) * speed, (rand.nextFloat()-rand.nextFloat()) * speed);
			} else if (type == 1) {
				entFX = new EntityReddustFX(worldObj, posX, posY, posZ, 1F, 1F, 0F, 0F);
				/*entFX.setRBGColorF(0, 0, 0.5F + (float)(Math.random() * 0.5F));
				entFX.setRBGColorF(0.7F + (float)(Math.random() * 0.5F), 0.5F + (float)(Math.random() * 0.5F), 0.5F + (float)(Math.random() * 0.5F));
				entFX.setRBGColorF((float)Math.random(), (float)Math.random(), (float)Math.random());*/
			} else if (type == 2) {
				entFX = new EntityReddustFX(worldObj, posX, posY, posZ, 1F, 1F, 0F, 0F);
				entFX.setRBGColorF(0, 0, 0.5F + (float)(Math.random() * 0.5F));
			}
		}
		
		if (entFX != null) {
			Minecraft.getMinecraft().effectRenderer.addEffect(entFX);
			particles.add(entFX);
		}
		
		for (int i = 0; i < particles.size(); i++) {
			EntityFX particle = particles.get(i);
			
			if (particle == null || particle.isDead) {
				particles.remove(particle);
			} else {
				
				ParticleMan.spinAround(particle, this, 10F, 0.2F, 0, i, 0.01F, 0, 0);
				
				//ReflectionHelper.setPrivateValue(EntityFX.class, particle, 0, "field_70546_d", "particleAge");
				
				//particle.particleAge = 0;
				
				//particle.setPosition(posX, posY, posZ);
				
				/*speed = 0.1F;
		    	
		    	double vecX = posX - particle.posX;
		    	double vecY = posY - particle.posY;
		    	double vecZ = posZ - particle.posZ;
		        
		        double var9 = (double)MathHelper.sqrt_double(vecX * vecX + vecY * vecY + vecZ * vecZ);
		        particle.motionX = vecX / var9 * speed;
		        particle.motionY = vecY / var9 * speed;
		        particle.motionZ = vecZ / var9 * speed;*/
			}
		}
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		// TODO Auto-generated method stub

	}

	@Override
    public void writeSpawnData(ByteArrayDataOutput data)
    {
        data.writeInt(type);
        data.writeInt(index);
        data.writeUTF(owner);
    }

    @Override
    public void readSpawnData(ByteArrayDataInput data)
    {
        type = data.readInt();
        index = data.readInt();
        owner = data.readUTF();
    }

}
