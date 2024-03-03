package org.spigotmc.netty;

import com.github.protospigot.ProtoSpigot;
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
        super( ReadState.DATA ); // ProtoSpigot
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
                    /*short packetId = in.readUnsignedByte();
                    // ProtoSpigot start - multiple protocol support
                    NettyNetworkManager manager = ctx.channel().pipeline().get(NettyNetworkManager.class);
                    packetReader = ProtoSpigot.getPacketReader(packetId, manager.getProtocolVersion());
                    if ( packetReader == null ) {
                        throw new IOException("Bad packet id " + packetId);
                    }
                    // ProtoSpigot end
                    checkpoint( ReadState.DATA );*/
                case DATA:
                    try
                    {
                        // ProtoSpigot start - multiple protocol support
                        NettyNetworkManager manager = ctx.channel().pipeline().get(NettyNetworkManager.class);
                        Packet packet = ProtoSpigot.readPacket(this.input, manager.getProtocolVersion(), manager.isModern());
                        if (packet != null)
                            out.add(packet);
                        // ProtoSpigot end
                    } catch ( EOFException ex )
                    {
                        return;
                    }

                    //checkpoint( ReadState.HEADER );
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }
}
