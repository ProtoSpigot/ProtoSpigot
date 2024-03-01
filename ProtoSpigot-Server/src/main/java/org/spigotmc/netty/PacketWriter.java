package org.spigotmc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.MessageList;
import io.netty.handler.codec.EncoderException;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.server.Packet;
import net.minecraft.server.PendingConnection;

/**
 * Netty encoder which takes a packet and encodes it, and adds a byte packet id
 * header.
 */
public class PacketWriter
{

    private static final int FLUSH_TIME = 1;
    /*========================================================================*/
    long lastFlush;
    private final MessageList<Packet> pending = MessageList.newInstance();

    void release()
    {
        pending.recycle();
    }

    void write(Channel channel, NettyNetworkManager networkManager, Packet msg)
    {
        // Append messages to queue
        pending.add( msg );

        // If we are not in the pending connect phase, and we have not reached our timer
        if ( !( networkManager.connection instanceof PendingConnection ) && System.currentTimeMillis() - lastFlush < FLUSH_TIME )
        {
            return;
        }
        // Update our last write time
        lastFlush = System.currentTimeMillis();

        // Since we are writing in batches it can be useful to guess the size of our output to limit memcpy
        int estimatedSize = 0;
        for ( Packet packet : pending )
        {
            estimatedSize += packet.a();
        }
        // Allocate an output buffer of estimated size
        ByteBuf outBuf = channel.alloc().buffer( estimatedSize );
        // And a stream to which we can write this buffer to
        DataOutputStream dataOut = new DataOutputStream( new ByteBufOutputStream( outBuf ) );

        try
        {
            // Iterate through all packets, this is safe as we know we will only ever get packets in the pipeline
            for ( Packet packet : pending )
            {
                // Write packet ID
                outBuf.writeByte( packet.n() );
                // Write packet data
                try
                {
                    packet.a( dataOut );
                } catch ( IOException ex )
                {
                    throw new EncoderException( ex );
                }
            }
            // Add to the courtesy API providing number of written bytes
            networkManager.addWrittenBytes( outBuf.readableBytes() );
            // Write down our single ByteBuf
            channel.write( outBuf );
        } finally
        {
            // Reset packet queue
            pending.clear();
            // Since we are now in the event loop, the bytes have been written, we can free them if this was not the case
            if ( outBuf.refCnt() != 0 )
            {
                outBuf.release();
            }
        }
    }
}
