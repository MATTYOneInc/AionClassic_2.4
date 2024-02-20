package com.aionemu.gameserver.model;

public enum DuelResult
{
	DUEL_YOU_WIN(1300098, (byte) 2),
	DUEL_YOU_LOSE(1300099, (byte) 0),
	DUEL_TIMEOUT(1300100, (byte) 1);
	
	private int msgId;
	private byte resultId;
	
	private DuelResult(int msgId, byte resultId) {
		this.msgId = msgId;
		this.resultId = resultId;
	}
	
	public int getMsgId() {
		return msgId;
	}
	
	public byte getResultId() {
		return resultId;
	}
}