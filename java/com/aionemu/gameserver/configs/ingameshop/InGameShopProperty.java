package com.aionemu.gameserver.configs.ingameshop;

import com.aionemu.commons.utils.xml.JAXBUtil;
import com.aionemu.gameserver.model.templates.ingameshop.IGCategory;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "in_game_shop")
public class InGameShopProperty {

	@XmlElement(name = "category", required = true)
	private List<IGCategory> categories;

	public List<IGCategory> getCategories() {
		if (categories == null) {
			categories = new ArrayList<IGCategory>();
		}
		return categories;
	}

	public int size() {
		return getCategories().size();
	}

	public void clear() {
		if (categories != null)
			categories.clear();
	}

	public static InGameShopProperty load() {
		InGameShopProperty ing = null;
		try {
			String xml = FileUtils.readFileToString(new File("./config/ingameshop/in_game_shop.xml"), "UTF-8");
			ing = (InGameShopProperty) JAXBUtil.deserialize(xml, InGameShopProperty.class);
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to initialize ingameshop", e);
		}
		return ing;
	}
}
