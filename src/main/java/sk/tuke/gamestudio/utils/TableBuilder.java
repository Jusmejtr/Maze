package sk.tuke.gamestudio.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;


public class TableBuilder {
    private final Button back;
    private final Button next;
    public TableBuilder(Button back, Button next){
        this.back = back;
        this.next = next;
    }
    public void createTable(SlashCommandInteractionEvent event, EmbedBuilder embedBuilder, boolean singlePage){
        if(singlePage){
            event.replyEmbeds(embedBuilder.build()).setEphemeral(true)
                    .addActionRow(
                            back.asDisabled(),
                            next.asDisabled()
                    ).queue();
        }else{
            event.replyEmbeds(embedBuilder.build()).setEphemeral(true)
                    .addActionRow(
                            back.asDisabled(),
                            next.asEnabled()
                    ).queue();
        }
    }

    public void previousPage(ButtonInteractionEvent event, EmbedBuilder embedBuilder, boolean firstPage){
        if (firstPage) {
            event.editMessageEmbeds(embedBuilder.build()).setActionRow(
                    back.asDisabled(),
                    next.asEnabled()
            ).queue();
        } else {
            event.editMessageEmbeds(embedBuilder.build()).setActionRow(
                    back.asEnabled(),
                    next.asEnabled()
            ).queue();
        }
    }
    public void nextPage(ButtonInteractionEvent event, EmbedBuilder embedBuilder, boolean lastPage){
        if (lastPage) {
            event.editMessageEmbeds(embedBuilder.build()).setActionRow(
                    back.asEnabled(),
                    next.asDisabled()
            ).queue();
        } else {
            event.editMessageEmbeds(embedBuilder.build()).setActionRow(
                    back.asEnabled(),
                    next.asEnabled()
            ).queue();
        }
    }
}
