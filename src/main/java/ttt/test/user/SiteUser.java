package ttt.test.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @Column
    private String membership;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "register_date", nullable = false, updatable = false)
    private Date registerDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;  // 권한 저장을 위한 필드

    // 결제 관련 필드 추가
    @Column(precision = 10, scale = 2)  // 금액 처리를 위한 설정
    private BigDecimal paymentAmount;  // 결제 금액

    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;  // 결제 일자

}
