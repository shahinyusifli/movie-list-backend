package dev.shahin.movies.Services;

import com.mongodb.client.result.UpdateResult;
import dev.shahin.movies.Entities.Movie;
import dev.shahin.movies.Entities.MovieCreateDTO;
import dev.shahin.movies.Entities.MovieGetDTO;
import dev.shahin.movies.Entities.Review;
import dev.shahin.movies.Repositories.MovieRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<MovieGetDTO> getAllMovies() {
        Query query =new Query();
        query.addCriteria(Criteria.where("flag").is(false));
        query.fields()
                .include("title")
                .include("releaseDate")
                .include("genres")
                .include("poster");
        return mongoTemplate.find(query, MovieGetDTO.class);
    }

    public Optional<Movie> getSingleMovie(ObjectId id) { return movieRepository.findById(id); }

    public Long deleteMovie(ObjectId id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        Update updateDefinition = new Update().set("flag", true);

        UpdateResult updateResult = mongoTemplate.updateFirst(query, updateDefinition, Movie.class);
        return updateResult.getMatchedCount();
    }

    public Movie createMovie(MovieCreateDTO payload) {
        Movie movie = new Movie();
        List<String> emptyGenres = new ArrayList<>();
        List<Review> emptyReviews = new ArrayList<>();
        Stream<String> demo = payload.getGenres().stream();
        movie.setImdbId(Optional.ofNullable(payload.getImdbId()).orElse("tt0000"));
        movie.setTitle(payload.getTitle());
        movie.setReleaseDate(payload.getReleaseDate());
        movie.setTrailerLink(payload.getTrailerLink());
        movie.setPoster(payload.getPoster());
        movie.setGenres(payload.getGenres());
        movie.setBackdrops(emptyGenres);
        movie.setReviewIds(emptyReviews);
        movie.setFlag(false); // Set flag to false

        Movie savedMovie = movieRepository.insert(movie);
        return savedMovie;
    }
}
