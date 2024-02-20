package com.aionemu.gameserver.questEngine.handlers.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NpcInfos")
public class NpcInfos
{
	@XmlAttribute(name = "npc_id", required = true)
	protected int npcId;
	
	@XmlAttribute(name = "var", required = true)
	protected int var;
	
	@XmlAttribute(name = "quest_dialog", required = true)
	protected int questDialog;
	
	@XmlAttribute(name = "close_dialog")
	protected int closeDialog;
	
	@XmlAttribute(name = "movie")
	protected int movie;
	
	public int getNpcId() {
		return npcId;
	}
	
	public int getVar() {
		return var;
	}
	
	public int getQuestDialog() {
		return questDialog;
	}
	
	public int getCloseDialog() {
		return closeDialog;
	}
	
	public int getMovie() {
		return movie;
	}
	
	public void setMovie(int movie) {
		this.movie = movie;
	}
}