package me.xbenz.party.Commands;

import me.xbenz.party.Main.Main;
import me.xbenz.party.Main.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Party
extends Command
{
public Party()
{
  super("party");
}

public void execute(CommandSender cs, String[] args)
{
  if ((cs instanceof ProxiedPlayer))
  {
    ProxiedPlayer player = (ProxiedPlayer)cs;
    if (args.length > 0)
    {
      if (args[0].equalsIgnoreCase("list"))
      {
        Utils.listParty(player);
        return;
      }
      if (args[0].equalsIgnoreCase("leave"))
      {
        Utils.leaveParty(player);return;
      }
      if (args[0].equalsIgnoreCase("chat"))
      {
        String msg = "";
        for (int i = 1; i < args.length; i++) {
          msg = msg + args[i] + " ";
        }
        Utils.chat(player, msg);
        return;
      }
      if (args[0].equalsIgnoreCase("invite"))
      {
        if (args.length > 1)
        {
          for (ProxiedPlayer target : ProxyServer.getInstance().getPlayers())
          {
            if (args[1].toLowerCase().equalsIgnoreCase(player.getName().toLowerCase()))
            {
              player.sendMessage(Main.prefix + ChatColor.GRAY + "You cannot invite yourself!");
              return;
            }
            if (target.getName().toLowerCase().equalsIgnoreCase(args[1].toLowerCase()))
            {
              ProxiedPlayer newtarget = ProxyServer.getInstance().getPlayer(args[1]);
              Utils.invitePlayer(player, newtarget);
              return;
            }
          }
          player.sendMessage(Main.prefix + ChatColor.GRAY + "Could not find " + ChatColor.YELLOW + args[1]);
        }
        else
        {
          player.sendMessage(Main.prefix + ChatColor.GRAY + "Usage: /party invite <user>");
        }
        return;
      }
      if (args[0].equalsIgnoreCase("accept"))
      {
        Utils.acceptInvite(player);
        return;
      }
      if (args[0].equalsIgnoreCase("kick"))
      {
        if (args.length > 1)
        {
          for (ProxiedPlayer target : ProxyServer.getInstance().getPlayers()) {
            if (target.getName().equalsIgnoreCase(args[1]))
            {
              Utils.kickPlayer(target, player);
              return;
            }
          }
          player.sendMessage(Main.prefix + ChatColor.GRAY + "Could not find " + ChatColor.YELLOW + args[1]);
        }
        else
        {
          player.sendMessage(Main.prefix + ChatColor.GRAY + "Usage: /party kick <user>");
        }
        return;
      }
    }
    player.sendMessage(ChatColor.GOLD + "-------------------------------------------------");
    player.sendMessage(Main.prefix + ChatColor.GRAY + "/party invite <user>" + ChatColor.YELLOW + " - Invites a user to the party");
    player.sendMessage(Main.prefix + ChatColor.GRAY + "/party kick <user>" + ChatColor.YELLOW + " - Kicks a user from the party");
    player.sendMessage(Main.prefix + ChatColor.GRAY + "/party accept" + ChatColor.YELLOW + " - Accept a party request");
    player.sendMessage(Main.prefix + ChatColor.GRAY + "/party leave" + ChatColor.YELLOW + " - Leaves the current party");
    player.sendMessage(Main.prefix + ChatColor.GRAY + "/party list" + ChatColor.YELLOW + " - Lists all members of the party");
    player.sendMessage(Main.prefix + ChatColor.GRAY + "/party chat" + ChatColor.YELLOW + " - Talk in party chat");
    player.sendMessage(ChatColor.GOLD + "-------------------------------------------------");
  }
}
}
