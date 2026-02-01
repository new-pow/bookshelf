const R = require('ramda'); // ramda 모듈을 가져온다.

const a = R.add(1); // R.add() 함수에 1을 인자로 넘긴다. 아직 실행되지 않은 함수를 반환한다.
console.log(a); // [Function: f1] // 함수가 타입인 것을 알 수 있다.
const b = a(2); // a 함수에 2를 인자로 넘겨서 실행한다.
console.log(b); // 3

// 
const addFourNumbers = (a, b, c, d) => a + b + c + d; // 4개의 인자를 받아서 더하는 함수
const curriedAddFourNumbers = R.curry(addFourNumbers); // curry 함수를 이용해서 함수를 커링한다.
const f = curriedAddFourNumbers(1, 2);
const g = f(3);
console.log(g(4)); // 10

//
const curry = fn => a1 => b1 => fn(a1, b1); // 커링을 위한 함수
const f1 = curry((a,b) => a+b); // 커링된 함수
const f2 = f1(1); // 첫번째 인자를 넘겨준다.
const ret = f2(2); // 두번째 인자를 넘겨준다.
console.log(ret); // 3
