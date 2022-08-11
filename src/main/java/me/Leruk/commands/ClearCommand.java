package me.Leruk.commands;

import me.Leruk.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearCommand extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent e) {
        final String[] args = e.getMessage().getContentRaw().split(" ");
        final TextChannel tChannel = e.getChannel().asTextChannel();

        final EmbedBuilder error = new EmbedBuilder();

        if (args[0].equalsIgnoreCase(DiscordBot.getPrefix() + "clear")) {

            if (args.length < 2) {
                error.setDescription("Для удаления сообщений используйте:  \n`"
                        + DiscordBot.getPrefix() + "clear [кол-во сообщений]`");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            final Member member = e.getMember();

            if (!member.hasPermission(Permission.MESSAGE_MANAGE)) {
                error.setDescription("У вас нет прав удалять сообщения");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            final Member bot = e.getGuild().getSelfMember();

            if (!bot.hasPermission(Permission.MESSAGE_MANAGE)) {
                error.setDescription("У меня нет прав удалять сообщения");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            try
            {
                List<Message> messages = e.getChannel().getHistory().retrievePast(Integer.parseInt(args[1]) + 1).complete();
                e.getChannel().asGuildMessageChannel().deleteMessages(messages).queue();

                //Success
                EmbedBuilder success = new EmbedBuilder();
                success.setColor(Color.GREEN);
                success.setDescription("✅ Успешно удалено " + args[1] + " сообщений");

                tChannel.sendMessage(" ")
                        .setEmbeds(success.build())
                        .queue((message) -> message.delete().queueAfter(10, TimeUnit.SECONDS));
            }
            catch (Exception exception)
            {

                if (exception.toString().contains("Message retrieval limit") || exception.toString().contains("Must provide at least")) {
                    //Error: Too many messages
                    error.setDescription("Кол-во удаляемых сообщений должно быть в пределе 1-99");

                    tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                    return;
                }

                if (exception.toString().contains("Message Id provided was older than 2 weeks")) {
                    //Error: Messages too old
                    error.setDescription("Сообщения, которым больше 2 недель, нельзя удалить");

                    tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                    return;
                }

                //Error: Invalid value
                error.setDescription("Для удаления сообщений используйте:  \n`"
                        + DiscordBot.getPrefix() + "clear [кол-во сообщений]`");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
            }
        }

    }
}
