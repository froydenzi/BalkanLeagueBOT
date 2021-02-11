import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class threadMatch extends faceitMessageListener implements Runnable {
    private static String channelOne;
    private static String channelTwo;
    private String chanOne;
    private String chanTwo;
    private static Vector<String> team_One = new Vector<>();
    private static Vector<String> team_Two = new Vector<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final int interval;

    public threadMatch(String channelone, String channeltwo, Vector<String> one, Vector<String> two, int sleepInterval) {
        channelOne = channelone;
        channelTwo = channeltwo;
        team_One = one;
        team_Two = two;
        interval = sleepInterval;
    }

    @Override
    public void run() {


        Guild guildThread = guild;
        VoiceChannel conn = guildThread.getVoiceChannelById(moveme);

        Objects.requireNonNull(guildThread.getCategoryById(hubcategory)).createVoiceChannel(channelOne)
                .queue(channel -> chanOne = channel.getId());
        Objects.requireNonNull(guildThread.getCategoryById(hubcategory)).createVoiceChannel(channelTwo)
                .queue(channel -> chanTwo = channel.getId());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final String chaOne = chanOne;
        final String chaTwo = chanTwo;

        final String[] teamOne = team_One.toArray(new String[0]);
        final String[] teamTwo = team_Two.toArray(new String[0]);

        System.out.println("[MATCH THREAD] Starting thread with name: " + Thread.currentThread().getName());
        System.out.println("[MATCH THREAD] Channel one: " + Objects.requireNonNull(guildThread.getVoiceChannelById(chaOne)).getName()
                + " channel two: " + Objects.requireNonNull(guildThread.getVoiceChannelById(chaTwo)).getName());
        System.out.println("[MATCH THREAD IDS] Channel one: " + chaOne
                + " channel two: " + chaTwo);
        System.out.println("Team one: " + team_One.toString());
        System.out.println("Team two: " + team_Two.toString());

        running.set(true);

        while (running.get()) {
            try {
                Thread.sleep(interval);
                assert conn != null;
                List<Member> members = conn.getMembers();
                if (!members.isEmpty()) {
                    try {
                        String member = members.get(0).getId();
                        Member usr = guildThread.getMemberById(member);
                        databaseAdapter.discordId = member;
                        String faceitID;
                        String haveMatch;
                        databaseAdapter dba = new databaseAdapter();
                        dba.authSQL();
                        faceitID = databaseAdapter.faceitId;
                        assert usr != null;

                        if (databaseAdapter.discordRes.equals("yes") && faceitID != null) {
                            haveMatch = databaseAdapter.haveMatch;

                            if (haveMatch.equals("1")) {

                                for (String o : teamOne) {
                                    if (faceitID.equals(o)) {
                                        if (Objects.requireNonNull(usr.getVoiceState()).inVoiceChannel()) {
                                            System.out.println("[MATCH THREAD] Player " + usr.getEffectiveName() + " with " + faceitID + " faceit id, moved into "
                                                    + Objects.requireNonNull(guildThread.getVoiceChannelById(chaOne)).getName());
                                            guildThread.moveVoiceMember(usr, guildThread.getVoiceChannelById(chaOne)).queue();
                                        }
                                    }
                                }
                                for (String t : teamTwo) {
                                    if (faceitID.equals(t)) {
                                        System.out.println(usr.getEffectiveName() + " ima startan match i precesuira se dalje..." + faceitID);
                                        if (Objects.requireNonNull(usr.getVoiceState()).inVoiceChannel()) {
                                            System.out.println("[MATCH THREAD] Player " + usr.getEffectiveName() + " with " + faceitID + " faceit id, moved into "
                                                    + Objects.requireNonNull(guildThread.getVoiceChannelById(chaTwo)).getName());
                                            guildThread.moveVoiceMember(usr, guildThread.getVoiceChannelById(chaTwo)).queue();
                                        }
                                    }
                                }

                            }
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("[THREAD STARTER] Match " + Thread.currentThread().getName() + " je vec ugasen!");
                    return;
                }
                Thread.currentThread().interrupt();
                System.out.println("[MATCH THREAD] Deleting channel one: " + Objects.requireNonNull(guildThread.getVoiceChannelById(chaOne)).getName());
                Objects.requireNonNull(guildThread.getVoiceChannelById(chaOne)).delete().reason("Match ended.").queue();
                System.out.println("[MATCH THREAD] Deleting channel two: " + Objects.requireNonNull(guildThread.getVoiceChannelById(chaTwo)).getName());
                Objects.requireNonNull(guildThread.getVoiceChannelById(chaTwo)).delete().reason("Match ended.").queue();
                running.set(false);
            }
        }
    }
}