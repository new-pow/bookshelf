// 함수 합성
const R = require('ramda');

const f_1 = R.map(a => a + 1);
const f_2 = R.filter(a => a % 2 == 0);
const ret = f_2(f_1([1, 2, 3, 4, 5]));
console.log(ret); // [ 2, 4, 6 ]

// compose
const ret1 = R.compose(Math.abs, R.add(1), R.multiply(2))(-4); // 함수를 합성한다. 우측에서 좌측으로 실행된다.
console.log(ret1); // 7

// pipe
const f = R.pipe(R.negate, R.inc); // 함수를 합성한다. 좌측에서 우측으로 실행된다. 읽기 쉬우므로 pipe를 사용하는 것이 좋다.
console.log(f(3)); // -2

// fx.js 를 이용한 함수 합성
const {map, filter, pipe} = require('fxjs');
const f_pipe = pipe(
    map(a => a + 1),
    filter(a => a % 2)
)
const _ret = f_pipe([1, 2, 3, 4, 5]);
console.log(_ret); // [ 3,5 ]
