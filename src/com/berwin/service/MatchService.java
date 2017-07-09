package com.berwin.service;

import java.util.ArrayList;
import java.util.List;

import com.berwin.domain.User;
import com.berwin.game.Arena;
import com.berwin.server.SocketUser;

public class MatchService {

	public interface ResultListener {
		public void matchedEnemy(User u);
	}

	private static MatchService instance = null;

	private List<SocketUser> matchingQueue = null;

	private MatchService() {
		matchingQueue = new ArrayList<>();
	}

	public static synchronized MatchService getInstance() {
		if (instance == null) {
			instance = new MatchService();
		}
		return instance;
	}

	public synchronized void matchEnemy(SocketUser user) {
		if (matchingQueue.size() > 0) { // 队列里面有人，那么就直接让他们两干起来
			SocketUser enemy = matchingQueue.get(0);
			matchingQueue.remove(0);
			// 创建一个角斗场
			new Arena(enemy, user);
		} else { // 目前没有任何一个敌人，添加到队列等待
			matchingQueue.add(user);
		}
	}

}
