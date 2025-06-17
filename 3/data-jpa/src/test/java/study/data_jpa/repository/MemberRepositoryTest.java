package study.data_jpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Member;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @Test
    public void testMember() {
        //given
        Member member = new Member("member-B");
        Member savedMember = memberRepository.save(member);

        //when
        Member finMember = memberRepository.findById(savedMember.getId()).get();

        //then
        Assertions.assertThat(finMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(finMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(finMember).isEqualTo(member);

        memberRepository.findById(savedMember.getId()).ifPresent(item -> {
            //then
            Assertions.assertThat(item.getId()).isEqualTo(member.getId());
            Assertions.assertThat(item.getUsername()).isEqualTo(member.getUsername());
            Assertions.assertThat(item).isEqualTo(member);
        });

    }
}