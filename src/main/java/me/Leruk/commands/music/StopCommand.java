package me.Leruk.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


@SuppressWarnings("ConstantConditions")
public class StopCommand extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        final String[] args = e.getMessage().getContentRaw().split(" ");
        final TextChannel tChannel = e.getChannel().asTextChannel();

        final EmbedBuilder error = new EmbedBuilder();

        if(args[0].equalsIgnoreCase(DiscordBot.getPrefix() + "stop"))
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
                error.setDescription('`' + DiscordBot.getPrefix() + "stop` - команда для чистки, и остановки трек-листа");

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
                error.setDescription("Нечего останавливать");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

            player.stopTrack();
            queue.clear();

            tChannel.sendMessage("Музыка остановлена. Список c музыкой пуст").queue();
        }

    }
}
