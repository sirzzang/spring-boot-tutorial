package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberServiceTest {

    /*
     MemberService memberService = new MemberService();
     MemoryMemberRepository memoryMemberRepository = new MemoryMemberRepository();
     */

    // 테스트 실행할 때마다 리포지토리 만들고, 그것을 서비스에 주입
    MemberService memberService;
    MemoryMemberRepository memberRepository;
    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }

    // 메모리 스토어 정리
    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void join() {
        // given: 이 데이터를 기반으로
        Member member = new Member();
        member.setName("hello");

        // when: 이걸 검증한다
        Long saveId = memberService.join(member); // join 메소드 리턴 값 저장 id

       // then: 검증부
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    public void 중복_회원_예외() {
        // given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        // when
        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");

        /*
        try {
            memberService.join(member2);
            fail(); // 실패
        } catch(IllegalStateException e) {
            // 예외 잡아내면 정상적으로 테스트가 성공한 것
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }
        */

        // then
    }

    @Test
    void findMembers() {

    }

    @Test
    void findOne() {

    }

}