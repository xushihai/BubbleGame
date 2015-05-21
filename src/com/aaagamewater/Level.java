package com.aaagamewater;

public class Level {
	public Level(int level) {
		super();
		this.level = level;
	}
	public Level() {
		super();
		this.level = 1;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	private int id;
	private int level=1;
}
