package ttt.test.movie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ttt.test.content.Content;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int movieNo;

    @Column(name = "movie_name", nullable = false, length = 255)
    private String movieName;

    @Column(name = "movie_text", length = 1000)
    private String movieText;

    @Column(name = "movie_genre", length = 100)
    private String movieGenre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_no", referencedColumnName = "content_no")
    private Content content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "register_date", nullable = false, updatable = false)
    private Date registerDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "movie_img", length = 255)
    private String movieImg;

    @Column(name = "video_file_name", length = 255)
    private String videoFileName;

    public Movie() {
    }

    public Movie(String movieName, String movieText, String movieGenre, Content content, String movieImg, String videoFileName) {
        this.movieName = movieName;
        this.movieText = movieText;
        this.movieGenre = movieGenre;
        this.content = content;
        this.movieImg = movieImg;
        this.videoFileName = videoFileName;
    }

    @PrePersist
    protected void onCreate() {
        this.registerDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = new Date();
    }

}