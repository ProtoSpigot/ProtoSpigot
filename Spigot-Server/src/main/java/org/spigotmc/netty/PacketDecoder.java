package org.spigotmc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageList;
import io.netty.handler.codec.ReplayingDecoder;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Packet;

/**
 * Packet decoding class backed by a reusable {@link DataInputStream} which
 * backs the input {@link ByteBuf}. Reads an unsigned byte packet header and
 * then decodes the packet accordingly.
 */
public class PacketDecoder extends ReplayingDecoder<ReadState>
{

    private DataInputStream input;
    private Packet packet;

    public PacketDecoder()
    {
        super( ReadState.HEADER );
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, MessageList<Object> out) throws Exception
    {
        if ( input == null )
        {
            input = new DataInputStream( new ByteBufInputStream( in ) );
        }

        while ( true )
        {
            switch ( state() )
            {
                case HEADER:
                    short packetId = in.readUnsignedByte();
                    packet = Packet.a( MinecraftServer.getServer().getLogger(), packetId );
                    if ( packet == null )
                    {
                        throw new IOException( "Bad packet id " + packetId );
                    }
                    checkpoint( ReadState.DATA );
                case DATA:
                    try
                    {
                        packet.a( input );
                    } catch ( EOFException ex )
                    {
                        return;
                    }

                    checkpoint( ReadState.HEADER );
                    out.add( packet );
                    packet = null;
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }
}
