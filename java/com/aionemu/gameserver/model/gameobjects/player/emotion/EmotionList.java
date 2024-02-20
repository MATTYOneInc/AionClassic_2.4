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
package com.aionemu.gameserver.model.gameobjects.player.emotion;

import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dao.PlayerEmotionListDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_ADDREMOVE_SOCIAL;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * @author MrPoke
 *
 */
public class EmotionList {
	private Map<Integer, Emotion> emotions;
	private Player owner;
	/**
	 * @param owner
	 */
	public EmotionList(Player owner) {
		this.owner = owner;
	}

	public void add(int emotionId, int dispearTime, boolean isNew){
		if (emotions == null) {
			emotions = new HashMap<Integer, Emotion>();
		}
		Emotion emotion = new Emotion(emotionId, dispearTime);
		emotions.put(emotionId, emotion);

		if (isNew){
			if (emotion.getExpireTime() != 0)
				ExpireTimerTask.getInstance().addTask(emotion, owner);
			PlayerEmotionListDAO.insertEmotion(owner, emotion);
			PacketSendUtility.sendPacket(owner, new S_ADDREMOVE_SOCIAL((byte) 1, Collections.singletonList(emotion)));
		}
	}

	public void remove(int emotionId){
		emotions.remove(emotionId);
		PlayerEmotionListDAO.deleteEmotion(owner.getObjectId(), emotionId);
		PacketSendUtility.sendPacket(owner, new S_ADDREMOVE_SOCIAL((byte)0, getEmotions()));
	}

	public boolean contains(int emotionId){
		if (emotions == null)
			return false;
		return emotions.containsKey(emotionId);
	}

	public boolean canUse(int emotionId) {
		return emotionId < 64 || emotionId > 155 || (emotions != null && emotions.containsKey(emotionId)) || owner.havePermission(MembershipConfig.EMOTIONS_ALL);
	}

	public Collection<Emotion> getEmotions(){
		if (emotions == null)
			return Collections.emptyList();
		return emotions.values();
	}
}
