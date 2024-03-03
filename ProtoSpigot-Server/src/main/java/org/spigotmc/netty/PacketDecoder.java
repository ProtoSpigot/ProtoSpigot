package org.spigotmc.netty;

import com.github.protospigot.ProtoSpigot;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageList;
import io.netty.handler.codec.ReplayingDecoder;
import java.io.DataInputStream;
import java.io.EOFException;
import net.minecraft.server.Packet;

/**
 * Packet decoding class backed by a reusable {@link DataInputStream} which
 * backs the input {@link ByteBuf}. Reads an unsigned byte packet header and
 * then decodes the packet accordingly.
 */
public class PacketDecoder extends ReplayingDecoder<ReadState>
{

    private DataInputStream input;

    public PacketDecoder()
    {
        super(ReadState.DATA); // ProtoSpigot
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, MessageList<Object> out) throws Exception
    {
        if ( input == null )
        {
            input = new DataInputStream( new ByteBufInputStream( in ) );
        }

        // ProtoSpigot start - multiple protocol support
        while (true) {
            try {
                NettyNetworkManager manager = ctx.channel().pipeline().get(NettyNetworkManager.class);
                Packet packet = ProtoSpigot.readPacket(manager, this.input);
                if (packet != null)
                    out.add(packet);
            } catch ( EOFException ex ) {
                return;
            }
        }
        // ProtoSpigot end
    }
}
