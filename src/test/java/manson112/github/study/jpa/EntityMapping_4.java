package manson112.github.study.jpa;


public class EntityMapping_4 {

    public void entity() {
        /*
            @Entity

            JPA 를 사용해서 테이블과 매핑할 클래스에 붙이는 Annotation
            @Entity 가 붙은 클래스는 JPA 가 관리한다.

            * 주의사항
            1. 기본 생성자는 필수이다. (파라미터가 없는 public, protected 생성자)
                - JPA 가 엔티티 객체를 생성할 때 기본 생성자를 이용한다.
            2. final 클래스(enum, interface, inner) 클래스에는 사용할 수 없다.
            3. 저장할 필드에 final 을 사용하면 안 된다.

         */
    }

    public void table() {
        /*
            @Table

            엔티티와 매핑할 테이블을 지정한다.
            생략하면 매핑한 엔티티 이름을 테이블 이름으로 사용한다.
            - name: 매핑할 테이블 이름
            - catalog: catalog 기능이 있는 데이터베이스에서 catalog 를 매핑한다.
            - schema: schema 기능이 있는 데이터베이스에서 schema 를 매핑한다.
            - uniqueConstraints: DDL 생성 시에 유니크 제약 조건을 만든다. 2개 이상의 복합 유니크 제약조건도 만들 수 있다.
         */

    }
}
