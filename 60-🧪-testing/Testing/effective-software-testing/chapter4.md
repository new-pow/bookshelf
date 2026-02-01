---
tags: [testing]
---

# 계약 설계
- 사전 조건, 사후 조건, 불변식을 설계하는 방법
- 계약과 유효성 검사의 차이점에 대한 이해
## 4.1 사전조건과 사후 조건
- 메서드가 제대로 동작하도록 하는 사전조건
	- ex. 간단한 if 문으로 유효하지 않은 값이 통과되지 않도록 한다.
- 메서드가 산출물로 보장하는 사후조건
	- 무언가 잘못되었다면 예외를 던진다.
- 단언키워드 
	- assert : 작성한바와 같지 않으면 JVM 은 AssertionError 를 던진다.
		- if 대신 사용 가능
- 강한 조건과 약한 조건
	-  약한 조건과 강한 조건을 쓸지에는 정답은 없다.
	- 약한 조건은 다른 클래스가 이 메서드를 쉽게 호출할 수 있도록한다.
	- 강한 조건은 코드에서 발생할 수 있는 실수의 범위를 줄여준다.
## 4.2 불변식
- 사전, 사후 모두의 경우에서 유지되어야 하는 조건
- 불변식은 메서드 실행 도중 유지되지 않을 수 있다. 하지만 결국 불변식이 유지되도록 보장할 필요가 있다.

## 4.3 계약 변경과 리스코프 치환 법칙
- 시스템에 기대하는 동작을 깨뜨리지 ㅇ낳고 자식 클래스를 부모 클래스로 치환할 수 있는 개념을 리스코프 치환법칙이라고 한다.
- 결국 계약이 변경되었을 때 중요한 것은 이를 사용하고 있는 곳에서의 영향이다.

## 4.4 계약에 의한 설계가 테스트와 어떤 관련이 있는가?
- 단언문을 통해 제품 코드에서 버그를 일찍 발견할 수 있다.
- 사전조건, 사후 조건, 불변식은 개발자에게 테스트 대상을 제공한다.
- 명시적인 계약은 소비자의 삶을 편안하게 해준다.

## 4.5 현업에서의 계약에 의한 설계
- 강한 사전조건 vs. 약한 사전조건
	- 명확한 방법은 없다. 전체 맥락을 고려해서 결정을 내려야 한다.
- 입력 유효성 검사인가, 계약인가? 아니면 둘 다인가?
	- 유효성 검사와 계약은 서로 다르기 때문에 둘 다 이루어져야한다.
	- 유효성 검사 : 사용자로부터 들어올 수 있는 불량 데이터나 유효하지 않은 데이터가 시스템에 침투하지 않도록 한다.
	- 계약 : 클래스간의 의사소통이 문제없이 일어나도록 한다. 계약 위반이 일어난다면 프로그램은 중지한다.
	- 각 상황에서 가장 나은 바를 결정하기 위해 맥락을 고려하자.
		- 어떤 부분에서는 이미 수행했기 때문에 중복하지 않아도된다.
		- 반면 약간의 중복과 입력 유효성 및 계약 검사를 수행할 때 중복하는 것을 감수해야할 필요도 있다.
-  https://stackoverflow.com/questions/5049163/when-should-i-use-apache-commons-validate-istrue-and-when-should-i-just-use-th/5452329#5452329
	-  Validate.isTrue와 'assert'는 완전히 다른 목적을 위해 사용됩니다.
	- **assert**  
		- Java의 assert 문은 일반적으로 메서드가 어떤 상황에서 호출될 수 있는지, 그리고 호출자가 나중에 무엇이 참일 것으로 기대할 수 있는지를 (assertion을 통해) 문서화하는 데 사용됩니다. assertion은 선택적으로 런타임에 검사할 수 있으며, 유지되지 않으면 AssertionError 예외가 발생합니다.
		- 설계 계약 측면에서 어설션은 사전 및 사후 조건과 클래스 불변식을 정의하는 데 사용할 수 있습니다. 런타임에 이러한 조건이 유지되지 않는 것으로 감지되면 이는 시스템의 설계 또는 구현 문제를 나타냅니다.
	- **Validate.isTrue**  
		- org.apache.commons.lang.Validate는 다릅니다. 조건을 확인하고 조건이 충족되지 않으면 "IllegalArgumentException"을 throw하는 간단한 JUnit 유사 메서드 세트를 제공합니다.
		- 일반적으로 공개 API가 잘못된 입력에 대해 관대해야 할 때 사용됩니다. 이 경우 계약은 잘못된 입력에 대해 IllegalArgumentException을 throw하도록 약속할 수 있습니다. Apache Validate는 이를 구현하기 위한 편리한 약어를 제공합니다.
		- IllegalArgumentException이 throw되므로 Apache의 Validate를 사용하여 사후 조건이나 불변식을 확인하는 것은 의미가 없습니다. 마찬가지로, 사용자 입력 검증에 'assert'를 사용하는 것은 잘못된 것입니다. 왜냐하면 어설션 확인은 런타임에 비활성화될 수 있기 때문입니다.
	- **둘 다 사용**  
		- 하지만, 다른 목적이기는 하지만 동시에 둘 다 사용할 수 있습니다. 이 경우, 계약은 특정 유형의 입력에 대해 IllegalArgumentException이 발생하도록 명시적으로 요구해야 합니다. 그런 다음 Apache Validate를 통해 구현합니다. 그런 다음 불변식과 사후 조건이 간단히 주장되고, 가능한 추가 사전 조건(예: 객체의 상태에 영향을 미침)도 주장됩니다.

```java
public int m(int n) {
  // the class invariant should hold upon entry;
  assert this.invariant() : "The invariant should hold.";

  // a precondition in terms of design-by-contract
  assert this.isInitialized() : "m can only be invoked after initialization.";

  // Implement a tolerant contract ensuring reasonable response upon n <= 0:
  // simply raise an illegal argument exception.
  Validate.isTrue(n > 0, "n should be positive");

  // the actual computation.
  int result = complexMathUnderTrickyCircumstances(n);

  // the postcondition.
  assert result > 0 : "m's result is always greater than 0.";
  assert this.processingDone() : "processingDone state entered after m.";
  assert this.invariant() : "Luckily the invariant still holds as well.";

  return result;
}
```

- 더 많은 정보:
	- Bertrand Meyer, "계약에 의한 설계 적용", IEEE Computer, 1992( [pdf](http://se.ethz.ch/~meyer/publications/computer/contract.pdf) )
	- Johsua Bloch. _Effective Java_ , 2nd ed., 항목 38. 유효성을 위한 매개변수 확인. ( [구글 도서](http://books.google.com/books?id=ka2VUBqHiWkC&lpg=PA182&ots=yXKkOgn3R4&dq=assertions%20%22effective%20java%22&pg=PA181#v=onepage&q&f=false) )
- 단언과 예외
	- 둘 중하나를 사용해야 하는 경우
	- 많은 개발자가 확인된 예외나 확인되지 않은 예외를 선호한다.
	- 라이브러리나 유틸리티 클래스에 대한 계약을 모델링한다면, 라이브러리를 따른다.
	- 데이터가 이전 레이어에서 정제되었다는 것을 알고 있다면 단언문을 선호, 그러나 정제되어 있는지 모를 경우 예외를 선택한다.
	- 유효성 검사에 대해서는
		- 더 세련된 방법으로 유효성 검사를 모델링하는 것을 선호
			- 전체 오류 목록을 사용자에게 표시하는 것이 더 일반적
			- 많은 코드가 요구되는 복잡한 유효성 검사를 모델링 할 수 있다.
			- 도메인주도설계(에릭 에반스) : 명세패턴
				- https://studynote.oopy.io/books/13#49d0df11-3b0a-4432-a2ba-ec4c9e4df58e
			- 단언문 사용법(존 레기어)
	- 더 전문적이고 의미있는 예외를 사용하는 것이 좋다.
- 예외 vs. 부드러운 반환값
- 계약에 의한 설계를 사용하지 않는 경우
	- 걍 무조건 사용하라.
	- 객체 지향 시스템을 개발하느 ㄴ일은 객체가 제대로 소통하고 협업할 수 있도록 보장하는 것.
	- 테스트로 계약에 의한 설계를 서로 대체할 수 없다.
- 사전 조건, 사후조건, 불변식에 대한 테스트가 필요한가?
	- 유효성 검사에 대해서 자동 테스트를 작성하는 것을 추천.
	- 단언문에 대해서는 비즈니스 규칙을 다루는 다른 테스트에 의해 자연스럽게 수행됨.
	- https://stackoverflow.com/questions/4995471/cobertura-coverage-and-the-assert-keyword/6486294#6486294
		> [설계 계약](http://en.wikipedia.org/wiki/Design_by_contract) 스타일 로 단언을 사용하는 경우 Java 명령문을 실패하게 만드는 테스트를 추가할 필요가 없습니다 `assert`. 사실, 많은 단언(예: 불변식, 사후 조건)의 경우 실패하게 만드는 객체를 생성할 수도 없으므로 이러한 테스트를 작성하는 것은 _불가능합니다_ . 그러나 할 수 있는 것은 불변식/사후 조건을 사용하여 _경계를_ 연습하는 테스트 사례를 도출하는 것입니다 (Robert Binder의 [불변 경계](http://books.google.com/books?id=P3UkDhLHP4YC&pg=PA448&lpg=PA448&dq=robert%20binder%20invariant%20boundaries&source=bl&ots=_Eee5n7ZB7&sig=RDmq6wCNOUdriIww1qO_AHtXEdk&hl=en&ei=qe8fTtKvGI7pOdmDjZYD&sa=X&oi=book_result&ct=result&resnum=1&ved=0CBQQ6AEwAA#v=onepage&q&f=false) 패턴 참조). 하지만 이렇게 하면 단언이 실패하지 않습니다.
		
