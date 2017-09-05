package me.xbenz.party.FriendManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.config.Configuration;

public class sqlmanager
{
  Connection conn = null;
  
  public boolean openConnection(Configuration config)
  {
    String user = config.getString("sql.username");
    String pass = config.getString("sql.password");
    String url = config.getString("sql.url");
    String dbName = config.getString("sql.dbname");
    try
    {
      Class.forName("com.mysql.jdbc.Driver");
      this.conn = DriverManager.getConnection("jdbc:" + url + "/" + dbName, user, pass);
      
      return true;
    }
    catch (Exception e)
    {
      BungeeCord.getInstance().getLogger().severe("[ArcaneParty] Could not connect to the database!");
      e.printStackTrace();
    }
    return false;
  }
  
  public void submitQuery(String s)
  {
    if (this.conn != null) {
      try
      {
        PreparedStatement statement = this.conn.prepareStatement(s);
        statement.execute();
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  public void terminateConnection()
  {
    try
    {
      this.conn.close();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  
  public ResultSet getFromDB(String s)
    throws SQLException
  {
    if (this.conn != null)
    {
      PreparedStatement statement = this.conn.prepareStatement(s);
      ResultSet results = statement.executeQuery();
      return results;
    }
    throw new SQLException("Could not submit query! The connection is not valid.");
  }
}
