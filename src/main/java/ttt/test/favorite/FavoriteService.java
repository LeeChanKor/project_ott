package ttt.test.favorite;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttt.test.drama.Drama;
import ttt.test.drama.DramaRepository;
import ttt.test.movie.Movie;
import ttt.test.movie.MovieRepository;
import ttt.test.user.SiteUser;
import ttt.test.user.UserRepository;
import ttt.test.variety.Variety;
import ttt.test.variety.VarietyRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final DramaRepository dramaRepository;
    private final MovieRepository movieRepository;
    private final VarietyRepository varietyRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           DramaRepository dramaRepository,
                           MovieRepository movieRepository,
                           VarietyRepository varietyRepository,
                           UserRepository userRepository
    ) {
        this.favoriteRepository = favoriteRepository;
        this.dramaRepository = dramaRepository;
        this.movieRepository = movieRepository;
        this.varietyRepository = varietyRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public boolean toggleFavorite(SiteUser user, Drama drama, Movie movie, Variety variety) {
        Optional<Favorite> favorite = Optional.empty();

        if (drama != null) {
            favorite = favoriteRepository.findByUsernameAndDramaNo(user, drama);
        } else if (movie != null) {
            favorite = favoriteRepository.findByUsernameAndMovieNo(user, movie);
        } else if (variety != null) {
            favorite = favoriteRepository.findByUsernameAndVarietyNo(user, variety);
        }

        if (favorite.isPresent()) {
            // 찜 상태가 이미 있으면 삭제
            favoriteRepository.delete(favorite.get());
            return false; // 찜 해제됨
        } else {
            // 찜 상태가 없으면 추가
            Favorite newFavorite = new Favorite();
            newFavorite.setUsername(user);
            newFavorite.setDramaNo(drama);
            newFavorite.setMovieNo(movie);
            newFavorite.setVarietyNo(variety);
            newFavorite.setFavoriteAt(LocalDateTime.now().withNano(0)); // 찜 등록 시간을 현재 시간으로 설정
            favoriteRepository.save(newFavorite);
            return true; // 찜 추가됨
        }
    }

    public boolean isHearted(SiteUser user, Drama drama, Movie movie, Variety variety) {
        Optional<Favorite> favorite = Optional.empty();

        if (drama != null) {
            favorite = favoriteRepository.findByUsernameAndDramaNo(user, drama);
        } else if (movie != null) {
            favorite = favoriteRepository.findByUsernameAndMovieNo(user, movie);
        } else if (variety != null) {
            favorite = favoriteRepository.findByUsernameAndVarietyNo(user, variety);
        }

        return favorite.isPresent();
    }

    public List<Favorite> getFavoriteByUsername(String username, Drama dramaNo, Variety varietyNo) {
        return favoriteRepository.findByUsername_UsernameAndDramaNoOrVarietyNoOrderByFavoriteAtDesc(username, dramaNo, varietyNo);
    }


    public List<Favorite> getFavoriteByUsername(String username, Movie movieNo) {
        if (movieNo == null) {
            return favoriteRepository.findByUsername_UsernameOrderByFavoriteAtDesc(username);
        }
        return favoriteRepository.findByUsername_UsernameAndMovieNoOrderByFavoriteAtDesc(username, movieNo);
    }


    public List<Favorite> getLatestFavoriteMovies(String username) {
        // username에 해당하는 관심 영화 목록을 최신순으로 6개 가져옴
        return favoriteRepository.findTop6ByUsername_UsernameAndMovieNoIsNotNullOrderByFavoriteAtDesc(username);
    }

    public List<Favorite> getLatestFavoritePrograms(String username) {
        // username에 해당하는 관심 프로그램 목록을 6개 가져옴
        return favoriteRepository.findTop6ByUsername_UsernameAndDramaNoIsNotNullOrVarietyNoIsNotNullOrderByFavoriteAtDesc(username);
    }

    @Transactional
    public void deleteFavoriteMoviesByIds(List<Integer> movieNos) {
        favoriteRepository.deleteAllByMovieNoIn(movieNos);
    }

    @Transactional
    public void deleteFavoriteDramasByIds(List<Integer> dramaNos) {
        favoriteRepository.deleteAllByDramaNoIn(dramaNos);
    }

    @Transactional
    public void deleteFavoriteVarietiesByIds(List<Integer> varietyNos) {
        favoriteRepository.deleteAllByVarietyNoIn(varietyNos);
    }




}
