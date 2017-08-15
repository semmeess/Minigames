package au.com.mineauz.minigames.backend;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.TestUtilities;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import jdk.nashorn.internal.runtime.StoredScript;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 20/06/2017.
 */
public class BackendManagerITest {

    private FileConfiguration config;
    Logger logger;


    @Before
    public void setup() throws Exception
    {

        TestUtilities utils = new TestUtilities();
        logger = utils.getLogger();
        config = utils.createTestConfig();
        ConfigurationSection backend = config.getConfigurationSection("backend");
        backend.set("type","mysql");
    }


    @Test
    public void initialize() throws Exception {
        BackendManager manager = new BackendManager(logger);
        manager.initialize(config);
        Minigame minigame = mock(Minigame.class);

        minigame.setDisplayName("TestGame");
        minigame.setMaxPlayers(1);
        Player bukkitPlayer = mock(Player.class);
        UUID uuid = UUID.randomUUID();
        when(bukkitPlayer.getUniqueId()).thenReturn(uuid);
        MinigamePlayer player = mock(MinigamePlayer.class);
        when(player.getPlayer()).thenReturn(bukkitPlayer);
        minigame.addPlayer(player);
        StoredGameStats stats = new StoredGameStats(minigame, player);
        MinigameStat stat = MinigameStats.Wins;
        stats.addStat(stat, 1);
        manager.saveStats(stats);
        when(minigame.getSettings(MinigameStats.Wins)).thenReturn(new StatSettings(stat,StatFormat.Total,"wins"));
        StatSettings settings = minigame.getSettings(stat);
        StatValueField[] field = settings.getFormat().getFields();
        ListenableFuture<List<StoredStat>> future = manager.loadStats(minigame, stat, field[0], ScoreboardOrder.ASCENDING, 0, 1);
        Futures.addCallback(future, new FutureCallback<List<StoredStat>>() {
            @Override
            public void onSuccess(@Nullable List<StoredStat> storedStats) {
                for(StoredStat ret: storedStats){
                    assertTrue(ret.getValue() == 1);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                    throwable.printStackTrace();
                    fail("Could not retrieve Stat: "  + throwable.getMessage());
            }
        });
        future.get();
    }


    @Test
    public void shutdown() throws Exception {
    }

    @Test
    public void toggleDebug() throws Exception {
    }

    @Test
    public void isDebugging() throws Exception {
    }

}