package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.summons.SkillOrder;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_SUMMON_CASTSPELL extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger(CM_SUMMON_CASTSPELL.class);
	
	private int summonObjId;
    private int targetObjId;
    private int skillId;
    private int skillLvl;
    private int unk;
	
	public CM_SUMMON_CASTSPELL(int opcode, AionConnection.State state, AionConnection.State ... restStates) {
        super(opcode, state, restStates);
    }
	
	protected void readImpl() {
        this.summonObjId = this.readD();
        this.skillId = this.readH();
        this.skillLvl = this.readC();
        this.targetObjId = this.readD();
        this.unk = this.readD();
    }
	
	protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        final Summon summon = player.getSummon();
        if (summon == null) {
            return;
        } if (summon.getObjectId() != this.summonObjId) {
            return;
        }
        Creature target = null;
        if (this.targetObjId != summon.getObjectId()) {
            VisibleObject obj = summon.getKnownList().getObject(this.targetObjId);
            if (obj instanceof Creature) {
                target = (Creature)obj;
            }
        } else {
            target = summon;
        } if (target != null) {
            final SkillOrder order = summon.retrieveNextSkillOrder();
            if (order != null && order.getTarget() == target) {
                ThreadPoolManager.getInstance().execute(new Runnable(){
                    @Override
                    public void run() {
                        summon.getController().useSkill(order);
                    }
                });
            }
        }
    }
}