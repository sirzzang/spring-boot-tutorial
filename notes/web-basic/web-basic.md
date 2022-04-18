# 스프링 웹 개발 기초

 웹 개발에는 크게 3가지의 방법이 있다.
 
* 정적 컨텐츠: 서버에서 정적 컨텐츠를 그대로 서빙
* MVC와 템플릿 엔진: 서버에서 동적 가공을 후 컨텐츠 서빙
* API: 클라이언트에게 혹은 서버끼리 json, xml 등 데이터를 서빙

> 정적 컨텐츠 방식을 제외하면, HTML로 내리는지, 아니면 API로 데이터를 내리는지의 차이다. 

## 정적 컨텐츠

스프링부트는 보통 `/static` 폴더에서 정적 컨텐츠를 제공한다.

 정적 컨텐츠 디렉토리에 예제 html 파일을 만들어 보자.
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Static</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
정적 컨텐츠
</body>
</html>
```

 실행 후 파일명 그대로, `localhost:8080/hello-static.html`으로 접근한다.

<br>

원리는 다음과 같다.

![static-contents](assets/static-contents.png)
* 스프링 부트 내장 톰캣 서버가 `hello-static.html`에 대한 요청을 스프링 컨테이너에 위임한다
* 스프링 컨테이너가 컨트롤러에서 `hello-static` 관련 컨트롤러가 있는지 찾는다 → **컨트롤러가 우선순위**
* 스프링 컨테이너가 매핑된 컨트롤러를 찾지 못하면 `resources` 내부의 정적 컨텐츠를 찾는다
* 찾으면 반환한다. 변환하지 않고 그대로 넘긴다

## 템플릿 엔진

 MVC는 모델(Model), 뷰(View), 컨트롤러(Controller)의 약자이다.

 과거에는 컨트롤러와 뷰가 분리되어 있지 않고, 뷰에서 대부분을 처리하는 모델 원 방식을 사용했다. 요즘은 MVC 스타일을 사용한다. **관심사의 분리**, **역할과 책임**의 관점에서 이해하면 된다.
* view: 화면을 그리는 데 집중
* controller, model: 비즈니스 로직 처리에 집중

 지금의 MVC 및 템플릿 엔진 방식에서는 템플릿 엔진을 Model, View, Controller 방식으로 바꿔서, 처리를 거쳐서 렌더링된 HTML을 클라이언트에게 전달한다.


<br>
 `name` 파라미터를 받아 모델에 넘기도록 컨트롤러 예제를 작성해 보자.
* `name` 파라미터 넘기지 않고 실행하면 `MissingServletRequestParameterException`이 발생하므로, `required = false` 옵션 사용 

```java
@Controller
public class HelloController {
    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello"); // 스프링이 직접 받는다
        return "hello"; // hello.html 리턴
    }
    
    @GetMapping("hello-mvc")
    public String helloMvc(@RequestParam("name", required=false) String name, Model model) {
        // url 파라미터로 받은 것을 model에 넘김
        model.addAttribute("name", name);
        return "hello-mvc";
    }
}
```

 `name` 파라미터를 받아 처리할 `hello-mvc.html`을 작성해 보자.
* thymeleaf 템플릿 엔진으로 작성. 템플릿 엔진이 html을 동적으로 바꾸는 역할을 담당
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<p th:text="'안녕하세요. ' + ${name}">Hello! empty</p>
</body>
</html>
```

 실행 후, `name` 쿼리 파라미터를 넘기면 된다.

 원리는 다음과 같다.

![template-engine](assets/mvc-template.png)
* `hello-mvc` 요청이 오면, 톰캣 서버가 요청을 스프링 컨테이너에 넘긴다
* 스프링 컨테이너에서 `hello-mvc`와 매핑된 컨트롤러를 찾는다
* 찾아진 `helloController`는 request parameter를 model에 key(`name`) - value 형태로 넘겨서, 스프링에게 넘긴다
* 스프링의 화면과 관련된 resolver가 템플릿 엔진을 찾아 연결시킨다
* 변환된 HTML이 클라이언트에게 보여진다. 정적 컨텐츠와 달리, 템플릿 엔진에 의해 변환된 화면이 넘어간다


## API

 컨트롤러에 API 방식으로 데이터를 내려줄 수 있도록 추가해 보자.
* `@ResponseBody`: HTTP 통신 Body 단에 데이터 직접 넣어 줌
  * 응답 body
  * 기본은 JSON 형태. 원하면 XML로도 반환 가능
* `helloString`: view가 없이 데이터가 문자로 그대로 내려감
* `helloApi`: 문자가 아니라 데이터를 내림
  * `Hello`: static class로, 클래스 안에서 접근 가능. 데이터 객체를 내리기 위함
  * `@RequestParam(name)`: `name` 파라미터를 받고, 그걸 이용해서 객체 반환

```java
@Controller
public class HelloController {
    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello");
        return "hello";
    }
    
    @GetMapping("hello-mvc")
    public String hello(@RequestParam("name") String name, Model model) {
        model.addAttribute("name", name);
        return "hello-mvc";
    }
    
    @GetMappping("hello-string")
    @ResponseBody
    public String helloString(@RequestParam("name") String name) {
        return "hello" + name;
    }
    
    @GetMapping("hello-api")
    @ResponseBody
    public HelloApi(@RequestParam("name") String name) {
        Hello hello = new Hello();
        hello.setName(name); // 파라미터로 받은 name 설정
        return hello; // 데이터 객체 반환
    }

    static class Hello {
        private String name;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    
}
```

 실행 후, `name` 파라미터를 넘겼을 때, `hello-string`과 `hello-api`의 실행 결과는 각각 다음과 같다.

![api-result](assets/api-result.png)
![api-object](assets/api-object.png)

 원리는 다음과 같다. `@ResponseBody`를 사용하면 HTTP의 body에 데이터를 직접 반환한다
 
![api-responsebody](assets/api-responsebody.png)

* 요청이 오면, 내장 톰갯 서버가 스프링 컨테이너에 요청을 넘긴다
* `hello-api` 혹은 `hello-string`에 매핑된 `hello-controller`를 찾는다
* `@ResponseBody` 어노테이션이 붙어 있다면, 데이터를 HTTP body에 응답으로 내려줄 수 있도록 동작한다
  * `viewResolver`(MVC 패턴에서 동작) 대신에 `HttpMessageConverter`가 동작한다(*HTTP message로 변환해 주는 동작을 한다는 느낌?*)
    * 기본 문자 처리: `StringHttpMessageConverter`(문자로 변환)
    * 기본 객체 처리: `MappingJackson2HttpMssageConverter`(json 스타일로 변환)

> 기본은 JSON이지만, 사실은 클라이언트의 HTTP-accept 관련 헤더와 서버의 컨트롤러 반환 타입 정보를 조합해서 `HttpMessageConverter`가 선택된다. 예컨대, 클라이언트에서 `accept` 헤더에 XML로 요청하면, 관련 converter가 동작하는 것이다.
