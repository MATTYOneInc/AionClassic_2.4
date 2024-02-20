package com.aionemu.gameserver.skillengine.periodicaction;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PeriodicActions", propOrder = "periodicActions")
public class PeriodicActions
{
	@XmlElements({ @XmlElement(name = "hpuse", type = HpUsePeriodicAction.class), @XmlElement(name = "mpuse", type = MpUsePeriodicAction.class), @XmlElement(name = "dpuse", type = DpUsePeriodicAction.class) })
	protected List<PeriodicAction> periodicActions;
	
	@XmlAttribute(name = "checktime")
	protected int checktime;
	
	public List<PeriodicAction> getPeriodicActions() {
		return periodicActions;
	}
	
	public int getChecktime() {
		return checktime;
	}
}
