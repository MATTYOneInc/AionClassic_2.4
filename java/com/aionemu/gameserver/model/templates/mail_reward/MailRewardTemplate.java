package com.aionemu.gameserver.model.templates.mail_reward;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Wnkrz on 26/07/2017.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RewardMail")
public class MailRewardTemplate
{
    @XmlAttribute(name = "id")
    protected int id;
	
    @XmlAttribute(name = "name")
    protected String name;
	
    @XmlAttribute(name = "title")
    protected String title;
	
    @XmlAttribute(name = "tail")
    protected String tail;
	
    @XmlAttribute(name = "body")
    protected String body;
	
    @XmlAttribute(name = "item_id")
    protected int itemId;
	
    @XmlAttribute(name = "item_count")
    protected int itemCount;
	
    @XmlAttribute(name = "ap_count")
    protected int apCount;
	
    @XmlAttribute(name = "kinah_count")
    protected int kinahCount;
	
    @XmlAttribute(name = "sender")
    protected String sender;
	
    public int getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getSender() {
        return this.sender;
    }
    public String getTitle() {
        return this.title;
    }
    public String getTail() {
        return this.tail;
    }
    public String getBody() {
        return this.body;
    }
    public int getItemId() {
        return this.itemId;
    }
    public int getItemCount() {
        return this.itemCount;
    }
    public int getApCount() {
        return this.apCount;
    }
    public int getKinahCount() {
        return this.kinahCount;
    }
}