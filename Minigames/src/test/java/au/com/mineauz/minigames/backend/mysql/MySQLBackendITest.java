package au.com.mineauz.minigames.backend.mysql;

import au.com.mineauz.minigames.TestUtilities;
import au.com.mineauz.minigames.backend.ConnectionHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import static junit.framework.TestCase.*;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 21/06/2017.
 */
public class MySQLBackendITest {

    ConfigurationSection config;
    Logger log;
    MySQLBackend backend;

    @Before
    public void setUp() {
        TestUtilities utilities = new TestUtilities();
        log = utilities.getLogger();
        config = utilities.createTestConfig().getConfigurationSection("backend");
        config.set("type","mysql");
        config.get("host","localhost:3306");
        config.get("database","games");
        config.get("username", "games");
        config.get("password", "games");
        config.get("useSSL", "false");
        backend = new MySQLBackend(log);
    }

    @Test
    public void initializeTest() {
        assertTrue(backend.initialize(config,true));
        config.set("useSSL ", "true");
        String old = config.getString("password");
        config.set("password", "password");
        assertFalse(backend.initialize(config,false));
        config.set("password",old);
        backend.shutdown();
    }

    @Test
    public void handlerIsValid(){
        backend.initialize(config,true);
        try{
            assertTrue(backend.getPool().getConnection().getConnection().isValid(10));
        }catch (SQLException e){
            e.printStackTrace();
            fail("SQL Exception thrown on handler test ");
        }
        backend.shutdown();
    }
    @Test
    public void testDbAccess(){
        backend.initialize(config,false);
        try {
            Connection con = backend.getPool().getConnection().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT 1 FROM `Players` LIMIT 0;");
            assertTrue(statement.execute());
        }catch (SQLException e){
            e.printStackTrace();
            fail("Error");
        }finally {
            backend.shutdown();
        }
    }

    @Test
    public void clean(){
        backend.initialize(config,false);
        backend.getPool().setMaxIdleTime(0);
        try{
            Connection con = backend.getPool().getConnection().getConnection();
            assertTrue(con.isValid(10));
            backend.clean();
            assertTrue(con.isValid(10));
        }catch (SQLException e){
            fail(e.getMessage());
        }
        backend.shutdown();
    }


    @Rule public ExpectedException exception = ExpectedException.none();
    @Test
    public void connectionInvalid() throws SQLException {
        exception.expect(SQLException.class);
        backend.initialize(config,false);
        backend.getPool().setMaxIdleTime(0);
        Connection con = backend.getPool().getConnection().getConnection();
        backend.clean();
        backend.shutdown();
        backend.getPool().getConnection(false).getConnection().isValid(10);
        fail("SQLException not thrown ..connection true");
    }

    @After
    public void tearDown(){
        backend.shutdown();
    }
}