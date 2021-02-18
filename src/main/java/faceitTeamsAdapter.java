import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Vector;


public class faceitTeamsAdapter{
    public static ArrayList<String> teams = new ArrayList<>();
    public static Vector<String> teamOne = new Vector<>();
    public static Vector<String> teamTwo = new Vector<>();
    private static String MATCH_ID;

    public faceitTeamsAdapter(String matchid) {
        MATCH_ID = matchid;
    }

    public void getData() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + Main.FACEITTOKEN)
                .uri(URI.create("https://open.faceit.com/data/v4/matches/" + URLEncoder.encode(MATCH_ID, StandardCharsets.UTF_8)))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(faceitTeamsAdapter::parse)
                .join();

    }

    public static String parse(String responseBody) {
        if (teams!=null)
            teams.clear();
        if (teamOne!=null)
            teamOne.clear();
        if (teamTwo!=null)
            teamTwo.clear();

        JSONObject obj = new JSONObject(responseBody);
        JSONObject teams1 = obj.getJSONObject("teams");
        JSONObject faction1 = teams1.getJSONObject("faction1");
        JSONArray roster = faction1.getJSONArray("roster");
        for (int i = 0; i < roster.length(); i++) {
            JSONObject player = roster.getJSONObject(i);
            teamOne.add(player.getString("player_id"));
        }
        JSONObject faction2 = teams1.getJSONObject("faction2");
        JSONArray rostertwo = faction2.getJSONArray("roster");
        for (int j = 0; j < rostertwo.length(); j++) {
            JSONObject player = rostertwo.getJSONObject(j);
            teamTwo.add(player.getString("player_id"));
        }

        JSONObject rounds = obj.getJSONObject("teams");
        JSONObject jOBJNEW = rounds.getJSONObject("faction1");
        teams.add(jOBJNEW.getString("name"));
        JSONObject jOBJNEW2 = rounds.getJSONObject("faction2");
        teams.add(jOBJNEW2.getString("name"));
        return null;
    }
}