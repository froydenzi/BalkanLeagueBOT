import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class threadActiveMatches extends faceitMessageListener implements Runnable {
    private static String channelOne;
    private static String channelTwo;
    private static String matchId;
    private final int interval;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public threadActiveMatches(String channelone, String channeltwo, String matchid, int sleepInterval) {
        channelOne = channelone;
        channelTwo = channeltwo;
        matchId = matchid;
        interval = sleepInterval;
    }

    @Override
    public void run() {

        Guild guildThread = guild;
        AtomicBoolean createMssg = new AtomicBoolean(true);
        AtomicBoolean deleteMssg = new AtomicBoolean(true);

        final String[] channelid = new String[2];
        channelid[0] = channelOne;
        channelid[1] = channelTwo;
        System.out.println(printTimeStamp() + "[MATCH THREAD] Starting match results thread with name: " + Thread.currentThread().getName());

        while (createMssg.get()) {
            //TODO: Adding message to text channel
        }

        running.set(true);

        while (running.get()) {
            try {
                Thread.sleep(interval);
                //TODO: code here
            } catch (InterruptedException e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(printTimeStamp() + "[THREAD STARTER] Match results thread " + Thread.currentThread().getName() + " je vec ugasen!");
                    return;
                }
                running.set(false);
                Thread.currentThread().interrupt();
            }
        }
        while (deleteMssg.get()) {
            //TODO: Removing message from text channel
        }
    }
}