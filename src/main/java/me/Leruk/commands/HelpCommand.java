package me.Leruk.commands;

import me.Leruk.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class HelpCommand extends ListenerAdapter
{
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
        eb.addField("\uD83D\uDEE0️Команды каналов\uD83D\uDEE0️", "clear [кол-во сообщений]", false);
        eb.addField("\uD83D\uDEABКоманды ограничения\uD83D\uDEAB", "kick [@участник], ban [@участник] [кол-во дней]", false);
        eb.setColor(new Color(160, 50, 255));

        return eb;
    }
}
