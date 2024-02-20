package com.aionemu.gameserver.model;

public enum EmotionId
{
	NONE(0),
	LAUGH(1),
	ANGRY(2),
	SAD(3),
	POINT(5),
	YES(6),
	NO(7),
	VICTORY(8),
	CLAP(11),
	SIGH(12),
	SURPRISE(13),
	COMFORT(14),
	THANK(15),
	BEG(16),
	BLUSH(17),
	SMILE(28),
	SALUTE(29),
	PANIC(30),
	SORRY(31),
	THINK(33),
	DISLIKE(34),
	STAND(128);
	
	private int id;
	
	private EmotionId(int id) {
		this.id = id;
	}
	
	public int id() {
		return id;
	}
}