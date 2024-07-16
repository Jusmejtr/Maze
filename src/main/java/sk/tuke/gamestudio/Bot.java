package sk.tuke.gamestudio;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.json.JSONObject;
import sk.tuke.gamestudio.commands.*;
import sk.tuke.gamestudio.utils.ConfigManager;

import javax.security.auth.login.LoginException;

public class Bot {
    private static Guild guild;
    public static void main(String[] args) throws LoginException, InterruptedException{
        ConfigManager configManager = new ConfigManager();
        JSONObject config = configManager.loadConfig();

        SlashCommand slashCommand = new SlashCommand();
        ScoreHandler scoreHandler = new ScoreHandler();
        RatingHandler ratingHandler = new RatingHandler();
        CommentHandler commentHandler = new CommentHandler();
        ConfigUpdate configUpdate = new ConfigUpdate(configManager);

        JDABuilder jdaBuilder = JDABuilder.createDefault(config.getString("token"));
        JDA jda = jdaBuilder
                .setActivity(Activity.playing("Maze game"))
                .enableIntents(
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS
                )
                .addEventListeners(
                        slashCommand,
                        scoreHandler,
                        ratingHandler,
                        commentHandler,
                        configUpdate
                )
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build().awaitReady();

        configManager.subscribe(scoreHandler);
        configManager.subscribe(commentHandler);
        configManager.subscribe(slashCommand);
        configManager.notif();

        guild = jda.getGuildById(config.getString("serverID"));
        guild.loadMembers();//cache all members

        OptionData rows = new OptionData(OptionType.INTEGER, "rows", "Number of rows", true);
        rows.setMinValue(5);
        rows.setMaxValue(39);

        OptionData columns = new OptionData(OptionType.INTEGER, "columns", "Number of columns", true);
        columns.setMinValue(5);
        columns.setMaxValue(39);

        OptionData rating = new OptionData(OptionType.INTEGER, "rating", "Write your rating", true);
        rating.setMinValue(1);
        rating.setMaxValue(5);

        OptionData comment = new OptionData(OptionType.STRING, "comment", "Write your comment", true);
        comment.setMinLength(3);
        comment.setMaxLength(255);

        OptionData perPage = new OptionData(OptionType.INTEGER, "result-size", "Number of result on single page", false);
        perPage.setMinValue(1);
        perPage.setMaxValue(10);

        OptionData imageSize = new OptionData(OptionType.INTEGER, "image-size", "Number of pixels", false);
        imageSize.setMinValue(15);
        imageSize.setMaxValue(35);

        if(guild != null){
            CommandListUpdateAction commands = guild.updateCommands();
            commands.addCommands(
                    Commands.slash("play", "Start playing maze game").setGuildOnly(true)
                            .addOptions(rows)
                            .addOptions(columns),
                    Commands.slash("topscore", "Get top score").setGuildOnly(true),
                    Commands.slash("average-rating", "Get average rating of the game").setGuildOnly(true),
                    Commands.slash("player-rating", "Get player rating").setGuildOnly(true)
                            .addOption(OptionType.MENTIONABLE, "player", "Player name", true),
                    Commands.slash("comments", "Get comments for this game").setGuildOnly(true),
                    Commands.slash("add-rating", "Add your rating for the maze game").setGuildOnly(true)
                            .addOptions(rating),
                    Commands.slash("add-comment", "Add comment for the maze game").setGuildOnly(true)
                            .addOptions(comment),
                    Commands.slash("result-per-page", "Number of result per page").setGuildOnly(true)
                            .addOptions(perPage).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                    Commands.slash("image-size", "Resolution of the textures").setGuildOnly(true)
                            .addOptions(imageSize).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                    Commands.slash("set-image", "Upload PNG textures for game").setGuildOnly(true)
                            .addOption(OptionType.ATTACHMENT, "texture", "Texture for game", true)
                            .addOption(OptionType.STRING, "texture-type", "Specify type of image (wall, road, player, finish)", true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            );
            commands.queue();
        }
    }
    public static Guild getGuild(){
        return guild;
    }


}
