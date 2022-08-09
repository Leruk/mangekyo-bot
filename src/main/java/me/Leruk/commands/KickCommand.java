package me.Leruk.commands;

import me.Leruk.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class KickCommand extends ListenerAdapter
{
    public void onMessageReceived(MessageReceivedEvent e)
    {
        Member member = e.getMember();
        String[] args = e.getMessage().getContentRaw().split(" ");

        if(args[0].equalsIgnoreCase(DiscordBot.getPrefix() + "kick"))
        {
            if(e.getMessage().getMentions().getUsers().isEmpty())
            {
                e.getChannel().sendMessage(" ").setEmbeds(invalidCommand().build()).queue();
                return;
            }

            Member kickUser = e.getMessage().getMentions().getMembers().get(0);

            final Member selfMember = e.getGuild().getSelfMember();

            if(!member.hasPermission(Permission.KICK_MEMBERS))
            {
                e.getChannel().sendMessage("У вас нет прав удалять участников этой группы").queue();
                return;
            }

            if (!selfMember.hasPermission(Permission.KICK_MEMBERS)) {
                e.getChannel().sendMessage("У меня нет прав удалять участников этой группы").queue();
                return;
            }

            if (!selfMember.canInteract(kickUser) || !member.canInteract(kickUser))
            {
                e.getChannel().sendMessage("Участник, которого вы хотите удалить является администратором").queue();
                return;
            }


            e.getGuild()
                    .kick(kickUser, "⠀")
                    .reason("⠀")
                    .queue(
                            (__) -> e.getChannel().sendMessage("Участник " + kickUser.getUser().getName() + " был удален").queue(),
                            (error) -> e.getChannel().sendMessageFormat("При удалении участника произошла ошибка: %s", error.getMessage()).queue()
                    );
        }

    }

    private static EmbedBuilder invalidCommand()
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Отсутствует упоминание для удаления");
        eb.setDescription("Для удаления участника надо упомянуть используя команду \n" +  DiscordBot.getPrefix() + "kick [@имя_пользователя]");
        eb.setColor(Color.RED);

        return eb;
    }

}
