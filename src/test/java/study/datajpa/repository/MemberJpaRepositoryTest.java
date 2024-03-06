package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    TeamJPARepository teamJPARepository;
    
    @Test
    void 회원가입() throws Exception {
        //given
        Member member = new Member("이찬호");

        //when
        Member saveMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(saveMember.getId());

        //then

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    void basicCRUD() throws Exception {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamJPARepository.save(teamA);
        teamJPARepository.save(teamB);

        Member member1 = new Member("member1",27,teamA);
        Member member2 = new Member("member2",25,teamB);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);
        
        // 리스트 조회 검증
        List<Member> allMember = memberJpaRepository.findAll();
        for (Member member : allMember) {
            System.out.println("member = " + member.getUsername());
        }
        
        
        // 팀 단건 조회 검증
        Team findTeamA = teamJPARepository.findById(teamA.getId()).get();
        Team findTeamB = teamJPARepository.findById(teamB.getId()).get();
        assertThat(findTeamA).isEqualTo(teamA);
        assertThat(findTeamB).isEqualTo(teamB);

        //팀 리스트 조회 검증
        List<Team> findAllTeam = teamJPARepository.findAll();
        assertThat(findAllTeam.size()).isEqualTo(2);
    }

    @Test
    void paging() throws Exception {
        //given
        memberJpaRepository.save(new Member("member1",10,null));
        memberJpaRepository.save(new Member("member2",10,null));
        memberJpaRepository.save(new Member("member3",10,null));
        memberJpaRepository.save(new Member("member4",10,null));
        memberJpaRepository.save(new Member("member5",10,null));

        int age = 10;
        int offset = 0;
        int limit = 3;
        //when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

    }
    @Test
    void bulkUpdate() throws Exception {
        //given
        memberJpaRepository.save(new Member("member1",27,null));
        memberJpaRepository.save(new Member("member1",27,null));
        memberJpaRepository.save(new Member("member1",27,null));
        memberJpaRepository.save(new Member("member1",27,null));
        memberJpaRepository.save(new Member("member1",27,null));

        //when
        int resultCount = memberJpaRepository.bulkAgePlus(20);
        assertThat(resultCount).isEqualTo(5);
    }
}