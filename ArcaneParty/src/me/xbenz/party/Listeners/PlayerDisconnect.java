package me.xbenz.party.Listeners;

import me.xbenz.party.Main.Main;
import me.xbenz.party.Main.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnect
  implements Listener
{
  @EventHandler
  public void leave(PlayerDisconnectEvent e)
  {
    ProxiedPlayer player = e.getPlayer();
    if ((Main.partyleader.contains(player.getName())) || (Main.inparty.containsKey(player.getName()))) {
      Utils.leaveParty(player);
    }
  }
}
