package com.berwin.service;

import java.util.ArrayList;
import java.util.List;

import com.berwin.server.SocketUser;

public class MatchService {

	private static MatchService instance = null;

	private List<SocketUser> matchingUsers = null;

	private MatchService() {
		matchingUsers = new ArrayList<>();
	}

	public static synchronized MatchService getInstance() {
		if (instance == null) {
			instance = new MatchService();
		}
		return instance;
	}

	public synchronized SocketUser matchEnemy(SocketUser user) {
		SocketUser enemy = null;
		if (matchingUsers.size() > 0) {
			enemy = matchingUsers.get(0);
			matchingUsers.remove(0);
		} else {
			matchingUsers.add(user);
		}
		return enemy;
	}

}
