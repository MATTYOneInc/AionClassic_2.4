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

import com.aionemu.gameserver.model.templates.pet.PetTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * This is a container holding and serving all {@link PetTemplate} instances.<br>
 * 
 * @author IlBuono
 */
@XmlRootElement(name = "pets")
@XmlAccessorType(XmlAccessType.FIELD)
public class PetData {

	@XmlElement(name = "pet")
	private List<PetTemplate> pets;

	/** A map containing all pet templates */
	private TIntObjectHashMap<PetTemplate> petData = new TIntObjectHashMap<PetTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (PetTemplate pet : pets) {
			petData.put(pet.getId(), pet);
		}
		pets.clear();
		pets = null;
	}

	public int size() {
		return petData.size();
	}

	/**
	 * /** Returns an {@link PetTemplate} object with given id.
	 * 
	 * @param id
	 *          id of Pet
	 * @return PetTemplate object containing data about Pet with that id.
	 */
	public PetTemplate getPetTemplate(int id) {
		return petData.get(id);
	}

}
