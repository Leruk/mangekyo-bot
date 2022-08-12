package me.Leruk.commands.restriction;

import me.Leruk.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class BanCommand extends ListenerAdapter
{
    public void onMessageReceived(MessageReceivedEvent e)
    {
        final String[] args = e.getMessage().getContentRaw().split(" ");
        final TextChannel tChannel = e.getChannel().asTextChannel();

        int banDays = 0;
        EmbedBuilder error = new EmbedBuilder();

        if(args[0].equalsIgnoreCase(DiscordBot.getPrefix() + "ban"))
        {
            if(e.getMessage().getMentions().getUsers().isEmpty())
            {
                error.setDescription("Для бана участника его надо упомянуть, и написать срок используя команду: \n`"
                        +  DiscordBot.getPrefix() + "ban [@имя_пользователя] [кол-во дней]`");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            try
            {
                banDays = Integer.parseInt(args[2]);
            }
            catch (Exception exception)
            {
                error.setDescription("Для бана участника его надо упомянуть, и написать срок используя команду: \n`"
                        +  DiscordBot.getPrefix() + "ban [@имя_пользователя] [кол-во дней]`");

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            final Member member = e.getMember();
            final Member banUser = e.getMessage().getMentions().getMembers().get(0);
            final Member selfMember = e.getGuild().getSelfMember();

            if(!member.hasPermission(Permission.BAN_MEMBERS))
            {
                error.setDescription("У вас нет прав банить участников этой группы");
                error.setColor(Color.RED);

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            if (!selfMember.hasPermission(Permission.BAN_MEMBERS)) {
                error.setDescription("У меня нет прав банить участников этой группы");
                error.setColor(Color.RED);

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }

            if (!selfMember.canInteract(banUser) || !member.canInteract(banUser))
            {
                error.setDescription("Участник, которого вы хотите забанить является администратором");
                error.setColor(Color.RED);

                tChannel.sendMessage(" ").setEmbeds(error.build()).queue();
                return;
            }


            e.getGuild()
                    .ban(banUser, banDays)
                    .reason("⠀")
                    .queue(
                            (__) -> e.getChannel().sendMessage("Участник " + banUser.getUser().getName() + " был забанен").queue(),
                            (trouble) -> e.getChannel().sendMessageFormat("При бане участника произошла ошибка: %s", trouble.getMessage()).queue()
                    );
        }

    }

}
