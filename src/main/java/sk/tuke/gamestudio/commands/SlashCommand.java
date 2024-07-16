package sk.tuke.gamestudio.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import sk.tuke.gamestudio.utils.ApiRequest;
import sk.tuke.gamestudio.utils.ConfigManager;
import sk.tuke.gamestudio.utils.Observer;
import sk.tuke.gamestudio.utils.Subject;
import sk.tuke.gamestudio.core.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;


public class SlashCommand extends ListenerAdapter implements Observer {
    private BufferedImage wall;
    private BufferedImage player;
    private BufferedImage road;
    private BufferedImage finish;

    private HashMap<String, Field> games = new HashMap<>();// hashmap for active games
    private int imageSize;
    ApiRequest apiRequest = new ApiRequest();
    ScoreHandler scoreHandler = new ScoreHandler();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equals("play")){
            OptionMapping optionRows = event.getOption("rows");
            OptionMapping optionColumns = event.getOption("columns");

            if(optionRows == null){
                event.reply("No rows provided").queue();
            }
            if(optionColumns == null){
                event.reply("No columns provided").queue();
            }
            int rows = optionRows.getAsInt();
            int columns = optionColumns.getAsInt();


            if(!isOdd(rows)){
                event.reply("Number of rows must be odd number.").setEphemeral(true).queue();
                return;
            }
            if(!isOdd(columns)){
                event.reply("Number of columns must be odd number.").setEphemeral(true).queue();
                return;
            }
            games.put(event.getUser().getId(), new Field(rows, columns));

            event.replyFiles(FileUpload.fromData(generateImage(getField(getUserId(event)).getRowCount(), getField(getUserId(event)).getColumnCount(), getUserId(event)))).addActionRow(
                    Button.primary("left", "LEFT"),
                    Button.primary("right", "RIGHT"),
                    Button.primary("up", "UP"),
                    Button.primary("down", "DOWN"),
                    Button.danger("exit", "EXIT")
            ).setEphemeral(true).queue();
        }

    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getComponent().getId()){
            case "left":
                if(!canPlay(event)) return;
                getField(getUserId(event)).getPlayer().move(Direction.LEFT);
                updateImage(event);
                break;
            case "right":
                if(!canPlay(event)) return;
                getField(getUserId(event)).getPlayer().move(Direction.RIGHT);
                updateImage(event);
                break;
            case "up":
                if(!canPlay(event)) return;
                getField(getUserId(event)).getPlayer().move(Direction.UP);
                updateImage(event);
                break;
            case "down":
                if(!canPlay(event)) return;
                getField(getUserId(event)).getPlayer().move(Direction.DOWN);
                updateImage(event);
                break;
            case "exit":
                if(!canPlay(event)) return;
                endGame(getUserId(event));
                event.reply("thanks for playing").setEphemeral(true).queue();
                break;
        }
    }
    @Override
    public void update(Subject subject) {
        System.out.println("Loading textures ...");
        this.imageSize = ((ConfigManager) subject).getImageSize();
        this.wall = resizeImage(loadImage("./src/main/resources/static/images/maze/wall.png"), imageSize, imageSize);
        this.player = resizeImage(loadImage("./src/main/resources/static/images/maze/player.png"), imageSize, imageSize);
        this.finish = resizeImage(loadImage("./src/main/resources/static/images/maze/finish.png"), imageSize, imageSize);
        this.road = resizeImage(loadImage("./src/main/resources/static/images/maze/road.png"), imageSize, imageSize);
        System.out.println("Textures loaded successfully");

    }
    private BufferedImage resizeImage(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage image = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = image.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return image;
    }

    private void updateImage(ButtonInteractionEvent event) {
        if(getField(getUserId(event)).getFieldState() == FieldState.FINISHED){
            renderStats(event);
        }else{
            event.editMessageAttachments(FileUpload.fromData(generateImage(getField(getUserId(event)).getRowCount(), getField(getUserId(event)).getColumnCount(), getUserId(event)))).queue();
        }
    }

    private File generateImage(int rows, int columns, String userId) {
        int offsetX = 25;
        BufferedImage img = new BufferedImage(imageSize*columns+2*offsetX, imageSize*rows+25, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        Font font = new Font("Arial", Font.BOLD, 20);

        g2d.setFont(font);
        g2d.setColor(Color.GREEN);

        g2d.drawString("MAZE GAME", (imageSize*columns+2*offsetX)/2 - 60, 17);

        for (int row = 0; row < getField(userId).getRowCount(); row++) {
            for (int col = 0; col < getField(userId).getColumnCount(); col++) {
                int imageX = col * imageSize+offsetX;
                int imageY = row * imageSize+25;
                if (getField(userId).getTile(row, col).getState() == TileState.PLAYER) {
                    g2d.drawImage(player, imageX, imageY, null);
                } else if (getField(userId).getTile(row, col).getState() == TileState.FINISH) {
                    g2d.drawImage(finish, imageX, imageY, null);
                }else if(getField(userId).getTile(row, col) instanceof Road){
                    g2d.drawImage(road, imageX, imageY, null);
                }else if(getField(userId).getTile(row, col) instanceof Wall){
                    g2d.drawImage(wall, imageX, imageY, null);
                }
            }
        }

        File output = null;

        try {
            output = new File("./src/main/resources/static/images/maze.png");
            ImageIO.write(img, "png", output);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        g2d.dispose();
        return output;
    }
    private void renderStats(ButtonInteractionEvent event){// render stats if player finish game
        int playerRating = apiRequest.getRequestInteger("http://localhost:8080/api/rating/maze/"+ getUserId(event));

        StringBuilder sb = new StringBuilder();
        sb.append("Your score is: **" + getField(getUserId(event)).getScore()+ "**\n" +
                "Your time is: **"+ calculateTime(getField(getUserId(event)).getTimer()) +"**");
        if(playerRating == 0){
            sb.append("\n\nYou don't have a rating yet, you can fix it below");
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Congratulation " + event.getUser().getName()+", YOU WON!");
        eb.setDescription(sb.toString());
        eb.setTimestamp(Instant.now());

        boolean result = scoreHandler.creatScore(getUserId(event), getField(getUserId(event)).getScore());
        if(!result){
            event.reply("Error while writing score to database").queue();
            return;
        }
        if(playerRating == 0){
            event.editMessageEmbeds(eb.build()).setAttachments(FileUpload.fromData(generateImage(getField(getUserId(event)).getRowCount(), getField(getUserId(event)).getColumnCount(), getUserId(event))))
                    .setActionRow(
                            Button.primary("oneStar", "⭐"),
                            Button.primary("twoStar", "⭐⭐"),
                            Button.primary("threeStar", "⭐⭐⭐"),
                            Button.primary("fourStar", "⭐⭐⭐⭐"),
                            Button.primary("fiveStar", "⭐⭐⭐⭐⭐")

                    ).queue();
        }else{
            List<ActionRow> actionRow = event.getMessage().getActionRows();
            actionRow.removeIf(row -> row.getComponents().contains(event.getComponent()));

            event.editMessageEmbeds(eb.build()).setAttachments(FileUpload.fromData(generateImage(getField(getUserId(event)).getRowCount(), getField(getUserId(event)).getColumnCount(), getUserId(event)))).setComponents(actionRow).queue();
        }
        endGame(getUserId(event));
    }

    private boolean canPlay(ButtonInteractionEvent event){

        if(getField(getUserId(event)) == null){
            event.reply("This game is not active, please create a new one.").setEphemeral(true).queue();
            return false;
        }
        return true;
    }
    private Field getField(String userId){
        return games.get(userId);
    }
    private String getUserId(ButtonInteractionEvent event){
        return event.getUser().getId();
    }
    private String getUserId(SlashCommandInteractionEvent event){
        return event.getUser().getId();
    }
    private void endGame(String userId){
        games.remove(userId);
    }

    private static BufferedImage loadImage(String filename) {
        try {
            return ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isOdd(int number) {
        if (number % 2 == 1) {
            return true;
        } else {
            return false;
        }
    }
    public String calculateTime(Timer timer){
        StringBuilder sb = new StringBuilder();
        if(timer.getEndTimeMinutes()%60 < 10){
            sb.append("0");
        }
        sb.append(timer.getEndTimeMinutes()%60);
        sb.append(":");
        if(timer.getEndTimeSeconds()%60 < 10){
            sb.append("0");
        }
        sb.append(timer.getEndTimeSeconds()%60);
        return sb.toString();
    }

}
