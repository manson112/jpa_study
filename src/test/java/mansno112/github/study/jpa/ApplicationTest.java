package mansno112.github.study.jpa;

import mansno112.github.study.jpa.start.Member;
import org.junit.jupiter.api.Test;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class ApplicationTest {

    @Test
    public void test01() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpatest");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            logic(em);
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

        em.persist(member);

        member.setAge(20);

        Member findMember = em.find(Member.class, id);
        System.out.println("fineMember=" + findMember.getUsername() + ", age=" + findMember.getAge());

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        System.out.println("members.size=" + members.size());

        em.remove(member);
    }
}
