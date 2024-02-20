package com.aionemu.gameserver.model.instance.playerreward;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceBuff;

public class PvPArenaPlayerReward extends InstancePlayerReward
{
	private int position;
	private int timeBonus;
	private float timeBonusModifier;
	private int basicAP;
	private int rankingAP;
	private int scoreAP;
	private int basicCrucible;
	private int rankingCrucible;
	private int scoreCrucible;
	private int basicCourage;
	private int rankingCourage;
	private int scoreCourage;
	private long logoutTime;
	private boolean isRewarded = false;
	private InstanceBuff boostMorale;
	
	public PvPArenaPlayerReward(Integer object, int timeBonus, byte buffId) {
		super(object);
		super.addPoints(13000);
		this.timeBonus = timeBonus;
		timeBonusModifier = ((float) this.timeBonus / (float) 660000);
		boostMorale = new InstanceBuff(buffId);
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public int getTimeBonus() {
		return timeBonus > 0 ? timeBonus : 0;
	}
	
	public void updateLogOutTime() {
		logoutTime = System.currentTimeMillis();
	}
	
	public void updateBonusTime() {
		int offlineTime = (int) (System.currentTimeMillis() - logoutTime);
		timeBonus -= offlineTime * timeBonusModifier;
	}
	
	public boolean isRewarded() {
		return isRewarded;
	}
	
	public void setRewarded() {
		isRewarded = true;
	}
	
	public int getBasicAP() {
		return basicAP;
	}
	
	public int getRankingAP() {
		return rankingAP;
	}
	
	public int getScoreAP() {
		return scoreAP;
	}
	
	public void setBasicAP(int ap) {
		this.basicAP = ap;
	}
	
	public void setRankingAP(int ap) {
		this.rankingAP = ap;
	}
	
	public void setScoreAP(int ap) {
		this.scoreAP = ap;
	}
	
	public float getParticipation() {
		return (float) getTimeBonus() / timeBonus;
	}
	
	public int getBasicCrucible() {
		return basicCrucible;
	}
	
	public int getRankingCrucible() {
		return rankingCrucible;
	}
	
	public int getScoreCrucible() {
		return scoreCrucible;
	}
	
	public void setBasicCrucible(int basicCrucible) {
		this.basicCrucible = basicCrucible;
	}
	
	public void setRankingCrucible(int rankingCrucible) {
		this.rankingCrucible = rankingCrucible;
	}
	
	public void setScoreCrucible(int scoreCrucible) {
		this.scoreCrucible = scoreCrucible;
	}
	
	public void setBasicCourage(int basicCourage) {
		this.basicCourage = basicCourage;
	}
	
	public void setRankingCourage(int rankingCourage) {
		this.rankingCourage = rankingCourage;
	}
	
	public void setScoreCourage(int scoreCourage) {
		this.scoreCourage = scoreCourage;
	}
	
	public int getBasicCourage() {
		return basicCourage;
	}
	
	public int getRankingCourage() {
		return rankingCourage;
	}
	
	public int getScoreCourage() {
		return scoreCourage;
	}
	
	public int getScorePoints() {
		return timeBonus + getPoints();
	}
	
	public boolean hasBoostMorale() {
		return boostMorale.hasInstanceBuff();
	}
	
	public void applyBoostMoraleEffect(Player player) {
		boostMorale.applyEffect(player, 20000);
	}
	
	public void endBoostMoraleEffect(Player player) {
		boostMorale.endEffect(player);
	}
	
	public int getRemaningTime() {
		int time = boostMorale.getRemaningTime();
		if (time >= 0 && time < 20) {
			return 20 - time;
		}
		return 0;
	}
}