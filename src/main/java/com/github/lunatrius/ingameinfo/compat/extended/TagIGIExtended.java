package com.github.lunatrius.ingameinfo.compat.extended;

import com.github.lunatrius.ingameinfo.compat.extended.network.IGIExtendedRemoteDataMessage;
import com.github.lunatrius.ingameinfo.network.PacketHandler;
import com.github.lunatrius.ingameinfo.reference.Reference;
import com.github.lunatrius.ingameinfo.tag.Tag;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

import java.util.Locale;

public abstract class TagIGIExtended extends Tag {
    public static NBTTagCompound cachedData = new NBTTagCompound();
    protected static long lastRemoteUpdate = 0;

    @Override
    public String getCategory() {
        return "IGIExtended";
    }

    public static class TPS extends TagIGIExtended {
        @Override
        public String getValue() {
            try {
                if (world != null && world.isRemote) {
                    requestRemoteUpdate();
                    return format(cachedData.getDouble("meanTPS"));
                }

                final MinecraftServer minecraftServer = getCurrentServer();
                if (minecraftServer != null) {
                    final double meanTickTime = IGIExtendedRemoteDataMessage.mean(minecraftServer.tickTimeArray) * 1.0E-6D;
                    final double meanTPS = Math.min(1000.0D / meanTickTime, 20.0D);
                    return format(meanTPS);
                }
            } catch (final Throwable e) {
                log(this, e);
            }

            return "-1";
        }
    }

    public static class MSPT extends TagIGIExtended {
        @Override
        public String getValue() {
            try {
                if (world != null && world.isRemote) {
                    requestRemoteUpdate();
                    return format(cachedData.getDouble("meanTickTime"));
                }

                final MinecraftServer minecraftServer = getCurrentServer();
                if (minecraftServer != null) {
                    final double meanTickTime = IGIExtendedRemoteDataMessage.mean(minecraftServer.tickTimeArray) * 1.0E-6D;
                    return format(meanTickTime);
                }
            } catch (final Throwable e) {
                log(this, e);
            }

            return "-1";
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new TPS().setName("tps"));
        TagRegistry.INSTANCE.register(new MSPT().setName("mspt"));
    }

    private static void requestRemoteUpdate() {
        final long delay = System.currentTimeMillis() - lastRemoteUpdate;
        if (delay > 1500 || delay < 0) {
            PacketHandler.INSTANCE.sendToServer(new IGIExtendedRemoteDataMessage());
            lastRemoteUpdate = System.currentTimeMillis();
        }
    }

    private static MinecraftServer getCurrentServer() {
        if (server != null) {
            return server;
        }

        return world != null ? world.getMinecraftServer() : null;
    }

    private static String format(final double value) {
        return String.format(Locale.ENGLISH, "%.2f", value);
    }

    private static void log(final Tag tag, final Throwable ex) {
        Reference.logger.warn(Reference.MODID + ":" + tag.getName(), ex);
    }
}
