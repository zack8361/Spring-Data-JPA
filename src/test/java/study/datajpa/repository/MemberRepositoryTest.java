package study.datajpa.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.awt.print.Pageable;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @PersistenceContext
    private EntityManager em;
    @Test
    void basicCRUD() throws Exception {
        Team team = new Team("연구 4팀");
        teamRepository.save(team);
        Member member = new Member("이찬호",27,team);
        memberRepository.save(member);

        List<Member> byUsernameAndAgeGreaterThan = memberRepository.findByUsernameAndAgeGreaterThan(member.getUsername(),25);

        for (Member member1 : byUsernameAndAgeGreaterThan) {
            System.out.println("member1 = " + member1.getUsername());
        }
    }
    @Test
    void nativeQueryTest() throws Exception {
        Member member = new Member("이찬호", 27, null);
        memberRepository.save(member);

        List<Member> findMembers = memberRepository.findUser("이찬호", 27);
        assertThat(member).isEqualTo(findMembers.get(0));
    }

    @Test
    void findUserNameList() throws Exception {
        Member memberA = new Member("이찬호", 27,null);
        Member memberB = new Member("공희진", 25,null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);


        List<String> userNameList = memberRepository.findUserNameList();
        for (String s : userNameList) {
            System.out.println("이름은 : " + s);
        }

    }
    @Test
    void findMemberByDto() throws Exception {
        Team teamA = new Team("연구 4팀");
        Team teamB = new Team("연구 2팀");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member memberA = new Member("이찬호", 27, teamA);
        Member memberB = new Member("곽명환", 29, teamB);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }

        List<Member> findByNames = memberRepository.findByNames(Arrays.asList("이찬호", "곽명환"));
        for (Member findByName : findByNames) {
            System.out.println("findByName = " + findByName);
        }
    }
    @Test
    void sepTypeTest() throws Exception {
        //given
        Team teamA = new Team("연구 4팀");
        Team teamB = new Team("연구 2팀");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member memberA = new Member("이찬호", 27, teamA);
        Member memberB = new Member("곽명환", 29, teamB);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        //when


        // 다양한 타입을 지원 - Spring Data JPA
        List<Member> findList = memberRepository.findListByUsername("이찬호");
        Member findObject = memberRepository.findOneByUsername("이찬호");
        Optional<Member> findOptional = memberRepository.findOptionalByUsername("이찬호");

    }
    @Test
    void pagingJPA() throws Exception {
        //given
        memberRepository.save(new Member("member1",10,null));
        memberRepository.save(new Member("member2",10,null));
        memberRepository.save(new Member("member3",10,null));
        memberRepository.save(new Member("member4",10,null));
        memberRepository.save(new Member("member5",10,null));
        //when

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0,3,Sort.by(Sort.Direction.DESC,"username"));
        Page<Member> pageContent = memberRepository.findByAge(age, pageRequest);

        
        // api 로 반환하기 위해서 DTO 로 변환하는 작업.
        Page<MemberDto> toMap = pageContent.map(member -> new MemberDto(member.getId(), member.getUsername(), member.getTeam().getName()));
        
        List<Member> content = pageContent.getContent();
        long totalElements = pageContent.getTotalElements();
        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);
    }

    @Test
    void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("이찬호",27,null));
        memberRepository.save(new Member("이찬호",27,null));
        memberRepository.save(new Member("이찬호",27,null));
        memberRepository.save(new Member("이찬호",27,null));
        memberRepository.save(new Member("이찬호",27,null));
        memberRepository.save(new Member("이찬호",27,null));
        //when
        int changeCount = memberRepository.bulkAgePlus(10);

        //then
        assertThat(changeCount).isEqualTo(6);
    }
    @Test
    void findMemberLazy() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamA");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();
        //when

        List<Member> all = memberRepository.findAll();
        for (Member member : all) {
            System.out.println("member = " + member.getTeam());
        }
    }
}