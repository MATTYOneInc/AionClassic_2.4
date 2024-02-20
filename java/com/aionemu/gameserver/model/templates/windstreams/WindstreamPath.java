package com.aionemu.gameserver.model.templates.windstreams;

public class WindstreamPath
{
  public int teleportId;
  public int distance;

  public WindstreamPath(int teleportId, int distance)
  {
    this.teleportId = teleportId;
    this.distance = distance;
  }
}