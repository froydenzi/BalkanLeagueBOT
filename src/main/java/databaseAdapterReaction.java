import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class databaseAdapterReaction extends databaseAdapter {
    public static String userLang;

    private Connection createConnection() {
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

    public void writeLang(String discordId2,String lang, String nickname) throws SQLException {

        Statement s = Objects.requireNonNull(createConnection()).createStatement();
        String sql = "SELECT * FROM Language WHERE discord_id='" + discordId2 + "'";
        ResultSet rec = s.executeQuery(sql);
        if (!rec.next()) {
            System.out.println("[LANG] Adding " + lang + " language to " + nickname);
            String sttmnt = "INSERT INTO Language(discord_id,nickname,lang) VALUES('" + discordId2 + "','" + nickname + "','" + lang + "')";
            PreparedStatement preparedStmt = Objects.requireNonNull(createConnection()).prepareStatement(sttmnt);
            preparedStmt.execute();
        } else if (rec.getString("discord_id").equals(discordId2)) {
            System.out.println("[LANG] Found user " + nickname + " with language: " + rec.getString("lang"));
            String sttmnt = "UPDATE Language SET lang='" + lang + "' WHERE discord_id='" + discordId2 + "'";
            PreparedStatement preparedStmt = Objects.requireNonNull(createConnection()).prepareStatement(sttmnt);
            preparedStmt.execute();
            System.out.println("[LANG] User " + nickname + " updated his/her language to: " + lang);
        }
    }
    public void readLang(String discordId3,String nickname) throws SQLException {

        Statement s = Objects.requireNonNull(createConnection()).createStatement();
        String sql = "SELECT * FROM Language WHERE discord_id='" + discordId3 + "'";
        ResultSet rec = s.executeQuery(sql);
        if (!rec.next()) {
            userLang = "en";
        } else if (rec.getString("discord_id").equals(discordId3)) {
            System.out.println("[LANG] Found user " + nickname + " with language: " + rec.getString("lang"));
            userLang = rec.getString("lang");
        }
    }
}