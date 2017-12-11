package http.server;

import java.util.Collection;
import java.util.Set;

public interface Request {

    String getParameter(String name);

    Set<String> getParameterNames();

    Collection<String> getParameterValues();
    
    String getRequestAsText();

    String getURI();

	String getMethod();
}
