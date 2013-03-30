package particleman.forge;

import java.util.Random;
import java.util.logging.Level;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkMod;


@NetworkMod(clientSideRequired = true, serverSideRequired = true)
@Mod(modid = "ParticleMan", name="Particle Man", version="v1.0")
public class ParticleMan {
	
	@Mod.Instance( value = "ParticleMan" )
	public static ParticleMan instance;
    
    /** For use in preInit ONLY */
    public Configuration config;
    
    @SidedProxy(clientSide = "particleman.forge.ClientProxy", serverSide = "particleman.forge.CommonProxy")
    public static CommonProxy proxy;
    
    int itemIDStart = 3242;
    
    public static Item itemGlove;

    public ParticleMan() {
    	
    }
    
    public static void spinAround(Entity source, Entity center, float angleRate, float radius, float distOffset, int index, float speed, int mode) {
		
    	if (mode == 0) {
			float angle = (-center.rotationYaw + 65F/* + ((float)Math.sin(worldObj.getWorldTime() * 0.1F) * 3F)*/) * 0.01745329F;
			float angle2 = (-center.rotationYaw + 65F + ((index*60) + source.worldObj.getWorldTime() % 360)) * 0.01745329F;
			
			float dist = distOffset;
			
			//temp
			//radius = 1.0F;
			//angleRate = 10F;
			float angleRateY = 10F;
			
			float angleRateRad = angleRate * 0.01745329F;
			
			float i = index; //use for particleindex, to offset position
			
			float range1 = (float) (Math.sin(((source.worldObj.getWorldTime() - (i*3.5F)) * angleRateRad)) * radius);
	        float range2 = (float) (Math.cos(((source.worldObj.getWorldTime() - (i*30.5F)) * (angleRateY * 0.01745329F))) * radius); 
			
	        /*source.posX = ;
	        source.posY = ;
	        source.posZ = ;*/
			
	        double x = center.posX - ((Math.cos(angle2) * range1) + (Math.cos(angle) * dist));
	        double y = center.posY + range2 + 0.8F;
	        double z = center.posZ + ((Math.sin(angle2) * range1) + (Math.sin(angle) * dist));
	        
	        /*source.posX = x;
	        source.posY = y;
	        source.posZ = z;*/
	        /*source.motionX *= 0.8F;
	        source.motionY *= 0.8F;
	        source.motionZ *= 0.8F;
	        source.setPosition(x, y, z);*/
	        
	        float speed2 = 0.05F;
	    	
	    	double vecX = x - source.posX;
	    	double vecY = y - source.posY;
	    	double vecZ = z - source.posZ;
	        
	        double dist2 = (double)Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
	        source.motionX += vecX / dist2 * speed2;
	        source.motionY += vecY / dist2 * speed2;
	        source.motionZ += vecZ / dist2 * speed2;
	        
	        
	        
	        if (dist2 < 1D) {
	        	source.motionX *= 0.8F;
		        source.motionY *= 0.8F;
		        source.motionZ *= 0.8F;
	        } else if (dist2 > 10D) {
	        	source.setPosition(center.posX, center.posY + 0.68, center.posZ);
	        	source.motionX = center.motionX * 0.99F;
		        source.motionY = center.motionY * 0.99F;
		        source.motionZ = center.motionZ * 0.99F;
	        } else if (dist2 > 2D) {
	        	//source.setPosition(center.posX, center.posY + 0.68, center.posZ);
	        	source.motionX += center.motionX;
		        source.motionY += center.motionY;
		        source.motionZ += center.motionZ;
	        } else {
	        	
	        }
	        
	        source.motionX *= 0.95F;
	        source.motionY *= 0.95F;
	        source.motionZ *= 0.95F;
	        
    	} else if (mode == 1) {
    		double adjAngle = 40D;
    		double dist = 1.5D;
    		double newX = (center.posX - Math.cos((-center.rotationYaw + adjAngle) * 0.01745329D) * dist);
    		double newY = center.worldObj.isRemote ? center.posY - 1.68 : center.posY;
    		double newZ = (center.posZ + Math.sin((-center.rotationYaw + adjAngle) * 0.01745329D) * dist);
    		double vecX = newX - source.posX;
    		double vecZ = newZ - source.posZ;
    		
    		double angle = ((Math.atan2(vecZ, vecX) * 180D) / Math.PI);
    		angle += 7D;
    		
    		double speedThreshold = 0.3F;
    		
    		if (source.getDistance(newX, newY, newZ) < 2F) {
    			speedThreshold = 0.2F;
    			speed *= 0.5D;
    		} else {
    			angle -= 10D;
    		}
    		
    		if (Math.sqrt(source.motionX * source.motionX + source.motionZ * source.motionZ) < speedThreshold) {
	    		source.motionX -= Math.cos(-angle * 0.01745329D - Math.PI) * speed * 0.8F;
	    		//source.motionY += Math.sin((center.posY - source.posY * 0.01745329D)) * speed;
	    		source.motionZ += Math.sin(-angle * 0.01745329D - Math.PI) * speed * 0.8F;
    		} else {
    			source.motionX *= 0.95F;
    	        source.motionY *= 0.95F;
    	        source.motionZ *= 0.95F;
    		}

    		Random rand = new Random();
    		if (source.posY + 0.2D > newY + 0.5D) source.motionY -= /*rand.nextFloat() * */0.01F;
    		if (source.posY - 0.2D < newY + 0.5D) source.motionY += /*rand.nextFloat() * */0.01F;
    		
    		//source.setPosition(source.posX, center.posY + 0.7F, source.posZ);
    		//source.posY = center.posY + 0.7F;
    		
	        
	        
    	}
        
        
        
		
		
		
		
	}
    
    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
    	config = new Configuration(event.getSuggestedConfigurationFile());

        try
        {
        	config.load();
        	itemIDStart = config.get(Configuration.CATEGORY_BLOCK, "itemIDStart", itemIDStart).getInt(itemIDStart);
            
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "Hostile Worlds has a problem loading it's configuration");
        }
        finally
        {
        	config.save();
        }
    }
    
    @Init
    public void load(FMLInitializationEvent event)
    {
    	proxy.init(this);
    }
    
    @PostInit
	public void postInit(FMLPostInitializationEvent event) {
		
	}
    
    @Mod.ServerStarted
    public void serverStart(FMLServerStartedEvent event) {
    	
    }
    
    @Mod.ServerStopped
    public void serverStop(FMLServerStoppedEvent event) {
    	
    }
    
	public static void dbg(Object obj) {
		if (true) System.out.println(obj);
	}
}
