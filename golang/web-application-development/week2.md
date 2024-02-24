# Chapter3. database/sql 패키지
- `*sql.DB` 를 가져오기 위헤 `sql.Open` 함수를 사용한다.
- database/sql 패키지는 `*sql.DB` 를 이용해서 쿼리를 실행하거나, 트랜잭션을 실행한다.
- `*sql.DB` 타입은 고루틴에서 동시에 사용하여 thread safe 하다.
- connection pool
	- 명시적으로 설정이 없어도 사용 가능하다. 물론 명시적으로 설정할 수도 있다.
	- `*sql.DB` 가 내부적으로 연결 풀을 가지며, 종료될 때까지 `Close`를 별도로 호출하지 않아도 처음 실행시에만 호출하면 계속 사용가능하다.
