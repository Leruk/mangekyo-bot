package me.Leruk.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import me.Leruk.DiscordBot;
import me.Leruk.errors.ErrorChecks;
import me.Leruk.lavaplayer.GuildMusicManager;
import me.Leruk.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ConstantConditions")
public class RepeatCommand extends ListenerAdapter {

    private final String REPEAT = "\uD83D\uDD01";

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        final String[] args = e.getMessage().getContentRaw().split(" ");
        final TextChannel tChannel = e.getChannel().asTextChannel();

        final EmbedBuilder error = new EmbedBuilder();

        if(args[0].equalsIgnoreCase(DiscordBot.getPrefix() + "repeat"))
        {
            if(!ErrorChecks.isMusicChanel(tChannel))
            {
                e.getMessage().delete().queue();

                error.setTitle("Музыка работает только в `#музыка`");
                error.setColor(Color.RED);

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue((warning) -> warning.delete().queueAfter(10, TimeUnit.SECONDS));
                return;
            }

            if (args.length > 1) {
                error.setDescription('`' + DiscordBot.getPrefix() + "repeat` - команда для повтора трека");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            final GuildVoiceState botVoiceState = e.getGuild().getSelfMember().getVoiceState();
            final GuildVoiceState memberVoiceState = e.getMember().getVoiceState();

            if(!ErrorChecks.isBotPresent(botVoiceState))
            {
                error.setDescription("Бот отсутствует в голосовом канале");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            if(!ErrorChecks.isMemberPresent(memberVoiceState))
            {
                error.setDescription("Для обращения к боту, надо находится в голосовом канале");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            if (!ErrorChecks.isSameChannelAsBot(botVoiceState, memberVoiceState))
            {
                error.setDescription("Бот находится в другом голосовом канале");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
            final AudioPlayer player = musicManager.audioPlayer;

            if (!ErrorChecks.isAudioPlaying(botVoiceState, player))
            {
                error.setDescription("Нечего повторять");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            boolean newRepeating = !musicManager.scheduler.repeating;

            musicManager.scheduler.repeating = newRepeating;

            EmbedBuilder repeat = new EmbedBuilder();
            repeat.setColor((new Color(115, 147, 179)));
            repeat.setDescription(newRepeating ? REPEAT + " Повтор Вкл." : "Повтор Выкл.");


            tChannel.sendMessage(" ").setEmbeds(repeat.build()).queue();
        }
    }
}
