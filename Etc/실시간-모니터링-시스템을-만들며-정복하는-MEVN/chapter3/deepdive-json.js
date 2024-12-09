const a = {
    name: 'Lee',
    tall: 180,
}

const b = JSON.stringify(a);
console.log(b); // {"name":"Lee","tall":180}
console.log(typeof b); // string
const c = JSON.parse(b);
console.log(c); // { name: 'Lee', tall: 180 }
console.log(typeof c); // object

// stringify value, replacer, space
const d = JSON.stringify(a, null, 3); // space는 들여쓰기를 의미
console.log(d); //

const replacer = (key, value) => {
    return (typeof value === 'string') ? undefined : value;
};

const e = JSON.stringify(a, replacer, 2); // replacer는 value를 변환하는 함수
console.log(e); //