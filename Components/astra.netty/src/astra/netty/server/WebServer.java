package astra.netty.server;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

/**
 * The WebServer class is a convenience wrapper around the Netty HTTP server.
 */
public class WebServer {
    public static final String TYPE_PLAIN = "text/plain; charset=UTF-8";
    public static final String TYPE_HTML = "text/html; charset=UTF-8";
    public static final String TYPE_JSON = "application/json; charset=UTF-8";
    public static final String SERVER_NAME = "Netty";
    private final RouteTable routeTable;
    private final int port;


    /**
     * Creates a new WebServer.
     */
    public WebServer() {
        this(9000);
    }


    public WebServer(int port) {
        this.routeTable = new RouteTable();
        this.port = port;
	}


	/**
     * Adds a GET route.
     *
     * @param path The URL path.
     * @param handler The request handler.
     * @return This WebServer.
     */
    public WebServer get(final String path, final Handler handler) {
        this.routeTable.addRoute(new Route(HttpMethod.GET, path, handler));
        return this;
    }


    /**
     * Adds a POST route.
     *
     * @param path The URL path.
     * @param handler The request handler.
     * @return This WebServer.
     */
    public WebServer post(final String path, final Handler handler) {
        this.routeTable.addRoute(new Route(HttpMethod.POST, path, handler));
        return this;
    }

    public WebServer createContext(final String path, final Handler handler) {
        this.routeTable.addRoute(new Route(HttpMethod.GET, path, handler));
        this.routeTable.addRoute(new Route(HttpMethod.POST, path, handler));
        this.routeTable.addRoute(new Route(HttpMethod.PUT, path, handler));
        this.routeTable.addRoute(new Route(HttpMethod.DELETE, path, handler));
        return this;
    }

    /**
     * Starts the web server.
     *
     * @throws Exception
     */
    public void start() throws Exception {
        if (Epoll.isAvailable()) {
            start(new EpollEventLoopGroup(), EpollServerSocketChannel.class);
        } else {
            start(new NioEventLoopGroup(), NioServerSocketChannel.class);
        }
    }


    /**
     * Initializes the server, socket, and channel.
     *
     * @param loopGroup The event loop group.
     * @param serverChannelClass The socket channel class.
     * @throws InterruptedException on interruption.
     */
    private void start(
            final EventLoopGroup loopGroup,
            final Class<? extends ServerChannel> serverChannelClass)
                    throws InterruptedException {

        try {
            final InetSocketAddress inet = new InetSocketAddress(port);

            final ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.option(ChannelOption.SO_REUSEADDR, true);
            b.group(loopGroup).channel(serverChannelClass).childHandler(new WebServerInitializer());
            b.option(ChannelOption.MAX_MESSAGES_PER_READ, Integer.MAX_VALUE);
            b.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true));
            b.childOption(ChannelOption.SO_REUSEADDR, true);
            b.childOption(ChannelOption.MAX_MESSAGES_PER_READ, Integer.MAX_VALUE);

            final Channel ch = b.bind(inet).sync().channel();
            ch.closeFuture().sync();

        } finally {
            loopGroup.shutdownGracefully().sync();
        }
    }


    /**
     * The Initializer class initializes the HTTP channel.
     */
    private class WebServerInitializer extends ChannelInitializer<SocketChannel> {

        /**
         * Initializes the channel pipeline with the HTTP response handlers.
         *
         * @param ch The Channel which was registered.
         */
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            final ChannelPipeline p = ch.pipeline();
            p.addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192, false));
            p.addLast("aggregator", new HttpObjectAggregator(100 * 1024 * 1024));
            p.addLast("encoder", new HttpResponseEncoder());
            p.addLast("handler", new WebServerHandler());
        }
    }


    /**
     * The Handler class handles all inbound channel messages.
     */
    private class WebServerHandler extends SimpleChannelInboundHandler<Object> {

        /**
         * Handles a new message.
         *
         * @param ctx The channel context.
         * @param msg The HTTP request message.
         */
        @Override
        public void messageReceived(final ChannelHandlerContext ctx, final Object msg) {
            if (!(msg instanceof FullHttpRequest)) {
                return;
            }

            final FullHttpRequest request = (FullHttpRequest) msg;

            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            final HttpMethod method = request.method();
            final String uri = request.uri();

            final Route route = WebServer.this.routeTable.findRoute(method, uri);
            if (route == null) {
                writeNotFound(ctx, request);
                return;
            }

            try {
                route.getHandler().handle(ctx, request);
            } catch (final Exception ex) {
                ex.printStackTrace();
                writeInternalServerError(ctx, request);
            }
        }


        /**
         * Handles an exception caught.  Closes the context.
         *
         * @param ctx The channel context.
         * @param cause The exception.
         */
        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
            ctx.close();
        }


        /**
         * Handles read complete event.  Flushes the context.
         *
         * @param ctx The channel context.
         */
        @Override
        public void channelReadComplete(final ChannelHandlerContext ctx) {
            ctx.flush();
        }
    }


    /**
     * Writes a 404 Not Found response.
     *
     * @param ctx The channel context.
     * @param request The HTTP request.
     */
    private static void writeNotFound(
            final ChannelHandlerContext ctx,
            final FullHttpRequest request) {

        writeErrorResponse(ctx, request, HttpResponseStatus.NOT_FOUND);
    }


    /**
     * Writes a 500 Internal Server Error response.
     *
     * @param ctx The channel context.
     * @param request The HTTP request.
     */
    private static void writeInternalServerError(
            final ChannelHandlerContext ctx,
            final FullHttpRequest request) {

        writeErrorResponse(ctx, request, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * Writes a HTTP error response.
     *
     * @param ctx The channel context.
     * @param request The HTTP request.
     * @param status The error status.
     */
    private static void writeErrorResponse(
            final ChannelHandlerContext ctx,
            final FullHttpRequest request,
            final HttpResponseStatus status) {

        writeResponse(ctx, request, status, TYPE_PLAIN, status.reasonPhrase().toString());
    }


    /**
     * Writes a HTTP response.
     *
     * @param ctx The channel context.
     * @param request The HTTP request.
     * @param status The HTTP status code.
     * @param contentType The response content type.
     * @param content The response content.
     */
    public static void writeResponse(
            final ChannelHandlerContext ctx,
            final FullHttpRequest request,
            final HttpResponseStatus status,
            final CharSequence contentType,
            final String content) {

        final byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        final ByteBuf entity = Unpooled.wrappedBuffer(bytes);
        writeResponse(ctx, request, status, entity, contentType, bytes.length);
    }


    /**
     * Writes a HTTP response.
     *
     * @param ctx The channel context.
     * @param request The HTTP request.
     * @param status The HTTP status code.
     * @param buf The response content buffer.
     * @param contentType The response content type.
     * @param contentLength The response content length;
     */
    private static void writeResponse(
            final ChannelHandlerContext ctx,
            final FullHttpRequest request,
            final HttpResponseStatus status,
            final ByteBuf buf,
            final CharSequence contentType,
            final int contentLength) {

        // Decide whether to close the connection or not.
        final boolean keepAlive = HttpUtil.isKeepAlive(request);

        // Build the response object.
        final FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                buf,
                false);

        final ZonedDateTime dateTime = ZonedDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

        final DefaultHttpHeaders headers = (DefaultHttpHeaders) response.headers();
        headers.set(HttpHeaderNames.SERVER, SERVER_NAME);
        headers.set(HttpHeaderNames.DATE, dateTime.format(formatter));
        headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(contentLength));

        // Close the non-keep-alive connection after the write operation is done.
        if (!keepAlive) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }


    /**
     * Writes a 100 Continue response.
     *
     * @param ctx The HTTP handler context.
     */
    private static void send100Continue(final ChannelHandlerContext ctx) {
        ctx.write(new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.CONTINUE));
    }
}