package com.theprogrammingturkey.comz.commands;

import com.theprogrammingturkey.comz.util.CommandUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.theprogrammingturkey.comz.COMZombies;
import com.theprogrammingturkey.comz.game.Game;
import com.theprogrammingturkey.comz.game.GameManager;

public class EnableCommand implements SubCommand
{
	@Override
	public boolean onCommand(Player player, String[] args)
	{
		if(player.hasPermission("zombies.enable") || player.hasPermission("zombies.admin"))
		{
			if(args.length == 1)
			{
				CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "Incorrect usage! Please use /zombies enable [arena]");
				return true;
			}
			else
			{
				if(GameManager.INSTANCE.isValidArena(args[1]))
				{
					Game game = GameManager.INSTANCE.getGame(args[1]);
					if(game == null)
					{
						CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "That arena does not exist!");
					}
					else
					{
						game.setEnabled();
						game.signManager.updateGame();
						CommandUtil.sendMessageToPlayer(player, ChatColor.GREEN + "Arena " + game.getName() + " has been enabled!");
						return true;
					}
				}
				else
				{
					CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "That arena does not exist!");
				}
			}
		}
		else
		{
			CommandUtil.noPermission(player, "enable this arena");
		}
		return false;
	}

}
