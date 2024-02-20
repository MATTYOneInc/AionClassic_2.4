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
package com.aionemu.gameserver.model;

public enum TeleportAnimation
{
    NO_ANIMATION(0, 0),
    BEAM_ANIMATION(1, 3),
    JUMP_ANIMATION(3, 10),
    JUMP_ANIMATION_2(4, 10),
    FIRE_ANIMATION(4, 0x0B),
    JUMP_ANIMATION_3(8, 3),
	MAGE_ANIMATION(8, 10);
	
    private int startAnimation;
    private int endAnimation;
	
    private TeleportAnimation(int startAnimation, int endAnimation) {
	   this.startAnimation = startAnimation;
       this.endAnimation = endAnimation;
	}
	
    public int getStartAnimationId() {
        return startAnimation;
    }
	
    public int getEndAnimationId() {
        return endAnimation;
    }
	
    public boolean isNoAnimation() {
        return getStartAnimationId() == 0;
    }
}