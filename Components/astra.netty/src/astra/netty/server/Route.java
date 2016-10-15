package astra.netty.server;

import io.netty.handler.codec.http.HttpMethod;

/**
 * The Route class represents a single entry in the RouteTable.
 */
public class Route {
    private final HttpMethod method;
    private final String path;
    private final Handler handler;

    public Route(final HttpMethod method, final String path, final Handler handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Handler getHandler() {
        return handler;
    }

    public boolean matches(final HttpMethod method, final String path) {
        return this.method.equals(method) && path.startsWith(this.path);
    }
}
