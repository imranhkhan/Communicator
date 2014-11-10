package com.pramati.communicator.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class CommunicationClient {

	private SocketChannel socketChannel;
	private final int BUFFER_SIZE = 30;

	public boolean connect(String host, int port) throws IOException {
		boolean connected = false;
		socketChannel = SocketChannel.open();
		if (!socketChannel.isConnected()) {
			connected = socketChannel
					.connect(new InetSocketAddress(host, port));
		}
		return connected;
	}

	public void write(String message) throws IOException {
		CharBuffer charBuffer = CharBuffer.wrap(message);
		while (charBuffer.hasRemaining()) {
			socketChannel.write(Charset.defaultCharset().encode(charBuffer));
		}
		charBuffer.clear();
	}

	public String read() throws IOException {
		String message = "";

		ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		if (socketChannel.read(byteBuffer) > 0) {
			byteBuffer.flip();
			message += Charset.defaultCharset().decode(byteBuffer).toString();
		}
		close();
		return message;
	}

	public void close() throws IOException {
		socketChannel.close();
	}
}
