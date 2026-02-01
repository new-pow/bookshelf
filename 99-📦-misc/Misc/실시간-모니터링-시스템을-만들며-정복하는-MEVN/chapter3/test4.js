const f = c => c + 10

// 고차함수

const a = (a) => {
    return a+10
}

const b = [1,2,3].map(a) // map은 고차함수 중 하나로 함수를 인자로 받아서 실행하는 함수
// 모나드 : 어떠한 값을 받아서 그 값을 가공해서 다시 반환하는 함수

// 함수를 실행하여 반환
const _call  = (a, b) => a() + b() // 함수를 인자로 받아서 실행하는 함수

// 함수를 반환하는 함수
const a1 = val => () => val
const a_lazy = a1(12)
console.log(a_lazy()) // 실행시점을 지연시킬 수 있음
