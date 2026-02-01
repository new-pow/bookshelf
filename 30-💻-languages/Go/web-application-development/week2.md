---
tags: [language, go]
---

# Chapter3. database/sql 패키지
- `*sql.DB` 를 가져오기 위헤 `sql.Open` 함수를 사용한다.
- database/sql 패키지는 `*sql.DB` 를 이용해서 쿼리를 실행하거나, 트랜잭션을 실행한다.
- `*sql.DB` 타입은 고루틴에서 동시에 사용하여 thread safe 하다.
- connection pool
	- 명시적으로 설정이 없어도 사용 가능하다. 물론 명시적으로 설정할 수도 있다.
	- `*sql.DB` 가 내부적으로 연결 풀을 가지며, 종료될 때까지 `Close`를 별도로 호출하지 않아도 처음 실행시에만 호출하면 계속 사용가능하다.
- `*Context` 메서드를 함께 사용하는 것이 좋다.
	- 데이터베이스 관련 처리 실행 중에 외부에서 중단 처리를 전달하기 위해서.
	- 만약 사용하지 않는다면 실행 중에 멈출 수 있는 방법이 없다.
- `*sql.Row` 타입값을 반환하는 메서드만 `sql.ErrNoRows`가 발생한다.
	- `QueryContext` 메서드에서 발생하지 않는다.
	- `SelectContext` 메서드에서 발생하지 않는다.
	- 예상한 것과는 달리 동작할 수 있으니 주의하자
- 트랜잭션을 사용할 때는 defer 문으로 Rollback 메서드를 호출하면 된다.
	- `defer`문으로 선언한 처리는 메서드가 끝나는 시점에 반드시 실행되므로 `Rollback`을 항상 호출할 수 있다.
	- 만약 로직을 실행하다가
		- 에러가 발생하면 : Err 발생 -> return Err -> defer tx.Rollback() 호츨
		- 에러가 발생하지 않으면 : Commit -> defer tx.Rollback() 호출 -> Rollback 메서드는 commit이나 context 패키지 경유의 중단 처리가 끝난 트랜잭션에 대해 rollback을 호출하지 않는다.
```go
func (r *Repository) Update(ctx context.Context) error {
	tx, err := r.db.BeginTx(ctx, nil)
	if err != nil {
	return err
	}
}
	defer tx.Rollback()
	// 이하 생략 p.40
```

- 대신 많이 사용하는 오픈소스 패키지
	- 다음과 같은 이유로 이 `database/sql` 패키지를 실제 제품에 사용하는 경우는 드물다.
		- 쿼리 결과를 구조체로 변환하는 것이 어렵다.
		- SQL로부터 RDBMS를 처리하는 Go 코드를 자동생성하고 싶다.
		- ORM을 사용하고싶다.
	- 쓸 수 있는 오픈소스 패키지
		- gorm.io/gorm
		- github.com/jmoiron/sqlx
		- github.com/ent/ent
		- ...

---
# Chapter4. 가시성과 Go
> Go에는 `private/public` 이 없다.
> 대신 패키지 밖에서 참조할 수 있는가/없는가 만 있다. `exported/unexported`

- `exported` 대문자로 시작하는 패키지 변수, 함수, 구조체, 구조체 필드
- `unexported` 소문자로 시작하는 패키지 변수, 함수, 구조체, 구조체 필드

## `internal` 패키지
```
root
- internal
```
- 외부 패키지에서 참조할 수 없다.

---
# Chapter5. Go Modules
> go의 패키지 관리 툴. go의 특징 중 하나

외부 라이브러리를 가리키는 용어.
- 모듈 : 버전 관리를 통해 릴리스하는 단위
- 패키지 : 각 리포지터리의 각 디렉토리

## go modules 를 사용하는 것을 권장한다.
- Go 1.11 에서 시험판 제공
- 2019 Go 1.13부터 정식 탑재
- `GO111MODULE` 환경 변수에 따라 모듈 사용여부를 설정할 수 있다.

## `go.mod`
- 모듈 관리는 `go.mod` 파일과 `go.sum` 파일을 사용해서 패키지 버전을 관리한다.
- 시맨틱 버저닝 2.0.0 으로 관리된다.
- 의존하고 있는 모듈 중 가장 오래된 모듈의 패키지를 이용하도록 설계되어 있다.
- 시작 방법
	- `go mod init`
- 시작 이후 원하는 패키지 얻기
	- `go get`
- 의존 패키지 업데이트
	- `go get -u ./...` 모든 패키지 업데이트
	- `go get -u [패키지명]`

## 의존 대상 코드에 디버그 코드 추가하는 방법
1. `go mod vendor`
2. `go.mod` 파일에 `replace` 디렉티브 작성
3. `Workspace` 모드 사용

## Go Module은 어디서 오는걸까?
- 프록시 서버를 거쳐 리소스를 가져온다.
- 가져온 패키지의 코드 무결성도 체크섬을 통해 검증

## `GOPRIVATE`
- 프록시 서버를 거치지 않고, 체크섬 검증도 불필요한 패키지의 프리픽스를 설정
- `go env -w` 명령어나 직접 os 에서 변경할 수 있다.
```
GOPRIVATE="github.com/my_company,github.com/my_account/me"
```

