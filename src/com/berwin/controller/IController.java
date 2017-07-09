package com.berwin.controller;

import com.berwin.server.SocketUser;

public interface IController {

	public void dispatch(SocketUser su, int action, byte[] data);

}
