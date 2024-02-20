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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.io.filefilter.FileFilterUtils.*;


/**
 * @author ViAl
 *
 */
public abstract class ReloadableData {

	protected static final Logger log = LoggerFactory.getLogger(ReloadableData.class);
	
	public abstract void reload(Player admin);
	
	protected abstract List<?> getData();
	
	protected abstract void setData(List<?> data);
	
	protected Schema getSchema(String xml_schema) {
		Schema schema = null;
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			schema = sf.newSchema(new File(xml_schema));
		}
		catch (SAXException saxe) {
			throw new Error("Error while getting schema", saxe);
		}
		return schema;
	}

	protected Collection<File> listFiles(File root, boolean recursive) {
		IOFileFilter dirFilter = recursive ? makeSVNAware(HiddenFileFilter.VISIBLE) : null;
		return FileUtils.listFiles(root,
			and(and(notFileFilter(prefixFileFilter("new")), suffixFileFilter(".xml")), HiddenFileFilter.VISIBLE), dirFilter);
	}
}
