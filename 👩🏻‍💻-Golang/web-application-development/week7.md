# Chapter 16. HTTP 서버를 약한 결합 구성으로 변경하기
## 들어가기 전에
- 코드 해석에 부족함이 있을 수 있습니다 🥲
- 추가/정정할 내용이 있다면 알려주세요.

- 해당 챕터에서는
	- 실용적인 Server 구조체를 정의한다.
	- Beyond the Twelve-Factor App 관점을 적용한다.
### 참고 : Beyond the Twelve-Factor App 이란?
- 케빈 호프만의 무료 전자책 '클라우드 앱의 12가지 요소를 넘어서'를 뜻합니다.
- 무료 전자책 [링크](https://raw.githubusercontent.com/ffisk/books/master/beyond-the-twelve-factor-app.pdf)

## 060 환경 변수로부터 설정 불러오기
- 변경할 내용
	- 변경 전 : 어플리케이션 실행시 포트 번호를 인수로 받는다.
	- 변경 후 : 환경 변수로부터 각 정보를 읽어오는 패키지를 추가하여 환경변수를 사용한다.
- Beyond the Twelve-Factor App 5. Configuration, Credentials, and Code
	- Configuration : 애플리케이션 자체의 일부인 내부 정보가 포함되지 않습니다. 모든 배포에서 동일하게 유지되는 경우 구성이 아닙니다. (예 : DB 연결 정보, AWS와 같은 자격 증명 등)
	- Credentials : 매우 민감한 정보이며 코드베이스에 들어가서는 안됩니다.

> 만약 민감한 정보나 환경별 정보를 노출하지 않고도 코드베이스를 오픈 소스로 공개할 수 있다면, 당신은 아마도 코드, 구성 및 자격 증명을 격리시키는 데 좋은 일을 한 것입니다.

### Config 패키지 구현하기
- 독립된 패키지로 만드는 이유는 환경 변수를 처리하는 테스트가 다른 테스트에 주는 영향을 피하기 위해서입니다.
- `t.Setenv` 메서드
	- `os.Setenv(key, value)` 호출하고, `Cleanup`으로 테스트 이후 환경 변수를 원래 값으로 돌려놓습니다.
	- 전체 프로세스에 영향을 미치므로 병렬 테스트나 병렬 조상(ancestors)이 있는 테스트에서는 사용할 수 없습니다.
	- [testing.go 참조](https://cs.opensource.google/go/go/+/refs/tags/go1.22.1:src/testing/testing.go;l=1290

```go
package config

import (
	"github.com/caarlos0/env/v6"
)

type Config struct {
	Env  string `env:"TODO_ENV" envDefault:"dev"`
	Port int    `env:"TODO_PORT" envDefault:"80"`
}

func New() (*Config, error) {
	cfg := &Config{} // 구조체 포인터 생성
	// 환경 변수 parse
	// Parse 함수는 'env' 태그가 포함된 구조체를 구문 분석하고 환경 변수에서 해당 값을 로드합니다.
	if err := env.Parse(cfg); err != nil {
		return nil, err
	}
	return cfg, nil
}

```

### 환경 변수 사용해서 실행하기
```go
package main

import (
	"context"
	"fmt"
	"log"
	"net"
	"net/http"
	"os"

	"golang.org/x/sync/errgroup"

	"github.com/new-pow/go_todo_app/config"
)

func main() {
	if err := run(context.Background()); err != nil {
		log.Fatalf("failed to terminate server: %v", err)
		os.Exit(1)
	}
}

func run(ctx context.Context) error {
	cfg, err := config.New() // config 생성
	if err != nil {
		return err
	}
	l, err := net.Listen("tcp", fmt.Sprintf(":%d", cfg.Port)) // 포트 설정
	if err != nil {
		log.Fatalf("failed to listen port %d: %v", cfg.Port, err)
	}
	url := fmt.Sprintf("http://%s", l.Addr().String())
	log.Printf("server listening at %s", url)
	
	s := &http.Server{
		Addr: ":18080",
		Handler: http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			fmt.Fprintf(w, "Hello, %s!", r.URL.Path[1:])
		}),
	}
	eg, ctx := errgroup.WithContext(ctx)
	eg.Go(func() error {
		if err := s.Serve(l); err != nil &&
			err != http.ErrServerClosed {
			log.Printf("failed to close: %+v", err)
			return err
		}
		return nil
	})
	<-ctx.Done()
	if err := s.Shutdown(context.Background()); err != nil {
		log.Printf("failed to shutdown: %+v", err)
	}
	return eg.Wait()
}

```


## 061 시그널 처리하기
### signal.NotifyContext 를 사용해 시그널 기다리기
#### 정상 종료
- 웹 애플리케이션은 **정상 종료**되어야 합니다.
- 정상 종료란?
	- 종료 시그널을 받으면 새로운 요청을 받지 않고 처리되고 있던 요청이 끝낸 후 모든 자원을 릴리즈한 뒤 프로세스를 종료하는 것입니다.
	- 다른 말로는 그레이스풀 셧다운(graceful shutdown)
- Go http 표준 라이브러리의 `Shutdown`을 사용하는 경우, 정상 종료합니다.

> Shutdown gracefully shuts down the server without interrupting any active connections. Shutdown works by first closing all open listeners, then closing all idle connections, and then waiting indefinitely for connections to return to idle and then shut down. If the provided context expires before the shutdown is complete, Shutdown returns the context's error, otherwise it returns any error returned from closing the [Server](https://pkg.go.dev/net/http#Server)'s underlying Listener(s).
> 
> 종료는 활성 연결을 중단하지 않고 서버를 정상적으로 종료합니다. 셧다운은 먼저 열려 있는 모든 수신기를 닫은 다음 유휴 연결을 모두 닫은 다음 연결이 유휴 상태로 돌아갈 때까지 무한정 기다린 다음 종료하는 방식으로 작동합니다. 종료가 완료되기 전에 제공된 컨텍스트가 만료되면 종료는 컨텍스트의 오류를 반환하고, 그렇지 않으면 서버의 기본 리스너를 닫을 때 반환된 모든 오류를 반환합니다.
> 
> [참고링크](https://pkg.go.dev/net/http#Server.Shutdown)

#### 시그널
- 처리해야 할 시그널
	- 간섭 시그널 SIGINT
		- 명령줄에서 ctrl + c 를 누른 경우
		- `2 SIGINT terminate process interrupt program`
	- 종료 시그널 SIGTERM
		- 외부로부터 컨테이너 종료 시그널을 받는 경우
		- `15 SIGTERM terminate process software termination signal`
- OS마다 지원 시그널이 다를 수 있으며 리눅스 기준 `kill -l` 명령어를 통해 확인할 수 있습니다.
- Go 1.16부터 os/signal 패키지에 추가된 `signal.NotifyContext` 함수를 사용하여 시그널을 감지할 수 있게 되었습니다. (p.198)

#### 구현
```go
package main

import (
	"context"
	"fmt"
	"log"
	"net"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	// 동작 확인을 위해
	"golang.org/x/sync/errgroup"

	"github.com/new-pow/go_todo_app/config"
)

func main() {
	if err := run(context.Background()); err != nil {
		log.Fatalf("failed to terminate server: %v", err)
		os.Exit(1)
	}
}

func run(ctx context.Context) error {
	ctx, stop := signal.NotifyContext(ctx, syscall.SIGINT, syscall.SIGTERM) // os.Interrupt 대신 syscall.SIGINT 사용
	defer stop()                                                            // defer로 시그널이 들어오면 stop
	cfg, err := config.New()                                                // config 생성
	if err != nil {
		return err
	}
	l, err := net.Listen("tcp", fmt.Sprintf(":%d", cfg.Port)) // 포트 설정
	if err != nil {
		log.Fatalf("failed to listen port %d: %v", cfg.Port, err)
	}
	url := fmt.Sprintf("http://%s", l.Addr().String())
	log.Printf("server listening at %s", url)
	s := &http.Server{
		Handler: http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			// 명령 줄에서 테스트 하기 위한 로직
			time.Sleep(5 * time.Second)
			fmt.Fprintf(w, "Hello, %s!", r.URL.Path[1:])
		}),
	}
	// 다른 고루틴에서 HTTP 서버를 실행한다.
	eg, ctx := errgroup.WithContext(ctx)
	eg.Go(func() error {
		if err := s.Serve(l); err != nil &&
			err != http.ErrServerClosed {
			log.Printf("failed to close: %+v", err)
			return err
		}
		return nil
	})
	// 채널로부터의 알림(종료 알림)을 기다린다.
	<-ctx.Done()
	if err := s.Shutdown(context.Background()); err != nil {
		log.Printf("failed to shutdown: %+v", err)
	}
	// Go 메서드로 실행한 다른 고루틴의 종료를 기다린다.
	return eg.Wait()
}

```

#### 결과
```
app-1  | 
app-1  |   __    _   ___  
app-1  |  / /\  | | | |_) 
app-1  | /_/--\ |_| |_| \_ v1.51.0, built with Go go1.22.1
app-1  | 
app-1  | mkdir /app/tmp
app-1  | watching .
app-1  | watching config
app-1  | !exclude tmp
app-1  | building...
app-1  | running...
app-1  | 2024/04/02 22:05:05 server listening at http://[::]:8080
^CGracefully stopping... (press Ctrl+C again to force)
[+] Stopping 1/0
 ✔ Container go_todo_app-app-1  Stopped                                                                                                                 0.1s 
canceled
```
- 결과 값이 모두 출력된 후, 종료됨
```
curl localhost:18000/hi
Hello, hi!%       
```

## 062 Server 구조체 정의하기
- 기존 Run 함수 내부에서 하는 처리가 많아졌습니다.
- `Server` 타입을 통해 일부 역할을 분할합니다.
- Server 의 역할
	- 외부에서 port, routing 설정을 주입받아 server 생성
	- 외부 시그널을 감지하여 프로세스 종료 관리
	- 다른 go routine으로 HTTP 요청을 처리하기 위한 서버 실행.
- [type Server](https://pkg.go.dev/net/http#Server) 참고

```go
package main

import (
	"context"
	"log"
	"net"
	"net/http"
	"os"
	"os/signal"
	"syscall"

	"golang.org/x/sync/errgroup"
)

type Server struct {
	srv *http.Server
	l   net.Listener
}

// 값을 받아서 Server 구조체를 생성한다.
// 동적으로 포트를 선택하기 위해 net.Listener를 받는다.
// 라우팅 로직을 담은 mux를 받는다.
func NewServer(l net.Listener, mux http.Handler) *Server {
	return &Server{
		srv: &http.Server{Handler: mux},
		l:   l,
	}
}

func (s *Server) Run(ctx context.Context) error {
	ctx, stop := signal.NotifyContext(ctx, syscall.SIGINT, syscall.SIGTERM)
	defer stop()
	eg, ctx := errgroup.WithContext(ctx)
	eg.Go(func() error { // 다른 고루틴에서 HTTP 서버를 실행
		// http.ErrServerClosed 는 정상적인 종료를 의미한다.
		// http.Server.Shutdown() 이 호출되면 http.ErrServerClosed 가 반환되기 때문
		if err := s.srv.Serve(s.l); err != nil &&
			err != http.ErrServerClosed {
			log.Printf("failed to close: %+v", err)
			return err
		}
		return nil
	})

	// 채널로부터의 알림(종료 알림)을 기다린다. 시그널이나 취소 요청에 의해 취소될 때까지 블록된 상태로 있다.
	<-ctx.Done()
	// 모든 요청을 처리한 후 서버를 종료한다.
	if err := s.srv.Shutdown(context.Background()); err != nil {
		log.Printf("failed to shutdown: %+v", err)
	}
	// 정상 종료를 기다린다.
	return eg.Wait()
}

```

## 063 라우팅 정의를 분할한 NewMux 정의하기
- 라우팅하는 역할을 분리합니다.
- (해결 못함) 정적 분석 오류를 회피하기 위해 명시적으로 반환값을 버린다? 가 정확히 무슨 뜻인지 이해 못하였습니다.
	- status 외에 다른 반환값이 없다?

### 구현
```go
package main

import "net/http"

func NewMux() http.Handler {
	mux := http.NewServeMux()
	// health check
	mux.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json; charset=utf-8")
		_, _ = w.Write([]byte(`{"status": "ok"}`))
})
return mux
}
```

- 테스트는 mock을 이용하여 외부 동작을 제어합니다.
- httptest 패키지를 이용하면 http 패키지 타입값과 동일한 타입을 얻을 수 있습니다.
	- [참고 : httptest pkg](https://pkg.go.dev/net/http/httptest)
```go
package main

import (
	"io"
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestNewMux(t *testing.T) {
	w := httptest.NewRecorder()                              // 응답을 기록하는 레코드
	r := httptest.NewRequest(http.MethodGet, "/health", nil) // 요청을 생성
	sut := NewMux()
	sut.ServeHTTP(w, r)                         // 요청을 처리
	resp := w.Result()                          // 요청 처리 후에 해당 함수로 응답을 얻음
	t.Cleanup(func() { _ = resp.Body.Close() }) // 테스트 종료 시 응답 바디를 닫음

	// 응답 코드가 200인지 확인
	if resp.StatusCode != http.StatusOK {
		t.Error("want status code 200, but", resp.StatusCode)
	}
	// 응답 바디가 올바른지 확인
	got, err := io.ReadAll(resp.Body)
	if err != nil {
		t.Fatalf("failed to read body: %v", err)
	}
	want := `{"status": "ok"}`
	if string(got) != want {
		t.Errorf("want %q, but got %q", want, got)
	}
}
```

## 064 run 함수를 다시 리팩터링합니다.
- 새로 정의한 Server 타입과 NewMux 함수를 사용하여 run 함수를 리팩터링합니다.
- 역할을 분배했기 때문에 훨씬 간결해졌습니다.

```go
package main

import (
	"context"
	"fmt"
	"log"
	"net"
	"os"

	"github.com/new-pow/go_todo_app/config"
)

func main() {
	if err := run(context.Background()); err != nil {
		log.Printf("failed to terminated server: %v", err)
		os.Exit(1)
	}
}

func run(ctx context.Context) error {
	cfg, err := config.New() // 환경 변수를 읽어들인다.
	if err != nil {
		return err
	}
	l, err := net.Listen("tcp", fmt.Sprintf(":%d", cfg.Port)) // 포트를 지정하여 리스닝한다.
	if err != nil {
		log.Fatalf("failed to listen port %d: %v", cfg.Port, err)
	}
	url := fmt.Sprintf("http://%s", l.Addr().String())
	log.Printf("start with: %v", url)
	mux := NewMux()        // 라우팅 로직을 담은 mux를 생성한다.
	s := NewServer(l, mux) // 서버를 생성한다.
	return s.Run(ctx)      // 서버를 실행한다.
}

```


---
# 실습하면서 겪었던 문제들
## config 패키지 import 가 안됨
- p.195 config 패키지 사용을 위해 import를 시도했으나 IDE에 다음과 같은 에러가 뜨며 import가 안되는 문제가 있었습니다.

![](https://private-user-images.githubusercontent.com/103120173/318957437-8cb720cf-0abd-4497-a879-ddc18d314292.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MTIwOTEyMTMsIm5iZiI6MTcxMjA5MDkxMywicGF0aCI6Ii8xMDMxMjAxNzMvMzE4OTU3NDM3LThjYjcyMGNmLTBhYmQtNDQ5Ny1hODc5LWRkYzE4ZDMxNDI5Mi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwNDAyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDQwMlQyMDQ4MzNaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0yMDUzMjAxNmJlZmZiNTNjNzQ3Mjc0MDVkMWViMDdlZmU2NzdhZWVlZmE1ZjFkNDIyOTBjYjI2NTMwOWIwZDdkJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.IhXxsd2gUNj0yv1NvvMHLfss7B4wSJMOU24R7fIniIE)
- `go get`을 터미널에서 명령해보니 다음과 같은 에러가 발생합니다.
```
go: web-server imports
        github.com/new-pow/go_todo_app/config: cannot find module providing package github.com/new-pow/go_todo_app/config
```

### 해결
- go package는 `pkg.go.dev` 사이트에서 관리되고 있습니다. 
	- go get으로 패키지를 가져올 때는 해당 사이트에서 가져오게 되며, `pkg.go.dev`가 polling 하는 방식으로 동작하기 때문에 즉시 반영되지 않으므로 주의합니다.
- 해당 사이트에 접속해보니 다음과 같은 예외를 확인할 수 있었습니다.
```
https://pkg.go.dev/github.com/new-pow/go_todo_app/config

### Not Found

"github.com/new-pow/go_todo_app/config" does not have a valid module path ("web-server").
```
- 처음에 만들 때 module 이름을 잘못 지은 이유에서 발생한 오류였습니다. github와 go pkg 사이트를 거치지 않아도 문제없이 import 됩니다.

```
# go.mod
module web-server

# 변경 후
module github.com/new-pow/go_todo_app
```