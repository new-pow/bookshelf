---
tags: [systems, database]
---

# 👩🏻‍💻 MySQL에서 `Process`의 의미

## 궁금했던 것 :  단일 프로세스인데 프로세스를 종료할 수 있다고요?
요즘 공부하고 있는 책 `RealMySQL`에서 다음과 같은 구절이 나옵니다. (Lock 파트)

```text
강제 잠금을 해제하려면 KILL 명령을 통해 MySQL 서버의 프로세스를 강제로 종료하면 됩니다.
```

그런데, Process를 죽인다는 것이 가능한 것일까요?
제가 아는 바로는 MySQL은 단일 프로세스이기 때문에 Process를 죽이면 MySQL이 종료되지 않을까 생각했습니다.

![](https://www.notion.so/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F1feb7462-9c33-4bf1-b0bb-7973d34ffaf2%2Ff1f142ee-5ed7-4131-a171-3d6c1a1616e3%2FUntitled.png?table=block&id=f9352e62-d498-4ba7-b4eb-6872708d8b3d&spaceId=1feb7462-9c33-4bf1-b0bb-7973d34ffaf2&width=2000&userId=180a704c-6552-4796-9dd2-ab125439ed98&cache=v2)

그런데 이것은 `Process`라는 용어가 OS와 MySQL DBMS 내부에서 다르게 쓰인다는 것을 모를 때 생각한 의문이었습니다.

이에 대해서 알아본 바를 간단하게 서술해보려고 합니다.

## MySQL은 스레드 기반으로 작동합니다.

> MySQL 서버는 프로세스 기반이 아니라 스레드 기반으로 작동하며, 크게 포그라운드(Foreground) 스레드와 백그라운드(Background) 스레드로 구분할 수 있다.
> 
> *Real MySQL 8.0 80page*

동일한 책에서도 언급되다 시피 MySQL은 스레드 기반으로 작동합니다. 단일 프로세스로 작동되며 이 프로세스의 내붕에서 여러 스레드들이 메모리 자원을 공유하며 동작하게 됩니다.
[참고링크 : 프로세스와 스레드의 차이](https://charlezz.medium.com/process%EC%99%80-thread-%EC%9D%B4%EC%95%BC%EA%B8%B0-5b96d0d43e37)

그래서 이를 기반으로 MySQL 8.0의 공식 문서는 어떻게 언급하고 있는지 찾아보았습니다.

- `mysqld` 실제 서비스 데몬. 데이터베이스 엔진과 상호작용하며 SQL 쿼리를 처리합니다.
    
    > MySQL 설치 시 대부분의 작업을 수행하는 단일 다중 스레드 프로그램입니다. **추가 프로세스를 생성하지 않습니다.** MySQL 서버는 데이터베이스와 테이블이 포함된 MySQL 데이터 디렉터리에 대한 액세스를 관리합니다.
    


- `mysqld_safe` 서버 시작 스크립트
    
    - MySQL이 비정상적으로 종료되는 경우, 다시 기동합니다.
    
    > 오류 발생 시 서버를 다시 시작하고 런타임 정보를 오류 로그에 기록하는 등 일부 안전 기능을 추가합니다.
    > 
    > [https://dev.mysql.com/doc/refman/8.0/en/mysqld-safe.html](https://dev.mysql.com/doc/refman/8.0/en/mysqld-safe.html)

- `mysql.server` 서버 시작 스크립트
    
    > Unix 및 Unix 계열 시스템의 MySQL 배포판에는 **[mysqld_safe를](https://dev.mysql.com/doc/refman/8.0/en/mysqld-safe.html)** 사용하여 MySQL 서버를 시작하는 **[mysql.server](https://dev.mysql.com/doc/refman/8.0/en/mysql-server.html)** 라는 스크립트가 포함되어 있습니다 .

- `mysqld_multi` 여러 MySQL 서버 관리
    
    > 다양한 Unix 소켓 파일과 TCP/IP 포트에서 연결을 수신하는 **[여러 mysqld](https://dev.mysql.com/doc/refman/8.0/en/mysqld.html)** 프로세스를 관리하도록 설계되었습니다 서버를 시작 또는 중지하거나 현재 상태를 보고할 수 있습니다.

결국 `mysqld` 만이 유일한 SQL 쿼리를 처리하는 프로세스라는 문서 내용입니다.
그 프로세스 내에서 포그라운드 스레드와 백그라운드 스레드로 나뉘며 각 스레드의 역할을 수행합니다.
클라이언트의 쿼리 요청을 처리하는 것은 포그라운드 스레드에서 이루어집니다.


![](https://www.notion.so/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F1feb7462-9c33-4bf1-b0bb-7973d34ffaf2%2F0ec12c3c-7287-444a-ab5e-4e68e6f40278%2FUntitled.png?table=block&id=cbd181ce-fd12-43f3-b207-b088c7073dab&spaceId=1feb7462-9c33-4bf1-b0bb-7973d34ffaf2&width=2000&userId=180a704c-6552-4796-9dd2-ab125439ed98&cache=v2)
- 참고 : 80 페이지 MySQL 스레딩 모델

[참고 링크 :  https://dev.mysql.com/doc/refman/8.0/en/mysqld.html](https://dev.mysql.com/doc/refman/8.0/en/mysqld.html)



## 그럼 `processlist`의 정체는 무엇일까?
다음의 쿼리로 MySQL의 processlist를 조회할 수 있습니다.
대체 이 process는 무엇일까요?


```sql
SHOW FULL PROCESSLIST;

+----+---------------+-------------------+-------------+-------+------+----------------------+-----------------------------------------------------------------------------------------+
|ID  |USER           |HOST               |DB           |COMMAND|TIME  |STATE                 |INFO                                                                                     |
+----+---------------+-------------------+-------------+-------+------+----------------------+-----------------------------------------------------------------------------------------+
|1364|root           |192.168.228.1:38667|secondhand-db|Query  |0     |executing             |/* ApplicationName=IntelliJ IDEA 2022.1.3 */ SELECT * FROM information_schema.PROCESSLIST|
|5   |event_scheduler|localhost          |NULL         |Daemon |643686|Waiting on empty queue|NULL                                                                                     |
|1303|root           |192.168.228.1:38417|rss_reader   |Sleep  |17744 |                      |NULL                                                                                     |
+----+---------------+-------------------+-------------+-------+------+----------------------+-----------------------------------------------------------------------------------------+
```

위에서 언급한 OS의 프로세스와는 다르게 이 `processlist`는 MySQL 서버 내에서 실행중인 스레드 집합에 의해 수행되는 작업 단위를 모아둔 리스트입니다.

<aside> 💡 **`processlist`**

MySQL 프로세스 목록은 서버 내에서 실행 중인 스레드 집합에 의해 현재 수행되고 있는 작업을 나타냅니다.

</aside>

- 그래서 process kill 이 가능한 것이지요. OS kill 구문과 같아서 헷갈릴 수 있지만 이 "쿼리"로 진행중인 프로세스를 강제 종료시킬 수 있으며, 이 때 해당 process 수행 중에 획득했던 lock을 다시 반납하게 되는 것입니다.

```sql
kill [process id];
```



### OS의 작업단위인 thread와는 무슨 관계가 있나요?

다음의 쿼리를 통해 현재 MySQL 프로세스 내부에서 작동중인 스레드 리스트를 확인할 수 있습니다.

```sql
select * From performance_schema.threads;

+---------+-------------------------------------------+----------+--------------+----------------+----------------+--------------+-------------------+----------------+--------------------------+-------------------------------------------------------------------------------------+----------------+----+------------+-------+---------------+------------+--------------+----------------+-----------------+---------------------+------------+----------------+----------------+
|THREAD_ID|NAME                                       |TYPE      |PROCESSLIST_ID|PROCESSLIST_USER|PROCESSLIST_HOST|PROCESSLIST_DB|PROCESSLIST_COMMAND|PROCESSLIST_TIME|PROCESSLIST_STATE         |PROCESSLIST_INFO                                                                     |PARENT_THREAD_ID|ROLE|INSTRUMENTED|HISTORY|CONNECTION_TYPE|THREAD_OS_ID|RESOURCE_GROUP|EXECUTION_ENGINE|CONTROLLED_MEMORY|MAX_CONTROLLED_MEMORY|TOTAL_MEMORY|MAX_TOTAL_MEMORY|TELEMETRY_ACTIVE|
+---------+-------------------------------------------+----------+--------------+----------------+----------------+--------------+-------------------+----------------+--------------------------+-------------------------------------------------------------------------------------+----------------+----+------------+-------+---------------+------------+--------------+----------------+-----------------+---------------------+------------+----------------+----------------+
|1        |thread/sql/main                            |BACKGROUND|NULL          |NULL            |NULL            |mysql         |NULL               |643829          |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |1           |SYS_default   |PRIMARY         |480              |66992                |1228598     |1249003         |NO              |
|3        |thread/innodb/io_ibuf_thread               |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |84          |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|4        |thread/innodb/io_read_thread               |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |85          |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|5        |thread/innodb/io_read_thread               |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |86          |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|6        |thread/innodb/io_read_thread               |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |87          |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|7        |thread/innodb/io_read_thread               |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |88          |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|8        |thread/innodb/io_write_thread              |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |89          |SYS_default   |PRIMARY         |0                |0                    |0           |36              |NO              |
|9        |thread/innodb/io_write_thread              |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |90          |SYS_default   |PRIMARY         |0                |0                    |0           |76              |NO              |
|10       |thread/innodb/io_write_thread              |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |91          |SYS_default   |PRIMARY         |0                |0                    |0           |76              |NO              |
|11       |thread/innodb/io_write_thread              |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |92          |SYS_default   |PRIMARY         |0                |0                    |0           |76              |NO              |
|12       |thread/innodb/page_flush_coordinator_thread|BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |643829          |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |93          |SYS_default   |PRIMARY         |240              |240                  |1978        |1978            |NO              |
|14       |thread/innodb/log_checkpointer_thread      |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |95          |SYS_default   |PRIMARY         |0                |0                    |0           |1728            |NO              |
|15       |thread/innodb/log_flush_notifier_thread    |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |96          |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|16       |thread/innodb/log_flusher_thread           |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |97          |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|17       |thread/innodb/log_write_notifier_thread    |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |98          |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|18       |thread/innodb/log_writer_thread            |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |99          |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|19       |thread/innodb/log_files_governor_thread    |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |100         |SYS_default   |PRIMARY         |0                |0                    |0           |120             |NO              |
|24       |thread/innodb/srv_lock_timeout_thread      |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |105         |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|25       |thread/innodb/srv_error_monitor_thread     |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |106         |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|26       |thread/innodb/srv_monitor_thread           |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |107         |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|27       |thread/innodb/buf_resize_thread            |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |108         |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|28       |thread/innodb/srv_master_thread            |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |643829          |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |109         |SYS_default   |PRIMARY         |240              |240                  |1978        |2082            |NO              |
|29       |thread/innodb/dict_stats_thread            |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |643829          |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |110         |SYS_default   |PRIMARY         |240              |240                  |3354        |32434           |NO              |
|30       |thread/innodb/fts_optimize_thread          |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |643829          |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |111         |SYS_default   |PRIMARY         |240              |240                  |2962        |2962            |NO              |
|31       |thread/mysqlx/worker                       |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |1               |NULL|YES         |YES    |NULL           |112         |USR_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|32       |thread/mysqlx/worker                       |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |1               |NULL|YES         |YES    |NULL           |113         |USR_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|33       |thread/mysqlx/acceptor_network             |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |1               |NULL|YES         |YES    |NULL           |114         |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|37       |thread/innodb/buf_dump_thread              |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |118         |SYS_default   |PRIMARY         |0                |0                    |0           |14256           |NO              |
|38       |thread/innodb/clone_gtid_thread            |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |643829          |waiting for handler commit|NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |119         |SYS_default   |PRIMARY         |108512           |108512               |585233      |623781          |NO              |
|39       |thread/innodb/srv_purge_thread             |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |643829          |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |120         |SYS_default   |PRIMARY         |240              |240                  |2266        |43832           |NO              |
|40       |thread/innodb/srv_worker_thread            |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |643829          |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |121         |SYS_default   |PRIMARY         |240              |240                  |3610        |85282           |NO              |
|41       |thread/innodb/srv_worker_thread            |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |643829          |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |122         |SYS_default   |PRIMARY         |240              |240                  |2890        |93178           |NO              |
|42       |thread/innodb/srv_worker_thread            |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |643829          |NULL                      |NULL                                                                                 |NULL            |NULL|YES         |YES    |NULL           |123         |SYS_default   |PRIMARY         |240              |240                  |2074        |43644           |NO              |
|43       |thread/sql/event_scheduler                 |FOREGROUND|5             |event_scheduler |localhost       |NULL          |Daemon             |643829          |Waiting on empty queue    |NULL                                                                                 |1               |NULL|YES         |YES    |NULL           |124         |SYS_default   |PRIMARY         |0                |0                    |16665       |16665           |NO              |
|44       |thread/sql/signal_handler                  |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |1               |NULL|YES         |YES    |NULL           |125         |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|46       |thread/mysqlx/acceptor_network             |BACKGROUND|NULL          |NULL            |NULL            |NULL          |NULL               |NULL            |NULL                      |NULL                                                                                 |1               |NULL|YES         |YES    |NULL           |127         |SYS_default   |PRIMARY         |0                |0                    |0           |0               |NO              |
|47       |thread/sql/compress_gtid_table             |FOREGROUND|7             |NULL            |NULL            |NULL          |Daemon             |643829          |Suspending                |NULL                                                                                 |1               |NULL|YES         |YES    |NULL           |128         |SYS_default   |PRIMARY         |8240             |8240                 |14432       |14496           |NO              |
|1404     |thread/sql/one_connection                  |FOREGROUND|1364          |root            |192.168.228.1   |secondhand-db |Query              |0               |executing                 |/* ApplicationName=IntelliJ IDEA 2022.1.3 */ select * From performance_schema.threads|NULL            |NULL|YES         |YES    |SSL/TLS        |244         |USR_default   |PRIMARY         |1094784          |1107120              |1339154     |1374270         |NO              |
|1343     |thread/sql/one_connection                  |FOREGROUND|1303          |root            |192.168.228.1   |rss_reader    |Sleep              |17887           |NULL                      |NULL                                                                                 |1               |NULL|YES         |YES    |SSL/TLS        |245         |USR_default   |PRIMARY         |1067328          |1107360              |1160552     |1200413         |NO              |
+---------+-------------------------------------------+----------+--------------+----------------+----------------+--------------+-------------------+----------------+--------------------------+-------------------------------------------------------------------------------------+----------------+----+------------+-------+---------------+------------+--------------+----------------+-----------------+---------------------+------------+----------------+----------------+
```

위 조회 결과에서 `THREAD_OS_ID` 가 OS 상에서 할당된 thread에 대한 id 값입니다.

- MySQL 스레드가 수명 기간 동안 동일한 운영 체제 스레드와 연결되어 있는 경우 THREAD_OS_ID에는 운영 체제 스레드 ID가 포함됩니다.
- Windows의 경우 THREAD_OS_ID는 프로세스 탐색기([https://technet.microsoft.com/en-us/sysinternals/bb896653.aspx](https://technet.microsoft.com/en-us/sysinternals/bb896653.aspx))에 표시되는 스레드 ID에 해당합니다.
- Linux의 경우 THREAD_OS_ID는 gettid() 함수의 값에 해당합니다. 이 값은 예를 들어 perf 또는 ps -L 명령을 사용하거나 proc 파일 시스템(/proc/[pid]/task/[tid])에서 노출됩니다.




---
# Refs.
- [27.12.21.8 The threads Table](https://dev.mysql.com/doc/refman/8.0/en/performance-schema-threads-table.html)
- [MySQL Architecture - 3. Thread](https://blog.ex-em.com/1681)