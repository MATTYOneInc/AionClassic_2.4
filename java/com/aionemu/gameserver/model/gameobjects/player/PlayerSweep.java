package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.model.gameobjects.PersistentState;
import javolution.util.FastList;

import java.util.List;

public class PlayerSweep {

    private int step;
    private int freeDice;
    private int boardId;
    private int lastDiceReward;
    private int roll;
    private List<Integer> rewardCell = new FastList<Integer>();

    public PlayerSweep(int step, int freeDice, int boardId, List<Integer> rewardCell) {
        this.step = step;
        this.freeDice = freeDice;
        this.boardId = boardId;
        this.rewardCell = rewardCell;
    }

    public PlayerSweep() {
    }

    public int getBoardId() {
        return boardId;
    }

    public int getFreeDice() {
        return freeDice;
    }

    public int getLastDiceReward() {
        return lastDiceReward;
    }

    public int getStep() {
        return step;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public void setFreeDice(int freeDice) {
        this.freeDice = freeDice;
    }

    public void setLastDiceReward(int lastDiceReward) {
        this.lastDiceReward = lastDiceReward;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public List<Integer> getRewardCell() {
        return rewardCell;
    }

    public void setRewardCell(List<Integer> rewardCell) {
        this.rewardCell = rewardCell;
    }

    public boolean isCellRewarded(int cellId){
        return this.rewardCell.contains(cellId);
    }
}
