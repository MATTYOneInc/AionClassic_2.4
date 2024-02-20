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
package com.aionemu.gameserver.model.templates.pet;

import com.aionemu.gameserver.model.templates.stats.PetStatsTemplate;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author IlBuono
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "pet")
public class PetTemplate {

	@XmlAttribute(name = "id", required = true)
	private int id;

	@XmlAttribute(name = "name", required = true)
	private String name;

	@XmlAttribute(name = "nameid", required = true)
	private int nameId;

	@XmlAttribute(name = "condition_reward")
	private int conditionReward;

	@XmlElement(name = "petfunction")
	private List<PetFunction> petFunctions;

	@XmlElement(name = "petstats")
	private PetStatsTemplate petStats;

	@XmlTransient
	Boolean hasPlayerFuncs = null;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getNameId() {
		return nameId;
	}

	public List<PetFunction> getPetFunctions() {
		if (hasPlayerFuncs == null) {
			hasPlayerFuncs = false;
			if (petFunctions == null) {
				List<PetFunction> result = new ArrayList<PetFunction>();
				result.add(PetFunction.CreateEmpty());
				petFunctions = result;
			}
			else {
				for (PetFunction func : petFunctions) {
					if (func.getPetFunctionType().isPlayerFunction()) {
						hasPlayerFuncs = true;
						break;
					}
				}
				if (!hasPlayerFuncs)
					petFunctions.add(PetFunction.CreateEmpty());
			}
		}
		return petFunctions;
	}

	public PetFunction getWarehouseFunction() {
		if (petFunctions == null)
			return null;
		for (PetFunction pf : petFunctions) {
			if (pf.getPetFunctionType() == PetFunctionType.WAREHOUSE)
				return pf;
		}
		return null;
	}

	/**
	 * Used to write to SM_PET packet, so checks only needed ones
	 */
	public boolean ContainsFunction(PetFunctionType type) {
		if (type.getId() < 0)
			return false;

		for (PetFunction t : getPetFunctions()) {
			if (t.getPetFunctionType() == type)
				return true;
		}
		return false;
	}

	/**
	 * Returns function if found, otherwise null
	 */
	public PetFunction getPetFunction(PetFunctionType type) {
		for (PetFunction t : getPetFunctions()) {
			if (t.getPetFunctionType() == type)
				return t;
		}
		return null;
	}

	public PetStatsTemplate getPetStats() {
		return petStats;
	}

	public final int getConditionReward() {
		return conditionReward;
	}

}
