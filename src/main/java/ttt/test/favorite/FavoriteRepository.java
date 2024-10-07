package ttt.test.favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ttt.test.drama.Drama;
import ttt.test.movie.Movie;
import ttt.test.user.SiteUser;
import ttt.test.variety.Variety;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    Optional<Favorite> findByUsernameAndDramaNo(SiteUser username, Drama dramaNo);
    Optional<Favorite> findByUsernameAndMovieNo(SiteUser username, Movie movieNo);
    Optional<Favorite> findByUsernameAndVarietyNo(SiteUser username, Variety varietyNo);

    List<Favorite> findByUsername_UsernameAndDramaNoOrVarietyNoOrderByFavoriteAtDesc(String username, Drama dramaNo, Variety varietyNo);
    List<Favorite> findByUsername_UsernameAndMovieNoOrderByFavoriteAtDesc(String username, Movie movieNo);
    List<Favorite> findByUsername_UsernameOrderByFavoriteAtDesc(String username);

    // 관심 영화 중 최신순으로 6개 가져오는 메서드
    List<Favorite> findTop6ByUsername_UsernameAndMovieNoIsNotNullOrderByFavoriteAtDesc(String username);
    List<Favorite> findTop6ByUsername_UsernameAndDramaNoIsNotNullOrVarietyNoIsNotNullOrderByFavoriteAtDesc(String username);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM favorite WHERE movie_no IN (:movieNos)", nativeQuery = true)
    void deleteAllByMovieNoIn(@Param("movieNos") List<Integer> movieNos);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM favorite WHERE drama_no IN (:dramaNos)", nativeQuery = true)
    void deleteAllByDramaNoIn(@Param("dramaNos") List<Integer> dramaNos);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM favorite WHERE variety_no IN (:varietyNos)", nativeQuery = true)
    void deleteAllByVarietyNoIn(@Param("varietyNos") List<Integer> varietyNos);

    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.username.username = :username")
    void deleteByUsername(@Param("username") String username);

    // 추가: 드라마 번호로 즐겨찾기 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.dramaNo.dramaNo = :dramaNo")
    void deleteAllByDramaNo(@Param("dramaNo") int dramaNo);

    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.movieNo.movieNo = :movieNo")
    void deleteAllByMovieNo(@Param("movieNo") int movieNo);

    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.varietyNo.varietyNo = :varietyNo")
    void deleteAllByVarietyNo(@Param("varietyNo") int varietyNo);

}
