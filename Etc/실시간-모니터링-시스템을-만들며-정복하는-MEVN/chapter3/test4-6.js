const {map, filter, pipe, values, go} = require('fxjs');
const L = require('fxjs'); // fxjs 모듈을 가져온다.

const friends = [
    {"name": "연제호", "do": "군인"},
    {"name": "김재현", "do": "회장"},
    {"name": "윤성용", "do": "개발자"},
    {"name": "박동영", "do": "개발자"},
    {"name": "이준혁", "do": "배우"}
];

const t = friends.map(e => e.do == "개발자" ? e : null).filter(e => e);
const prop = key => obj => obj[key]; // key를 받아 obj[key]를 반환하는 함수를 선언
const propEq = value => key => obj => prop(key)(obj) === value; // value를 받아 key를 받아 obj[key] === value를 반환하는 함수를 선언

console.log(t); // [ { name: '윤성용', do: '개발자' }, { name: '박동영', do: '개발자' }]

const t2 = go( // go 함수를 이용하여 함수 합성
    friends,
    L.filter(propEq("개발자")("do")),
    L.takeAll
)
console.log(t2); // [ { name: '윤성용', do: '개발자' }, { name: '박동영', do: '개발자' }]

