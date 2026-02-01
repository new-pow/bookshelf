const a_ES6 = () => 1 // ES6 화살표 함수
console.log(a_ES6()) // 1

// 기본 매개변수
const b_ES6 = (c = 3) => c
console.log(b_ES6()) // 3

// setTimeout
function arrow() {
    setTimeout(() => {
        console.log(this) // this는 상위 스코프인 arrow 함수를 가리킴. 화살표 함수가 아닌 일반 함수로 작성하면 window 객체를 가리킴
    }, 1000)
}
const p1 = new arrow() // arrow {}
const p2 = new arrow() // arrow {}