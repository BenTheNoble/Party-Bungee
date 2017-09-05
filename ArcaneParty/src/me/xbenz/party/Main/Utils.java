package me.xbenz.party.Main;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;


public class Utils
{
  public static void createParty(ProxiedPlayer player)
  {
    if ((Main.inparty.containsKey(player.getName())) || (Main.partyleader.contains(player.getName())))
    {
      player.sendMessage(Main.prefix + ChatColor.GRAY + "You are already in a party!");
    }
    else
    {
      Main.partyleader.add(player.getName());
      player.sendMessage(Main.prefix + ChatColor.GRAY + "Your party has been created!");
    }
  }
  
  public static void listParty(ProxiedPlayer player)
  {
    if (Main.partyleader.contains(player.getName()))
    {
      String players = "";
      int count = 0;
      for (ProxiedPlayer target : ProxyServer.getInstance().getPlayers()) {
        if ((Main.inparty.containsKey(target.getName())) && (Main.inparty.get(target.getName()) == player.getName()))
        {
          if (count != 0) {
            players = players + ", ";
          }
          players = players + target.getName();
          count++;
        }
      }
      player.sendMessage(Main.prefix + ChatColor.GRAY + "Party Leader" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + player.getName());
      player.sendMessage(Main.prefix + ChatColor.GRAY + "Party Members" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + players);
    }
    else if (Main.inparty.containsKey(player.getName()))
    {
      String players = "";
      int count = 0;
      for (ProxiedPlayer target : ProxyServer.getInstance().getPlayers()) {
        if ((Main.inparty.containsKey(target.getName())) && (Main.inparty.get(target.getName()) == Main.inparty.get(player.getName())))
        {
          if (count != 0) {
            players = players + ", ";
          }
          players = players + target.getName();
          count++;
        }
      }
      player.sendMessage(Main.prefix + ChatColor.GRAY + "Party Leader" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + (String)Main.inparty.get(player.getName()));
      player.sendMessage(Main.prefix + ChatColor.GRAY + "Party Members" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + players);
    }
    else
    {
      player.sendMessage(Main.prefix + ChatColor.GRAY + "You are not in a party!");
    }
  }
  
  public static void leaveParty(ProxiedPlayer player)
  {
    if (Main.inparty.containsKey(player.getName()))
    {
      player.sendMessage(Main.prefix + ChatColor.GRAY + "You left the party!");
      for (ProxiedPlayer inParty : ProxyServer.getInstance().getPlayers()) {
        if ((Main.inparty.containsKey(inParty.getName())) && (Main.inparty.get(inParty.getName()) == Main.inparty.get(player.getName())))
        {
          ProxiedPlayer target = ProxyServer.getInstance().getPlayer((String)Main.inparty.get(player.getName()));
          target.sendMessage(Main.prefix + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " left the party!");
        }
      }
      Main.inparty.remove(player.getName());
    }
    else if (Main.partyleader.contains(player.getName()))
    {
      player.sendMessage(Main.prefix + ChatColor.GRAY + "You disbanded the party!");
      Main.partyleader.remove(player.getName());
      for (ProxiedPlayer target : ProxyServer.getInstance().getPlayers()) {
        if ((Main.inparty.containsKey(target.getName())) && (Main.inparty.get(target.getName()) == player.getName()))
        {
          target.sendMessage(Main.prefix + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " disbanded the party!");
          Main.inparty.remove(target.getName());
        }
      }
    }
    else
    {
      player.sendMessage(Main.prefix + ChatColor.GRAY + "You are not in a party!");
    }
  }
  
  public static void chat(ProxiedPlayer p, String message)
  {
    if (p.hasPermission("party.chat.color")) {
      message = ChatColor.translateAlternateColorCodes('&', message);
    }
    if (Main.partyleader.contains(p.getName()))
    {
      p.sendMessage(ChatColor.BLUE + "[PartyChat] " + ChatColor.YELLOW + p.getName() + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + message);
      for (ProxiedPlayer target : ProxyServer.getInstance().getPlayers()) {
        if ((Main.inparty.containsKey(target.getName())) && (Main.inparty.get(target.getName()) == p.getName())) {
          target.sendMessage(ChatColor.BLUE + "[PartyChat] " + ChatColor.YELLOW + p.getName() + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + message);
        }
      }
    }
    else if (Main.inparty.containsKey(p.getName()))
    {
      ProxyServer.getInstance().getPlayer((String)Main.inparty.get(p.getName())).sendMessage(ChatColor.BLUE + "[PartyChat] " + ChatColor.YELLOW + p.getName() + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + message);
      for (ProxiedPlayer target : ProxyServer.getInstance().getPlayers()) {
        if ((Main.inparty.containsKey(target.getName())) && (Main.inparty.get(target.getName()) == Main.inparty.get(p.getName()))) {
          target.sendMessage(ChatColor.BLUE + "[PartyChat] " + ChatColor.YELLOW + p.getName() + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + message);
        }
      }
    }
    else
    {
      p.sendMessage(Main.prefix + ChatColor.GRAY + "You are not in a party!");
    }
  }
  
  public static void invitePlayer(ProxiedPlayer player, ProxiedPlayer target)
  {
    if (Main.partyleader.contains(player.getName()))
    {
      int i = 0;
      for (ProxiedPlayer inParty : ProxyServer.getInstance().getPlayers()) {
        if ((Main.inparty.containsKey(inParty.getName())) && (Main.inparty.get(inParty.getName()) == player.getName())) {
          i++;
        }
      }
      if (i >= (player.hasPermission("party.premium") ? Main.PremiumPartySize : Main.PartySize))
      {
        player.sendMessage(Main.prefix + ChatColor.GRAY + "You reached the maximum amount of party members!");
      }
      else if (Main.inparty.containsKey(target.getName()))
      {
        player.sendMessage(Main.prefix + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " is already in a party!");
      }
      else if (Main.partyleader.contains(target.getName()))
      {
        player.sendMessage(Main.prefix + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " is already in a party!");
      }
      else if ((!Main.inparty.containsKey(target.getName())) && (!Main.partyleader.contains(target.getName())))
      {
        Main.invite.put(target.getName(), player.getName());
        player.sendMessage(Main.prefix + ChatColor.GRAY + "You invited " + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " to the party!");
        target.sendMessage(Main.prefix + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " invited you to their party! Do /party accept to join!");
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new Runnable()
        {
            public void run()
            {
              Main.invite.remove(null);
            }
        }, 5L, TimeUnit.MINUTES);
      }
    }
    else if (Main.inparty.containsKey(player.getName()))
    {
      player.sendMessage(Main.prefix + ChatColor.GRAY + "You are not the party leader!");
    }
    else
    {
      createParty(player);
      invitePlayer(player, target);
    }
  }


public static void acceptInvite(ProxiedPlayer player)
  {
    if ((Main.partyleader.contains(player.getName())) || (Main.inparty.containsKey(player.getName())))
    {
      player.sendMessage(Main.prefix + ChatColor.GRAY + "You are already in a party!");
    }
    else if (Main.invite.containsKey(player.getName()))
    {
      int i = 0;
      ProxiedPlayer target = ProxyServer.getInstance().getPlayer((String)Main.invite.get(player.getName()));
      for (ProxiedPlayer inParty : ProxyServer.getInstance().getPlayers()) {
        if ((Main.inparty.containsKey(inParty.getName())) && (Main.inparty.get(inParty.getName()) == target.getName())) {
          i++;
        }
      }
      if (i >= (target.hasPermission("party.premium") ? Main.PremiumPartySize : Main.PartySize))
      {
        player.sendMessage(Main.prefix + ChatColor.GRAY + "You reached the maximum amount of party members!");
      }
      else
      {
        for (ProxiedPlayer inParty : ProxyServer.getInstance().getPlayers()) {
          if ((Main.inparty.containsKey(inParty.getName())) && (Main.inparty.get(inParty.getName()) == target.getName())) {
            inParty.sendMessage(Main.prefix + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " joined the party!");
          }
        }
        target.sendMessage(Main.prefix + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " joined the party!");
        Main.invite.remove(player.getName());
        Main.inparty.put(player.getName(), target.getName());
        player.sendMessage(Main.prefix + ChatColor.GRAY + "You joined " + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " party!");
      }
    }
    else
    {
      player.sendMessage(Main.prefix + ChatColor.GRAY + "You do not have any party invites!");
    }
  }
  
  public static void kickPlayer(ProxiedPlayer player, ProxiedPlayer target)
  {
    if (Main.partyleader.contains(target.getName()))
    {
      if ((Main.inparty.containsKey(player.getName())) && (Main.inparty.get(player.getName()) == target.getName()))
      {
        Main.inparty.remove(player.getName());
        player.sendMessage(Main.prefix + ChatColor.GRAY + "You were kicked out of " + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " party!");
        target.sendMessage(Main.prefix + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " was kicked from the party!");
        for (ProxiedPlayer inParty : ProxyServer.getInstance().getPlayers()) {
          if ((Main.inparty.containsKey(inParty.getName())) && (Main.inparty.get(inParty.getName()) == target.getName())) {
            inParty.sendMessage(Main.prefix + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " was kicked from the party!");
          }
        }
      }
      else
      {
        target.sendMessage(Main.prefix + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " is not in the party!");
      }
    }
    else {
      target.sendMessage(Main.prefix + ChatColor.GRAY + "You are not the party leader!");
    }
  }
}
