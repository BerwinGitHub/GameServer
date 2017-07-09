package com.berwin.domain;

public class User {
	private int id;

	private String name;

	private String passWord;

	private int score;

	public User() {
	}

	public User(int id, String name, String passWord, int score) {
		super();
		this.id = id;
		this.name = name;
		this.passWord = passWord;
		this.score = score;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

}
