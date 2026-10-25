package com.example.catalogmenu;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * Registra la tecla H y abre el menú (solo visual) al pulsarla.
 */
public class CatalogMenuClient implements ClientModInitializer {

	public static final String MOD_ID = "catalogmenu";

	/** Tecla para abrir el menú (por defecto H, se puede cambiar en Controles). */
	public static KeyMapping openMenuKey;

	@Override
	public void onInitializeClient() {
		openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.catalogmenu.open",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_H,
				"category.catalogmenu"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openMenuKey.consumeClick()) {
				if (client.player != null && client.screen == null) {
					client.setScreen(new CatalogScreen());
				}
			}
		});
	}
}
