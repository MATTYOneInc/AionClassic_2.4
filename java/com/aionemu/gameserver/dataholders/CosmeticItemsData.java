/*
 * This file is part of aion-lightning <aion-lightning.org>
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.cosmeticitems.CosmeticItemTemplate;
import javolution.util.FastMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;
/**
 *
 * @author xTz
 */
@XmlRootElement(name = "cosmetic_items")
@XmlAccessorType(XmlAccessType.FIELD)
public class CosmeticItemsData {
	@XmlElement(name = "cosmetic_item", type = CosmeticItemTemplate.class)
	private List<CosmeticItemTemplate> templates;
	private final Map<String, CosmeticItemTemplate> cosmeticItemTemplates = new FastMap<String, CosmeticItemTemplate>().shared();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (CosmeticItemTemplate template : templates) {
			cosmeticItemTemplates.put(template.getCosmeticName(), template);
		}
		templates.clear();
		templates = null;
	}

	public int size() {
		return cosmeticItemTemplates.size();
	}

	public CosmeticItemTemplate getCosmeticItemsTemplate(String str) {
		return cosmeticItemTemplates.get(str);
	}
}
