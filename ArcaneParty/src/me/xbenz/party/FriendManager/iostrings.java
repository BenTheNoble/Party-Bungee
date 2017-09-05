package me.xbenz.party.FriendManager;

import java.util.Random;

import me.xbenz.party.Main.Main;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class iostrings
{
  
  public static String encodedIOfriendMessage(String requester, String requestee)
  {
    return Main.pluginAuthenticationString + "/friendrequest/" + requester + "/" + requestee;
  }
  
  public static String encodedIOplayerJoinMessage(String joined)
  {
    return Main.pluginAuthenticationString + "/playerjoin/" + joined;
  }
  
  public static String encodedIOplayerLeaveMessage(String disconnected)
  {
    return Main.pluginAuthenticationString + "/playerleave/" + disconnected;
  }
  
  public static String encodedIOlocatePlayerMessage(String playerToFind, String CommandSenderUsername)
  {
    return Main.pluginAuthenticationString + "/locate/" + playerToFind + "/" + new Random().nextInt(9999999) + "/" + CommandSenderUsername;
  }
  
  public static String encodedIOplayerFoundMessage(ProxiedPlayer playerWhoWasFound, int messageID, String playerWhoWasSearching)
  {
    return Main.pluginAuthenticationString + "/foundplayer/" + playerWhoWasFound.getName() + "/" + messageID + "/" + playerWhoWasSearching + "/" + playerWhoWasFound.getServer().getInfo().getName();
  }
  
  public static String encodedIOfriendReqAcceptedMessage(String requestSender, String requestReceiver)
  {
    return Main.pluginAuthenticationString + "/requestaccept/" + requestSender + "/" + requestReceiver;
  }
}
