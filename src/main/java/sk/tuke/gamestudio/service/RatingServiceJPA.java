package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class RatingServiceJPA implements RatingService{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        entityManager.merge(rating);
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try{
            double avg = entityManager.createNamedQuery("Rating.getAverageRating", Double.class)
                    .setParameter("game", game)
                    .getSingleResult();
            return (int) avg;
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try{
            return entityManager.createNamedQuery("Rating.getRating", Integer.class)
                    .setParameter("game", game)
                    .setParameter("player", player)
                    .getSingleResult();
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public void reset() throws RatingException {
        entityManager.createNamedQuery("Rating.resetRatings").executeUpdate();
    }
}
