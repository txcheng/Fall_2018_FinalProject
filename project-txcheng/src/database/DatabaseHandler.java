package database;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Properties;
import java.util.Random;

/**
 * Modified from the example of Prof. Olga
 */
public class DatabaseHandler {

 private static DatabaseHandler dbHandler;

    /** Prepared Statements  */
    /** For creating table users */
    private static final String CREATE_USER_TABLE =
            "CREATE TABLE users (" +
                    "user_id INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL UNIQUE, " +
                    "password CHAR(64) NOT NULL, " +
                    "usersalt CHAR(32) NOT NULL);";

    /** Used to insert a new user into the database. */
    private static final String REGISTER_SQL =
            "INSERT INTO users (username, password, usersalt) " +
                    "VALUES (?, ?, ?);";


    /** Used to generate password hash salt for user. */
    private Random random = new Random();

    public Properties loadConfigFile() {
        // load info from config file database.properties
        Properties config = new Properties();
        try (FileReader fr = new FileReader("database.properties")) {
            config.load(fr);
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return config;
    }


    /**
     * Gets the single instance of the database handler.
     *
     * @return instance of the database handler
     */
    public static DatabaseHandler getInstance() {
        return dbHandler;
    }

    public void createTable() {
        Properties config = loadConfigFile();

        String uri = String.format("jdbc:mysql://%s/%s", config.getProperty("hostname"),
                config.getProperty("database"));

        System.out.println("uri = " + uri);
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");

            ResultSet results = null;
            statement = dbConnection.createStatement();
            statement.executeUpdate(CREATE_USER_TABLE);

        }
        catch (SQLException ex) {
        System.out.println(ex);
        }

 }

    /**
     * Returns the hex encoding of a byte array.
     *
     * @param bytes - byte array to encode
     * @param length - desired length of encoding
     * @return hex encoded byte array
     */
    public static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    /**
     * Calculates the hash of a password and salt using SHA-256.
     *
     * @param password - password to hash
     * @param salt - salt associated with user
     * @return hashed password
     */
    public static String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        }
        catch (Exception ex) {
            System.out.println(ex);
        }

        return hashed;
    }

    public boolean userExists(String user)
    {
        boolean exists = false;
        String LOGIN_SQL =
                "SELECT username\n" +
                        " FROM users\n" +
                        " WHERE username =  '" + user +
                        "';";

        Properties config   = loadConfigFile();

        String uri = String.format("jdbc:mysql://%s/%s", config.getProperty("hostname"),
                config.getProperty("database"));

        System.out.println("uri = " + uri);

        Statement statement;

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            try {
                statement = connection.createStatement();
                ResultSet results = statement.executeQuery(LOGIN_SQL);
                while (results.next()) {
                    String username = results.getString("username");
                    if(username.equals(user))
                    {
                        exists = true;
                    }
                }
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
        return exists;
    }

    public boolean correctInfo(String user, String pass)
    {
        boolean correct = false;
        String getHash = "SELECT usersalt from users where username = '" + user +"';";


        Properties config   = loadConfigFile();

        String uri = String.format("jdbc:mysql://%s/%s", config.getProperty("hostname"),
                config.getProperty("database"));

        System.out.println("uri = " + uri);

        Statement statement;

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            try {
                statement = connection.createStatement();
                ResultSet results = statement.executeQuery(getHash);
                while (results.next()) {
                    String salt = results.getString("usersalt");
                    String LOGIN_SQL =
                            "SELECT username\n" +
                                    " FROM users\n" +
                                    " WHERE username =  '" + user +
                                    "' and password = '"+ getHash(pass,salt)+ "';";
                    try{
                        statement = connection.createStatement();
                        ResultSet checkUser = statement.executeQuery(LOGIN_SQL);
                        while(checkUser.next())
                        {
                            correct = true;
                        }
                    }
                    catch(SQLException e) {
                        System.out.println(e);
                    }
                }
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
        return correct;
    }

    /**
     * Registers a new user, placing the username, password hash, and
     * salt into the database.
     *
     * @param newuser - username of new user
     * @param newpass - password of new user
     */
    public void registerUser(String newuser, String newpass) {
        Properties config   = loadConfigFile();

        String uri = String.format("jdbc:mysql://%s/%s", config.getProperty("hostname"),
                config.getProperty("database"));

        System.out.println("uri = " + uri);

        // Generate salt
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        String usersalt = encodeHex(saltBytes, 32); // salt
        String passhash = getHash(newpass, usersalt); // hashed password
        System.out.println(usersalt);

        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            try {
                statement = connection.prepareStatement(REGISTER_SQL);
                statement.setString(1, newuser);
                statement.setString(2, passhash);
                statement.setString(3, usersalt);
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
    }
}

