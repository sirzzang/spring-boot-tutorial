# 회원 도메인과 리포지토리 개발

 도메인 및 리포지토리 코드를 작성하고, 원하는 방식대로 작동하는지 테스트하자. 
 
테스트를 위해서는 `JUnit` 프레임워크를 사용한다. 테스트 시 **순서에 의존적인 테스트를 작성하지 않도록** 주의한다.


## 도메인

 식별자(`id`)와 이름(`name`)을 갖는 `Member` 도메인을 만들어 보자.
```java
public class Member {
    
    private Long id;
    private String name;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
```
<br>

 회원 도메인 객체를 저장하고 불러올 수 있는 리포지토리를 만들어 보자. 

 먼저 인터페이스를 만든다. 회원 리포지토리에서 반드시 구현해야 하는 기능을 담자.
* `save`: 회원 저장
* `findById`: `id`로 회원 조회
* `findByName`: `name`으로 회원 조회
* `findAll`: 지금까지 저장한 모든 회원 조회
> `Optional`: 자바 8 기능. `null`이 반환될 때 감싸서 반환

```java
public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByName(String name);
    List<Member> findAll();
}
```

 메모리 상에서 동작하는 회원 리포지토리 구현체를 만들어 보자.
* `store`: 메모리 상에 어딘가에 저장해 놓을 회원 리스트
  * `id`와 `Member` 객체를 각각 `key`, `value`로 갖는 `Map` 형태의 자료 구조
  * 실제로 사용할 `Map`은 `Hashmap`. 실무에서는 동시성 문제로 `ConcurrentHashmap`을 사용하는 것을 권장
* `save`: 시스템 저장 용도. `id`만 설정. `name`은 고객이 회원 가입 시 작성하는 것을 가정
* `findById`
  * `store`에서 `get`을 통해 조회
  * `Optional.ofNullable`을 이용해 `null`일 경우 대비
* `findByName`: 자바 8 람다 기능 이용. 하나라도 회원을 찾으면 반환
  - [ ] : 같은 이름을 가진 회원이 여러 명이면 어떻게?

```java
import java.util.Optional;

public class MemoryMemberRepository implements MemberRepository {

    private static Map<Long, Member> store = new Hashmap<>(); // 회원 저장소
    private static long sequence = 0L; // 회원 id 생성 용도


    @Override
    Member save(Member member) {
        member.setId(++sequence); // 회원 id 설정
        store.put(member.getId(), member); // 회원 id와 회원 객체 저장
        return member;
    }

    @Override
    Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    Optional<Member> findByName(String name) {
        store.values().stream() // 루프 돌면서
                .filter(member -> member.getName().equals(name)) // 파라미터로 넘어 온 이름과 같은 회원 조회
                .findAny(); // 하나라도 찾기
    }

    @Override
    List<Member> findAll() {
        return new ArrayList<>(store.values());
    }
    
    // 초기화                      
    public void clearStore() {                   
        store.clear(); // ArrayList의 원소 모두 삭제    
    }                                          

}
```

## 테스트

 회원 리포지토리 테스트를 위한 테스트 케이스를 작성해 보자.
* 테스트의 경우, 다른 곳에서 접근하지 않아도 되므로 굳이 public 아니어도 됨
* `save`: 회원 저장 기능 테스트
  * `findMember`: 저장 후 꺼낸 회원 객체. `Optional`에서 꺼낼 때는 `.get()` 이용(권장되는 방법은 아니나, 테스트 코드에서 꺼낼 때는 이렇게 해도 됨)
  * 만든 `Member` 객체와 저장 후 꺼낸 `Member` 객체가 동일하면 저장이 잘 된다고 검증 가능
    * 표준 출력으로 `==` 연산자를 이용해 확인
    * `org.junit.jupiter.api.assertEquals`를 이용해 확인
    * `org.assetj.core.api.assertThat`을 이용해 확인
* `findAll`: 전체 회원 조회 테스트
  * 테스트 작성 후 전체 테스트를 진행하면, `findByName` 테스트에서 에러 발생: `org.opentest4j.AssertionFailedError`
    * 테스트 시 `findAll` 실행 후 `findByName` 등이 실행됨
      * 이미 `spring1`, `spring2`라는 이름을 가진 회원이 저장됨
      * 이전에 저장한 객체가 나와 버림
    * 테스트 순서는 보장되지 않음
    * 모든 테스트는 순서에 의존적으로 설계되면 안 됨
  * 테스트 끝난 후 데이터를 지울 수 있도록 해야 함
* `afterEach`: 테스트 케이스 끝날 때마다 실행되는 콜백 메서드로, 테스트 후 `MemoryMemberRepository`의 데이터 초기화
  * `@AfterEach`: 테스트 클래스 내 메소드가 끝날 때마다 동작하는 콜백 메서드
  * `MemoryMemberRepository`만 테스트하므로 구현체 타입을 `MemberRepository` 인터페이스가 아닌 `MemoryMemberRepository`로 설정

```java
import static org.assertj.core.api.Assertions.*;

class MemoryMemberRepositoryTest {
    
    MemberRepository repository = new MemoryMemberRepository();
    
    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }
    
    @Test
    public void save() {
        // given
        Member member = new Member();
        member.setName("spring");
        
        // when
        repository.save(member); 
        
        // then
        Member findMember = repository.findById(member.getId).get();
        System.out.println("findMember = " + (findMember == member));
        assertThat(findMember, member);
    }
    
    @Test
    public void findByName() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);
        
        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);
        
        Member findMember = repository.findByName("spring1").get();
        
        assertThat(findMember).isEqualTo(member1);
    }
    
    @Test
    public void findAll() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);
        
        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);
        
        List<Member> findMembers = repository.findAll();
        
        assertThat(findMembers.size()).isEqualTo(2);
    }
}
```

> 먼저 리포지토리를 개발한 후 테스트 케이스를 작성했는데, 테스트 케이스를 작성한 후 개발하는 것이 테스트 주도 개발(Test Driven Development)이다. 개발할 것을 검증할 수 있는 틀을 만들어 놓고, 개발 후 검증하는 것이다. 테스트 케이스를 먼저 작성한 후 구현 클래스를 작성한다.




