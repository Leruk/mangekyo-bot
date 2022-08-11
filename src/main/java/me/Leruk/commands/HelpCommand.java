package me.Leruk.commands;

import me.Leruk.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class HelpCommand extends ListenerAdapter
{
    private static final String CONTROL = "\uD83D\uDEE0";
    private static final String RESTRICT = "\uD83D\uDEAB";
    private static final String MUSIC = "\uD83C\uDFB5";

    public void onMessageReceived(MessageReceivedEvent e)
    {
        String command = e.getMessage().getContentRaw();
        if(command.equalsIgnoreCase(DiscordBot.getPrefix() + "help"))
        {
            e.getChannel().sendMessage(" ").setEmbeds(helpMessage().build()).queue();
        }
    }

    public static EmbedBuilder helpMessage()
    {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Команды");
        eb.setDescription("Для получения дополнительной информации: " + DiscordBot.getPrefix() + "help [...]");
        eb.addField(CONTROL + " Команды каналов " + CONTROL, "`clear [кол-во сообщений]`", false);
        eb.addField(RESTRICT + " Команды ограничения " + RESTRICT, "`kick [@участник]`, `ban [@участник] [кол-во дней]`", false);
        eb.addField(MUSIC + " Команды музыки " + MUSIC, "`join`, `play [<url> или название]`, `nowplaying`\n `pause`, `resume`, `stop`, `skip`, `repeat`, `leave`", false);
        eb.setColor(new Color(160, 50, 255));

        return eb;
    }
}
