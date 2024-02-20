package com.aionemu.gameserver.skillengine.action;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Actions", propOrder = {"actions"})
public class Actions
{
	@XmlElements({
	@XmlElement(name = "itemuse", type = ItemUseAction.class), 
	@XmlElement(name = "mpuse", type = MpUseAction.class),
	@XmlElement(name = "hpuse", type = HpUseAction.class), 
	@XmlElement(name = "dpuse", type = DpUseAction.class),
	@XmlElement(name = "spuse", type = SpUseAction.class)})
	
	protected List<Action> actions;
	
	public List<Action> getActions() {
		if (actions == null) {
			actions = new ArrayList<Action>();
		}
		return this.actions;
	}
}