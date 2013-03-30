package particleman.forge;

import java.util.logging.Level;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
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
    
    public static void spinAround(Entity source, Entity center, float angleRate, float radius, float distOffset, int index, float speed) {
		
		float angle = (-center.rotationYaw + 65F/* + ((float)Math.sin(worldObj.getWorldTime() * 0.1F) * 3F)*/) * 0.01745329F;
		float angle2 = (-center.rotationYaw + 65F + ((index*60) + source.worldObj.getWorldTime() % 360)) * 0.01745329F;
		
		float dist = distOffset;
		
		//temp
		//radius = 1.0F;
		//angleRate = 10F;
		float angleRateY = 5F;
		
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
        
        //float speed = 0.02F;
    	
    	double vecX = x - source.posX;
    	double vecY = y - source.posY;
    	double vecZ = z - source.posZ;
        
        double var9 = (double)MathHelper.sqrt_double(vecX * vecX + vecY * vecY + vecZ * vecZ);
        source.motionX += vecX / var9 * speed;
        source.motionY += vecY / var9 * speed;
        source.motionZ += vecZ / var9 * speed;
        
        /*source.motionX = 0F;
        source.motionY = 0F;
        source.motionZ = 0F;*/
		
		//source.setPosition(source.posX, source.posY, source.posZ);
		
		
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
