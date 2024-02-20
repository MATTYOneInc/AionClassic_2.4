package com.aionemu.gameserver.utils.stats;

import com.aionemu.gameserver.configs.main.RateConfig;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Update Aion Classic 2.5
 * https://aion.plaync.com/board/hotissue/view?articleId=1265311&fbclid=IwAR18mA0OowDiXFdfvZennWvxx4pYwKfJrgDJn25fyH9VrG1LmX1EYomYP5Y
 */

@XmlEnum
public enum AbyssRankEnum
{
	GRADE9_SOLDIER(1, 120, 9, 0, 0, 1802431),
	GRADE8_SOLDIER(2, 168, 18, 23500, 0, 1802433),
	GRADE7_SOLDIER(3, 235, 36, 42780, 0, 1802435),
	GRADE6_SOLDIER(4, 329, 59, 63000, 0, 1802437),
	GRADE5_SOLDIER(5, 461, 75, 85200, 0, 1802439),
	GRADE4_SOLDIER(6, 645, 90, 105600, 0, 1802441),
	GRADE3_SOLDIER(7, 903, 125, 137000, 0, 1802443),
	GRADE2_SOLDIER(8, 1264, 250, 169700, 0, 1802445),
	GRADE1_SOLDIER(9, 1770, 500, 214100, 0, 1802447),
	STAR1_OFFICER(10, 2124, 800, 279000, 1000, 1802449),
	STAR2_OFFICER(11, 2549, 1300, 344500, 800, 1802451),
	STAR3_OFFICER(12, 3059, 1700, 488200, 500, 1802453),
	STAR4_OFFICER(13, 3671, 2300, 643200, 300, 1802455),
	STAR5_OFFICER(14, 4405, 2700, 721600, 100, 1802457),
	GENERAL(15, 5286, 4300, 800700, 30, 1802459),
	GREAT_GENERAL(16, 6343, 7000, 1050800, 10, 1802461),
	COMMANDER(17, 7612, 9500, 1856300, 3, 1802463),
	SUPREME_COMMANDER(18, 9134, 12000, 2500000, 1, 1802465);
	
	static Logger log = LoggerFactory.getLogger(AbyssRankEnum.class);

	private int id;
	private int pointsGained;
	private int pointsLost;
	private int apRequired;
	private int quota;
	private int descriptionId;

	/**
	 * @param id
	 * @param pointsGained
	 * @param pointsLost
	 * @param required
	 * @param quota
	 */
	private AbyssRankEnum(int id, int pointsGained, int pointsLost, int apRequired, int quota, int descriptionId) {
		this.id = id;
		this.pointsGained = pointsGained;
		this.pointsLost = pointsLost;
		this.apRequired = apRequired;
		this.quota = quota;
		this.descriptionId = descriptionId;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the pointsLost
	 */
	public int getPointsLost() {
		return pointsLost;
	}

	/**
	 * @return the pointsGained
	 */
	public int getPointsGained() {
		return pointsGained;
	}

	/**
	 * @return AP required for Rank
	 */
	public int getApRequired() {
		return apRequired;
	}


	/**
	 * @return The quota is the maximum number of allowed player to have the rank
	 */
	public int getQuota() {
		return quota;
	}

	public int getDescriptionId() {
		return descriptionId;
	}


	public static DescriptionId getRankDescriptionId(Player player){
		int pRankId = player.getAbyssRank().getRank().getId();
		for (AbyssRankEnum rank : values()) {
			if (rank.getId() == pRankId) {
				int descId = rank.getDescriptionId();
				return (player.getRace() == Race.ELYOS) ? new DescriptionId(descId) : new DescriptionId(descId + 36);
			}
		}
		throw new IllegalArgumentException("No rank Description Id found for player: " + player);
	}

	/**
	 * @param id
	 * @return The abyss rank enum by his id
	 */
	public static AbyssRankEnum getRankById(int id) {
		for (AbyssRankEnum rank : values()) {
			if (rank.getId() == id) {
				return rank;
			}
		}
		throw new IllegalArgumentException("Invalid abyss rank provided " + id);
	}

	/**
	 * @param ap
	 * @return The abyss rank enum for his needed ap
	 */
	public static AbyssRankEnum getRankForAp(int ap) {
        AbyssRankEnum r = AbyssRankEnum.GRADE9_SOLDIER;
        for (AbyssRankEnum rank : values()) {
            if (rank.getApRequired() <= ap) {
                r = rank;
            } else {
                break;
            }
        }
        return r;
    }
}