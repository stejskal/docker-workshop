package workshop.mr;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    private HttpServer httpServer;

    public void start(String[] workerURLs) throws IOException {
        if (httpServer != null) {
            throw new IllegalStateException("Http server already started");
        }
        httpServer = HttpServer.create(new InetSocketAddress(4567), 1);
        httpServer.createContext("/factorial/", new MapReduceHandler(workerURLs));
        httpServer.start();
    }

}

