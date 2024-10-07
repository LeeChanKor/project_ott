package ttt.test.mypage;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchHistoryService {

    private final WatchHistoryRepository watchHistoryRepository;

    public WatchHistoryService(WatchHistoryRepository watchHistoryRepository) {
        this.watchHistoryRepository = watchHistoryRepository;
    }

    // 드라마 시청 기록 저장
    public void saveWatchHistory(WatchHistory watchHistory) {
        watchHistoryRepository.save(watchHistory);
    }

    // 사용자 시청 기록 조회
    public List<WatchHistory> getWatchHistoryByUsername(String username) {
        return watchHistoryRepository.findByUsernameOrderByWatchedAtDesc(username);
    }

    // 최신 시청 기록 6개 가져오는 메서드
    public List<WatchHistory> getLatestWatchHistory(String username) {
        return watchHistoryRepository.findTop6ByUser_UsernameOrderByWatchedAtDesc(username);
    }

    // 중복 드라마 시청 기록 삭제
    @Transactional
    public void deleteDuplicateDramaWatchHistory(String username, int dramaId) {
        watchHistoryRepository.deleteDuplicateDramaWatchHistory(username, dramaId);
    }

    // 중복 영화 시청 기록 삭제
    @Transactional
    public void deleteDuplicateMovieWatchHistory(String username, int movieId) {
        watchHistoryRepository.deleteDuplicateMovieWatchHistory(username, movieId);
    }

    // 중복 예능 시청 기록 삭제
    @Transactional
    public void deleteDuplicateVarietyWatchHistory(String username, int varietyId) {
        watchHistoryRepository.deleteDuplicateVarietyWatchHistory(username, varietyId);
    }

    // 시청 기록 삭제
    @Transactional
    public void deleteWatchHistoryByIds(List<Long> ids) {
        watchHistoryRepository.deleteAllByIds(ids);
    }
}
