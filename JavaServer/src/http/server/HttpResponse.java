package http.server;

import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class HttpResponse implements Response {

    private final OutputStream output;    

    public HttpResponse(OutputStream output) {
        this.output = output;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        PrintWriter writer = new PrintWriter(output, true);
        return writer;
    }

}
