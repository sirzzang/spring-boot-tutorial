# 스프링 통합 테스트

 [JDBC 리포지토리](02_jdbc.md)에서 만든 리포지토리를 바탕으로, 스프링 컨테이너와 DB까지 모두 연결한 통합 테스트를 진행해 보자.

> 이전에 작성했던 테스트 코드들(`src.test.java.hello.hellospring.repository` 등)은 순수 자바 코드를 가지고 테스트한 것으로, **단위 테스트**이다.
* 스프링 configuration도 읽고, 아예 스프링 컨테이너가 뜬다. 단위 테스트에 비해 시간이 오래 걸린다.
* 순수 단위 테스트가 더 좋은 테스트일 가능성이 높다. 컨테이너 없이도 테스트를 작성할 수 있어야 한다.

## 통합 테스트


* `@SpringBootTest`: 스프링 컨테이너와 테스트 케이스를 함께 실행
* `@Transactional`: 테스트 실행 후 DB 반영 방지
  * 기본적으로(`@Transactional` 어노테이션 없을 때) auto commit
  * 테스트 케이스에 `@Transactional` 어노테이션을 사용하면, 테스트 시작 전에 트랜잭션을 시작해, 테스트 완료 후 항상 롤백
    * `@Service` 등에 사용되면 롤백하지 않고 정상 작동
* `@BeforeEach` 삭제
  * 의존성 주입을 개발자가 직접 하지 않음
  * 스프링 컨테이너에서 **의존성 주입을 통해** 해당 역할 담당\
* `@AfterEach` 삭제: 테스트 후 DB 데이터 삭제는 `@Transactional`을 통함
* 의존성 주입: 테스트 코드는 말단이므로, **필드 주입**(가장 쉬운 방법) 사용
  * `MemoryMemberRepository` 타입이 아니라 `MemberRepository` 타입으로 필드 선언
  * 구현체는 스프링이 설정 파일(configuration)에서 찾아서 가져 옴


```java
class MemberServiceIntegrationTest {
    
    // 테스트를 위한 의존성 주입
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    
    @Test
    void 회원가입() {
        // given
        Member member = new Member();
        member.setName("이레");
        
        // when
        Long saveId = memberService.join(member);
        
        // then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }
    
    @Test
    void 중복_회원_예외() {
        // given
        Member member1 = new Member();
        member.setName("spring");
        
        Member member2 = new Member();
        member.setName("spring");
        
        // when
        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> memberService.join(member2));
        
        // then
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }
    
}
``` 

