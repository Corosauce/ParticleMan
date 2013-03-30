package particleman.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import particleman.forge.ParticleMan;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityParticleControllable extends Entity implements IEntityAdditionalSpawnData {

	public String owner = "";
	public int type = 0; //0 = fire, 1 = redstone
	
	public int index = 0;
	
	public int decayTime = 0;
	public int decayTimeMax = 80;
	
	@SideOnly(Side.CLIENT)
	public List<EntityFX> particles = new LinkedList<EntityFX>();
	
	public EntityParticleControllable(World par1World) {
		super(par1World);
	}
	
	public EntityParticleControllable(World par1World, String parOwner, int parType) {
		super(par1World);
		type = parType;
		owner = parOwner;
	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub

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
		
		//Movement
		float speedSlowing = 1F;
		float gravity = 0F;
		
		this.motionX *= (double)speedSlowing;
        this.motionY *= (double)speedSlowing;
        this.motionZ *= (double)speedSlowing;
        this.motionY -= (double)gravity;
        
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        
        
		
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
				
				ParticleMan.spinAround(particle, this, 10F, 0.2F, 0, i, 0.01F, 0);
				
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
    }

    @Override
    public void readSpawnData(ByteArrayDataInput data)
    {
        type = data.readInt();
        index = data.readInt();
    }

}
