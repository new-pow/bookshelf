# 👩🏻‍💻 MySQL에서의 threads 그리고 ProcessList_id
## 궁금했던 것
RealMySQL에서 다음과 같은 구절이 나옵니다. (Lock 파트)

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


