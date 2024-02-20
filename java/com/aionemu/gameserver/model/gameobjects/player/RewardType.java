package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.model.stats.container.StatEnum;

public enum RewardType
{
	AP_PLAYER {
		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.AP_BOOST, 100).getCurrent() / 100f;
			return (long) (reward * player.getRates().getApPlayerGainRate() * statRate);
		}
	},
	AP_NPC {
		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.AP_BOOST, 100).getCurrent() / 100f;
			return (long) (reward * player.getRates().getApNpcRate() * statRate);
		}
	},
	AP_QUEST {
		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.AP_BOOST, 100).getCurrent() / 100f;
			return (long) (reward * player.getRates().getQuestApRate() * statRate);
		}
	},
	HUNTING {
		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_HUNTING_XP_RATE, 100).getCurrent() / 100f;
			return (long) (reward * player.getRates().getXpRate() * statRate);
		}
	},
	GROUP_HUNTING {
		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_GROUP_HUNTING_XP_RATE, 100).getCurrent() / 100f;
			return (long) (reward * player.getRates().getXpRate() * statRate);
		}
	},
	PVP_KILL {
		@Override
		public long calcReward(Player player, long reward) {
			return (reward);
		}
	},
	QUEST {
		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_QUEST_XP_RATE, 100).getCurrent() / 100f;
			return (long) (reward * player.getRates().getQuestXpRate() * statRate);
		}
	},
	CRAFTING {
		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_CRAFTING_XP_RATE, 100).getCurrent() / 100f;
			return (long) (reward * statRate);
		}
	},
	GATHERING {
		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_GATHERING_XP_RATE, 100).getCurrent() / 100f;
			return (long) (reward * statRate);
		}
	};
	public abstract long calcReward(Player player, long reward);
}