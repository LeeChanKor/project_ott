package ttt.test.drama;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ttt.test.content.Content;
import ttt.test.mypage.WatchHistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "drama")
public class Drama {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dramaNo;

    @Column(name = "drama_name", nullable = false, length = 255)
    private String dramaName;

    @Column(name = "drama_text", length = 1000)
    private String dramaText;

    @Column(name = "drama_genre", length = 100)
    private String dramaGenre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drama_content_no", referencedColumnName = "content_no")
    private Content content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "register_date", nullable = false, updatable = false)
    private Date registerDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "drama_img", length = 255)
    private String dramaImg;

    @Column(name = "video_file_name", length = 255)
    private String videoFileName;

    // watchHistory와의 연관관계 설정
    @OneToMany(mappedBy = "drama", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<WatchHistory> watchHistories = new ArrayList<>();

    // 기본 생성자
    public Drama() {
    }

    // 필드를 포함한 생성자
    public Drama(String dramaName, String dramaText, String dramaGenre, Content content, String dramaImg, String videoFileName) {
        this.dramaName = dramaName;
        this.dramaText = dramaText;
        this.dramaGenre = dramaGenre;
        this.content = content;
        this.dramaImg = dramaImg;
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
