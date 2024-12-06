const func1 = (e, index) => {
    console.log(`${index}번째 요소는 ${e}입니다.`)
}
const func2 = (e, index) => e * 2
const func3 = (prev, curr, index) => prev + curr
const func4 = (e) => e % 2

const a = [1, 2, 3, 4, 5].forEach(func1)
console.log(a) // undefined (forEach는 반환값이 없음)
const b = [1, 2, 3, 4, 5].map(func2)
console.log(b) // [2, 4, 6, 8, 10]
const c = [1, 2, 3, 4, 5].reduce(func3)
console.log(c) // 15
const d = [1, 2, 3, 4, 5].filter(func4)
console.log(d) // [1, 3, 5]
