package sk.tuke.gamestudio.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sk.tuke.gamestudio.core.*;

@Controller
@RequestMapping("/maze")
public class MazeController {
    private Field field;

    @GetMapping("/new")
    public String newGame(@RequestParam int row, @RequestParam int column) {
        field = new Field(row, column);
        return "maze";
    }
    @GetMapping("/move")
    public String movePlayers(@RequestParam String direction){
        switch (direction){
            case "up":
                field.getPlayer().move(Direction.UP);
                break;
            case "down":
                field.getPlayer().move(Direction.DOWN);
                break;
            case "left":
                field.getPlayer().move(Direction.LEFT);
                break;
            case "right":
                field.getPlayer().move(Direction.RIGHT);
                break;
            default:
                break;
        }
        return "maze";
    }


    public String getHtmlField(){
        StringBuilder sb = new StringBuilder();
        sb.append("<table cellpadding='0' cellspacing='0'>");
        for(int row = 0; row < field.getRowCount(); row++){
            sb.append("<tr>");
            for(int column = 0; column < field.getColumnCount(); column++){
                sb.append("<td>");
                if(field.getTile(row, column).getState() == TileState.PLAYER){
                    sb.append("<div class='player'>");
                }else if(field.getTile(row, column).getState() == TileState.FINISH){
                    sb.append("<div class='finish'>");
                }else if(field.getTile(row, column) instanceof Road){
                    sb.append("  ");
                }else if(field.getTile(row, column) instanceof Wall){
                    sb.append("<div class='wall'>");
                    //sb.append("<img src='/images/maze/stena100x100.png'>");
                }
                sb.append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>\n");
        return sb.toString();
    }
    public String getFieldState(){
        StringBuilder sb = new StringBuilder();
        sb.append(field.getFieldState());
        return sb.toString();
    }
    public String getGameTime(){
        StringBuilder sb = new StringBuilder();
        if(field.getFieldState() == FieldState.PLAYING){
            sb.append(field.getTimer().getMinutes());
            sb.append(":");
            sb.append(field.getTimer().getSeconds()%60);
        }else if(field.getFieldState() == FieldState.FINISHED){
            sb.append(field.getTimer().getEndTimeMinutes());
            sb.append(":");
            sb.append(field.getTimer().getEndTimeSeconds()%60);
        }
        return sb.toString();
    }
}
