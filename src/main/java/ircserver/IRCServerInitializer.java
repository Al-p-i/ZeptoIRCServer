package ircserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class IRCServerInitializer extends ChannelInitializer<SocketChannel> {

    @NotNull
    private static final StringDecoder DECODER = new StringDecoder();
    @NotNull
    private static final StringEncoder ENCODER = new StringEncoder();

    @NotNull
    private static final IRCServerConnectionHandler SERVER_HANDLER = new IRCServerConnectionHandler();

    @NotNull
    private final SslContext sslCtx;

    public IRCServerInitializer(@Nullable SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(@NotNull SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        // Add the text line codec combination first,
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // the encoder and decoder are static as these are sharable
        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);

        // and then business logic.
        pipeline.addLast(SERVER_HANDLER);
    }
}