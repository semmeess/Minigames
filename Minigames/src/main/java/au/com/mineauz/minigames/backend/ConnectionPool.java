package au.com.mineauz.minigames.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import com.google.common.collect.Lists;

public class ConnectionPool {
    private String connectionString;
    private Properties props ;

    
    private long maxIdleTime;
    
    private List<ConnectionHandler> connections;
    @Deprecated
    public ConnectionPool(String connectionString, String username, String password) {
        props = new Properties();
        props.put("username", username);
        props.put("password", password);
        props.put("useSSL", false);
        this.connectionString = connectionString;
        connections = Collections.synchronizedList(Lists.<ConnectionHandler>newArrayList());
        maxIdleTime = TimeUnit.SECONDS.toMillis(30);
    }
    public ConnectionPool(String connectionString, Properties properties) {
        this(connectionString,properties,30);
    }

    public ConnectionPool(String connectionString, Properties properties, int idleTime){
        this.connectionString = connectionString;
        props = properties;
        connections = Collections.synchronizedList(Lists.<ConnectionHandler>newArrayList());
        maxIdleTime = TimeUnit.SECONDS.toMillis(idleTime);
    }

    public void setMaxIdleTime(long maxTime) {
        maxIdleTime = maxTime;
    }
    
    public long getMaxIdleTime() {
        return maxIdleTime;
    }
    
    public void removeExpired() {
        synchronized(connections) {
            Iterator<ConnectionHandler> it = connections.iterator();
            while(it.hasNext()) {
                ConnectionHandler handler = it.next();
                
                if (!handler.isInUse()) {
                    if (System.currentTimeMillis() - handler.getCloseTime() > maxIdleTime) {
                        // Timeout
                        handler.closeConnection();
                        it.remove();
                    }
                } else {
                	if (System.currentTimeMillis() - handler.getOpenTime() > maxIdleTime) {
                		// So we dont just accumulate connections forever
                		handler.release();
                	}
                }
            }
        }
    }
    public ConnectionHandler getConnection() throws SQLException {
        return getConnection(true);
    }
    /**
     * @return Returns a free connection from the pool of connections. Creates a new connection if there are none available
     */
    public ConnectionHandler getConnection(boolean createNew) throws SQLException {
        synchronized(connections) {
            for (int i = 0; i < connections.size(); ++i) {
                ConnectionHandler con = connections.get(i);
                
                if (con.lease()) {
                    // Check connection
                    boolean healthy = true;
                    
                    try {
                        if (con.getConnection().isClosed()) {
                            healthy = false;
                        }
                    } catch (SQLException e) {
                        healthy = false;
                    }
                    
                    // Get rid of the connection
                    if (!healthy) {
                        con.closeConnection();
                        connections.remove(i--);
                    // Its ok
                    } else {
                        return con;
                    }
                }
            }
        }
        if(createNew){
            return createConnection();
        }else{
            throw new SQLException("No Connections available");
        }
    }
    
    private ConnectionHandler createConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(connectionString, props);
        ConnectionHandler handler = new ConnectionHandler(connection);
        connections.add(handler);
        return handler;
    }

    public void closeConnections() {
        synchronized(connections) {
            for (ConnectionHandler c : connections) {
                c.closeConnection();
            }
            
            connections.clear();
        }
    }
}