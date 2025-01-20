package me.quickscythe.quipt.utils.sql;

import me.quickscythe.quipt.utils.CoreUtils;

import java.sql.*;
import java.util.Properties;

public class SqlDatabase {
    public Connection connection;
    private final Properties properties;
    private final String url;
    private final SqlUtils.SQLDriver driver;

    public SqlDatabase(SqlUtils.SQLDriver driver, String host, Integer port, String database, String username, String password) {
        this.properties = new Properties();
        this.properties.setProperty("user", username);
        this.properties.setProperty("password", password);
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        this.driver = driver;
    }

    public Boolean init() {
        try {
            if (driver.equals(SqlUtils.SQLDriver.MYSQL)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection( url + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", properties);
            }
            if (driver.equals(SqlUtils.SQLDriver.SQLITE)) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(url);
            }

            if (connection != null) {
                return true;
            }

        } catch (SQLException | ClassNotFoundException ex) {
            CoreUtils.logger().log("SqlManager", "An error occurred while connecting databse (" + url + ")");
            return false;
        }
        return false;
    }

    public ResultSet query(String query, Object... values) {
        try {
            PreparedStatement statement = prepare(query, values);
            if (statement == null) {
                return null;
            }
            return statement.executeQuery();
        } catch (Exception exception) {
            return null;
        }
    }

    public Integer update(String query, Object... values) {
        try {
            PreparedStatement statement = prepare(query, values);
            if (statement == null) {
                return -1;
            }
            return statement.executeUpdate();
        } catch (Exception exception) {
            return -1;
        }

    }

    public boolean input(String query, Object... values) {
        try {
            PreparedStatement statement = prepare(query, values);
            if (statement == null) {
                return false;
            }
            return statement.execute();
        } catch (Exception exception) {
            return false;
        }

    }

    private PreparedStatement prepare(String query, Object... values) {
        try {
            if (!init()) {
                return null;
            }
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            return statement;
        } catch (Exception exception) {
            return null;
        }
    }
}
