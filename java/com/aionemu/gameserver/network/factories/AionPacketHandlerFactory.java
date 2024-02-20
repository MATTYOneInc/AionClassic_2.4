package com.aionemu.gameserver.network.factories;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.AionPacketHandler;
import com.aionemu.gameserver.network.aion.clientpackets.*;

public class AionPacketHandlerFactory {
    private AionPacketHandler handler;

    public static AionPacketHandlerFactory getInstance() {
        return SingletonHolder.instance;
    }

    public AionPacketHandlerFactory() {
        handler = new AionPacketHandler();
        addPacket(new CM_VERSION_CHECK(0xF2, State.CONNECTED)); //2.7
        addPacket(new CM_TIME_CHECK(0xE0, State.CONNECTED, State.AUTHED, State.IN_GAME)); //2.7
        addPacket(new CM_E0_UNK(0xE2, State.CONNECTED, State.AUTHED, State.IN_GAME)); //2.7
        addPacket(new CM_L2AUTH_LOGIN_CHECK(0x11F, State.CONNECTED)); //2.7
        addPacket(new CM_AUTH_SUCCESS(0x193, State.AUTHED, State.IN_GAME)); //2.7
        addPacket(new CM_MAC_ADDRESS(0x174, State.CONNECTED, State.AUTHED, State.IN_GAME)); //2.7
        addPacket(new CM_CHARACTER_LIST(0x11C, State.AUTHED)); //2.7
        addPacket(new CM_PING(0xC6, State.AUTHED, State.IN_GAME)); //2.7
        addPacket(new CM_MAY_LOGIN_INTO_GAME(0x179, State.AUTHED)); //2.7
        addPacket(new CM_ENTER_WORLD(0xEA, State.AUTHED)); //2.7
        addPacket(new CM_CHECK_NICKNAME(0x143, State.AUTHED)); //2.7
        addPacket(new CM_RESTORE_CHARACTER(0x11D, State.AUTHED)); //2.7
        addPacket(new CM_CREATE_CHARACTER(0x11D, State.AUTHED)); //2.7
        addPacket(new CM_DELETE_CHARACTER(0x11A, State.AUTHED)); //2.7
        addPacket(new CM_LEVEL_READY(0xEB, State.IN_GAME)); //2.7
        addPacket(new CM_RECONNECT_AUTH(0x17A, State.AUTHED)); //2.7
        addPacket(new CM_FRIEND_STATUS(0x148, State.IN_GAME)); //2.7
        addPacket(new CM_SHOW_BLOCKLIST(0x14A, State.IN_GAME)); //2.7
        addPacket(new CM_SHOW_FRIENDLIST(0x104, State.IN_GAME)); //2.7
        addPacket(new CM_CHAT_AUTH(0x144, State.IN_GAME)); //2.7
        addPacket(new CM_MOVE_ITEM(0x116, State.IN_GAME)); //2.7
        addPacket(new CM_MOVE(0xC2, State.IN_GAME)); //2.7
        addPacket(new CM_UI_SETTINGS(0xE8, State.IN_GAME)); //2.7
        addPacket(new CM_QUIT(0xF1, State.AUTHED, State.IN_GAME)); //2.7
        addPacket(new CM_MAY_QUIT(0xEE, State.AUTHED, State.IN_GAME)); //2.7
        addPacket(new CM_PLAY_MOVIE_END(0x123, State.IN_GAME)); //2.7
        addPacket(new CM_PLAYER_LISTENER(0xCA, State.IN_GAME)); //2.7
        addPacket(new CM_PING_REQUEST(0x10D, State.IN_GAME)); //2.7
        addPacket(new CM_CLIENT_COMMAND_ROLL(0x109, State.IN_GAME)); //2.7
        addPacket(new CM_USE_ITEM(0xCF, State.IN_GAME)); //2.7
        addPacket(new CM_CHAT_MESSAGE_PUBLIC(0x99, State.IN_GAME)); //2.7
        addPacket(new CM_EMOTION(0xC9, State.IN_GAME)); //2.7
        addPacket(new CM_FRIEND_STATUS(0x148, State.IN_GAME)); //2.7
        addPacket(new CM_INSTANCE_INFO(0x1B3, State.IN_GAME)); //2.7
        addPacket(new CM_ATTACK(0xD2, State.IN_GAME)); //2.7
        addPacket(new CM_EQUIP_ITEM(0xCC, State.IN_GAME)); //2.7
        addPacket(new CM_TARGET_SELECT(0x95, State.IN_GAME)); //2.7
        addPacket(new CM_START_LOOT(0x118, State.IN_GAME)); //2.7
        addPacket(new CM_SPLIT_ITEM(0x117, State.IN_GAME)); //2.7
        addPacket(new CM_CASTSPELL(0xD3, State.IN_GAME)); //2.7
        addPacket(new CM_LOOT_ITEM(0x119, State.IN_GAME)); //2.7
        addPacket(new CM_DELETE_ITEM(0x13E, State.IN_GAME)); //2.7
        addPacket(new CM_SHOW_MAP(0x1AF, State.IN_GAME)); //2.7
        addPacket(new CM_SET_NOTE(0xF8, State.IN_GAME)); //2.7
        addPacket(new CM_REMOVE_ALTERED_STATE(0xD1, State.IN_GAME)); //2.7
        addPacket(new CM_FIND_GROUP(0x127, State.IN_GAME));//2.7
		addPacket(new CM_SHOW_DIALOG(0xFE, State.IN_GAME)); //2.7
		addPacket(new CM_DIALOG_SELECT(0xFC, State.IN_GAME)); //2.7
		addPacket(new CM_CLOSE_DIALOG(0xFF, State.IN_GAME)); //2.7
		addPacket(new CM_PLAYER_SEARCH(0x115, State.IN_GAME)); //2.7
		addPacket(new CM_DELETE_QUEST(0x122, State.IN_GAME)); //2.7
		addPacket(new CM_TITLE_SET(0x169, State.IN_GAME)); //2.7
		addPacket(new CM_REVIVE(0xEF, State.IN_GAME)); //2.7
		addPacket(new CM_MOVE_IN_AIR(0x186, State.IN_GAME)); //2.7
		addPacket(new CM_TELEPORT_SELECT(0x11E, State.IN_GAME)); //2.7
		addPacket(new CM_MOTION(0x12D, State.IN_GAME)); //2.7
		addPacket(new CM_ITEM_REMODEL(0x2D8, State.IN_GAME)); //2.7
		addPacket(new CM_GODSTONE_SOCKET(0x2D9, State.IN_GAME)); //2.7
		addPacket(new CM_FUSION_WEAPONS(0x1A5, State.IN_GAME)); //2.7
		addPacket(new CM_BREAK_WEAPONS(0x1A2, State.IN_GAME)); //2.7
		addPacket(new CM_PET(0x9C, State.IN_GAME)); //2.7
        addPacket(new CM_PET_EMOTE(0x9E, State.IN_GAME)); //2.7
		addPacket(new CM_CUBE_EXPAND(0x1BB, State.IN_GAME)); //2.7
		addPacket(new CM_ENCHANMENT_STONES(0x128, State.IN_GAME)); //2.7
		addPacket(new CM_GATHER(0xE1, State.IN_GAME)); //2.7
		addPacket(new CM_MACRO_DELETE(0x142, State.IN_GAME)); //2.7
		addPacket(new CM_MACRO_CREATE(0x145, State.IN_GAME)); //2.7
		addPacket(new CM_ABYSS_RANKING_PLAYERS(0x177, State.IN_GAME)); //2.7
        addPacket(new CM_ABYSS_RANKING_LEGIONS(0x13C, State.IN_GAME)); //2.7
		addPacket(new CM_STOP_TRAINING(0x2DE, State.IN_GAME)); //2.7
		addPacket(new CM_WINDSTREAM(0x12C, State.IN_GAME)); //2.7
		addPacket(new CM_QUEST_SHARE(0x14E, State.IN_GAME)); //2.7
		addPacket(new CM_QUESTIONNAIRE(0x163, State.IN_GAME)); //2.7
		addPacket(new CM_OBJECT_SEARCH(0xE9, State.IN_GAME)); //2.7
		addPacket(new CM_CHARGE_ITEM(0x124, State.IN_GAME)); //2.7
		addPacket(new CM_AUTO_GROUP(0x1AB, State.IN_GAME)); //2.7
		addPacket(new CM_INSTANCE_LEAVE(0xC4, State.IN_GAME)); //2.7
		addPacket(new CM_OPEN_STATICDOOR(0x9D, State.IN_GAME)); //2.7
		addPacket(new CM_BLOCK_ADD(0x14C, State.IN_GAME)); //2.7
		addPacket(new CM_FRIEND_ADD(0x105, State.IN_GAME)); //2.7
        addPacket(new CM_FRIEND_DEL(0x102, State.IN_GAME)); //2.7
		addPacket(new CM_CHARACTER_EDIT(0xED, State.AUTHED)); //2.7
		addPacket(new CM_APPEARANCE(0x1AC, State.IN_GAME)); //2.7
		addPacket(new CM_QUESTION_RESPONSE(0xC0, State.IN_GAME)); //2.7
		addPacket(new CM_TOGGLE_SKILL_DEACTIVATE(0xD0, State.IN_GAME)); //2.7
		addPacket(new CM_PRIVATE_STORE(0x13D, State.IN_GAME)); //2.7
		addPacket(new CM_PRIVATE_STORE_NAME(0x13A, State.IN_GAME)); //2.7
		addPacket(new CM_SUMMON_MOVE(0x1A8, State.IN_GAME)); //2.7
        addPacket(new CM_SUMMON_COMMAND(0x13B, State.IN_GAME)); //2.7
        addPacket(new CM_SUMMON_EMOTION(0x1A9, State.IN_GAME)); //2.7
        addPacket(new CM_SUMMON_ATTACK(0x1A6, State.IN_GAME)); //2.7
        addPacket(new CM_SUMMON_CASTSPELL(0x1A4, State.IN_GAME)); //2.7
		addPacket(new CM_CAPTCHA(0xE4, State.IN_GAME)); //2.7
		addPacket(new CM_CRAFT(0x167, State.IN_GAME)); //2.7
		addPacket(new CM_BUY_ITEM(0xC1, State.IN_GAME)); //2.7
		addPacket(new CM_BUY_TRADE_IN_TRADE(0x2DA, State.IN_GAME)); //2.7
		addPacket(new CM_VIEW_PLAYER_DETAILS(0x10E, State.IN_GAME)); //2.7
        addPacket(new CM_TEAM_INVITE(0x113, State.IN_GAME)); //2.7
		addPacket(new CM_DUEL_REQUEST(0x100, State.IN_GAME)); //2.7
		addPacket(new CM_EXCHANGE_REQUEST(0xF5, State.IN_GAME)); //2.7
        addPacket(new CM_EXCHANGE_ADD_ITEM(0x132, State.IN_GAME)); //2.7
        addPacket(new CM_EXCHANGE_ADD_KINAH(0x130, State.IN_GAME)); //2.7
        addPacket(new CM_EXCHANGE_CANCEL(0x12F, State.IN_GAME)); //2.7
        addPacket(new CM_EXCHANGE_LOCK(0x131, State.IN_GAME)); //2.7
        addPacket(new CM_EXCHANGE_OK(0x12E, State.IN_GAME)); //2.7
        addPacket(new CM_GROUP_DISTRIBUTION(0x106, State.IN_GAME)); //2.7
        addPacket(new CM_DISTRIBUTION_SETTINGS(0x178, State.IN_GAME)); //2.7
        addPacket(new CM_GROUP_LOOT(0x17b, State.IN_GAME)); //2.7
        addPacket(new CM_GROUP_PLAYER_STATUS_INFO(0x112, State.IN_GAME)); //2.7
		addPacket(new CM_CHAT_MESSAGE_WHISPER(0x96, State.IN_GAME)); //2.7
		addPacket(new CM_SHOW_BRAND(0x17F, State.IN_GAME)); //2.7
		addPacket(new CM_SEND_MAIL(0x16E, State.IN_GAME)); //2.7
        addPacket(new CM_READ_MAIL(0x16C, State.IN_GAME)); //2.7
		addPacket(new CM_DELETE_MAIL(0x16B, State.IN_GAME)); //2.7
		addPacket(new CM_READ_EXPRESS_MAIL(0x150, State.IN_GAME)); //2.7
        addPacket(new CM_GET_MAIL_ATTACHMENT(0x16A, State.IN_GAME)); //2.7
		addPacket(new CM_CHARACTER_PASSKEY(0x1A0, State.AUTHED)); //2.7
		addPacket(new CM_CUSTOM_SETTINGS(0xE6, State.IN_GAME)); //2.7
		addPacket(new CM_CHECK_MAIL_SIZE(0x16F, State.IN_GAME)); //2.7
		addPacket(new CM_CHECK_MAIL_SIZE_2(0x15F, State.IN_GAME)); //2.7
        addPacket(new CM_CHAT_PLAYER_INFO(0xCD, State.IN_GAME)); //2.7
        addPacket(new CM_REPLACE_ITEM(0x140, State.IN_GAME)); //2.7
        addPacket(new CM_BLOCK_DEL(0x14D, State.IN_GAME)); //2.7
        addPacket(new CM_BLOCK_SET_REASON(0x141, State.IN_GAME)); //2.7
		addPacket(new CM_GUILD(0xC7, State.IN_GAME)); //2.7
        addPacket(new CM_GUILD_TABS(0xFD, State.IN_GAME)); //2.7
        addPacket(new CM_GUILD_WH_KINAH(0x126, State.IN_GAME)); //2.7
        addPacket(new CM_BUY_BROKER_ITEM(0x134, State.IN_GAME)); //2.7
        addPacket(new CM_REGISTER_BROKER_ITEM(0x135, State.IN_GAME)); //2.7
        addPacket(new CM_BROKER_SEARCH(0x136, State.IN_GAME)); //2.7
        addPacket(new CM_BROKER_REGISTERED_LIST(0x137, State.IN_GAME)); //2.7
        addPacket(new CM_BROKER_LIST(0x139, State.IN_GAME)); //2.7
        addPacket(new CM_BROKER_COLLECT_SOLD_ITEMS(0x170, State.IN_GAME)); //2.7
        addPacket(new CM_BROKER_CANCEL_REGISTERED(0x172, State.IN_GAME)); //2.7
        addPacket(new CM_BROKER_SOLD_LIST(0x173, State.IN_GAME)); //2.7
        addPacket(new CM_BROKER_ADD_ITEM(0x190, State.IN_GAME)); //2.7
        addPacket(new CM_NP_MESSAGE(0xF4, State.IN_GAME)); //2.7
        addPacket(new CM_GG(0x10A, State.IN_GAME)); //2.7
        addPacket(new CM_UPGRADE_ARCADE(0x1B8, State.IN_GAME)); //2.7
		addPacket(new CM_SELECT_ITEM(0x15A, State.IN_GAME)); //2.7
		addPacket(new CM_EQUIPMENT_CHANGE(0x182, State.IN_GAME)); //2.7
		addPacket(new CM_EQUIPMENT_CHANGE_USE(0x183, State.IN_GAME)); //2.7
		addPacket(new CM_USER_CLASSIC_WARDROBE_FAVORITE(0x18A, State.IN_GAME)); //2.7
        addPacket(new CM_USER_CLASSIC_WARDROBE_APPLY(0x18B, State.IN_GAME)); //2.7
        addPacket(new CM_USER_CLASSIC_WARDROBE_REMOVE(0x18C, State.IN_GAME)); //2.7
        addPacket(new CM_USER_CLASSIC_WARDROBE_DYE(0x18D, State.IN_GAME)); //2.7
        addPacket(new CM_USER_CLASSIC_WARDROBE_EXTEND(0x188, State.IN_GAME)); //2.7
        addPacket(new CM_USER_CLASSIC_WARDROBE_REGISTER(0x18F, State.IN_GAME)); //2.7
        addPacket(new CM_QUEST_TELEPORT(0x1BF, State.IN_GAME)); //2.7
		addPacket(new CM_BATTLE_PASS_REWARD(0x156, State.IN_GAME)); //2.7
		
		/*
        addPacket(new CM_LEGION_MODIFY_EMBLEM(0xAE, State.IN_GAME)); //2.7
		addPacket(new CM_LEGION_UPLOAD_INFO(0x113, State.IN_GAME)); //2.7
		addPacket(new CM_LEGION_UPLOAD_EMBLEM(0x114, State.IN_GAME)); //2.7
		addPacket(new CM_LEGION_SEND_EMBLEM(0x81, State.IN_GAME)); //2.7
		addPacket(new CM_LEGION_SEND_EMBLEM_INFO(0xA2, State.IN_GAME)); //2.7
		*/
		//addPacket(new CM_DISCONNECT(0xF1, State.AUTHED, State.IN_GAME));
		addPacket(new CM_BLACKCLOUD_MAIL_OPEN(0x15B, State.IN_GAME)); //send packe for count
		addPacket(new CM_BLACKCLOUD_MAIL(0x15F, State.IN_GAME));
        addPacket(new CM_BLACKCLOUD_MAIL_CLAIM(0x15C, State.IN_GAME));
        addPacket(new CM_REPORT_CHAT(0x18E, State.IN_GAME));
        //0x15F
    }

    public AionPacketHandler getPacketHandler() {
        return handler;
    }

    private void addPacket(AionClientPacket prototype) {
        handler.addPacketPrototype(prototype);
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final AionPacketHandlerFactory instance = new AionPacketHandlerFactory();
    }
}