package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {
    // 스프링 데이터 jpa가 jpa repository를 받고 있어서 구현체를 자동으로 만들어 준다
    @Override
    Optional<Member> findByName(String name);
}
