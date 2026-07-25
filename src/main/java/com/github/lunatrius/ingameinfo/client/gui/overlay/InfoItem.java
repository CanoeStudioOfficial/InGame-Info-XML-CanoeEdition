package com.github.lunatrius.ingameinfo.client.gui.overlay;

import com.github.lunatrius.ingameinfo.handler.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class InfoItem extends Info {
    private static final int ITEM_CACHE_SIZE = 256;
    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    private static final Map<ItemKey, ItemStack> ITEM_CACHE = new LinkedHashMap<ItemKey, ItemStack>(ITEM_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<ItemKey, ItemStack> eldest) {
            return size() > ITEM_CACHE_SIZE;
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
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();

            GlStateManager.translate(getX(), getY(), 0);
            if (!this.large) {
                GlStateManager.scale(0.5f, 0.5f, 0.5f);
            }

            final RenderItem renderItem = MINECRAFT.getRenderItem();
            final float zLevel = renderItem.zLevel;
            renderItem.zLevel = 300;
            renderItem.renderItemAndEffectIntoGUI(this.itemStack, 0, 0);

            if (ConfigurationHandler.showOverlayItemIcons) {
                renderItem.renderItemOverlayIntoGUI(MINECRAFT.fontRenderer, this.itemStack, 0, 0, "");
            }

            renderItem.zLevel = zLevel;

            if (!this.large) {
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
            }
            GlStateManager.translate(-getX(), -getY(), 0);

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
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
        private final NBTTagCompound tagCompound;
        private final int hashCode;

        private ItemKey(final ItemStack itemStack) {
            this.item = itemStack.getItem();
            this.metadata = itemStack.getItemDamage();
            this.count = itemStack.getCount();
            this.tagCompound = itemStack.hasTagCompound() ? itemStack.getTagCompound().copy() : null;
            this.hashCode = Objects.hash(this.item, this.metadata, this.count, this.tagCompound);
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
            return this.item == other.item && this.metadata == other.metadata && this.count == other.count && Objects.equals(this.tagCompound, other.tagCompound);
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }
}
