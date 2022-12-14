package me.Leruk.commands.music;

import me.Leruk.DiscordBot;
import me.Leruk.errors.ErrorChecks;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ConstantConditions")
public class JoinCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        final String[] args = e.getMessage().getContentRaw().split(" ");
        final TextChannel tChannel = e.getChannel().asTextChannel();

        final EmbedBuilder error = new EmbedBuilder();

        if (args[0].equalsIgnoreCase(DiscordBot.getPrefix() + "join")) {

            if(!ErrorChecks.isMusicChanel(tChannel))
            {
                e.getMessage().delete().queue();

                error.setTitle("Музыка работает только в `#музыка`");
                error.setColor(Color.RED);

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue((warning) -> warning.delete().queueAfter(10, TimeUnit.SECONDS));
                return;
            }

            if (args.length > 1) {
                error.setDescription('`' + DiscordBot.getPrefix() + "join` - команда для вызова бота");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            final GuildVoiceState memberVoiceState = e.getMember().getVoiceState();

            if(!ErrorChecks.isMemberPresent(memberVoiceState))
            {
                error.setDescription("Для обращения к боту, надо находится в голосовом канале");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            final GuildVoiceState botVoiceState = e.getGuild().getSelfMember().getVoiceState();

            if(ErrorChecks.isBotPresent(botVoiceState))
            {
                if (!ErrorChecks.isSameChannelAsBot(botVoiceState, memberVoiceState))
                {
                    error.setDescription("Бот находится в другом голосовом канале");

                    tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                }

                return;
            }

            final AudioManager audioManager = e.getGuild().getAudioManager();
            final AudioChannel memberChannel = memberVoiceState.getChannel();

            audioManager.openAudioConnection(memberChannel);
        }
    }
}
