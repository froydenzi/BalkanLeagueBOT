import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main implements EventListener {
    public static String FACEITTOKEN;
    public static String DISCORDTOKEN;
    public static JDA jda;

    public static void main(String[] args) throws LoginException {

        settingConfig cfg = new settingConfig();
        try {
            cfg.getPropValues();
            System.out.println("[BOT-CFG] CFG Loaded...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[TOKEN] Discord token: " + DISCORDTOKEN);
        Logger.getLogger(Main.class);

        jda = JDABuilder.createDefault(DISCORDTOKEN)
                .addEventListeners(new faceitMessageListener())
                .build();
        Main.jda.getPresence().setActivity(Activity.listening("www.balkan-csgo.com"));

    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {

    }
}