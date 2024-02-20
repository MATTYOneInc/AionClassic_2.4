package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.controllers.FlyController;
import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.attack.PlayerAggroList;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.controllers.movement.PlayerMoveController;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerVarsDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.AccountSielEnergy;
import com.aionemu.gameserver.model.actions.PlayerActions;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.blackcloud.BlackcloudLetter;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank.AbyssRankUpdateType;
import com.aionemu.gameserver.model.gameobjects.player.FriendList.Status;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.PlayerBattlePass;
import com.aionemu.gameserver.model.gameobjects.player.emotion.EmotionList;
import com.aionemu.gameserver.model.gameobjects.player.equipmentset.EquipmentSettingList;
import com.aionemu.gameserver.model.gameobjects.player.motion.MotionList;
import com.aionemu.gameserver.model.gameobjects.player.npcFaction.NpcFactions;
import com.aionemu.gameserver.model.gameobjects.player.title.TitleList;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.ingameshop.InGameShop;
import com.aionemu.gameserver.model.items.ItemCooldown;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.LegionStorageProxy;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionJoinRequestState;
import com.aionemu.gameserver.model.team.legion.LegionMember;
import com.aionemu.gameserver.model.team2.TeamMember;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceGroup;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.BoundRadius;
import com.aionemu.gameserver.model.templates.decomposable.DecomposableItemList;
import com.aionemu.gameserver.model.templates.flypath.FlyPathEntry;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.ItemUseLimits;
import com.aionemu.gameserver.model.templates.npc.AbyssNpcType;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.model.templates.windstreams.WindstreamPath;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.S_STATUS;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.serialkillers.SerialKiller;
import com.aionemu.gameserver.skillengine.condition.ChainCondition;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.effect.RebirthEffect;
import com.aionemu.gameserver.skillengine.effect.ResurrectBaseEffect;
import com.aionemu.gameserver.skillengine.model.ChainSkills;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.task.CraftingTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.rates.Rates;
import com.aionemu.gameserver.utils.rates.RegularRates;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import javolution.util.FastList;
import javolution.util.FastMap;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends Creature
{
	public InRoll inRoll;
	public InGameShop inGameShop;
	public WindstreamPath windstreamPath;
	private PlayerAppearance playerAppearance;
	private PlayerAppearance savedPlayerAppearance;
	private PlayerCommonData playerCommonData;
	private Account playerAccount;
	private LegionMember legionMember;
	private MacroList macroList;
	private PlayerSkillList skillList;
	private FriendList friendList;
	private BlockList blockList;
	private PetList toyPetList;
	private Mailbox mailbox;
	private PrivateStore store;
	private TitleList titleList;
	private QuestStateList questStateList;
	private RecipeList recipeList;
	private ResponseRequester requester;
	private boolean lookingForGroup = false;
	private Storage inventory;
	private Storage[] petBag = new Storage[StorageType.PET_BAG_MAX - StorageType.PET_BAG_MIN + 1];
	private Storage regularWarehouse;
	private Storage accountWarehouse;
	private Equipment equipment;
	private PlayerStatsTemplate playerStatsTemplate;
	private final AbsoluteStatOwner absStatsHolder;
	private PlayerSettings playerSettings;
	private com.aionemu.gameserver.model.team2.group.PlayerGroup playerGroup2;
	private PlayerAllianceGroup playerAllianceGroup;
	private AbyssRank abyssRank;
	private NpcFactions npcFactions;
	private Rates rates;
	private int flyState = 0;
	private boolean isTrading;
	private long prisonTimer = 0;
	private boolean isGathering;
	private long startPrison;
	private boolean invul;
	private FlyController flyController;
	private CraftingTask craftingTask;
	private int flightTeleportId;
	private int flightDistance;
	private Summon summon;
	private Pet toyPet;
	private Kisk kisk;
	private boolean isResByPlayer = false;
	private int resurrectionSkill = 0;
	private boolean isFlyingBeforeDeath = false;
	private boolean isGagged = false;
	private boolean edit_mode = false;
	private Npc questFollowingNpc = null;
	private Npc postman = null;
	private boolean isInResurrectPosState = false;
	private float resPosX = 0;
	private float resPosY = 0;
	private float resPosZ = 0;
	private boolean underNoFPConsum = false;
	private boolean isAdminTeleportation = false;
	private boolean cooldownZero = false;
	private boolean isUnderInvulnerableWing = false;
	private boolean isFlying = false;
	private boolean isWispable = true;
	private boolean isCommandUsed = false;
	private int abyssRankListUpdateMask = 0;
	private BindPointPosition bindPoint;
	private Map<Integer, ItemCooldown> itemCoolDowns;
	private PortalCooldownList portalCooldownList;
	private CraftCooldownList craftCooldownList;
	private long nextSkillUse;
	private long nextSummonSkillUse;
	private ChainSkills chainSkills;
	private Map<AttackStatus, Long>	lastCounterSkill = new HashMap<AttackStatus, Long>();
	private int dualEffectValue = 0;
	private int rawKillcount = 0;
	private int spreeLevel = 0;
	private boolean hasBonus;
	private int bonusId = 0;
	private boolean hasAbyssBonus;
	private int abyssId = 0;
	private static final int CUBE_SPACE = 9;
	private static final int WAREHOUSE_SPACE = 8;
	private boolean isAttackMode = false;
	private long gatherableTimer = 0;
	private long stopGatherable;
	private String captchaWord;
	private byte[] captchaImage;
	private float instanceStartPosX, instanceStartPosY, instanceStartPosZ;
	private int rebirthResurrectPercent = 1;
	private int rebirthSkill = 0;
	private AionConnection clientConnection;
	private FlyPathEntry flyLocationId;
	private long flyStartTime;
	private EmotionList emotions;
	private MotionList motions;
	private int partnerId;
	private long flyReuseTime;
	private boolean isMentor;
	private long lastMsgTime = 0;
	private int floodMsgCount = 0;
	private long onlineTime = 0;
	private int lootingNpcOid;
	private boolean rebirthRevive;
	private int subtractedSupplementsCount;
	private int subtractedSupplementId;
	private int portAnimation;
	private boolean isInSprintMode;
	private int battleReturnMap;
	private float[] battleReturnCoords;
	public int speedHackCounter;
	public int abnormalHackCounter;
	public WorldPosition prevPos;
	public long prevPosUT;
	public byte prevMoveType;
	private Map<String, Object> vars = FastMap.newInstance();
	private int playersBonusId = 0;
	private int transformModelId;
	private int transformItemId;
	private int transformPanelId;
	private boolean isInWindstream = false;
	private int silenceReportCount = 0;
	private boolean enchantBoost;
	private boolean authorizeBoost;
	private int enchantBoostValue;
	private int authorizeBoostValue;
	private SerialKiller skList;
	private PlayerBattlePass playerBattlePass;
	private Map<Integer, PlayerEventWindow> playerEventWindow = new FastMap<Integer, PlayerEventWindow>();
	private AccountSielEnergy accountSielEnergy;
	private PlayerBoostEvent playerBoostEvent;

	private Player(PlayerCommonData plCommonData) {
		super(plCommonData.getPlayerObjId(), new PlayerController(), null, plCommonData, null);
		this.playerCommonData = plCommonData;
		this.playerAccount = new Account(0);
		this.absStatsHolder = new AbsoluteStatOwner(this, 0);
	}

	public Player(PlayerController controller, PlayerCommonData plCommonData, PlayerAppearance appereance, Account account) {
		super(plCommonData.getPlayerObjId(), controller, null, plCommonData, plCommonData.getPosition());
		this.playerCommonData = plCommonData;
		this.playerAppearance = appereance;
		this.playerAccount = account;
		this.requester = new ResponseRequester(this);
		this.questStateList = new QuestStateList();
		this.titleList = new TitleList();
		this.portalCooldownList = new PortalCooldownList(this);
		this.craftCooldownList = new CraftCooldownList(this);
		this.toyPetList = new PetList(this);
		controller.setOwner(this);
		moveController = new PlayerMoveController(this);
		plCommonData.setBoundingRadius(new BoundRadius(0.5f, 0.5f, getPlayerAppearance().getHeight()));
		setPlayerStatsTemplate(DataManager.PLAYER_STATS_DATA.getTemplate(this));
		setGameStats(new PlayerGameStats(this));
		setLifeStats(new PlayerLifeStats(this));
		inGameShop = new InGameShop();
		absStatsHolder = new AbsoluteStatOwner(this, 0);
		playerBattlePass = new PlayerBattlePass();
		skList = new SerialKiller(this);
		this.playerBoostEvent = new PlayerBoostEvent();
	}

	public boolean isInPlayerMode(PlayerMode mode) {
		return PlayerActions.isInPlayerMode(this, mode);
	}

	public void setPlayerMode(PlayerMode mode, Object obj) {
		PlayerActions.setPlayerMode(this, mode, obj);
	}

	public void unsetPlayerMode(PlayerMode mode) {
		PlayerActions.unsetPlayerMode(this, mode);
	}
	@Override
	public PlayerMoveController getMoveController() {
		return (PlayerMoveController) super.getMoveController();
	}

	@Override
	protected final AggroList createAggroList() {
		return new PlayerAggroList(this);
	}

	public PlayerCommonData getCommonData() {
		return playerCommonData;
	}

	@Override
	public String getName() {
		return playerCommonData.getName();
	}

	public PlayerAppearance getPlayerAppearance() {
		return playerAppearance;
	}

	public void setPlayerAppearance(PlayerAppearance playerAppearance) {
		this.playerAppearance = playerAppearance;
	}

	/**
	 * Only use for the Size admin command
	 *
	 * @return PlayerAppearance : The saved player's appearance, to rollback his appearance
	 */
	public PlayerAppearance getSavedPlayerAppearance() {
		return savedPlayerAppearance;
	}

	/**
	 * Only use for the Size admin command
	 *
	 * @param playerAppearance
	 *          PlayerAppearance : The saved player's appearance, to rollback his appearance
	 */
	public void setSavedPlayerAppearance(PlayerAppearance savedPlayerAppearance) {
		this.savedPlayerAppearance = savedPlayerAppearance;
	}

	/**
	 * Set connection of this player.
	 *
	 * @param clientConnection
	 */
	public void setClientConnection(AionConnection clientConnection) {
		this.clientConnection = clientConnection;
	}

	/**
	 * Get connection of this player.
	 *
	 * @return AionConnection of this player.
	 */
	public AionConnection getClientConnection() {
		return this.clientConnection;
	}

	public MacroList getMacroList() {
		return macroList;
	}

	public void setMacroList(MacroList macroList) {
		this.macroList = macroList;
	}

	public PlayerSkillList getSkillList() {
		return skillList;
	}

	public void setSkillList(PlayerSkillList skillList) {
		this.skillList = skillList;
	}

	/**
	 * @return the toyPet
	 */
	public Pet getPet() {
		return toyPet;
	}

	/**
	 * @param toyPet
	 *          the toyPet to set
	 */
	public void setToyPet(Pet toyPet) {
		this.toyPet = toyPet;
	}

	/**
	 * Gets this players Friend List
	 *
	 * @return FriendList
	 */
	public FriendList getFriendList() {
		return friendList;
	}

	/**
	 * Is this player looking for a group
	 *
	 * @return true or false
	 */
	public boolean isLookingForGroup() {
		return lookingForGroup;
	}

	/**
	 * Sets whether or not this player is looking for a group
	 *
	 * @param lookingForGroup
	 */
	public void setLookingForGroup(boolean lookingForGroup) {
		this.lookingForGroup = lookingForGroup;
	}

	public boolean isAttackMode() {
		return isAttackMode;
	}

	public void setAttackMode(boolean isAttackMode) {
		this.isAttackMode = isAttackMode;
	}

	public boolean isNotGatherable() {
		return gatherableTimer != 0;
	}

	public void setGatherableTimer(long gatherableTimer) {
		if (gatherableTimer < 0)
			gatherableTimer = 0;

		this.gatherableTimer = gatherableTimer;
	}

	public long getGatherableTimer() {
		return gatherableTimer;
	}

	public long getStopGatherable() {
		return stopGatherable;
	}

	public void setStopGatherable(long stopGatherable) {
		this.stopGatherable = stopGatherable;
	}

	public String getCaptchaWord() {
		return captchaWord;
	}

	public void setCaptchaWord(String captchaWord) {
		this.captchaWord = captchaWord;
	}

	public byte[] getCaptchaImage() {
		return captchaImage;
	}

	public void setCaptchaImage(byte[] captchaImage) {
		this.captchaImage = captchaImage;
	}

	/**
	 * Sets this players friend list. <br/>
	 * Remember to send the player the <tt>SM_FRIEND_LIST</tt> packet.
	 *
	 * @param list
	 */
	public void setFriendList(FriendList list) {
		this.friendList = list;
	}

	public BlockList getBlockList() {
		return blockList;
	}

	public void setBlockList(BlockList list) {
		this.blockList = list;
	}

	public final PetList getPetList() {
		return toyPetList;
	}

	@Override
	public PlayerLifeStats getLifeStats() {
		return (PlayerLifeStats) super.getLifeStats();
	}

	@Override
	public PlayerGameStats getGameStats() {
		return (PlayerGameStats) super.getGameStats();
	}

	/**
	 * Gets the ResponseRequester for this player
	 *
	 * @return ResponseRequester
	 */
	public ResponseRequester getResponseRequester() {
		return requester;
	}

	public boolean isOnline() {
		return getClientConnection() != null;
	}

	public void setQuestExpands(int questExpands) {
		this.playerCommonData.setQuestExpands(questExpands);
		getInventory().setLimit(getInventory().getLimit() + (questExpands + getNpcExpands()) * CUBE_SPACE);
	}

	public int getQuestExpands() {
		return this.playerCommonData.getQuestExpands();
	}

	public void setNpcExpands(int npcExpands) {
		this.playerCommonData.setNpcExpands(npcExpands);
		getInventory().setLimit(getInventory().getLimit() + (npcExpands + getQuestExpands()) * CUBE_SPACE);
	}

	public int getNpcExpands() {
		return this.playerCommonData.getNpcExpands();
	}

	public PlayerClass getPlayerClass() {
		return playerCommonData.getPlayerClass();
	}

	public Gender getGender() {
		return playerCommonData.getGender();
	}

	/**
	 * Return PlayerController of this Player Object.
	 *
	 * @return PlayerController.
	 */
	@Override
	public PlayerController getController() {
		return (PlayerController) super.getController();
	}

	@Override
	public int getLevel() {
		return playerCommonData.getLevel();
	}

	/**
	 * @return the inventory
	 */

	public Equipment getEquipment() {
		return equipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	/**
	 * @return the player private store
	 */
	public PrivateStore getStore() {
		return store;
	}

	/**
	 * @param store
	 *          the store that needs to be set
	 */
	public void setStore(PrivateStore store) {
		this.store = store;
	}

	/**
	 * @return the questStatesList
	 */
	public QuestStateList getQuestStateList() {
		return questStateList;
	}

	/**
	 * @param questStateList
	 *          the QuestStateList to set
	 */
	public void setQuestStateList(QuestStateList questStateList) {
		this.questStateList = questStateList;
	}

	/**
	 * @return the playerStatsTemplate
	 */
	public PlayerStatsTemplate getPlayerStatsTemplate() {
		return playerStatsTemplate;
	}

	/**
	 * @param playerStatsTemplate
	 *          the playerStatsTemplate to set
	 */
	public void setPlayerStatsTemplate(PlayerStatsTemplate playerStatsTemplate) {
		this.playerStatsTemplate = playerStatsTemplate;
	}

	public RecipeList getRecipeList() {
		return recipeList;
	}

	public void setRecipeList(RecipeList recipeList) {
		this.recipeList = recipeList;
	}

	/**
	 * @param inventory
	 *          the inventory to set Inventory should be set right after player object is created
	 */
	public void setStorage(Storage storage, StorageType storageType) {
		if (storageType == StorageType.CUBE) {
			this.inventory = storage;
		}
		if (storageType.getId() >= StorageType.PET_BAG_MIN && storageType.getId() <= StorageType.PET_BAG_MAX) {
			this.petBag[storageType.getId() - StorageType.PET_BAG_MIN] = storage;
		}
		if (storageType == StorageType.REGULAR_WAREHOUSE) {
			this.regularWarehouse = storage;
		}
		if (storageType == StorageType.ACCOUNT_WAREHOUSE) {
			this.accountWarehouse = storage;
		}
		storage.setOwner(this);
	}

	/**
	 * @param storageType
	 * @return
	 */
	public IStorage getStorage(int storageType) {
		if (storageType == StorageType.REGULAR_WAREHOUSE.getId())
			return regularWarehouse;

		if (storageType == StorageType.ACCOUNT_WAREHOUSE.getId())
			return accountWarehouse;

		if (storageType == StorageType.LEGION_WAREHOUSE.getId() && getLegion() != null) {
			return new LegionStorageProxy(getLegion().getLegionWarehouse(), this);
		}

		if (storageType >= StorageType.PET_BAG_MIN && storageType <= StorageType.PET_BAG_MAX)
			return petBag[storageType - StorageType.PET_BAG_MIN];

		if (storageType == StorageType.CUBE.getId())
			return inventory;
		return null;
	}

	/**
	 * Items from UPDATE_REQUIRED storages and equipment
	 *
	 * @return
	 */
	public List<Item> getDirtyItemsToUpdate() {
		List<Item> dirtyItems = new ArrayList<Item>();

		IStorage cubeStorage = getStorage(StorageType.CUBE.getId());
		if (cubeStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
			dirtyItems.addAll(cubeStorage.getItemsWithKinah());
			dirtyItems.addAll(cubeStorage.getDeletedItems());
			cubeStorage.setPersistentState(PersistentState.UPDATED);
		}

		IStorage regularWhStorage = getStorage(StorageType.REGULAR_WAREHOUSE.getId());
		if (regularWhStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
			dirtyItems.addAll(regularWhStorage.getItemsWithKinah());
			dirtyItems.addAll(regularWhStorage.getDeletedItems());
			regularWhStorage.setPersistentState(PersistentState.UPDATED);
		}

		IStorage accountWhStorage = getStorage(StorageType.ACCOUNT_WAREHOUSE.getId());
		if (accountWhStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
			dirtyItems.addAll(accountWhStorage.getItemsWithKinah());
			dirtyItems.addAll(accountWhStorage.getDeletedItems());
			accountWhStorage.setPersistentState(PersistentState.UPDATED);
		}

		IStorage legionWhStorage = getStorage(StorageType.LEGION_WAREHOUSE.getId());
		if (legionWhStorage != null) {
			if (legionWhStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
				dirtyItems.addAll(legionWhStorage.getItemsWithKinah());
				dirtyItems.addAll(legionWhStorage.getDeletedItems());
				legionWhStorage.setPersistentState(PersistentState.UPDATED);
			}
		}
		for (int petBagId = StorageType.PET_BAG_MIN; petBagId <= StorageType.PET_BAG_MAX; petBagId++) {
			IStorage petBag = getStorage(petBagId);
			if (petBag != null && petBag.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
				dirtyItems.addAll(petBag.getItemsWithKinah());
				dirtyItems.addAll(petBag.getDeletedItems());
				petBag.setPersistentState(PersistentState.UPDATED);
			}
		}
		Equipment equipment = getEquipment();
		if (equipment.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
			dirtyItems.addAll(equipment.getEquippedItems());
			equipment.setPersistentState(PersistentState.UPDATED);
		}

		return dirtyItems;
	}

	/**
	 * //TODO probably need to optimize here
	 *
	 * @return
	 */
	public FastList<Item> getAllItems() {
		FastList<Item> items = FastList.newInstance();
		items.addAll(this.inventory.getItemsWithKinah());
		if (this.regularWarehouse != null)
			items.addAll(this.regularWarehouse.getItemsWithKinah());
		if (this.accountWarehouse != null)
			items.addAll(this.accountWarehouse.getItemsWithKinah());

		for (int petBagId = StorageType.PET_BAG_MIN; petBagId <= StorageType.PET_BAG_MAX; petBagId++) {
			IStorage petBag = getStorage(petBagId);
			if (petBag != null)
				items.addAll(petBag.getItemsWithKinah());
		}
		items.addAll(getEquipment().getEquippedItems());
		return items;
	}

	public Storage getInventory() {
		return inventory;
	}

	/**
	 * @return the playerSettings
	 */
	public PlayerSettings getPlayerSettings() {
		return playerSettings;
	}

	/**
	 * @param playerSettings
	 *          the playerSettings to set
	 */
	public void setPlayerSettings(PlayerSettings playerSettings) {
		this.playerSettings = playerSettings;
	}

	public TitleList getTitleList() {
		return titleList;
	}

	public void setTitleList(TitleList titleList) {
		this.titleList = titleList;
		titleList.setOwner(this);
	}

	public com.aionemu.gameserver.model.team2.group.PlayerGroup getPlayerGroup2() {
		return playerGroup2;
	}

	public void setPlayerGroup2(com.aionemu.gameserver.model.team2.group.PlayerGroup playerGroup) {
		this.playerGroup2 = playerGroup;
	}

	/**
	 * @return the abyssRank
	 */
	public AbyssRank getAbyssRank() {
		return abyssRank;
	}

	/**
	 * @param abyssRank
	 *          the abyssRank to set
	 */
	public void setAbyssRank(AbyssRank abyssRank) {
		this.abyssRank = abyssRank;
	}

	@Override
	public PlayerEffectController getEffectController() {
		return (PlayerEffectController) super.getEffectController();
	}

	public void onLoggedIn() {
		friendList.setStatus(Status.ONLINE, getCommonData());
	}

	public void onLoggedOut() {
		requester.denyAll();
		friendList.setStatus(FriendList.Status.OFFLINE, getCommonData());
	}

	/**
	 * Returns true if has valid LegionMember
	 */
	public boolean isLegionMember() {
		return legionMember != null;
	}

	/**
	 * @param legionMember
	 *          the legionMember to set
	 */
	public void setLegionMember(LegionMember legionMember) {
		this.legionMember = legionMember;
	}

	/**
	 * @return the legionMember
	 */
	public LegionMember getLegionMember() {
		return legionMember;
	}

	/**
	 * @return the legion
	 */
	public Legion getLegion() {
		return legionMember != null ? legionMember.getLegion() : null;
	}

	/**
	 * Checks if object id's are the same
	 *
	 * @return true if the object id is the same
	 */
	public boolean sameObjectId(int objectId) {
		return this.getObjectId() == objectId;
	}

	/**
	 * @return true if a player has a store opened
	 */
	public boolean hasStore() {
		if (getStore() != null)
			return true;
		return false;
	}

	/**
	 * Removes legion from player
	 */
	public void resetLegionMember() {
		setLegionMember(null);
	}

	public boolean isInGroup2() {
		return playerGroup2 != null;
	}

	/**
	 * Access level of this player
	 *
	 * @return byte
	 */
	public byte getAccessLevel() {
		return playerAccount.getAccessLevel();
	}

	/**
	 * Membership of this player
	 * @return
	 */
	public byte getMembership() {
		if (playerAccount == null) {
			return 0x00;
		}
		return playerAccount.getMembership();
	}

	/**
	 * accountName of this player
	 *
	 * @return int
	 */
	public String getAcountName() {
		return playerAccount.getName();
	}

	/**
	 * @return the rates
	 */
	public Rates getRates() {
		if (rates == null)
			rates = new RegularRates();
		return rates;
	}

	/**
	 * @param rates
	 *          the rates to set
	 */
	public void setRates(Rates rates) {
		this.rates = rates;
	}

	/**
	 * @return warehouse size
	 */
	public int getWarehouseSize() {
		return this.playerCommonData.getWarehouseSize();
	}

	/**
	 * @param warehouseSize
	 */
	public void setWarehouseSize(int warehouseSize) {
		this.playerCommonData.setWarehouseSize(warehouseSize);
		getWarehouse().setLimit(getWarehouse().getLimit() + (warehouseSize * WAREHOUSE_SPACE));
	}

	/**
	 * @return regularWarehouse
	 */
	public Storage getWarehouse() {
		return regularWarehouse;
	}

	/**
	 * 0: regular, 1: fly, 2: glide
	 */
	public int getFlyState() {
		return this.flyState;
	}

	public void setFlyState(int flyState) {
		this.flyState = flyState;
		if (flyState == 1)
			setFlyingMode(true);
		else if (flyState == 0)
			setFlyingMode(false);
	}

	/**
	 * @return the isTrading
	 */
	public boolean isTrading() {
		return isTrading;
	}

	/**
	 * @param isTrading
	 *          the isTrading to set
	 */
	public void setTrading(boolean isTrading) {
		this.isTrading = isTrading;
	}

	public boolean isGathering() {
		return isGathering;
	}

	public void setIsGathering(boolean isGathering) {
		this.isGathering = isGathering;
	}

	/**
	 * @return the isInPrison
	 */
	public boolean isInPrison() {
		return prisonTimer != 0;
	}

	/**
	 * @param prisonTimer
	 *          the prisonTimer to set
	 */
	public void setPrisonTimer(long prisonTimer) {
		if (prisonTimer < 0)
			prisonTimer = 0;

		this.prisonTimer = prisonTimer;
	}

	/**
	 * @return the prisonTimer
	 */
	public long getPrisonTimer() {
		return prisonTimer;
	}

	/**
	 * @return the time in ms of start prison
	 */
	public long getStartPrison() {
		return startPrison;
	}

	/**
	 * @param start
	 *          : The time in ms of start prison
	 */
	public void setStartPrison(long start) {
		this.startPrison = start;
	}

	/**
	 * @return
	 */
	public boolean isProtectionActive() {
		return isInVisualState(CreatureVisualState.BLINKING);
	}

	/**
	 * Check is player is invul
	 *
	 * @return boolean
	 **/
	public boolean isInvul() {
		return invul;
	}

	/**
	 * Sets invul on player
	 *
	 * @param invul
	 *          - boolean
	 **/
	public void setInvul(boolean invul) {
		this.invul = invul;
	}

	public void setMailbox(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	public Mailbox getMailbox() {
		return mailbox;
	}

	/**
	 * @return the flyController
	 */
	public FlyController getFlyController() {
		return flyController;
	}

	/**
	 * @param flyController
	 *          the flyController to set
	 */
	public void setFlyController(FlyController flyController) {
		this.flyController = flyController;
	}

	public int getLastOnline() {
		Timestamp lastOnline = playerCommonData.getLastOnline();
		if (lastOnline == null || isOnline())
			return 0;

		return (int) (lastOnline.getTime() / 1000);
	}

	/**
	 * @param craftingTask
	 */
	public void setCraftingTask(CraftingTask craftingTask) {
		this.craftingTask = craftingTask;
	}

	/**
	 * @return
	 */
	public CraftingTask getCraftingTask() {
		return craftingTask;
	}

	/**
	 * @param flightTeleportId
	 */
	public void setFlightTeleportId(int flightTeleportId) {
		this.flightTeleportId = flightTeleportId;
	}

	/**
	 * @return flightTeleportId
	 */
	public int getFlightTeleportId() {
		return flightTeleportId;
	}

	/**
	 * @param flightDistance
	 */
	public void setFlightDistance(int flightDistance) {
		this.flightDistance = flightDistance;

	}

	/**
	 * @param path
	 */
	public void setCurrentFlypath(FlyPathEntry path) {
		this.flyLocationId = path;
		if (path != null)
			this.flyStartTime = System.currentTimeMillis();
		else
			this.flyStartTime = 0;
	}

	/**
	 * @return flightDistance
	 */
	public int getFlightDistance() {
		return flightDistance;
	}

	/**
	 * @return
	 */
	public boolean isUsingFlyTeleport() {
		return isInState(CreatureState.FLIGHT_TELEPORT) && flightTeleportId != 0;
	}

	public boolean isGM() {
		return getAccessLevel() >= AdminConfig.GM_LEVEL;
	}

	@Override
	public boolean isEnemy(Creature creature) {
		return creature.isEnemyFrom(this);
	}

	@Override
	public boolean isEnemyFrom(Npc npc) {
		return npc.isAttackableNpc() || isAggroIconTo(npc);
	}

	@Override
	public boolean isEnemyFrom(Player enemy) {
		if (this.getObjectId() == enemy.getObjectId()) {
			return false;
		} else if ((this.getAdminEnmity() > 1 || enemy.getAdminEnmity() > 1)) {
			return false;
		} else if (canPvP(enemy) || this.getController().isDueling(enemy)) {
			return true;
		} else if (!enemy.getRace().equals(getRace()) || getController().isDueling(enemy)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isAggroIconTo(Player player) {
		if (getAdminEnmity() > 1 || player.getAdminEnmity() > 1) {
			return true;
		}
		return !player.getRace().equals(getRace());
	}

	private boolean canPvP(Player enemy) {
		int worldId = enemy.getWorldId();
		if (!enemy.getRace().equals(getRace())) {
			if (World.getInstance().getWorldMap(getWorldId()).isPvpAllowed()) {
				return (!this.isInDisablePvPZone() && !enemy.isInDisablePvPZone());
			} else {
				return (this.isInPvPZone() && enemy.isInPvPZone());
			}
		} else {
			if (worldId != 210020000 && //Elten.
				worldId != 210040000 && //Heiron.
				worldId != 210050000 && //Inggison.
				worldId != 220020000 && //Morheim.
				worldId != 220040000 && //Beluslan.
				worldId != 220070000 && //Gelkmaros.
				worldId != 400010000 && //Reshanta.
				worldId != 600010000) { //Silentera Canyon.
				return (this.isInsideZoneType(ZoneType.PVP) && enemy.isInsideZoneType(ZoneType.PVP) && !isInSameTeam(enemy));
			}
		}
		return false;
	}

	private boolean isInDisablePvPZone() {
		List<ZoneInstance> zones = this.getPosition().getMapRegion().getZones(this);
		for (ZoneInstance zone: zones) {
			if (!zone.isPvpAllowed()) {
				return true;
			}
		}
		return false;
	}

	private boolean isInPvPZone() {
		List<ZoneInstance> zones = this.getPosition().getMapRegion().getZones(this);
		for (ZoneInstance zone: zones) {
			if (!zone.isPvpAllowed()) {
				return true;
			}
		}
		return false;
	}

	public boolean isInSameTeam(Player player) {
		if (isInGroup2() && player.isInGroup2()) {
			return getPlayerGroup2().getTeamId().equals(player.getPlayerGroup2().getTeamId());
		} else if (isInAlliance2() && player.isInAlliance2()) {
			return getPlayerAlliance2().getObjectId().equals(player.getPlayerAlliance2().getObjectId());
		} else if (isInLeague() && player.isInLeague()) {
			return getPlayerAllianceGroup2().getObjectId().equals(player.getPlayerAllianceGroup2().getObjectId());
		}
		return false;
	}

	public boolean canSee(Creature creature) {
		if (creature.isInVisualState(CreatureVisualState.BLINKING)) {
			return true;
		} if (((creature instanceof Player)) && (isInSameTeam((Player) creature))) {
			return true;
		} if (((creature instanceof Trap)) && (((Creature) ((Trap) creature).getCreator()).getObjectId() == getObjectId())) {
			return true;
		}
		return creature.getVisualState() <= getSeeState();
	}

	@Override
	public TribeClass getTribe() {
		TribeClass transformTribe = getTransformModel().getTribe();
		if (transformTribe != null) {
			return transformTribe;
		}
		return getRace() == Race.ELYOS ? TribeClass.PC : TribeClass.PC_DARK;
	}

	@Override
	public boolean isAggroFrom(Npc npc) {
		return (isAggroIconTo(npc) && (npc.getTribe().isGuard() || npc.getObjectTemplate().getAbyssNpcType() != AbyssNpcType.NONE || npc.getLevel() + 11 > getLevel() || (npc.isInInstance() && InstanceService.isAggro(npc.getWorldId()))));
	}

	/**
	 * Used in SM_NPC_INFO to check aggro irrespective to level
	 *
	 * @param npcTribe
	 * @return
	 */
	public boolean isAggroIconTo(Npc npc) {
		Race race = npc.getRace();
		TribeClass tribe = npc.getTribe();
		if (getAdminEnmity() == 1 || getAdminEnmity() == 3)
			return true;
		// Exception by Tribe
		if (tribe == TribeClass.USEALL)
			return false;
		// AbyssType != NONE -> SiegeNpc
		if (npc.getObjectTemplate().getAbyssNpcType() != AbyssNpcType.NONE)
			return checkSiegeRelations(npc);

		if (npc.getObjectTemplate().getNpcType().equals(NpcType.PEACE))
			return false;

		if (npc.getObjectTemplate().getNpcType().equals(NpcType.INVULNERABLE))
			return false;

		if (npc.getObjectTemplate().getNpcType() == NpcType.NON_ATTACKABLE && (npc.getWorldId() == 310010000 || npc.getWorldId() == 320010000))
			return false;

		switch (getTribe()) {
		case PC:
			if (race == Race.ASMODIANS || tribe == null || tribe.isDarkGuard())
				return true;
			return DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(tribe, TribeClass.PC);
		case PC_DARK:
			if (race == Race.ELYOS || tribe == null || tribe.isLightGuard())
				return true;
			return DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(tribe, TribeClass.PC_DARK);
		default:
			break;
		}
		return false;
	}

	/*
	 * Siege npc relations to player
	 */
	public boolean checkSiegeRelations(Npc npc) {
		Race race = npc.getRace();
		NpcType npcType = npc.getNpcType();
		TribeClass tribe = npc.getTribe();
		// Artifact can't be Enemy
		if (npc.getObjectTemplate().getAbyssNpcType().equals(AbyssNpcType.ARTIFACT))
			return false;
		// Exception friendly Balaur's
		if (race == Race.DRAKAN && npcType == NpcType.NON_ATTACKABLE)
			return false;
		switch (getRace()) {
		case ELYOS:
			// Elyos Gate
			if (race == Race.PC_LIGHT_CASTLE_DOOR)
				return false;
			// Elyos General
			if (race == Race.GCHIEF_LIGHT)
				return false;
			// Elyos Teleporter
			if (race == Race.TELEPORTER && tribe == TribeClass.GENERAL)
				return false;
			// Elyos Shield generators
			if ((race == Race.CONSTRUCT || race == Race.BARRIER) &&
					(tribe == TribeClass.GENERAL || tribe == TribeClass.F4GUARD_LIGHT))
				return false;
			break;
		case ASMODIANS:
			// Asmo Gate
			if (race == Race.PC_DARK_CASTLE_DOOR)
				return false;
			// Asmo General
			if (race == Race.GCHIEF_DARK)
				return false;
			// Asmo Teleporter
			if (race == Race.TELEPORTER && tribe == TribeClass.GENERAL_DARK)
				return false;
			// Elyos Shield generators
			if ((race == Race.CONSTRUCT || race == Race.BARRIER) &&
					(tribe == TribeClass.GENERAL_DARK || tribe == TribeClass.F4GUARD_DARK))
				return false;
			break;
		}
		return getRace() != race;
	}

	/**
	 * @return the summon
	 */
	public Summon getSummon() {
		return summon;
	}

	/**
	 * @param summon
	 *          the summon to set
	 */
	public void setSummon(Summon summon) {
		this.summon = summon;
	}

	public void setKisk(Kisk newKisk) {
		this.kisk = newKisk;
	}

	/**
	 * @return
	 */
	public Kisk getKisk() {
		return this.kisk;
	}

	/**
	 * @param delayId
	 * @return
	 */
	public boolean isItemUseDisabled(ItemUseLimits limits) {
		if (limits == null) {
			return false;
		}
		if (itemCoolDowns == null || !itemCoolDowns.containsKey(limits.getDelayId())) {
			return false;
		}
		Long coolDown = itemCoolDowns.get(limits.getDelayId()).getReuseTime();
		if (coolDown == null)
			return false;

		if (coolDown < System.currentTimeMillis()) {
			itemCoolDowns.remove(limits.getDelayId());
			return false;
		}

		return true;
	}

	/**
	 * @param delayId
	 * @return
	 */
	public long getItemCoolDown(int delayId) {
		if (itemCoolDowns == null || !itemCoolDowns.containsKey(delayId))
			return 0;

		return itemCoolDowns.get(delayId).getReuseTime();
	}

	/**
	 * @return the itemCoolDowns
	 */
	public Map<Integer, ItemCooldown> getItemCoolDowns() {
		return itemCoolDowns;
	}

	/**
	 * @param delayId
	 * @param time
	 * @param useDelay
	 */
	public void addItemCoolDown(int delayId, long time, int useDelay) {
		if (itemCoolDowns == null)
			itemCoolDowns = new FastMap<Integer, ItemCooldown>().shared();

		itemCoolDowns.put(delayId, new ItemCooldown(time, useDelay));
	}

	/**
	 * @param itemMask
	 */
	public void removeItemCoolDown(int itemMask) {
		if (itemCoolDowns == null)
			return;
		itemCoolDowns.remove(itemMask);
	}

	/**
	 * @param isGagged
	 *          the isGagged to set
	 */
	public void setGagged(boolean isGagged) {
		this.isGagged = isGagged;
	}

	/**
	 * @return the isGagged
	 */
	public boolean isGagged() {
		return isGagged;
	}

	public void setQuestFollowingNpc(Npc npc) {
		questFollowingNpc = npc;
	}

	public Npc getQuestFollowingNpc() {
		return questFollowingNpc;
	}

	/**
	 * @return isAdminTeleportation
	 */
	public boolean getAdminTeleportation() {
		return isAdminTeleportation;
	}

	/**
	 * @param isAdminTeleportation
	 */
	public void setAdminTeleportation(boolean isAdminTeleportation) {
		this.isAdminTeleportation = isAdminTeleportation;
	}

	public final boolean isCoolDownZero() {
		return cooldownZero;
	}

	public final void setCoolDownZero(boolean cooldownZero) {
		this.cooldownZero = cooldownZero;
	}

	public void setPlayerResActivate(boolean isActivated) {
		this.isResByPlayer = isActivated;
	}

	public boolean getResStatus() {
		return isResByPlayer;
	}

	public int getResurrectionSkill() {
		return resurrectionSkill;
	}

	public void setResurrectionSkill(int resurrectionSkill) {
		this.resurrectionSkill = resurrectionSkill;
	}

	public void setIsFlyingBeforeDeath(boolean isActivated) {
		this.isFlyingBeforeDeath = isActivated;
	}

	public boolean getIsFlyingBeforeDeath() {
		return isFlyingBeforeDeath;
	}

	public com.aionemu.gameserver.model.team2.alliance.PlayerAlliance getPlayerAlliance2() {
		return playerAllianceGroup != null ? playerAllianceGroup.getAlliance() : null;
	}

	public PlayerAllianceGroup getPlayerAllianceGroup2() {
		return playerAllianceGroup;
	}

	public boolean isInAlliance2() {
		return playerAllianceGroup != null;
	}

	public void setPlayerAllianceGroup2(PlayerAllianceGroup playerAllianceGroup) {
		this.playerAllianceGroup = playerAllianceGroup;
	}

	public final boolean isInLeague() {
		return isInAlliance2() && getPlayerAlliance2().isInLeague();
	}

	public final boolean isInTeam() {
		return isInGroup2() || isInAlliance2();
	}

	/**
	 * @return current {@link PlayerGroup}, {@link PlayerAlliance} or null
	 */
	public final TemporaryPlayerTeam<? extends TeamMember<Player>> getCurrentTeam() {
		return isInGroup2() ? getPlayerGroup2() : getPlayerAlliance2();
	}

	/**
	 * @return current {@link PlayerGroup}, {@link PlayerAllianceGroup} or null
	 */
	public final TemporaryPlayerTeam<? extends TeamMember<Player>> getCurrentGroup() {
		return isInGroup2() ? getPlayerGroup2() : getPlayerAllianceGroup2();
	}

	/**
	 * @return current team id
	 */
	public final int getCurrentTeamId() {
		return isInTeam() ? getCurrentTeam().getTeamId() : 0;
	}

	/**
	 * @param worldId
	 * @return
	 */
	public PortalCooldownList getPortalCooldownList() {
		return portalCooldownList;
	}

	public CraftCooldownList getCraftCooldownList() {
		return craftCooldownList;
	}

	public void setEditMode(boolean edit_mode) {
		this.edit_mode = edit_mode;
	}

	public boolean isInEditMode() {
		return edit_mode;
	}

	public Npc getPostman() {
		return postman;
	}

	public void setPostman(Npc postman) {
		this.postman = postman;
	}

	public Account getPlayerAccount() {
		return playerAccount;
	}

	/**
	 * Quest completion
	 */
	public boolean isCompleteQuest(int questId) {
		QuestState qs = getQuestStateList().getQuestState(questId);

		if (qs == null)
			return false;

		return qs.getStatus() == QuestStatus.COMPLETE;
	}

	public long getNextSkillUse() {
		return nextSkillUse;
	}

	public void setNextSkillUse(long nextSkillUse) {
		this.nextSkillUse = nextSkillUse;
	}

	public long getNextSummonSkillUse() {
		return nextSummonSkillUse;
	}

	public void setNextSummonSkillUse(long nextSummonSkillUse) {
		this.nextSummonSkillUse = nextSummonSkillUse;
	}

	/**
	 * chain skills
	 */
	public ChainSkills getChainSkills() {
		if (this.chainSkills == null)
			this.chainSkills = new ChainSkills();
		return this.chainSkills;
	}

	public void setLastCounterSkill(AttackStatus status) {
        long time = System.currentTimeMillis();
        if (AttackStatus.getBaseStatus(status) == AttackStatus.DODGE && PlayerClass.getStartingClassFor(this.getPlayerClass()) == PlayerClass.SCOUT) {
            this.lastCounterSkill.put(AttackStatus.DODGE, time);
        } else if (AttackStatus.getBaseStatus(status) == AttackStatus.PARRY && (this.getPlayerClass() == PlayerClass.GLADIATOR ||
		    this.getPlayerClass() == PlayerClass.CHANTER || this.getPlayerClass() == PlayerClass.MONK) || this.getPlayerClass() == PlayerClass.THUNDERER) {
            this.lastCounterSkill.put(AttackStatus.PARRY, time);
        } else if (AttackStatus.getBaseStatus(status) == AttackStatus.BLOCK && PlayerClass.getStartingClassFor(this.getPlayerClass()) == PlayerClass.WARRIOR) {
            this.lastCounterSkill.put(AttackStatus.BLOCK, time);
        } else if (AttackStatus.getBaseStatus(status) == AttackStatus.RESIST && PlayerClass.getStartingClassFor(this.getPlayerClass()) == PlayerClass.WARRIOR) {
            this.lastCounterSkill.put(AttackStatus.RESIST, time);
        }
    }

	public long getLastCounterSkill(AttackStatus status) {
		if (this.lastCounterSkill.get(status) == null)
			return 0;

		return this.lastCounterSkill.get(status);
	}

	/**
	 * @return the dualEffectValue
	 */
	public int getDualEffectValue() {
		return dualEffectValue;
	}


	/**
	 * @param dualEffectValue the dualEffectValue to set
	 */
	public void setDualEffectValue(int dualEffectValue) {
		this.dualEffectValue = dualEffectValue;
	}

	/**
	 * @return the Resurrection Positional State
	 */
	public boolean isInResPostState() {
		return this.isInResurrectPosState;
	}

	/**
	 * @param the
	 *          Resurrection Positional State to set
	 */
	public void setResPosState(boolean value) {
		this.isInResurrectPosState = value;
	}

	/**
	 * @param the
	 *          Resurrection Positional X value to set
	 */
	public void setResPosX(float value) {
		this.resPosX = value;
	}

	/**
	 * @return the Resurrection Positional X value
	 */
	public float getResPosX() {
		return this.resPosX;
	}

	/**
	 * @param the
	 *          Resurrection Positional Y value to set
	 */
	public void setResPosY(float value) {
		this.resPosY = value;
	}

	/**
	 * @return the Resurrection Positional Y value
	 */
	public float getResPosY() {
		return this.resPosY;
	}

	/**
	 * @param the
	 *          Resurrection Positional Z value to set
	 */
	public void setResPosZ(float value) {
		this.resPosZ = value;
	}

	/**
	 * @return the Resurrection Positional Z value
	 */
	public float getResPosZ() {
		return this.resPosZ;
	}

	public boolean isInSiegeWorld() {
		switch (getWorldId()) {
			case 210050000:
			case 220070000:
			case 400010000:
			return true;
		}
		return false;
	}

	/**
	 * @return true if player is under NoFly Effect
	 */
	public boolean isUnderNoFly() {
		return this.getEffectController().isAbnormalSet(AbnormalState.NOFLY);
	}

	/**
	 * @param the
	 *          status of NoFpConsum Effect
	 */
	public void setUnderNoFPConsum(boolean value) {
		this.underNoFPConsum = value;
	}

	/**
	 * @return true if player is under NoFpConsumEffect
	 */
	public boolean isUnderNoFPConsum() {
		return this.underNoFPConsum;
	}

	public void setInstanceStartPos(float instanceStartPosX, float instanceStartPosY, float instanceStartPosZ) {
		this.instanceStartPosX = instanceStartPosX;
		this.instanceStartPosY = instanceStartPosY;
		this.instanceStartPosZ = instanceStartPosZ;
	}

	public float getInstanceStartPosX() {
		return instanceStartPosX;
	}

	public float getInstanceStartPosY() {
		return instanceStartPosY;
	}

	public float getInstanceStartPosZ() {
		return instanceStartPosZ;
	}

	public boolean havePermission(byte perm) {
		return playerAccount.getMembership() >= perm;
	}

	/**
	 * @return Returns the emotions.
	 */
	public EmotionList getEmotions() {
		return emotions;
	}

	/**
	 * @param emotions
	 *          The emotions to set.
	 */
	public void setEmotions(EmotionList emotions) {
		this.emotions = emotions;
	}

	public int getRebirthResurrectPercent() {
		return rebirthResurrectPercent;
	}

	public void setRebirthResurrectPercent(int rebirthResurrectPercent) {
		this.rebirthResurrectPercent = rebirthResurrectPercent;
	}

	public int getRebirthSkill() {
		return rebirthSkill;
	}

	public void setRebirthSkill(int rebirthSkill) {
		this.rebirthSkill = rebirthSkill;
	}

	public BindPointPosition getBindPoint() {
		return bindPoint;
	}

	public void setBindPoint(BindPointPosition bindPoint) {
		this.bindPoint = bindPoint;
	}

	@Override
	public ItemAttackType getAttackType() {
		Item weapon = getEquipment().getMainHandWeapon();
		if (weapon != null)
			return weapon.getItemTemplate().getAttackType();
		return ItemAttackType.PHYSICAL;
	}

	public long getFlyStartTime() {
		return this.flyStartTime;
	}

	public FlyPathEntry getCurrentFlyPath() {
		return flyLocationId;
	}

	public void setUnWispable() {
		this.isWispable = false;
	}

	public void setWispable() {
		this.isWispable = true;
	}

	public boolean isWispable() {
		return isWispable;
	}

	public boolean isInvulnerableWing() {
		return this.isUnderInvulnerableWing;
	}

	public void setInvulnerableWing(boolean value) {
		this.isUnderInvulnerableWing = value;
	}

	public void resetAbyssRankListUpdated() {
		this.abyssRankListUpdateMask = 0;
	}

	public void setAbyssRankListUpdated(AbyssRankUpdateType type) {
		this.abyssRankListUpdateMask |= type.value();
	}

	public boolean isAbyssRankListUpdated(AbyssRankUpdateType type) {
		return (this.abyssRankListUpdateMask & type.value()) == type.value();
	}

	public void addSalvationPoints(long points) {
		this.playerCommonData.addSalvationPoints(points);
		PacketSendUtility.sendPacket(this, new S_STATUS(this));
	}

	public long getCurrentSalvationPercent() {
		return this.playerCommonData.getCurrentSalvationPercent();
	}

	@Override
	public byte isPlayer() {
		if (this.isGM())
			return 2;
		else
			return 1;
	}

	/**
	 * @return the motions
	 */
	public MotionList getMotions() {
		return motions;
	}

	/**
	 * @param motions
	 *          the motions to set
	 */
	public void setMotions(MotionList motions) {
		this.motions = motions;
	}

	public void setTransformed(boolean value) {
		getTransformModel().setActive(value);
	}

	public boolean isTransformed() {
		return getTransformModel().isActive();
	}


	/**
	 * @return the npcFactions
	 */
	public NpcFactions getNpcFactions() {
		return npcFactions;
	}

	/**
	 * @param npcFactions
	 *          the npcFactions to set
	 */
	public void setNpcFactions(NpcFactions npcFactions) {
		this.npcFactions = npcFactions;
	}


	/**
	 * @return the flyReuseTime
	 */
	public long getFlyReuseTime() {
		return flyReuseTime;
	}

	/**
	 * @param flyReuseTime
	 *          the flyReuseTime to set
	 */
	public void setFlyReuseTime(long flyReuseTime) {
		this.flyReuseTime = flyReuseTime;
	}

	/**
	 * @param the
	 *          flying mode flag to set
	 */
	public void setFlyingMode(boolean value) {
		this.isFlying = value;
	}

	/**
	 * @return true if player is in Flying mode
	 */
	public boolean isInFlyingMode() {
		return this.isFlying;
	}

	/**
	 * Stone Use Order determined by highest inventory slot. :( If player has two types, wrong one might be used.
	 *
	 * @param player
	 * @return selfRezItem
	 */
	public Item getSelfRezStone() {
		Item item = null;
		item = getReviveStone(161001001);
		item = getReviveStone(161001004);
		item = getReviveStone(161001005);
		if (item == null) {
			item = getReviveStone(161000003); //Reviving Elemental Stone.
		} if (item == null) {
			item = getReviveStone(161000004); //Tombstone Of Revival.
		} if (item == null) {
			item = getReviveStone(161000005); //Reviving Elemental Stone.
		}
		return item;
	}

	/**
	 * @param stoneItemId
	 * @return stoneItem or null
	 */
	private Item getReviveStone(int stoneId) {
		Item item = getInventory().getFirstItemByItemId(stoneId);
		if (item != null && isItemUseDisabled(item.getItemTemplate().getUseLimits()))
			item = null;
		return item;
	}

	/**
	 * Need to find how an item is determined as able to self-rez.
	 *
	 * @return boolean can self rez with item
	 */
	public boolean haveSelfRezItem() {
		return (getSelfRezStone() != null);
	}

	/**
	 * Rebirth Effect is id 160.
	 *
	 * @return
	 */
	public boolean haveSelfRezEffect() {
		if (getAccessLevel() >= AdminConfig.ADMIN_AUTO_RES)
			return true;

		// Store the effect info.
		List<Effect> effects = getEffectController().getAbnormalEffects();
		for (Effect effect : effects) {
			for (EffectTemplate template : effect.getEffectTemplates()) {
				if (template.getEffectid() == 160 && (template instanceof RebirthEffect)) {
					RebirthEffect rebirthEffect = (RebirthEffect) template;
					setRebirthResurrectPercent(rebirthEffect.getResurrectPercent());
					setRebirthSkill(rebirthEffect.getSkillId());
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasResurrectBase() {
		List<Effect> effects = getEffectController().getAbnormalEffects();
		for (Effect effect : effects) {
			for (EffectTemplate template : effect.getEffectTemplates()) {
				if (template.getEffectid() == 160 && (template instanceof ResurrectBaseEffect)) {
					return true;
				}
			}
		}
		return false;
	}

	public void unsetResPosState() {
		if (isInResPostState()) {
			setResPosState(false);
			setResPosX(0);
			setResPosY(0);
			setResPosZ(0);
		}
	}

	public LootGroupRules getLootGroupRules() {
		if (isInGroup2()) {
			return getPlayerGroup2().getLootGroupRules();
		}
		if (isInAlliance2()) {
			return getPlayerAlliance2().getLootGroupRules();
		}
		return null;
	}

	public boolean isLooting() {
		return lootingNpcOid != 0;
	}

	public void setLootingNpcOid(int lootingNpcOid) {
		this.lootingNpcOid = lootingNpcOid;
	}

	public int getLootingNpcOid()	{
		return lootingNpcOid;
	}

	public final boolean isMentor() {
		return isMentor;
	}

	public final void setMentor(boolean isMentor) {
		this.isMentor = isMentor;
	}

	@Override
	public Race getRace() {
		return playerCommonData.getRace();
	}

	public Player findPartner() {
		return World.getInstance().findPlayer(partnerId);
	}

	public boolean hasVar(String key) {
		return vars.containsKey(key);
	}

	public void setVar(String key, Object value, boolean sql) {
		vars.put(key, value);
		if(sql)
			PlayerVarsDAO.set(this.getObjectId(), key, value);
	}

	public Object getVar(String key) {
		return this.vars.get(key);
	}

	public int getVarInt(String key) {
		Object o = this.vars.get(key);
		if(o != null)
			return Integer.parseInt(o.toString());
		return 0;
	}

	public String getVarStr(String key) {
		Object o = this.vars.get(key);
		if(o != null)
			return o.toString();
		return null;
	}

	public void setVars(Map<String, Object> map) {
		this.vars = map;
	}

	public boolean isMarried() {
		return partnerId != 0;
	}

	public void setPartnerId(int partnerId) {
		this.partnerId = partnerId;
	}

	@Override
	public int getSkillCooldown(SkillTemplate template) {
		return isCoolDownZero() ? 0 : template.getCooldown();
	}

	@Override
	public int getItemCooldown(ItemTemplate template) {
		return isCoolDownZero() ? 0 : template.getUseLimits().getDelayTime();
	}

	public void setLastMessageTime() {
		if ((System.currentTimeMillis() - lastMsgTime) / 1000 < SecurityConfig.FLOOD_DELAY)
			floodMsgCount++;
		else
			floodMsgCount = 0;
		lastMsgTime = System.currentTimeMillis();
	}

	public int floodMsgCount() {
		return floodMsgCount;
	}

	public void setOnlineTime() {
		onlineTime = System.currentTimeMillis();
	}

	/*
	 * return online time in sec
	 */
	public long getOnlineTime() {
		return (System.currentTimeMillis() - onlineTime) / 1000;
	}

	public void setCommandUsed(boolean value) {
		isCommandUsed = value;
	}

	public boolean isCommandInUse() {
		return isCommandUsed;
	}

	public void setRebirthRevive(boolean result) {
		rebirthRevive = result;
	}

	public boolean canUseRebirthRevive() {
		return rebirthRevive;
	}

	public void subtractSupplements(int count, int supplementId) {
		subtractedSupplementsCount = count;
		subtractedSupplementId = supplementId;
	}

	public void updateSupplements(boolean result) {
		if ((subtractedSupplementId == 0) || (subtractedSupplementsCount == 0)) {
			return;
		}
		this.getInventory().decreaseByItemId(subtractedSupplementId, (int) (subtractedSupplementsCount * (result ? 1 : 0.4)));
		subtractedSupplementsCount = 0;
		subtractedSupplementId = 0;
	}

	public int getPortAnimation() {
		return portAnimation;
	}

	public void setPortAnimation(int portAnimation) {
		this.portAnimation = portAnimation;
	}

	public boolean isSkillDisabled(SkillTemplate template) {
		ChainCondition cond = template.getChainCondition();
		if (cond != null && cond.getSelfCount() > 0) {
			int chainCount = getChainSkills().getChainCount(this, template, cond.getCategory());
			if (chainCount > 0 && chainCount < cond.getSelfCount() && getChainSkills().chainSkillEnabled(cond.getCategory(), cond.getTime())) {
				return false;
			}
		}
		return super.isSkillDisabled(template);
	}

	public boolean isInSprintMode() {
		return isInSprintMode;
	}

	public void setSprintMode(boolean isInSprintMode) {
		this.isInSprintMode = isInSprintMode;
	}

	public String getCustomTag(boolean isForChatCommands) {
		String customTag = getAcountTag() != "%s" ? getAcountTag() : getAccessTag();
		String customTagForChatCommands = customTag != "%s" ? customTag.substring(0, customTag.indexOf("%")) : "";
		return isForChatCommands ? customTagForChatCommands : customTag;
	}

	private String getAccessTag() {
		String accessTag = "%s";
		switch (getClientConnection().getAccount().getAccessLevel()) {
			case 1:
				accessTag = AdminConfig.ADMIN_TAG_1;
				break;
			case 2:
				accessTag = AdminConfig.ADMIN_TAG_2;
				break;
			case 3:
				accessTag = AdminConfig.ADMIN_TAG_3;
				break;
			case 4:
				accessTag = AdminConfig.ADMIN_TAG_4;
				break;
			case 5:
				accessTag = AdminConfig.ADMIN_TAG_5;
				break;
			case 6:
				accessTag = AdminConfig.ADMIN_TAG_6;
				break;
			case 7:
				accessTag = AdminConfig.ADMIN_TAG_7;
				break;
			case 8:
				accessTag = AdminConfig.ADMIN_TAG_8;
				break;
			case 9:
				accessTag = AdminConfig.ADMIN_TAG_9;
				break;
			case 10:
				accessTag = AdminConfig.ADMIN_TAG_10;
				break;
		default:
			accessTag = "%s";
		}
		return accessTag;
	}

	private String getAcountTag() {
		String accountName = getClientConnection().getAccount().getName();
		String accountTag = "%s";
		if (accountName.equalsIgnoreCase("")) {
			accountTag = MembershipConfig.PLAYER_TAG_1;
		}
		return accountTag;
	}

	public int getRawKillCount() {
		return rawKillcount;
	}

	public void setRawKillCount(int count) {
		rawKillcount = count;
	}

	public int getSpreeLevel() {
		return spreeLevel;
	}

	public void setSpreeLevel(int value) {
		spreeLevel = value;
	}

	public AbsoluteStatOwner getAbsoluteStats() {
		return absStatsHolder;
	}

	public boolean hasBonus() {
		return hasBonus;
	}

	public void setBonus(boolean hasBonus) {
		this.hasBonus = hasBonus;
	}

	public int getBonusId() {
		return bonusId;
	}

	public void setBonusId(int id) {
		bonusId = id;
	}

	public boolean hasAbyssBonus() {
		return hasAbyssBonus;
	}

	public void setAbyssBonus(boolean hasAbyssBonus) {
		this.hasAbyssBonus = hasAbyssBonus;
	}

	public int getAbyssId() {
		return abyssId;
	}

	public void setAbyssId(int id) {
		abyssId = id;
	}

	public int getPlayersBonusId() {
		return playersBonusId;
	}

	public void setPlayersBonusId(int id) {
		playersBonusId = id;
	}

	public int getTransformedModelId() {
		return transformModelId;
	}

	public void setTransformedModelId(int id) {
		transformModelId = id;
	}

	public int getTransformedItemId() {
		return transformItemId;
	}

	public void setTransformedItemId(int id) {
		transformItemId = id;
	}

	public int getTransformedPanelId() {
		return transformPanelId;
	}

	public void setTransformedPanelId(int id) {
		transformPanelId = id;
	}

	public boolean isInWindstream() {
		return this.isInWindstream;
	}

	public void setInWindstream(boolean value) {
		this.isInWindstream = value;
	}

	public int getSilenceReportCount() {
		return silenceReportCount;
	}

	public void setSilenceReportCount(int count) {
		silenceReportCount = count;
	}

	public void setEnchantBoost(boolean boost) {
		this.enchantBoost = boost;
	}

	public void setAuthorizeBoost(boolean boost) {
		this.authorizeBoost = boost;
	}

	public boolean isEnchantBoost() {
		return this.enchantBoost;
	}

	public boolean isAuthorizeBoost() {
		return this.authorizeBoost;
	}

    public boolean isMagicalTypeClass() {
        if ((this.playerCommonData.getPlayerClass() == PlayerClass.MAGE) ||
			(this.playerCommonData.getPlayerClass() == PlayerClass.CLERIC) ||
		    (this.playerCommonData.getPlayerClass() == PlayerClass.SORCERER) ||
			(this.playerCommonData.getPlayerClass() == PlayerClass.SPIRIT_MASTER)) {
            return true;
        }
        return false;
    }

	private List<DecomposableItemList> decomposableItemLists = new FastList<DecomposableItemList>();

	public List<DecomposableItemList> getDecomposableItemLists() {
		return decomposableItemLists;
	}

	public void setDecomposableItemLists(List<DecomposableItemList> decomposableItemLists) {
		this.decomposableItemLists = decomposableItemLists;
	}

	public PlayerBattlePass getPlayerBattlePass() {
		return playerBattlePass;
	}

	public void setPlayerBattlePass(PlayerBattlePass playerBattlePass) {
		this.playerBattlePass = playerBattlePass;
	}

	public Map<Integer, PlayerEventWindow> getPlayerEventWindow() {
		return playerEventWindow;
	}

	public void setPlayerEventWindow(Map<Integer, PlayerEventWindow> playerEventWindow) {
		this.playerEventWindow = playerEventWindow;
	}

	public AccountSielEnergy getAccountSielEnergy() {
		return accountSielEnergy;
	}

	public void setAccountSielEnergy(AccountSielEnergy accountSielEnergy) {
		this.accountSielEnergy = accountSielEnergy;
	}

	public SerialKiller getSKInfo() {
		return skList;
	}
	public void setSKInfo(SerialKiller serialKiller) {
		skList = serialKiller;
	}

	public void sendMessage(String string) {
		PacketSendUtility.sendMessage(this, string);
	}

	public PlayerBoostEvent getPlayerBoostEvent() {
		return playerBoostEvent;
	}

	public void clearJoinRequest() {
		playerCommonData.setJoinRequestLegionId(0);
		playerCommonData.setJoinRequestState(LegionJoinRequestState.NONE);
		PlayerDAO.clearJoinRequest(getObjectId());
	}

	public PlayerUpgradeArcade getUpgradeArcade() {
		return playerCommonData.getUpgradeArcade();
	}


	private Map<Integer, WardrobeEntry> playerWardrobe = new FastMap<Integer, WardrobeEntry>();
	private EquipmentSettingList equipmentSettingList;

	public Map<Integer, WardrobeEntry> getPlayerWardrobe() {
		return playerWardrobe;
	}

	public void setPlayerWardrobe(Map<Integer, WardrobeEntry> playerWardrobe) {
		this.playerWardrobe = playerWardrobe;
	}

	public EquipmentSettingList getEquipmentSettingList() {
		return equipmentSettingList;
	}
	public void setEquipmentSettingList(EquipmentSettingList equipmentSettingList) {
		this.equipmentSettingList = equipmentSettingList;
	}

	private Map<Integer, BlackcloudLetter> blackcloudLetters = new FastMap<Integer, BlackcloudLetter>();

	public Map<Integer, BlackcloudLetter> getBlackcloudLetters() {
		return blackcloudLetters;
	}

	public void setBlackcloudLetters(Map<Integer, BlackcloudLetter> blackcloudLetters) {
		this.blackcloudLetters = blackcloudLetters;
	}

	private PlayerSweep shugoSweep;
	public PlayerSweep getPlayerShugoSweep() {
		return shugoSweep;
	}

	public void setPlayerShugoSweep(PlayerSweep ps) {
		this.shugoSweep = ps;
	}

	/**
	 * GM Mode
	 */

	// This variables are for the GM system
	private boolean isGmMode = false;

	public boolean isGmMode() {
		return isGmMode;
	}

	public void setGmMode(boolean isGmMode) {
		this.isGmMode = isGmMode;
	}
}
