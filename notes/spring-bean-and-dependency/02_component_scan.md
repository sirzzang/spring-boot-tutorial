# 컴포넌트 스캔과 자동 의존관계 설정


## 회원 컨트롤러

회원 서비스를 호출하는 회원 컨트롤러를 작성해 보자.
> 컨트롤러의 기능은 아무 것도 없다. 현재 상태에서는 깡통 컨트롤러다.

* `@Controller`: 스프링 컨테이너가 해당 애노테이션이 붙은 클래스의 인스턴스를 생성하여 관리
    * `@Autowired`: 의존성 주입
        * `MemberController` 인스턴스가 생성될 때, 생성자 호출
        * 생성자에 `@Autowired` 애노테이션이 있으면, **스프링 컨테이너에서** 의존성 주입의 대상이 되는 객체를 찾아 의존성 주입
            * `MemberService`와 `MemberController` 연결

```java
@Controller
public class MemberController {
    private final MemberService memberService;
    
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
}
```

## 스프링 빈 등록

`MemberService`, `MemberRepository`도 찾을 수 있도록 스프링 빈으로 등록
* 등록하지 않았을 때의 오류
    * `... MemberController required a bean of type 'hello.hellospring.service.MemberService' that could not be found.`
    * `@Autowired`가 붙은 것을 발견하면, 스프링 컨테이너는 이미 등록된 것을 찾는데, 없으니까 오류 발생
* `@Service`: `MemberService`를 스프링 빈으로 등록
* `@Repository`: `MemoryMemberRepository`를 스프링 빈으로 등록

```java
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원 가입
     */

    /**
     * 회원 조회
     */
}
```

```java
@Repository
public class MemoryMemberRepository implements MemberRepository {
    private static Map<Long, Member> store = new HashMap<> ();
    private static Long sequence = 0L;

    /**
     * 저장 등 로직
     */
    
}
```


## 의존성 주입

등록된 스프링 빈 간의 의존 관계를 주입한다. 최종적으로, 다음과 같다.

* `MemberController`

```java
@Controller
public class MemberController {
    private final MemberService memberService;
    
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
}
```

* `MemberService`
```java
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    
    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.MemberRepository = memberRepository;
    }
}
```
