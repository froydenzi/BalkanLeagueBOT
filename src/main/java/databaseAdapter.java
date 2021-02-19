import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.*;
import java.util.Objects;

public class databaseAdapter {
    public static String DB_NAME;
    public static String DB_USER;
    public static String DB_ADRES;
    public static int DB_PORT;
    public static String DB_PASSWORD;
    public static String discordId;
    public static String faceitId;
    public static String haveMatch;
    public static String discordRes;

    public Connection createConnection() {
        try {
            MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
            dataSource.setUser(DB_USER);
            dataSource.setPassword(DB_PASSWORD);
            dataSource.setServerName(DB_ADRES);
            dataSource.setPort(DB_PORT);
            dataSource.setDatabaseName(DB_NAME);
            dataSource.setZeroDateTimeBehavior("convertToNull");
            dataSource.setUseUnicode(true);
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Can't connect to the database! Make sure the database settings are correct and the database server is running AND the database `" + DB_NAME + "` exists");
        }
        return null;
    }
}