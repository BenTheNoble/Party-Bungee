package me.xbenz.party.FriendManager;

import java.util.Random;

public class friendJoinObj
{
  public String playerWhoJoined = null;
  public String playerNotified = null;
  public int ID;
  
  public friendJoinObj(String playerJoined, String playerNotifd)
  {
    this.playerNotified = playerNotifd;
    this.playerWhoJoined = playerJoined;
    Random rand = new Random();
    this.ID = rand.nextInt(999999);
  }
  
  public int getID()
  {
    return this.ID;
  }
}
