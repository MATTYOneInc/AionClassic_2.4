/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.operations;

import com.aionemu.gameserver.questEngine.model.QuestEnv;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestOperations", propOrder = { "operations" })
public class QuestOperations {

	@XmlElements({ @XmlElement(name = "take_item", type = TakeItemOperation.class),
		@XmlElement(name = "npc_dialog", type = NpcDialogOperation.class),
		@XmlElement(name = "set_quest_status", type = SetQuestStatusOperation.class),
		@XmlElement(name = "give_item", type = GiveItemOperation.class),
		@XmlElement(name = "start_quest", type = StartQuestOperation.class),
		@XmlElement(name = "npc_use", type = ActionItemUseOperation.class),
		@XmlElement(name = "set_quest_var", type = SetQuestVarOperation.class),
		@XmlElement(name = "collect_items", type = CollectItemQuestOperation.class) })
	protected List<QuestOperation> operations;
	@XmlAttribute
	protected Boolean override;

	/**
	 * Gets the value of the override property.
	 * 
	 * @return possible object is {@link Boolean }
	 */
	public boolean isOverride() {
		if (override == null) {
			return true;
		}
		else {
			return override;
		}
	}

	public boolean operate(QuestEnv env) {
		if (operations != null) {
			for (QuestOperation oper : operations) {
				oper.doOperate(env);
			}
		}
		return isOverride();
	}
}
