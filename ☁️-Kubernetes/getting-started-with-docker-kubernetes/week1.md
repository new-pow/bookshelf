# 쿠버네티스 설치
- 개발용도의 쿠버네티스는 로컬 노드를 스탠드얼론(standalone) 모드로 사용하기 때문에 쿠버네티스의 기능들을 완벽하게 사용해보기 적합하지 않습니다.
	- 여러 서버의 자원을 클러스터링해서 컨테이너를 배치하는 것이 쿠버네티스의 핵심 기능이기 때문
- 쿠버네티스 버전
	- 너무 오래되거나 너무 최신 버전을 사용하지 않는 것이 좋다.

## 쿠버네티스 클러스터 초기화
- 버전 1.24부터 도커엔진을 사용할 수 없습니다.
	- [링크](https://blog.hyojun.me/5)
- 관련 오류
```
kubeadm init --apiserver-advertise-address 172.31.0.100 --pod-network-cidr=192.168.0.0/16 --cri-socket /run/containerd/containerd.sock

W0417 12:57:50.246946    2871 initconfiguration.go:125] Usage of CRI endpoints without URL scheme is deprecated and can cause kubelet errors in the future. Automatically prepending scheme "unix" to the "criSocket" with value "/run/containerd/containerd.sock". Please update your configuration!
[init] Using Kubernetes version: v1.29.4
[preflight] Running pre-flight checks
	[WARNING Swap]: swap is supported for cgroup v2 only; the NodeSwap feature gate of the kubelet is beta but disabled by default
	[WARNING FileExisting-socat]: socat not found in system path
	[WARNING SystemVerification]: missing optional cgroups: hugetlb
error execution phase preflight: [preflight] Some fatal errors occurred:
	[ERROR Port-6443]: Port 6443 is in use
	[ERROR Port-10259]: Port 10259 is in use
	[ERROR Port-10257]: Port 10257 is in use
	[ERROR FileAvailable--etc-kubernetes-manifests-kube-apiserver.yaml]: /etc/kubernetes/manifests/kube-apiserver.yaml already exists
	[ERROR FileAvailable--etc-kubernetes-manifests-kube-controller-manager.yaml]: /etc/kubernetes/manifests/kube-controller-manager.yaml already exists
	[ERROR FileAvailable--etc-kubernetes-manifests-kube-scheduler.yaml]: /etc/kubernetes/manifests/kube-scheduler.yaml already exists
	[ERROR FileAvailable--etc-kubernetes-manifests-etcd.yaml]: /etc/kubernetes/manifests/etcd.yaml already exists
	[ERROR Port-10250]: Port 10250 is in use
	[ERROR Port-2379]: Port 2379 is in use
	[ERROR Port-2380]: Port 2380 is in use
	[ERROR DirAvailable--var-lib-etcd]: /var/lib/etcd is not empty
[preflight] If you know what you are doing, you can make a check non-fatal with `--ignore-preflight-errors=...`
To see the stack trace of this error execute with --v=5 or higher
```

# 쿠버네티스 시작하기
## 쿠버네티스만의 고유한 특징
- 모든 리소스는 오브젝트 형태로 관리된다
- YAML파일을 더 많이 사용한다.
	- 리소스 오브젝트를 정의하는 방법
- 여러개의 컴포넌트로 구성되어 있다

## Pod
- 컨테이너를 다루는 기본 단위
	- 포드는 **1개 이상의 컨테이너로 구성된 컨테이너의 집합**
    - 1개의 포드에서 1개의 컨테이너가 존재할 수도 있고, 여러 개의 컨테이너가 존재할 수도 있다.
- **pod 와 도커 컨테이너의 다른 점**
	- 여러 리눅스 네임스페이스(namespace)를 공유하는 여러 컨테이너들을 추상화된 집합으로 사용할 수 있다.
	- 우분투 컨테이너에서 Nginx 서버를 실행하고 있지 않지만, 로컬호스트에서 Nginx 서버로 접근이 가능
	- 리눅스 네임스페이스란? [참고링크](https://www.44bits.io/ko/keyword/linux-namespace)
		- 프로세스를 실행할 때 시스템의 리소스를 분리해서 실행할 수 있도록 도와주는 기능
		- 한 시스템의 프로세스들은 기본적으로 시스템의 리소스들을 공유해서 실행된다.
	- 네트워크 네임스페이스란? [참고링크](https://www.44bits.io/ko/post/container-network-2-ip-command-and-network-namespace)
		- 컨테이너의 네트워크 환경을 격리해준다
		- 네임스페이스에 속한 프로세스들에 새로운 IP를 부여하거나 네트워크 인터페이스를 추가하는 것이 가능.
- 완전하 애플리케이션으로서의 포드
	- 하나의 포드는 하나의 완전한 애플리케이션
	- 포드에 정의된 부가적인 컨테이너를 사이드카(sidecar) 컨테이너라고 한다.
	- 따라서 포드에 정의된 여러 개의 컨테이너는 하나의 완전한 애플리케이션을 이룬다.
	- 도커 컨테이너와 쿠버네티스 포드의 차이점이기도 하다.
- yml 파일 작성 후 `kubectl apply -f`

## Replica Set
- 일정 개수의 pod를 유지하는 컨트롤러
	- **Controller**중 하나로, **Pod를 복제 생성하고, 복제된 Pod의 개수를 정의된 대로 지속적으로 유지하는 Controller**
- 사용하는 이유
	- 노드 장애 등의 이유로 포드를 사용할 수 없다면 다른 노드에서 포드를 다시 생성한다.
- 동작 원리
	- 라벨 셀렉터를 통한 느슨한 연결(Loosely coupled)
	- 포드를 생성할 때, 부가적인 정보 중 하나로 라벨을 설정할 수 있는데,  spec.selector.matchLabel 에 정의된 라벨을 통해 생성해야 하는 포드를 찾는다.
	- 만약 라벨을 설정하지 않는다면?
- 컨트롤러 vs. 레플리카셋
- 사용자 지정 업데이트 오케스트레이션이 필요하지 않거나 업데이트가 전혀 필요하지 않은 경우가 아니면 ReplicaSet를 직접 사용하는 대신 deployment를 사용하는 것이 좋습니다.

```
apiVersion: apps/v1beta2 
kind: ReplicaSet 
metadata: 
	name: xyz 
spec: 
	replicas: 2
```

## Deployment
- 디플로이먼트는 컨테이너 애플리케이션을 배포하고 관리하는 역할
- 디플로이먼트는 레플리카셋의 상위 오브젝트. 디플로이먼트를 생성하면 해당 디플로이먼트에 대응하는 레플리카셋도 함께 생성
- 

## Service
- 