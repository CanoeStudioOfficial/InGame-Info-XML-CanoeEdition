package com.github.lunatrius.ingameinfo.compat.extended.network;

import com.github.lunatrius.ingameinfo.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class IGIExtendedRemoteDataMessage implements IMessage {
    public IGIExtendedRemoteDataMessage() {
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
    }

    @Override
    public void toBytes(final ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<IGIExtendedRemoteDataMessage, IGIExtendedResponseMessage> {
        public Handler() {
        }

        @Override
        public IGIExtendedResponseMessage onMessage(final IGIExtendedRemoteDataMessage message, final MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            final IThreadListener mainThread = (WorldServer) player.world;
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    final NBTTagCompound data = new NBTTagCompound();
                    try {
                        final double meanTickTime = mean(player.server.tickTimeArray) * 1.0E-6D;
                        final double meanTPS = Math.min(1000.0D / meanTickTime, 20.0D);

                        data.setDouble("meanTickTime", meanTickTime);
                        data.setDouble("meanTPS", meanTPS);
                        PacketHandler.INSTANCE.sendTo(new IGIExtendedResponseMessage(data), player);
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
    }

    public static long mean(final long[] values) {
        long sum = 0L;
        for (final long value : values) {
            sum += value;
        }

        return values.length > 0 ? sum / values.length : 0L;
    }
}
