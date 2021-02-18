import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class threadMatch extends faceitMessageListener implements Runnable {
    private Boolean bool = true;
    private static String channelOne;
    private static String channelTwo;
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
        final String[] channelid = new String[2];
        System.out.println(printTimeStamp() + "[MATCH THREAD] Starting thread with name: " + Thread.currentThread().getName());

        Objects.requireNonNull(guildThread.getCategoryById(hubcategory)).createVoiceChannel(channelOne)
                .queue(channel -> {
                    channelid[0] = channel.getId();
                    System.out.println(printTimeStamp() + "[MATCH THREAD IDS] Channel one created - id: " + channelid[0]);
                });
        Objects.requireNonNull(guildThread.getCategoryById(hubcategory)).createVoiceChannel(channelTwo)
                .queue(channel -> {
                    channelid[1] = channel.getId();
                    System.out.println(printTimeStamp() + "[MATCH THREAD IDS] Channel two created - id: " + channelid[1]);
                    bool = false;
                });
        try {
            while (bool)
                Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final String[] teamOne = team_One.toArray(new String[0]);
        final String[] teamTwo = team_Two.toArray(new String[0]);

        System.out.println(printTimeStamp() + "[MATCH THREAD] Channel one: " + Objects.requireNonNull(guildThread.getVoiceChannelById(channelid[0])).getName()
                + " channel two: " + Objects.requireNonNull(guildThread.getVoiceChannelById(channelid[1])).getName());
        System.out.println(printTimeStamp() + "Team one: " + team_One.toString());
        System.out.println(printTimeStamp() + "Team two: " + team_Two.toString());

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
                                            System.out.println(printTimeStamp() + "[MATCH THREAD] Player " + usr.getEffectiveName() + " with " + faceitID + " faceit id, moved into "
                                                    + Objects.requireNonNull(guildThread.getVoiceChannelById(channelid[0])).getName());
                                            guildThread.moveVoiceMember(usr, guildThread.getVoiceChannelById(channelid[0])).queue();
                                        }
                                    }
                                }
                                for (String t : teamTwo) {
                                    if (faceitID.equals(t)) {
                                        if (Objects.requireNonNull(usr.getVoiceState()).inVoiceChannel()) {
                                            System.out.println(printTimeStamp() + "[MATCH THREAD] Player " + usr.getEffectiveName() + " with " + faceitID + " faceit id, moved into "
                                                    + Objects.requireNonNull(guildThread.getVoiceChannelById(channelid[1])).getName());
                                            guildThread.moveVoiceMember(usr, guildThread.getVoiceChannelById(channelid[1])).queue();
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
                    System.out.println(printTimeStamp() + "[THREAD STARTER] Match " + Thread.currentThread().getName() + " je vec ugasen!");
                    return;
                }
                running.set(false);
                Thread.currentThread().interrupt();
            }
        }
        Objects.requireNonNull(guildThread.getVoiceChannelById(channelid[0])).delete().reason("Match ended.").complete();
        System.out.println(printTimeStamp() + "[MATCH THREAD] Deleting channel one: " + Objects.requireNonNull(guildThread.getVoiceChannelById(channelid[0])).getName());
        Objects.requireNonNull(guildThread.getVoiceChannelById(channelid[1])).delete().reason("Match ended.").complete();
        System.out.println(printTimeStamp() + "[MATCH THREAD] Deleting channel two: " + Objects.requireNonNull(guildThread.getVoiceChannelById(channelid[1])).getName());
    }
}