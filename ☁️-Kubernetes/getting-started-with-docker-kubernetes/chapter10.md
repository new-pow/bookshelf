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
## 쿠버네티스 API 서버에 접근
- API 서버 엔드포인트 엑세스를 퍼블릭 및 프라이빗으로 해두었을 때
- Amazon EKS 클러스터 엔드포인트 액세스 제어 https://docs.aws.amazon.com/ko_kr/eks/latest/userguide/cluster-endpoint.html
```
☁  iirin-chapter10 [main] ⚡  curl https://D31FD2C8504431DE82C0CA51B34D68B1.gr7.us-west-2.eks.amazonaws.com -k
{
  "kind": "Status",
  "apiVersion": "v1",
  "metadata": {},
  "status": "Failure",
  "message": "forbidden: User \"system:anonymous\" cannot get path \"/\"",
  "reason": "Forbidden",
  "details": {},
  "code": 403
}%
```

- api 서버에 접근하려면 별도의 인증 정보를 HTTP 페이로드에 포함시켜 REST API 요청을 전송해야 합니다.
	- 어떻게 인증정보를 포함시키는 걸까? header?
	- JWT 인증을 사용한다.
- 수동 토큰 생성
	- https://kubernetes.io/docs/tasks/configure-pod-container/configure-service-account/
```
☁  iirin-chapter10 [main] ⚡  export secret_name=iirin
☁  iirin-chapter10 [main] ⚡  export secret_name=iirin-secret-token
☁  iirin-chapter10 [main] ⚡  export decoded_token=$(kubectl get secret $secret_name -o jsonpath='{.data.token}
' | base64 -d)
☁  iirin-chapter10 [main] ⚡  echo $decoded_token
eyJhbGciOiJSUzI1NiIsImtpZCI6IjdYMXRIQ3plcEVlOXZ2SlRGXzZMbEtYOWNKVVFYcWY2clRrRUoyUkZUb1kifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImlpcmluLXNlY3JldC10b2tlbiIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJpaXJpbiIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6ImMzM2YwYjQ2LTBiOGMtNGUyZC1iM2NkLTA0OTRlMjczODIxMyIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmlpcmluIn0.HQu9P6pQXVMoeq4eqyDNwwjyMMx526ZL7h4Fkqge1kYLlvmQyj0xmK87LnADPO90dGSGLVcblvTJsjlZHqT3PnaaAyQlU_fS3KZDIY5MTuiLfGVv88CM4YejEC7SWI5rJYiFc_Rp6id_QIaLUmXqGfHDHCGbH0tf6uX-XSedsFJrPoMExT94eb1LGQpGuF9K-hbuMi2XoBEv46x_mOCrUYciiUgj55u7G9nzWARxO8xfdh5qHKgKreaIwyOKw7M0wMJAOwYT4BNmiHnoSi1FfPSFydbsJVImClibNG9cHLjo1kWfVDdEdPCHjyL-KXQWHRPZ1AMazlqw1k5Y5LDByw
```

```
☁  iirin-chapter10 [main] ⚡  curl https://D31FD2C8504431DE82C0CA51B34D68B1.gr7.us-west-2.eks.amazonaws.com/api
s --header "Authorization: Bearer $decoded_token" -k
{
  "kind": "APIGroupList",
  "apiVersion": "v1",
  "groups": [
    {
      "name": "apiregistration.k8s.io",
      "versions": [
        {
          "groupVersion": "apiregistration.k8s.io/v1",
          "version": "v1"
        }
      ],
      "preferredVersion": {
        "groupVersion": "apiregistration.k8s.io/v1",
        "version": "v1"
      }
    },
    ...
```

- kubectl 에서 사용할 수 있는 기능은 모두 REST API 에서도 동일하게 사용할 수 있습니다.
- 그러나 API 서버로 REST 요청 또한 롤 또는 클러스터 롤을 통해 서비스 어카운트에 권한을 부여하지 않으면 접근이 불가능합니다.

### 클러스터 내부에서 API 서버 접근
- 쿠버네티스 클러스터 내부에서 실행되는 애플리케이션의 경우 `default` 네임스페이스에 `kubernetes` 서비스를 통해 API 서버에 접근합니다.
- API 서버에 접근했던 방식과 동일하게 시크릿 토큰을 HTTP 요청에 담아 kubernetes 서비스에 전달해야만 인증과 인가를 진행할 수 있습니다.
	- 디플로이먼트, 파드는 모두 서비스 어카운트의 시크릿을 자동을 ㅗ내부 마운트 하고 있었습니다.
	- 해당 경로 파일을 보면 시크릿 데이터가 각각 파일로 존재합니다.
```
 Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-ll6rh (ro)
```
- 파드 생성시 yaml 에 아무런 설정도 하지 않으면 자동으로 default 서비스 어카운트 시크릿을 마운트하고, `serviceAccountName` 항목을 별도로 지정하면 특정 서비스 어카운트의 시크릿을 마운트할 수 있습니다.
```
파드 내부 접근
kubectl exec iirin-k8s-python-sdk -it -- bash
```

### docker 이미지 레지스트리 접근을 위한 시크릿 설정
- 이미지 레지스트리 접근할 때마다 
```
☁  iirin-chapter10 [main] ⚡  kubectl describe sa iirin-reg-auth-alicek106
Name:                iirin-reg-auth-alicek106
Namespace:           default
Labels:              <none>
Annotations:         <none>
Image pull secrets:  registry-auth
Mountable secrets:   <none>
Tokens:              <none>
Events:              <none>
```

### kubeconfig 파일에 서비스 어카운트 인증 정보 설정
- 쿠버네티스를 설치하면 kubeconfig 파일에 기본적으로 클러스터 관리자 권한을 가지는 인증서 정보가 저장됩니다.
	- 아무런 제한 없이 쿠버네티스를 사용할 수 있습니다.
- 만약 여러 개발자가 `kubectl` 명령어를 사용한다면 서비스 어카운트를 이용해 적절한 권한을 조절하는 것이 바람직합니다.

#### kubeconfig 파일에 대하여
- 일반적으로 `~/.kube/config` 경로에 있습니다. 필요에 따라 `KUBECONFIG` 셸 환경 변수로 경로를 직접 설정할 수 있습니다.
- `kubectl` 명령어 사용시 kubeconfig의 설정 정보에서 API 서버의 주소와 사용자 인증 정보를 로드하게됩니다.
- 내용
	- clusters
		- 쿠버네티스 API 서버의 접속 정보 목록
	- users
		- 사용자 인증 정보 목록
	- contexts
		- clusters + users 종합하여 최종적으로 사용할 클러스터의 정보를 설정합니다.
		- `kubectl`을 사용하려면 여러 개의 컨텍스트 중 하나를 선택해야 합니다.
- 로컬 개발 환경의 쿠버네티스 컨텍스트, AWS 운영 환경의 쿠버네티스 컨텍스트 등 여러 개의 쿠버네티스 클러스터를 유동적으로 선택해 `kubectl` 명령어를 사용할 수 있습니다.
- 현재 어떤 컨텍스트를 사용중인지 `kubeconfig`의 `current-context` 항목에서 확인할 수 있습니다.

## 유저와 그룹
- 왜 따로 사용하는 것일까?
	- 유저는 '사용자'를 나타내는 개념
	- 오브젝트는 아니다.
- `system:serviceaccounts`
	- 특정 네임스페이스의 모든 서비스 어카운트
		- `system:serviceaccounts:{namespace}`
	- API 서버 인증에 성공한 그룹
		- `systme:authenticated`
	- 인증에 실패한 유저
		- `system:anonymous`
- 쿠버네티스에 의해 미리 정의된 유저나 그룹은 접두어로 `system:` 사용
### x509 인증
- 별도의 서버 인증 서버를 사용하면 깃허브 계정, 구글 계정, LDAP 데이터 등을 쿠버네티스 사용자 인증에 사용할 수 있습니다
- Oauth -> Dex
- Webhook -> Guard
- 별도 인증 서버는 쿠버네티스가 아닌 별도의 솔루션을 사용해 구축하는 것이 일반적
- x509 인증서를 이용한 인증 방법은 몇 가지 한계점이 있어서 실제 환경에서 사용하기 어렵다
	- 쿠버네티스에서 인증서가 유출됐을 때 하위 인증서를 파기하는 기능이 없음
	- 파일로 인증 정보를 관리하는 것은 보안상 바람직하지 않음
- 실제로 클러스터를 운영할 때는 덱스Dex나 가드 Guard 등의 솔루션을 이용해 깃허브 등과 같은 서드 파티에서 인증 정보를 관리하는 것이 더욱 효율적이다.