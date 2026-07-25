package com.github.lunatrius.ingameinfo.compat.extended.network;

import com.github.lunatrius.ingameinfo.compat.extended.TagIGIExtended;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class IGIExtendedResponseMessage implements IMessage {
    public NBTTagCompound data;

    public IGIExtendedResponseMessage() {
    }

    public IGIExtendedResponseMessage(final NBTTagCompound data) {
        this.data = data.copy();
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        this.data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.data);
    }

    public static class ResponseHandler implements IMessageHandler<IGIExtendedResponseMessage, IMessage> {
        public ResponseHandler() {
        }

        @Override
        public IMessage onMessage(final IGIExtendedResponseMessage message, final MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    if (message.data != null && message.data.hasKey("meanTickTime")) {
                        TagIGIExtended.cachedData = message.data.copy();
                    }
                }
            });
            return null;
        }
    }
}
