package com.berwin.controller;

import com.berwin.proto.Protobufs.Action;
import com.berwin.server.SocketUser;

public class MessageDispatcher {

	public MessageDispatcher() {
	}

	public void dispatch(SocketUser su, int action, byte[] data) {
		IController controller = null;
		if (action == Action.ACTION_LOGIN_VALUE) {
			controller = new LoginController();
		} else if (action == Action.ACTION_MATCH_VALUE
				|| action == Action.ACTION_MATCH_CANCEL_VALUE
				|| action == Action.ACTION_MATCH_FAILED_VALUE
				|| action == Action.ACTION_MATCHED_VALUE) {
			controller = new MatchController();
		} else if (action == Action.ACTION_GAMING_VALUE
				|| action == Action.ACTION_GAMEND_VALUE) {
			controller = new GameController();
		}
		if (controller != null)
			controller.dispatch(su, action, data);
	}
}
