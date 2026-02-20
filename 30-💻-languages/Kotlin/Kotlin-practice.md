# 🏃‍♂️‍➡️ Kotlin 기본기 루틴
- 6 kyu 1문제
- Kotlin 표준 라이브러리 문서 1개 확인
- 배운 API를 Notion/블로그에 3줄 요약
---
## 26-02-20


---
## 26. 02. 17
## 8 kyu [Convert a Boolean to a String](https://www.codewars.com/kata/551b4501ac0447318f0009cd/train/kotlin)
- ref
	- https://kotlinlang.org/docs/control-flow.html#when-expressions-and-statements
- 새로 알게된 점
```kotlin
fun convert(b: Boolean) = "$b"
// StringBuilder()
    .append(b)
    .toString() // 내부적으로 toString() 호출
```

```kotlin
fun convert(b: Boolean) = b.toString() // 이렇게 쓰는게 실무적으로도 더 괜찮을듯.
```

- 실제로 동작하는 방식
```java
public static String toString(boolean b) {
    return b ? "true" : "false";
}
```

## [8kyu] [get ascii value of character](https://www.codewars.com/kata/55acfc59c3c23d230f00006d/train/kotlin)

- 새로 알게된 점
```kotlin
fun getAscii(c: Char) = c.code // 해당 문자의 Unicode 코드 포인트(Int)
```

```kotlin
c.toInt() // 이것도 동일한 동작. Kotlin 1.5 이전에 주로 사용함.
```

```kotlin
c.hashCode() // 같은 결과값.
```

## [8kyu] [Is the string uppercase?](https://www.codewars.com/kata/56cd44e1aa4ac7879200010b/kotlin)
- ref
	- https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/uppercase.html
- 새로 알게된 점
```kotlin
fun String.isUpperCase() = this == uppercase()
```

```kotlin
fun String.isUpperCase(): Boolean =
    all { !it.isLetter() || it.isUpperCase() }
```

---
## 26.02.19
## [8kyu] [If you can't sleep, just count sheep!!](https://www.codewars.com/kata/5b077ebdaf15be5c7f000077/train/kotlin)
- 다양한 풀이 방법들이 있었음.

```kotlin
fun countingSheep(num: Int) = (1..num).joinToString("") { "$it sheep..." }
```

```kotlin
fun countingSheep(num: Int): String = buildString { for(i in 1..num) append(i, " sheep...") }
```

```kotlin
fun countingSheep(num: Int) = (1..num).fold("") { a, n -> a + "$n sheep..." }
```

## [8kyu] [Total amount of points](https://www.codewars.com/kata/5bb904724c47249b10000131/train/kotlin)
- 새로 알게된 것
```kotlin
fun points(games: List<String>) =
        games.sumBy { // sumOf 로 개선하는 것이 좋음
            val (x, y) = it.split(":")
            when {
                x > y -> 3 // 숫자로 안바꿔도 비교 가능
                x < y -> 0
                else -> 1
            }
        }
```

- https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/sum-by.html
- `sumBy`는 **Int 전용이라는 구조적 한계** 때문에 deprecated 되었고,  이를 일반화한 `sumOf`로 통합됨.
	- `sumOf`는 타입별 오버로드가 존재함.

## [8kyu] [Sum Arrays](https://www.codewars.com/kata/53dc54212259ed3d4f00071c/kotlin)
- (생략)

## [7kyu] [Credit Card Mask]
- 새로 알게된 것
```kotlin
fun maskify(cc: String) = cc.takeLast(4).padStart(cc.length,'#')
```

```kotlin
fun maskify(cc: String): String {
    if(cc.length <= 4){
        return cc
    }
    val maskedPart = "#".repeat(cc.length - 4)
    val visiblePart = cc.takeLast(4)
    return maskedPart  + visiblePart
    
}
```
