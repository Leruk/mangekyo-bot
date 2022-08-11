package me.Leruk.errors;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;


@SuppressWarnings("ConstantConditions")
public class ErrorChecks {

    public static boolean isMusicChanel(TextChannel tChannel) {
        return tChannel.getName().equals("музыка");
    }

    public static boolean isBotPresent(GuildVoiceState botVoiceState) {
        return botVoiceState.inAudioChannel();
    }

    public static boolean isMemberPresent(GuildVoiceState memberVoiceState) {
        return memberVoiceState.inAudioChannel();
    }

    public static boolean isSameChannelAsBot(GuildVoiceState botVoiceState, GuildVoiceState memberVoiceState) {
        return botVoiceState.inAudioChannel() && memberVoiceState.getChannel().equals(botVoiceState.getChannel());
    }

    public static boolean isAudioPlaying(GuildVoiceState botVoiceState, AudioPlayer player) {
        return botVoiceState.inAudioChannel() && player.getPlayingTrack() != null;
    }

}
