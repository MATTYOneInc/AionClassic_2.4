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
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300430000)
public class Discipline_Training_Ground extends Arena_System
{
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		killBonus += 200;
		deathFine = -100;
		super.onInstanceCreate(instance);
	}
	
	@Override
	public void onGather(Player player, Gatherable gatherable) {
		if (!instanceReward.isStartProgress()) {
			return;
		}
		getPlayerReward(player.getObjectId()).addPoints(1250);
		sendPacket();
		int nameId = gatherable.getObjectTemplate().getNameId();
		DescriptionId name = new DescriptionId(nameId * 2 + 1);
		PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400237, name, 1250));
	}
	
	protected void reward() {
		int totalPoints = instanceReward.getTotalPoints();
		int size = instanceReward.getInstanceRewards().size();
		float totalAP = (1.0f * size) * 100;
		float totalCrucible = (0.01f * size) * 100;
		float totalCourage = (0.01f * size) * 100;
		for (InstancePlayerReward playerReward : instanceReward.getInstanceRewards()) {
			PvPArenaPlayerReward reward = (PvPArenaPlayerReward) playerReward;
			if (!reward.isRewarded()) {
				int score = reward.getScorePoints();
				float scoreRate = ((float) score / (float) totalPoints);
				int rank = instanceReward.getRank(score);
				float percent = reward.getParticipation();
				int basicAP = 100;
				int rankingAP = 500;
				if (size > 1) {
					rankingAP = rank == 0 ? 1000 : 500;
				}
				int scoreAP = (int) (totalAP * scoreRate);
				basicAP *= percent;
				rankingAP *= percent;
				reward.setBasicAP(basicAP);
				reward.setRankingAP(rankingAP);
				reward.setScoreAP(scoreAP);
				int basicCrI = 0;
				basicCrI *= percent;
				int rankingCrI = 150;
				if (size > 1) {
					rankingCrI = rank == 0 ? 500 : 150;
				}
				rankingCrI *= percent;
				int scoreCrI = (int) (totalCrucible * scoreRate);
				reward.setBasicCrucible(basicCrI);
				reward.setRankingCrucible(rankingCrI);
				reward.setScoreCrucible(scoreCrI);
				int basicCoI = 0;
				basicCoI *= percent;
				int rankingCoI = 25;
				if (size > 1) {
					rankingCoI = rank == 0 ? 50 : 25;
				}
				rankingCoI *= percent;
				int scoreCoI = (int) (totalCourage * scoreRate);
				reward.setBasicCourage(basicCoI);
				reward.setRankingCourage(rankingCoI);
				reward.setScoreCourage(scoreCoI);
			}
		}
		super.reward();
	}
}