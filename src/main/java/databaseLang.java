import java.sql.*;
import java.util.Objects;

public class databaseLang extends databaseAdapter {
    public static String userLang;

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
            System.out.println("[LANG] Can't find preset language for user " + nickname);
            userLang = "en";
        } else if (rec.getString("discord_id").equals(discordId3)) {
            System.out.println("[LANG] Found user " + nickname + " with language: " + rec.getString("lang"));
            userLang = rec.getString("lang");
        }
    }

    public void authSQL() throws SQLException {
        faceitId = null;
        Statement s = Objects.requireNonNull(createConnection()).createStatement();
        String sql = "SELECT * FROM Korisnici WHERE discord_id='" + discordId + "'";
        ResultSet rec = s.executeQuery(sql);
        if(rec.next()){
            if (rec.getString("discord_id").equals(discordId)) {
                discordRes = "yes";
                faceitId = rec.getString("faceit_playerid");
                haveMatch = rec.getString("Game");
            }
        }else
            discordRes = "no";
    }
}