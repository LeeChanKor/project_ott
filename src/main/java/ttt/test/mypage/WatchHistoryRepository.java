package ttt.test.mypage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {

    List<WatchHistory> findByUsernameOrderByWatchedAtDesc(String username);

    List<WatchHistory> findTop6ByUser_UsernameOrderByWatchedAtDesc(String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM WatchHistory w WHERE w.username = :username AND w.drama.dramaNo = :dramaNo")
    void deleteDuplicateDramaWatchHistory(String username, int dramaNo);

    @Modifying
    @Transactional
    @Query("DELETE FROM WatchHistory w WHERE w.username = :username AND w.movie.movieNo = :movieNo")
    void deleteDuplicateMovieWatchHistory(String username, int movieNo);

    @Modifying
    @Transactional
    @Query("DELETE FROM WatchHistory w WHERE w.username = :username AND w.variety.varietyNo = :varietyNo")
    void deleteDuplicateVarietyWatchHistory(String username, int varietyNo);


    // 시청기록 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM WatchHistory w WHERE w.id IN :ids")
    void deleteAllByIds(List<Long> ids);

    @Modifying
    @Transactional
    @Query("DELETE FROM WatchHistory w WHERE w.username = :username")
    void deleteAllByUserName(String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM WatchHistory w WHERE w.drama.dramaNo = :dramaNo")
    void deleteAllByDramaNo(@Param("dramaNo") int dramaNo);


    @Modifying
    @Transactional
    @Query("DELETE FROM WatchHistory w WHERE w.movie.movieNo = :movieNo")
    void deleteAllByMovieNo(@Param("movieNo") int movieNo);

    @Modifying
    @Transactional
    @Query("DELETE FROM WatchHistory w WHERE w.variety.varietyNo = :varietyNo")
    void deleteAllByVarietyNo(@Param("varietyNo") int varietyNo);


}
