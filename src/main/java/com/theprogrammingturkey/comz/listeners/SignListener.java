package com.theprogrammingturkey.comz.listeners;

import com.theprogrammingturkey.comz.COMZombies;
import com.theprogrammingturkey.comz.economy.PointManager;
import com.theprogrammingturkey.comz.game.Game;
import com.theprogrammingturkey.comz.game.GameManager;
import com.theprogrammingturkey.comz.game.features.Barrier;
import com.theprogrammingturkey.comz.game.signs.*;
import com.theprogrammingturkey.comz.util.BlockUtils;
import com.theprogrammingturkey.comz.util.CommandUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class SignListener implements Listener
{
	private static final Map<String, IGameSign> GAME_SIGNS = new HashMap<>();
	public static final GameManager INSTANCE = new GameManager();

	static
	{
		MysteryBoxSign sign = new MysteryBoxSign();
		GAME_SIGNS.put("Mystery Box", sign);
		GAME_SIGNS.put("mysterybox", sign);
		GAME_SIGNS.put("box", sign);
		GAME_SIGNS.put("randombox", sign);

		JoinSign join = new JoinSign();
		GAME_SIGNS.put("Join", join);
		GAME_SIGNS.put("join", join);

		SpectateSign spectateSign = new SpectateSign();
		GAME_SIGNS.put("Spectate", spectateSign);
		GAME_SIGNS.put("spectate", spectateSign);
		GAME_SIGNS.put("Spec", spectateSign);
		GAME_SIGNS.put("spec", spectateSign);

		KitSign kit = new KitSign();
		GAME_SIGNS.put("Kit", kit);
		GAME_SIGNS.put("kit", kit);

		PerkMachineSign perk = new PerkMachineSign();
		GAME_SIGNS.put("Perk Machine", perk);
		GAME_SIGNS.put("perk", perk);
		GAME_SIGNS.put("perk machine", perk);
		GAME_SIGNS.put("Perk machine", perk);

		PackAPunchSign packAPunch = new PackAPunchSign();
		GAME_SIGNS.put("Pack-a-Punch", packAPunch);
		GAME_SIGNS.put("pack", packAPunch);
		GAME_SIGNS.put("pack-a-punch", packAPunch);
		GAME_SIGNS.put("pack a punch", packAPunch);
		GAME_SIGNS.put("Pack a Punch", packAPunch);

		DoorSign door = new DoorSign();
		GAME_SIGNS.put("Door", door);
		GAME_SIGNS.put("door", door);

		GunSign gun = new GunSign();
		GAME_SIGNS.put("Weapon", gun);
		GAME_SIGNS.put("weapon", gun);

		PowerSign power = new PowerSign();
		GAME_SIGNS.put("Power", power);
		GAME_SIGNS.put("power", power);

		TeleporterSign teleporter = new TeleporterSign();
		GAME_SIGNS.put("Teleporter", teleporter);
		GAME_SIGNS.put("teleporter", teleporter);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreakEvent(BlockBreakEvent event)
	{
		if(!BlockUtils.isSign(event.getBlock().getType()))
			return;

		Sign sign = (Sign) event.getBlock().getState();

		String lineOne = ChatColor.stripColor(sign.getLine(0));
		String lineTwo = ChatColor.stripColor(sign.getLine(1));
		if(lineOne.equalsIgnoreCase("[Zombies]"))
		{
			Game game = GameManager.INSTANCE.getGame(sign.getLocation());
			IGameSign signLogic = GAME_SIGNS.get(lineTwo);
			if(signLogic != null && (game != null || !signLogic.requiresGame()))
				signLogic.onBreak(INSTANCE.getGame(sign.getLocation()), event.getPlayer(), sign);
		}
		else if(lineOne.equalsIgnoreCase("[BarrierRepair]"))
		{
			Player player = event.getPlayer();
			if(GameManager.INSTANCE.isPlayerInGame(player))
			{
				Game game = GameManager.INSTANCE.getGame(player);
				Barrier b = game.barrierManager.getBarrierFromRepair(sign.getLocation());
				if(b != null)
				{
					b.repair();
					PointManager.addPoints(player, b.getReward());
					PointManager.notifyPlayer(player);
					event.setCancelled(true);
				}
				else
				{
					CommandUtil.sendMessageToPlayer(player, "Congrats! You broke the plugin! JK its all fixed now.");
					BlockUtils.setBlockToAir(event.getBlock());
				}
			}
		}
	}

	@EventHandler
	public void RightClickSign(PlayerInteractEvent event)
	{
		if(event.getClickedBlock() == null)
			return;

		COMZombies plugin = COMZombies.getPlugin();

		if(BlockUtils.isSign(event.getClickedBlock().getType()))
		{
			Sign sign = (Sign) event.getClickedBlock().getState();
			Player player = event.getPlayer();

			if(event.getAction() == Action.RIGHT_CLICK_BLOCK && player.isSneaking() && player.isOp())
			{
				String Line1 = ChatColor.stripColor(sign.getLine(0));
				if(!plugin.isEditingASign.containsKey(player) && Line1.equalsIgnoreCase("[Zombies]") && !GameManager.INSTANCE.isPlayerInGame(player))
				{
					plugin.isEditingASign.put(player, sign);
					CommandUtil.sendMessageToPlayer(player, "You are now editing a sign!");
					return;
				}
			}

			String lineOne = ChatColor.stripColor(sign.getLine(0));
			String lineTwo = ChatColor.stripColor(sign.getLine(1));
			if(lineOne.equalsIgnoreCase("[Zombies]"))
			{
				Game game = GameManager.INSTANCE.getGame(sign.getLocation());
				IGameSign signLogic = GAME_SIGNS.get(lineTwo);
				if(signLogic != null && (game != null || !signLogic.requiresGame()))
				{
					if(game != null && signLogic.requiresGame() && game.getMode() != Game.ArenaStatus.INGAME)
						return;
					signLogic.onInteract(game, player, sign);
				}
			}
		}
	}

	@EventHandler
	public void eventSignChanged(SignChangeEvent event)
	{
		if(!BlockUtils.isSign(event.getBlock().getType()))
			return;
		String lineOne = ChatColor.stripColor(event.getLine(0));
		String lineTwo = ChatColor.stripColor(event.getLine(1));
		if(lineOne != null && lineOne.equalsIgnoreCase("[Zombies]"))
		{
			Game game = GameManager.INSTANCE.getGame(event.getBlock().getLocation());
			IGameSign signLogic = GAME_SIGNS.get(lineTwo);
			if(signLogic != null && (game != null || !signLogic.requiresGame()))
			{
				signLogic.onChange(game, event.getPlayer(), event);
			}
			else if(signLogic == null)
			{
				event.setLine(0, ChatColor.RED + "" + ChatColor.BOLD + lineTwo);
				event.setLine(1, ChatColor.RED + "" + ChatColor.BOLD + "is not a");
				event.setLine(2, ChatColor.RED + "" + ChatColor.BOLD + "valid sign");
				event.setLine(3, "");

			}
			else
			{
				event.setLine(0, ChatColor.RED + "" + ChatColor.BOLD + "Sign is");
				event.setLine(1, ChatColor.RED + "" + ChatColor.BOLD + "not in");
				event.setLine(2, ChatColor.RED + "" + ChatColor.BOLD + "an arena!");
				event.setLine(3, "");
			}
		}
	}
}
