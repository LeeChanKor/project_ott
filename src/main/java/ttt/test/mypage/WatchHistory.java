package ttt.test.mypage;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ttt.test.drama.Drama;
import ttt.test.movie.Movie;
import ttt.test.user.SiteUser;
import ttt.test.variety.Variety;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "watch_history")
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_name", referencedColumnName = "username", insertable = false, updatable = false)
    private SiteUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drama_no")
    private Drama drama;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_no")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variety_no")
    private Variety variety;

    @Column(name = "watched_at")
    private LocalDateTime watchedAt; // 시청 일자 추가
}
