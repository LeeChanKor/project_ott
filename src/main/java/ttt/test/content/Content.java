package ttt.test.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ttt.test.drama.Drama;
import ttt.test.movie.Movie;
import ttt.test.variety.Variety;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_no")
    private int contentNo;

    @Column(name = "content_name", nullable = false, length = 50)
    private String contentName;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Drama> dramas = new ArrayList<>();

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movie> movies = new ArrayList<>();

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Variety> varieties = new ArrayList<>();

    public Content() {}

    public Content(String contentName) {
        this.contentName = contentName;
    }
}
