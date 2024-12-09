// object의 복사 Object.assign
const a = {"b": 1};
let b = {... a}; // 객체 해체 할당 ...을 사용하면 객체의 요소를 하나씩 할당하는 것이 아니라 객체 전체를 할당하기 때문에 객체의 복사가 가능하다.
let c = Object.assign({}, a); // 객체의 복사를 위해서는 Object.assign을 사용할 수도 있다.
b.b = 2;
c.b = 3;
console.log(a); // { b: 1 }

// 2차 배열의 경우
const a1 = {
    "b": 1,
    "c": {
        "d": 2
    }
};
let b1 = {... a1};
let c1 = Object.assign({}, a1);
c1.c.d = 3;
console.log(a1); // { b: 1, c: { d: 3 } }

// 재귀함수를 이용한 복사
const copy = (obj) => {
    let out = {};
    let value, key;
    for (key in obj) {
        value = obj[key];
        out[key] = (typeof value === 'object' && value != null) ? copy(value) : value; // 재귀
    }
    return out;
}
let d = copy(a1);
d.c.d = 4;
console.log(a1); // { b: 1, c: { d: 3 } }

// JSON.stringify와 JSON.parse를 이용한 복사
let e = JSON.parse(JSON.stringify(a1));
e.c.d = 5;
console.log(a1); // { b: 1, c: { d: 3 } }
