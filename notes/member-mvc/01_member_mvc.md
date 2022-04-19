# 회원관리 예제 웹 MVC 개발

 회원 관리 예제를 웹 MVC로 만들어 보자. 회원 컨트롤러를 통해 회원을 등록하고 조회할 수 있도록 한다.
 
## 홈

홈 접속 시 사용자의 요청을 처리할 홈 컨트롤러와 홈 관련 화면을 만들자.

### 컨트롤러

* `/` 도메인으로 접속 시 `home.html` 반환
> 아무 것도 없으면 welcome page(`static/index.html`)로 [간다고 했는데](../web-basic/01_web_basic.md), 우선순위가 있음. 요청이 오면 스프링 컨트롤러를 먼저 찾고, 없으면 정적 파일을 찾는 것
```java
@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
```

### 홈 화면
* 회원 가입: `/members/new`
* 회원 목록: `/members`
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <div class="container">
        <div>
            <h1>Hello Spring</h1>
            <p>회원 기능</p>
            <p>
                <a href="/members/new">회원 가입</a>
                <a href="/members">회원 목록</a>
            </p>
        </div>
    </div>
</body>
</html>
```

## 회원 등록

 회원 등록, 조회 등을 위해 다음의 회원 컨트롤러 및 화면을 추가하자.
* 회원 등록
  * GET `/members/new`: 회원 가입 폼
  * 사용자가 폼 제출 시 `createMemberForm.html`의 폼이 POST `/members/new`로 전달
  * POST `/members/new`: 폼에서 전달된 정보를 통해 회원 가입


### 컨트롤러

* `MemberController`: 회원 등록, 회원 조회 등의 요청 처리
* `MemberForm`: `/members/new`로 POST 제출된 폼을 관리하기 위한 클래스


#### 회원 컨트롤러

 회원 컨트롤러에 회원 등록을 위해 다음 부분을 추가하자.
* `createForm`: `/members/new`의 회원 등록 폼 GET 요청 처리
  * `@GetMapping("/members/new")`
* `create`: `/members/new`의 회원 등록 폼 POST 요청 처리
  * `@PostMapping("/members/new")`
  * 폼(`MemberForm`) 객체에서 가져 온 `name`을 이름으로 등록
  * 회원가입 후 홈 화면으로 리다이렉트: `redirect:/`
* `list`: `/members`의 GET 요청 처리
  * `@GetMapping("/members")`
  * memberService에서 전체 회원을 조회한 후 **모델에 담아 뷰 템플릿으로** 넘김
```java
@Controller
public class MemberController {
    private MemberService memberService;
    
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    
    // 회원 등록 GET
    @GetMapping("/members/new")
    public String createForm() {
        return "members/createMemberForm";
    }
    
    // 회원 등록 POST
    @PostMapping("/members/new")
    public String create(MemberForm form) {
        Member member = new Member(); // 새로운 회원
        member.setName(form.getName());
        
        memberService.join(member); // 회원 서비스에 회원 객체 넘김
        
        return "redirect:/";
    }
    
    // 회원 조회
    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList"; // memberList 템플릿
    }
}
```

#### 폼 컨트롤러

 제출된 폼 객체이다. MemberController

```java
public class MemberForm {
    private String name;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
```


 폼이 반환되면 


### 회원 등록 템플릿
`/members/new`로 폼을 제출(POST)한다.
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<div class="container">
    <form action="/members/new" method="post">
        <div class="form-group">
            <label for="name">이름</label>
            <input type="text" id="name" name="name" placeholder="이름을 입력하세요" />
        </div>
        <button type="submit">등록</button>
    </form>
</div>
</body>
</html>
```

### 회원 조회 템플릿
`/members`에서 넘어 온 회원을 보여준다. 템플릿 언어에서 모델 안의 값을 읽어서 템플릿 언어의 방식대로 렌더링한다.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<div class="container">
    <div class="table-area">
        <table>
            <thead>
            <tr>
                <th>#</th>
                <th>이름</th>
            </tr>
            </thead>
            <tbody>
            <tr th:each="member : ${members}">
                <td th:text="${member.id}"></td>
                <td th:text="${member.name}"></td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
```