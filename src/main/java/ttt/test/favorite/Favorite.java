package ttt.test.favorite;

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
@Table(name = "favorite")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int favoriteNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private SiteUser username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dramaNo")
    private Drama dramaNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movieNo")
    private Movie movieNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "varietyNo")
    private Variety varietyNo;

    @Column(name = "favorite_at")
    private LocalDateTime favoriteAt; // 시청 일자 추가

}
