package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.ScoreException;
import sk.tuke.gamestudio.service.ScoreService;

import java.util.List;

@RestController
@RequestMapping("/api/score")
public class ScoreServiceRest {
    @Autowired
    private ScoreService scoreService;

    @GetMapping("/{game}")
    public List<Score> getTopScores(@PathVariable String game) {
        return scoreService.getTopScores(game);
    }

    @PostMapping
    public void addScore(@RequestBody Score score) {
        if(score == null) throw new ScoreException("Bad input");
        if(score.getPlayedon() == null || score.getPlayer() == null || score.getGame() == null){
            throw new ScoreException("Bad input");
        }else if(score.getPoints() < 0){
            throw new ScoreException("Negative points");
        }
        scoreService.addScore(score);
    }

}
