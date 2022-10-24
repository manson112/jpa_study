package mansno112.github.study.jpa.start;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "MEMBER")
public class Member {

    // 영속성 컨텍스트는 엔티티를 식별자 값(@Id)으로 구분하므로 영속상태는 식별자 값이 반드시 있어야 한다.
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "NAME")
    private String username;

    private Integer age;
}
