package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryMemberRepositoryTest { // 다른 곳에서 접근하지 않아도 되므로 굳이 public 아니어도 됨

    MemoryMemberRepository repository = new MemoryMemberRepository();

    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }

    @Test
    public void save() {
        Member member = new Member(); // 회원 도메인 생성
        member.setName("spring"); // 회원 이름 설정

        repository.save(member); // 회원 저장소 저장

        // 저장 시 설정된 id로 회원 조회 후 result 변수에 할당
        Member result = repository.findById(member.getId()).get(); // Optional에서 값 꺼낼 때 get

        // 검증: 저장했던 member가 find로 조회되는지 확인
        // System.out.println(result == member);
        // Assertions.assertEquals(member, result); org.junit.jupiter.api.Assertions;
        // Assertions.assertThat(member).isEqualTo(result); org.assertj.core.api.Assertions;
        assertThat(member).isEqualTo(result); // static import
    }

    @Test
    public void findByName() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

        Member result1 = repository.findByName("spring1").get();
        assertThat(result1).isEqualTo(member1);

        // Member result2 = repository.findByName("spring2").get();
        // assertThat(result2).isEqualTo(member1);
    }

    @Test
    public void findAll() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

        List<Member> result = repository.findAll();
        assertThat(result.size()).isEqualTo(2);
    }
}
