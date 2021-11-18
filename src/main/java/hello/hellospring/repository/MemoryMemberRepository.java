package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import java.util.*;

//@Repository
public class MemoryMemberRepository implements MemberRepository {

    private static Map<Long, Member> store = new HashMap<>(); // 회원을 저장할 repository
    private static long sequence = 0L; // key 생성

    @Override
    public Member save(Member member) {
        member.setId(++sequence); // sequence 값을 증가시켜 id로 세팅한 뒤,
        store.put(member.getId(), member); // 회원 저장
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id)); // null일 가능성이 있으므로
    }

    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream()
                .filter(member -> member.getName().equals(name))
                .findAny();
    }

    @Override
    public List<Member> findAll() {
        // store의 모든 values(Member) 반환
        return new ArrayList<>(store.values());
    }

    // 테스트 시 데이터 비우도록
    public void clearStore() {
        store.clear();
    }
}
