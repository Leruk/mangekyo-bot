package me.Leruk.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Random;

public class MemberLeaveEvent extends ListenerAdapter {

    String[] farewells = {
            "[member] ливнул с позором!",
            "[member] ливнул, и перешел на сторону зла!"
    };

    public void onGuildMemberRemove(GuildMemberRemoveEvent e)
    {
        Random random = new Random();
        int num = random.nextInt(farewells.length);

        EmbedBuilder leave = new EmbedBuilder();
        leave.setColor(Color.BLACK);
        leave.setDescription(farewells[num].replace("[member]", e.getUser().getAsMention()));

        e.getGuild().getSystemChannel().sendMessage(" ").setEmbeds(leave.build()).queue();
    }


}
