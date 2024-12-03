// 제너레이터를 이용한 코루틴
const log = console.log // log 함수에 console.log 함수를 할당
function *map(f, list) { // 제너레이터 함수
    for (const e of list) { // list의 요소를 하나씩 순회
        yield f(e) // yield 키워드로 값을 반환. 제너레이터 함수는 실행을 일시 중단하고 값을 반환한 후 다시 실행을 재개함
        // return f(e) // return 키워드로 값을 반환하면 제너레이터 함수가 종료됨
    }
}
const add = a => a +10
const a = [1, 2, 3, 4, 5]
const customGenerator = map(add, a)
log(customGenerator.next()) // { value: 11, done: false }
log('어떤 로직 1')
log(customGenerator.next()) // { value: 12, done: false }
log('어떤 로직 2')
log(customGenerator.next()) // { value: 13, done: false } // next 메서드를 호출할 때마다 yield 키워드로 반환한 값이 value 프로퍼티에 할당됨
// for 문 안에서 호출 당시의 상태를 유지하면서 값을 반환하는 제너레이터 함수
// 다만 이전으로 갈 수 없고 한 방향인 디ㅜ로만 넘겨줄 수 있어서 정확히 세미 코루틴이라고 함.
