package me.xbenz.party.Main;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import me.xbenz.party.Commands.Party;
import me.xbenz.party.FriendManager.friendJoinObj;
import me.xbenz.party.FriendManager.friendLeaveObj;
import me.xbenz.party.FriendManager.friendsmanager;
import me.xbenz.party.FriendManager.sqlmanager;
import me.xbenz.party.Listeners.PlayerDisconnect;
import me.xbenz.party.Listeners.ServerSwitch;
import me.xbenz.party.Message.Msg;
import me.xbenz.party.Message.Reply;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.api.plugin.Listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

public class Main
  extends Plugin implements Listener
{
  public static Plugin plugin;
  public static File f;
  public static Configuration c;
  public static HashMap<String, String> inparty = new HashMap();
  public static HashMap<String, String> invite = new HashMap();
  public static ArrayList<String> partyleader = new ArrayList();
  public static String prefix;
  public static String prefix2;
  public static String prefix3;
  public static int PremiumPartySize = 20;
  public static int PartySize = 10;
  public static HashMap<Integer, String> messageIDsEncoded = new HashMap();
  public static HashMap<Integer, String> locatePlayerEncoded = new HashMap();
  public static HashMap<Integer, friendJoinObj> friendJoinTemp = new HashMap();
  public static HashMap<Integer, friendLeaveObj> friendLeaveTemp = new HashMap();
  public static ServerSocket ioSock;
  public static ArrayList<InetSocketAddress> otherServers = new ArrayList();
  public static String pluginAuthenticationString = "27591Thsefinaa6786785fsefguysgfhkshfbelruh";
  public static sqlmanager sql = null;
  public static int connectionsInLast5minutes = 0;
  public static int outgoingconnectionsInLast5minutes = 0;
  
  public void onEnable()
  {
    plugin = this;
    
    loadConfig();
    
    prefix3 = ChatColor.BLUE + "[Message] ";
    prefix2 = ChatColor.BLUE + "[Friend] ";
    prefix = ChatColor.BLUE + "[Party] ";
    
    PartySize = c.getInt("Partysize.Normal");
    PremiumPartySize = c.getInt("Partysize.Premium");
    
    ProxyServer.getInstance().getPluginManager().registerCommand(this, new Party());
    ProxyServer.getInstance().getPluginManager().registerCommand(this, new friendsmanager());
    BungeeCord.getInstance().getPluginManager().registerCommand(this, new Msg("msg"));
    BungeeCord.getInstance().getPluginManager().registerCommand(this, new Msg("tell"));
    BungeeCord.getInstance().getPluginManager().registerCommand(this, new Msg("w"));
    BungeeCord.getInstance().getPluginManager().registerCommand(this, new Msg("m"));
    BungeeCord.getInstance().getPluginManager().registerCommand(this, new Msg("t"));
    BungeeCord.getInstance().getPluginManager().registerCommand(this, new Msg("pm"));
    BungeeCord.getInstance().getPluginManager().registerCommand(this, new Msg("whisper"));
    BungeeCord.getInstance().getPluginManager().registerCommand(this, new Reply("r"));
    BungeeCord.getInstance().getPluginManager().registerCommand(this, new Reply("reply"));
    ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerSwitch());
    ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerDisconnect());
    
    BungeeCord.getInstance().getPluginManager().registerListener(this, this);
    
    sql = new sqlmanager();
    if (sql.openConnection(getConfig()))
    {
      BungeeCord.getInstance().getLogger().info("[ArcaneParty] Connection to database established! ");
      sql.submitQuery("CREATE TABLE IF NOT EXISTS `players` (`username` varchar(30) NOT NULL,`uuid` varchar(100) NOT NULL, PRIMARY KEY (`uuid`)) ENGINE=InnoDB DEFAULT CHARSET=latin1");
      sql.submitQuery("CREATE TABLE IF NOT EXISTS `pendingrequests` (`requested` varchar(100) NOT NULL, `requestee` varchar(100) NOT NULL, PRIMARY KEY (`requested`,`requestee`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1");
      sql.submitQuery("CREATE TABLE IF NOT EXISTS `relationships` ( `primaryuser` varchar(60) NOT NULL, `secondaryuser` varchar(60) NOT NULL, PRIMARY KEY (`primaryuser`,`secondaryuser`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1");
    }
  }
  
  public static Plugin getInstance()
  {
    return plugin;
  }

  
  public static void loadConfig()
  {
    try
    {
      f = new File(plugin.getDataFolder(), "config.yml");
      if (!plugin.getDataFolder().exists())
      {
        plugin.getDataFolder().mkdir();
        if (!f.exists()) {
          Files.copy(plugin.getResourceAsStream("config.yml"), f.toPath(), new CopyOption[0]);
        }
      }
      c = ConfigurationProvider.getProvider(YamlConfiguration.class).load(f);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void save()
  {
    try
    {
      ConfigurationProvider.getProvider(YamlConfiguration.class).save(c, f);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  @EventHandler
  public void onJoin(PostLoginEvent evt)
  {
    sql.submitQuery("INSERT IGNORE INTO `players`(`username`, `uuid`) VALUES ('" + evt.getPlayer().getName() + "','" + evt.getPlayer().getUUID() + "')");
    sql.submitQuery("UPDATE `players` SET `username` = '" + evt.getPlayer().getName() + "' WHERE `uuid` = '" + evt.getPlayer().getUUID() + "';");
    for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
      try
      {
        if (sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + p.getUUID() + "' AND `secondaryuser` LIKE '" + evt.getPlayer().getUUID() + "'").next())
        {
          p.sendMessage(ChatColor.YELLOW + "Your friend " + evt.getPlayer().getName() + ChatColor.YELLOW + " joined!");
          friendJoinObj frand = new friendJoinObj(evt.getPlayer().getName(), p.getName());
          friendJoinTemp.put(Integer.valueOf(frand.getID()), frand);
        }
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
    
    BungeeCord.getInstance().getScheduler().runAsync(this, new Runnable()
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
        
        final String plName = evt.getPlayer().getName();
        
        ArrayList<Integer> keysToRemove = new ArrayList();
        for (Iterator i$ = friendJoinTemp.keySet().iterator(); i$.hasNext();)
        {
          int i = ((Integer)i$.next()).intValue();
          if (((friendJoinObj)friendJoinTemp.get(Integer.valueOf(i))).playerWhoJoined.equals(plName)) {
            keysToRemove.add(Integer.valueOf(i));
          }
        }
        for (Iterator i$ = keysToRemove.iterator(); i$.hasNext();)
        {
          int i = ((Integer)i$.next()).intValue();
          friendJoinTemp.remove(Integer.valueOf(i));
        }
      }
    });
  }
  
  @EventHandler
  public void onLeave(PlayerDisconnectEvent evt)
  {
    for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
      try
      {
        if (sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + p.getUUID() + "' AND `secondaryuser` LIKE '" + evt.getPlayer().getUUID() + "'").next())
        {
          p.sendMessage(ChatColor.RED + "Your friend " + evt.getPlayer().getName() + " disconnected!");
          friendLeaveObj frand = new friendLeaveObj(evt.getPlayer().getName(), p.getName());
          friendLeaveTemp.put(Integer.valueOf(frand.getID()), frand);
        }
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
    
    BungeeCord.getInstance().getScheduler().runAsync(this, new Runnable()
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
        
        final String plName = evt.getPlayer().getName();
        
        ArrayList<Integer> keysToRemove = new ArrayList();
        for (Iterator i$ = friendJoinTemp.keySet().iterator(); i$.hasNext();)
        {
          int i = ((Integer)i$.next()).intValue();
          if (((friendLeaveObj)friendLeaveTemp.get(Integer.valueOf(i))).playerWhoDCd.equals(plName)) {
            keysToRemove.add(Integer.valueOf(i));
          }
        }
        for (Iterator i$ = keysToRemove.iterator(); i$.hasNext();)
        {
          int i = ((Integer)i$.next()).intValue();
          friendLeaveTemp.remove(Integer.valueOf(i));
        }
      }
    });
  }
  
  
  public static Configuration getConfig()
  {
    return c;
  }
  
}
