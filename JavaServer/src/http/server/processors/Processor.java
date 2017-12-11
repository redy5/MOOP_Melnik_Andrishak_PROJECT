package http.server.processors;

import http.server.Request;
import http.server.Response;


public interface Processor {

    void process(Request request, Response response);
    
}
