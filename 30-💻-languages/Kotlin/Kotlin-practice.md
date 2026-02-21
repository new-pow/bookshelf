# 🏃‍♂️‍➡️ Kotlin 기본기 루틴
- 매일 6 kyu 1문제 이상
- Kotlin 표준 라이브러리 문서 1개 확인
---
## 26-02-21
### 7kyu [Printer Errors](https://www.codewars.com/kata/56541980fa08ab47a0000040/train/kotlin)


---
## 26-02-20
- **7kyu 로 레벨업했다 🎉**
### 7 kyu [Fizz Buzz](https://www.codewars.com/kata/5300901726d12b80e8000498/train/kotlin)
```kotlin
fun fizzBuzz(n: Int): Array<String> {
  return (1..n).map {
      when {
          it % 3 == 0 && it % 5 == 0-> "FizzBuzz" 
          it % 3 == 0 -> "Fizz"
          it % 5 == 0 -> "Buzz"
          else -> it.toString()
      }
  }.toTypedArray()
}
```
- 새로 알게된 것
```kotlin
expect inline fun <T> Collection<T>.toTypedArray(): Array<T>
```

- `CharArray`
	- **원시 타입 배열 (primitive array)**
	- JVM에서는 내부적으로 `char[]`
	- 각 요소가 **primitive char**
    - **박싱(Boxing) 없음**
    - 메모리/성능 효율적

```
char[] 
[a][b][c]
```

- 연속된 2바이트 primitive
- GC 부담 적음

```kotlin
val a: CharArray = charArrayOf('a', 'b', 'c')
```
- `Array<Char>`
	- **제네릭 배열**
	- JVM에서는 `Character[]`
	- 각 요소가 **java.lang.Character (박싱된 객체)**
	- 제네릭 컬렉션과 호환 가능
    - 박싱 비용 발생

```kotlin
val b: Array<Char> = arrayOf('a', 'b', 'c')
```

```
Character[] (참조 배열)
[ref][ref][ref]
   ↓    ↓    ↓
 Character('a')
 Character('b')
 Character('c')
```

- 객체 + 참조
- 메모리 2~3배 이상 사용 가능
- GC 부담 증가

왜 필요할까?
- Kotlin의 제네릭은 primitive를 직접 다룰 수 없음.
- 박싱해서 넘겨야 함.
Kotlin이 primitive 배열을 별도로 둔 이유:
1. JVM의 구조적 한계
2. 박싱 비용 제거
3. 메모리 효율성 확보
4. Java interop 유지
5. 제네릭 타입 시스템 제약

### [6kyu] [Split Strings](https://www.codewars.com/kata/515de9ae9dcfc28eb6000001/train/kotlin)
- mine
	- 마음에 너무 안들어서 작성해둠.
```kotlin
fun solution(s: String): List<String> {
    var curStart = 0
    var curEnd = 2
    
    val result = mutableListOf<String>() // mutableListOf 를 mutableList() 로 헷갈림
    
    if (s.length == 0) return result
    
    while (curEnd < s.length) {
        result.add(s.substring(curStart, curEnd))
        curStart = curEnd
        curEnd = curStart + 2
    }
    
    when {
        s.length % 2 == 0 -> result.add(s.substring(curStart, curEnd))
        else -> result.add(s.substring(curStart, curEnd-1) + "_")
    }
    
    return result
}
```
- 새로 알게된 점
	- [chunked(n)](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/chunked.html
```kotlin
// transform 도 할 수 있다.
fun <T, R> Iterable<T>.chunked(size: Int, transform: (List<T>) -> R): List<R>
```

```kotlin
@SinceKotlin("1.2")
public fun <T> Iterable<T>.chunked(size: Int): List<List<T>> {
    return windowed(size, size, partialWindows = true) // 첫번째 아이디어는 그닥 다르진 않은 듯.
}

@SinceKotlin("1.2")
public fun <T> Iterable<T>.windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): List<List<T>> {
    checkWindowSizeStep(size, step) // 먼저 step 을 측정
    if (this is RandomAccess && this is List) {
        val thisSize = this.size
        val resultCapacity = thisSize / step + if (thisSize % step == 0) 0 else 1
        val result = ArrayList<List<T>>(resultCapacity)
        var index = 0
        while (index in 0 until thisSize) {
            val windowSize = size.coerceAtMost(thisSize - index)
            if (windowSize < size && !partialWindows) break
            result.add(List(windowSize) { this[it + index] })
            index += step
        }
        return result
    }
    val result = ArrayList<List<T>>()
    windowedIterator(iterator(), size, step, partialWindows, reuseBuffer = false).forEach {
        result.add(it)
    }
    return result
}
```

- padStart 도 있고, padEnd 도 있다. 활용할것.
```kotlin
fun solution(s: String): List<String> = 
    s.chunked(2).map { it.padEnd(2, '_') }
```

- 인덱스 기반으로 정리할 수도 있다.
```kotlin
fun solution(s: String): List<String> =
    (0 until s.length step 2).map { i -> // step 2 는 2번식 건너뛴다는 이야기
        s.substring(i, minOf(i + 2, s.length)).padEnd(2, '_') // minOf 로 에러 방지
    }
```

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
