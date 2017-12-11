package front;

import http.server.HttpServer;
import http.server.servlet.AbstractServletsMap;
import java.io.IOException;

public class RunServer {
     public static void main(String[] args) throws IOException {
        AbstractServletsMap servletsMap = new ServletsMap();
        HttpServer server = new HttpServer(servletsMap);
        server.await();
    }
}
