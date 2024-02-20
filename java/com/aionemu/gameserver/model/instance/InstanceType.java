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
package com.aionemu.gameserver.model.instance;

public enum InstanceType
{
    LF1,
	SCORE,
	ARENA,
	NORMAL,
	INVASION,
	DREADGION,
	ARENA_PVP,
	TOURNAMENT,
	ARENA_TEAM,
	TIME_ATTACK,
    BATTLEFIELD;
	
    public boolean isDarkPoetaInstance() {
        return this.equals(InstanceType.LF1);
    }
	public boolean isScoreInstance() {
        return this.equals(InstanceType.SCORE);
    }
	public boolean isArenaInstance() {
        return this.equals(InstanceType.ARENA);
    }
	public boolean isNormalInstance() {
        return this.equals(InstanceType.NORMAL);
    }
	public boolean isInvasionInstance() {
        return this.equals(InstanceType.INVASION);
    }
	public boolean isDreadgionInstance() {
        return this.equals(InstanceType.DREADGION);
    }
	public boolean isArenaPvPInstance() {
        return this.equals(InstanceType.ARENA_PVP);
    }
	public boolean isTournamentInstance() {
        return this.equals(InstanceType.TOURNAMENT);
    }
	public boolean isArenaTeamInstance() {
        return this.equals(InstanceType.ARENA_TEAM);
    }
	public boolean isTimeAttackInstance() {
        return this.equals(InstanceType.TIME_ATTACK);
    }
    public boolean isBattlefieldInstance() {
        return this.equals(InstanceType.BATTLEFIELD);
    }
}