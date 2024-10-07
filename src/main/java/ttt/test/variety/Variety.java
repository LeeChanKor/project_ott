package ttt.test.variety;

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
@Table(name = "variety")
public class Variety {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int varietyNo;

    @Column(name = "variety_name", nullable = false, length = 255)
    private String varietyName;

    @Column(name = "variety_text", length = 1000)
    private String varietyText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_no", referencedColumnName = "content_no")
    private Content content;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "register_date", nullable = false, updatable = false)
    private Date registerDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "variety_img", length = 255)
    private String varietyImg;

    @Column(name = "video_file_name", length = 255)
    private String videoFileName;

    public Variety() {
    }

    public Variety(String varietyName, String varietyText, Content content, String varietyImg, String videoFileName) {
        this.varietyName = varietyName;
        this.varietyText = varietyText;
        this.content = content;
        this.varietyImg = varietyImg;
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


