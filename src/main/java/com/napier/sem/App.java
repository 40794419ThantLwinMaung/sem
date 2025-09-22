package com.napier.sem;

import java.sql.*;

public class App
{
    public static void main(String[] args)
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        // Connection to the database
        Connection con = null;
        int retries = 10; // Reduce retries, Docker Compose healthcheck ensures DB starts
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database (attempt " + (i+1) + ")...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(5000);

                // Connect to database with public key retrieval allowed
                con = DriverManager.getConnection(
                        "jdbc:mysql://db:3306/employees?useSSL=false&allowPublicKeyRetrieval=true",
                        "root",
                        "example"
                );

                System.out.println("Successfully connected to MySQL!");
                break; // Exit loop if connection succeeds
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + (i+1));
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }

        if (con != null)
        {
            try
            {
                // Close connection
                con.close();
                System.out.println("Database connection closed.");
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
        else
        {
            System.out.println("Could not establish a connection to MySQL after retries.");
        }
    }
}
