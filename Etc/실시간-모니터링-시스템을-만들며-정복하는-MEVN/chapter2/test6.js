
let a = 1;
let b = 2;
[a,b] = [b,a] // swap이 안되는 이유는 배열의 해체 할당이기 때문에 배열의 요소를 하나씩 할당하는 것이 아니라 배열 전체를 할당하기 때문
console.log(a); // 2
