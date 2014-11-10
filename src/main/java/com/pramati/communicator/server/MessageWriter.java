package com.pramati.communicator.server;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class MessageWriter implements Runnable {

	private final SocketChannel socketChannel;
	private final String message;

	protected MessageWriter(SocketChannel socketChannel, String message) {
		this.socketChannel = socketChannel;
		this.message = message;
	}

	public void run() {
		try {
			CharBuffer charBuffer = CharBuffer.wrap(message);
			while (charBuffer.hasRemaining()) {
				socketChannel
						.write(Charset.defaultCharset().encode(charBuffer));
			}
			charBuffer.clear();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

}
