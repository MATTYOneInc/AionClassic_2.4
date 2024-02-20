package com.aionemu.gameserver.model.team.legion;

import com.aionemu.gameserver.model.gameobjects.player.Player;

import java.sql.Timestamp;

public class LegionJoinRequest {
    private int legionId = 0;
    private int playerId = 0;
    private String playerName = "";
    private int playerClass = 0;
    private int race = 0;
    private int level = 0;
    private int genderId = 0;
    private String msg = "";
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public LegionJoinRequest(){

    }

    public LegionJoinRequest(int legionId, Player player, String msg) {
        this.legionId = legionId;
        this.playerId = player.getObjectId();
        this.playerName = player.getName();
        this.playerClass = player.getPlayerClass().ordinal();
        this.race = player.getRace().getRaceId();
        this.level = player.getLevel();
        this.genderId = player.getGender().getGenderId();
        this.msg = msg;
    }

    public int getLegionId() {
        return legionId;
    }

    public void setLegionId(int legionId) {
        this.legionId = legionId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(int playerClass) {
        this.playerClass = playerClass;
    }

    public int getRace() {
        return race;
    }

    public void setRace(int race) {
        this.race = race;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getGenderId() {
        return genderId;
    }

    public void setGenderId(int genderId) {
        this.genderId = genderId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Timestamp getDate() {
        return timestamp;
    }

    public void setDate(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
