package study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback(false)
class JpaBaseEntityTest {

    @Autowired
    MemberRepository memberRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void jpaBaseEntityTest1() throws Exception {
        //given
        Member member = new Member("member1");
        memberRepository.save(member); //@PrePersist 발생
        Thread.sleep(100);
        member.setUsername("member2");
        em.flush();
        em.clear();
        //when
        Member findMember = memberRepository.findById(member.getId()).get();
        System.out.println("findMember.getCreatedDate() = " + findMember.getCreatedDate());
        //then

    }
}