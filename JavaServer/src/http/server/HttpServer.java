package http.server;

import http.server.processors.Processor;
import http.server.servlet.AbstractServletsMap;
import http.server.processors.ServletProcessor;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.net.ServerSocket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class HttpServer {
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
	private static final boolean SHUTDOWN = true;
	private static final int NTHREADS = 100;
	private static final Executor EXEC = Executors.newFixedThreadPool(NTHREADS);

	private final AbstractServletsMap servletsMap;
	private int port;

	public HttpServer(AbstractServletsMap servletsMap, int port) {
		this.servletsMap = servletsMap;
		this.port = port;
	}

	public HttpServer(AbstractServletsMap servletsMap) {
		this(servletsMap, 80);
	}

	public void await() throws IOException {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(80);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Server is waiting for request at port: " + port);
		servletsMap.callInit();
		final CBool isShutDown = new CBool(!SHUTDOWN);
		while (!isShutDown.get()) {
			try {
				final Socket socket = serverSocket.accept();
				Runnable task = new Runnable() {
					public void run() {
						try {
							System.out.println("Thread "+Thread.currentThread().getName());
							if (processRequest(socket)) {
								isShutDown.set(true);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
				EXEC.execute(task);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		servletsMap.callDestroy();
		serverSocket.close();
	}

	private class CBool {
		private boolean val;

		public CBool(boolean v) {
			val = v;
		}

		public boolean get() {
			return val;
		}

		public boolean set(boolean v) {
			return val = v;
		}
	}

	private boolean processRequest(Socket socket) throws IOException {
		InputStream input = socket.getInputStream();
		OutputStream output = socket.getOutputStream();

		Request request = new HttpRequest(input);
		System.out.println(request.getRequestAsText());

		Response response = new HttpResponse(output);

		String uri = request.getURI();
		System.out.println("THIS IS URI - " + uri);
		if (isNull(uri)) {
			return !SHUTDOWN;
		}

		if (isShutDown(uri)) {
			return SHUTDOWN;
		}

		Processor processor = selectProcessor(uri);
		processor.process(request, response);

		socket.close();

		return !SHUTDOWN;
	}

	private boolean isNull(String uri) {
		return uri == null;
	}

	private boolean isShutDown(String uri) {
		return uri.equals(SHUTDOWN_COMMAND);
	}

	private Processor selectProcessor(String uri) {
		Processor processor;
		processor = new ServletProcessor(servletsMap);
		return processor;
	}
}
