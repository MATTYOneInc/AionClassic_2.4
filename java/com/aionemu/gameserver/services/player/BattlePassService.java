package com.aionemu.gameserver.services.player;

import com.aionemu.gameserver.dao.PlayerBattlePassDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.BattlePassQuest;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.BattlePassReward;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.BattlePassSeason;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.BattleQuestState;
import com.aionemu.gameserver.model.templates.battle_pass.*;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class BattlePassService
{
    private Logger log = LoggerFactory.getLogger(BattlePassService.class);
    List<BattlePassSeasonTemplate> season = new FastList<BattlePassSeasonTemplate>();
    Map<Integer, List<BattlePassQuestTemplate>> seasonQuest = new FastMap<Integer, List<BattlePassQuestTemplate>>();

    private Timestamp start;
    private Timestamp end;
    private Timestamp endWeekly;

    public void init() {
        InitBattlePass();
        InitResetTime();
        log.info("Daeva Pass Start");
    }

    public void InitBattlePass() {
        DataManager.BATTLE_PASS_DATA.getAllTemplate().forEach((template) -> {
            if (template.getStartDate().isBeforeNow() && template.getEndDate().isAfterNow()) {
                season.add(template);
            }
        });
        DataManager.BATTLE_PASS_QUEST_DATA.getAllTemplate().forEach((template) -> {
            this.season.forEach((season) -> {
                if (season.getId() == template.getPassId()) {
                    if (seasonQuest.containsKey(season.getId())) {
                        seasonQuest.get(season.getId()).add(template);
                    } else {
                        List<BattlePassQuestTemplate> list = new FastList<BattlePassQuestTemplate>();
                        list.add(template);
                        seasonQuest.put(season.getId(), list);
                    }
                }
            });
        });
    }

    public void onEnterWorld(Player player) {
        player.getPlayerBattlePass().setBattlePassQuest(PlayerBattlePassDAO.loadPlayerBattleQuest(player));
        player.getPlayerBattlePass().setBattlePassSeason(PlayerBattlePassDAO.loadPlayerBattlePass(player));
        this.season.forEach((season) -> {
            if (!player.getPlayerBattlePass().getBattlePassSeason().containsKey(season.getId())) {
                AddSeasonToPlayer(player, season.getId());
            }
            this.seasonQuest.get(season.getId()).forEach((quests) -> {
                if (!player.getPlayerBattlePass().haveQuest(quests.getId())) {
                    Timestamp endQuest = new Timestamp(System.currentTimeMillis());
                    if (quests.getType() == BattleQuestType.DAILY) {
                        endQuest = this.end;
                    } else if (quests.getType() == BattleQuestType.WEEKLY) {
                        endQuest = this.endWeekly;
                    } else {
                        endQuest = new Timestamp(season.getEndDate().getMillis());
                    }
                    BattlePassQuest battlePassQuest = new BattlePassQuest(IDFactory.getInstance().nextId(), quests.getId(), quests.getType(), BattleQuestState.START, 0, this.start, endQuest);
                    if (quests.getRace() == Race.PC_ALL || quests.getRace() == player.getRace()) {
                        player.getPlayerBattlePass().getBattlePassQuest().put(battlePassQuest.getObjectId(), battlePassQuest);
                        PlayerBattlePassDAO.storeQuest(player, battlePassQuest);
                    }
                }
            });
        });
        PacketSendUtility.sendPacket(player, new S_CLEAR_ACHIEVEMENT_EVENT());
        PacketSendUtility.sendPacket(player, new S_CREATE_ACHIEVEMENT_EVENT(player));
        PacketSendUtility.sendPacket(player, new S_BATTLEPASS_LIST(player));
    }

    public void onUpdateBattlePassMission(Player player, int value, int count, BattlePassAction type) {
        List<BattlePassQuest> questUpdateList = new FastList<BattlePassQuest>();
        List<BattlePassQuest> questFinishList = new FastList<BattlePassQuest>();
        player.getPlayerBattlePass().getBattlePassQuest().forEach((id, quest) -> {
            if (quest.getActionTemplate().getType() == type && quest.getState() == BattleQuestState.START) {
                int progress = quest.getStep() + count;
                questUpdateList.add(quest);
                if (quest.getActionTemplate().getRequired().getType() == ActionRequiredType.SPECIFIC) {
                    quest.getActionTemplate().getRequired().getValues().forEach((ids -> {
                        if (ids == value) {
                            quest.setStep(progress);
                        }
                    }));
                } else {
                    quest.setStep(progress);
                } if (quest.getStep() >= quest.getActionTemplate().getCount()) {
                    quest.setStep(quest.getActionTemplate().getCount());
                    questFinishList.add(quest);
                } else {
                    PlayerBattlePassDAO.updateQuest(player, quest);
                }
            }
        });
        PacketSendUtility.sendPacket(player, new S_PROGRESS_ACHIEVEMENT(questUpdateList));
        onFinishQuest(player, questFinishList);
    }

    public void OnEnchant(Player player, int enchantLevel) {
        List<BattlePassQuest> questFinishList = new FastList<BattlePassQuest>();
        player.getPlayerBattlePass().getBattlePassQuest().forEach((id, quest) -> {
            if (quest.getActionTemplate().getType() == BattlePassAction.SUCCESS_ENCHANT && quest.getState() == BattleQuestState.START) {
                if (enchantLevel >= quest.getActionTemplate().getEnchant().getLevel()) {
                    quest.setStep(quest.getActionTemplate().getCount());
                    questFinishList.add(quest);
                }
            }
        });
        onFinishQuest(player, questFinishList);
    }

    public void onWinAp(Player player, int ap) {
        List<BattlePassQuest> questFinishList = new FastList<BattlePassQuest>();
        List<BattlePassQuest> questUpdateList = new FastList<BattlePassQuest>();
        player.getPlayerBattlePass().getBattlePassQuest().forEach((id, quest) -> {
            if (quest.getActionTemplate().getType() == BattlePassAction.CHANGE_AP && quest.getState() == BattleQuestState.START) {
                int newValue = ap + quest.getStep();
                quest.setStep(newValue);
                if (quest.getStep() >= quest.getActionTemplate().getCount()) {
                    questFinishList.add(quest);
                } else {
                    questUpdateList.add(quest);
                    PlayerBattlePassDAO.updateQuest(player, quest);
                }
            }
        });
        PacketSendUtility.sendPacket(player, new S_PROGRESS_ACHIEVEMENT(questUpdateList));
        onFinishQuest(player, questFinishList);
    }

    public void onFinishQuest(Player player, List<BattlePassQuest> quests) {
        List<BattlePassQuest> questList = new FastList<BattlePassQuest>();
        quests.forEach((quest) -> {
            quest.setState(BattleQuestState.REWARD);
            PlayerBattlePassDAO.updateQuest(player, quest);
            questList.add(quest);
        });
        PacketSendUtility.sendPacket(player, new S_PROGRESS_ACHIEVEMENT(questList));
        onUpdatePass(player, questList);
    }

    public void onGetReward(Player player, int passId) {
        if (player.getInventory().isFull() || player.getInventory().isFullSpecialCube()) {
            ///Your inventory is full. Try again after making space.
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DEVAPASS_REWARD_INVENTORY_FULL);
            return;
        }
        BattlePassSeason season = player.getPlayerBattlePass().getBattlePassSeason().get(passId);
        season.getRewards().forEach((id, battlePassReward) -> {
            if (battlePassReward.getTemplate().getLevel() <= season.getLevel()) {
                if (!battlePassReward.isRewarded()) {
                    battlePassReward.setRewarded(true);
                    if (player.getRace() == Race.ELYOS) {
                        battlePassReward.getTemplate().getLightBasic().forEach((passReward -> ItemService.addItem(player, passReward.getItemId(), passReward.getCount())));
                    } else {
                        battlePassReward.getTemplate().getDarkBasic().forEach((passReward -> ItemService.addItem(player, passReward.getItemId(), passReward.getCount())));
                    }
                } if (player.getPlayerAccount().getMembership() == 2) {
                    if (!battlePassReward.isUnlockReward()) {
                        battlePassReward.setUnlockReward(true);
                        if (player.getRace() == Race.ELYOS) {
                            battlePassReward.getTemplate().getLightUnlock().forEach((passReward -> ItemService.addItem(player, passReward.getItemId(), passReward.getCount())));
                        } else {
                            battlePassReward.getTemplate().getDarkUnlock().forEach((passReward -> ItemService.addItem(player, passReward.getItemId(), passReward.getCount())));
                        }
                    }
                }
                PlayerBattlePassDAO.updateReward(player, battlePassReward);
            }
        });
        PacketSendUtility.sendPacket(player, new S_BATTLEPASS_UPDATED(season));
    }

    public void onUpdatePass(Player player, List<BattlePassQuest> quests) {
        quests.forEach((quest) -> {
            BattlePassSeason season = player.getPlayerBattlePass().getBattlePassSeason().get(quest.getSeasonId());
            long exp = season.getExp() + quest.getTemplate().getCompleteExp(); //(50 + 200 ) = 250
            if (exp >= 100) {
                if (season.getLevel() < season.getTemplate().getMaxLevel() - 1) {
                    season.setExp(exp);
                    onLevelUpPass(player, season);
                } else if (season.getLevel() == season.getTemplate().getMaxLevel()) {
                    season.setLevel(season.getTemplate().getMaxLevel());
                    season.setExp(100);
                    PlayerBattlePassDAO.updateSeason(player, season);
                    PacketSendUtility.sendPacket(player, new S_BATTLEPASS_UPDATED(season));
                }
            } else {
                season.setExp(exp);
                PlayerBattlePassDAO.updateSeason(player, season);
                PacketSendUtility.sendPacket(player, new S_BATTLEPASS_UPDATED(season));
            }
            quest.setState(BattleQuestState.COMPLETE);
            PlayerBattlePassDAO.updateQuest(player, quest);
            PacketSendUtility.sendPacket(player, new S_REWARD_ACHIEVEMENT_EVENT_RESULT(quest));
        });
    }

    public void onLevelUpPass(Player player, BattlePassSeason season) {
        int level = 0;
        if (season.getExp() >= 100) {
            level += 1;
            season.setExp(season.getExp() - 100);
            if (season.getExp() >= 100) {
                level += 1;
                season.setExp(season.getExp() - 100);
            } if (season.getExp() >= 100) {
                level += 1;
                season.setExp(season.getExp() - 100);
            } if (season.getExp() >= 100) {
                level += 1;
                season.setExp(season.getExp() - 100);
            } if (season.getExp() >= 100) {
                level += 1;
                season.setExp(season.getExp() - 100);
            }
            season.setLevel(season.getLevel() + level);
            PlayerBattlePassDAO.updateSeason(player, season);
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DEVAPASS_LEVEL_UP(season.getLevel()));
            PacketSendUtility.sendPacket(player, new S_BATTLEPASS_UPDATED(season));
        } else {
            season.setLevel(season.getLevel() + 1);
            PlayerBattlePassDAO.updateSeason(player, season);
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DEVAPASS_LEVEL_UP(season.getLevel()));
            PacketSendUtility.sendPacket(player, new S_BATTLEPASS_UPDATED(season));
        }
    }

    public void AddSeasonToPlayer(Player player, int id) {
        BattlePassSeasonTemplate season = DataManager.BATTLE_PASS_DATA.getSeasonById(id);
        BattlePassSeason battlePassSeason = new BattlePassSeason(season.getId(), 1, 0);
        player.getPlayerBattlePass().getBattlePassSeason().put(battlePassSeason.getId(), battlePassSeason);
        DataManager.BATTLE_PASS_REWARD_DATA.getRewardBySeasonId(battlePassSeason.getId()).forEach((rewards) -> {
            BattlePassReward reward = new BattlePassReward(rewards.getId(), false, false);
            player.getPlayerBattlePass().getBattlePassSeason().get(battlePassSeason.getId()).getRewards().put(rewards.getLevel(), reward);
            PlayerBattlePassDAO.storeReward(player, reward, season.getId());
        });
        PlayerBattlePassDAO.storeSeason(player, battlePassSeason);
    }

    public void onRestart() {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            PlayerBattlePassDAO.deleteQuest(BattleQuestType.WEEKLY);
            PlayerBattlePassDAO.deleteQuest(BattleQuestType.DAILY);
        } else {
            PlayerBattlePassDAO.deleteQuest(BattleQuestType.DAILY);
        }
    }

    public void InitResetTime() {
        this.start = new Timestamp(System.currentTimeMillis());
        //Daily reset 9h00 tommorow
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_WEEK, 1);
        cal.set(Calendar.HOUR, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //Weekly reset 9h00 WEDNESDAY
        this.end = new Timestamp(cal.getTimeInMillis());
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        cal2.set(Calendar.HOUR, 9);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        this.endWeekly = new Timestamp(cal2.getTimeInMillis());
    }

    public static BattlePassService getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final BattlePassService instance = new BattlePassService();
    }
}
