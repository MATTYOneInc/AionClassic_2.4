package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.account.AccountSielEnergy;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.items.GodStone;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.team.legion.LegionEmblem;
import com.aionemu.gameserver.model.team.legion.LegionEmblemType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.EnchantService;

import javolution.util.FastList;

import java.util.List;

public class S_PUT_USER extends AionServerPacket
{
	private final Player player;
	private boolean enemy;
	
	public S_PUT_USER(Player player, boolean enemy) {
		this.player = player;
		this.enemy = enemy;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		Player activePlayer = con.getActivePlayer();
		if (activePlayer == null || player == null) {
			return;
		}
		PlayerCommonData pcd = player.getCommonData();
		final int raceId;
		if (player.getAdminNeutral() > 1 || activePlayer.getAdminNeutral() > 1) {
			raceId = activePlayer.getRace().getRaceId();
		} else if (activePlayer.isEnemy(player)) {
			raceId = (activePlayer.getRace().getRaceId() == 0 ? 1 : 0);
		} else {
			raceId = player.getRace().getRaceId();
		}
		final int genderId = pcd.getGender().getGenderId();
		final PlayerAppearance playerAppearance = player.getPlayerAppearance();
		writeF(player.getX());// x
		writeF(player.getY());// y
		writeF(player.getZ());// z
		writeD(player.getObjectId());
		writeD(pcd.getTemplateId());
		int model = player.getTransformModel().getModelId();
		writeD(model != 0 ? model : pcd.getTemplateId());
		writeD(0);
		writeD(0);
		writeD(player.getTransformModel().getType().getId());
		writeC(enemy ? 0x00 : 0x26);
		writeC(raceId); // race
		writeC(pcd.getPlayerClass().getClassId());
		writeC(genderId); // sex
		writeH(player.getState());
		writeB(new byte[8]);
		writeC(player.getHeading());
		/**
		 * Server Staff Access Level
		 */
		String nameFormat = "%s";
		StringBuilder sb = new StringBuilder(nameFormat);
		if (player.isGmMode()) {
			switch (player.getClientConnection().getAccount().getAccessLevel()) {
				case 1:
					nameFormat = AdminConfig.ADMIN_TAG_1.replace("%s", sb.toString());
					break;
				case 2:
					nameFormat = AdminConfig.ADMIN_TAG_2.replace("%s", sb.toString());
					break;
				case 3:
					nameFormat = AdminConfig.ADMIN_TAG_3.replace("%s", sb.toString());
					break;
				case 4:
					nameFormat = AdminConfig.ADMIN_TAG_4.replace("%s", sb.toString());
					break;
				case 5:
					nameFormat = AdminConfig.ADMIN_TAG_5.replace("%s", sb.toString());
					break;
				case 6:
					nameFormat = AdminConfig.ADMIN_TAG_6.replace("%s", sb.toString());
					break;
				case 7:
					nameFormat = AdminConfig.ADMIN_TAG_7.replace("%s", sb.toString());
					break;
				case 8:
					nameFormat = AdminConfig.ADMIN_TAG_8.replace("%s", sb.toString());
					break;
				case 9:
					nameFormat = AdminConfig.ADMIN_TAG_9.replace("%s", sb.toString());
					break;
				case 10:
					nameFormat = AdminConfig.ADMIN_TAG_10.replace("%s", sb.toString());
					break;
			}
		}

		/**
		 * orphaned players - later find/remove them
		 */
		if ((player.getClientConnection() != null) && (AdminConfig.ADMIN_TAG_ENABLE)) {
			nameFormat = player.getCustomTag(false);
		}
		writeS(String.format(nameFormat, player.getName()));
		//writeS(player.getName());
		writeH(pcd.getTitleId());
		writeH(player.getCommonData().isHaveMentorFlag()? 1 : 0);
		writeH(player.getCastingSkillId());
		if (player.isLegionMember()) {
			writeD(player.getLegion().getLegionId());
			writeC(player.getLegion().getLegionEmblem().getEmblemId());
			writeC(player.getLegion().getLegionEmblem().getEmblemType().getValue());
			writeC(player.getLegion().getLegionEmblem().getEmblemType() == LegionEmblemType.DEFAULT ? 0x00 : 0xFF);
			writeC(player.getLegion().getLegionEmblem().getColor_r());
			writeC(player.getLegion().getLegionEmblem().getColor_g());
			writeC(player.getLegion().getLegionEmblem().getColor_b());
			writeS(player.getLegion().getLegionName());
		} else {
			writeB(new byte[12]);
		}
		int maxHp = player.getLifeStats().getMaxHp();
		int currHp = player.getLifeStats().getCurrentHp();
		writeC(100 * currHp / maxHp);// %hp
		writeH(pcd.getDp());// current dp
		writeC(0x00);// unk (0x00)
		/**
		 * Start Item Appearance
		 */
		List<Item> items = player.getEquipment().getEquippedItemsWithoutStigma();
		short mask = 0;
		for (Item item : items) {
			mask |= item.getEquipmentSlot();
		}
		writeH(mask);
		for (Item item : items) {
			if (item.getEquipmentSlot() < Short.MAX_VALUE * 2) {
				writeD(item.getItemSkinTemplate().getTemplateId());
				GodStone godStone = item.getGodStone();
				writeD(godStone != null ? godStone.getItemId() : 0);
				writeD(item.getItemColor());
				writeH(0x00);// unk (0x00)
			}
		}
		writeD(playerAppearance.getSkinRGB());
		writeD(playerAppearance.getHairRGB());
		writeD(playerAppearance.getEyeRGB());
		writeD(playerAppearance.getLipRGB());
		writeC(playerAppearance.getFace());
		writeC(playerAppearance.getHair());
		writeC(playerAppearance.getDeco());
		writeC(playerAppearance.getTattoo());
		writeC(playerAppearance.getFaceContour());
		writeC(playerAppearance.getExpression());
		writeC(player.getGender() == Gender.FEMALE ? 6 : 5);
		writeC(playerAppearance.getJawLine());
		writeC(playerAppearance.getForehead());
		writeC(playerAppearance.getEyeHeight());
		writeC(playerAppearance.getEyeSpace());
		writeC(playerAppearance.getEyeWidth());
		writeC(playerAppearance.getEyeSize());
		writeC(playerAppearance.getEyeShape());
		writeC(playerAppearance.getEyeAngle());
		writeC(playerAppearance.getBrowHeight());
		writeC(playerAppearance.getBrowAngle());
		writeC(playerAppearance.getBrowShape());
		writeC(playerAppearance.getNose());
		writeC(playerAppearance.getNoseBridge());
		writeC(playerAppearance.getNoseWidth());
		writeC(playerAppearance.getNoseTip());
		writeC(playerAppearance.getCheek());
		writeC(playerAppearance.getLipHeight());
		writeC(playerAppearance.getMouthSize());
		writeC(playerAppearance.getLipSize());
		writeC(playerAppearance.getSmile());
		writeC(playerAppearance.getLipShape());
		writeC(playerAppearance.getJawHeigh());
		writeC(playerAppearance.getChinJut());
		writeC(playerAppearance.getEarShape());
		writeC(playerAppearance.getHeadSize());
		writeC(playerAppearance.getNeck());
		writeC(playerAppearance.getNeckLength());
		writeC(playerAppearance.getShoulderSize());
		writeC(playerAppearance.getTorso());
		writeC(playerAppearance.getChest()); // only woman
		writeC(playerAppearance.getWaist());
		writeC(playerAppearance.getHips());
		writeC(playerAppearance.getArmThickness());
		writeC(playerAppearance.getHandSize());
		writeC(playerAppearance.getLegThickness());
		writeC(playerAppearance.getFootSize());
		writeC(playerAppearance.getFacialRate());
		writeC(0x00); // always 0
		writeC(playerAppearance.getArmLength());
		writeC(playerAppearance.getLegLength());
		writeC(playerAppearance.getShoulders());
		writeC(playerAppearance.getFaceShape());
		writeC(0x00); // always 0
		writeC(playerAppearance.getVoice());
		writeF(playerAppearance.getHeight());
		writeF(0.25f); // scale
		writeF(2.0f); // gravity or slide surface o_O
		writeF(player.getGameStats().getMovementSpeedFloat()); // move speed
		writeH(player.getGameStats().getAttackSpeed().getBase());
		writeH(player.getGameStats().getAttackSpeed().getCurrent());
		writeC(player.getPortAnimation());
		writeS(player.hasStore() ? player.getStore().getStoreMessage() : "");// private store message
		/**
		 * Movement
		 */
		writeF(0);
		writeF(0);
		writeF(0);
		writeF(player.getX());// x
		writeF(player.getY());// y
		writeF(player.getZ());// z
		writeC(0x00); // move type
		if (player.isUsingFlyTeleport()) {
			writeD(player.getFlightTeleportId());
			writeD(player.getFlightDistance());
		} else if (player.isInPlayerMode(PlayerMode.WINDSTREAM)) {
            writeD(player.windstreamPath.teleportId);
            writeD(player.windstreamPath.distance);
        }
		writeC(player.getVisualState()); // visualState
		writeS(player.getCommonData().getNote()); // note show in right down windows if your target on player
		writeH(player.getLevel()); // [level]
		writeH(player.getPlayerSettings().getDisplay()); // unk - 0x04
		writeH(player.getPlayerSettings().getDeny()); // unk - 0x00
		writeH(player.getAbyssRank().getRank().getId()); // abyss rank
		writeH(0x00); // unk - 0x01
		writeD(player.getTarget() == null ? 0 : player.getTarget().getObjectId());
		writeC(0); // suspect id
		writeD(player.getCurrentTeamId());
		writeC(player.isMentor() ? 1 : 0);

		//
		writeD(player.getPlayerAccount().getMembership() == 2 ? 3 : 1);
		writeC(0); //test
		writeD(0);

		//writeD(player.getPlayerAccount().getMembership() == 2 ? 3 : 0);
	}
}