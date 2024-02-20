package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.List;

public class S_ATTACK extends AionServerPacket
{
	private int attackno;
	private int time;
	private int type;
	private List<AttackResult> attackList;
	private Creature attacker;
	private Creature target;
	
	public S_ATTACK(Creature attacker, Creature target, int attackno, int time, int type, List<AttackResult> attackList) {
		this.attacker = attacker;
		this.target = target;
		this.attackno = attackno;
		this.time = time;
		this.type = type;
		this.attackList = attackList;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(attacker.getObjectId());
		writeC(attackno);
		writeH(time);
		writeC(type);
		writeC(1); //unk 2.7
		writeD(target.getObjectId());
		int attackerMaxHp = attacker.getLifeStats().getMaxHp();
		int attackerCurrHp = attacker.getLifeStats().getCurrentHp();
		int targetMaxHp = target.getLifeStats().getMaxHp();
		int targetCurrHp = target.getLifeStats().getCurrentHp();
		writeC((int) (100f * targetCurrHp / targetMaxHp));
		writeC((int) (100f * attackerCurrHp / attackerMaxHp));
		switch (attackList.get(0).getAttackStatus().getId()) {
			case 196:
			case 4:
			case 5:
			case 213:
				writeD(32);
			break;
			case 194:
			case 2:
			case 3:
			case 211:
				writeD(64);
			break;
			case 192:
			case 0:
			case 1:
			case 209:
				writeD(128);
			break;
			case 198:
			case 6:
			case 7:
			case 215:
				writeD(256);
			break;
			default:
				writeD(0);
			break;
		} if (target instanceof Player) {
            if (attackList.get(0).getAttackStatus().isCounterSkill()) {
                ((Player) target).setLastCounterSkill(attackList.get(0).getAttackStatus());
            }
        }
		writeC(attackList.size());
		for (AttackResult attack : attackList) {
			writeD(attack.getDamage());
			writeC(attack.getAttackStatus().getId());
			byte shieldType = (byte) attack.getShieldType();
			writeC(shieldType);
			switch (shieldType) {
				case 0:
				case 2:
				break;
				case 8:
				case 10:
					writeD(attack.getProtectorId()); 
					writeD(attack.getProtectedDamage());
					writeD(attack.getProtectedSkillId());
				break;
				default:
					writeD(attack.getProtectorId());
					writeD(attack.getProtectedDamage());
					writeD(attack.getProtectedSkillId());
					writeD(attack.getReflectedDamage());
					writeD(attack.getReflectedSkillId());
				break;
			}
		}
		writeC(0);
		writeB("00000A00");
	}
}