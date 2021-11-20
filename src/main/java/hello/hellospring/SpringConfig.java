package hello.hellospring;

import hello.hellospring.repository.MemberRepository;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    /* ---- Repository ---- */
    /* MemoryMemberRepository 구현체 */
    // @Bean
    // public MemberRepository memberRepository() {
    // return new MemoryMemberRepository();
    // }

    /* JdbcMemberRepository 구현체: DataSource 필요 */
    // private DataSource dataSource;
    // @Autowired
    // public SpringConfig(Datasource datasource) {
    // this.datasource = dataSource;
    // }
    // @Bean
    // public MemberRepository memberRepository() {
    // // return new JdbcMemberRepository();
    // }

    /* JpaMemberRepository 구현체: EntityManger 필요 */
    // @PersistentContext
    // private Entitymaneger em;
    // public SpringConfig(EntityManger em) {
    // this.em = em;
    // }
    // @Bean
    // public MemberRepository memberRepository() {
    // // return new JpaMemberRepository();
    // }

    // spring 데이터 jpa: 알아서 repository 구현체 만듦

    /* SpringDataMemberRepository 구현체: Spring Data JPA에서 알아서 repository 구현체 생성 */
    private final MemberRepository memberRepository;

    @Autowired
    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /* ---- Service ---- */
    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository);
    }

    /* ---- AOP 설정 Spring Config에서도 가능 ---- */
    // @Bean
    // public TimeTraceAop timeTraceAop() {
    // return new TimeTraceAop();
    // }

}
