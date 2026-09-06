package net.mcreator.unpatched.procedures;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreateTxtOnFirstDtartupProcedure {
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		execute();
	}

	public static void execute() {
		execute(null);
	}

	private static void execute(@Nullable Event event) {
		String pcUsername = System.getProperty("user.name");
		java.io.File markerFile = new java.io.File(net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get().toFile(), "system_corrupted.dat");
		if (!markerFile.exists()) {
			try {
				if (markerFile.createNewFile()) {
					java.nio.file.Files.writeString(markerFile.toPath(), "INITIALIZATION_FAILED_LOG_0x83A");
				}
				// 1. Uses your fixed FileManager to leave a creepy note on the desktop
				net.mcreator.unpatched.FileManager.createFileOnDesktop("note.txt", "Here i am..." + pcUsername);
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}
		}
	}
}
