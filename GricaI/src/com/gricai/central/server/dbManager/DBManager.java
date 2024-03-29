package com.gricai.central.server.dbManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBManager {

	public static final String DB_LOCATION = "localhost:xxxx/db_gricai";
	public static final String USERNAME = "";
	public static final String PASSWORD = "";
	

	
	private static DBManager instance = new DBManager();

	private DBManager(){
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} finally {
			}
	}
	
	public static DBManager getInstance(){
		return instance;
	}
	// ja bi da u konnekt uzima koja je baza user u pass cisto ako imamo vishe baza da ne mora za svaku poseban connect	
	public Connection connect ( String DB_LOCATION, String USERNAME, String password){
        try{
            
            return DriverManager.getConnection(DB_LOCATION, USERNAME, password);
        } catch ( Exception ex){
        	// nama ne treba runtime al ajd kad pogledas da vidimo sta treba il ne treba nista
          throw new RuntimeException("DB connection failure");
        }
    }
	
	public final void closeAll(Connection conn, PreparedStatement ps, ResultSet rs){
		try {
			if(conn!=null)
				conn.close();
		} catch (SQLException e) {}
		try{
			if(ps!=null)
				ps.close();
		}catch (SQLException e) {}
		try{
			if(rs!=null)
				rs.close();
		}catch (SQLException e) {}
	}
}
