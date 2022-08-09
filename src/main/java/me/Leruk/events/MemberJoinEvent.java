package me.Leruk.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Random;

public class MemberJoinEvent extends ListenerAdapter {

    String[] greetings = {
            "Приветствуем, [member]. Теперь в нашем сервере на одного бунтаря больше!",
            "[member] агресивно вколол герыча, и присоединился на наш сервер. Поприветствуем его!",
            "[member] приветствуем смотрящего. Рады видеть тебя на нашем сервере!"
    };

    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        Random random = new Random();
        int num = random.nextInt(greetings.length);

        EmbedBuilder join = new EmbedBuilder();
        join.setColor(random.nextInt(0xffffff));
        join.setDescription(greetings[num].replace("[member]", e.getMember().getAsMention()));

        e.getGuild().getSystemChannel().sendMessage(" ").setEmbeds(join.build()).queue();

        //Add default role
        e.getGuild().addRoleToMember(e.getMember(), e.getGuild().getRolesByName("Бунтарь", true).get(0)).complete();
    }
}
