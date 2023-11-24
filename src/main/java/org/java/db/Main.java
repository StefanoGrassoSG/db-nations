package org.java.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {
	
	private static final String url = "jdbc:mysql://localhost:3306/db_nation";
	private static final String user = "root";
	private static final String pws = "root";

	public static void main(String[] args) {
		
		queryTest();
	}

	private static void queryTest() {
		
		Scanner s = new Scanner(System.in);
		System.out.print("Parola che vuoi cercare: ");
		
		String filterWord = s.nextLine();
		String pattern = "%" + filterWord + "%";
		
		try (Connection con = DriverManager.getConnection(url, user, pws)) {
			
			final String SQL = ""
					+ " SELECT countries.country_id, countries.name, regions.name, continents.name "
					+ " FROM `countries` "
					+ " JOIN regions ON countries.region_id = regions.region_id "
					+ " JOIN continents ON regions.continent_id = continents.continent_id "
					+ " WHERE countries.name LIKE ? "
					+ " ORDER BY countries.name ";
			
			try (PreparedStatement ps = con.prepareStatement(SQL)) {
				
				ps.setString(1, pattern);
				
				try (ResultSet rs = ps.executeQuery()) {
					
					int rowCounter = 0;
					while(rs.next()) {
						
						final int COUNTRY_ID = rs.getInt(1);
						final String COUNTRY_NAME = rs.getString(2);
						final String REGION_NAME = rs.getString(3);
						final String CONTINENT_NAME = rs.getString(4);
						
						System.out.println("[" + COUNTRY_ID + "] " + COUNTRY_NAME 
								+ ": " + REGION_NAME 
								+ "/" + CONTINENT_NAME
								);
						
						rowCounter++;
					}
					
					System.out.println("\n------------------------------\n");
					System.out.println("Row count: " + rowCounter);
				}
			}
		} catch (Exception e) {
			
			System.out.println("Error in db: " + e.getMessage());
		}
	}
}
