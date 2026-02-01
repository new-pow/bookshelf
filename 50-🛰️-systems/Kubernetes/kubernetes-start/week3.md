---
tags: [systems, kubernetes]
---

# 쿠버네티스 오브젝트
- 거의 모든 쿠버네티스의 오브젝트들은 `상태`를 갖습니다.
- `상태` 란 오브젝트를 추구하는 상태를 기술해 둔 것입니다.
	- 감시하며 차이를 발견하고 상태를 변경합니다.
- 오브젝트는 **추구하는 상태**를 기술해 둔 것
	- Spec, Status
- 상태를 갖는 오브젝트
	- pod
	- service
	- namespace
	- kubesystems
	- vol : 영속적인 데이터를 보유
- 