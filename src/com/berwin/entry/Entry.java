package com.berwin.entry;

import com.berwin.server.Server;

public class Entry {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// 开启Socket端口监听
		new Server(8868).startServer();
	}

}
