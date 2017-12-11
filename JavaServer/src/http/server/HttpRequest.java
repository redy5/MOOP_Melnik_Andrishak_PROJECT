package http.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpRequest implements Request {

	private final String request;
	private final String uri;
	private final Map<String, String> parameterMap;
	private final String method;

	public HttpRequest(InputStream input) throws IOException {
		this.request = convertInputStreamToString(input);
		this.uri = parseUri(request);
		this.parameterMap = parseParameterMap(request);
		method = parseMethod(request);
	}

	private String convertInputStreamToString(InputStream in) {
		StringBuffer request = new StringBuffer(2048);
		int i;
		byte[] buffer = new byte[2048];
		try {
			i = in.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			i = -1;
		}
		for (int j = 0; j < i; j++) {
			request.append((char) buffer[j]);
		}
		return request.toString();
	}

	private String parseMethod(String requestString) {
		if (requestString.isEmpty()) {
			return "";
		}
		int index1 = requestString.indexOf(' ');
		return requestString.substring(0, index1);
	}

	private String parseUri(String requestString) {

		if (requestString.isEmpty()) {
			return "";
		}

		int index1 = requestString.indexOf(' ');
		if (index1 != -1) {
			int index2 = requestString.indexOf(' ', index1 + 1);
			if (index2 > index1) {
				return requestString.substring(index1 + 1, index2);
			}
		}

		return "";
	}

	private Map<String, String> parseParameterMap(String request) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(request));

		String requestLine = reader.readLine();
		if (requestLine == null) {
			System.out.println(":(");
			return Collections.<String, String> emptyMap();
		}

		String method = requestLine.split(" ")[0];
		System.out.println(requestLine);

		if ("GET".equals(method)) {
			String paramsLine = "";
			try {
				paramsLine = uri.substring(2,uri.length());
			} catch (Exception e) {
				return Collections.<String, String> emptyMap();
			}
			return parseParamLine(paramsLine);
		}

		if ("POST".equals(method)) {
			String header = reader.readLine();
			while (header != null && header.length() > 0) {
				header = reader.readLine();
			}
			String bodyLine = reader.readLine();
			StringBuilder paramsLine = new StringBuilder();
			while (bodyLine != null) {
				paramsLine.append(bodyLine);
				bodyLine = reader.readLine();
			}
			return parseParamLine(paramsLine.toString());
		}
		
		return Collections.<String, String> emptyMap();
	}

	private Map<String, String> parseParamLine(String paramsLine) throws UnsupportedEncodingException {
		String[] paramsArray = paramsLine.split("&");
		Map<String, String> res = new HashMap<>();
		String[] paramKVArr = null;
		for (String paramKV : paramsArray) {
			paramKVArr = paramKV.split("=");
			if (paramKVArr.length > 1) {
				System.out.println(paramKVArr[0] + "=" + paramKVArr[1]);
				res.put(URLDecoder.decode(paramKVArr[0], "UTF-8").toLowerCase(), URLDecoder.decode(paramKVArr[1], "UTF-8"));
			}
		}
		return res;
	}

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public String getParameter(String name) {
		return parameterMap.get(name);
	}

	@Override
	public Set<String> getParameterNames() {
		return parameterMap.keySet();
	}

	@Override
	public Collection<String> getParameterValues() {
		return parameterMap.values();
	}

	@Override
	public String getRequestAsText() {
		return request;
	}

	@Override
	public String getMethod() {
		return method;
	}

}