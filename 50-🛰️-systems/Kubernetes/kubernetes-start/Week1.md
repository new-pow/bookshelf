---
tags: [systems, kubernetes]
---

# 쿠버네티스가 하는 일
- 컨테이너(Docker 등)를 **관리**해줍니다. 이를 **컨테이너 오케스트레이션**이라고 합니다.

## Docker 가 뭔가요?
- 가상화 환경에 비해서 컨테이너 환경에서 더 많은 애플리케이션을 운용할 수 있습니다.
![](https://private-user-images.githubusercontent.com/103120173/309235833-5b52b060-b6cf-4913-9f40-1bd0402daf21.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MDkyOTA2NTIsIm5iZiI6MTcwOTI5MDM1MiwicGF0aCI6Ii8xMDMxMjAxNzMvMzA5MjM1ODMzLTViNTJiMDYwLWI2Y2YtNDkxMy05ZjQwLTFiZDA0MDJkYWYyMS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwMzAxJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDMwMVQxMDUyMzJaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1jOGY4MjZhZjJmNDVmNTFmYjg4ZjRlMDQyOTAwMTM0M2QxNTEyMmNiY2RkYWRmNGFmNmY2MTE3MWY5MWNiZTAwJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.o6DAkh1mmpEdyRzGzuo2OxaN-93ZQdrRCquFVSH_6UQ)

- 그러나 쿠버네티스를 배우는 데에 도커가 꼭 먼저 알아야 하는 것은 아닙니다. 특히 지금의 쿠버네티스에서는 이미 어플리케이션이 많이 올라와있기 때문에 굳이 도커로 만들어 줄 필요는 없습니다.

# 쿠버네티스는 누가 만들었고 관리하나요?
- Google의 Borg 시스템을 CNCF 클라우드 네이티브 컴퓨팅 파운데이션에다 기부하였고, 이후 CNCF의 관리하에 성장하고 있다.
- 이것이 중요한 이유는 사실 특정 **벤더의 종속성이 없다**는 것!
	- 벤더에 종속적이라면 비용을 지불하거나 시스템이 의존할 수밖에 없는데, `Vendor-neutral`한 쿠퍼네티스의 특징은 현대 IT에서 굉장히 중요한 부분!