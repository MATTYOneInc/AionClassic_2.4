package com.aionemu.gameserver.model;

public enum EventType
{
	NONE(0, ""),
	CHRISTMAS(1 << 0, "christmas"),
	HALLOWEEN(1 << 1, "halloween"),
	VALENTINE(1 << 2, "valentine"),
	BRAX_CAFE(1 << 3, "brax_cafe"),
	SUMMER_VACATION(1 << 4, "summer_vacation"),
	CHINA_NEW_YEAR(1 << 5, "china_new_year"),
	AQUA_PARK(1 << 6, "aqua_park"),
	CHUSEOK(1 << 7, "chuseok"),
	AION_ANNIVERSARY(1 << 8, "aion_anniversary");
	
	private int id;
	private String theme;
	
	private EventType(int id, String theme) {
		this.id = id;
		this.theme = theme;
	}
	
	public int getId() {
		return id;
	}
	
	public String getTheme() {
		return theme;
	}
	public static EventType getEventType(String theme) {
		for (EventType type : values()) {
			if (theme.equals(type.getTheme())) {
				return type;
			}
		}
		return EventType.NONE;
	}
}