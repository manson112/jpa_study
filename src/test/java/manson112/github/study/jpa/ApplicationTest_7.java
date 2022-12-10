package manson112.github.study.jpa;

import manson112.github.study.jpa.shop.model.entity.Member;
import manson112.github.study.jpa.shop.model.entity.Order;
import manson112.github.study.jpa.shop.model.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class ApplicationTest_7 {

    @Test
    void test01() {
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
        Member member = new Member();
        member.setName("NAME");
        member.setCity("BUSAN");
        em.persist(member);

        Order order = new Order();
        order.setMember(member);
        order.setStatus(OrderStatus.ORDER);
        em.persist(order);

    }
}