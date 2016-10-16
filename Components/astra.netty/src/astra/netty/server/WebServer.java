package astra.netty.server;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

/**
 * The WebServer class is a convenience wrapper around the Netty HTTP server.
 */
public class WebServer {
	public static final String TYPE_PLAIN = "text/plain; charset=UTF-8";
	public static final String TYPE_HTML = "text/html; charset=UTF-8";
	public static final String TYPE_JSON = "application/json; charset=UTF-8";
	public static final String SERVER_NAME = "Netty";

	// Routing Table
	private final Map<String, Handler> routes;
	private final int port;
	private final EventLoopGroup masterGroup;
	private final EventLoopGroup slaveGroup;

	/**
	 * Creates a new WebServer.
	 */
	public WebServer() {
		this(9000);
	}

	public WebServer(int port) {
		this.routes = new HashMap<String, Handler>();
		this.port = port;
		masterGroup = new NioEventLoopGroup();
		slaveGroup = new NioEventLoopGroup();
	}

	public WebServer createContext(final String path, final Handler handler) {
		this.routes.put(path, handler);
		return this;
	}

	public Handler getHandler(String uri) {
		for (Entry<String, Handler> entry : routes.entrySet()) {
			if (uri.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * Starts the web server.
	 *
	 * @throws Exception
	 */
	public void start() throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shutdown();
			}
		});

		final ServerBootstrap b = new ServerBootstrap();
		b.group(masterGroup, slaveGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new WebServerInitializer())
				.option(ChannelOption.SO_BACKLOG, 1024)
				.option(ChannelOption.SO_REUSEADDR, true)
				.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
				.childOption(ChannelOption.SO_REUSEADDR, true);

		b.bind(new InetSocketAddress(port)).sync().channel().closeFuture().sync();
	}

	public void shutdown() {
		slaveGroup.shutdownGracefully();
		masterGroup.shutdownGracefully();
	}

	/**
	 * The Initializer class initializes the HTTP channel.
	 */
	private class WebServerInitializer extends ChannelInitializer<SocketChannel> {

		/**
		 * Initializes the channel pipeline with the HTTP response handlers.
		 *
		 * @param ch
		 *            The Channel which was registered.
		 */
		@Override
		public void initChannel(SocketChannel ch) throws Exception {
			final ChannelPipeline p = ch.pipeline();
			p.addLast("codec", new HttpServerCodec());
//			p.addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192, false));
			p.addLast("aggregator", new HttpObjectAggregator(100 * 1024 * 1024));
//			p.addLast("encoder", new HttpResponseEncoder());
			p.addLast("handler", new WebServerHandler());
		}
	}

	private class WebServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
			if (HttpUtil.is100ContinueExpected(request)) send100Continue(ctx);

			final Handler handler = WebServer.this.getHandler(request.uri());
			if (handler == null) {
				writeNotFound(ctx, request);
			} else {
				try {
					handler.handle(ctx, request);
				} catch (final Throwable ex) {
					ex.printStackTrace();
					writeInternalServerError(ctx, request);
				}
			}
		}

		@Override
		public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
			ctx.close();
		}

		@Override
		public void channelReadComplete(final ChannelHandlerContext ctx) {
			ctx.flush();
		}
	}

	private static void writeNotFound(final ChannelHandlerContext ctx, final FullHttpRequest request) {
		writeErrorResponse(ctx, request, HttpResponseStatus.NOT_FOUND);
	}

	private static void writeInternalServerError(final ChannelHandlerContext ctx, final FullHttpRequest request) {
		writeErrorResponse(ctx, request, HttpResponseStatus.INTERNAL_SERVER_ERROR);
	}

	public static void writeErrorResponse(final ChannelHandlerContext ctx, final FullHttpRequest request,
			final HttpResponseStatus status) {
		writeResponse(ctx, request, status, TYPE_PLAIN, status.reasonPhrase().toString());
	}

	public static void writeFileResponse(final ChannelHandlerContext ctx, final FullHttpRequest request, String file_uri) throws Exception {
		File file = new File(file_uri);
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		HttpUtil.setContentLength(response, raf.length());
		
		ctx.write(response); 
		ctx.write(new DefaultFileRegion(raf.getChannel(), 0, file.length()));
		ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(new FutureListener<Object>() {
			@Override
			public void operationComplete(Future<Object> arg0) throws Exception {
				raf.close();
			}
		});
		
		
	}
	
	public static void writeResponse(final ChannelHandlerContext ctx, final FullHttpRequest request,
			final HttpResponseStatus status, final CharSequence contentType, final String content) {

		final byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
		final ByteBuf entity = Unpooled.wrappedBuffer(bytes);
		writeResponse(ctx, request, status, entity, contentType, bytes.length);
	}

	private static void writeResponse(final ChannelHandlerContext ctx, final FullHttpRequest request,
			final HttpResponseStatus status, final ByteBuf buf, final CharSequence contentType,
			final int contentLength) {

		// Decide whether to close the connection or not.
		final boolean keepAlive = HttpUtil.isKeepAlive(request);

		// Build the response object.
		final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf, false);

		final ZonedDateTime dateTime = ZonedDateTime.now();
		final DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

		final DefaultHttpHeaders headers = (DefaultHttpHeaders) response.headers();
		headers.set(HttpHeaderNames.SERVER, SERVER_NAME);
		headers.set(HttpHeaderNames.DATE, dateTime.format(formatter));
		headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
		headers.set(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(contentLength));

		// Close the non-keep-alive connection after the write operation is
		// done.
		if (!keepAlive) {
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		} else {
			ctx.writeAndFlush(response, ctx.voidPromise());
		}
	}

	private static void send100Continue(final ChannelHandlerContext ctx) {
		ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
	}
}