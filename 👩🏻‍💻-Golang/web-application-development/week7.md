# Chapter 16. HTTP 서버를 약한 결합 구성으로 변경하기
## 들어가기 전에

- 해당 챕터에서는
	- 실용적인 Server 구조체를 정의한다.
	- Beyond the Twelve-Factor App 관점을 적용한다.
### 참고 : Beyond the Twelve-Factor App 이란?
- 케빈 호프만의 무료 전자책 '클라우드 앱의 12가지 요소를 넘어서'를 뜻합니다.
- 무료 전자책 [링크](https://www.google.com/search?q=Beyond+the+Twelve-Factor+App&oq=Beyond+the+Twelve-Factor+App&gs_lcrp=EgZjaHJvbWUqBggAEEUYOzIGCAAQRRg7MgYIARAAGB4yBggCEAAYHjIGCAMQRRg8MgYIBBBFGDzSAQc1MTlqMGo3qAIAsAIA&sourceid=chrome&ie=UTF-8#:~:text=Beyond%20the%20Twelve%2DFactor%20App%20%2D%20GitHub,raw.githubusercontent.com%20%E2%80%BA%20books%20%E2%80%BA%20master)
- 

## 060 환경 변수로부터 설정 불러오기
- 변경할 내용
	- 변경 전 : 어플리케이션 실행시 포트 번호를 인수로 받는다.
	- 변경 후 : 환경 변수로부터 각 정보를 읽어오는 패키지를 추가하여 환경변수를 사용한다.
- Beyond the Twelve-Factor App 5. Configuration, Credentials, and Code
	- Configuration : 애플리케이션 자체의 일부인 내부 정보가 포함되지 않습니다. 모든 배포에서 동일하게 유지되는 경우 구성이 아닙니다. (예 : DB 연결 정보, AWS와 같은 자격 증명 등)
	- //todo 

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

	// config 패키지 import, 주의! github에 올라가 있어야 함
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