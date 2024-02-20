package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.flypath.FlyPathEntry;
import gnu.trove.map.hash.TShortObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author KID
 */
@XmlRootElement(name = "flypath_template")
@XmlAccessorType(XmlAccessType.FIELD)
public class FlyPathData {
	@XmlElement(name = "flypath_location")
	private List<FlyPathEntry> list;

	private TShortObjectHashMap<FlyPathEntry> loctlistData = new TShortObjectHashMap<FlyPathEntry>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (FlyPathEntry loc : list) {
			loctlistData.put(loc.getId(), loc);
		}
	}

	public int size() {
		return loctlistData.size();
	}

	public FlyPathEntry getPathTemplate(byte i) {
		return loctlistData.get((short)i);
	}
}
