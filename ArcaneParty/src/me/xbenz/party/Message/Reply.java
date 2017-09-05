package me.xbenz.party.Message;

import me.xbenz.party.Main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Reply
  extends Command
{
  public Reply(String name)
  {
    super(name);
  }
  
  public void execute(CommandSender sender, String[] args)
  {
    if (!Msg.lastMessage.containsKey(sender.getName()))
    {
      sender.sendMessage(new TextComponent(Main.prefix3 + ChatColor.GRAY + "You do not have anyone to reply to!"));
      return;
    }
    ProxiedPlayer target = ProxyServer.getInstance().getPlayer((String)Msg.lastMessage.get(sender.getName()));
    if (target == null)
    {
      sender.sendMessage(new TextComponent(Main.prefix3 + ChatColor.YELLOW + (String)Msg.lastMessage.get(sender.getName()) + ChatColor.GRAY + " is not online!"));
      return;
    }
    String message = "";
    for (int i = 0; i < args.length; i++) {
      message = message + args[i] + " ";
    }
    message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message.trim()));
    
    target.sendMessage(new TextComponent(ChatColor.YELLOW + "" + ChatColor.BOLD + sender.getName() + ChatColor.GOLD + "" + ChatColor.BOLD + " -> " + ChatColor.YELLOW + "" + ChatColor.BOLD + target.getDisplayName() + ChatColor.GOLD + " " + message));
    sender.sendMessage(new TextComponent(ChatColor.YELLOW + "" + ChatColor.BOLD + sender.getName() + ChatColor.GOLD + "" + ChatColor.BOLD + " -> " + ChatColor.YELLOW + "" + ChatColor.BOLD + target.getDisplayName() + ChatColor.GOLD + " " + message));
    if (Msg.lastMessage.containsKey(sender.getName())) {
      Msg.lastMessage.remove(sender.getName());
    }
    Msg.lastMessage.put(sender.getName(), target.getName());
    if (Msg.lastMessage.containsKey(target.getName())) {
      Msg.lastMessage.remove(target.getName());
    }
    Msg.lastMessage.put(target.getName(), sender.getName());
  }
}
