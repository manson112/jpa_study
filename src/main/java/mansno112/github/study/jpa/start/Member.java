package mansno112.github.study.jpa.start;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "MEMBER", uniqueConstraints = { @UniqueConstraint(
        name = "NAME_AGE_UNIQUE",
        columnNames = {"NAME", "AGE"}
)})
public class Member {

    // 영속성 컨텍스트는 엔티티를 식별자 값(@Id)으로 구분하므로 영속상태는 식별자 값이 반드시 있어야 한다.
    @Id
    @Column(name = "ID")
    private String id;

    /*
        기본 키 매핑
        - 직접할당: 기본 키를 애플리케이션에서 직접 할당한다.
            @Id 만 유지
            em.persist() 전에 기본키를 직접 할당해야한다.
        - 자동생성: 대리 키 사용 방식
            1. IDENTITY: 기본 키 생성을 데이터베이스에 위임한다.
            2. SEQUENCE: 데이터베이스 시퀀스를 사용해서 기본 키를 할당한다.
            3. TABLE: 키 생성 테이블을 사용한다.
            @GeneratedValue(strategy = GenerationType.IDENTITY) 를 사용한다.
            * hibernate.id.new_generator_mappings = true

     */

    // precision, scale: BigDecimal, BigInteger 타입에서 사용한다.
    // precision - 소수점을 포함한 전체 자릿수
    // scale - 소수점 자릿수

    // columnDefinition = "varchar(100) default 'EMPTY'" DDL 생성 시
    @Column(name = "NAME", nullable = false, length = 10)
    private String username;

    private Integer age;

    // 자바 enum 타입을 매핑할 때 사용
    // EnumType.ORDINAL: enum 순서를 데이터베이스에 저장 => 이미 저장된 enum의 순서를 변경할 수 없다.
    // EnumType.STRING: enum 이름을 데이터베이스에 저장
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    // 자바의 날짜 타입은 Temporal 을 이용한다.
    // 생략하면 자바의 date와 가장 유사한 timestamp로 정의된다.
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    // 매핑하는 필드 타입이 문자면 CLOB으로, 나머지는 BLOB로 매핑한다.
    @Lob // 회원을 설명하는 필드는 길이 제한이 없기 때문데 CLOB, BLOB 타입을 매핑할 수 있도록 한다.
    private String description;


    /*
    @Transient: 특정 필드를 데이터베이스에 매핑하지 않는다. 저장, 조회 둘다 안한다.
    @Access: JPA가 엔티티에 접근하는 방식을 지정한다.
     - AccessType.FIELD: 필드에 직접 접근한다. private 이어도 접근 가능하다.
     - AccessType.PROPERTY: Getter로 필드에 접근한다.
     * 지정하지 않으면 @Id의 위치를 기준으로 접근 방식이 설정된다. (필드 , Getter)
     */
}
