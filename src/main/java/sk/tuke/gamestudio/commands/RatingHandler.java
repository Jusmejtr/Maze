package sk.tuke.gamestudio.commands;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.json.JSONObject;
import sk.tuke.gamestudio.utils.ApiRequest;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public class RatingHandler extends ListenerAdapter {
    ApiRequest apiRequest = new ApiRequest();
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equals("average-rating")){
            int averageRating = apiRequest.getRequestInteger("http://localhost:8080/api/rating/maze");

            if(averageRating == 0){
                event.reply("Error loading average rating.").setEphemeral(true).queue();
                return;
            }

            event.reply("Average rating is "+averageRating).setEphemeral(true).queue();
        }
        if(event.getName().equals("player-rating")){
            OptionMapping optionRows = event.getOption("player");
            IMentionable mentionable = optionRows.getAsMentionable();

            int playerRating = apiRequest.getRequestInteger("http://localhost:8080/api/rating/maze/"+ mentionable.getId());
            if(playerRating == 0){
                event.reply("There is no rating for player "+ mentionable.getAsMention()).setEphemeral(true).queue();
                return;
            }

            event.reply(mentionable.getAsMention() + " rating is " + playerRating).setEphemeral(true).queue();
        }
        if(event.getName().equals("add-rating")){
            OptionMapping optionRows = event.getOption("rating");

            boolean result = createRating(event.getUser().getId(), optionRows.getAsInt());

            if(result){
                event.reply("Thanks for your rating").setEphemeral(true).queue();
            }else{
                event.reply("An error has occurred, please try again later.").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getComponent().getId()){
            case "oneStar":
                ratingResult(createRating(event.getUser().getId(), 1), event);
                break;
            case "twoStar":
                ratingResult(createRating(event.getUser().getId(), 2), event);
                break;
            case "threeStar":
                ratingResult(createRating(event.getUser().getId(), 3), event);
                break;
            case "fourStar":
                ratingResult(createRating(event.getUser().getId(), 4), event);
                break;
            case "fiveStar":
                ratingResult(createRating(event.getUser().getId(), 5), event);
                break;
        }
    }

    public boolean createRating(String player, int rating) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("game", "maze");
        jsonObject.put("rating", rating);
        jsonObject.put("player", player);
        jsonObject.put("ratedon", getCurrentTime());

        return apiRequest.postRequestJson(jsonObject, "http://localhost:8080/api/rating");
    }
    private String getCurrentTime(){
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);

        OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, ZoneOffset.UTC);
        return offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
    private void ratingResult(boolean result, ButtonInteractionEvent event){
        if(result){
            event.reply("Your rating has been saved.").setEphemeral(true).queue();
        }else{
            event.reply("An error has occurred, please try again later.").setEphemeral(true).queue();
        }
    }
}
