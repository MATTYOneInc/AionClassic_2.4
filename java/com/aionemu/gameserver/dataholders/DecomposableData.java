package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.decomposable.DecomposableTemplate;
import javolution.util.FastMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "decomposable_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class DecomposableData
{
    @XmlElement(name = "decomposable_template")
    private List<DecomposableTemplate> decomposableTemplates;
	
    @XmlTransient
    private FastMap<Integer, DecomposableTemplate> decomposable = new FastMap<Integer, DecomposableTemplate>();
	
    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (DecomposableTemplate template : decomposableTemplates) {
            decomposable.put(template.getId(), template);
		}
    }
	
    public int size() {
        return decomposable.size();
    }
	
    public DecomposableTemplate getInfoByItemId(int id) {
        return decomposable.get(id);
    }
}