package com.napier.sem;

public class App
{
    public static void main(String[] args)
    {
        // Create new Connect object
        Connect db = new Connect();

        // Connect to database
        db.connect();

        // Here you could run queries using db.getConnection()

        // Disconnect from database
        db.disconnect();
    }
}
