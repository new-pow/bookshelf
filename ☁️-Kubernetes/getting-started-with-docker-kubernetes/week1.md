# 쿠버네티스 설치
- 개발용도의 쿠버네티스는 로컬 노드를 스탠드얼론(standalone) 모드로 사용하기 때문에 쿠버네티스의 기능들을 완벽하게 사용해보기 적합하지 않습니다.
	- 여러 서버의 자원을 클러스터링해서 컨테이너를 배치하는 것이 쿠버네티스의 핵심 기능이기 때문
- 쿠버네티스 버전
	- 너무 오래되거나 너무 최신 버전을 사용하지 않는 것이 좋다.

## 쿠버네티스 클러스터 초기화
- 버전 1.24부터 도커엔진을 사용할 수 없습니다.
	- [링크](https://kubernetes.io/docs/setup/production-environment/container-runtimes/)
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

