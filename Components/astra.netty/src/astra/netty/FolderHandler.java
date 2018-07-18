package astra.netty;

import java.io.File;

import astra.netty.server.Handler;
import astra.netty.server.WebServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

public class FolderHandler implements Handler {
	private String base_url;
	private String path;

	public FolderHandler(String base_url, String path) {
		this.base_url= base_url;
		this.path = path;
	}

	@Override
	public void handle(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		if (request.method().equals(HttpMethod.GET)) {
			String file_uri = request.uri().substring(base_url.length()).replace('/', File.separatorChar);
			file_uri = path+(path.endsWith(File.separator) ? "":File.separator) + file_uri;
			WebServer.writeFileResponse(ctx, request, file_uri);
		} else {
			WebServer.writeErrorResponse(ctx, request, HttpResponseStatus.FORBIDDEN);
		}
	}

}
