package particleman.forge;

import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class SoundLoader {
	@ForgeSubscribe
    public void onSound(SoundLoadEvent event) {
		registerSound(event.manager, ParticleMan.modID+":fire_grab.ogg");
		registerSound(event.manager, ParticleMan.modID+":fire_shoot.ogg");
		registerSound(event.manager, ParticleMan.modID+":redstone_grab.ogg");
		registerSound(event.manager, ParticleMan.modID+":redstone_shoot.ogg");
		registerSound(event.manager, ParticleMan.modID+":shockwave_echo_loud.ogg");
		
		//eg playing
		//par3World.playSoundEffect(par2EntityPlayer.posX, par2EntityPlayer.posY, par2EntityPlayer.posZ, ParticleMan.modID+":fire_grabb", 0.9F, par3World.rand.nextFloat());
		
		//TEMP!
		//registerSound(event.manager, ZombieCraftMod.modID+":zc.gun.deagle.ogg");
		
		
    }
    
    private void registerSound(SoundManager manager, String path) {
        try {
            manager.addSound(path);
        } catch (Exception ex) {
            System.out.println(String.format("Warning: unable to load sound file %s", path));
        }
    }
    
    private void registerStreaming(SoundManager manager, String path) {
        try {
            manager.soundPoolStreaming.addSound(path);
        } catch (Exception ex) {
            System.out.println(String.format("Warning: unable to load sound file %s"));
        }
    }

}
