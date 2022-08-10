package me.Leruk.commands.music;

import me.Leruk.DiscordBot;
import me.Leruk.errors.ErrorCodes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

@SuppressWarnings("ConstantConditions")
public class JoinCommand extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent e) {
        final String[] args = e.getMessage().getContentRaw().split(" ");
        final MessageChannel channel = e.getChannel();

        if(args[0].equalsIgnoreCase(DiscordBot.getPrefix() + "join"))
        {
            if(args.length >= 2)
            {
                channel.sendMessage(" ").setEmbeds(invalidCommand(ErrorCodes.AMOUNT_ARGUMENTS).build()).queue();
                return;
            }

            final Member member = e.getMember();
            final GuildVoiceState memberVoiceState = member.getVoiceState();

            if(!member.getVoiceState().inAudioChannel())
            {
                channel.sendMessage(" ").setEmbeds(invalidCommand(ErrorCodes.MEMBER_MISS_VOICE).build()).queue();
                return;
            }

            final Member bot = e.getGuild().getSelfMember();
            final GuildVoiceState botVoiceState = bot.getVoiceState();

            if(botVoiceState.inAudioChannel() && memberVoiceState.getChannel() != botVoiceState.getChannel())
            {
                channel.sendMessage(" ").setEmbeds(invalidCommand(ErrorCodes.BOT_VOICE_ACTIVE).build()).queue();
                return;
            }

            if(botVoiceState.getChannel() == botVoiceState.getChannel()) return;

            final AudioManager audioManager = e.getGuild().getAudioManager();
            final AudioChannel audioChannel = memberVoiceState.getChannel();

            audioManager.openAudioConnection(audioChannel);
            channel.sendMessage(" ").setEmbeds(correctCommand(memberVoiceState.getChannel().getName()).build()).queue();
        }


    }

    private static EmbedBuilder invalidCommand(ErrorCodes errorCode) {
        EmbedBuilder error = new EmbedBuilder();

        error.setColor(Color.RED);

        switch (errorCode) {
            case AMOUNT_ARGUMENTS:
                error.setTitle("\uD83D\uDD34 Неверное кол-во аргументов");
                error.setDescription("Для вызова бота используйте:  \n" + DiscordBot.getPrefix() + "join");
                break;

            case MEMBER_MISS_VOICE:
                error.setTitle("\uD83D\uDD34 Ошибка подключения");
                error.setDescription("Для вызова бота, надо находится в голосовом канале");
                break;

            case BOT_VOICE_ACTIVE:
                error.setTitle("\uD83D\uDD34 Ошибка подключения");
                error.setDescription("Бот уже занят другим участником сервера");
                break;

            default:
                error.setTitle("\uD83D\uDD34 Неизвестная команда");
                error.setDescription("Для вызова бота используйте:  \n" + DiscordBot.getPrefix() + "join");
        }
        return error;
    }

    private static EmbedBuilder correctCommand(String channelName) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("\uD83D\uDD0A Подключился к  " + channelName);
        eb.setColor(Color.GREEN);

        return eb;
    }


}
