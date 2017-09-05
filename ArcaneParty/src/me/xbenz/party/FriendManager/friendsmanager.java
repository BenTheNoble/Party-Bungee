package me.xbenz.party.FriendManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import me.xbenz.party.Main.Main;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;

public class friendsmanager
  extends Command
{
  public friendsmanager()
  {
    super("friend");
  }
  
  public void sendHelpMessages(ProxiedPlayer p)
  {
    p.sendMessage(ChatColor.GOLD + "-------------------------------------------------");
    p.sendMessage(Main.prefix2 + ChatColor.GRAY + "/friend add <user>" + ChatColor.YELLOW + " - Sends a friend request");
    p.sendMessage(Main.prefix2 + ChatColor.GRAY + "/friend remove <user>" + ChatColor.YELLOW + " - Removes a friend");
    p.sendMessage(Main.prefix2 + ChatColor.GRAY + "/friend list" + ChatColor.YELLOW + " - List your current friends");
    p.sendMessage(Main.prefix2 + ChatColor.GRAY + "/friend jump <user>" + ChatColor.YELLOW + " - Teleport to your friends server");
    p.sendMessage(ChatColor.GOLD + "-------------------------------------------------");
  }
  
  public void execute(final CommandSender commandSender, String[] strings)
  {
    if ((commandSender instanceof ProxiedPlayer))
    {
      if (strings.length >= 1) {
        switch (strings[0].toLowerCase())
        {
        case "help": 
          sendHelpMessages((ProxiedPlayer)commandSender);
          
          break;
        case "list": 
          try
          {
            ResultSet getPlayers = Main.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + ((ProxiedPlayer)commandSender).getUUID() + "'");
            
            int friendscount = 0;
            ArrayList<String> friends = new ArrayList();
            while (getPlayers.next())
            {
              friendscount++;
              ResultSet getPlayerName = Main.sql.getFromDB("SELECT * FROM `players` WHERE `uuid` LIKE '" + getPlayers.getString("secondaryuser") + "'");
              getPlayerName.next();
              friends.add(getPlayerName.getString("username"));
            }
            if (friendscount == 0)
            {
              commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "You have no friends! Use /friend add <user>");
              return;
            }
            commandSender.sendMessage(ChatColor.GOLD + "-------------------------------------------------");
            commandSender.sendMessage(ChatColor.GRAY + ("You have " + ChatColor.YELLOW + friendscount + ChatColor.GRAY + " Friend(s): ").toString());
            for (String s : friends) {
              TextComponent message = new TextComponent(ChatColor.YELLOW + s);
              message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/f jump " + s) );
              message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Click to TP to friend!").create() ) );
              commandSender.sendMessage( message );
            }
            commandSender.sendMessage(ChatColor.GOLD + "-------------------------------------------------");
            return;
          }
          catch (SQLException e)
          {
            e.printStackTrace();
          }
        case "add": 
          if (strings.length == 2) {
            try
            {
              ResultSet ifPlayerExists = null;
              
              ifPlayerExists = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '" + strings[1] + "'");
              if (ifPlayerExists.next())
              {
                String requestedUUID = ifPlayerExists.getString("uuid");
                
                ResultSet results = Main.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + ((ProxiedPlayer)commandSender).getUUID() + "' AND `secondaryuser` LIKE '" + requestedUUID + "'");
                ResultSet results2 = Main.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + requestedUUID + "' AND `secondaryuser` LIKE '" + ((ProxiedPlayer)commandSender).getName() + "'");
                if ((!results.next()) && (!results2.next()))
                {
                  ResultSet hasRequested = Main.sql.getFromDB("SELECT * FROM `pendingrequests` WHERE `requested` LIKE '" + requestedUUID + "' AND `requestee` LIKE '" + ((ProxiedPlayer)commandSender).getUUID() + "'");
                  
                  ResultSet isAccepting = Main.sql.getFromDB("SELECT * FROM `pendingrequests` WHERE `requested` LIKE '" + ((ProxiedPlayer)commandSender).getUUID() + "' AND `requestee` LIKE '" + requestedUUID + "'");
                  if (hasRequested.next())
                  {
                    if (isAccepting.next())
                    {
                      Main.sql.submitQuery("INSERT IGNORE INTO `relationships` (`primaryuser`, `secondaryuser`) VALUES ('" + ((ProxiedPlayer)commandSender).getUUID() + "', '" + requestedUUID + "');");
                      Main.sql.submitQuery("INSERT IGNORE INTO `relationships` (`primaryuser`, `secondaryuser`) VALUES ('" + requestedUUID + "', '" + ((ProxiedPlayer)commandSender).getUUID() + "');");
                      Main.sql.submitQuery("DELETE FROM `pendingrequests` WHERE `pendingrequests`.`requested` = '" + ((ProxiedPlayer)commandSender).getUUID() + "' AND `pendingrequests`.`requestee` = '" + requestedUUID + "'");
                      Main.sql.submitQuery("DELETE FROM `pendingrequests` WHERE `pendingrequests`.`requested` = '" + requestedUUID + "' AND `pendingrequests`.`requestee` = '" + ((ProxiedPlayer)commandSender).getUUID() + "'");
                      commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "You are now friends with " + ChatColor.YELLOW + strings[1]);
                      if (BungeeCord.getInstance().getPlayer(strings[1]) != null) {
                        BungeeCord.getInstance().getPlayer(strings[1]).sendMessage(Main.prefix2 + ChatColor.YELLOW + commandSender.getName() + ChatColor.GRAY + " is now friends with you!");
                      } else {
                      }
                      return;
                    }
                    commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "You've already sent a friend request to that player!");
                    return;
                  }
                  Main.sql.submitQuery("INSERT IGNORE INTO `pendingrequests` (`requested`, `requestee`) VALUES ('" + requestedUUID + "', '" + ((ProxiedPlayer)commandSender).getUUID() + "');");
                  
                  ResultSet hasRequested2 = Main.sql.getFromDB("SELECT * FROM `pendingrequests` WHERE `requested` LIKE '" + requestedUUID + "' AND `requestee` LIKE '" + ((ProxiedPlayer)commandSender).getUUID() + "'");
                  
                  ResultSet isAccepting2 = Main.sql.getFromDB("SELECT * FROM `pendingrequests` WHERE `requested` LIKE '" + ((ProxiedPlayer)commandSender).getUUID() + "' AND `requestee` LIKE '" + requestedUUID + "'");
                  if ((hasRequested2.next()) && (isAccepting2.next()))
                  {
                    Main.sql.submitQuery("INSERT IGNORE INTO `relationships` (`primaryuser`, `secondaryuser`) VALUES ('" + ((ProxiedPlayer)commandSender).getUUID() + "', '" + requestedUUID + "');");
                    Main.sql.submitQuery("INSERT IGNORE INTO `relationships` (`primaryuser`, `secondaryuser`) VALUES ('" + requestedUUID + "', '" + ((ProxiedPlayer)commandSender).getUUID() + "');");
                    Main.sql.submitQuery("DELETE FROM `pendingrequests` WHERE `pendingrequests`.`requested` = '" + ((ProxiedPlayer)commandSender).getUUID() + "' AND `pendingrequests`.`requestee` = '" + requestedUUID + "'");
                    Main.sql.submitQuery("DELETE FROM `pendingrequests` WHERE `pendingrequests`.`requested` = '" + requestedUUID + "' AND `pendingrequests`.`requestee` = '" + ((ProxiedPlayer)commandSender).getUUID() + "'");
                    commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "You are now friends with " + ChatColor.YELLOW + strings[1]);
                    if (BungeeCord.getInstance().getPlayer(strings[1]) != null) {
                      BungeeCord.getInstance().getPlayer(strings[1]).sendMessage(Main.prefix2 + ChatColor.YELLOW + commandSender.getName() + ChatColor.GRAY + " is now friends with you!");
                    } else {
                    }
                    return;
                  }
                  commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "You've sent a friend request to " + ChatColor.YELLOW + strings[1]);
                  if (BungeeCord.getInstance().getPlayer(strings[1]) != null)
                  {
                    BungeeCord.getInstance().getPlayer(strings[1]).sendMessage(Main.prefix2 + ChatColor.YELLOW + commandSender.getName() + ChatColor.GRAY + " sent you a friend request!");
                    BungeeCord.getInstance().getPlayer(strings[1]).sendMessage(Main.prefix2 + ChatColor.GRAY + "Type " + ChatColor.YELLOW + "/friend add " + commandSender.getName() + ChatColor.GRAY + " to accept the request!");
                  }
                  else
                  {
                  }
                  return;
                }
                commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "You're already friends with that player!");
              }
              else
              {
                commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "Player not found!");
              }
            }
            catch (SQLException e)
            {
              e.printStackTrace();
            }
          }
          commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "Please specify a player!");
          
          break;
        case "remove": 
          if (strings.length == 2)
          {
            ResultSet playerExistence = null;
            String uuid = null;
            try
            {
              playerExistence = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '" + strings[1] + "'");
              if (playerExistence.next())
              {
                uuid = playerExistence.getString("uuid");
                if (uuid.length() <= 2) {
                  commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "That player doesn't exist!");
                }
              }
              else
              {
                commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "That player doesn't exist!");
                return;
              }
            }
            catch (SQLException e)
            {
              e.printStackTrace();
            }
            Main.sql.submitQuery("DELETE FROM `relationships` WHERE `relationships`.`primaryuser` = '" + ((ProxiedPlayer)commandSender).getUUID() + "' AND `relationships`.`secondaryuser` = '" + uuid + "'");
            Main.sql.submitQuery("DELETE FROM `relationships` WHERE `relationships`.`primaryuser` = '" + uuid + "' AND `relationships`.`secondaryuser` = '" + ((ProxiedPlayer)commandSender).getUUID() + "'");
            commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "Player deleted from your friends list!");
          }
          else
          {
            commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "Please specify a player!");
          }
          break;
        case "delete": 
          if (strings.length == 2)
          {
            ResultSet playerExistence = null;
            String uuid = null;
            try
            {
              playerExistence = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '" + strings[1] + "'");
              if (playerExistence.next())
              {
                uuid = playerExistence.getString("uuid");
                if (uuid.length() <= 2) {
                  commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "That player doesn't exist!");
                }
              }
              else
              {
                commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "That player doesn't exist!");
                return;
              }
            }
            catch (SQLException e)
            {
              e.printStackTrace();
            }
            Main.sql.submitQuery("DELETE FROM `relationships` WHERE `relationships`.`primaryuser` = '" + ((ProxiedPlayer)commandSender).getUUID() + "' AND `relationships`.`secondaryuser` = '" + uuid + "'");
            Main.sql.submitQuery("DELETE FROM `relationships` WHERE `relationships`.`primaryuser` = '" + uuid + "' AND `relationships`.`secondaryuser` = '" + ((ProxiedPlayer)commandSender).getUUID() + "'");
            commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "Player deleted from your friends list!");
          }
          else
          {
            commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "Please specify a player!");
          }
          break;
        case "jump": 
          if (strings.length == 2) {
            try
            {
              String uuid = null;
              ResultSet playerExistence = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '" + strings[1] + "'");
              if (playerExistence.next())
              {
                uuid = playerExistence.getString("uuid");
                if (uuid.length() <= 2) {
                  commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "That player doesn't exist!");
                }
              }
              else
              {
                commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "That player doesn't exist!");
                return;
              }
              ResultSet isFriend1 = Main.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + ((ProxiedPlayer)commandSender).getUUID() + "' AND `secondaryuser` LIKE '" + uuid + "'");
              ResultSet isFriend2 = Main.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + uuid + "' AND `secondaryuser` LIKE '" + ((ProxiedPlayer)commandSender).getUUID() + "'");
              if ((isFriend1.next()) || (isFriend2.next()))
              {
                commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "Searching for " + ChatColor.YELLOW + strings[1]);
                
                boolean found = false;
                Server server = null;
                for (ProxiedPlayer pl : BungeeCord.getInstance().getPlayers()) {
                  if (pl.getName().equalsIgnoreCase(strings[1]))
                  {
                    server = pl.getServer();
                    found = true;
                  }
                }
                if (found)
                {
                  commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "Sending you to " + ChatColor.YELLOW + server.getInfo().getName());
                  ((ProxiedPlayer)commandSender).connect(server.getInfo());
                  return;
                }
                String encodedLocMessage = iostrings.encodedIOlocatePlayerMessage(strings[1], commandSender.getName());
                final int locateID = Integer.parseInt(encodedLocMessage.split("/")[3]);
                Main.locatePlayerEncoded.put(Integer.valueOf(locateID), commandSender.getName());
                
                final String[] stringsFinal = strings;
                
                BungeeCord.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable()
                {
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
                    if (Main.locatePlayerEncoded.containsKey(Integer.valueOf(locateID)))
                    {
                      commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "Could not find " + ChatColor.YELLOW + stringsFinal[1]);
                      Main.locatePlayerEncoded.remove(Integer.valueOf(locateID));
                    }
                  }
                });
              }
              else
              {
                commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "You are not friends with that player!");
              }
            }
            catch (SQLException e)
            {
              e.printStackTrace();
            }
          }
          commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "Please specify a player!");
          
          break;
        default: 
          commandSender.sendMessage(Main.prefix2 + ChatColor.GRAY + "Incorrect Arguments! Use /friend");
        }
      } else {
        sendHelpMessages((ProxiedPlayer)commandSender);
      }
    }
    else {
      commandSender.sendMessage(ChatColor.RED + "Players only!");
    }
  }
}
