package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EditCommand implements ICommand {

	@Override
	public String getName() {
		return "edit";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Lets you edit a Minigame using a neat menu. Clicking on the menu items will allow you to change the settings of the Minigame.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame edit <Minigame>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to use the Minigame edit menu.";
	}

	@Override
	public String getPermission() {
		return "minigame.edit";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		
		if(args != null){
            if (plugin.minigameManager.hasMinigame(args[0])) {
                Minigame mgm = plugin.minigameManager.getMinigame(args[0]);
                mgm.displayMenu(plugin.playerManager.getMinigamePlayer((Player) sender));
//				Menu menu = new Menu(6, "Edit: " + mgm.getName(), plugin.playerManager.getMinigamePlayer((Player)sender));
//				int slot = 0;
//				for(MenuItem item : mgm.getMenuItems()){
//					menu.addItem(item, slot);
//					slot++;
//				}
//				menu.displayMenu(plugin.playerManager.getMinigamePlayer((Player)sender));
			}
			else{
				sender.sendMessage(ChatColor.RED + "There is no Minigame by the name " + args[0]);
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args != null && args.length == 1){
            List<String> mgs = new ArrayList<>(plugin.minigameManager.getAllMinigames().keySet());
			return MinigameUtils.tabCompleteMatch(mgs, args[0]);
		}
		return null;
	}

}