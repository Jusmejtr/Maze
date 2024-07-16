package sk.tuke.gamestudio.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.json.JSONArray;
import org.json.JSONObject;

import sk.tuke.gamestudio.Bot;
import sk.tuke.gamestudio.utils.*;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ScoreHandler extends ListenerAdapter implements Observer {
    private int perPage;
    private int size;
    private Button back = Button.primary("pageBackScore", Emoji.fromUnicode("◀"));
    private Button next = Button.primary("pageNextScore", Emoji.fromUnicode("▶"));
    private JSONArray scoreData;
    ApiRequest apiRequest = new ApiRequest();
    private TableBuilder tableBuilder = new TableBuilder(back, next);
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equals("topscore")){
            apiRequest = new ApiRequest();
            scoreData = apiRequest.getRequestJson("http://localhost:8080/api/score/maze");


            if(scoreData == null){
                event.reply("Error loading top score.").setEphemeral(true).queue();
                return;
            }
            size = scoreData.length() - 1;//start indexing form 0

            if(size <= perPage-1){
                tableBuilder.createTable(event, getDataForPage(0, size), true);
            }else{
                tableBuilder.createTable(event, getDataForPage(0, perPage-1), false);
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getComponent().getId()){
            case "pageBackScore":
                String footerText2 = event.getMessage().getEmbeds().get(0).getFooter().getText();
                String[] parts2 = footerText2.split("/");
                int curPage2 = Integer.parseInt(parts2[0]);

                if (curPage2 == 2) {
                    tableBuilder.previousPage(event, getDataForPage(0, perPage - 1), true);
                } else {
                    curPage2 -= 2;
                    tableBuilder.previousPage(event, getDataForPage(curPage2 * perPage, curPage2 * perPage + perPage - 1), false);
                }
                break;
            case "pageNextScore":
                String footerText = event.getMessage().getEmbeds().get(0).getFooter().getText();
                String[] parts = footerText.split("/");
                int curPage = Integer.parseInt(parts[0]);

                if (scoreData.length() <= curPage * perPage + perPage) {
                    tableBuilder.nextPage(event, getDataForPage(curPage * perPage, scoreData.length() - 1), true);
                } else {
                    tableBuilder.nextPage(event, getDataForPage(curPage * perPage, curPage * perPage + perPage - 1), false);
                }
                break;
        }
    }
    @Override
    public void update(Subject subject) {
        this.perPage = ((ConfigManager) subject).getResultPerPage();
    }
    public boolean creatScore(String player, int score) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("game", "maze");
        jsonObject.put("points", score);
        jsonObject.put("player", player);
        jsonObject.put("playedon", getCurrentTime());

        return apiRequest.postRequestJson(jsonObject, "http://localhost:8080/api/score");
    }
    private String getCurrentTime(){
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);

        OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, ZoneOffset.UTC);
        return offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private EmbedBuilder getDataForPage(int start, int end){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("TOP SCORES", null);
        eb.setColor(Color.red);
        StringBuilder sb = new StringBuilder();

        for (int i = start; i <= end; i++) {
            JSONObject jsonObject = scoreData.getJSONObject(i);
            //String game = jsonObject.getString("game");
            String player = jsonObject.getString("player");
            int points = jsonObject.getInt("points");
            String playedOn = jsonObject.getString("playedon");

            int position = i+1;

            String score = Integer.toString(points);
            Member member = Bot.getGuild().getMemberById(player);
            if(member == null){
                sb.append(position + ". Anonymous: `"  + score + "` "+getFormattedTime(playedOn)+"\n\n");
            }else{
                sb.append(position + ". <@" + member.getId() +  "> : `" + score + "` "+getFormattedTime(playedOn)+"\n\n");
            }
        }
        eb.setDescription(sb.toString());
        eb.setFooter((start/perPage)+1+"/" + ((size/perPage)+1));
        eb.setTimestamp(Instant.now());
        return eb;
    }
    private String getFormattedTime(String time){
        LocalDateTime dt = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        Instant instant = dt.toInstant(ZoneOffset.UTC);
        return "<t:" + instant.getEpochSecond() + ":f>";
    }

}
