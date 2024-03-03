package com.github.protospigot.protocol.protocol_1_5_2.clientbound;

import com.github.protospigot.handler.PacketWriter;
import net.minecraft.server.Packet1Login;
import net.minecraft.server.WorldType;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.protospigot.protocol.protocol_1_5_2.Protocol_1_5_2.writeString;

public final class LoginPacketWriter implements PacketWriter<Packet1Login> {
    @Override
    public void write(Packet1Login packet, DataOutputStream stream) throws IOException {
        stream.writeInt(packet.getEntityId());

        WorldType worldType = packet.getWorldType();
        writeString(worldType == null ? "" : worldType.name(), stream);

        int gameModeId = packet.getGameMode().getId();
        if (packet.isHardcore())
            gameModeId |= 8;
        stream.writeByte(gameModeId);

        stream.writeByte(packet.getDimension());
        stream.writeByte(packet.getDifficulty());
        stream.writeByte(packet.getWorldHeight());
        stream.writeByte(packet.getMaxPlayers());
    }
}
