package particleman.entities;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityParticleControllable extends Entity implements IEntityAdditionalSpawnData {

	public String owner = "";
	public int type = 0; //0 = fire, 1 = redstone
	
	public int decayTime = 0;
	public int decayTimeMax = 80;
	
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
			
			System.out.println(posX + " - " + posY + " - " + posZ);
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
    }

    @Override
    public void readSpawnData(ByteArrayDataInput data)
    {
        type = data.readInt();
    }

}
