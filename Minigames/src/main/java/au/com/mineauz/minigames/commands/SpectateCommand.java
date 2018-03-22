package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpectateCommand implements ICommand {

	@Override
	public String getName() {
		return "spectate";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"spec"};
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Allows a player to force spectate a Minigame.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame spectate <Minigame>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to use the spectate command!";
	}

	@Override
	public String getPermission() {
		return "minigame.spectate";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
            if (plugin.minigameManager.hasMinigame(args[0])) {
                MinigamePlayer ply = plugin.playerManager.getMinigamePlayer((Player) sender);
                Minigame mgm = plugin.minigameManager.getMinigame(args[0]);
                plugin.playerManager.spectateMinigame(ply, mgm);
			}
			else{
				sender.sendMessage(ChatColor.RED + "No Minigame found by the name: " + args[0]);
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
        List<String> mgs = new ArrayList<>(plugin.minigameManager.getAllMinigames().keySet());
		return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
	}

}