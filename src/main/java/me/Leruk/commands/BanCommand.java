package me.Leruk.commands;

import me.Leruk.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class BanCommand extends ListenerAdapter
{
    public void onMessageReceived(MessageReceivedEvent e)
    {
        int banDays = 0;

        Member member = e.getMember();
        String[] args = e.getMessage().getContentRaw().split(" ");

        if(args[0].equalsIgnoreCase(DiscordBot.getPrefix() + "ban"))
        {
            if(e.getMessage().getMentions().getUsers().isEmpty() || args.length != 3)
            {
                e.getChannel().sendMessage(" ").setEmbeds(invalidCommand().build()).queue();
                return;
            }

            try
            {
                banDays = Integer.parseInt(args[2]);
            }
            catch (Exception exception)
            {
                e.getChannel().sendMessage(" ").setEmbeds(invalidCommand().build()).queue();
                return;
            }

            Member banUser = e.getMessage().getMentions().getMembers().get(0);

            if(!member.hasPermission(Permission.BAN_MEMBERS))
            {
                e.getChannel().sendMessage("У вас нет прав банить участников этой группы").queue();
                return;
            }

            final Member selfMember = e.getGuild().getSelfMember();

            if (!selfMember.hasPermission(Permission.BAN_MEMBERS)) {
                e.getChannel().sendMessage("У меня нет прав банить участников этой группы").queue();
                return;
            }

            if (!selfMember.canInteract(banUser) || !member.canInteract(banUser))
            {
                e.getChannel().sendMessage("Участник, которого вы хотите забанить является администратором").queue();
                return;
            }


            e.getGuild()
                    .ban(banUser, banDays)
                    .reason("⠀")
                    .queue(
                            (__) -> e.getChannel().sendMessage("Участник " + banUser.getUser().getName() + " был забанен").queue(),
                            (error) -> e.getChannel().sendMessageFormat("При бане участника произошла ошибка: %s", error.getMessage()).queue()
                    );
        }

    }

    private static EmbedBuilder invalidCommand()
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Неправильно введенная команда");
        eb.setDescription("Для бана участника надо использовать команду \n" + DiscordBot.getPrefix() + "ban [@имя_пользователя] [кол-во дней]");
        eb.setColor(Color.RED);

        return eb;
    }

}
