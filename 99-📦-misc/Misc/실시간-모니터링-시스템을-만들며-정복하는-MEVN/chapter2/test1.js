function b() {
    var i=3;
    for (var i=0; i<5; i++) {
    }
    return i;
}

console.log(b()); // 5

function b1() {
    for (var i=0; i<5; i++) {
    }
    return i; // 블록 안에서 선언된 변수임에도 불구하고 전역 변수로 선언되어 있어서 에러가 발생하지 않음
}

console.log(b1()); // 5

// function b2() {
//     for (let i=0; i<5; i++) { // let으로 선언하면 블록 안에서만 유효한 변수가 됨
//     }
//     return i; // 블록 안에서 선언된 변수이기 때문에 에러가 발생함
// }

// console.log(b2()); // ReferenceError: i is not defined

let a = 1;
if (true) {
    console.log(a); // ReferenceError: Cannot access 'a' before initialization
    let a = 2;
    console.log(a); // 2
}