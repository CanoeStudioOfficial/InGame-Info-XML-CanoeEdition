package com.github.lunatrius.ingameinfo.network;

import com.github.lunatrius.ingameinfo.compat.bloodmagic.bmnetwork.BMRemoteDataMessage;
import com.github.lunatrius.ingameinfo.compat.bloodmagic.bmnetwork.BMResponseMessage;
import com.github.lunatrius.ingameinfo.compat.extended.network.IGIExtendedRemoteDataMessage;
import com.github.lunatrius.ingameinfo.compat.extended.network.IGIExtendedResponseMessage;
import com.github.lunatrius.ingameinfo.compat.thaumcraft.tcnetwork.TCRemoteDataMessage;
import com.github.lunatrius.ingameinfo.compat.thaumcraft.tcnetwork.TCResponseMessage;
import com.github.lunatrius.ingameinfo.network.message.MessageSeed;
import com.github.lunatrius.ingameinfo.reference.Reference;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);

    public static void init() {
        INSTANCE.registerMessage(MessageSeed.class, MessageSeed.class, 0, Side.CLIENT);

        if (Loader.isModLoaded("thaumcraft")) {
            INSTANCE.registerMessage(TCResponseMessage.ResponseHandler.class, TCResponseMessage.class, 1, Side.CLIENT);
            INSTANCE.registerMessage(TCRemoteDataMessage.Handler.class, TCRemoteDataMessage.class, 2, Side.SERVER);
        }

        if (Loader.isModLoaded("bloodmagic")) {
            INSTANCE.registerMessage(BMResponseMessage.ResponseHandler.class, BMResponseMessage.class, 3, Side.CLIENT);
            INSTANCE.registerMessage(BMRemoteDataMessage.Handler.class, BMRemoteDataMessage.class, 4, Side.SERVER);
        }

        INSTANCE.registerMessage(IGIExtendedResponseMessage.ResponseHandler.class, IGIExtendedResponseMessage.class, 5, Side.CLIENT);
        INSTANCE.registerMessage(IGIExtendedRemoteDataMessage.Handler.class, IGIExtendedRemoteDataMessage.class, 6, Side.SERVER);
    }
}
