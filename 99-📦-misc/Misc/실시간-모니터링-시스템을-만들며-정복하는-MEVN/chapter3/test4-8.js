const log = console.log
const users = [
    {name: '큰돌'}, {name: '재호'}, {name: '우람'}, {name: '다빈'}
]
const getUserByName = (name) => users.find(u => u.name === name) || Promise.reject("객체에 없습니다.")
const g = getUserByName
const f = ({name}) => `${name}이 춤을 춥니다`
const fg = x => new Promise((resolve, reject) => resolve(x)).then(g).then(f)
fg("큰돌1").catch(_=> _).then(log)
fg("큰돌").catch(_=> _).then(log)
