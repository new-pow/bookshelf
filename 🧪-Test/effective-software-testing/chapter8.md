# 테스트 주도 개발
전통적인 개발 방법론은. 먼저 구현을 한다. 그러고 나서 구현을 한 뒤에 꼭 테스트를 한다.
반대는 하지 않는다.

**왜일까?**



테스트 주도 개발은 **'코드를 약간 작성하고 테스트한다'** 라는 기존의 코딩 방식에 도전한다.

### 1. 첫번째 TDD 세션

**TDD의 예를 들어 로마 숫자를 정수형으로 바꾸는 프로그램이라 가정해보자.**

- I, unus, 1
- V, quinque, 5
- X, decem, 10
- L quiquaginta, 50
- C, cemtum, 100

여기에는 두 가지 규칙이 있다.

- **오른쪽에 있는 숫자가 더 작거나 같은 값을 가지면 더 높은 값을 가진 숫자에 더한다.**
- **왼쪽에 있는 숫자가 더 작은 값을 가지면 더 높은 값을 가진 숫자에서 차감한다.**

예를 들어 XV는 15(10+ 5). XXIV는 24(10 + 10 - 1 + 5)

먼저 예시를 만드는 일은 TDD의 일부이다

- 단순하게 단일 문자를 사용한 경우
- 숫자가 여러 문자로 이루어진 경우(뺄셈 규칙 사용 X)
- 간단한 뺄셈 규칙 사용
- 숫자가 여러 문자로 이루어져 있고, 뺄셈 규칙을 사용하는 경우
  - 입력값: "XIV", 기댓값: 14
  - 입력값: "XXIX", 기댓값: 29

**과정은?**

1. 우리가 만든 예시 목록에서 가장 간단한 예를 선택
2. 주어진 입력과 기댓값에 대한 단언문으로 프로그램을 수행하는 자동 테스트 작성. 이 시점에서 코드는 심지어 컴파일되지 않을 수도 있다. 테스트를 실행하면 실패할 것. 기능을 아직 구현하지 않았기 때문
3. 테스트를 통과할 수 있을 만큼 제품 코드 작성
4. 작업을 멈추고 지금까지 수행한 작업 확인. 제품 코드 개선. 테스트 코드 개선. 목록에 예시 추가
5. 이를 반복.

```java
package io.agistep.contractual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RomanNumberConverterTest {

    @Test
    void shouldUnderstandSymbolTest() {
        RomanNumberConverter roman = new RomanNumberConverter();
        int number = roman.convert("I");
        assertThat(number).isEqualTo(1);
    }

    @Test
    void shouldUnderstandSymbol_V_Test() {
        RomanNumberConverter roman = new RomanNumberConverter();
        int number = roman.convert("V");
        assertThat(number).isEqualTo(5);
    }

    @ParameterizedTest
    @CsvSource({"I,1", "V,5", "X,10", "C, 100", "D, 500", "M, 1000"})
    void parmasTest(String num, int number) {
        RomanNumberConverter roman = new RomanNumberConverter();
        int cNumber = roman.convert(num);
        assertThat(number).isEqualTo(cNumber);
    }

    @Test
    void 여러문자로_이뤄진_로마숫자() {
        RomanNumberConverter roman = new RomanNumberConverter();
        int xxx = roman.convert("II");
        assertThat(xxx).isEqualTo(2);
    }

    private class RomanNumberConverter {

        private static final Map<String, Integer> table = new HashMap<>() {{
            put("I", 1);
            put("V", 5);
            put("X", 10);
            put("L", 50);
            put("C", 100);
            put("D", 500);
            put("M", 1000);
        }};

        public int convert(String i) {
            int finalNumber = 0;

            for (int j = 0; j < i.length(); j++) {
                String key = i.charAt(j) + "";
                finalNumber += table.get(key);
            }
            return finalNumber;
        }
    }
}
```



### 2. TDD 에 대한 고찰

1. 구현하고자 하는 기능 조각에 대한 (단위)테스트를 작성. 테스트 실패
2. 기능을 구현. 테스트 통과
3. 제품 코드와 테스트 코드 리팩터링

이 TDD 프로세스를 **빨강-초록-리팩터 주기**

TDD 실무자들은 이 접근법이 개발 과정에서 다음과 같은 이점이 있다고 말한다.

- 요구사항을 먼저 살펴본다.

  - 기본적으로 요구사항을 수행.
  - 테스트 코드를 작성할 때마다 프로그램이 해야할 일과 하지 말아야 할 일을 반영

- 제품 코드 작성 속도를 완전히 제어한다.

  - 풀고자 하는 문제를 잘 알고 있으면 더 복잡한 테스트 케이스를 한 번에 작성할 수 있다.하지만 문제를 어떻게 해결해야 할지 확실하지 않다면 작은 부분으로 나누어서 간단한 부분에 대해 먼저 테스트 작성

- 피드백이 빠르다.

  - TDD 주기로 일하지 않는 개발자들은 큰 덩어리의 제품 코드를 만들고 나서야 피드백을 얻음. TDD 주기를 사용하면 개발자들은 한 번에 하나씩 수행한다. 테스트를 하나 작성하고, 이를 통과시키고, 다시 고찰. 고찰을 통해 발생할 수 있는 새로운 문제를 쉽게 발견. 이전 주기에서 모든 것을 통제하고 있고, 그 이후로 적은 양의 코드를 작성했기 때문

- 테스트 가능한 코드를 작성한다.

  - 테스트를 먼저 작성하면 제품 코드를 구현하기 전에 처음부터 테스트를 할 수 있는 방법 고민. 기존의 개발 흐름에서는 종종 기능 개발 단계 이후가 되어서야 테스틀 고민. 이 시점에서 테스트를 보조하도록 코드를 바꾸려면 비용이 많이 든다.

- 설계에 대한 피드백을 얻을 수 있다.

  - 테스트 코드를 종종 개발 중인 클래스나 구성요소의 첫번째 클라이언트.

  https://www.jamesshore.com/v2/projects/lets-play-tdd



### 3. 현업에서의 TDD

1. TDD인가 아닌가?
   - TDD 를 하지 않아도 동일한 이점을 누릴 수 있다고 한다. 그러나 필자는 TDD가 주는 리듬에 감사하다고 말한다. 다음으로 개발할 가장 간단한 기능을 찾고, 그에 맞는 테스트를 작성하고, 필요한 만큼만 구현하고, 작업 내용을 고찰하는 일은 필자의 개발 속도에 따라 조절할 수 있다. TDD 는 혼란과 좌절의 무한 루프에서 빠져나오도록 해준다.
   - 클래스를 설계하는 일은 어려운 편에 속하는데, TDD 덕분에 개발 초기부터 코드를 수행해볼 수 있다.
2. 항상 TDD를 사용해야 할까?
   - **실용적** 으로 '아니다' **개발할 기능을 내가 얼마나 알아야 할지에 따라 다르다.**
     - 설계나 아키텍쳐, 특정 요구사항에 대한 구현 방법이 명확하지 않을 때 TDD를 사용한다. 이러한 경우에는 조금 천천히 진행하면서 여러 가능성을 실험하는 것이 좋다. 잘 알고 있는 문제를 작업하고 있다면 문제 해결 방안을 이미 알고 있으므로 몇몇 단계 생략
     - 필자는 복잡한 문제를 다루거나 그 문제에 관한 전문성이 부족할 때 TDD 사용. 구현 난이도가 있는 경우에 TDD를 적용하면 한 걸음 뒤로 물러서서 요구사항을 학습하고 작은 테스트부터 작성하게 해준다.
     - **개발 과정에서 배울만한 게 없으면 TDD를 사용하지 않는다.** 이미 어떤 문제와 그 해결책을 잘 알고 있다면 편안하게 해결 방안을 바로 코딩한다.
   - **TDD는 설계관점(원하는 대로 구조화되었는가)에서뿐만 아니라 구현 관점(코드가 필요한 일을 하고 있는가)**에서 작성 중인 코드를 배울 수 있도록 해준다. 하지만 일부 복잡한 기능에 대해서는 어떤 테스트를 먼저 작성할지 결정하기 어렵다. 이 경우 TDD를 사용하지 않는다.



우리는 멈춰 서서 지금 무엇을 하고 있는지 생각할 수 있는 도구가 필요하다.

TDD는 그 목적을 위한 완벽한 접근 방식이지만, 유일한 방법은 아니다. 언제 TDD를 사용할지 결정하는 일은 경험이 필요하다.

---
-  **실패하는 테스트가 말해주는 것**
	- 실패하는 테스트는 그 자체로 **학습 도구**가 될 수 있어. 왜 실패했는지를 파고들다 보면 숨겨진 문제를 미리 발견함.
	- 특히 병렬성, 동기화, 캐싱, 외부 API 등 복잡한 로직을 다룰 땐 **“테스트 실패 패턴 분석”**이 좋은 디버깅 루틴이 돼.
- 