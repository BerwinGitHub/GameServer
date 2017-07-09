package com.berwin.controller;

import com.berwin.proto.Protobufs.Action;
import com.berwin.server.SocketUser;
import com.berwin.service.MatchService;

public class MatchController implements IController {

	public MatchController() {
	}

	@Override
	public void dispatch(SocketUser su, int action, byte[] data) {
		if (action == Action.ACTION_MATCH_VALUE) {
			MatchService.getInstance().matchEnemy(su);
		} else if (action == Action.ACTION_MATCH_CANCEL_VALUE) {
		} else if (action == Action.ACTION_MATCH_FAILED_VALUE) {
		} else if (action == Action.ACTION_MATCHED_VALUE) {
		}
	}

}
