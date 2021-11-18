package hello.hellospring.domain;

import javax.persistence.*;

@Entity
public class Member {

    // 회원 요구사항: id, 이름
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    // id 프로퍼티 접근
    public Long getId() { return id; }
    public void setId(Long id) {
        this.id = id;
    }

    // name 프로퍼티 접근
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}
