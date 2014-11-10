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
	private EchoHandler echoHandler;
	private final int BUFFER_SIZE = 30;
	ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);

	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	public void setEchoHandler(EchoHandler echoHandler) {
		this.echoHandler = echoHandler;
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
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		try {
			byteBuffer.clear();
			if (socketChannel.read(byteBuffer) != -1) {
				byteBuffer.flip();
				echoHandler.handleMessage(socketChannel, Charset
						.defaultCharset().decode(byteBuffer).toString());
			} else {   //End of Stream
				cancle(selectionKey);
			}
		} catch (IOException io) {
			cancle(selectionKey);
		}
	}

	private void cancle(SelectionKey selectionKey) throws IOException {
		selectionKey.cancel();
		selectionKey.channel().close();
	}
}
