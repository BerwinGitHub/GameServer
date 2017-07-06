package com.berwin.server;

import java.net.ServerSocket;

public class Server {

	private int port;

	public Server(int port) {
		this.port = port;
	}

	public void startServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(this.port);
			System.out.println("Server start at " + this.port);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopServer() {
	}

}
