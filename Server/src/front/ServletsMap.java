package front;

import http.server.servlet.AbstractServletsMap;

public class ServletsMap extends AbstractServletsMap {

    public ServletsMap() {
        servlets.put("/", new MainServlet());
    }

}