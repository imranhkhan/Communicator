package com.pramati.communicator.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class ConnectionListener implements Runnable {

	private Selector selector;
	private EchoThread echoThread;
	private final int BUFFER_SIZE = 30;

	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	public void setEchoThread(EchoThread echoThread) {
		this.echoThread = echoThread;
	}

	public void run() {
		while (true) {
			try {
				if (selector.select() == 0) {
					continue;
				}
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				handleKeys(selectionKeys);
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
	}

	private void handleKeys(Set<SelectionKey> selectionKeys) throws IOException {
		Iterator<SelectionKey> iterator = selectionKeys.iterator();
		while (iterator.hasNext()) {
			SelectionKey selectionKey = iterator.next();
			if (selectionKey.isAcceptable()) {
				acceptConnection(selectionKey);
			} else if (selectionKey.isReadable()) {
				read(selectionKey);
			}
			iterator.remove();
		}
	}

	private void acceptConnection(SelectionKey selectionKey) throws IOException {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey
				.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();
		if (socketChannel != null) {
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_READ);
		}
	}

	private void read(SelectionKey selectionKey) throws IOException {
		int count = 0;
		String message = "";
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		if ((count = socketChannel.read(byteBuffer)) > 0) {
			byteBuffer.flip();
			message += Charset.defaultCharset().decode(byteBuffer).toString();
		}
		echoThread.getSocketmap().put(socketChannel, message);
		closeAtEOF(socketChannel, count);
		byteBuffer.clear();
	}

	private void closeAtEOF(SocketChannel socketChannel, int count)
			throws IOException {
		if (count < 0) {
			socketChannel.close();
		}
	}
}
