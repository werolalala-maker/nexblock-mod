package com.example.catalogmenu;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Menú de catálogo con diseño futurista. Es SOLO VISUAL: no tiene ninguna función.
 *
 * Todo se dibuja en "píxeles de arte": la textura menu.png mide 296x164 y se
 * amplía por un factor ENTERO para que el pixel-art se vea nítido a cualquier
 * resolución y tamaño de GUI.
 */
public class CatalogScreen extends Screen {

	private static final ResourceLocation MENU_TEX =
			ResourceLocation.fromNamespaceAndPath(CatalogMenuClient.MOD_ID, "textures/gui/menu.png");
	private static final ResourceLocation TAB_HOVER_TEX =
			ResourceLocation.fromNamespaceAndPath(CatalogMenuClient.MOD_ID, "textures/gui/tab_hover.png");

	// Tamaño de la textura en píxeles de arte
	private static final int ART_W = 296;
	private static final int ART_H = 164;

	// Botones de la izquierda (libro, espada, pergamino, lupa)
	private static final int TAB_X = 9;
	private static final int TAB_SIZE = 34;
	private static final int[] TAB_Y = {9, 46, 84, 122};

	// Cuadrícula de objetos
	private static final int GRID_X = 52;
	private static final int GRID_Y = 10;
	private static final int CELL_W = 16;
	private static final int CELL_H = 17;
	private static final int ITEM = 16;
	private static final int RIGHT_BLOCK_FIRST_COL = 7;

	/** Brillo suave al pasar el ratón (solo estético). Ponlo en false para quitarlo. */
	private static final boolean HOVER_EFFECTS = true;

	private final ItemStack[][] leftBlock;
	private final ItemStack[][] rightBlock;

	public CatalogScreen() {
		super(Component.translatable("screen.catalogmenu.title"));

		// Objetos de adorno (los del video eran de otro mod; aquí uso objetos de vanilla).
		this.leftBlock = stacks(new Item[][]{
				{Items.OBSERVER, Items.DISPENSER, Items.BOW, Items.DIAMOND_SWORD, Items.CROSSBOW, null},
				{Items.TRIDENT, Items.NETHERITE_SWORD, null, null, null, null},
				{Items.ELYTRA, Items.LODESTONE, Items.MINECART, Items.IRON_BLOCK, Items.SADDLE, Items.NAME_TAG},
				{Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_APPLE, null},
				{Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE, Items.ENDER_EYE, null, null},
				{Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.CHAINMAIL_CHESTPLATE, null},
				{Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.SHIELD, null},
				{Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS, Items.NETHERITE_HOE, Items.NETHERITE_AXE},
		});

		this.rightBlock = stacks(new Item[][]{
				{Items.GLOWSTONE_DUST, Items.RED_MUSHROOM, Items.FEATHER, Items.ENDER_PEARL, Items.BLAZE_POWDER, Items.SLIME_BALL, null},
				{Items.TUFF, Items.SANDSTONE, Items.REDSTONE_LAMP, Items.BONE_BLOCK, Items.NETHER_WART_BLOCK, Items.GRANITE, Items.NETHER_BRICK_SLAB},
				{Items.TERRACOTTA, Items.RABBIT_HIDE, Items.LEATHER, Items.BEEF, Items.BRAIN_CORAL, Items.NAUTILUS_SHELL, Items.HONEYCOMB},
				{Items.STICK, Items.DEAD_BUSH, Items.GOAT_HORN, Items.BONE, Items.CRIMSON_FUNGUS, Items.SWEET_BERRIES, Items.BEETROOT},
				{Items.GOLD_INGOT, Items.IRON_INGOT, Items.REDSTONE, Items.COAL, Items.GUNPOWDER, Items.DIAMOND, Items.RED_DYE},
				{Items.BLAZE_ROD, Items.SPIDER_EYE, Items.LAVA_BUCKET, Items.FIRE_CHARGE, Items.MAGMA_CREAM, Items.NETHERITE_SCRAP, null},
				{Items.AMETHYST_SHARD, Items.LAPIS_LAZULI, Items.GOLD_NUGGET, null, null, null, null},
		});
	}

	private static ItemStack[][] stacks(Item[][] items) {
		ItemStack[][] out = new ItemStack[items.length][];
		for (int r = 0; r < items.length; r++) {
			out[r] = new ItemStack[items[r].length];
			for (int c = 0; c < items[r].length; c++) {
				out[r][c] = items[r][c] == null ? ItemStack.EMPTY : new ItemStack(items[r][c]);
			}
		}
		return out;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	/** Oscurece el mundo detrás del menú (sin el desenfoque de vanilla). */
	@Override
	public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		g.fill(0, 0, this.width, this.height, 0xA0000000);
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(g, mouseX, mouseY, partialTick);

		Window window = this.minecraft.getWindow();
		double guiScale = window.getGuiScale();

		// Factor entero de ampliación: el menú ocupa casi toda la ventana.
		int k = Math.max(1, (int) Math.floor(Math.min(
				window.getWidth() * 0.95 / ART_W,
				window.getHeight() * 0.93 / ART_H)));
		int left = (window.getWidth() - ART_W * k) / 2;
		int top = (window.getHeight() - ART_H * k) / 2;

		// Posición del ratón en píxeles de arte
		double ax = (mouseX * guiScale - left) / k;
		double ay = (mouseY * guiScale - top) / k;

		PoseStack pose = g.pose();
		pose.pushPose();
		// A partir de aquí 1 unidad = 1 píxel real de la ventana
		float inv = (float) (1.0 / guiScale);
		pose.scale(inv, inv, 1.0f);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		// 1) Fondo completo del menú
		g.blit(MENU_TEX, left, top, ART_W * k, ART_H * k, 0.0f, 0.0f, ART_W, ART_H, ART_W, ART_H);

		// 2) Brillos al pasar el ratón (opcional)
		if (HOVER_EFFECTS) {
			for (int i = 0; i < TAB_Y.length; i++) {
				if (inside(ax, ay, TAB_X, TAB_Y[i], TAB_SIZE, TAB_SIZE)) {
					g.blit(TAB_HOVER_TEX, left + TAB_X * k, top + TAB_Y[i] * k,
							TAB_SIZE * k, TAB_SIZE * k, 0.0f, 0.0f, TAB_SIZE, TAB_SIZE, TAB_SIZE, TAB_SIZE);
				}
			}
			hoverBlock(g, leftBlock, 0, ax, ay, left, top, k);
			hoverBlock(g, rightBlock, RIGHT_BLOCK_FIRST_COL, ax, ay, left, top, k);
		}

		// 3) Objetos de adorno
		drawBlock(g, leftBlock, 0, left, top, k);
		drawBlock(g, rightBlock, RIGHT_BLOCK_FIRST_COL, left, top, k);

		pose.popPose();
	}

	private static boolean inside(double ax, double ay, int x, int y, int w, int h) {
		return ax >= x && ax < x + w && ay >= y && ay < y + h;
	}

	private void hoverBlock(GuiGraphics g, ItemStack[][] block, int firstCol,
							double ax, double ay, int left, int top, int k) {
		for (int r = 0; r < block.length; r++) {
			for (int c = 0; c < block[r].length; c++) {
				if (block[r][c].isEmpty()) continue;
				int x = GRID_X + (firstCol + c) * CELL_W;
				int y = GRID_Y + r * CELL_H;
				if (inside(ax, ay, x, y, ITEM, ITEM)) {
					int px = left + x * k;
					int py = top + y * k;
					g.fill(px, py, px + ITEM * k, py + ITEM * k, 0x40FFFFFF);
				}
			}
		}
	}

	private void drawBlock(GuiGraphics g, ItemStack[][] block, int firstCol, int left, int top, int k) {
		PoseStack pose = g.pose();
		for (int r = 0; r < block.length; r++) {
			for (int c = 0; c < block[r].length; c++) {
				ItemStack stack = block[r][c];
				if (stack.isEmpty()) continue;
				int x = GRID_X + (firstCol + c) * CELL_W;
				int y = GRID_Y + r * CELL_H;
				pose.pushPose();
				pose.translate((float) (left + x * k), (float) (top + y * k), 0.0f);
				pose.scale((float) k, (float) k, (float) k);
				g.renderItem(stack, 0, 0);
				pose.popPose();
			}
		}
	}

	/** H (o la tecla configurada) cierra el menú; ESC lo cierra por defecto. */
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (CatalogMenuClient.openMenuKey != null && CatalogMenuClient.openMenuKey.matches(keyCode, scanCode)) {
			this.onClose();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
