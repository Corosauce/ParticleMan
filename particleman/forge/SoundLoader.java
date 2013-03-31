package particleman.forge;

import java.io.FileNotFoundException;
import java.net.URL;

import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class SoundLoader {
	@ForgeSubscribe
    public void onSound(SoundLoadEvent event) {
		
		registerSound(event.manager, "fire_grab.ogg", "/resources/sound/particleman/fire_grab.ogg");
		registerSound(event.manager, "fire_shoot.ogg", "/resources/sound/particleman/fire_shoot.ogg");
		registerSound(event.manager, "redstone_grab.ogg", "/resources/sound/particleman/redstone_grab.ogg");
		registerSound(event.manager, "redstone_shoot.ogg", "/resources/sound/particleman/redstone_shoot.ogg");
		registerSound(event.manager, "shockwave_echo_loud.ogg", "/resources/sound/particleman/shockwave_echo_loud.ogg");
		
		
    }
    
    private void registerSound(SoundManager manager, String name, String path) {
        try {
            URL filePath = SoundLoader.class.getResource(path);
            if (filePath != null) {
                manager.soundPoolSounds.addSound(name, filePath);
            } else {
                throw new FileNotFoundException();
            }
        } catch (Exception ex) {
            System.out.println(String.format("Warning: unable to load sound file %s", path));
        }
    }
    
    private void registerStreaming(SoundManager manager, String name, String path) {
        try {
            URL filePath = SoundLoader.class.getResource(path);
            if (filePath != null) {
                manager.soundPoolStreaming.addSound(name, filePath);
            } else {
                throw new FileNotFoundException();
            }
        } catch (Exception ex) {
            System.out.println(String.format("Warning: unable to load sound file %s"));
        }
    }

}
