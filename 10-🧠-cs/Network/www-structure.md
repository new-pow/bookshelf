# 🕸️ 브라우저에서 도메인을 치면 일어나는 일들
- 인터넷은 어떻게 이루어져 있는가?
## 요약
- DNS lookup time
- TCP connect time
    
- TLS handshake time
    
- TTFB(Time To First Byte)
    
- FCP(First Contentful Paint)
    
- LCP(Largest Contentful Paint)
    
- TTI(Time To Interactive)

- 도메인으로부터 IP + 포트 추출
- 브라우저 캐시 체크
- OS 캐시 체크
- 라우터 캐시 체크
- ISP 캐시 체크
- DNS로 IP 주소 획득
- 브라우저가 TCP/IP 프로토콜을 사용해 서버에 연결
- Firewall & Https/SSL 접근 제한 방법 거침
- Load balancer
- WebServer
- HTML 브라우저 렌더링

## 브라우저 캐시 체크
- 두가지를 저장한다.
- DNS 캐시
	- TTL(Time To Live) 동안 유지
	- 브라우저 종료 시 사라지는 경우가 많음 (Chrome은 별도 DNS 캐시 보유)
- HTTP 리소스 캐시
	- HTML
    - CSS
    - JS
    - 이미지
    - 폰트
    - API 응답 (Cache-Control 허용 시)
- 
## DNS 로 IP 주소 획득
- Domain Name Service
- 재귀 검색 (Recursive search)
- Root domain -> Top-level domain -> second-level domain -> third-level domain
- IP 주소를 찾습니다.

## 브라우저가 TCP/IP 프로토콜을 사용해 서버에 연결
- frame - packet - segment
- 3way handshaking

## Firewall & Https/SSL 접근 제한 방법 거침
- Firewall - 특정 IP 주소나 어떤 지역에서 접근해 오는 신호를 차단할 수 있으며
- Https/SSL - 클라이언트와 서버와의 암호화를 통해 중간에 누가 패킷을 엿듣는 것을 차단

## Load Balancer
- 중간 서버가 트래픽을 고려하여 요청을 서버로 보내줍니다.

## Web Server
- TCP 연결 후, 클라이언트는 데이터를 보내게 되고
- 서버에서는 브라우저 버전, 종류, 쿠키 등을 받아서 이를 바탕으로 요청한 내용을 보냄. HTTP 상태 코드와 함께 보내어 성공/실패 여부의 정보를 알 수 있음.

## HTML 렌더링
- 브라우저는 HTTP 코드와 HTML 을 통해 렌더링을 시작.
- 만약 이미지나 필요한 파일이 있으면, 보내달라고 요청.