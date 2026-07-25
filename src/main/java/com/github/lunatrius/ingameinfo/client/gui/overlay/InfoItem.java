package com.github.lunatrius.ingameinfo.client.gui.overlay;

import com.github.lunatrius.ingameinfo.core.client.gui.GuiHelper;
import com.github.lunatrius.ingameinfo.handler.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class InfoItem extends Info {
    private static final int ITEM_CACHE_SIZE = 256;
    private static final int TEXTURE_CACHE_SIZE = 256;
    private static final int TEXTURE_SIZE = 16;
    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    private static final Map<ItemKey, ItemStack> ITEM_CACHE = new LinkedHashMap<ItemKey, ItemStack>(ITEM_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<ItemKey, ItemStack> eldest) {
            return size() > ITEM_CACHE_SIZE;
        }
    };
    private static final Map<ItemKey, CachedItemTexture> TEXTURE_CACHE = new LinkedHashMap<ItemKey, CachedItemTexture>(TEXTURE_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<ItemKey, CachedItemTexture> eldest) {
            if (size() > TEXTURE_CACHE_SIZE) {
                eldest.getValue().delete();
                return true;
            }

            return false;
        }
    };

    private final ItemStack itemStack;
    private final boolean large;
    private final int size;

    public static InfoItem get(final ItemStack itemStack) {
        return get(itemStack, false);
    }

    public static InfoItem get(final ItemStack itemStack, final boolean large) {
        return get(itemStack, large, 0, 0);
    }

    public static InfoItem get(final ItemStack itemStack, final boolean large, final int x, final int y) {
        return new InfoItem(getCachedItemStack(itemStack), large, x, y);
    }

    private static ItemStack getCachedItemStack(final ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        final ItemKey itemKey = new ItemKey(itemStack);
        ItemStack cachedItemStack = ITEM_CACHE.get(itemKey);
        if (cachedItemStack == null) {
            cachedItemStack = itemStack.copy();
            ITEM_CACHE.put(itemKey, cachedItemStack);
        }

        return cachedItemStack;
    }

    public InfoItem(final ItemStack itemStack) {
        this(itemStack, false);
    }

    public InfoItem(final ItemStack itemStack, final boolean large) {
        this(itemStack, large, 0, 0);
    }

    public InfoItem(final ItemStack itemStack, final boolean large, final int x, final int y) {
        super(x, y);
        this.itemStack = itemStack;
        this.large = large;
        this.size = large ? 16 : 8;
        if (large) {
            this.y = -4;
        }
    }

    @Override
    public void drawInfo() {
        if (!this.itemStack.isEmpty()) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

            GlStateManager.translate(getX(), getY(), 0);
            drawCachedTexture(getCachedTexture(this.itemStack));
            GlStateManager.translate(-getX(), -getY(), 0);

            GlStateManager.disableBlend();
        }
    }

    private static CachedItemTexture getCachedTexture(final ItemStack itemStack) {
        final boolean renderOverlay = ConfigurationHandler.showOverlayItemIcons;
        final ItemKey itemKey = new ItemKey(itemStack, renderOverlay);
        CachedItemTexture cachedItemTexture = TEXTURE_CACHE.get(itemKey);
        if (cachedItemTexture == null) {
            cachedItemTexture = new CachedItemTexture(itemStack.copy(), renderOverlay);
            TEXTURE_CACHE.put(itemKey, cachedItemTexture);
        }

        return cachedItemTexture;
    }

    private void drawCachedTexture(final CachedItemTexture cachedItemTexture) {
        cachedItemTexture.bindTexture();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        GuiHelper.drawTexturedRectangle(buffer, 0, 0, this.size, this.size, 300, 0, 1, 1, 0);
        tessellator.draw();
    }

    @Override
    public int getWidth() {
        return !this.itemStack.isEmpty() ? this.size : 0;
    }

    @Override
    public int getHeight() {
        return !this.itemStack.isEmpty() ? this.size : 0;
    }

    @Override
    public String toString() {
        return String.format("InfoItem{itemStack: %s, x: %d, y: %d, offsetX: %d, offsetY: %d, children: %s}", this.itemStack, this.x, this.y, this.offsetX, this.offsetY, this.children);
    }

    private static class ItemKey {
        private final Item item;
        private final int metadata;
        private final int count;
        private final boolean renderOverlay;
        private final NBTTagCompound tagCompound;
        private final int hashCode;

        private ItemKey(final ItemStack itemStack) {
            this(itemStack, false);
        }

        private ItemKey(final ItemStack itemStack, final boolean renderOverlay) {
            this.item = itemStack.getItem();
            this.metadata = itemStack.getItemDamage();
            this.count = itemStack.getCount();
            this.renderOverlay = renderOverlay;
            this.tagCompound = itemStack.hasTagCompound() ? itemStack.getTagCompound().copy() : null;
            this.hashCode = Objects.hash(this.item, this.metadata, this.count, this.renderOverlay, this.tagCompound);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof ItemKey)) {
                return false;
            }

            final ItemKey other = (ItemKey) obj;
            return this.item == other.item && this.metadata == other.metadata && this.count == other.count && this.renderOverlay == other.renderOverlay && Objects.equals(this.tagCompound, other.tagCompound);
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }

    private static class CachedItemTexture {
        private final Framebuffer framebuffer;

        private CachedItemTexture(final ItemStack itemStack, final boolean renderOverlay) {
            this.framebuffer = new Framebuffer(TEXTURE_SIZE, TEXTURE_SIZE, true);
            this.framebuffer.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);
            render(itemStack, renderOverlay);
        }

        private void render(final ItemStack itemStack, final boolean renderOverlay) {
            final Framebuffer minecraftFramebuffer = MINECRAFT.getFramebuffer();

            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.pushMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.pushMatrix();

            this.framebuffer.framebufferClear();
            this.framebuffer.bindFramebuffer(true);

            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, TEXTURE_SIZE, TEXTURE_SIZE, 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0f, 0.0f, -2000.0f);

            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();

            final RenderItem renderItem = MINECRAFT.getRenderItem();
            final float zLevel = renderItem.zLevel;
            renderItem.zLevel = 300;
            renderItem.renderItemAndEffectIntoGUI(itemStack, 0, 0);

            if (renderOverlay) {
                renderItem.renderItemOverlayIntoGUI(MINECRAFT.fontRenderer, itemStack, 0, 0, "");
            }

            renderItem.zLevel = zLevel;

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();

            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);

            minecraftFramebuffer.bindFramebuffer(true);
            GL11.glPopAttrib();
        }

        private void bindTexture() {
            this.framebuffer.bindFramebufferTexture();
        }

        private void delete() {
            this.framebuffer.deleteFramebuffer();
        }
    }
}
