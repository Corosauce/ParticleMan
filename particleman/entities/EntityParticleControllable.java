package particleman.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import particleman.forge.ParticleMan;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityParticleControllable extends Entity implements IEntityAdditionalSpawnData {

	public String owner = "";
	public int type = 0; //0 = fire, 1 = redstone
	public int state = 0; //0 = being grabbed, 1 = free
	
	public int index = 0;
	
	public int decayTime = 0;
	public int decayTimeMax = 80;
	
	public int regrabDelay = 0;
	public int regrabDelayMax = 20;
	
	@SideOnly(Side.CLIENT)
	public List<EntityFX> particles = new LinkedList<EntityFX>();
	
	public EntityParticleControllable(World par1World) {
		super(par1World);
		
		this.setSize(0.5F, 0.5F);
	}
	
	public EntityParticleControllable(World par1World, String parOwner, int parType) {
		super(par1World);
		type = parType;
		owner = parOwner;
	}

	@Override
	protected void entityInit() {
		
		this.dataWatcher.addObject(16, Byte.valueOf((byte)state));

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
		
		//Server logic
		if (!worldObj.isRemote) {

			decayTime++;
			
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
		if (state == 0) {
			if (worldObj.playerEntities.size() > 0) {
				EntityPlayer player = worldObj.getPlayerEntityByName(owner);
				if (player != null) {
					/*if (worldObj.isRemote) */ParticleMan.spinAround(this, player, 10F, 0.5F, 2F, index, 0.02F, 1, (player.getEntityData() != null && player.getEntityData().getInteger("particleMode") == 1) ? 1 : 0);
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
		
		//Movement
		float speedSlowing = 0.98F;
		float gravity = 0F;
		
		this.motionX *= (double)speedSlowing;
        this.motionY *= (double)speedSlowing;
        this.motionZ *= (double)speedSlowing;
        this.motionY -= (double)gravity;
        
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        
        this.setPosition(posX, posY, posZ);
        
        this.onGround = true;
        
        if (!worldObj.isRemote) {
        	double size = 0.5D;
	        List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(size, size, size));
	        
	        for (int i = 0; entities != null && i < entities.size(); ++i)
	        {
	            Entity var10 = (Entity)entities.get(i);
	            
	            if (var10 != null && !var10.isDead && ((var10 instanceof EntityPlayer && ((EntityPlayer)var10).username != owner) || (var10 instanceof EntityLiving && ((EntityLiving)var10).health > 0 && !(var10 instanceof EntityPlayer)))) {
	            	Random rand = new Random();
	            	
	            	var10.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this), 10);
	            	setDead();
	            	//this.motionX *= (0.95F + (rand.nextFloat() * 0.05F));
	            	//this.motionY *= rand.nextFloat();
	            	//this.motionZ *= (0.95F + (rand.nextFloat() * 0.05F));
	            	break;
	            }
	            
	        }
        }
		
    }
	
	@SideOnly(Side.CLIENT)
	public void manageParticles() {
		Random rand = new Random();
		float speed = 0.05F;
		EntityFX entFX = null;
		
		//System.out.println("particles.size(): " + particles.size());
		
		if (particles.size() < 15) {
			if (type == 0) {
				entFX = new EntityFlameFX(worldObj, posX, posY, posZ, (rand.nextFloat()-rand.nextFloat()) * speed, (rand.nextFloat()-rand.nextFloat()) * speed, (rand.nextFloat()-rand.nextFloat()) * speed);
			} else if (type == 1) {
				entFX = new EntityReddustFX(worldObj, posX, posY, posZ, 1F, 1F, 0F, 0F);
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
				
				particle.particleAge = 0;
				
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
