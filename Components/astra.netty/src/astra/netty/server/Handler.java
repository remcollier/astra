package astra.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface Handler {

    void handle(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception;

}
