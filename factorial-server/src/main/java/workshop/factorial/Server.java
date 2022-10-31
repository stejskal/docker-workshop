package workshop.factorial;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    private HttpServer httpServer;

    public void start(int port) throws IOException {
        if (httpServer != null) {
            throw new IllegalStateException("Http server already started");
        }
        httpServer = HttpServer.create(new InetSocketAddress(port), 1);
        httpServer.createContext("/factorial/", new FactorialHandler());
        httpServer.start();
    }

}

