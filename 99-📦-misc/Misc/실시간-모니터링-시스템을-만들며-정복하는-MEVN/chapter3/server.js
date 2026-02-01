const express = require('express'); // express 모듈을 가져온다.
const path = require('path'); // path 모듈을 가져온다.
const app = express(); // express 객체를 app 변수에 담는다.
const _path = path.join(__dirname, './dist'); // _path 변수에 dist 폴더 경로를 담는다.
app.use('/', express.static(_path)); // express.static() 함수를 사용하여 dist 폴더를 정적 파일로 제공한다.
app.listen(12010, () => {
    console.log('lazy 이미지서버 : 12010 시작 http://localhost:12010');
})