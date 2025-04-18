# 테스트 가능성을 위한 설계



모든 소프트웨어 시스템을 테스트할 수 있지만, 어떤 시스템에서는 테스트하기 힘들다.

소프트웨어 시스템은 때때로 테스트를 할 수 있도록 설계되어 있지 않다.

**테스트 가능성** 이라는 말은 테스트 대상 시스템이나 클래스, 메서드에 대해 자동 테스트를 얼마나 쉽게 작성할 수 있는지를 말한다.

> 테스트 가능성을 위한 설계는 체계적인 테스트를 수행하기 위한 핵심사항이다. 코드가 테스트하기 어려우면 테스트를 하지 않으려고 할 것 이다. 테스트 가능성을 위한 설계는 언제 진행해야 할까? 테스트 가능성을 생각해야 하는 적당한 때는 언제일가? **항상 고려해야 한다.**



## 1. 도메인 코드에서 인프라 코드 분리

도메인 코드에서 인프라 코드 분리하기.

```java
package com.likelen.openapi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class InvoiceFilter {

    private List<Invoice> all() {
        try {
            Connection connection = DriverManager.getConnection("db", "root", "");
            PreparedStatement ps = connection.prepareStatement("select * from invoice");
            ResultSet rs = ps.executeQuery();

            List<Invoice> allInvoices = new ArrayList<>();
            while (rs.next()) {
                allInvoices.add(new Invoice(rs.getString("name"), rs.getInt("value")));
            }
            ps.close();
            connection.close();
            return allInvoices;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Invoice> lowValueInvoices() {
        List<Invoice> issuedInvoices = all();
        return issuedInvoices.stream().filter(invoice -> invoice.value < 100).collect(Collectors.toList());
    }

    private class Invoice {

        private final String name;
        private final int value;

        public Invoice(String name, int value) {

            this.name = name;
            this.value = value;
        }
    }
}
```

문제점은

1. 도메인 코드와 인프라 코드가 뒤섞여 있다. lowValueInvoices호출 시 DB 접속을 피할 수 없다. public 메서드를 수행하면서 어떻게 private 메서드를 스텁으로 만들 수 있을까? DB를 다루는 부분은 스텁으로 만들 수 없다.
2. 책임이 클수록 더 복잡해지고 버그가 발생할 가능성이 증가한다. 덜 응집된 클래스는 코드양이 많다. 코드양이 많다는 것은 버그가 발생할 확률이 크다는 뜻. 



이 책에서는 핵사고날 아키텍쳐를 언급하는데, 이 부분에 대해서 논쟁이 있다. 인터페이스를 무조건 만들어야 하는가?

**'모든 포트에 대해 인터페이스를 만들어야 하나요?' 옭고 그른 것은 없고, 모든 것은 상황에 따라 다르며, 실용성이 관건이라는 것을 납득시키고자 한다. 소프트웨어 시스템의 모든 것에 대해 인터페이스를 생성할 필요는 없다. 필자는 구현체가 두 개 이상이 되는 포트에 대해서는 인터페이스를 만든다. 또한 추상적 행위를 표현하는 인터페이스를 만들지 않을 때는, 구체적인 구현에서 구현 세부사항이 유출되지 않도록 한다. 언제나 문맥에 따라 판단하는 실용주의가 최고의 방법이다.**

## 2. 의존성 주입과 제어 가능성

클래스 수준에서 필자는 클래스를 완전히 **제어**할 수 있고(즉 테스트 대상 클래스의 행위를 쉽게 제어할 수 있고), **관찰**할 수 있도록(테스트 대상 클래스에서 무슨 일이 일어나는지 알 수 있고 출력 결과를 검사할 수 있도록)해야 한다는 것.a

제어 가능성은 일반적으로 테스트 스위트(모의 객체, 페이크, 스텝)을 활용한다.

의존성 주입의 경우, 생성자를 통해서 주입하는 것을 설명한다.

핵사고날 아키텍쳐의 포트는 의존성 역전 원칙을 도입한 것인데, 다음과 같은 공식화를 가진다.

- (비지니스 클래스 같은) 고수준 모듈은 저수준 모듈에 의존해서는 안 된다. 이 둘 모두는(인터페이스 같은)추상화에 의존해야 한다.
- 추상화는 세부사항에 의존하면 안 된다. 세부사항(구체적인 사항)은 추상황에 의존해야 한다.

![image-20240127122838067](https://raw.githubusercontent.com/LenKIM/images/master/2024-01-27/image-20240127122838067.png)

우리의 코드는 언제나 가능한 한 추상황에 의존해야 하고 세부사항에 거의 의존하지 않도록 해야 한다. 이 패턴의 이점은 추상화가 저수준의 세부사항보다 덜 취약하고 변경하기 쉽다는 점. 그러나, 모든 것을 인터페이스로 만드는 것은 폼이 많이 든다.

## 3. 클래스 및 메서드를 관찰 가능하게 하기

클래스 수준에서 관찰 가능성은 기능이 기대했던 대로 동작하는지를 얼마나 쉽게 단언할 수 있는가에 관한 것.

1. 예제1 - 단언을 보조하는 메서드 도입
   ```java
   public class ShoppingCart {
     private boolean readyForDelivery = false;
     // 장바구니에 대한 정ㅂ
     
     public void markAsReadyForDelivery(Calendar estimatedDayOfDelivery) {
       this.readyForDelivery = true;
       // ...
     }
     public boolean isReadyForDelivery(){
       return readyForDelivery
     }
   }
   ```

   여기서 isReadyForDelivery 메서드는 테스트 코드 작성시 단언을 보조해주는 역할을 한다.

2. 예제2 - void 메서드의 행위를 관찰하기
   ```java
   public class InstallmentGenerator {
     private InstallmentRepository repo;
     
     public void generateInstallments(ShoppingCart cart, int numberOfInstallments) {
       ...
     }
   }
   ```

   generateInstallments 메서드를 어떻게 테스트할 수 있을까? 모키토를 잘 알고 있다면 모의 객체에 전달된 모든 인스턴스를 얻는 방법(argumentCaptor)을 사용하는 것이다. '테스트 도중에 전달된 모든 인스턴스를 돌려줄래?'

   ```java
   public class InstallmentGeneratorTest {
   	@Mock private InstallmentRepository repository;
     
     @Test
     void checkInstallments() {
       InstallmentGenerator generator = new InstallmentGenerator(repository);
       ShoppingCart cart = new ShoppingCart(100.0);
       generator.generateInstallments(cart,10);
       
       ArgumentCaptor<Installment> captor = ArgumentCaptor.forClass(Installment.class);
       
       verify(repository, times(10)).persist(captor.capture());
       List<Installment> allInstallments = captor.getAllValues();
     }
   }
   ```

   또는 리스트를 반환하게 해서 테스트 코드를 변경할 수도 있다.

    핵심은 실용주의이다. 테스트 가능성을 개선해주는 작은 설계 변경은 괜찮다는 점만 기억하자. 가끔은 변경사항이 코드 설계를 망치지 않을지 판단하기 어려울 수 있다. 시도해보고 마음에 안들면 폐기하자.

## 4. 의존성 전달 방법: 클래스생성자와 메서드 매개변수

## 5. 현업에서의 테스트 가능성 설계

테스트에 귀를 기울이면, 테스트하려는 코드의 설계에 대한 힌트를 얻을 수 있다.

클래스를 훌륭하게 설계하는 일은 객체 지향 시스템에서 어려운 작업. 도움을 더 얻을수록 너 나은 설계를 할 수 있다.

**"테스트가 코드 설계에 대해 피드백을 제공한다"**

1. 테스트는 테스트 대상 클래스의 인스턴스를 생성.
2. 테스트는 테스트 대상 메서드를 호출
3. 테스트는 메서드가 기대한 대로 동작했는지 단언

### 5.1 테스트 대상 클래스의 응집도

응집도란 아키텍쳐상의 모듈, 클래스, 메서드 또는 어떤 요소든지 단 하나의 책임을 가지는 것을 뜻한다. **단일 책임을 결정하는 것은 문맥에 따라 달라진다.**

테스트 코드를 작성하면서 약간의 징후가 있을 수 있는 부분은 다음과 같았다.

- 응집력이 없는 클래스에 대한 테스트 스위트는 거대하다.
- 응집력이 없는 클래스는 크기가 커지는 일을 멈추지 않는다.
  - 계속해서 크기가 커지는 클래스는 SOLID 지침에 있는 단일 책임 원칙/개방 폐쇄 원칙을 모두 어긴다. 

### 5.2 테스트 대상 클래스의 결합

응집력 있는 클래스를 사용하면, 여러 클래스를 조합해서 큰 행위를 구성한다. 하지만 이렇게 하면 결합도가 높은 설계를 하게 될 수 있다. 과도한 결합은 진화를 해칠 수 있다.

**테스트 코드를 통해 결합도가 높은 클래스를 발견할 수 있다.**

- 만약 제품 코드의 클래스를 테스트할 때 수많은 의존성 인스턴스가 필요하다면 이것은 나쁜 징후일 수 있다. 클래스 재설계를 고려하자. 다양한 리팩터링 전략을 사용할 수 있다. 아마 클래스가 구현하고 있는 큰 행위를 두 단계로 나눌 수 있을 것이다.
- ATest클래스에서 어떤 테스트(A 행위에 대한 테스트)가 실패했는데 디버깅 해보니 클래스 B의 문제를 발견하는 경우다. 이 클래스들이 어떻게 결합되어 있고, 어떻게 상호작용하는지, 그리고 그러한 설계 오류를 시스템의 다음 버전에서 예방할 수 있는지 재확인



### 5.3 복잡한 조건과 테스트 가능성

복잡한 조건들을 여러 개의 작은 조건들로 분할하는 방식으로 복잡성을 감소시키는 방법은 문제의 전체적인 복잡성을 감소시키지는 않겠지만 적어도 확산시키지는 않을 것. **Specification 과 Condition 고민하기**



### 5.4 private 메서드와 테스트 가능성

만약 private 메서드를 테스트하고 싶다는 마음이 생긴다면, 이것은 트세트가 우리에게 무엇간를 알려주는 좋은 예시다. 설계 관점에서 private 메서드가 현재 위치에 있어서는 안 된다는 뜻일 수 있다. 



### 5.5 정적 메서드, 싱글톤, 테스트 가능성

정적 메서드는 테스트 가능성에 악영향을 미친다. 따라서 가능하면 정적 메서드를 만들지 않는 것이 좋다. 만약 LocalDate 클래스에서 수행한 것과 같이 추상화를 그 위에 추가하는 방식으로 테스트 가능성이 높이는게 좋다.

![image-20240128234452956](/Users/len/Library/Application Support/typora-user-images/image-20240128234452956.png)

---
테스트 가능성은 단순히 “테스트 코드가 잘 돌아가게 하는 기술”이 아니라, **좋은 설계의 결과물**이다. 테스트가 잘 되도록 설계하는 과정은, 클래스의 책임을 나누고, 의존성을 정리하고, 동작을 명확하게 만드는 방향으로 우리를 이끈다.

> 좋은 설계는 테스트를 쉽게 만든다.  
> 테스트하기 쉬운 코드는 유지보수도 쉬운 코드다.


- 테스트가 힘들면 리팩토링 신호
	- 테스트 작성에 if/loop/mock이 너무 많아지면 SRP 위반 의심
	- void 함수는 테스트를 고려해서 이벤트/콜백/리턴값 설계 고민
- 