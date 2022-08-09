package me.Leruk.commands;

import me.Leruk.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearCommand extends ListenerAdapter {

    enum ErrorCodes {
        MEMBER_PERMISSION,
        BOT_PERMISSION,
        FEW_ARGUMENTS,
        MANY_MESSAGES,
        OLD_MESSAGES,
        INVALID_VALUE
    }

    public void onMessageReceived(MessageReceivedEvent e) {

        String[] args = e.getMessage().getContentRaw().split(" ");
        Member member = e.getMember();
        Member bot = e.getGuild().getSelfMember();

        if (args[0].equalsIgnoreCase(DiscordBot.getPrefix() + "clear")) {
            if (!member.hasPermission(Permission.MESSAGE_MANAGE)) {
                //Error: Member haven't permission to delete messages
                e.getChannel().sendMessage(" ").setEmbeds(invalidCommand(ErrorCodes.MEMBER_PERMISSION).build()).queue();
            } else if (!bot.hasPermission(Permission.MESSAGE_MANAGE)) {
                //Error: Bot haven't permission to delete messages
                e.getChannel().sendMessage(" ").setEmbeds(invalidCommand(ErrorCodes.BOT_PERMISSION).build()).queue();
            } else if (args.length < 2) {
                e.getChannel().sendMessage(" ").setEmbeds(invalidCommand(ErrorCodes.FEW_ARGUMENTS).build()).queue();
            } else {
                try {
                    List<Message> messages = e.getChannel().getHistory().retrievePast(Integer.parseInt(args[1])+1).complete();
                    e.getChannel().asGuildMessageChannel().deleteMessages(messages).queue();

                    //Success
                    e.getChannel().sendMessage(" ").setEmbeds(correctCommand(args[1]).build()).queue((message) -> message.delete().queueAfter(5, TimeUnit.SECONDS));
                } catch (IllegalArgumentException exception) {
                    if (exception.toString().contains("Must provide at least 2 or at most 100 messages to be deleted")) {
                        //Error: Too many messages
                        e.getChannel().sendMessage(" ").setEmbeds(invalidCommand(ErrorCodes.MANY_MESSAGES).build()).queue();
                    } else if (exception.toString().contains("Message Id provided was older than 2 weeks")) {
                        //Error: Messages too old
                        e.getChannel().sendMessage(" ").setEmbeds(invalidCommand(ErrorCodes.OLD_MESSAGES).build()).queue();
                    } else {
                        //Error: Invalid value
                        e.getChannel().sendMessage(" ").setEmbeds(invalidCommand(ErrorCodes.INVALID_VALUE).build()).queue();
                    }
                }
            }
        }

    }

    private static EmbedBuilder invalidCommand(ErrorCodes errorCode) {
        EmbedBuilder error = new EmbedBuilder();

        error.setColor(Color.RED);

        switch (errorCode) {
            case MEMBER_PERMISSION:
                error.setTitle("\uD83D\uDD34 У вас нет прав удалять сообщения");
                break;

            case BOT_PERMISSION:
                error.setTitle("\uD83D\uDD34 У бота нет прав удалять сообщения");
                break;

            case FEW_ARGUMENTS:
                error.setTitle("\uD83D\uDD34 Слишком мало аргументов");
                error.setDescription("Для удаления сообщений используйте:  \n" + DiscordBot.getPrefix() + "clear [кол-во сообщений]");
                break;

            case MANY_MESSAGES:
                error.setTitle("\uD83D\uDD34 Значение вне диапазона");
                error.setDescription("Кол-во удаляемых сообщений должно быть в пределе 1-99");
                break;

            case OLD_MESSAGES:
                error.setTitle("\uD83D\uDD34 Выбранным сообщениям больше 2 недель");
                error.setDescription("Сообщения, которым больше 2 недель, нельзя удалить");
                break;

            case INVALID_VALUE:
                error.setTitle("\uD83D\uDD34 Значение должно быть целочисленным");
                error.setDescription("Для удаления сообщений используйте:  \n" + DiscordBot.getPrefix() + "clear [кол-во сообщений]");
                break;

            default:
                error.setTitle("\uD83D\uDD34 Неизвестная команда");
                error.setDescription("Для удаления сообщений используйте:  \n" + DiscordBot.getPrefix() + "clear [кол-во сообщений]");
        }
        return error;
    }

    private static EmbedBuilder correctCommand(String amountDeleted) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("✅ Успешно удалено " + amountDeleted + " сообщений.");
        eb.setColor(Color.GREEN);

        return eb;
    }

}
