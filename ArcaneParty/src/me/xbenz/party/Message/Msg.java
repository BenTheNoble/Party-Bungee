package me.xbenz.party.Message;

import java.util.HashMap;

import me.xbenz.party.Main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Msg extends Command {
	
	  public Msg(String name)
	  {
	    super(name);
	  }

  public static HashMap<String, String> lastMessage = new HashMap();
  
  public void execute(CommandSender sender, String[] args)
  {
    if (args.length < 2)
    {
      sender.sendMessage(new TextComponent(Main.prefix3 + ChatColor.GRAY + "Usage: /msg <player> <message>"));
      return;
    }
    ProxiedPlayer p = ProxyServer.getInstance().getPlayer(args[0]);
    if (p == null)
    {
      sender.sendMessage(new TextComponent(Main.prefix3 + ChatColor.YELLOW + args[0] + ChatColor.GRAY + " is not online!"));
      return;
    }
    StringBuilder msgBuilder = new StringBuilder();
    for (int i = 1; i < args.length; i++) {
      msgBuilder.append(args[i]).append(" ");
    }
    String msg = ChatColor.translateAlternateColorCodes('&', msgBuilder.toString().trim());
    
    p.sendMessage(new TextComponent(ChatColor.YELLOW + "" + ChatColor.BOLD + sender.getName() + ChatColor.GOLD + ChatColor.BOLD + " -> " + ChatColor.YELLOW + ChatColor.BOLD + p.getDisplayName() + ChatColor.GOLD + " " + msg));
    sender.sendMessage(new TextComponent(ChatColor.YELLOW + "" + ChatColor.BOLD + sender.getName() + ChatColor.GOLD + ChatColor.BOLD + " -> " + ChatColor.YELLOW + ChatColor.BOLD + p.getDisplayName() + ChatColor.GOLD + " " + msg));
    if (lastMessage.containsKey(sender.getName())) {
      lastMessage.remove(sender.getName());
    }
    lastMessage.put(sender.getName(), p.getName());
    if (lastMessage.containsKey(p.getName())) {
      lastMessage.remove(p.getName());
    }
    lastMessage.put(p.getName(), sender.getName());
  }
}

