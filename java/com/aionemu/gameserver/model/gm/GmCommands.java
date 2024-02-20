package com.aionemu.gameserver.model.gm;

public enum GmCommands
{
    GM_DIALOG_TELEPORTTO,
	GM_DIALOG_RECALL,
    GM_DIALOG,
    GM_DIALOG_POS,
	GM_DIALOG_MEMO,
	GM_DIALOG_BOOKMARK,
	GM_DIALOG_INVENTORY,
	GM_DIALOG_SKILL,
	GM_DIALOG_STATUS,
	GM_DIALOG_QUEST,
	GM_DIALOG_REFRESH,
	GM_DIALOG_WAREHOUSE,
	GM_DIALOG_MAIL,
	GM_POLL_DIALOG,
	GM_POLL_DIALOG_SUBMIT,
	GM_BOOKMARK_DIALOG,
	GM_BOOKMARK_DIALOG_ADD_BOOKMARK,
	GM_MEMO_DIALOG,
	GM_MEMO_DIALOG_ADD_MEMO,
	GM_DIALOG_CHECK_BOT1,
	GM_DIALOG_CHECK_BOT99,
	GM_INDICATOR_DIALOG_TOOLTIP_HOUSING_MODE,
	GM_DIALOG_CHARACTER,
	GM_DIALOG_OPTION,
	GM_DIALOG_BUILDER_CONTROL,
	GM_DIALOG_BUILDER_COMMAND;
	
	public static GmCommands getValue(String command) {
		for (GmCommands value : values()) {
			if (value.name().equals(command.toUpperCase())) {
				return value;
		    }
		}
		throw new IllegalArgumentException("Invalid GmCommands id: " + command);
	}
}