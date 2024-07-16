package sk.tuke.gamestudio.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import sk.tuke.gamestudio.utils.ConfigManager;


public class ConfigUpdate extends ListenerAdapter {
    private ConfigManager configManager;
    public ConfigUpdate(ConfigManager configManager){
        this.configManager = configManager;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("result-per-page")) {
            OptionMapping option = event.getOption("result-size");
            if(option == null){
                event.reply("Result per page is configured to " + configManager.getResultPerPage()).setEphemeral(true).queue();
            }else{
                int perPage = option.getAsInt();
                configManager.updateConfig("resultPerPage", perPage);
                configManager.notif();
                event.reply("Result per page configured to " + perPage).setEphemeral(true).queue();
            }
        }
        if (event.getName().equals("image-size")) {
            OptionMapping option = event.getOption("image-size");
            if(option == null){
                event.reply("Image size is configured to " + configManager.getImageSize()+"px").setEphemeral(true).queue();
            }else{
                int imageSize = option.getAsInt();
                configManager.updateConfig("imageSize", imageSize);
                configManager.notif();
                event.reply("Image size configured to " + imageSize).setEphemeral(true).queue();
            }
        }
        if(event.getName().equals("set-image")){
            OptionMapping texture = event.getOption("texture");
            OptionMapping type = event.getOption("texture-type");

            if(!texture.getAsAttachment().getFileExtension().equals("png")){
                event.reply("Only PNG images are allowed").setEphemeral(true).queue();
                return;
            }


            switch (type.getAsString().toLowerCase()){
                case "wall":
                    texture.getAsAttachment().downloadToFile("./src/main/resources/static/images/maze/wall.png")
                            .thenAccept(file -> {
                                event.reply("Your wall texture has been saved").setEphemeral(true).queue();
                                configManager.notif();
                            })
                            .exceptionally(t ->
                            {
                                event.reply("Error while saving file").setEphemeral(true).queue();
                                return null;
                            });
                    break;
                case "road":
                    texture.getAsAttachment().downloadToFile("./src/main/resources/static/images/maze/road.png")
                            .thenAccept(file -> {
                                event.reply("Your road texture has been saved").setEphemeral(true).queue();
                                configManager.notif();
                            })
                            .exceptionally(t ->
                            {
                                event.reply("Error while saving file").setEphemeral(true).queue();
                                return null;
                            });
                    break;
                case "player":
                    texture.getAsAttachment().downloadToFile("./src/main/resources/static/images/maze/player.png")
                            .thenAccept(file -> {
                                event.reply("Your player texture has been saved").setEphemeral(true).queue();
                                configManager.notif();
                            })
                            .exceptionally(t ->
                            {
                                event.reply("Error while saving file").setEphemeral(true).queue();
                                return null;
                            });
                    break;
                case "finish":
                    texture.getAsAttachment().downloadToFile("./src/main/resources/static/images/maze/finish.png")
                            .thenAccept(file -> {
                                event.reply("Your finish texture has been saved").setEphemeral(true).queue();
                                configManager.notif();
                            })
                            .exceptionally(t ->
                            {
                                event.reply("Error while saving file").setEphemeral(true).queue();
                                return null;
                            });
                    break;
                default:
                    event.reply("You need to select one of the textures types: `wall` `player` `finish` `road`").setEphemeral(true).queue();
                    break;
            }
        }
    }
}
