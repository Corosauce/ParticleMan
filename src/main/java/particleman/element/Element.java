package particleman.element;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import particleman.entities.EntityParticleControllable;

public class Element {

	public static void affectEntity(Entity affectee, Entity affecter, int parType) {
		
		int type = parType;
		Entity source = affecter;
		
		if (affecter instanceof EntityParticleControllable) {
			EntityParticleControllable particle;
			particle = (EntityParticleControllable)affecter;
			source = particle.worldObj.getPlayerEntityByName(particle.owner);
			//type = particle.type;
		} else if (affecter instanceof EntityPlayer) {
			
		}
		
		double x = affectee.motionX;
		double y = affectee.motionY;
		double z = affectee.motionZ;
    	
		int damage = 6;
		int fire = 0;
		boolean knockback = true;
		boolean vanillaKnockback = false;
		if (affecter instanceof EntityPlayer) vanillaKnockback = true; //melee used
		double knockBackXZFactor = 0.4D;
		double knockBackY = 0.3D;
		
		if (type == 0) {
			fire = 5;
			knockBackXZFactor = 0.2D;
			knockBackY = 0.0D;
		} else if (type == 1) {
			
		} else if (type == 2) {
			damage = 2;
			knockBackXZFactor = 0.5D;
			knockBackY = 0.01D;
		}
		
		
    	if (type == 0) {
    		if (!affectee.isImmuneToFire()) {
    			affectee.attackEntityFrom(DamageSource.causeIndirectMagicDamage(affecter, source), damage);
    		}
    	} else if (type == 1) {
    		affectee.attackEntityFrom(DamageSource.causeIndirectMagicDamage(affecter, source), damage);
    	} else if (type == 2) {
    		affectee.extinguish();
    		affectee.attackEntityFrom(DamageSource.cactus, damage); //marking diff damage to boost it on worms side of code
    		//affectee.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, worldObj.getPlayerEntityByName(owner)), 2);
    	}
    	
    	if (fire > 0) affectee.setFire(fire);

    	//prevents VANILLA knockback (mainly for worm)
    	if (!vanillaKnockback && type != 1) {
    		affectee.motionX = x;
    		affectee.motionY = y;
    		affectee.motionZ = z;
    	}
    	
    	if (knockback) {
    		double speed2 = knockBackXZFactor;

			double vecX = affectee.posX - affecter.posX;
			double vecY = affectee.posY - affecter.posY;
			double vecZ = affectee.posZ - affecter.posZ;

			double dist2 = (double)Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
			double particleSpeed = (double)Math.sqrt(affecter.motionX * affecter.motionX + affecter.motionY * affecter.motionY + affecter.motionZ * affecter.motionZ);
			
			if (type == 1) speed2 += particleSpeed;
			
			affectee.motionX += vecX / dist2 * speed2;
			affectee.motionY += knockBackY;
			affectee.motionZ += vecZ / dist2 * speed2;
    	}
	}
	
}
