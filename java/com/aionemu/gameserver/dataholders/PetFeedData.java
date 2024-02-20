package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.pet.PetFlavour;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "flavours" })
@XmlRootElement(name = "pet_feed")
public class PetFeedData {

	@XmlElement(name = "flavour")
	protected List<PetFlavour> flavours;

	@XmlTransient
	private Map<Integer, PetFlavour> petFlavoursById = new HashMap<Integer, PetFlavour>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		if (flavours == null) {
			return;
		}
		for (PetFlavour flavour : flavours) {
			petFlavoursById.put(flavour.getId(), flavour);
		}
		flavours.clear();
		flavours = null;
	}

	public PetFlavour getFlavourById(int flavourId) {
		return petFlavoursById.get(flavourId);
	}

	public int size() {
		return petFlavoursById.size();
	}

	public PetFlavour[] getPetFlavours() {
		return petFlavoursById.values().toArray(new PetFlavour[0]);
	}
}
