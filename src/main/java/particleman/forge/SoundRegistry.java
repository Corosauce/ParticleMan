package particleman.forge;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashMap;

public class SoundRegistry {

	private static HashMap<String, SoundEvent> lookupStringToEvent = new HashMap<String, SoundEvent>();

	public static void init() {
		register("fire_grab");
		register("fire_shoot");
		register("redstone_grab");
		register("redstone_shoot");
		register("shockwave_echo_loud");
		
	}

	public static void register(String soundPath) {
		ResourceLocation resLoc = new ResourceLocation(ParticleMan.modID, soundPath);
		SoundEvent event = new SoundEvent(resLoc).setRegistryName(resLoc);
		ForgeRegistries.SOUND_EVENTS.register(event);
		if (lookupStringToEvent.containsKey(soundPath)) {
			System.out.println("PARTICLEMAN SOUNDS WARNING: duplicate sound registration for " + soundPath);
		}
		lookupStringToEvent.put(soundPath, event);
	}

	public static SoundEvent get(String soundPath) {
		return lookupStringToEvent.get(soundPath);
	}

}
