import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;


public class faceitMessageListener extends ListenerAdapter implements EventListener {
    public static String input;
    public static String setPrefix;
    public static String webhookname;
    public static String moveme;
    public static String movetocha;
    public static String auth;
    public static String hubcategory;
    public static String hubrole;
    public static String langcha;
    public static Guild guild;
    public static Runnable rnbl;
    public static Thread thread;
    public final String[] nasLang = {"U+1f1e7U+1f1e6", "U+1f1f7U+1f1f8", "U+1f1f2U+1f1f0", "U+1f1edU+1f1f7", "U+1f1f2U+1f1ea"};
    public final String[] engLang = {"U+1f1f8U+1f1ee", "U+1f1e6U+1f1f1", "U+1f1fdU+1f1f0", "U+1f1e7U+1f1ec", "U+1f1f7U+1f1f4", "U+1f1faU+1f1f8"};

    public static String printTimeStamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ")
                .withResolverStyle(ResolverStyle.STRICT);
        LocalDateTime date = LocalDateTime.now();
        return formatter.format(date);
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        guild = event.getGuild();
        TextChannel cha = guild.getTextChannelById(langcha);
        assert cha != null;
        boolean mssg = cha.hasLatestMessage();
        if (!mssg) {
            cha.sendMessage("Language selection:").queue(message -> {
                for (String s : nasLang) {
                    message.addReaction(s).queue();
                }
                for (String s : engLang) {
                    message.addReaction(s).queue();
                }
            });
        }
        super.onGuildReady(event);
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (Objects.requireNonNull(event.getUser()).isBot()) {
            return;
        }

        guild = event.getReaction().getGuild();
        String channel = event.getChannel().getId();
        if (channel.equals(langcha)) {
            MessageReaction reaction = event.getReaction();
            String emote = reaction.getReactionEmote().getAsCodepoints();
            String nickname = Objects.requireNonNull(event.getMember()).getEffectiveName();
            String disId = event.getUserId();
            databaseLang dbl = new databaseLang();

            EmbedBuilder rccmsg = new EmbedBuilder();
            rccmsg.setColor(0x08d5da)
                    .setAuthor("BALKAN CSGO LEAGUE", "https://www.balkan-csgo.com/");
            PrivateChannel chapriv = Objects.requireNonNull(event.getMember().getUser().openPrivateChannel().complete());

            if (Arrays.toString(nasLang).contains(emote)) {
                try {
                    dbl.writeLang(disId, "ba", nickname);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                rccmsg.addField("Status registracije jezika:", "Uspjesno ste promjenili Vas jezik na "+reaction.getReactionEmote().getEmoji()+"! :flag_white:", true);
                chapriv.sendMessage(rccmsg.build()).queue();

            } else if (Arrays.toString(engLang).contains(emote)) {
                try {
                    dbl.writeLang(disId, "en", nickname);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                rccmsg.addField("Language registration status:", "You have updated your language to "+reaction.getReactionEmote().getEmoji()+"! :flag_white:", true);
                chapriv.sendMessage(rccmsg.build()).queue();
            }

        }
        super.onMessageReactionAdd(event);
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        Member usr = event.getEntity();
        guild = usr.getGuild();
        VoiceChannel cha = event.getChannelJoined();

        if (Objects.equals(cha, guild.getVoiceChannelById(moveme))) {
            databaseAdapter.discordId = usr.getId();

            try {
                new databaseLang().authSQL();
                new databaseLang().readLang(usr.getId(), usr.getEffectiveName());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            String faceitID = databaseAdapter.faceitId;
            String discordLang = databaseLang.userLang;

            if (Objects.requireNonNull(usr.getVoiceState()).inVoiceChannel()) {
                EmbedBuilder authmsg2 = new EmbedBuilder();
                authmsg2.setColor(0x08d5da);
                if (databaseAdapter.discordRes.equals("yes") && faceitID == null) {
                    System.out.println(printTimeStamp() + "[MATCH LOGIC] " + usr.getEffectiveName() + " Nije kompletno registrovan... move");
                    PrivateChannel chapriv = Objects.requireNonNull(usr.getUser().openPrivateChannel().complete());
                    authmsg2.setAuthor("REGISTRATION SITE - CLICK HERE", "https://www.balkan-csgo.com/");
                    if (discordLang.equals("ba"))
                        authmsg2.addField("Status registracije:", "Nepotpuna registracija,provjerite jeste li registrovali ispravan faceit profil sa ispravnim discord profilom! :thinking:", true);
                    else
                        authmsg2.addField("Registration status:", "Incomplete registration,check if you connected right discord profile with right faceit account! :thinking:", true);
                    chapriv.sendMessage(authmsg2.build()).queue();
                    guild.moveVoiceMember(usr, guild.getVoiceChannelById(movetocha)).complete();
                } else if (databaseAdapter.discordRes.equals("yes") && databaseAdapter.haveMatch.equals("0")) {
                    authmsg2.setAuthor("BALKAN CSGO LEAGUE", "https://www.balkan-csgo.com/");
                    if (discordLang.equals("ba")) {
                        authmsg2.addField("Nemate startan match!", "Ako se match trenutno konfigurise,probajte kasnije! :face_with_monocle:", true)
                                .addField("\nMoguci problem:", "Provjerite jeste li registrovali ispravan faceit profil sa ispravnim discord profilom! :v:", true);
                    } else {
                        authmsg2.addField("You don't have started match!", "If match is in configuring state,try later! :face_with_monocle:", true)
                                .addField("\nPossible problem:", "Check if you connected right discord profile with right faceit account! :v:", true);
                    }
                    System.out.println(printTimeStamp() + "[MATCH LOGIC] " + usr.getEffectiveName() + " Nema startan match... kick");
                    PrivateChannel chapriv = Objects.requireNonNull(usr.getUser().openPrivateChannel().complete());
                    chapriv.sendMessage(authmsg2.build()).queue();
                    guild.moveVoiceMember(usr, guild.getVoiceChannelById(movetocha)).complete();

                } else if (databaseAdapter.discordRes.equals("no")) {
                    System.out.println(printTimeStamp() + "[MATCH LOGIC] " + usr.getEffectiveName() + " Nije registrovan... move");
                    PrivateChannel chapriv = Objects.requireNonNull(usr.getUser().openPrivateChannel().complete());
                    authmsg2.setAuthor("REGISTRATION SITE - CLICK HERE", "https://www.balkan-csgo.com/");
                    if (discordLang.equals("ba")) {
                        authmsg2.addField("Status registracije:", "Niste registrovani na nas website, za registraciju posjetite link iznad! :thinking:", true)
                                .addField("\nMoguci problem:", "Provjerite jeste li registrovali ispravan faceit profil sa ispravnim discord profilom! :v:", true);
                    } else {
                        authmsg2.addField("Registration status:", "You aren't registered on our website,to register click the link above! :thinking:", true)
                                .addField("\nPossible problem:", "Check if you connected right discord profile with right faceit account! :v:", true);
                    }
                    chapriv.sendMessage(authmsg2.build()).queue();
                    guild.moveVoiceMember(usr, guild.getVoiceChannelById(movetocha)).complete();
                }
            }

        }
        super.onGuildVoiceUpdate(event);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() && !event.getAuthor().getName().equals(webhookname)) {
            return;
        }

        String[] cmessage = event.getMessage().getContentRaw().split(" ");
        guild = event.getGuild();

        if (cmessage[0].equalsIgnoreCase(setPrefix + "matchid")) {
            if (cmessage.length == 3) {

                System.out.println(printTimeStamp() + "[BOT Listener] Dobio sam poziv !matchid " + cmessage[1] + " sa komandom " + cmessage[2]);

                if (cmessage[2].equals("match_status_ready")) {

                    Set<Thread> threads = Thread.getAllStackTraces().keySet();

                    ArrayList<String> started = new ArrayList<>();

                    for (Thread t : threads) {
                        String name1 = t.getName();
                        String[] nameSplit = name1.split("---");
                        if (nameSplit.length == 2 && nameSplit[1].equals("check")) {
                            started.add(nameSplit[0]);
                        }
                    }
                    if (!started.contains(cmessage[1])) {

                        input = cmessage[1];

                        faceitTeamsAdapter fta = new faceitTeamsAdapter(input);
                        fta.getData();

                        Vector<String> team1 = faceitTeamsAdapter.teamOne;
                        Vector<String> team2 = faceitTeamsAdapter.teamTwo;
                        String channel1 = faceitTeamsAdapter.teams.get(0);
                        String channel2 = faceitTeamsAdapter.teams.get(1);

                        rnbl = new threadMatch(channel1, channel2, team1, team2, 500);
                        thread = new Thread(rnbl);
                        thread.setName(input + "---check");
                        thread.start();
                        started.clear();
                        event.getChannel().sendMessage("[THREAD STARTER] Match started. Thread name: " + thread.getName()).queue();


                    } else {
                        System.out.println(printTimeStamp() + "[THREAD STARTER] Match " + cmessage[1] + " je vec startan!");
                        event.getChannel().sendMessage("[THREAD STARTER] Match already started. Thread name: " + thread.getName()).queue();
                        started.clear();
                    }

                } else if (cmessage[2].equals("match_status_finished") || cmessage[2].equals("match_status_cancelled") || cmessage[2].equals("match_status_aborted")) {

                    Set<Thread> threads = Thread.getAllStackTraces().keySet();

                    for (Thread t : threads) {
                        if (t.getName().equals(cmessage[1] + "---check")) {
                            event.getChannel().sendMessage("[THREAD STARTER] Match ended. Thread name: " + t.getName()).queue();
                            System.out.println(printTimeStamp() + "[Thread] Match zavrsen,gasim thread sa imenom: " + t.getName());
                            t.interrupt();
                        }
                    }
                }
            }
        }

        if (cmessage[0].equalsIgnoreCase(setPrefix + "stopthreads")) {
            if (cmessage.length == 1) {

                Set<Thread> threads = Thread.getAllStackTraces().keySet();

                for (Thread t : threads) {

                    String name1 = t.getName();
                    String[] nameSplit = name1.split("---");

                    if (nameSplit.length == 2 && nameSplit[1].equals("check")) {
                        event.getChannel().sendMessage("[THREAD STARTER] Match stopped. Thread name: " + t.getName()).complete();
                        t.interrupt();
                    }
                }
            }
        }

        if (cmessage[0].equalsIgnoreCase(setPrefix + "matches")) {
            if (cmessage.length == 1) {

                Set<Thread> threads = Thread.getAllStackTraces().keySet();
                int thread_count = 0;
                for (Thread t : threads) {

                    String name1 = t.getName();
                    String[] nameSplit = name1.split("---");

                    if (nameSplit.length == 2 && nameSplit[1].equals("check")) {
                        thread_count++;
                    }
                }
                event.getChannel().sendMessage("[COUNTER] Trenutno startanih meceva: " + thread_count + " potreban broj soba: " + thread_count * 2).queue();
                List<VoiceChannel> channel_count = Objects.requireNonNull(guild.getCategoryById(hubcategory)).getVoiceChannels();
                event.getChannel().sendMessage("[COUNTER] Trenutan broj soba u hub kategoriji: " + channel_count.size()).queue();
            }
        }

        if (cmessage[0].equalsIgnoreCase(setPrefix + "deleteall")) {
            if (cmessage.length == 1) {

                Category category = guild.getCategoryById(hubcategory);
                assert category != null;
                List<GuildChannel> channels = category.getChannels();

                for (GuildChannel channel : channels) {
                    channel.delete().reason("Bot stopped...").queue();
                }
            }
        }

        if (cmessage[0].equalsIgnoreCase(setPrefix + "auth")) {
            if (cmessage.length == 1) {

                String cha = event.getChannel().getId();

                if (cha.equals(auth)) {

                    System.out.println(printTimeStamp() + "[AUTH] " + event.getAuthor() + " discord id: " + event.getAuthor().getId());
                    databaseAdapter.discordId = event.getAuthor().getId();

                    try {
                        new databaseLang().authSQL();
                        new databaseLang().readLang(event.getAuthor().getId(), Objects.requireNonNull(event.getMember()).getEffectiveName());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    System.out.println(printTimeStamp() + "[AUTH] " + event.getAuthor() + " have discord id set: " + databaseAdapter.discordRes);
                    String discordLang = databaseLang.userLang;

                    EmbedBuilder botmsg = new EmbedBuilder();
                    botmsg.setColor(0x08d5da);
                    if (databaseAdapter.discordRes.equals("no")) {
                        botmsg.setAuthor("REGISTRATION SITE - CLICK HERE", "https://www.balkan-csgo.com/");
                        if (discordLang.equals("ba")) {
                            botmsg.addField("Status registracije:", "Niste registrovani na nas website, za registraciju posjetite link iznad! :thinking:", true)
                                    .addField("\nMoguci problem:", "Provjerite jeste li povezali Vas discrod profil na nasem sajtu! :v:", true);
                        } else {
                            botmsg.addField("Registration status:", "You aren't registered on our website,to register click the link above! :thinking:", true)
                                    .addField("\nPossible problem:", "Check if you connect your discord profile on our website! :v:", true);
                        }
                        event.getChannel().sendMessage(botmsg.build()).queue();
                    } else {
                        if (databaseAdapter.discordRes.equals("yes") && databaseAdapter.faceitId == null) {
                            botmsg.setAuthor("REGISTRATION SITE - CLICK HERE", "https://www.balkan-csgo.com/");
                            if (discordLang.equals("ba"))
                                botmsg.addField("Status registracije:", "Nepotpuna registracija,provjerite jeste li registrovali ispravan faceit profil sa ispravnim discord profilom! :thinking:", true);
                            else
                                botmsg.addField("Registration status:", "Incomplete registration,check if you connected right discord profile with right faceit account! :thinking:", true);
                            event.getChannel().sendMessage(botmsg.build()).queue();
                        } else {
                            Role role = guild.getRoleById(hubrole);
                            assert role != null;
                            botmsg.setAuthor("BALKAN CSGO LEAGUE", "https://www.balkan-csgo.com/");
                            if (discordLang.equals("ba"))
                                botmsg.addField("Status registracije:", "Autentifikacija uspjesna! :white_check_mark:", false);
                            else
                                botmsg.addField("Registration status:", "Authentication successful! :white_check_mark:", false);

                            if (!Objects.requireNonNull(event.getMember()).getRoles().contains(role)) {
                                if (discordLang.equals("ba"))
                                    botmsg.addField("", "Kao nagradu,dodjeljujemo Vam nasu hub permisiju! :clap:\n", false);
                                else
                                    botmsg.addField("", "As a reward,we are giving you our hub permission! :clap:\n", false);

                                event.getChannel().sendMessage(botmsg.build()).queue();
                                guild.addRoleToMember(event.getMember(), role).queue();
                            } else
                                event.getChannel().sendMessage(botmsg.build()).queue();
                        }
                    }
                }
            }
        }
    }
}