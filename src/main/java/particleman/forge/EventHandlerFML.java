package particleman.forge;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class EventHandlerFML {

	public static World lastWorld = null;
	
	@SubscribeEvent
	public void tickWorld(WorldTickEvent event) {
		if (event.phase == Phase.START) {
			
		}
	}
	
	@SubscribeEvent
	public void tickServer(ServerTickEvent event) {
		
		if (event.phase == Phase.START) {
			
		}
		
	}
	
	@SubscribeEvent
	public void tickClient(ClientTickEvent event) {
		if (event.phase == Phase.START) {
			PMKeybindHandler.tickClient();
		}
	}
	
	@SubscribeEvent
	public void tickRenderScreen(RenderTickEvent event) {
		if (event.phase == Phase.END) {
			ClientTickHandler.onRenderTick();
		}
	}
}
