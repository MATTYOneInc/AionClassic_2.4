package com.aionemu.gameserver.services;

import com.aionemu.gameserver.model.DuelResult;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.network.aion.serverpackets.S_DUEL;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public class DuelService
{
	private static Logger log = LoggerFactory.getLogger(DuelService.class);
	
	private FastMap<Integer, Integer> duels;
	private FastMap<Integer, Future<?>> timeOutTask;

	public static final DuelService getInstance() {
		return SingletonHolder.instance;
	}
	
	private DuelService() {
		this.duels = new FastMap<Integer, Integer>().shared();
		timeOutTask = new FastMap<Integer, Future<?>>().shared();
	}
	
	public void onDuelRequest(Player requester, Player responder) {
		if (requester.isInsideZoneType(ZoneType.PVP) || responder.isInsideZoneType(ZoneType.PVP)) {
			PacketSendUtility.sendPacket(requester, S_MESSAGE_CODE.STR_DUEL_PARTNER_INVALID(responder.getName()));
			return;
		} if (isDueling(requester.getObjectId()) || isDueling(responder.getObjectId())) {
			PacketSendUtility.sendPacket(requester, S_MESSAGE_CODE.STR_DUEL_HE_REJECT_DUEL(responder.getName()));
			return;
		} for (ZoneInstance zone : responder.getPosition().getMapRegion().getZones(responder)) {
			if (((!zone.isOtherRaceDuelsAllowed()) && (!responder.getRace().equals(requester.getRace()))) || ((!zone.isSameRaceDuelsAllowed()) && (responder.getRace().equals(requester.getRace())))) {
				PacketSendUtility.sendPacket(requester, S_MESSAGE_CODE.STR_MSG_DUEL_CANT_IN_THIS_ZONE);
				return;
			}
		}
		RequestResponseHandler rrh = new RequestResponseHandler(requester) {
			@Override
			public void denyRequest(Creature requester, Player responder) {
				rejectDuelRequest((Player) requester, responder);
			}
			@Override
			public void acceptRequest(Creature requester, Player responder) {
				startDuel((Player) requester, responder);
			}
		};
		responder.getResponseRequester().putRequest(S_ASK.STR_DUEL_DO_YOU_ACCEPT_REQUEST, rrh);
		PacketSendUtility.sendPacket(responder, new S_ASK(S_ASK.STR_DUEL_DO_YOU_ACCEPT_REQUEST, 0, 0, requester.getName()));
		PacketSendUtility.sendPacket(responder, S_MESSAGE_CODE.STR_DUEL_REQUESTED(requester.getName()));
	}
	
	public void confirmDuelWith(Player requester, Player responder) {
		if (requester.isEnemy(responder)) {
			return;
		}
		RequestResponseHandler rrh = new RequestResponseHandler(responder) {
			@Override
			public void denyRequest(Creature requester, Player responder) {
			}
			@Override
			public void acceptRequest(Creature requester, Player responder) {
				cancelDuelRequest(responder, (Player) requester);
			}
		};
		requester.getResponseRequester().putRequest(S_ASK.STR_DUEL_DO_YOU_WITHDRAW_REQUEST, rrh);
		PacketSendUtility.sendPacket(requester, new S_ASK(S_ASK.STR_DUEL_DO_YOU_WITHDRAW_REQUEST, 0, 0, responder.getName()));
		PacketSendUtility.sendPacket(requester, S_MESSAGE_CODE.STR_DUEL_REQUEST_TO_PARTNER(responder.getName()));
	}
	
	private void rejectDuelRequest(Player requester, Player responder) {
		PacketSendUtility.sendPacket(requester, S_MESSAGE_CODE.STR_DUEL_HE_REJECT_DUEL(responder.getName()));
		PacketSendUtility.sendPacket(responder, S_MESSAGE_CODE.STR_DUEL_REJECT_DUEL(requester.getName()));
	}
	
	private void cancelDuelRequest(Player owner, Player target) {
		PacketSendUtility.sendPacket(target, S_MESSAGE_CODE.STR_DUEL_REQUESTER_WITHDRAW_REQUEST(owner.getName()));
		PacketSendUtility.sendPacket(owner, S_MESSAGE_CODE.STR_DUEL_WITHDRAW_REQUEST(target.getName()));
	}
	
	private void startDuel(final Player requester, final Player responder) {
		PacketSendUtility.sendPacket(requester, S_DUEL.SM_DUEL_STARTED(responder.getObjectId()));
		PacketSendUtility.sendPacket(responder, S_DUEL.SM_DUEL_STARTED(requester.getObjectId()));
		startDuelMsg(requester, responder);
		createDuel(requester.getObjectId(), responder.getObjectId());
		createTask(requester, responder);
	}
	
	private void startDuelMsg(final Player player1, final Player player2) {
        World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player object) {
                if (MathUtil.isInRange(player1, object, 100)) {
				    //A duel between %0 and %1 has started.
				    PacketSendUtility.sendPacket(object, S_MESSAGE_CODE.STR_DUEL_START_BROADCAST(player2.getName(), player1.getName()));
				}
            }
        });
    }
	
	private void loseDuelMsg(final Player player1, final Player player2) {
        World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player object) {
                if (MathUtil.isInRange(player1, object, 100)) {
				    //%0 defeated %1 in a duel.
				    PacketSendUtility.sendPacket(object, S_MESSAGE_CODE.STR_DUEL_STOP_BROADCAST(player2.getName(), player1.getName()));
				}
            }
        });
    }
	
	private void drawDuelMsg(final Player player1, final Player player2) {
        World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player object) {
                if (MathUtil.isInRange(player1, object, 100)) {
				    //The duel between %0 and %1 was a draw.
				    PacketSendUtility.sendPacket(object, S_MESSAGE_CODE.STR_DUEL_TIMEOUT_BROADCAST(player2.getName(), player1.getName()));
				}	
            }
        });
    }
	
	public void loseDuel(Player player) {
		if (!isDueling(player.getObjectId())) {
			return;
		}
		int opponnentId = duels.get(player.getObjectId());
		Player opponent = World.getInstance().findPlayer(opponnentId);
		if (opponent != null) {
			opponent.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
			opponent.getController().cancelCurrentSkill();
			if (player.getSummon() != null) {
				SummonsService.doMode(SummonMode.GUARD, player.getSummon(), UnsummonType.UNSPECIFIED);
			} if (opponent.getSummon() != null) {
				SummonsService.doMode(SummonMode.GUARD, opponent.getSummon(), UnsummonType.UNSPECIFIED);
			} if (player.getSummon() != null) {
				player.getSummon().getController().cancelCurrentSkill();
			} if (opponent.getSummon() != null) {
				opponent.getSummon().getController().cancelCurrentSkill();
			}
			loseDuelMsg(player, opponent);
			PacketSendUtility.sendPacket(opponent, S_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_YOU_WIN, player.getName()));
			PacketSendUtility.sendPacket(player, S_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_YOU_LOSE, opponent.getName()));
		}
		removeDuel(player.getObjectId(), opponnentId);
	}
	
	public void loseArenaDuel(Player player) {
		if (!isDueling(player.getObjectId())) {
			return;
		}
		player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
		player.getController().cancelCurrentSkill();
		int opponnentId = duels.get(player.getObjectId());
		Player opponent = World.getInstance().findPlayer(opponnentId);
		if (opponent != null) {
			opponent.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
			opponent.getController().cancelCurrentSkill();
		}
		removeDuel(player.getObjectId(), opponnentId);
	}
	
	private void createTask(final Player requester, final Player responder) {
		Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {
			public void run() {
				if (isDueling(requester.getObjectId(), responder.getObjectId())) {
					drawDuelMsg(requester, responder);
					PacketSendUtility.sendPacket(requester, S_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_TIMEOUT, requester.getName()));
					PacketSendUtility.sendPacket(responder, S_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_TIMEOUT, responder.getName()));
					DuelService.this.removeDuel(requester.getObjectId(), responder.getObjectId());
				}
			}
		}, 5 * 60 * 1000);
		timeOutTask.put(requester.getObjectId(), task);
		timeOutTask.put(responder.getObjectId(), task);
	}
	
	public boolean isDueling(int playerObjId) {
		return duels.containsKey(playerObjId) && duels.containsValue(playerObjId);
	}
	
	public boolean isDueling(int playerObjId, int targetObjId) {
		return duels.containsKey(playerObjId) && duels.get(playerObjId) == targetObjId;
	}
	
	public void createDuel(int requesterObjId, int responderObjId) {
		duels.put(requesterObjId, responderObjId);
		duels.put(responderObjId, requesterObjId);
	}
	
	private void removeDuel(int requesterObjId, int responderObjId) {
		duels.remove(requesterObjId);
		duels.remove(responderObjId);
		removeTask(requesterObjId);
		removeTask(responderObjId);
	}
	
	private void removeTask(int playerId) {
		Future<?> task = timeOutTask.get(playerId);
		if (task != null && !task.isDone()) {
			task.cancel(true);
			timeOutTask.remove(playerId);
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final DuelService instance = new DuelService();
	}
}