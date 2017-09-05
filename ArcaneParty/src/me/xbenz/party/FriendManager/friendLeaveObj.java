package me.xbenz.party.FriendManager;

import java.util.Random;

public class friendLeaveObj
{
  public String playerWhoDCd = null;
  public String playerNotified = null;
  public int ID;
  
  public friendLeaveObj(String playerDisconnected, String playerNotifd)
  {
    this.playerNotified = playerNotifd;
    this.playerWhoDCd = playerDisconnected;
    Random rand = new Random();
    this.ID = rand.nextInt(999999);
  }
  
  public int getID()
  {
    return this.ID;
  }
}
