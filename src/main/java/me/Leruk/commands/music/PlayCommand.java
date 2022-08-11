package me.Leruk.commands.music;

import me.Leruk.DiscordBot;
import me.Leruk.errors.ErrorChecks;
import me.Leruk.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ConstantConditions")
public class PlayCommand extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent e) {
        final String[] args = e.getMessage().getContentRaw().split(" ");
        final TextChannel tChannel = e.getChannel().asTextChannel();

        final EmbedBuilder error = new EmbedBuilder();

        if (args[0].equalsIgnoreCase(DiscordBot.getPrefix() + "play")) {

            if(!ErrorChecks.isMusicChanel(tChannel))
            {
                e.getMessage().delete().queue();

                error.setTitle("Музыка работает только в `#музыка`");
                error.setColor(Color.RED);

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue((warning) -> warning.delete().queueAfter(10, TimeUnit.SECONDS));
                return;
            }

            if (args.length == 1) {
                error.setDescription('`' + DiscordBot.getPrefix() + "play` - команда для проигрывания трека");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            final GuildVoiceState botVoiceState = e.getGuild().getSelfMember().getVoiceState();
            final GuildVoiceState memberVoiceState = e.getMember().getVoiceState();

            if(!ErrorChecks.isMemberPresent(memberVoiceState))
            {
                error.setDescription("Для обращения к боту, надо находится в голосовом канале");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            if(ErrorChecks.isBotPresent(botVoiceState))
            {
                if (!ErrorChecks.isSameChannelAsBot(botVoiceState, memberVoiceState))
                {
                    error.setDescription("Бот находится в другом голосовом канале");

                    tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                    return;
                }
            }

            String link = getLink(args);

            final AudioManager audioManager = e.getGuild().getAudioManager();
            final AudioChannel memberChannel = memberVoiceState.getChannel();

            PlayerManager.getInstance().loadAndPlay(tChannel, link, audioManager, memberChannel);
        }

    }

    private String getLink(String[] args)
    {
        StringBuilder linkBuilder = new StringBuilder();
        for(int i = 1; i < args.length; i++)
        {
            linkBuilder.append(args[i]);
        }
        linkBuilder.trimToSize();

        String link = linkBuilder.toString();
        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        return link;
    }

    private boolean isUrl(String url)
    {
        try
        {
            new URL(url);
            return true;
        }
        catch (MalformedURLException e)
        {
            return false;
        }
    }
}
