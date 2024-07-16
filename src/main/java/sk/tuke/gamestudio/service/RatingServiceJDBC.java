package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import java.sql.*;

public class RatingServiceJDBC implements RatingService{
    public static final String URL = "jdbc:postgresql://localhost/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "kexik";
    public static final String DELETE = "DELETE FROM rating";
    public static final String CHECK_EXISTS = "select exists(select 1 from rating where player=?)";

    public static final String UPDATE = "UPDATE rating SET rating = ?, ratedOn = ? WHERE player = ?";

    public static final String INSERT = "INSERT INTO rating (game, player, rating, ratedOn) VALUES (?, ?, ?, ?)";

    public static final String GET_RATING = "SELECT rating FROM rating WHERE player = ? AND game = ?";

    public static final String GET_AVG_RATING = "SELECT AVG(rating) FROM rating WHERE game = ?";

    @Override
    public void setRating(Rating rating) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(CHECK_EXISTS)
        ) {
            statement.setString(1, rating.getPlayer());
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                if(rs.getBoolean(1) == false){
                    PreparedStatement statement1 = connection.prepareStatement(INSERT);
                    statement1.setString(1, rating.getGame());
                    statement1.setString(2, rating.getPlayer());
                    statement1.setInt(3, rating.getRating());
                    statement1.setTimestamp(4, new Timestamp(rating.getRatedon().getTime()));
                    statement1.executeUpdate();
                }else{
                    PreparedStatement statement2 = connection.prepareStatement(UPDATE);
                    statement2.setInt(1, rating.getRating());
                    statement2.setTimestamp(2, new Timestamp(rating.getRatedon().getTime()));
                    statement2.setString(3, rating.getPlayer());
                    statement2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Problem inserting rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(GET_AVG_RATING)
        ) {
            statement.setString(1,game);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Problem getting average rating", e);
        }
        return 0;
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(GET_RATING)
        ) {
            statement.setString(1, player);
            statement.setString(2,game);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Problem getting rating", e);
        }
        return 0;
    }

    @Override
    public void reset() throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new RatingException("Problem deleting rating", e);
        }
    }
}
