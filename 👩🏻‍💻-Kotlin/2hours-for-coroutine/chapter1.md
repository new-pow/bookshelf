# 들어가기 전에
코루틴은 코틀린을 접하면서 가장 헷갈렸던 개념 중 하나입니다.
그래서 틈틈이 인프런 강의도 보고 이것저것 글도 찾아보며 학습하고 있는데요. 조금씩 학습했던 것들을 정리하며, 더 깊이 남기기 위해 이 글을 작성해보았습니다.
혹시나 부족한 부분이 있다면 댓글로 알려주세요 😀

- 아래 개념을 전제하여 작성하였습니다만 이 글에서 다루고 있지는 않습니다.
	- 프로세스, 스레드
	- 컨텍스트 스위치 context switch
	- 동시성, 병렬성
	- 메모리 스택영역, 힙영역
	- 동기 프로그래밍, 비동기 프로그래밍

# Coroutine
- `co`는 '협력하는'이라는 의미가 있는 접두사입니다. `routine`은 컴퓨터 공학에서 이야기하는 루틴입니다. 즉, 협력하는 함수라는 의미입니다.
- Coroutine은 Kotlin 언어를 개발한 Jetbrain 이 개발자들이 겪는 스레딩 문제를 직관적인 방식으로 해결할 수있도록 추가한 기능입니다.

## Routine 과 Coroutine
- 일반적인 Routine은 다음과 같은 시퀀스 다이어그램을 가집니다.

```
    +--------------+          +-------------+
    | Main Routine |          | sub Routine |
    +--------------+          +-------------+
         |                           |
         |        routine 시작        |
         |-------------------------->|
         |                           |
         |                           |
         |                           |
         |                           |
         |                           |
         |                           | 
         |<--------------------------|
         |        routine 종료       초기화
         |
```

루틴은 이런 특징이 있습니다.
- 루틴은 진입하는 곳이 한 곳입니다.
- 한 번 시작하면 종료될 때까지 멈추지 않습니다.
- 루틴이 종료되면 그 루틴에서 사용했던 정보가 메모리에서 초기화 되어 사용할 수 없습니다. (지역변수)
---
- 아래는 coroutine 에 대한 시퀀스 다이어그램입니다.

```
    +----------------+          +---------------+
    | Main coRoutine |          | sub coRoutine |
    +----------------+          +---------------+
         |                              |
         |         routine 시작          |
         |----------------------------->|
         |             중단              |
         |<-----------------------------|
         |                              |
         |                              |
         |                              |
         |             재개              |
         |----------------------------->|
         |<-----------------------------|
         |         routine 종료
         |
```

- 코루틴은 중단하고 재개할 수 있습니다.
	- 중단-재개의 단위는 코드상으로 정의된 블록입니다.
- 코루틴이 완전히 종료되기 전까지 중단된 코루틴 함수 안에 있는 정보는 메모리에서 제거되지 않습니다.

여기서 코루틴이 완전히 종료되기 전까지 메모리에서 해당 코루틴에 대한 정보가 계속 남아있다는 점이 매우 중요합니다. 이는 스레드와 코루틴의 관계와 차이점을 보면 명확히 알 수 있습니다.

## Thread 와 Coroutine
위에서 언급하였듯이 코루틴은 스레드보다 더 작은 작업단위이기도 합니다.
![](https://miro.medium.com/v2/resize:fit:1400/format:webp/1*fg9-h6CWPz8p_pmHGktpyg.png)

코루틴은 코드 종류 중 하나이기 때문에 이를 실행하려면 스레드가 있어야 합니다. 루틴을 실행하려면 스레드가 배정되어 있어야 하는 것처럼요.

그래서 얼핏 보면 프로세스-스레드와 스레드-코루틴은 비슷한 관계 것처럼 보입니다.
하지만 스레드와 달리 코루틴은 중단과 재개를 할 수 있고 **하나의 코루틴이 스레드 여러개에 거쳐 실행될 수 있다는 차이점이 있습니다.**

위의 시퀀스 다이어그램에서 루틴은 2개가 실행되고 있지만, 3개의 스레드에서 각각 나뉘어 실행될 수 있는 것입니다. (물론, 더 적은 스레드에 나뉠 수도 있습니다.)

```
      A thread
    +----------------+          +---------------+
    | Main coRoutine |          | sub coRoutine |
    +----------------+          +---------------+
         |                              |
         |    routine 시작(B thread)     |
         |----------------------------->|
         |             중단              |
         |<-----------------------------|
         |                              |
         |                              |
         |                              |
         |         재개(C thread)        |
         |----------------------------->|
         |<-----------------------------|
         |         routine 종료
         |
```

- 다시 시퀀스 다이어그램을 그려보자면 이런식입니다.
```
       A thread                    B thread                    C thread
    +----------------+          +-----------------+          +-----------------+
    | Main coroutine |          | Sub coroutine-1 | 동일 코루틴 | Sub coroutine-2 |
    +----------------+          +-----------------+          +-----------------+
         |                              |                            
         |     routine 시작              |                            
         |----------------------------->|                            
         |                              |                            
         |             중단              |                            
         |<-----------------------------|                            
         |                                                           
         |                                                           
         |                                                           
         |                                       재개                 
         |---------------------------------------------------------->|
         |                                                           |
         |<----------------------------------------------------------|
         |         routine 종료                                       
         |                                                           

```

스레드의 컨텍스트 스위칭과 코루틴의 컨텍스트 스위칭의 비용에도 차이점이 있습니다.

- 스레드의 경우
	- 프로세스 내에서 힙 영역을 공유하고 있기 때문에 스레드간 컨텍스트 스위칭할 때는 스택 영역이 교체됩니다.
	- OS에 의해서 컨텍스트 스위칭이 일어납니다.
- 코루틴의 경우
	- 코루틴이 다른 스레드에서 실행될 경우, 스택 영역이 교체됩니다.
	- 코루틴이 같은 스레드에서 실행될 경우, 메모리를 교체할 필요가 없으므로 스레드보다 컨텍스트 스위칭 비용이 적습니다.
	- 코드를 통해 개발자가 코루틴이 다른 코루틴에게 실행을 양보하게 할 수 있습니다. (`yield()` 함수 등 subroutine 간의 상호작용)

## 비동기 프로그래밍을 더 쉽고, 더 가볍게
우리는 효율적인 애플리케이션 성능을 위해 스레드간 컨텍스트 스위칭을 이용하여 비동기 프로그래밍을 구현할 수 있습니다.

하지만 자칫 무분별한 스레드 생성과 컨텍스트 스위칭으로 많은 리소스를 소비하게 될 수 있다는 점. 그리고 코드가 복잡해진다는 단점이 있습니다.

콜백 지옥은 불시에 찾아올 수 있습니다 👀

![](https://miro.medium.com/v2/resize:fit:792/format:webp/0*MDSFS8Zy2WRlSlJ2.gif)
(이미지 출처 : https://medium.com/nerd-for-tech/a-journey-from-callback-hell-to-kotlin-coroutines-episode-1-98b52821b323)

아래는 GPT와 함께 짜본 스레드 카페 예시입니다. `Callback`, `Future` 등 주문을 기다리거나 커피 만드는 것을 기다리는 간단한 비동기 함수인데 코드의 복잡도가 쉽게 올라갑니다.

```java
class Cafe {
    fun takeOrder(order: Order) {
        synchronized(orderQueue) {
            orderQueue.add(order)
        }
    }

    fun makeCoffeeAsync(): CompletableFuture<Order> {
        return CompletableFuture.supplyAsync {
            var order: Order? = null
            synchronized(orderQueue) {
                if (orderQueue.isNotEmpty()) {
                    order = orderQueue.removeAt(0)
                }
            }
            if (order != null) {
                Thread.sleep(2000L) // 커피 만드는 시간
                order
            } else {
                null
            }
        }
    }

    fun serveCoffeeAsync(order: Order) {
        CompletableFuture.runAsync {
            Thread.sleep(500L) // 커피 서빙 시간
            synchronized(completedOrders) {
                completedOrders.add(order)
            }
        }
    }
}

fun main() {
    val cafe = Cafe()
    val executor = Executors.newFixedThreadPool(3)

    val orderTask = CompletableFuture.runAsync {
        cafe.takeOrder(Order("Alice", "Latte"))
        Thread.sleep(1000L)
        cafe.takeOrder(Order("Bob", "Espresso"))
        Thread.sleep(1000L)
        cafe.takeOrder(Order("Charlie", "Cappuccino"))
    }

    val coffeeMakerTask = CompletableFuture.supplyAsync {
        while (true) {
            cafe.makeCoffeeAsync().thenAcceptAsync { order ->
                if (order != null) {
                    cafe.serveCoffeeAsync(order)
                }
            }.join()
        }
    }

    orderTask.thenRunAsync(coffeeMakerTask, executor)

    try {
        Thread.sleep(10000L)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    
    executor.shutdown()
}

```

이런 점을 해결하기 위해서 각 언어마다 고안한 방법들이 등장하였는데요. Kotlin 에서는 Coroutine 이 해결책으로 등장한 것입니다.

코루틴은 Callback이나 블록에서의 예외처리 등을 코드 복잡도를 덜 높이면서 할 수 있도록 돕습니다. 그리고 OS에게 작업 스케줄링을 넘기지 않고, 코드를 통해 개발자가 직접 작업을 스케줄링할 수 있도록 합니다.

---
# Refs.
- https://tech.wonderwall.kr/articles/coroutinedeepdive/
- https://seunghyun.in/android/7/
- 인프런 강의 : 2시간으로 끝내는 코루틴
- https://kotlinlang.org/docs/coroutines-guide.html
