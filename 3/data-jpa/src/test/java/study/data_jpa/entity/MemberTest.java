package study.data_jpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {
    @PersistenceContext
    EntityManager em;

    @DisplayName("entity - 객체")
    @Test
    public void testEntity() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member memberA1 = new Member("memberA1", 10, teamA);
        Member memberA2 = new Member("memberA2", 15, teamA);
        Member memberB1 = new Member("memberB1", 20, teamB);
        Member memberB2 = new Member("memberB2", 25, teamB);

        em.persist(memberA1);
        em.persist(memberA2);
        em.persist(memberB1);
        em.persist(memberB2);

        //when
        /* 강제로 저장 */
        em.flush();
        /* 영속성을 비운다. */
        em.clear();

        /* 확인 */
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        //then
        for (Member member : members) {
            System.out.println("memger = "+ member);
            System.out.println("=> memger.team = "+ member.getTeam());
        }
    }
}