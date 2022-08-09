package me.Leruk;

import me.Leruk.commands.BanCommand;
import me.Leruk.commands.ClearCommand;
import me.Leruk.commands.HelpCommand;
import me.Leruk.commands.KickCommand;
import me.Leruk.events.MemberJoinEvent;
import me.Leruk.events.MemberLeaveEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class DiscordBot{

    private static String prefix = "$";

    public static void main(String[] args) throws LoginException
    {
        JDA jda = JDABuilder.createDefault("MTAwNTg4MDg4NzM0MDE4NzczOA.GUC6NU.x_C8017XQK4QXOkqBnSEp7JGQKSz0FqMCaOf3Y")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.playing("c Леруком | " + prefix + "help"))
                .build();

        jda.addEventListener(new KickCommand());
        jda.addEventListener(new BanCommand());
        jda.addEventListener(new HelpCommand());
        jda.addEventListener(new ClearCommand());

        jda.addEventListener(new MemberJoinEvent());
        jda.addEventListener(new MemberLeaveEvent());
    }

    public static String getPrefix() {
        return prefix;
    }

    public static void setPrefix(String newPrefix) throws Exception{

        if(newPrefix.isEmpty())
        {
            throw new Exception("Prefix cannot be null!");
        }

        prefix = newPrefix;
    }


}