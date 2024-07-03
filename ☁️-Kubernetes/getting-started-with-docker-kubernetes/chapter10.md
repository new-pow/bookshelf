# 보안을 위한 인증과 인가
- RBAC
	- 특정 명령을 실행할 수 있는 권한을 서비스 어카운트에 부여
- 설치 도구를 이용해 쿠버네티스를 설치할 때 설치 도구가 자동으로 kubectl 이 관리자 권한을 갖도록 설정해둡니다.
	- `~/.kube/config` 라는 파일에서 확인할 수 있습니다.
## 서비스 어카운트와 롤, 클러스터 롤
```
☁  ~  kubectl get svc --as systme:serviceaccount:default:iirin
Error from server (Forbidden): services is forbidden: User "systme:serviceaccount:default:iirin" cannot list resource "services" in API group "" in the namespace "default"
```
- 부여할 권한이 무엇인지 나타내는 쿠버네티스 오브젝트
	- 롤
	- 클러스터 롤
- 롤은 네임스페이스에 속하는 오브젝트이므로 디플로이먼트나 서비스처럼 네임스페이스에 속하는 오브젝트들에 대한 권한을 정의할 때 쓰입니다.
- 클러스터 롤은 클러스터 단위의 권한을 정의할 때 사용합니다.
	- 클러스터 단위의 권한이 무엇이 있지?
	- 네임스페이스에 속하지 않는 오브젝트 뿐 아니라 클러스터 전반에 걸친 기능을 사용하기 위해서
	- 여러 네임스페이스에서 반복적으로 사용되는 권한을 클러스터롤로 만들어 재사용
- 롤 바인딩
	- 롤 바인딩 오브젝트를 통해 특정 대상ㅇ과 롤을 연결해야 합니다.