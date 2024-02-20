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
package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.dataholders.loadingutils.XmlDataLoader;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassAction;
import com.aionemu.gameserver.model.templates.mail.Mails;
import com.aionemu.gameserver.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataManager
{
	static Logger log = LoggerFactory.getLogger(DataManager.class);
	public static NpcData NPC_DATA;
	public static NpcShoutData NPC_SHOUT_DATA;
	public static GatherableData GATHERABLE_DATA;
	public static WorldMapsData WORLD_MAPS_DATA;
	public static TradeListData TRADE_LIST_DATA;
	public static PlayerExperienceTable PLAYER_EXPERIENCE_TABLE;
	public static TeleporterData TELEPORTER_DATA;
	public static TeleLocationData TELELOCATION_DATA;
	public static CubeExpandData CUBEEXPANDER_DATA;
	public static WarehouseExpandData WAREHOUSEEXPANDER_DATA;
	public static BindPointData BIND_POINT_DATA;
	public static QuestsData QUEST_DATA;
	public static XMLQuests XML_QUESTS;
	public static PlayerStatsData PLAYER_STATS_DATA;
	public static SummonStatsData SUMMON_STATS_DATA;
	public static ItemData ITEM_DATA;
	public static TitleData TITLE_DATA;
	public static PlayerInitialData PLAYER_INITIAL_DATA;
	public static SkillData SKILL_DATA;
	public static MotionData MOTION_DATA;
	public static SkillTreeData SKILL_TREE_DATA;
	public static GuideHtmlData GUIDE_HTML_DATA;
	public static WalkerData WALKER_DATA;
	public static ZoneData ZONE_DATA;
	public static GoodsListData GOODSLIST_DATA;
	public static TribeRelationsData TRIBE_RELATIONS_DATA;
	public static RecipeData RECIPE_DATA;
	public static ChestData CHEST_DATA;
	public static StaticDoorData STATICDOOR_DATA;
	public static ItemSetData ITEM_SET_DATA;
	public static NpcFactionsData NPC_FACTIONS_DATA;
	public static NpcSkillData NPC_SKILL_DATA;
	public static PetSkillData PET_SKILL_DATA;
	public static SiegeLocationData SIEGE_LOCATION_DATA;
	public static FlyRingData FLY_RING_DATA;
	public static ShieldData SHIELD_DATA;
	public static PetData PET_DATA;
	public static PetFeedData PET_FEED_DATA;
	public static PetDopingData PET_DOPING_DATA;
	public static RoadData ROAD_DATA;
	public static InstanceCooltimeData INSTANCE_COOLTIME_DATA;
	public static AIData AI_DATA;
	public static FlyPathData FLY_PATH;
	public static WindstreamData WINDSTREAM_DATA;
	public static ItemRestrictionCleanupData ITEM_CLEAN_UP;
	public static AssembledNpcsData ASSEMBLED_NPC_DATA;
	public static CosmeticItemsData COSMETIC_ITEMS_DATA;
	public static ItemGroupsData ITEM_GROUPS_DATA;
	public static SpawnsData2 SPAWNS_DATA2;
	public static AutoGroupData AUTO_GROUP;
	public static EventData EVENT_DATA;
	public static InstanceBuffData INSTANCE_BUFF_DATA;
	public static InstanceExitData INSTANCE_EXIT_DATA;
	public static PortalLocData PORTAL_LOC_DATA;
	public static Portal2Data PORTAL2_DATA;
	public static CuringObjectsData CURING_OBJECTS_DATA;
	public static Mails SYSTEM_MAIL_TEMPLATES;
	public static AbyssBuffData ABYSS_BUFF_DATA;
	public static AbyssGroupData ABYSS_GROUP_DATA;
	public static AbsoluteStatsData ABSOLUTE_STATS_DATA;
	public static MaterialData MATERIAL_DATA;
	public static MapWeatherData MAP_WEATHER_DATA;
	public static RiftData RIFT_DATA;
	public static MailRewardData MAIL_REWARD;
	public static ReviveWorldStartPointsData REVIVE_WORLD_START_POINTS;
	public static ReviveInstanceStartPointsData REVIVE_INSTANCE_START_POINTS;
	public static ItemEnchantChancesData ITEM_ENCHANT_CHANCES_DATA;
	public static DecomposableData DECOMPOSABLE_TEMPLATE_DATA;
	public static BattlePassSeasonData BATTLE_PASS_DATA;
	public static BattlePassQuestData BATTLE_PASS_QUEST_DATA;
	public static BattlePassRewardData BATTLE_PASS_REWARD_DATA;
	public static BattlePassActionData BATTLE_PASS_ACTION_DATA;
	public static EventsWindowData EVENTS_WINDOW;
	public static SerialKillerData SERIAL_KILLER_DATA;
	public static InstanceRiftData INSTANCE_RIFT_DATA;
	public static PanelSkillsData PANEL_SKILL_DATA;
	public static ArcadeUpgradeData ARCADE_UPGRADE_DATA;
	public static AssemblyItemsData ASSEMBLY_ITEM_DATA;
	public static CommonsDropData COMMONS_DROP_DATA;
	public static ShugoSweepRewardData SHUGO_SWEEP_REWARD_DATA;
	private XmlDataLoader loader;
	
	public static final DataManager getInstance() {
		return SingletonHolder.instance;
	}
	
	private DataManager() {
		Util.printSection("Static Data");
		log.info("##### START LOADING STATIC DATA #####");
		this.loader = XmlDataLoader.getInstance();
		long start = System.currentTimeMillis();
		StaticData data = loader.loadStaticData();
		long time = System.currentTimeMillis() - start;
		WORLD_MAPS_DATA = data.worldMapsData;
		PLAYER_EXPERIENCE_TABLE = data.playerExperienceTable;
		PLAYER_STATS_DATA = data.playerStatsData;
		SUMMON_STATS_DATA = data.summonStatsData;
		ITEM_CLEAN_UP = data.itemCleanup;
		ITEM_DATA = data.itemData;
		NPC_DATA = data.npcData;
		NPC_SHOUT_DATA = data.npcShoutData;
		GATHERABLE_DATA = data.gatherableData;
		PLAYER_INITIAL_DATA = data.playerInitialData;
		SKILL_DATA = data.skillData;
		MOTION_DATA = data.motionData;
		SKILL_TREE_DATA = data.skillTreeData;
		TITLE_DATA = data.titleData;
		TRADE_LIST_DATA = data.tradeListData;
		TELEPORTER_DATA = data.teleporterData;
		TELELOCATION_DATA = data.teleLocationData;
		CUBEEXPANDER_DATA = data.cubeExpandData;
		WAREHOUSEEXPANDER_DATA = data.warehouseExpandData;
		BIND_POINT_DATA = data.bindPointData;
		QUEST_DATA = data.questData;
		XML_QUESTS = data.questsScriptData;
		ZONE_DATA = data.zoneData;
		WALKER_DATA = data.walkerData;
		GOODSLIST_DATA = data.goodsListData;
		TRIBE_RELATIONS_DATA = data.tribeRelationsData;
		RECIPE_DATA = data.recipeData;
		CHEST_DATA = data.chestData;
		STATICDOOR_DATA = data.staticDoorData;
		ITEM_SET_DATA = data.itemSetData;
		NPC_FACTIONS_DATA = data.npcFactionsData;
		NPC_SKILL_DATA = data.npcSkillData;
		PET_SKILL_DATA = data.petSkillData;
		SIEGE_LOCATION_DATA = data.siegeLocationData;
		FLY_RING_DATA = data.flyRingData;
		SHIELD_DATA = data.shieldData;
		PET_DATA = data.petData;
		PET_FEED_DATA = data.petFeedData;
		PET_DOPING_DATA = data.petDopingData;
		GUIDE_HTML_DATA = data.guideData;
		ROAD_DATA = data.roadData;
		INSTANCE_COOLTIME_DATA = data.instanceCooltimeData;
		AI_DATA = data.aiData;
		FLY_PATH = data.flyPath;
		WINDSTREAM_DATA = data.windstreamsData;
		ASSEMBLED_NPC_DATA = data.assembledNpcData;
		COSMETIC_ITEMS_DATA = data.cosmeticItemsData;
		SPAWNS_DATA2 = data.spawnsData2;
		ITEM_GROUPS_DATA = data.itemGroupsData;
		AUTO_GROUP = data.autoGroupData;
		EVENT_DATA = data.eventData;
		INSTANCE_BUFF_DATA = data.instanceBuffData;
		INSTANCE_EXIT_DATA = data.instanceExitData;
		PORTAL_LOC_DATA = data.portalLocData;
		PORTAL2_DATA = data.portalTemplate2;
		CURING_OBJECTS_DATA = data.curingObjectsData;
		SYSTEM_MAIL_TEMPLATES = data.systemMailTemplates;
		ITEM_DATA.cleanup();
		ABYSS_BUFF_DATA = data.abyssBuffData;
		ABYSS_GROUP_DATA = data.abyssGroupData;
		ABSOLUTE_STATS_DATA = data.absoluteStatsData;
		MATERIAL_DATA = data.materiaData;
		MAP_WEATHER_DATA = data.mapWeatherData;
		RIFT_DATA = data.riftData;
		MAIL_REWARD = data.mailReward;
		REVIVE_WORLD_START_POINTS = data.reviveWorldStartPoints;
		REVIVE_INSTANCE_START_POINTS = data.reviveInstanceStartPoints;
		ITEM_ENCHANT_CHANCES_DATA = data.itemEnchantChancesData;
		DECOMPOSABLE_TEMPLATE_DATA = data.decomposableTemplateData;
		BATTLE_PASS_DATA = data.battlePassData;
		BATTLE_PASS_QUEST_DATA = data.battlePassQuestData;
		BATTLE_PASS_REWARD_DATA = data.battlePassRewardData;
		BATTLE_PASS_ACTION_DATA = data.battlePassActionData;
		EVENTS_WINDOW = data.eventsWindow;
		SERIAL_KILLER_DATA = data.serialKillerData;
		INSTANCE_RIFT_DATA = data.instanceRiftData;
		PANEL_SKILL_DATA = data.panelSkillsData;
		ARCADE_UPGRADE_DATA = data.arcadeUpgradeData;
		ASSEMBLY_ITEM_DATA = data.assemblyItemData;
		COMMONS_DROP_DATA = data.commonsDropData;
		SHUGO_SWEEP_REWARD_DATA = data.shugoSweepsRewardData;
		String timeMsg = (time / 1000) + " seconds";
		log.info("##### [load time: " + timeMsg + "] #####");
		log.info("##### END LOADING STATIC DATA #####");
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final DataManager instance = new DataManager();
	}
}