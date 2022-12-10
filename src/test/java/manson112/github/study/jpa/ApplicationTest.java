package manson112.github.study.jpa;

import manson112.github.study.jpa.start.Member;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class ApplicationTest {

    @Test
    public void test01() {

        // 데이터베이스를 하나만 사용하는 애플리케이션은 일반적으로 EntityManagerFactory 를 하나만 생성한다.
        // 비용이 많이든다. 따라서 한 개만 만들어서 애플리케이션 전체에서 공유하도록 설계되어있다.
        // 여러 Thread 가 동시에 접근해도 안전하므로 다른 Thread 간에 공유해도 된다.
        // Persistence.xml 에 있는 정보를 바탕으로 EntityManagerFactory 를 생성한다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpatest");

        // EntityManager 생성, 비용이 거의 들지 않는다.
        // 여러 Thread 가 도잇에 접근하면 동시성 문제가 발생하므로 Thread 간 절대 공유하면 안된다.
        EntityManager em = emf.createEntityManager();

        // EntityManager 는 데이터베이스 연결이 필요한 시점까지 커넥션을 얻지 않는다.
        // 보통 Transaction 을 시작할 때 커넥션을 획득한다.
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            logic(em);

            // jpa 는 보통 트랜잭션을 커밋하는 순간 영속성 컨텍스트에 새로 저장된 엔티티를 데이터베이스에 반영하는데 이를 flush 라고 한다.
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    private void logic(EntityManager em) {
        String id = "id1";
        Member member = new Member();
        member.setId(id);
        member.setUsername("지한");
        member.setAge(2);



        // 영속성 컨텍스트 (persistence context): 엔티티를 영구 저장하는 환경
        // EntityManager 로 엔티티를 저장하거나 조회하면 EntityManager 는 영속성 컨텍스트에 엔티티를 보관하고 관리한다.

        // persist 메서드는 EntityManager 를 사용해서 회원 엔티티를 영속성 컨텍스트에 저장한다.
        // 여러 EntityManager 가 하나의 영속성 컨텍스트에 접근할 수도 있다. 지금은 EntityManager 하나에 하나의 영속성 컨텍스트가 만들어진다고 생각하자.
        em.persist(member);

        member.setAge(20);

        Member findMember = em.find(Member.class, id);
        System.out.println("fineMember=" + findMember.getUsername() + ", age=" + findMember.getAge());

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        System.out.println("members.size=" + members.size());

        em.remove(member);
    }


    /*
    영속성 컨텍스트가 엔티티를 관리할 떄의 장점
    1. 1차 캐시
        - 영속성 컨텍스트는 내부에 캐시를 가지고 있는데 이를 1차 캐시라고 한다.
        - 영속 상태의 엔티티는 모두 이 곳에 저장된다.
        - 1차 캐시의 키는 식별자 값(@Id)이다. 그리고 식별자 값은 데이터베이스 기본 키와 매핑되어 있다.
        - em.find() 를 호출하면 먼저 1차 캐시에서 엔티티를 찾고 만약 찾는 엔티티가 1차 캐시에 없으면 데이터베이스에서 조회하여 엔티티를 생성하고 1차 캐시에 저장한 후 영속상태의 엔티티를 반환한다.
    2. 동일성 보장 (identity)
        - 여러번 같은 키값으로 조회를 하더라도 1차 캐시에 있는 엔티티를 반환하므로 두 조회 결과의 동일성이 보장된다.
    3. 트랜잭션을 지원하는 쓰기 지연 (Transactional write-behind)
        - EntityManager 는 트랜잭션을 커밋하기 직전까지 데이터베이스에 엔티티를 저장하지 않고 내부 쿼리 저장소에 Insert sql 을 차곡차곡 모아뒀다가
          커밋할 때 데이터베이스에 보내는데 이를 트랜잭션을 지원하는 쓰기 지연이라고 한다.
    4. 변경 감지 (dirty checking)
        - Jpa 로 엔티티를 수정할 때는 단순히 엔티티를 조회해서 데이터만 변경하면 된다.
        - Jpa 는 엔티티를 영속성 컨텍스트에 보관할 때, 최초 상태를 복사해서 저장해두는데 이것을 스냅샷이라고 한다.
          그리고 플러시 시점에 스냅샷과 엔티티를 비교해서 변경된 엔티티를 찾는다.
        - 변경 감지는 영속 상태의 엔티티에만 적용된다. 비영속, 준영속처럼 영속성 컨텍스트의 관리를 받지 못하는 엔티티는 값을 변경해도 데이터베이스에 반영되지 않는다.
        - Jpa 는 기본적으로 엔티티의 모든 필드를 업데이트 하는데 이는 데이터베이스에 보내는 데이터 전송량이 증가하는 단점이 있지만, 다음과 같은 장점이 있다.
          - 수정 쿼리가 항상 같다. 따라서 애플리케이션 로딩 시점에 수정 쿼리를 미리 생성해두고 재사용할 수 있다.
          - 데이터베이스에 동일한 쿼리를 보내면 데이터베이스는 이전에 한 번 파싱된 쿼리를 재사용할 수 있다.
        - 필드가 너무 많거나(대략 30개 이상) 저장되는 내용이 너무 크면 수정된 데이터만 사용해서 동적으로 Update sql 을 생성하는 전략을 사용하면 된다. (하이버네이트 확장기능 사용, @org.hibernate.annotations.DynamicUpdate)
        - @DynamicInsert 도 존재
    5. 지연 로딩

     */

    /*
    - flush()
      1. 변경 감지가 동작해서 영속성 컨텍스트에 있는 모든 엔티티를 스냅샷과 비교해서 수정된 엔티티를 찾는다.
         수정된 엔티티는 수정 쿼리를 만들어 쓰기 지연 SQL 저장소에 등록한다.
      2. 쓰기지연 SQL 저장소의 쿼리를 데이터베이스에 전송한다(등록, 수정, 삭제 쿼리)

      - 영속성 컨텍스트를 flush 하는 방법
        1. em.flush() 를 직접 호출
            - 메소드를 직접 호출해서 영속성 컨텍스트를 강제로 플러시한다.
            - 테스트나 다른 프레임워크와 JPA 를 함께 사용할 때를 제외하고 거의 사용하지 않는다.
        2. 트랜잭션 커밋 시 플러시가 자동 호출된다.
            - 트랜색션을 커밋하기 전에 꼭 플러시를 호출해서 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영해야한다.
            - JPA 는 이런 문제를 예방하기 위해 트랜잭션을 커밋할 때 플러시를 자동으로 호출한다.
        3. JPQL 쿼리 실행 시 플러시가 자동 호출된다.
            - JPQL 이나 Criteria 같은 객체지향 쿼리를 호출할 때도 플러시가 실행된다.

       플러시를 한다고 해서 영속성 컨텍스트에 보관된 엔티티를 지우는 것이 아니라 변경 내용을 데이터베이스에 동기화한다.
     */


    public void detachTest() {
        /*
        영속성 컨텍스트가 관리하는 영속 상태의 엔티티가 영속성 컨텍스트에서 분리된 것을 준영속 상태라고 한다.
        따라서 준영속 상태의 엔티티는 영속성 컨텍스트가 제공하는 기능을 사용할 수 없다.

        1. em.detach(entity)
            - 특정 엔티티만 준영속 상태로 전환한다.
        2. em.clear()
            - 영속성 컨텍스트를 완전히 초기화한다.
            - 해당 영속성 컨텍스트의 모든 엔티티를 준영속 상태로 만든다.
        3. em.close()
            - 영속성 컨텍스트를 종료한다.

         - 준영속 상태의 특징
            - 거의 비영속 상태에 가깝다 = 영속성 컨텍스트가 제공하는 어떤 기능도 동작하지 않는다.
            - 식별자 값을 가지고 있다 = 비영속 상태에서는 식별자 값이 없을 수 있지만 준영속 상태에서는 반드시 식별자 값을 가지고 있다.
            - 지연 로딩을 할 수 없다
         */

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpatest");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Member member = new Member();
            member.setId("memberA");
            member.setUsername("회원A");

            // 영속 상태
            em.persist(member);

            // 준영속 상태
            // 1차 캐시부터 쓰기 지연 SQL 저장소까지 모든 정보 제거
            em.detach(member);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    public void merge() {
        /*
        준영속 상태의 엔티티를 다시 영속 상태로 변경하려면 병합을 사용하면 된다.
        merge() 메소드는 준영속 상태의 엔티티를 받아서 그 정보로 새로운 영속 상태의 엔티티를 반환한다.

        비영속 상태의 엔티티도 영속 상태로 만들 수 있다.

         */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpatest");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        Member member = new Member();
        member.setId("memberA");
        member.setUsername("회원A");
        // 영속 상태
        em.persist(member);

        // 준영속 상태
        em.detach(member);

        member.setUsername("회원1");

        Member merged = em.merge(member);

        em.contains(member); // false
        em.contains(merged); // true

        tx.commit();

        em.close();



    }
}
