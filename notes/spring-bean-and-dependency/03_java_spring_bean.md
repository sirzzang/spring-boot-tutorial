# 자바 코드로 스프링 빈 등록

 [컴포넌트 스캔과 자동 의존관계 설정](02_component_scan.md)에서 작성했던 `@Service`, `@Repository`, `@Autowired` 애노테이션을 제거하고 진행한다.(`@Contoller`는 그대로 둔다)

 애노테이션 없이, 자바 코드를 이용해 스프링 컨테이너에게 스프링 빈을 알려 준다.


## configuration 작성

 스프링 컨테이너에 스프링 빈이라고 알려주는 설정 파일을 작성한다.

스프링 컨테이너가 구동할 때, 설정 파일을 읽고 애노테이션을 통해 스프링 빈임을 인식해 객체를 생성한다. 그리고 스프링 빈으로 등록된 빈 간에 의존성을 주입한다.

* `@Configuration`: 설정 관련 파일임을 알려 주는 애노테이션
* `@Bean`: 스프링 빈으로 등록할 것임을 알려 주는 애노테이션
* `@Bean` 애노테이션을 통해 등록한 스프링 빈에 의존관계 주입
 
```java
@Configuration
public class SpringConfig {
    
    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository()); // 의존관계 주입
    }
    
    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository(); // 회원 리포지토리 구현체 반환
    }
    
}
```

 **컨트롤러는 어쩔 수 없다**. 스프링이 어차피 관리하는 것이기 때문에, `@Controller` 애노테이션을 붙여 두면 컴포넌트로 등록되고, 컴포넌트 스캔이기 때문에 `@Autowired`로 의존 관계를 주입해 두면 된다.

> 과거에는 자바 코드가 아니라 XML 형식으로 설정했다. 지금은 실무에서 XML을 거의 사용하지 않으므로, 자바 코드를 이용하면 돈다고 알아 두면 된다.


> 다른 구현체로 변경하여 의존성을 주입하기가 상대적으로 편하다. 예컨대, `MemoryMemberRepository`가 아니라 `DbMemoryRepository`를 사용하는 경우, 설정 파일에서 의존관계 설정할 때만 바꿔 주면 된다.
>
> ```java
> @Configuration
> public class SpringConfig {
>     
>     @Bean
>     public MemberService memberService() {
>         return new MemberService(memberRepository()); // 의존관계 주입
>     }
>     
>     @Bean
>     public MemberRepository memberRepository() {
>         // return new MemoryMemberRepository();
>         return new DbMemberRepository(); // 구현체 변경
>     }
>
> }
