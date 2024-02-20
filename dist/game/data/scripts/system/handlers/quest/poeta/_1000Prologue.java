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
package quest.poeta;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.network.aion.serverpackets.S_PLAY_CUTSCENE;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _1000Prologue extends QuestHandler
{
	private final static int questId = 1000;
	
	public _1000Prologue() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnMovieEndQuest(1, questId);
		qe.registerOnEnterZone(ZoneName.get("AKARIOS_PLAINS_210010000"), questId);
	}
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (zoneName == ZoneName.get("AKARIOS_PLAINS_210010000")) {
			if (qs == null) {
				env.setQuestId(questId);
				if (QuestService.startQuest(env)) {
					return true;
				}
			} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			    PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(1, 1));
			    return true;
		    }
		}
		return false;
	}
	
	@Override
    public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (movieId == 1) {
			qs.setStatus(QuestStatus.REWARD);
			QuestService.finishQuest(env);
            return true;
        }
        return false;
    }
}