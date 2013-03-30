package particleman.forge;

import java.util.logging.Level;

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
