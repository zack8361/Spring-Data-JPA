package study.datajpa.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {
    private final EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public void delete(Member member){
        em.remove(member);
    }

    public Optional<Member> findById(Long id){
        Member member = em.find(Member.class, id);
        return ofNullable(member);
    }

    public long count(){
        return em.createQuery("Select count(m) from Member m",Long.class)
                .getSingleResult();
    }
    public Member find(Long memberId){
        return em.find(Member.class,memberId);
    }

    public List<Member> findAll(){
        return em.createQuery("SELECT m From Member m", Member.class)
                .getResultList();
    }
    public List<Member> findByPage(int age, int offset, int limit){
        return em.createQuery("select m from Member m where m.age =:age order by m.age desc",Member.class)
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
    public long totalCount(int age){
        return em.createQuery("select count(m) from Member m where m.age =:age", Long.class)
                .getSingleResult();
    }
    
    public int bulkAgePlus(int age){
        return em.createQuery("UPDATE Member m set m.age = m.age + 1 " +
                        "where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }
}
