package me.Leruk.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    private List<AudioTrack> tmpAudioTracks = new ArrayList<>();

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel tChannel, String trackUrl, AudioManager audioManager, AudioChannel audioChannel) {
        final GuildMusicManager musicManager = this.getMusicManager(tChannel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {

                tChannel.sendMessage("Добавлен в очередь: `")
                        .append(audioTrack.getInfo().title)
                        .append('`')
                        .queue();

                musicManager.scheduler.queue(audioTrack);

                audioManager.openAudioConnection(audioChannel);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();

                if (audioPlaylist.isSearchResult()) {
                    String searchName = audioPlaylist.getName().substring(20);

                    if (searchName.length() == 1 &&
                            Character.isDigit(searchName.charAt(0)) &&
                            Integer.parseInt(searchName) >= 1 && Integer.parseInt(searchName) <= 5 &&
                            !tmpAudioTracks.isEmpty()) {
                        final AudioTrack audioTrack = tmpAudioTracks.get(Integer.parseInt(searchName)-1);
                        tmpAudioTracks.clear();

                        this.trackLoaded(audioTrack);
                        return;
                    }


                    final int trackCount = Math.min(tracks.size(), 5);
                    final MessageAction messageAction = tChannel.sendMessage(
                            String.format("Выберите трек используя " + DiscordBot.getPrefix() + `play 1-%d`:\n", trackCount));

                    for (int i = 0; i < trackCount; i++) {
                        final AudioTrack track = tracks.get(i);
                        final AudioTrackInfo info = track.getInfo();

                        messageAction.append('#')
                                .append(String.valueOf(i + 1))
                                .append(": `")
                                .append(info.title)
                                .append("` (")
                                .append(formatTime(track.getDuration()))
                                .append(")\n");

                        tmpAudioTracks.add(track);
                    }

                    messageAction.queue();
                    return;
                }

                tChannel.sendMessage("Добавлено в очередь: `")
                        .append(String.valueOf(tracks.size()))
                        .append("` саундтрека из `")
                        .append(audioPlaylist.getName())
                        .append('`')
                        .queue();

                for (final AudioTrack track : tracks) {
                    musicManager.scheduler.queue(track);
                }

                audioManager.openAudioConnection(audioChannel);
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    private String formatTime(long timeInMillis) {
        String formatTime = "";

        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis % TimeUnit.HOURS.toMillis(1) / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        if (hours != 0) {
            formatTime += String.format("%02d:", hours);
        }
        formatTime += String.format("%02d:%02d", minutes, seconds);

        return formatTime;
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}
