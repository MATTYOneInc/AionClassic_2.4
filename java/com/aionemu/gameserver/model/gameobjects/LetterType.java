package com.aionemu.gameserver.model.gameobjects;

public enum LetterType {
	NORMAL(0),
	EXPRESS(1),
	BLACKCLOUD(2);

	private int id;

	private LetterType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static LetterType getLetterTypeById(int id) {
		for (LetterType lt : values()) {
			if (lt.id == id)
				return lt;
		}
		throw new IllegalArgumentException("Unsupported revive type: " + id);
	}
}
