package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

// @Service
public class MemberService {

    private final MemberRepository memberRepository;

// @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


//    /* private final
//     private final로 선언한다면 직접 적으로 값을 참조할 수는 없지만 생성자를 통해 값을 참조할 수 있다
//     하지만 private static final의 경우에는 생성자를 통해 값을 참조할 수 없다.
//     이때 private static final 변수는 무조건 초기화돼있어야 한다.
//     출처: https://jwdeveloper.tistory.com/179
//     */
//    // private final MemberRepository memberRepository = new MemoryMemberRepository();
//    private final MemberRepository memberRepository;
//
//    // 생성자, DI
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원가입
     */
    public Long join(Member member) {

        /* AOP 일일이 로직을 짜면

        long start = System.currentTimeMillis();
        try {
            validateDuplicateMember(member);
            memberRepository.save(member);
            return member.getId();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("join = " + timeMs + "ms");
        }
         */


        // 중복 회원 검증
        validateDuplicateMember(member);

        // 중복 회원이 아니라면 회원 저장
        memberRepository.save(member);
        return member.getId(); // 임의로 id 반환

    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                        .ifPresent(m -> {
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
