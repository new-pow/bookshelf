# 미들웨어 패턴
- 동일한 처리를 여러 엔드포인트에서 반복적으로 사용하는 경우, 이런 공통 처리를 작성하는 패턴
- 예를 들어 접속 로그 출력 등을 뜻합니다.

## 미들웨어를 만드는 법
- 해당 시그니처를 충족하도록 구현하는 것이 일반적입니다.
```go
import "net/http"

func(h http.Handler) http.Handler
```
- HTTP 핸들러 구현에 적용할 수 있습니다.
- 구현할 때는 함수를 반환하는 함수를 만듭니다.
```go
func MyMiddleware(h http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		// 로직
	})
}
```

- 만약 다른 타입값을 인수로 전달하고 싶다면, 미들웨어 패턴을 반환하는 함수를 구현합니다
```go
func VersionAdder(v Appversion) func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			// 로직 안에서 v를 사용할 수 있다.
		})
	}
}
```

---
### 🤔
- 왜 이렇게 해야하지? 시그니처를 지키면서...
- 읽기 힘들진 않을까?


## 복원 미들웨어
- 요청 처리 중 발생한 panic 을 공통으로 처리하는 역할을 합니다.
- JSON 응답 바디에 panic 정보를 포함한 응답을 반환하는 복원 미들웨어 recovery middleware 예시
- p.135

```go

```

---
### 🤔
- 이걸 어떻게 조립해서 사용하지?
- 이렇게 구현만 해두면 호출은 어떻게 하지? 저절로 모두 될리가...

## 접속 로그 미들웨어

---

## 요청 바디를 로그에 남기는 미들웨어

---

## 상태 코드 및 응답 바디를 저장하는 미들웨어


---

## context.Context 타입값에 정보를 부여하는 미들웨어
- 이거 엄청 유용할 듯!

---
## 웹 애플리케이션 자체의 미들웨어 패턴


---
- 왜 브라우저에서 2번 호출되나?
	- 파비콘때문에
	- [참고링크](https://forum.golangbridge.org/t/why-handler-is-called-twice/14862)
	- [참고링크](https://stackoverflow.com/questions/33432192/handlefunc-being-called-twice)
- 미들웨어 패턴의 순서를 지정해주는 방법
```
func main() { firstHandler := Middleware(http.HandlerFunc(FirstHandler) secondHandler := Middleware(http.HandlerFunc(SecondHandler) http.Handle("/first", firstHandler) http.Handle("/second", secondHandler) }	
```