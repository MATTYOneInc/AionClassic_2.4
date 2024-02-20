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

package com.aionemu.gameserver.questEngine.handlers.models.xmlQuest;

import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.conditions.QuestConditions;
import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.operations.QuestOperations;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestDialog", propOrder = { "conditions", "operations" })
public class QuestDialog {

	protected QuestConditions conditions;
	protected QuestOperations operations;
	@XmlAttribute(required = true)
	protected int id;

	public boolean operate(QuestEnv env, QuestState qs) {
		if (env.getDialogId() != id)
			return false;
		if (conditions == null || conditions.checkConditionOfSet(env)) {
			if (operations != null) {
				return operations.operate(env);
			}
		}
		return false;
	}
}
