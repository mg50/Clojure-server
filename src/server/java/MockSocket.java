
package server.java;

import java.io.*;

public class MockSocket {
	public InputStream inputStream;
	public OutputStream outputStream = new ByteArrayOutputStream();

	public MockSocket(String initialString) {
		inputStream = new ByteArrayInputStream(initialString.getBytes());
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void close() {

	}

}
