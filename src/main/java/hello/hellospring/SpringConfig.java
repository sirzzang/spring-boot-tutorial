package hello.hellospring;

import hello.hellospring.repository.MemberRepository;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    // spring 데이터 jpa: 알아서 repository 구현체 만듦
    private final MemberRepository memberRepository;
    @Autowired
    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // JPA
    /*
    @PersistenceContext
    private EntityManager em;
    public SpringConfig(EntityManager em) {
        this.em = em;
    }
    */

    // jdbc
    /*
    // @Autowired DataSource dataSource;
    private DataSource dataSource;
    @Autowired
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    */

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository);
    }

//    @Bean
//    public TimeTraceAop timeTraceAop() {
//        return new TimeTraceAop();
//    }

    // spring data jpa에서는 리포지토리 구현을 알아서 해주니까, 아래 코드 모두 필요 없음
    //   public MemberService memberService() {
    //        return new MemberService(memberRepository()); // MemberService 생성자에 MemberRepository 필요
    //    }

    //    @Bean
    //    public MemberRepository memberRepository() {
    //        // return new MemoryMemberRepository();
    //        // return new JdbcMemberRepository(dataSource); // data source 필요
    //        // return new JdbcTemplateMemberRepository(dataSource);
    //        // return new JpaMemberRepository(em);
    //    }

}
