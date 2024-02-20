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
package com.aionemu.gameserver.configs.shedule;

import com.aionemu.commons.utils.xml.JAXBUtil;
import com.aionemu.gameserver.model.templates.rift.OpenRift;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.annotation.*;
import java.io.File;
import java.util.List;

/**
 * @author Rinzler (Encom)
 */

@XmlRootElement(name = "rift_schedule")
@XmlAccessorType(XmlAccessType.FIELD)
public class RiftSchedule
{
	@XmlElement(name = "rift", required = true)
    private List<Rift> riftsList;
	
    public List<Rift> getRiftsList() {
        return riftsList;
    }
	
	public void setRiftsList(List<Rift> fortressList) {
        this.riftsList = fortressList;
    }
	
	public static RiftSchedule load() {
        RiftSchedule rs;
        try {
            String xml = FileUtils.readFileToString(new File("./config/shedule/rift_schedule.xml"));
            rs = JAXBUtil.deserialize(xml, RiftSchedule.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Rifts", e);
        }
        return rs;
    }
	
	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "rift")
    public static class Rift {
        @XmlAttribute(required = true)
        private int id;
        @XmlElement(name = "open")
        private List<OpenRift> openRift;
		
        public int getWorldId() {
            return id;
        }
		
        public List<OpenRift> getRift() {
            return openRift;
        }
    }
}