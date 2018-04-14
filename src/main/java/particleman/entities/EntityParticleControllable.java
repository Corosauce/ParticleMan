package particleman.entities;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import particleman.element.Element;
import particleman.forge.ParticleMan;
import particleman.items.ItemParticleGlove;
import CoroUtil.util.CoroUtilEntity;

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
	public List<Particle> particles;

    private static final DataParameter<Integer> DP_STATE = EntityDataManager.<Integer>createKey(EntityParticleControllable.class, DataSerializers.VARINT);
	
	public EntityParticleControllable(World par1World) {
		super(par1World);
		
		this.setSize(0.25F, 0.25F);
	}
	
	public EntityParticleControllable(World par1World, String parOwner, int parType) {
		this(par1World);
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
		
		//this.dataWatcher.addObject(16, Byte.valueOf((byte)state));
        this.getDataManager().register(DP_STATE, Integer.valueOf(0));

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
		if (!world.isRemote) {

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
		if (world.isRemote) {
            state = this.getDataManager().get(DP_STATE);
		} else {
            this.getDataManager().set(DP_STATE, state);
		}
		if (!world.isRemote) {
			if (state == 0) {
				EntityPlayer player = null;
				if (world.playerEntities.size() > 0) {
					player = world.getPlayerEntityByName(owner);
				}
				if (player != null) {
					lastMode = player.getEntityData().getInteger("particleMode");
					/*if (world.isRemote) {
						System.out.println(player.getEntityData().getInteger("particleMode"));
					}*/
					//this nbt lookup doesnt work (for client)
					/*if (world.isRemote) */ParticleMan.spinAround(this, player, 10F, 0.5F, 2F, index, 0.02F, 1, moveMode);
					
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
					Entity ent = world.getEntityByID(ownerEntityID);
					if (ent != null) {
						ParticleMan.spinAround(this, ent, 10F, 0.5F, 2F, index, 0.02F, 1, 1);
					}
				}
				
			} else {
				if (!world.isRemote) {
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
        
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        
        if (!world.isRemote) {
        	if (isInWater()) {
	        	if (type == 0) {
	        		setDead();
	        		//world.playSoundEffect(posX, posY, posZ, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
					world.playSound(posX, posY, posZ, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.AMBIENT, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F, false);
	        	}
        	}
        	
        	//Block id = world.getBlock((int)posX, (int)posY, (int)posZ);
			BlockPos pos = new BlockPos(posX, posY, posZ);
			IBlockState state = world.getBlockState(pos);
        	
        	if (type == 2) {
        		if (state.getBlock() == Blocks.FIRE) {
            		//world.setBlock((int)posX, (int)posY, (int)posZ, Blocks.AIR);
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
            		health--;
            	}
        	}
        	
        }
        
        //this.setPosition(posX, posY, posZ);
        
        
        
        if (!world.isRemote) {
        	double size = 0.5D;
	        List entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(size, size, size));
	        
	        for (int i = 0; entities != null && i < entities.size(); ++i)
	        {
	            Entity var10 = (Entity)entities.get(i);
	            
	            if (var10 != null && !var10.isDead &&
						(world.getEntityByID(ownerEntityID) != var10) &&
						((var10 instanceof EntityPlayer && !CoroUtilEntity.getName(var10).equals(owner) && FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()) ||
								(var10 instanceof EntityLivingBase && ((EntityLivingBase)var10).getHealth() > 0 && !(var10 instanceof EntityPlayer || owner.equals(""))))) {
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
	            } else if (var10 instanceof EntityPlayer && CoroUtilEntity.getName(var10).equals(owner)) {
	            	EntityPlayer entP = (EntityPlayer)var10;
	            	if (type == 2) {
	            		var10.extinguish();
	            	}
	            	if (state == 0 && moveMode == 0) {
	            		//ItemStack is = entP.getCurrentEquippedItem();
						ItemStack is = entP.getHeldItem(EnumHand.MAIN_HAND);

						EnumHand handToUse = EnumHand.MAIN_HAND;
						if (is.getItem() != ParticleMan.itemGlove && entP.getHeldItem(EnumHand.OFF_HAND).getItem() == ParticleMan.itemGlove) {
							is = entP.getHeldItem(EnumHand.OFF_HAND);
							handToUse = EnumHand.OFF_HAND;
						}
			        	
			        	if (!is.isEmpty() && is.getItem() instanceof ItemParticleGlove) {
			        		if (is.getTagCompound() == null) is.setTagCompound(new NBTTagCompound());
			        		int curAmount = is.getTagCompound().getInteger("pm_storage_" + type);
							
							if (curAmount < ItemParticleGlove.maxStorage) {
								is.getTagCompound().setInteger("pm_storage_" + type, Math.min(curAmount + ItemParticleGlove.depleteRate, ItemParticleGlove.maxStorage));
							}
							
							this.setDead();
			        	}
	            	}
	            } else if (var10 instanceof EntityLivingBase) {
	            	if (type == 2) {
	            		var10.extinguish();
	            	}
	            }
	            
	        }
        }
        if (!world.isRemote) {
	        if (health <= 0) {
	    		setDead();
	    	}
        }
		
    }
	
	@SideOnly(Side.CLIENT)
	public void manageParticles() {
		Random rand = new Random();
		float speed = 0.05F;
		Particle entFX = null;

		Minecraft mc = Minecraft.getMinecraft();

		//System.out.println("particles.size(): " + particles.size());
		
		if (particles == null) particles = new LinkedList<>();
		
		if (particles.size() < 15) {
			if (type == 0) {
				//entFX = new ParticleFlame(world, posX, posY, posZ, (rand.nextFloat()-rand.nextFloat()) * speed, (rand.nextFloat()-rand.nextFloat()) * speed, (rand.nextFloat()-rand.nextFloat()) * speed);
				int id = EnumParticleTypes.FLAME.getParticleID();
				entFX = mc.effectRenderer.spawnEffectParticle(id, posX, posY, posZ, (rand.nextFloat()-rand.nextFloat()) * speed, (rand.nextFloat()-rand.nextFloat()) * speed, (rand.nextFloat()-rand.nextFloat()) * speed);

			} else if (type == 1) {
				//entFX = new EntityReddustFX(world, posX, posY, posZ, 1F, 1F, 0F, 0F);
				int id = EnumParticleTypes.REDSTONE.getParticleID();
				entFX = mc.effectRenderer.spawnEffectParticle(id, posX, posY, posZ, 1F, 1F, 0F);
				entFX.setRBGColorF(0.5F + (float)(Math.random() * 0.5F), 0, 0);
			} else if (type == 2) {
				//entFX = new EntityReddustFX(world, posX, posY, posZ, 1F, 1F, 0F, 0F);
				int id = EnumParticleTypes.REDSTONE.getParticleID();
				entFX = mc.effectRenderer.spawnEffectParticle(id, posX, posY, posZ, 1F, 1F, 0F);
				entFX.setRBGColorF(0, 0, 0.5F + (float)(Math.random() * 0.5F));
			}
		}

		if (entFX != null) {
			//done via spawnEffectParticle now
			//Minecraft.getMinecraft().effectRenderer.addEffect(entFX);
			particles.add(entFX);
		}
		
		for (int i = 0; i < particles.size(); i++) {
			Particle particle = particles.get(i);
			
			if (particle == null || !particle.isAlive()) {
				particles.remove(particle);
			} else {
				
				ParticleMan.spinAround(particle, this, 10F, 0.2F, 0, i, 0.01F, 0, 0);
				
				//ReflectionHelper.setPrivateValue(EntityFX.class, particle, 0, "particleAge", "particleAge");
				
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
    public void writeSpawnData(ByteBuf data)
    {
        data.writeInt(type);
        data.writeInt(index);
        ByteBufUtils.writeUTF8String(data, owner);
    }

    @Override
    public void readSpawnData(ByteBuf data)
    {
        type = data.readInt();
        index = data.readInt();
        owner = ByteBufUtils.readUTF8String(data);
    }

}
