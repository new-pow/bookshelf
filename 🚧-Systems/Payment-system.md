# 견고한 결제 시스템 만들기
## 결제 시스템 Requests
- 사용자 인증이나 보안을 고려해야한다.
- 결제옵션을 여러가지 다루어야 한다.
- 결제서비스가 처리할 수 있는 트래픽 양
	- 한달 1,000,000 건
	- 1초에 10건 이상 처리할 수 있어야 한다.
- 여러 서비스들의 처리가 필요하다.
	- 다른 서비스들이 장애를 겪더라도 결국 결제는 완료되어야 한다.
	- 즉, 신뢰성(Reliability)와 장애 허용성(Fault Tolerance)가 필요하다.
- 결제 데이터는 일관성(Consistency)가 훼손되어서는 안된다.
	- 동시성으로 인해 결과가 유실되는 등.

## High-Level에서 바라본 결제 시스템
- 결제시스템은 기본적으로 두가지 플로우를 가집니다.
- Pay-in 플로우
	- 구매자가 결제를 진행하고, 그 결제금이 이커머스 법인 계좌로 이체된다.
- Pay-out 플로우
	- 이커머스 법인 계좌에서 판매자에게 돈을 지불하는 과정

## 결제 시스템에 필요한 요소들
![](https://private-user-images.githubusercontent.com/103120173/324243195-67f911ab-afda-4480-8bb9-ddf492bb55cf.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MTM2OTgzMDYsIm5iZiI6MTcxMzY5ODAwNiwicGF0aCI6Ii8xMDMxMjAxNzMvMzI0MjQzMTk1LTY3ZjkxMWFiLWFmZGEtNDQ4MC04YmI5LWRkZjQ5MmJiNTVjZi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwNDIxJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDQyMVQxMTEzMjZaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0zNTAyYmVhNzg2YWYzNDhlMDg3MjUwMTM5Mzg5ZjIzOTFlYjVmN2MzZGM5ODBiNTZiZDgyOWRiN2M0OTYwMGMzJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.OLM6qrP-lr7ffasGeyZtZpazXOyDMt2QVS00iqTyKko)

![](https://private-user-images.githubusercontent.com/103120173/324243428-321eb4c6-1da0-4719-8347-1c8521355c5b.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MTM2OTg1NjUsIm5iZiI6MTcxMzY5ODI2NSwicGF0aCI6Ii8xMDMxMjAxNzMvMzI0MjQzNDI4LTMyMWViNGM2LTFkYTAtNDcxOS04MzQ3LTFjODUyMTM1NWM1Yi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwNDIxJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDQyMVQxMTE3NDVaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT02YzMyYTZkODAxYmZmYzE2NWY0MTQ3MWJhOGZiNWYyODg1OWI5ODIwM2NlMzYxMTQ1MzkwYmNlNzMxYzBjMzkyJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.yENdRSDm0Ss0Tm2DzWGasK91vadstPztmIEvzFrTRJ0)

