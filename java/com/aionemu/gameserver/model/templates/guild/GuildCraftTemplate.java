package com.aionemu.gameserver.model.templates.guild;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.battle_pass.ActionRequired;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GuildCraftTemplate")
public class GuildCraftTemplate {

    @XmlElement(name = "guild_craft_product")
    protected GuildCraftProduct guildCraftProduct;

    @XmlElement(name = "guild_craft_component")
    protected List<GuildCraftComponent> guildCraftComponents;

    @XmlElement(name = "guild_craft_essence")
    protected GuildCraftEssence guildCraftEssence;

    @XmlElement(name = "guild_craft_essence_piece")
    protected GuildCraftEssence guildCraftEssencePiece;

    @XmlAttribute(name = "id")
    private int id;

    @XmlAttribute(name = "active")
    private boolean active;

    @XmlAttribute(name = "race")
    private Race race;

    @XmlAttribute(name = "craft_time")
    private int craftTime;


    public GuildCraftProduct getGuildCraftProduct() {
        return guildCraftProduct;
    }

    public GuildCraftEssence getGuildCraftEssence() {
        return guildCraftEssence;
    }

    public GuildCraftEssence getGuildCraftEssencePiece() {
        return guildCraftEssencePiece;
    }

    public List<GuildCraftComponent> getGuildCraftComponents() {
        return guildCraftComponents;
    }

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public int getCraftTime() {
        return craftTime;
    }

    public Race getRace() {
        return race;
    }
}
