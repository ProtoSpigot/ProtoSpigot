package com.github.protospigot.protocol.protocol_1_5_2;

import com.github.protospigot.protocol.Protocol;
import com.github.protospigot.protocol.ProtocolType;
import com.github.protospigot.protocol.protocol_1_5_2.clientbound.ChatPacketWriter;
import com.github.protospigot.protocol.protocol_1_5_2.clientbound.EntityEquipmentPacketWriter;
import com.github.protospigot.protocol.protocol_1_5_2.clientbound.KeepAlivePacketWriter;
import com.github.protospigot.protocol.protocol_1_5_2.clientbound.KickPacketWriter;
import com.github.protospigot.protocol.protocol_1_5_2.clientbound.LoginPacketWriter;
import com.github.protospigot.protocol.protocol_1_5_2.clientbound.UpdateTimePacketWriter;
import net.minecraft.server.Packet0KeepAlive;
import net.minecraft.server.Packet1Login;
import net.minecraft.server.Packet255KickDisconnect;
import net.minecraft.server.Packet3Chat;
import net.minecraft.server.Packet4UpdateTime;
import net.minecraft.server.Packet5EntityEquipment;

import java.io.DataOutputStream;
import java.io.IOException;

public final class Protocol_1_5_2 extends Protocol {

    private static final int MAX_STRING_LENGTH = 32767;

    public Protocol_1_5_2() {
        super(61, ProtocolType.LEGACY);

        registerServerPacket(0, Packet0KeepAlive.class, new KeepAlivePacketWriter());
        registerServerPacket(1, Packet1Login.class, new LoginPacketWriter());
        registerServerPacket(3, Packet3Chat.class, new ChatPacketWriter());
        registerServerPacket(4, Packet4UpdateTime.class, new UpdateTimePacketWriter());
        registerServerPacket(5, Packet5EntityEquipment.class, new EntityEquipmentPacketWriter());
        registerServerPacket(255, Packet255KickDisconnect.class, new KickPacketWriter());
    }

    /**
     * Writes a string to a stream.
     *
     * @param string the string
     * @param stream the stream
     * @throws IOException when the string is bigger than allowed
     */
    public static void writeString(String string, DataOutputStream stream) throws IOException {
        if (string.length() > MAX_STRING_LENGTH)
            throw new IOException("String is bigger than allowed!");
        stream.writeShort(string.length());
        stream.writeChars(string);
    }
}