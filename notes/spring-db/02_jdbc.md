# JDBC

## 설정

 스프링에서 JDBC를 사용하기 위해 다음과 같이 설정을 변경하자. 
 
### gradle

`build.gradle`에 다음과 같이 JDBC, 사용하고자 하는 DB(h2) 관련 라이브러리를 추가한다.
 
```groovy
implementation 'org.springframework.boot:spring-boot-starter-jdbc'
runtimeOnly 'com.h2database:h2'
```

### application.properties

 `application.properties`에 다음과 같이 데이터베이스 연결 설정을 추가한다.
```properties
spring.datasource.url=jdbc:h2:tcp://localhost/~/test
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
```
 
## Jdbc 리포지토리

 `MemberRepository`를 구현하는 `JdbcMemberRepository`를 만든다.

* `DataSoruce`: DB에 접속하기 위한 데이터 소스. 스프링 컨테이너로부터 의존성 주입
  * `getConnection()`
    * 데이터 소스 연결
    * `dataSource.getConnection()`를 쓰면 계속해서 커넥션 늘어남
* `sql`: 변수보다 상수로 빼는 게 더 나은 선택
* 연결 후 쿼리 실행
  * `connection`: `dataSource`에서 `getConnection()`을 통해 활성화된 연결 획득
  * `prepareStatement`: 쿼리 실행(쿼리 문장 분석, 컴파일, 실행)
    * 생성된 키를 얻기 위한 설정
      * `Statement.RETURN_GENERATED_KEYS`
      * `pstmt.getGeneratedKeys()`
  * `setString`, `setLong`: sql 문의 파라미터 인덱스에 맞게 값(타입에 따라 `String` 혹은 `Long` 등) 설정
  * `executeUpdate`(등록), `executeQuery`(조회): sql 실행

* 리소스 반환: 커넥션을 끊어주지 않으면 계속해서 커넥션 쌓이다가 장애 발생할 수도 있음


```java
import java.sql.*;

public class JdbcMemberRepository implements MemberRepository {

    private final DataSource dataSource;

    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        String sql = "intert into member(name) values(?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, member.getName());

            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();

            if (rs.next()) {
                member.setId(rs.getLong(1));
            } else {
                throw new SQLException("id 조회 실패")
            }
            return member;
        } catch (Excetpion e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }

    }

    @Override
    public Optional<Member> findById(Long id) {

        String sql = "select * from member where id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Member> findAll() {
        String sql = "select * from member";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            
            rs = pstmt.executeQuery();
            
            // 결과값 Members 배열에 추가
            List<Members> members = new ArrayList<>();
            while(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                members.add(member);
            }
            
            return members;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
    
    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select * from member where name = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
    
    // connection 획득
    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }
    
    // connection 종료
    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        
        // result set 확인
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // prepared statement 확인
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // connection
        try {
            if (conn != null) {
                close(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void close(Connection conn) throws SQLException {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }
}
```

## 스프링 설정

 JDBC를 이용한 회원 리포지토리로 리포지토리를 변경해 보자. 
 
 Spring 설정파일(`SpringConfig`)을 변경하면 된다.
* `dataSource`: 스프링에서 제공하는 `DataSource`
  * `config`도 스프링 빈으로 관리되기 때문에, 스프링이 관리하는 스프링 부트가 데이터 소스를 보고 자체적으로 빈 생성
* `memberRepository`: 반환하는 구현체 반환
```java
@Configuration
public class SpringConfig {
    
    // datasource
    private DataSource dataSource;
    
    @Autowired
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }
    
    @Bean
    public MemberRepository memberRepository() {
        // return new MemoryMemberRepository();
        return new JdbcMemberRepository(dataSource); // repository 구현체 변경
    }
}
```
