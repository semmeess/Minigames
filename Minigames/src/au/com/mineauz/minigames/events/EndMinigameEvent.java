package au.com.mineauz.minigames.events;

import java.util.List;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.Bukkit;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class EndMinigameEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private List<MinigamePlayer> winners = null;
	private List<MinigamePlayer> losers = null;
	private Minigame mgm = null;
	private boolean cancelled = false;
	
	public EndMinigameEvent(List<MinigamePlayer> winners, List<MinigamePlayer> losers, Minigame minigame){
		this.winners = winners;
		this.losers = losers;
		mgm = minigame;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public List<MinigamePlayer> getWinners(){
		return winners;
	}
	
	public List<MinigamePlayer> getLosers(){
		return losers;
	}

	public Minigame getMinigame() {
		Bukkit.getSever().dispatchCommand(getServer().getConsoleSender, "sync console all broadcast TDM is starting!");
		Bukkit.getSever().dispatchCommand(getServer().getConsoleSender, "sync console all broadcast §bJoin now with §d/server §bto win 20 tokens!");
		return mgm;
	}

	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
