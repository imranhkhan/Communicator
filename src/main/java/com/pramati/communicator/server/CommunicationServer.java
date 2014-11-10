package com.pramati.communicator.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class CommunicationServer {

	private Selector selector;
	private ServerSocketChannel serverSocketChannel;

	public CommunicationServer(Selector selector) {
		this.selector = selector;
	}

	public void listen(String hostname, int port) throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(hostname, port));
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		startListening();
	}

	private void startListening() {
		ConnectionListener conListener = new ConnectionListener();
		EchoHandler echoHandler = new EchoHandler();
		conListener.setSelector(selector);
		conListener.setEchoHandler(echoHandler);

		Thread echoHandlerThread = new Thread(echoHandler);
		Thread conListenrThread = new Thread(conListener);
		echoHandlerThread.start();
		conListenrThread.start();
	}

}
