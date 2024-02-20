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

import com.aionemu.gameserver.model.templates.mail.Mails;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.model.templates.revive_start_points.*;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ae_static_data")
@XmlAccessorType(XmlAccessType.NONE)
public class StaticData
{
	@XmlElement(name = "world_maps")
	public WorldMapsData worldMapsData;
	@XmlElement(name = "npc_trade_list")
	public TradeListData tradeListData;
	@XmlElement(name = "npc_teleporter")
	public TeleporterData teleporterData;
	@XmlElement(name = "teleport_location")
	public TeleLocationData teleLocationData;
	@XmlElement(name = "bind_points")
	public BindPointData bindPointData;
	@XmlElement(name = "quests")
	public QuestsData questData;
	@XmlElement(name = "quest_scripts")
	public XMLQuests questsScriptData;
	@XmlElement(name = "player_experience_table")
	public PlayerExperienceTable playerExperienceTable;
	@XmlElement(name = "player_stats_templates")
	public PlayerStatsData playerStatsData;
	@XmlElement(name = "summon_stats_templates")
	public SummonStatsData summonStatsData;
	@XmlElement(name = "item_templates")
	public ItemData itemData;
	@XmlElement(name = "npc_templates")
	public NpcData npcData;
	@XmlElement(name = "npc_shouts")
	public NpcShoutData npcShoutData;
	@XmlElement(name = "player_initial_data")
	public PlayerInitialData playerInitialData;
	@XmlElement(name = "skill_data")
	public SkillData skillData;
	@XmlElement(name = "motion_times")
	public MotionData motionData;
	@XmlElement(name = "skill_tree")
	public SkillTreeData skillTreeData;
	@XmlElement(name = "cube_expander")
	public CubeExpandData cubeExpandData;
	@XmlElement(name = "warehouse_expander")
	public WarehouseExpandData warehouseExpandData;
	@XmlElement(name = "player_titles")
	public TitleData titleData;
	@XmlElement(name = "gatherable_templates")
	public GatherableData gatherableData;
	@XmlElement(name = "npc_walker")
	public WalkerData walkerData;
	@XmlElement(name = "zones")
	public ZoneData zoneData;
	@XmlElement(name = "goodslists")
	public GoodsListData goodsListData;
	@XmlElement(name = "tribe_relations")
	public TribeRelationsData tribeRelationsData;
	@XmlElement(name = "recipe_templates")
	public RecipeData recipeData;
	@XmlElement(name = "chest_templates")
	public ChestData chestData;
	@XmlElement(name = "staticdoor_templates")
	public StaticDoorData staticDoorData;
	@XmlElement(name = "item_sets")
	public ItemSetData itemSetData;
	@XmlElement(name = "npc_factions")
	public NpcFactionsData npcFactionsData;
	@XmlElement(name = "npc_skill_templates")
	public NpcSkillData npcSkillData;
	@XmlElement(name = "pet_skill_templates")
	public PetSkillData petSkillData;
	@XmlElement(name = "siege_locations")
	public SiegeLocationData siegeLocationData;
	@XmlElement(name = "fly_rings")
	public FlyRingData flyRingData;
	@XmlElement(name = "shields")
	public ShieldData shieldData;
	@XmlElement(name = "pets")
	public PetData petData;
	@XmlElement(name = "pet_feed")
	public PetFeedData petFeedData;
	@XmlElement(name = "dopings")
	public PetDopingData petDopingData;
	@XmlElement(name = "guides")
	public GuideHtmlData guideData;
	@XmlElement(name = "roads")
	public RoadData roadData;
	@XmlElement(name = "instance_cooltimes")
	public InstanceCooltimeData instanceCooltimeData;
	@XmlElement(name = "ai_templates")
	public AIData aiData;
	@XmlElement(name = "flypath_template")
	public FlyPathData flyPath;
	@XmlElement(name = "windstreams")
	public WindstreamData windstreamsData;
	@XmlElement(name = "item_restriction_cleanups")
	public ItemRestrictionCleanupData itemCleanup;
	@XmlElement(name = "assembled_npcs")
	public AssembledNpcsData assembledNpcData;
	@XmlElement(name = "cosmetic_items")
	public CosmeticItemsData cosmeticItemsData;
	@XmlElement(name = "auto_groups")
	public AutoGroupData autoGroupData;
	@XmlElement(name = "events_config")
	public EventData eventData;
	@XmlElement(name = "spawns")
	public SpawnsData2 spawnsData2;
	@XmlElement(name = "item_groups")
	public ItemGroupsData itemGroupsData;
	@XmlElement(name = "instance_bonusattrs")
	public InstanceBuffData instanceBuffData;
	@XmlElement(name = "instance_exits")
	public InstanceExitData instanceExitData;
	@XmlElement(name = "portal_locs")
	PortalLocData portalLocData;
	@XmlElement(name = "portal_templates2")
	Portal2Data portalTemplate2;
	@XmlElement(name = "curing_objects")
	public CuringObjectsData curingObjectsData;
	@XmlElement(name = "mails")
	public Mails systemMailTemplates;
	@XmlElement(name = "abyss_bonusattrs")
	public AbyssBuffData abyssBuffData;
	@XmlElement(name = "abyss_groupattrs")
	public AbyssGroupData abyssGroupData;
	@XmlElement(name = "absolute_stats")
	public AbsoluteStatsData absoluteStatsData;
	@XmlElement(name = "material_templates")
	public MaterialData materiaData;
	@XmlElement(name = "weather")
	public MapWeatherData mapWeatherData;
	@XmlElement(name = "rift_locations")
	public RiftData riftData;
	@XmlElement(name = "reward_mail_templates")
	public MailRewardData mailReward;
	@XmlElement(name = "revive_world_start_points")
	public ReviveWorldStartPointsData reviveWorldStartPoints;
	@XmlElement(name = "instance_revive_start_points")
	public ReviveInstanceStartPointsData reviveInstanceStartPoints;
	@XmlElement(name = "enchant_chances")
	public ItemEnchantChancesData itemEnchantChancesData;
	@XmlElement(name = "decomposable_templates")
	public DecomposableData decomposableTemplateData;
	@XmlElement(name = "battlepass_seasons")
	public BattlePassSeasonData battlePassData;
	@XmlElement(name = "battlepass_quests")
	public BattlePassQuestData battlePassQuestData;
	@XmlElement(name = "battlepass_rewards")
	public BattlePassRewardData battlePassRewardData;
	@XmlElement(name = "battlepass_actions")
	public BattlePassActionData battlePassActionData;
	@XmlElement(name="events_window")
	public EventsWindowData eventsWindow;
	@XmlElement(name = "serial_killers")
	public SerialKillerData serialKillerData;
	@XmlElement(name = "instance_rift")
	public InstanceRiftData instanceRiftData;
	@XmlElement(name = "polymorph_panels")
	public PanelSkillsData panelSkillsData;
	@XmlElement(name = "arcadelist")
	public ArcadeUpgradeData arcadeUpgradeData;
	@XmlElement(name = "assembly_items")
	public AssemblyItemsData assemblyItemData;
	@XmlElement(name = "commons_drop_items")
	public CommonsDropData commonsDropData;
	@XmlElement(name = "shugo_sweeps")
	public ShugoSweepRewardData shugoSweepsRewardData;
}