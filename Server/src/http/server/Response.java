package http.server;

import java.io.IOException;
import java.io.PrintWriter;


public interface Response {

    PrintWriter getWriter() throws IOException;
    
}
