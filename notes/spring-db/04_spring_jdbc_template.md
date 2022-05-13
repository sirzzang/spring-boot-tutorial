# 스프링 JdbcTemplate

* 스프링 JdbcTemplate, MyBatis 등과 같은 라이브러리는 JDBC API에서 본 반복 코드를 대부분 제거한다.
  * `ResultSet`, `Connection` 등
  * 템플릿 메서드 패턴을 이용해 코드 반복을 줄였다고
* 다만, SQL은 직접 생성해야 한다.

## 환경설정

* [순수 JDBC](02_jdbc.md)와 동일하게 설정

## JdbcTemplate 리포지토리

* `JdbcTemplateMemberRepository`에 `DataSource` 의존성 주입
  * [순수 JDBC](02_jdbc.md)의 `JdbcMemberRepository`가 `DataSource`를 주입 받았던 것과 동일
  * 생성자 1개인 경우, `@Autowired` 생략 가능

* `rowMapper`: DB 레코드를 자바 객체로 연결(실제 객체 생성)
* `jdbcTemplate`
  * 쿼리 작성 후, 결과를 row mapper로 매핑
    * `ResultSet`으로부터 각각의 컬럼(`id`, `name` 등) 정보를 엔티티 객체의 속성으로 세팅(`setId`, `setName` 등)
  * `?`: 파라미터
* `SimpleJdbcInsert`: 테이블명, `pk`를 알면 insert문을 만들어 줌


<br>

```java
public class JdbcTemplateMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    // 의존성 주입
    @Autowired
    public JdbcTemplateMemberRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
  
    public Member save(Member member) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("member").usingGeneratedKeyColumns("id");
    
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", member.getName());
        
        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        member.setId(key.longValue());
        return member;
    }
  
    public Optional<Member> findById(Long id) {
        List<Member> result = jdbcTemplate.query("select * from member where id = ?", memberRowMapper(), id);
        return result.stream().findAny(); // Optional 반환
    }
  
    public Optional<Member> findByName(String name) {
        List<Member> result = jdbcTemplate.query("select * from where name = ?", memberRowMapper(), name);
        return result.stream().findAny();
    }
  
    public List<Member> findAll() {
        return jdbcTemplate.query("select * from member", memberRowMapper());
    }
  
    // row mapper
    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setId(rs.getLong("id"));
            member.setName(rs.getString("name"));
            return member;
        };
    }
}
```

## jdbcTemplate 리포지토리 사용 설정

```java
@Configuration
public class SpringConfig {
    private DataSource dataSource;
    
    @Autowired
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }
    
    public MemberRepository memberRepository() {
        // return new MemoryMemberRepository();
        // return new JdbcMemberRepository(dataSource);
        return new JdbcTemplateMemberRepository(dataSource);
    }
    
}

```