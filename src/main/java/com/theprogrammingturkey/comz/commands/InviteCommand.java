package com.theprogrammingturkey.comz.commands;

import com.theprogrammingturkey.comz.game.Game;
import com.theprogrammingturkey.comz.game.Game.ArenaStatus;
import com.theprogrammingturkey.comz.game.GameManager;
import com.theprogrammingturkey.comz.util.CommandUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class InviteCommand implements SubCommand
{
	@Override
	public boolean onCommand(Player player, String[] args)
	{
		if(player.hasPermission("zombies.admin"))
		{
			if(args.length == 1)
			{
				CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "" + ChatColor.BOLD + "Not entered correctly! (1 arg)");
			}
			else
			{
				//if invite player arena
				String toInvite = args[1];
				if(Bukkit.getPlayer(toInvite) != null) 
				{
					Player invite = Bukkit.getPlayer(toInvite);
					if(GameManager.INSTANCE.isPlayerInGame(invite))
					{
						CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "They must leave their current game first!");
						return true;
					}
					if(GameManager.INSTANCE.isValidArena(args[2]))
					{
						Game game = GameManager.INSTANCE.getGame(args[2]);
						if(game.getMode() != ArenaStatus.DISABLED && game.getMode() != ArenaStatus.INGAME)
						{
							if(game.getMode() == ArenaStatus.INGAME)
							{
								CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "" + ChatColor.BOLD + "They're already in game!");
								return true;
							}
							if(game.spawnManager.getPoints().size() == 0)
							{
								CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "" + ChatColor.BOLD + "Arena has no spawn points!");
								return true;
							}
							if(game.maxPlayers <= game.players.size())
							{
								CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "" + ChatColor.BOLD + "Game is full!");
								return true;
							}
							if(invite.hasPermission("zombies.join." + game.getName()))
							{
								game.addPlayer(invite);
								CommandUtil.sendMessageToPlayer(player, ChatColor.GOLD + "" + ChatColor.BOLD + "They joined " + game.getName());
								return true;
							}
							else
							{
								CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "They do not have permission to join this game!");
								return false;
							}
						}
						else
						{
							String toSay = game.getMode().toString();
							if(toSay.equalsIgnoreCase("ingame"))
							{
								CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "" + ChatColor.BOLD + "This arena is in game!");
								return true;
							}
							else if(toSay.equalsIgnoreCase("disabled"))
							{
								CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "" + ChatColor.BOLD + "This arena is disabled!");
								return true;
							}
						}
					}
				}
				else
				{
					CommandUtil.sendMessageToPlayer(player, ChatColor.RED + "" + ChatColor.BOLD + "There is no arena called " + ChatColor.GOLD + args[2]);
					return true;
				}
			}
		}
		else
		{
			CommandUtil.noPermission(player, "join this game");
			return true;
		}
		return false;
	}
}
