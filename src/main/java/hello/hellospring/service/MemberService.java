package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

// SpringConfig를 통해 의존성을 주입하므로, 여기서는 어노테이션 주석 처리
// @Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원가입
     */
    public Long join(Member member) {

        /*
         * AOP 없이 일일이 시간 측정 로직을 짰을 때의 안 좋은 예
         * 
         * long start = System.currentTimeMillis(); try {
         * validateDuplicateMember(member); memberRepository.save(member); return
         * member.getId(); } finally { long finish = System.currentTimeMillis(); long
         * timeMs = finish - start; System.out.println("join = " + timeMs + "ms"); }
         */

        // 중복 회원 검증
        validateDuplicateMember(member);

        // 중복 회원이 아니라면 회원 저장
        memberRepository.save(member);
        return member.getId(); // 임의로 id 반환

    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName()).ifPresent(m -> {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        });
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 1명의 회원 조회
     */
    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }

}
