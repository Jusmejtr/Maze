package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.service.RatingException;
import sk.tuke.gamestudio.service.RatingService;

@RestController
@RequestMapping("/api/rating")
public class RatingServiceRest {

    @Autowired
    private RatingService ratingService;

    @GetMapping("/{game}")
    public int getAverageRating(@PathVariable String game){
        return ratingService.getAverageRating(game);
    }

    @GetMapping("/{game}/{player}")
    public int getRating(@PathVariable String game, @PathVariable String player){
        return ratingService.getRating(game, player);
    }

    @PostMapping
    public void setRating(@RequestBody Rating rating){
        if(rating == null) throw new RatingException("Bad input");
        if(rating.getPlayer() == null  || rating.getGame() == null || rating.getRatedon() == null){
            throw new RatingException("Bad input");
        }else if(rating.getRating() < 1 || rating.getRating() > 5){
            throw new RatingException("Invalid rating");
        }
        ratingService.setRating(rating);
    }
}
