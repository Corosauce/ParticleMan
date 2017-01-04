package particleman.forge;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import CoroUtil.util.CoroUtilEntOrParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import particleman.entities.EntityParticleControllable;
import particleman.items.ItemParticleGlove;


@Mod(modid = "particleman", name="Particle Man", version="v1.0", dependencies="required-after:coroutil")
public class ParticleMan {
	
	@Mod.Instance( value = "particleman" )
	public static ParticleMan instance;
	public static String modID = "particleman";
    
    /** For use in preInit ONLY */
    public Configuration config;
    
    @SidedProxy(clientSide = "particleman.forge.ClientProxy", serverSide = "particleman.forge.CommonProxy")
    public static CommonProxy proxy;
    
    //Config
    public static boolean hurtAnimals = false;
    
    public static Item itemGlove;
    
    public static String eventChannelName = "particleman";
	public static final FMLEventChannel eventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(eventChannelName);

    public ParticleMan() {
    	
    }
    
    public static void spinAround(Object source, Entity center, float angleRate, float radius, float distOffset, int index, float speed, int controlType, int mode) {
    	Random rand = new Random();

		World world = center.worldObj;

    	double motionX = CoroUtilEntOrParticle.getMotionX(source);
		double motionY = CoroUtilEntOrParticle.getMotionY(source);
		double motionZ = CoroUtilEntOrParticle.getMotionZ(source);

		double posX = CoroUtilEntOrParticle.getPosX(source);
		double posY = CoroUtilEntOrParticle.getPosY(source);
		double posZ = CoroUtilEntOrParticle.getPosZ(source);

    	if (controlType == 0) {
			float angle = (-center.rotationYaw + 65F/* + ((float)Math.sin(worldObj.getWorldTime() * 0.1F) * 3F)*/) * 0.01745329F;
			float angle2 = (-center.rotationYaw + 65F + ((index*60) + world.getWorldTime() % 360)) * 0.01745329F;
			
			float dist = distOffset;
			
			//temp
			//radius = 1.0F;
			//angleRate = 10F;
			float angleRateY = 10F;
			
			float angleRateRad = angleRate * 0.01745329F;
			
			float i = index; //use for particleindex, to offset position
			
			float range1 = (float) (Math.sin(((world.getWorldTime() - (i*3.5F)) * angleRateRad)) * radius);
	        float range2 = (float) (Math.cos(((world.getWorldTime() - (i*30.5F)) * (angleRateY * 0.01745329F))) * radius);
			
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
	    	
	    	double vecX = x - posX;
	    	double vecY = y - posY;
	    	double vecZ = z - posZ;
	        
	        double dist2 = (double)Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
	        motionX += vecX / dist2 * speed2;
	        motionY += vecY / dist2 * speed2;
	        motionZ += vecZ / dist2 * speed2;
	        
	        if (dist2 < 1D) {
	        	motionX *= 0.8F;
		        motionY *= 0.8F;
		        motionZ *= 0.8F;
	        } else if (dist2 > 10D) {
				if (source instanceof Entity) {
					((Entity)source).setPosition(center.posX, center.posY + 0.68, center.posZ);
				} else {
					CoroUtilEntOrParticle.setPosX(source, center.posX);
					CoroUtilEntOrParticle.setPosY(source, center.posY + 0.68);
					CoroUtilEntOrParticle.setPosZ(source, center.posZ);
				}

	        	motionX = center.motionX * 0.99F;
		        motionY = center.motionY * 0.99F;
		        motionZ = center.motionZ * 0.99F;
	        } else if (dist2 > 2D) {
	        	//source.setPosition(center.posX, center.posY + 0.68, center.posZ);
	        	motionX += center.motionX;
		        motionY += center.motionY;
		        motionZ += center.motionZ;
	        } else {
	        	
	        }
	        
	        motionX *= 0.95F;
	        motionY *= 0.95F;
	        motionZ *= 0.95F;
	        
    	} else if (controlType == 1) {
    		double adjAngle = 20D;
    		double dist = 1.2D;
    		
    		if (mode == 1) {
    			adjAngle = 0D;
    			dist = 0D;
    		}
    		
    		double newX = (center.posX - Math.cos((-center.rotationYaw + adjAngle) * 0.01745329D) * dist);
    		double newY = center.worldObj.isRemote ? center.posY - 1.68 : center.posY;
    		double newZ = (center.posZ + Math.sin((-center.rotationYaw + adjAngle) * 0.01745329D) * dist);
    		double vecX = newX - posX;
    		double vecZ = newZ - posZ;
    		
    		double angle = ((Math.atan2(vecZ, vecX) * 180D) / Math.PI);
    		angle += 20D;
    		
    		double speedThreshold = 0.3F;
    		
    		if (mode == 1) {
    			angle += 5D;
    			speed -= (rand.nextFloat() * 0.02F);
    			speed *= 0.5F;
    			if (CoroUtilEntOrParticle.getDistance(source, newX, newY, newZ) > 3F) {
    				angle -= 10D;
    			}
    		} else {
    			
    			//angle -= 5D;
    			
	    		if (CoroUtilEntOrParticle.getDistance(source, newX, newY, newZ) < 3F) {
	    			speedThreshold = 0.2F;
	    			//speed *= 0.5D;
	    			motionX *= 0.89F;
	    	        motionY *= 0.89F;
	    	        motionZ *= 0.89F;
	    		} else {
	    			angle -= 30D;
	    		}
    		}
    		
    		speed += (rand.nextFloat() * 0.005F);
    		
    		if (Math.sqrt(motionX * motionX + motionZ * motionZ) < speedThreshold) {
	    		motionX -= Math.cos(-angle * 0.01745329D - Math.PI) * speed * 1.5F;
	    		//source.motionY += Math.sin((center.posY - source.posY * 0.01745329D)) * speed;
	    		motionZ += Math.sin(-angle * 0.01745329D - Math.PI) * speed * 1.5F;
    		} else {
    			motionX *= 0.95F;
    	        motionY *= 0.95F;
    	        motionZ *= 0.95F;
    		}

    		if (posY + 0.2D > newY + 0.2D) motionY -= /*rand.nextFloat() * */0.01F;
    		if (posY - 0.2D < newY + 0.2D) motionY += /*rand.nextFloat() * */0.01F;
    		
    	}
        
	}
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	
    	eventChannel.register(new EventHandlerPacket());
    	
    	config = new Configuration(event.getSuggestedConfigurationFile());

        try
        {
        	config.load();
        	//itemIDStart = config.get(Configuration.CATEGORY_BLOCK, "itemIDStart", itemIDStart).getInt(itemIDStart);
        	hurtAnimals = config.get(Configuration.CATEGORY_GENERAL, "hurtAnimals", false).getBoolean(false);
            
        }
        catch (Exception e)
        {
            System.out.println("Hostile Worlds has a problem loading it's configuration");
        }
        finally
        {
        	config.save();
        }
        
        proxy.preInit(this);
    }
    
    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
    	proxy.init(this);
    	FMLCommonHandler.instance().bus().register(new EventHandlerFML());
    	
    }
    
    @Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
    
    @Mod.EventHandler
    public void serverStart(FMLServerStartedEvent event) {
    	ItemParticleGlove.playerParticles = new HashMap<String, List<EntityParticleControllable>>();
    }
    
    @Mod.EventHandler
    public void serverStop(FMLServerStoppedEvent event) {
    	
    }
    
	public static void dbg(Object obj) {
		if (true) System.out.println(obj);
	}
}
