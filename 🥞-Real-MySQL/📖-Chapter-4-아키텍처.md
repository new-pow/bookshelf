# 📖 4. 아키텍처
## 4.1 MySQL 엔진 아키텍처
![](https://www.notion.so/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F1feb7462-9c33-4bf1-b0bb-7973d34ffaf2%2F084031f1-99f4-41c0-8810-97f871f28ac1%2FUntitled.png?table=block&id=cad2f53f-6bf6-4ae7-8bc6-fd2db018dc6f&spaceId=1feb7462-9c33-4bf1-b0bb-7973d34ffaf2&width=2000&userId=180a704c-6552-4796-9dd2-ab125439ed98&cache=v2)
- 크게 **MySQL 엔진**과 **스토리지 엔진**으로 구분합니다.
- 이 둘을 합쳐 MySQL 혹은 MySQL 서버라고 표현합니다.

### 4.1.1.1 MySQL 엔진

- 클라이언트로부터 접속 및 쿼리 요청을 처리하는 **커넥션 핸들러**
- SQL 파서
- 전처리기
- 옵티마이저
- 표준 SQL(ANSI SQL) 문법을 지원하기 때문에 표준 문법에 따라 작성된 쿼리를 다른 DBMS와 호환할 수 있습니다.

### 4.1.1.2 스토리지 엔진

- 실제 데이터를 디스크 스토리지에 저장하거나 읽어오는 부분을 전담합니다.
- MySQL 엔진과 달리 스토리지 엔진은 여러개 사용할 수 있습니다.
- 테이블 생성시 같이 사용할 엔진을 지정할 경우, 해당 스토리지 엔진이 처리합니다.

### 4.1.1.3 핸들러 API

- MySQL엔진이 스토리지 엔진에 쓰기 또는 읽기를 요청하는데, 이러한 요청을 핸들러 요청이라고 합니다.
- 이에 사용되는 API를 핸들러 API라고 합니다.
- _MySQL 엔진이 스토리지 엔진에게 데이터를 요처앟고 받는 API_


## 4.1.2 MySQL 스레딩 구조
![](https://www.notion.so/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F1feb7462-9c33-4bf1-b0bb-7973d34ffaf2%2F0ec12c3c-7287-444a-ab5e-4e68e6f40278%2FUntitled.png?table=block&id=cbd181ce-fd12-43f3-b207-b088c7073dab&spaceId=1feb7462-9c33-4bf1-b0bb-7973d34ffaf2&width=2000&userId=180a704c-6552-4796-9dd2-ab125439ed98&cache=v2)
- 80 페이지 MySQL 스레딩 모델

- MySQL 서버는 스레드 기반으로 작동합니다.
- 포그라운드 스레드와 백그라운드 스레드가 있습니다.

<aside> 🤔 추후에 다룰 것

- MySQL 엔터프라이즈 에디션에서는 전통적인스레드 모델 뿐 아니라 스레드 풀 모델을 사용하고 있습니다. 하나의 스레드가 여러 개의 커넥션 요청을 전담합니다. </aside>

### 4.1.2.1 포그라운드 스레드 (클라이언트 스레드)

- MySQL 서버에 접속된 클라이언트의 수만큼 최소 존재합니다.
- 주로 사용자가 요청하는 쿼리 문장을 처리합니다.
- 사용자가 커넥션을 종료하면 해당 커넥션을 담당하던 스레드는 다시 스레드 캐시로 되돌아갑니다.
    - thread_cache_size 에 따라 이미 스레드 캐시에 일정 이상 있다면 그냥 스레드를 종료합니다.
- 데이터 버퍼나 캐시로부터 데이터를 가져오며 만약 데이터가 없는 경우,직접 데이터파일이나 인덱스 파일로부터 데이터를 읽어와서 처리합니다.

### 4.1.2.2 백그라운드 스레드

InnoDB에서만 존재합니다.

- 수행하는 작업
    - 인서트 버퍼(Insert Buffer)를 병합하는 스레드
    - **로그를 디스크로 기록하는 스레드**
    - **InnoDB 버퍼 풀의 데이터를 디스크에 기록하는 스레드**
    - 데이터를 버퍼로 읽어오는 스레드
    - 잠금이나 데드락을 모니터링하는 스레드

InnoDB에서 쓰기작업은 버퍼링되어 일괄 처리하지만, MyISAM은 사용자 스레드가 쓰기 작업까지 처리하도록 되어 있습니다.

읽기 작업은 절대로 지연되지 않습니다.

<aside> 🤔 그럼 포그라운드 스레드는 디스크에 저장되기 전에 버퍼에서 데이터를 읽기를 시도하는 건가요?

왜 데이터를 버퍼로 읽어오는 스레드가 백그라운드에 따로 있는 건가요?

</aside>

## 4.1.3 메모리 할당 및 사용 구조

- 글로벌 메모리 영역과 로컬 메모리 영역이 있습니다.

### 4.1.3.1 글로벌 메모리 영역

글로벌 메모리 영역의 경우 MySQL 서버가 시작되면서 운영체제로부터 할당됩니다.

모든 스레드에 의해 공유됩니다.

(이미지랑 왜 다르지??? 이미지가 잘못된건가;)

- 테이블 캐시
- InnoDB 버퍼 풀
- InnoDB 어댑티브 해시 인덱스
- InnoDB 리두 로그 버퍼

### 4.1.3.2 로컬 메모리 영역

세션 메모리 영역이라고도 합니다.

MySQL 서버상에 존재하는 클라이언트 스레드가 쿼리를 처리하는데 사용하는 메모리 영역입니다.

각 클라이언트 스레드별로 독립적으로 할당되어 절대 공유되어 사용되지 않습니다.

- 커넥션이 열려잇는동안 계속 할당된 생태로 남아있는 공간 (커넥션 버퍼나 결과 버퍼)
- 실행하는 순간에만 할당했다가 해제하는 공간 (소트 버퍼나 조인 버퍼)

## 4.2 InnoDB 스토리지 엔진 아키텍처
![](https://www.notion.so/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F1feb7462-9c33-4bf1-b0bb-7973d34ffaf2%2Fc03d4922-ef9b-4337-8028-98c94bf68fc3%2FUntitled.png?table=block&id=1a8e6665-dcea-41b3-b352-f540c5263d24&spaceId=1feb7462-9c33-4bf1-b0bb-7973d34ffaf2&width=2000&userId=180a704c-6552-4796-9dd2-ab125439ed98&cache=v2)

### 4.2.1 프라이머리 키에 의한 클러스터링

InnoDB의 모든 테이블은 기본적으로 프라이머리 키를 기준으로 클러스터링 되어 저장됩니다. 이 말은 프라이머리 키 값의 순서대로 디스크에 저장되어 프라이머리키를 이용한 레인지 스캔이 상당히 빠르게 처리될 수 있다는 뜻입니다.

모든 세컨더리 인덱스는 레코드 주소 대신 프라이머리 키의 값을 논리적인 주소로 사용합니다.

쿼리 실행 계획에서도 프라이머리 키를 이용한 쿼리가 세컨더리 인덱스에 비해 비중이 높게 설정됩니다. (선택될 확률이 높음)

### 4.2.2 외래 키 지원

외래 키에 대한 지원은 InnoDB 스토리지 엔진 레벨에서 지원하는 기능입니다. MyISAM이나 MEMORY 테이블에서는 사용할 수 없습니다.

InnoDB에서 외래키는 부모 테이블과 자식 테이블 모두 컬럼 인덱스 생성이 필요하고, 변경시에는 연관된 테이블에 해당 데이터가 있는지 체크하는 작업이 필요하므로 잠금이 여러 테이블로 전파되어 데드락이 발생할 수 있습니다. 이를 주의해야 합니다.

임시로 외래키 체크를 해제하고 빠르게 처리할 수 있습니다.

```sql
SET foreign_key_checks = OFF;
```

### 4.2.3 MVCC(Multi Version Concurrency Control)
![](https://www.notion.so/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F1feb7462-9c33-4bf1-b0bb-7973d34ffaf2%2F241d63d2-c9c6-403e-8cde-40caf562b612%2FUntitled.png?table=block&id=b11f7d04-1e24-4331-bc55-ace84d171dcd&spaceId=1feb7462-9c33-4bf1-b0bb-7973d34ffaf2&width=2000&userId=180a704c-6552-4796-9dd2-ab125439ed98&cache=v2)


레코드 레벨의 트랜잭션을 지원하는 DBMC가 제공하는 기능입니다.

목적은 **잠금을 사용하지 않는 일관된 읽기입니다.**

commit 하기 전까지 InnoDB 버퍼풀과 undo log 메모리에 있다가 트랜잭션 격리수준에 따라 다른 결과값을 보여줍니다.

- READ_UNCOMMITTED 인경우에는 InnoDB 버퍼풀이 가지고 있는 값.
- READ_COMMITTED 이상인 경우에는 undo log에 있는 값

즉, 하나의 레코드에 대해 2개 버전이 유지되고 필요에 따라 어느 데이터가 보여질 지 상황에 따라 달라지는 구조이빈다.

commit된 이후에는 InnoDB는 지금의 상태를 영구적인 데이터로 만듭니다.

하지만 롤백을 하면 언두 영역의 내용을 가져와 사용하므로 commit될 때 undo log를 삭제하지 않습니다. 언두 영역을 필요로하는 트랜잭션이 더 없을 때 비로소 삭제됩니다.

<aside> 🤔 그럼 만약 중간에 강제 종료가 되어버린다면? undo log도 날라가고 롤백할 수가 없게 되는 것인가요? 디스크에 있는 값을 보여주는 것인가요?

undo log와 redo log가 디스크에도 저장된다고 알고 있었는데 둘 다 저장되고 용도가 다른건가요? 혹은 잘못된 지식인가요?

</aside>

### 4.2.4 잠금 없는 일관된 읽기 Non-Locking Consistent Read

MVCC 기술을 이용해 잠금을 걸지 않고 읽기 작업을 수행합니다. (격리수준이 SERIALIZABLE 이 아닌경우)

즉, 다른 트랜잭션이 읽기 작업을 할 수 있는 것인데요. 다만 일관된 읽기를 위해 언두 로그를 삭제하지 못하고 유지하기 때문에 MySQL 서버가 느려질 수 있으므로 트랜잭션을 시작하고나서 가능한 빨리 롤백이나 커밋을 하여 완료하는 것이 좋습니다.


### 4.2.5 자동 데드락 감지

InnoDB 스토리지 엔진의 데드락 감지 스레드로 주기적으로 이 그래프를 검사해 교착 상태에 빠진 트랜잭션 중 하나를 강제 종료합니다.

- 언두 로그를 더 적게 가진 트랜잭션이 롤백의 대상이 됩니다.

InnoDB 스토리지 엔진은 잠금 대기 목록을 그래프(Wait-for List) 형태로 관리합니다.

<aside> 🤔 테이블 레벨의 잠금은 MyISAM 스토리지 엔진이 사용하는 방식인 것 같은데요. 이것도 감지하고 처리하는 것인가요??

</aside>

- `innodb_table_locks` 변수 활성화시 테이블 레벨의 잠금도 감지할 수 있습니다.
- `innodb_deadlock_detect` 이를 off하면 데드락 감지 스레드는 더 작동하지 않습니다.
- `innodb_lock_wait_timeout` 데드락 상황에서 일정 시간이 지나면 자동으로요청이 실패합니다.

# 4.2.6 자동화된 장애 복구

InnoDB 데이터 파일은 기본적으로 MySQL 서버가 시작될 때 항상 자동 복구를 수행합니다.

하지만 복구될 수 없는 손상이라면 MySQL 서버가 종료 되어버린는데요. 이 때 서버 설정 파일의 시스템 변수를 설정해서 강제 시작할 수 있습니다.

- `innodb_force_recovery`
    - 1 : 데이플 스페이스의 데이터나 인덱스 페이지에서 손상된 부분이 발견될 때
    - 2 : 메인 스레드를 시작하지 않고 MySQL 서버를 시작합니다.
        - 메인 스레드는 언두 데이터를 삭제하는 역할을 하는데, 이 과정에서 장애가 나타난 것입니다.
    - 3 : 커밋되지 않은 트랜잭션 작업을 롤백하지 않고 놔둡니다.
    - 4 : insert buffer(인덱스에 대한 부분)의 내용을 무시하고 mysql이 시작합니다.
    - 5 : InnoDB 엔진이 언두 로그를 무시하고 MySQL 서버를 시작합니다. (커밋하지 않은 내용이 커밋된 것처럼 보입니다.)
    - 6 : InnoDB 엔진이 리두 로그를 무시하고 MySQL 서버를 시작합니다. (마지막 체크포인트 시점 데이터만 남게됩니다.)

### 4.2.7 InnoDB 버퍼 풀

- 디스크의 데이터 파일이나 인덱스 정보를 메모리에 캐시해두는 공간입니다.
- 쓰기 작업을 지연시켜 일괄 작업으로 처리하는 버퍼 역할을 합니다.
- 변경된 데이터를 모아서 처리하므로 랜덤한 디스크 작업 횟수를 줄일 수 있습니다.

#### 4.2.7.1 버퍼 풀의 크기 설정

(( p108 을 직접 읽는게 더 낫겠습니다 ))

- MySQL 5.7 부터 InnoDB 버퍼 풀의 크기를 동적으로 조절할 수 있습니다.
- innodb_buffer_pool_size 로 크기를 설정할 수가 있습니다.

#### 4.2.7.2 버퍼 풀의 구조

InnoDB 스토리지 엔진은 버퍼풀을 페이지 크기로 쪼개어 관리합니다.

그게 3가지 자료 구조를 관리하는데요.
![](https://www.notion.so/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F1feb7462-9c33-4bf1-b0bb-7973d34ffaf2%2F22e4041d-a553-4c40-8953-01a8e869424f%2FUntitled.jpeg?table=block&id=ec2fcc95-ed7c-4028-983e-fb512d6f7831&spaceId=1feb7462-9c33-4bf1-b0bb-7973d34ffaf2&width=2000&userId=180a704c-6552-4796-9dd2-ab125439ed98&cache=v2)

- 플러시 리스트
    - 디스크로 동기화되지 않은 데이터를 가진 데이터 페이지 (= 더티 페이지)의 변경 시점 기준의 페이지 목록을 관리합니다.
    - 한번 데이터 변경이 가해졌다면 플러스 리스트에의해 관리되고, 특정 시점에 디스크로 기록됩니다.
    - 데이터가 변경되면 InnoDB는 변경 내용을 리두 로그와 버퍼풀의 데이터 페이지에도 변경 내용을 반영합니다.
- 프리 리스트

#### 4.2.7.3 버퍼 풀과 리두 로그
![](https://www.notion.so/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F1feb7462-9c33-4bf1-b0bb-7973d34ffaf2%2F618aa843-17f8-4454-bd4d-9508fa140fa3%2FUntitled.png?table=block&id=004fde23-7f9a-41f2-9435-94972819cf82&spaceId=1feb7462-9c33-4bf1-b0bb-7973d34ffaf2&width=2000&userId=180a704c-6552-4796-9dd2-ab125439ed98&cache=v2)

- 버퍼풀은 클린페이지와 더티페이지를 함께 가지고 있습니다.
- 여기서 더티페이지는 특정 리두 로그 엔트리와 관계를 가집니다.
- 체크 포인트가 발생하면, 체크포인트 LSN보다 작은 리두 로그 엔트리와 관련된 더티 페이지는 모두 디스크로 동기화됩니다.
    - LST는 리두 로그가 쌓일때마다 계속 증가된 값을 가지게 되는데, 최근 체크포인트의 LSN과 마지막 리두 로그 엔트리의 LSN의 차이를 체크포인트 에이지라고 합니다.

_(( 이 관계를 아직 잘 모르겠어요 ㅜㅜ ))_

### 4.2.7.4 버퍼 풀 플러시

MySQL 5.6버전까지는 더티페이지 플러시 기능이 사용자 쿼리 처리 성능에 영향을 주는 경우가 많았습니다.

하지만 MySQL 버전을 거쳐오면서 더티페이지를 디스크에 동기화하는 더티페이지 플러시 할 때 디스크 쓰기 폭증 현상이 발생하지는 않고 있습니다.

InnoDB 스토리지 엔진은 더티페이지들을 성능 악영향 없이 디스크에 동기화하기 위해 백그라운드로 2가지 플러시 기능을 실행합니다

- 플로시 리스트 플러시
- LRU 리스트 플러시

#### 4.2.7.4.1 플러시 리스트 플러시

플러시 리스트에서 오래전에 변경된 데이터 페이지를 순서대로 디스크에 동기화하는 작업을 수행합니다.

더티 페이지를 디스크로 동기화하는 스레드를 클리너 스레드라고 합니다. (`innodb_page_cleaners` 시스템 변수로 스레드 개수를 조정할 수 있게 해준다)

