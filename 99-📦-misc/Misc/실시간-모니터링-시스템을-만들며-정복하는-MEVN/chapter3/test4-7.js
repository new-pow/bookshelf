const g = JSON.parse
const f = k => k.temp
const fg = x => new Promise((resolve, reject) => resolve(x)).then(g).then(f)
const log = x => console.log(x)
fg('{"temp": 36.5}').catch(_ => 'JSON parse is not working').then(log)

const logPrint = console.log
const users = [
    {name: '큰돌'},
    {name: '재호'},
    {name: '우람'},
    {name: '다빈'}
]
const getUserByName = (name) => users.find(u => u.name === name)
