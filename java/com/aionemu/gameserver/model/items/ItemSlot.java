package com.aionemu.gameserver.model.items;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

public enum ItemSlot
{
	MAIN_HAND(1),
	SUB_HAND(1 << 1),
	HELMET(1 << 2),
	TORSO(1 << 3),
	GLOVES(1 << 4),
	BOOTS(1 << 5),
	EARRINGS_LEFT(1 << 6),
	EARRINGS_RIGHT(1 << 7),
	RING_LEFT(1 << 8),
	RING_RIGHT(1 << 9),
	NECKLACE(1 << 10),
	SHOULDER(1 << 11),
	PANTS(1 << 12),
	POWER_SHARD_RIGHT(1 << 13),
	POWER_SHARD_LEFT(1 << 14),
	WINGS(1 << 15),
	WAIST(1 << 16),
	MAIN_OFF_HAND(1 << 17),
	SUB_OFF_HAND(1 << 18),

	MAIN_OR_SUB(MAIN_HAND.slotIdMask | SUB_HAND.slotIdMask, true),
	MAIN_OFF_OR_SUB_OFF(MAIN_OFF_HAND.slotIdMask | SUB_OFF_HAND.slotIdMask, true),
	EARRING_RIGHT_OR_LEFT(EARRINGS_LEFT.slotIdMask | EARRINGS_RIGHT.slotIdMask, true),
	RING_RIGHT_OR_LEFT(RING_LEFT.slotIdMask | RING_RIGHT.slotIdMask, true),
	SHARD_RIGHT_OR_LEFT(POWER_SHARD_LEFT.slotIdMask | POWER_SHARD_RIGHT.slotIdMask, true),
	RIGHT_HAND(MAIN_HAND.slotIdMask | MAIN_OFF_HAND.slotIdMask, true),
	LEFT_HAND(SUB_HAND.slotIdMask | SUB_OFF_HAND.slotIdMask, true),
	
	// STIGMA slots
	STIGMA1(1 << 19),
	STIGMA2(1 << 20),
	STIGMA3(1 << 21),
	STIGMA4(1 << 22),
	STIGMA5(1 << 23),
	STIGMA6(1 << 24),
	REGULAR_STIGMAS(STIGMA1.slotIdMask | STIGMA2.slotIdMask | STIGMA3.slotIdMask | STIGMA4.slotIdMask | STIGMA5.slotIdMask | STIGMA6.slotIdMask),
	
	ADV_STIGMA1(1 << 26),
	ADV_STIGMA2(1 << 27),
	ADV_STIGMA3(1 << 28),
	ADV_STIGMA4(1 << 29),
	ADV_STIGMA5(1 << 30),
	ADVANCED_STIGMAS(ADV_STIGMA1.slotIdMask | ADV_STIGMA2.slotIdMask | ADV_STIGMA3.slotIdMask | ADV_STIGMA4.slotIdMask | ADV_STIGMA5.slotIdMask),
	ALL_STIGMAS(STIGMA1.slotIdMask | STIGMA2.slotIdMask | STIGMA3.slotIdMask | STIGMA4.slotIdMask | STIGMA5.slotIdMask | STIGMA6.slotIdMask |ADV_STIGMA1.slotIdMask | ADV_STIGMA2.slotIdMask | ADV_STIGMA3.slotIdMask | ADV_STIGMA4.slotIdMask | ADV_STIGMA5.slotIdMask);
	
	private int slotIdMask;
	private boolean combo;
	
	private static TIntObjectHashMap<ItemSlot[]> itemSlots;

	static{
		itemSlots = new TIntObjectHashMap<ItemSlot[]>();
		for(ItemSlot slot : values()){
			ItemSlot[] slotsForMask = calculateSlots(slot.getSlotIdMask());
			itemSlots.put(slot.getSlotIdMask(), slotsForMask);
		}
		itemSlots.put(10, calculateSlots(10));
	}
	
	private static ItemSlot[] calculateSlots(int slotIdMask) {
		List<ItemSlot> slots = new ArrayList<ItemSlot>();
		for (ItemSlot itemSlot : values()) {
			int sumMask = itemSlot.slotIdMask & slotIdMask;
			if (sumMask > 0 && sumMask <= slotIdMask && !itemSlot.isCombo()) {
				slots.add(itemSlot);
			}
		}
		return slots.toArray(new ItemSlot[slots.size()]);
	}
	
	private ItemSlot(int mask) {
		this(mask, false);
	}
	
	private ItemSlot(int mask, boolean combo) {
		this.slotIdMask = mask;
		this.combo = combo;
	}
	
	public int getSlotIdMask() {
		return slotIdMask;
	}
	
	public boolean isCombo() {
		return combo;
	}
	
	public static boolean isRegularStigma(long slot) {
		return (REGULAR_STIGMAS.slotIdMask & slot) == slot;
	}

	public static boolean isAdvancedStigma(long slot) {
		return (ADVANCED_STIGMAS.slotIdMask & slot) == slot;
	}

	public static boolean isStigma(long slot) {
		return (ALL_STIGMAS.slotIdMask & slot) == slot;
	}

	public static ItemSlot[] getSlotsFor(long slot) {
		List<ItemSlot> slots = new ArrayList<ItemSlot>();
		for (ItemSlot itemSlot : values()) {
			if (slot != 0 && !itemSlot.isCombo() && (slot & itemSlot.slotIdMask) == itemSlot.slotIdMask) {
				slots.add(itemSlot);
			}
		}
		return slots.toArray(new ItemSlot[slots.size()]);
	}
	
	public static ItemSlot getSlotFor(long slot) {
		ItemSlot[] slots = getSlotsFor(slot);
		if (slots != null && slots.length > 0) {
			return slots[0];
		}
		throw new IllegalArgumentException("Invalid provided slotIdMask " + slot);
	}
}