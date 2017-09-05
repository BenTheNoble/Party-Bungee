package me.xbenz.party.FriendManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import me.xbenz.party.Main.Main;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class iomessagehandler
{
  public static void handleMessage(String s)
  {
    String[] splitMessage = s.split("/");
    ProxiedPlayer plToTeleport;
    switch (splitMessage[1])
    {
    case "friendrequest": 
      if (BungeeCord.getInstance().getPlayer(splitMessage[3]) != null)
      {
        BungeeCord.getInstance().getPlayer(splitMessage[3]).sendMessage(Main.prefix2 + ChatColor.YELLOW + splitMessage[2] + ChatColor.GRAY + " sent you a friend request!");
        BungeeCord.getInstance().getPlayer(splitMessage[3]).sendMessage(Main.prefix2 + ChatColor.GRAY + "Type " + ChatColor.YELLOW + "/friend add " + splitMessage[2] + ChatColor.GRAY + " to accept the request!");
      }
      break;
    case "playerjoin": 
      for (ProxiedPlayer pl : BungeeCord.getInstance().getPlayers()) {
        try
        {
          ResultSet playerExistence = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '" + splitMessage[2] + "'");
          playerExistence.next();
          String uuid = playerExistence.getString("uuid");
          if (uuid.length() <= 2) {
            return;
          }
          if (Main.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + pl.getUUID() + "' AND `secondaryuser` LIKE '" + uuid + "'").next())
          {
            boolean isMsgSentAlready = false;
            for (Iterator i$ = Main.friendJoinTemp.keySet().iterator(); i$.hasNext();)
            {
              int i = ((Integer)i$.next()).intValue();
              if ((((friendJoinObj)Main.friendJoinTemp.get(Integer.valueOf(i))).playerWhoJoined.equals(splitMessage[2])) && (((friendJoinObj)Main.friendJoinTemp.get(Integer.valueOf(i))).playerNotified.equals(pl.getName()))) {
                isMsgSentAlready = true;
              }
            }
            if (!isMsgSentAlready)
            {
              pl.sendMessage(ChatColor.YELLOW + "Your friend " + splitMessage[2] + " joined!");
              friendJoinObj tempFriend = new friendJoinObj(splitMessage[2], pl.getName());
              Main.friendJoinTemp.put(Integer.valueOf(tempFriend.getID()), tempFriend);
            }
          }
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      BungeeCord.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable()
      {
        private Object[] val$splitMessage;

		public void run()
        {
          try
          {
            Thread.sleep(2000L);
          }
          catch (InterruptedException e)
          {
            e.printStackTrace();
          }
          ArrayList<Integer> keysToRemove = new ArrayList();
          for (Iterator i$ = Main.friendJoinTemp.keySet().iterator(); i$.hasNext();)
          {
            int i = ((Integer)i$.next()).intValue();
            if (((friendJoinObj)Main.friendJoinTemp.get(Integer.valueOf(i))).playerWhoJoined.equals(this.val$splitMessage[2])) {
              keysToRemove.add(Integer.valueOf(i));
            }
          }
          for (Iterator i$ = keysToRemove.iterator(); i$.hasNext();)
          {
            int i = ((Integer)i$.next()).intValue();
            Main.friendJoinTemp.remove(Integer.valueOf(i));
          }
        }
      });
      break;
    case "playerleave": 
      for (ProxiedPlayer pl : BungeeCord.getInstance().getPlayers()) {
        try
        {
          ResultSet playerExistence = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '" + splitMessage[2] + "'");
          playerExistence.next();
          String uuid = playerExistence.getString("uuid");
          if (uuid.length() <= 2) {
            return;
          }
          if (Main.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + pl.getUUID() + "' AND `secondaryuser` LIKE '" + uuid + "'").next())
          {
            boolean isMsgSentAlready = false;
            for (Iterator i$ = Main.friendLeaveTemp.keySet().iterator(); i$.hasNext();)
            {
              int i = ((Integer)i$.next()).intValue();
              if ((((friendLeaveObj)Main.friendLeaveTemp.get(Integer.valueOf(i))).playerWhoDCd.equals(splitMessage[2])) && (((friendLeaveObj)Main.friendLeaveTemp.get(Integer.valueOf(i))).playerNotified.equals(pl.getName()))) {
                isMsgSentAlready = true;
              }
            }
            if (!isMsgSentAlready)
            {
              pl.sendMessage(ChatColor.RED + "Your friend " + splitMessage[2] + " disconnected!");
              friendLeaveObj tempFriend = new friendLeaveObj(splitMessage[2], pl.getName());
              Main.friendLeaveTemp.put(Integer.valueOf(tempFriend.getID()), tempFriend);
            }
          }
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }
      }
      BungeeCord.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable()
      {
        private Object[] val$splitMessage;

		public void run()
        {
          try
          {
            Thread.sleep(2000L);
          }
          catch (InterruptedException e)
          {
            e.printStackTrace();
          }
          ArrayList<Integer> keysToRemove = new ArrayList();
          for (Iterator i$ = Main.friendLeaveTemp.keySet().iterator(); i$.hasNext();)
          {
            int i = ((Integer)i$.next()).intValue();
            if (((friendLeaveObj)Main.friendLeaveTemp.get(Integer.valueOf(i))).playerWhoDCd.equals(this.val$splitMessage[2])) {
              keysToRemove.add(Integer.valueOf(i));
            }
          }
          for (Iterator i$ = keysToRemove.iterator(); i$.hasNext();)
          {
            int i = ((Integer)i$.next()).intValue();
            Main.friendLeaveTemp.remove(Integer.valueOf(i));
          }
        }
      });
      break;
    case "locate": 
      if (BungeeCord.getInstance().getPlayer(splitMessage[2]) != null) {
      }
      break;
    case "foundplayer": 
      if (Main.locatePlayerEncoded.containsKey(Integer.valueOf(Integer.parseInt(splitMessage[3]))))
      {
        plToTeleport = BungeeCord.getInstance().getPlayer(splitMessage[4]);
        if (plToTeleport != null) {
          for (String server : BungeeCord.getInstance().getServers().keySet()) {
            if (splitMessage[5].equals(server))
            {
              plToTeleport.sendMessage(Main.prefix2 + ChatColor.GRAY + "Sending you to " + ChatColor.YELLOW + server);
              plToTeleport.connect(BungeeCord.getInstance().getServerInfo(server));
              Main.locatePlayerEncoded.remove(Integer.valueOf(Integer.parseInt(splitMessage[3])));
              return;
            }
          }
        }
      }
      break;
    case "requestaccept": 
      if (BungeeCord.getInstance().getPlayer(splitMessage[3]) != null) {
        BungeeCord.getInstance().getPlayer(splitMessage[3]).sendMessage(Main.prefix2 + ChatColor.YELLOW + splitMessage[2] + ChatColor.GRAY + " is now friends with you!");
      }
      break;
    }
  }
}
