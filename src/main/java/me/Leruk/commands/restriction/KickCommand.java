package me.Leruk.commands.restriction;

import me.Leruk.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

@SuppressWarnings("ConstantConditions")
public class KickCommand extends ListenerAdapter
{
    public void onMessageReceived(MessageReceivedEvent e)
    {
        final String[] args = e.getMessage().getContentRaw().split(" ");
        final TextChannel tChannel = e.getChannel().asTextChannel();

        final EmbedBuilder error = new EmbedBuilder();

        if(args[0].equalsIgnoreCase(DiscordBot.getPrefix() + "kick"))
        {
            if(e.getMessage().getMentions().getUsers().isEmpty())
            {
                error.setDescription("Для удаления участника его надо упомянуть используя команду: \n`"
                        +  DiscordBot.getPrefix() + "kick [@имя_пользователя]`");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            final Member member = e.getMember();
            final Member kickUser = e.getMessage().getMentions().getMembers().get(0);
            final Member selfMember = e.getGuild().getSelfMember();

            if(!member.hasPermission(Permission.KICK_MEMBERS))
            {
                error.setDescription("У вас нет прав удалять участников этой группы");
                error.setColor(Color.RED);

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            if (!selfMember.hasPermission(Permission.KICK_MEMBERS)) {
                error.setDescription("У меня нет прав удалять участников этой группы");
                error.setColor(Color.RED);

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            if (!selfMember.canInteract(kickUser) || !member.canInteract(kickUser))
            {
                error.setDescription("Участник, которого вы хотите удалить является администратором");
                error.setColor(Color.RED);

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }


            e.getGuild()
                    .kick(kickUser, "⠀")
                    .reason("⠀")
                    .queue(
                            (__) -> e.getChannel().sendMessage("Участник " + kickUser.getUser().getName() + " был удален").queue(),
                            (trouble) -> e.getChannel().sendMessageFormat("При удалении участника произошла ошибка: %s", trouble.getMessage()).queue()
                    );
        }

    }
}
