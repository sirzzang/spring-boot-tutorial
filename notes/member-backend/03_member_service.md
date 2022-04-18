# 회원 서비스

 리포지토리와 도메인을 활용한 비즈니스 로직이다.
 
> 리포지토리와 달리, 서비스 클래스는 비즈니스에 가까운 naming이 필요하다. 예컨대, 리포지토리에서 `save`였던 것이, 서비스에서는 `join`이 될 수 있다.
 
## 서비스

 회원 리포지토리를 이용하는 회원 서비스 예제를 작성해 보자.
* `join`: 회원가입
  * 가입 시 `id` 반환
  * 중복 회원 체크
    * `ifPresent`: `Optional`로 반환된 값이 있는지 검사
    * 

```java
public class MemberService {
    private final MemberRepository memberRepository = new MemoryMemberRepository();

    /**
     * 회원 가입
     * @param member
     * @return
     */
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 전체 회원 조회
     * @return
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * id로 회원 조회
     * @param memberId
     * @return
     */
    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }
    
    // 중복 회원 체크
    public void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent(
                        m -> {
                            throw new IllegalStateException("이미 존재하는 회원입니다.");
                        }
                );
    }
}
```

## 테스트

> 테스트 작성 시 주로 `given` - `when` - `then` 방식을 사용한다. 무언가가 주어졌을 때(`given`), 이걸 실행하면(`then`), 이런 결과가 나와야 한다(`then`)는 의미이다.

> 테스트 작성 시, 정상 플로우 외에 예외 플로우도 중요하다.

<br>

 회원 서비스의 동작을 확인하는 테스트를 작성해 보자.
* `회원가입`: 저장한 게 리포지토리에 있는 게 맞는지 검증
* `중복_회원_예외`: 중복 회원 가입 시 예외가 발생하는지 검증
  * `try-catch`문을 이용한 검증
    ```java
        // then
        try {
            memberService.join(member2);
            fail(); // 예외가 발생하지 않으면 실패
        } catch(IllegalStateException e) {
            // 예외가 발생하면 정상
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }    
    ```
  * `assertThrows`를 이용한 검증: 발생하기 기대하는 예외
    * `assertThrows`는 에러를 반환할 수 있음

* [리포지토리 테스트](`02_member_repository.md`)에서와 마찬가지로, 동일한 회원을 저장하면 테스트 시 에러가 발생할 수 있으므로, `clear`한다.

```java
class MemberServiceTest {
    
    MemberService memberService = new MemberService();
    MemoryMemberRepository memberRepository = new MemoryMemberRepository();
    
    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }
    
    @Test
    void 회원가입() {
        // given
        Member member = new Member();
        member.setName("hello");
        
        // when
        Long saveId = memberService.join(member);
        
        // then
        Optional<Member> findMember = MemberService.findOne(saveId).get();
        Assertions.assertThat(member.getName()).isEqualTo(findMember.getName());
    }
    
    @Test
    void 중복_회원_예외() {
        // given
        Member member1 = new Member();
        member1.setName("spring");
        
        Member member2 = new Member();
        member2.setName("spring");
        
        // when
        memberService.join(member1);
        
        // then
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }
    
    @Test
    void findMembers() {
        
    }
    
    @Test
    void findOne() {
        
    }
    
}

```

> 테스트는 직관적인 이름을 써도 되기 때문에, 한국어로 이름을 작성해도 된다. 빌드 시 코드에도 포함되지 않는다.

<br>

### 의존성 주입의 필요성

 테스트에서 사용하는 `MemberRepository`는 `new` 키워드를 통해 만든 새로운 인스턴스이다. 문제는, `MemberService`에서 만들어서 사용하는 `MemoryMemberRepository`와, 테스트 케이스에서 만든 `MemoryMemberRepository`가 **서로 다른 인스턴스**라는 것이다. 
 
 사실, 같은 리포지토리를 2개 쓸 이유가 없다. 현재 구현체에서 `MemberService`가 사용하는 `MemberRepository`는 `static` 키워드로 생성되어서 클래스에 붙어서 큰 문제는 되지 않는다. 다만, 문제가 될 수 있는 경우가 있다. 만약 `static` 클래스가 안 붙으면 DB가 서로 다른 DB가 되어 버리는 것이다. 따라서, 아래와 같이 변경하는 것이 좋다.

 이를 차치하더라도, 일단 같은 리포지토리를 사용해서 테스트하는 게 맥락 상에도 더 맞다.

<br>

 같은 리포지토리 인스턴스를 사용하기 위해, 아래와 같이 변경하자.

`MemberService`에서 생성자를 통해 `MemberRepository`를 주입하도록 변경한다.
```java
public class MemberService {
    private final MemberRepository memberRepository;
    
    // 외부에서 MemberRepository 주입
    public MemberService(MemberRepository memberRepository) {
      this.memberRepository = memberRepository;
    }
}
```

 `MemberServiceTest`에서 `MemoryMemberRepository` 인스턴스를 생성해 `MemberService`에 주입한다.
* `@BeforeEach`: 개별 테스트 메서드 동작 전에 작동하는 메서드
  * 테스트 실행 전마다 `memberService`, `memberRepository` 생성
  * 
 
```java
class MemberServiceTest {
    MemberService memberService;
    MemoryMemberRepository memoryMemberRepository;
    
    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        MemberService memberService = new MemberService(memberRepository);
    }
}
```

 그러면 `MemberService` 입장에서 같은 `MemberRepository`를 사용하게 된다.
