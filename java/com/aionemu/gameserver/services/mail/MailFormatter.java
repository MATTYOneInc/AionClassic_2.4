package com.aionemu.gameserver.services.mail;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.templates.mail.MailPart;
import com.aionemu.gameserver.model.templates.mail.MailTemplate;

public final class MailFormatter
{
	public static void sendBlackCloudMail(String recipientName, final int itemObjectId, final int itemCount) {
		final MailTemplate template = DataManager.SYSTEM_MAIL_TEMPLATES.getMailTemplate("$$CASH_ITEM_MAIL", "", Race.PC_ALL);
		MailPart formatter = new MailPart() {
			@Override
			public String getParamValue(String name) {
				if ("itemid".equals(name)) {
					return Integer.toString(itemObjectId);
				} else if ("count".equals(name)) {
					return Integer.toString(itemCount);
				} else if ("unk1".equals(name)) {
					return "0";
				} else if ("purchasedate".equals(name)) {
					return Long.toString(System.currentTimeMillis() / 1000);
				}
				return "";
			}
		};
		String title = template.getFormattedTitle(formatter);
		String body = template.getFormattedMessage(formatter);
		SystemMailService.getInstance().sendMail("$$CASH_ITEM_MAIL", recipientName, title, body, itemObjectId, itemCount, 0, LetterType.BLACKCLOUD);
	}
	
	public static void sendAbyssRewardMail(final SiegeLocation siegeLocation, final PlayerCommonData playerData,
	    final AbyssSiegeLevel level, final SiegeResult result, final long time, int attachedItemObjId,
        long attachedItemCount, long attachedKinahCount) {
        final MailTemplate template = DataManager.SYSTEM_MAIL_TEMPLATES.getMailTemplate("$$ABYSS_REWARD_MAIL", "", playerData.getRace());
        MailPart formatter = new MailPart() {
            @Override
            public String getParamValue(String name) {
                if ("siegelocid".equals(name)) {
                    return Integer.toString(siegeLocation.getTemplate().getId());
                } else if ("datetime".equals(name)) {
                    return Long.toString(time / 1000);
                } else if ("rankid".equals(name)) {
                    return Integer.toString(level.getId());
                } else if ("raceid".equals(name)) {
                    return Integer.toString(playerData.getRace().getRaceId());
                } else if ("resultid".equals(name)) {
                    return Integer.toString(result.getId());
                }
                return "";
            }
        };
        String title = template.getFormattedTitle(formatter);
        String message = template.getFormattedMessage(formatter);
        SystemMailService.getInstance().sendMail("$$ABYSS_REWARD_MAIL", playerData.getName(), title, message, attachedItemObjId, attachedItemCount, attachedKinahCount, LetterType.NORMAL);
    }
	
	public static void sendAbyssPointRewardMail(final SiegeLocation siegeLocation, final PlayerCommonData playerData,
		final AbyssSiegeLevel level, final SiegeResult result, final long time, int attachedItemObjId,
		long attachedItemCount, long attachedApCount) {
		final MailTemplate template = DataManager.SYSTEM_MAIL_TEMPLATES.getMailTemplate("$$ABYSS_REWARD_MAIL", "", playerData.getRace());
		MailPart formatter = new MailPart() {
			@Override
			public String getParamValue(String name) {
				if ("siegelocid".equals(name)) {
					return Integer.toString(siegeLocation.getTemplate().getId());
				} else if ("datetime".equals(name)) {
					return Long.toString(time / 1000);
				} else if ("rankid".equals(name)) {
					return Integer.toString(level.getId());
				} else if ("raceid".equals(name)) {
					return Integer.toString(playerData.getRace().getRaceId());
				} else if ("resultid".equals(name)) {
					return Integer.toString(result.getId());
				}
				return "";
			}
		};
		String title = template.getFormattedTitle(formatter);
		String message = template.getFormattedMessage(formatter);
		SystemMailService.getInstance().sendMail("$$ABYSS_REWARD_MAIL", playerData.getName(), title, message, attachedItemObjId, attachedItemCount, 0, LetterType.NORMAL);
	}
}