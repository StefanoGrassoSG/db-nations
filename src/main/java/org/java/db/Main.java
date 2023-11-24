package org.java.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
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
		System.out.print("Search: ");
		
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
					
					System.out.println("ID" + "\t   " + "COUNTRY" + "\t     " + "REGION" + "\t        " + "CONTINENT");
					while(rs.next()) {
						
						final int COUNTRY_ID = rs.getInt(1);
						final String COUNTRY_NAME = rs.getString(2);
						final String REGION_NAME = rs.getString(3);
						final String CONTINENT_NAME = rs.getString(4);
						
						System.out.println("[" + COUNTRY_ID + "] " + "\t   " + COUNTRY_NAME + "\t     " + REGION_NAME + "\t" + CONTINENT_NAME);
					}
					
					System.out.println("Choose a country id: ");
					String id = s.nextLine();
					int idInt = Integer.valueOf(id);
					
					final String SQL2 = "SELECT * "
		                    + " FROM `countries` "
		                    + " JOIN country_languages ON countries.country_id = country_languages.country_id "
		                    + " JOIN languages ON country_languages.language_id = languages.language_id "
		                    + " JOIN country_stats ON countries.country_id = country_stats.country_id "
		                    + " WHERE countries.country_id = ? "
		                    + " ORDER BY country_stats.year, country_stats.population; ";

		            try (PreparedStatement ps2 = con.prepareStatement(SQL2)) {
		                ps2.setInt(1, idInt);

		                try (ResultSet rss = ps2.executeQuery()) {
		                	List<String> languages = new ArrayList<>();
		                	String country_name = null;
		                	int year = 0;
		                	int population = 0;
		                	String gdp = null;
		                    while (rss.next()) {
		                    	country_name = rss.getString(2);
		                    	year = rss.getInt(14);
		                    	population = rss.getInt(15);
		                    	gdp = rss.getString(16);
		                        languages.add(rss.getString(12));
		                    }
		                    
		                    System.out.println("\nDetails for country: "
			                        + country_name 
			                        + "\nLanguages: " + languages
		                    		+ "\nMost recent stats"
				                    + "\nYear: " + year
				                    + "\nPopulation: " + population
		                    		+ "\nGDP: " + gdp);
		                }
		            } catch (Exception e) {
		                System.out.println("Error in db: " + e.getMessage());
		            }
				}
			}
		} catch (Exception e) {
			
			System.out.println("Error in db: " + e.getMessage());
			
		}
		s.close();
	}
}
