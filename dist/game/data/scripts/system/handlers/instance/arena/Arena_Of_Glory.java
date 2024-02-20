/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package instance.arena;

import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300470000)
public class Arena_Of_Glory extends Arena_System
{
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		killBonus += 1000;
		deathFine = -200;
		super.onInstanceCreate(instance);
	}
	
	@Override
	protected void reward() {
		int totalPoints = instanceReward.getTotalPoints();
		int size = instanceReward.getInstanceRewards().size();
		float totalScoreAP = (1.0f * size) * 100;
		float rankingRate = 0;
		if (size > 1) {
			rankingRate = (0.077f * (4 - size));
		}
		float totalRankingAP = 30800 - 30800 * rankingRate;
		for (InstancePlayerReward playerReward : instanceReward.getInstanceRewards()) {
			PvPArenaPlayerReward reward = (PvPArenaPlayerReward) playerReward;
			if (!reward.isRewarded()) {
				int score = reward.getScorePoints();
				float scoreRate = ((float) score / (float) totalPoints);
				int rank = instanceReward.getRank(score);
				float percent = reward.getParticipation();
				float generalRate = 0.167f + rank * 0.227f;
				int basicAP = 100;
				float rankingAP = totalRankingAP;
				if (rank > 0) {
					rankingAP = rankingAP - rankingAP * generalRate;
				}
				int scoreAP = (int) (totalScoreAP * scoreRate);
				basicAP *= percent;
				rankingAP *= percent;
				reward.setBasicAP(basicAP);
				reward.setRankingAP((int) rankingAP);
				reward.setScoreAP(scoreAP);
				switch (rank) {
					case 0:
						reward.setScoreCourage(4);
					break;
					case 1:
						reward.setScoreCourage(3);
					break;
					case 2:
						reward.setScoreCourage(2);
					break;
					case 3:
						reward.setScoreCourage(1);
					break;
				}
			}
		}
		super.reward();
	}
}