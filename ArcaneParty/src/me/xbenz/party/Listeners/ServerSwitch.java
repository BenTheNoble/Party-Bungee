package me.xbenz.party.Listeners;

import java.util.concurrent.TimeUnit;
import me.xbenz.party.Main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerSwitch
  implements Listener
{
  @EventHandler
  public void onSwitch(ServerSwitchEvent e)
  {
    final ProxiedPlayer player = e.getPlayer();
    ProxyServer.getInstance().getScheduler().schedule(Main.plugin, new Runnable()
    {
      public void run()
      {
        if (Main.partyleader.contains(player.getName()))
        {
          ServerInfo serverinfo = player.getServer().getInfo();
          player.sendMessage(Main.prefix + ChatColor.GRAY + "The party switched to " + ChatColor.YELLOW + serverinfo.getName());
          for (ProxiedPlayer target : ProxyServer.getInstance().getPlayers()) {
            if ((Main.inparty.containsKey(target.getName())) && (Main.inparty.get(target.getName()) == player.getName()))
            {
              target.sendMessage(Main.prefix + ChatColor.GRAY + "The party switched to " + ChatColor.YELLOW + serverinfo.getName());
              if (!target.getServer().getInfo().equals(serverinfo)) {
                target.connect(serverinfo);
              }
            }
          }
        }
      }
    }, 2L, TimeUnit.SECONDS);
  }
}