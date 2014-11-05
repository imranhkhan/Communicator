package com.pramati.communicator.server;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EchoThread implements Runnable {

	private final Map<SocketChannel, String> socketMap = 
			new ConcurrentHashMap<SocketChannel, String>();

	public Map<SocketChannel, String> getSocketmap() {
		return socketMap;
	}

	public void run() {
		while (true) {
			if (socketMap.size() <= 0) {
				continue;
			}
			Set<Map.Entry<SocketChannel, String>> socketMapSet = socketMap
					.entrySet();
			for (Map.Entry<SocketChannel, String> entry : socketMapSet) {
				echo(entry.getKey(), entry.getValue());
				socketMap.remove(entry.getKey());
			}
		}
	}

	private void echo(SocketChannel socketChannel, String message) {
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
