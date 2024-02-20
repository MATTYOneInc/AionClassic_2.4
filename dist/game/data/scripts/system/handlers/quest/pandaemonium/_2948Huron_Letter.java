/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modif (y
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.pandaemonium;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _2948Huron_Letter extends QuestHandler
{
	private final static int questId = 2948;
	
	public _2948Huron_Letter() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(204274).addOnTalkEvent(questId);
	}
	
	@Override
    public boolean onLvlUpEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        if (player == null || env == null) {
            return false;
        } if (player.getRace() == Race.ASMODIANS && player.getLevel() >= 29 && player.getLevel() <= 55) {
            QuestService.startQuest(env);
            return true;
        }
        return false;
    }
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 204274) {
                switch (env.getDialog()) {
					case START_DIALOG: {
						return sendQuestDialog(env, 10002);
					} case SELECT_REWARD: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 5);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
		    if (targetId == 204274) {
			    return sendQuestEndDialog(env);
		    }
		}
		return false;
	}
}