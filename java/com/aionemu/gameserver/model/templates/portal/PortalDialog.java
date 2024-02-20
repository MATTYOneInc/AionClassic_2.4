package com.aionemu.gameserver.model.templates.portal;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="PortalDialog", propOrder={"portalPath"})
public class PortalDialog
{
    @XmlElement(name="portal_path")
    protected List<PortalPath> portalPath;
	
    @XmlAttribute(name="npc_id")
    protected int npcId;
	
    @XmlAttribute(name="siege_id")
    protected int siegeId;
	
    @XmlAttribute(name = "teleport_dialog_id")
    protected int teleportDialogId = 1011;
	
    public List<PortalPath> getPortalPath() {
        return portalPath;
    }
	
    public int getNpcId() {
        return npcId;
    }
	
    public void setNpcId(int value) {
        npcId = value;
    }
	
    public int getSiegeId() {
        return siegeId;
    }
	
    public void setSiegeId(int value) {
        siegeId = value;
    }
	
	public int getTeleportDialogId() {
        return teleportDialogId;
    }
}