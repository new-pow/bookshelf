# Pod 실습
## Pod란?
- 컨테이너들의 집합
- 한 가지 일을 하는 것들의 단위
- 보통 컨테이너 하나가 들어있다.

```
# pod 실행
kubectl run nginx --image=nginx

# pod 정보 확인
kubectl get pod -o wide
```

- cluster 내부에서 테스트
![](https://private-user-images.githubusercontent.com/103120173/311544799-d0639f6b-5b61-47d2-8a89-4286c7737422.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MTAxMDU3ODIsIm5iZiI6MTcxMDEwNTQ4MiwicGF0aCI6Ii8xMDMxMjAxNzMvMzExNTQ0Nzk5LWQwNjM5ZjZiLTViNjEtNDdkMi04YTg5LTQyODZjNzczNzQyMi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwMzEwJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDMxMFQyMTE4MDJaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1hOGRmODYwZDk2ZDBjNDEyM2E1YTZmMWY5MDJhYWYxNTdkMTg5NTQ2OGIxMDNjNGJkYjdmODQ1MzA4OGRmMzY3JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.IHgCRqF5pCNfMJbnw3tv6_NyJJjkWACidSFSg4Tl1Ug)

---
- 간이 실습을 위해 kind 환경 구성 [참고링크](https://kind.sigs.k8s.io/?ref=blog.joe-brothers.com)
```
kind create cluster --config kind-example-config.yaml
```

```yaml
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
- role: control-plane
- role: worker
- role: worker
```

# Service 실습
- 배포한 파드를 외부에서 접속하게 하려면? service가 필요하다.
- service가 node port들을 찾아가고, node port에서 pod들이 위치한 곳을 찾아갑니다.
```
☁  ~  curl 10.244.2.2

# 한참 후에
curl: (28) Failed to connect to 10.244.2.2 port 80 after 75002 ms: Couldn't connect to server
```

- nodePort를 노출해보기
```
☁  ~  kubectl expose pod nginx --type=NodePort --port=80
service/nginx exposed
```

```
root@del-deploy-control-plane /
❯ kubectl get service
NAME         TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)        AGE
kubernetes   ClusterIP   10.96.0.1      <none>        443/TCP        12m
nginx        NodePort    10.96.50.160   <none>        80:31068/TCP   99s
```

- 다음에 접속하니 웹사이트도 접속 가능
```
# nodeIp:podPort
☁  kind  curl 192.168.247.5:31068
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
html { color-scheme: light dark; }
body { width: 35em; margin: 0 auto;
font-family: Tahoma, Verdana, Arial, sans-serif; }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
```

## 해당 Pod가 올라간 노드 외에 다른 노드에서도 접속이 가능한 이유
테스트해보신 것 처럼 NodePort로 expose 시, 해당 Pod가 올라간 노드 외 다른 노드에서도 접속이 가능한 이유는

아래 공식문서에서 설명하고 있는 내용과 같이 모든 노드에서 동일한 포트를 서비스로 프록시하기 때문입니다.

- NodePort 유형
	- https://kubernetes.io/ko/docs/concepts/services-networking/service/#type-nodeport
	- 각 노드는 해당 포트 (모든 노드에서 동일한 포트 번호)를 서비스로 프록시한다
---
kubectl get po -o wide를 해 보면 배포된 노드가 어디인지 확인할 수 있습니다. 하지만 현재 사용하고 있는 **NodePort**와 **LoadBalancer**의 경우 쿠버네티스 Cluster라는 단위에 소속되어 있는 모든 노드에 대해서 적용되는 설정이기 때문에 어느 노드에 있다고 해도 접속되는 것입니다. 

이는 사실 Pod는 언제라도 죽을 수 있는 단위이고, 죽게되는 경우 현재 노드에 있으리라는 보장이 없기 때문에 위와 같이 적용하여 사용하는 것입니다.

(스케줄링에 대해서는 다음 강의에서 따로 다루게 되는데 그때 아마 좀 더 심화적으로 이해하실수 있을 것 같습니다. )

만약 필요하다면 특정 노드의 파드(Pod)들만 노출할 수 있는 **Port-forward**, **HostPort** 와 같은 방법도 있긴 합니다. 이는 다음 강의에서 다루겠지만 간단하게 말씀드리면, 현재 내가 위치는 노드에서 그대로 포트를 포워딩해서 사용하게 하거나, 또는 호스트(여기서 노드를 의미)에 포트를 노출하여 사용하는 방법이 있습니다. 

하지만 위와 같이 Pod의 위치가 (기본적으로) 지정되지 않기 때문에 클러스터 단위로 서비스를 노출해주는 것이 일반적이게 됩니다. :)

## node port로 deployment 노출하기
```
root@kind-control-plane:/# kubectl expose deployment deploy-nginx --type=NodePort --port=80

service/deploy-nginx exposed
```



# Deployment
- 파드가 한개밖에 없어서, 그게 죽으면 더이상 그 요청을 받아줄 수 있는 방법이 없습니다.
- 파드를 여러개 만들 필요가 있습니다.
![](https://private-user-images.githubusercontent.com/103120173/311545311-fea9f77e-b1cb-4380-9f17-2d616a059b69.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MTAxMDY0NDcsIm5iZiI6MTcxMDEwNjE0NywicGF0aCI6Ii8xMDMxMjAxNzMvMzExNTQ1MzExLWZlYTlmNzdlLWIxY2ItNDM4MC05ZjE3LTJkNjE2YTA1OWI2OS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwMzEwJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDMxMFQyMTI5MDdaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT05YzM3ZDZjNWRkOTE0ZDZhODE4YzkwZmI0OWRhZGYyOGQzOGViOTRkYjg3NmNiYmMzMmVhMTM0Yzk4NmFjMDFmJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.PILA5KCj7izw2uAdIKxqfcCyFe9L7d_QZXWTxesTzP8)

- `kubectl run` 으로 배포하기 힘들다.
	- 테스트 할 때나 사용하지 보통 사용하지 않는다.
- 그래서 사용하는 것 pod, deployment를 배포하는 다른 방법
	- `kubectl create`
	- `kubectl apply`
		- 파일을 통해 배포하는 방법

```
kubectl create deployment deploy-nginx --image=nginx

kubectl scale deployment deploy-nginx --replicas=3
```

- 다수의 파드를 배포하는 방법
	- `ReplicaSet`
- 기본 replicaSet은 1개입니다.
```
kubectl scale deployment deploy-nginx --replicas=3

❯ kubectl get pods -o wide
NAME                            READY   STATUS    RESTARTS   AGE     IP           NODE                 NOMINATED NODE   READINESS GATES
deploy-nginx-7f979874cf-4kqjx   1/1     Running   0          24s     10.244.3.3   del-deploy-worker    <none>           <none>
deploy-nginx-7f979874cf-hn22v   1/1     Running   0          24s     10.244.2.3   del-deploy-worker2   <none>           <none>
deploy-nginx-7f979874cf-t48jt   1/1     Running   0          4m58s   10.244.1.2   del-deploy-worker3   <none>           <none>
nginx                           1/1     Running   0          7h26m   10.244.2.2   del-deploy-worker2   <none>           <none>
```


# Load balancer
- deployment는 어떻게 노출할까?
```
❯ kubectl expose deployment deploy-nginx --type=NodePort --port=80
service/deploy-nginx exposed
```

- 서비스 말고, 로드밸런서 타입을 정의하자
	- 네이티브 k8s에서는 지원을 안하기 때문에 보통 사용되는 MetalLB를 사용할 예정

## 노드포트보다 로드밸런서가 좋은 점

- 노트포트의 IP를 노출하는 부담이 없다
- 요청 경로를 최적화하여 보내줄 수 있다
```
kubectl create deployment chk-hn --image=
sysnet4admin/chk-hn
deployment.apps/chk-hn created

kubectl scale deployment chk-hn --replica
s=3
deployment.apps/chk-hn scaled

#

kubectl expose deployment chk-hn --type=L
oadBalancer --port=80
service/chk-hn exposed

#
kubectl get services
NAME           TYPE           CLUSTER-IP      EXTERNAL-IP    PORT(S)        AGE
chk-hn         LoadBalancer   10.96.253.167   192.168.1.11   80:30941/TCP   24s
```

## 💣 외부에서 로드밸런서 EXTERNAL-IP 접속 불가한 상황 발생
```
☁  _Lecture_k8s_starter.kit [main] ⚡  kubectl get services
NAME           TYPE           CLUSTER-IP      EXTERNAL-IP    PORT(S)        AGE
chk-hn         LoadBalancer   10.96.253.167   192.168.1.11   80:30941/TCP   12m
```
- 추측
	1. host network 설정이 잘못되었나?
	2. Kind의 특징인가?
- kind
	- http://192.168.247.5:30623/ 노드ip + service port로 접속가능했음
### 원인
- LoadBalancer 타입 특성 상 external IP로 접근 가능하지만, macOS, Windows에서는 docker network를 host에 노출하지 않으므로 여기서는 NodePort를 통해 외부에 노출 (port : 31593.참고로 이 땜시 개고생함. NodePort로 우회하는 전략을 설명하는 문서가 전무. 오직 hint만 kind 공식 문서에 있을 뿐. [macOS의 경우 docker network를 노출하는 방법](https://www.thehumblelab.com/kind-and-metallb-on-mac/)이 있긴 한데, 이 문서의 prerequisite인 tuntap 설치가 macOS에서는 이제 불가)
```
☁  kind  curl 192.168.247.5:30623
chk-hn-6cb6cdf8f8-68xsn
```
- **결국 container 방식의 kind의 한계였음**

---
# 모두 삭제하자

```
kubectl delete {object} {name}
```

---
# ✨ 쿠버네티스는 어떻게 구성되어 있나?
## 네임스페이스 namespce
- 구역을 나누는 네임스페이스
	- `default` : 우리가 주로 사용하는 구역
	- `kube-system` : kubernetes 실행을 위한 구역
- 보는 방법
```
kubectl get pods -n kube-system

NAME                                         READY   STATUS    RESTARTS   AGE
coredns-76f75df574-2lwpb                     1/1     Running   0          104m
coredns-76f75df574-7dfbd                     1/1     Running   0          104m
etcd-kind-control-plane                      1/1     Running   0          104m
kindnet-9vrpc                                1/1     Running   0          104m
kindnet-hmqt8                                1/1     Running   0          104m
kindnet-jgnbw                                1/1     Running   0          104m
kindnet-sz7tx                                1/1     Running   0          104m
kube-apiserver-kind-control-plane            1/1     Running   0          104m
kube-controller-manager-kind-control-plane   1/1     Running   0          104m
kube-proxy-8pkbs                             1/1     Running   0          104m
kube-proxy-bj4gr                             1/1     Running   0          104m
kube-proxy-f2z2p                             1/1     Running   0          104m
kube-proxy-p4f9z                             1/1     Running   0          104m
kube-scheduler-kind-control-plane            1/1     Running   0          104m
```

##  다른 서비스들
- 우리에게 안보여지는 요소들이 있는 master node는 사뭇 다를 수 있으나
- 기본적으로 핵심 구성 요소는 동일하다.
	- AWS - EKS
	- Azure - AKS
	- Google - GKE
- kubesystem 하위에 구성요소들이 동일하게 있다

---
# 쿠버네티스의 기본 철학
- 마이크로서비스 아키텍처 MSA 형태로 구성되어 있다.
	- Microservices Architecture
	- ↔️ 모놀리식 아키텍처
- 선언적인 시스템

![](https://private-user-images.githubusercontent.com/103120173/312494983-fc3ddbef-679c-4f91-8466-27d3438e36ed.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MTAzNDAzMTksIm5iZiI6MTcxMDM0MDAxOSwicGF0aCI6Ii8xMDMxMjAxNzMvMzEyNDk0OTgzLWZjM2RkYmVmLTY3OWMtNGY5MS04NDY2LTI3ZDM0MzhlMzZlZC5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwMzEzJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDMxM1QxNDI2NTlaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT04MjMxZjU0ODZmNjc0MTk2NGFiNmFhM2M4Y2JhZjUyODdkZWI2MjhhMWUxODg0MWIyMjllMjVkYzc4ZGUwNDg0JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.tl1a0ARJ5fxWdVYhX5wo8RqxMIhpLleYnY3d_yhtXVQ)


## 파드가 배포되면 무슨 일이 일어나나?

![](https://private-user-images.githubusercontent.com/103120173/319393390-206e7a12-73ca-4cd7-a017-ab4d5fc08d85.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MTIxODg0NTQsIm5iZiI6MTcxMjE4ODE1NCwicGF0aCI6Ii8xMDMxMjAxNzMvMzE5MzkzMzkwLTIwNmU3YTEyLTczY2EtNGNkNy1hMDE3LWFiNGQ1ZmMwOGQ4NS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwNDAzJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDQwM1QyMzQ5MTRaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1mMWYxOTk2NjFlZDViOGU5MGUzYTM4NjA0NTM1YWY2NDAwODk4OTQyMjUyNTEwNTZjNjUwNWQyYzcyN2UyZGUxJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.4HoSJOOf5GqQJ83dr56FHn8EE-7BYyUZEVdHc7E-AW0)
- 상태를 계속 유지하려고합니다.
- 

---
# 쿠버네티스의 구조 (아키텍처)

![](https://kubernetes-docsy-staging.netlify.app/images/docs/components-of-kubernetes.png)

- 쿠버네티스 클러스터는 컨테이너화된 애플리케이션을 배포, 스케일 및 관리하기 위한 쿠버네티스의 런타임 환경
- 클러스터는 여러 노드로 구성되며, 이러한 노드들은 물리적 또는 가상의 서버들을 나타냅니다.
- 클러스터의 관리 및 조정을 담당하는 **컨트롤 플레인 노드(마스터 노드)**와 실제 애플리케이션 컨테이너가 실행되는 **워커 노드**로 나뉩니다.

## Control Plane
- 컨트롤 플레인은 컨테이너 오케스트레이션의 핵심 요소
- 클러스터를 원하는 상태(Desired State)로 지속적으로 유지 및 관리합니다.
- API 서버, etcd, 컨트롤러 매니저, 스케줄러 등의 컴포넌트로 구성
- 이런 컴포넌트들을 이용하여 클러스터 내의 리소스들을 관리하고 조정하고 있습니다.

### kube-apiserver
- Kubernetes API를 제공하는 쿠버네티스 클러스터의 지휘관과 같은 역할로 모든 요청과 명령이 이곳을 통해 처리됩니다.
	- 상태값을 가지고 있고, 이에 맞춰 다른 컴포넌트들이 움직입니다.
- 엔드 유저와 다른 클러스터 구성 요소는 API 서버를 통해 클러스터와 상호작용 합니다.
- `kubectl`을 사용하여 클러스터를 관리할 때, 내부적으로는 HTTP REST API를 통해 kube-apiserver와 통신하게 됩니다. [참고링크](https://coffeewhale.com/apiserver)
- **controller manager, scheduler 등을 감시 한다**

### etcd
- 분산 키-값 데이터베이스로 설계되어 있습니다.
- 쿠버네티스 클러스터 내의 모든 오브젝트의 상태 정보, 메타데이터 및 설정을 보관하고 있습니다.
- 클러스터 내의 어떤 변화가 전체 클러스터에 일관되게 반영될 것을 보장합니다.
- **`kubectl describe`** 명령어를 사용해 특정 오브젝트의 세부 정보를 조회할 때, 실제로는 etcd에서 해당 정보를 가져옵니다

```bash
# 상세 출력을 위한 Describe 커맨드
kubectl describe nodes my-node
kubectl describe pods my-pod
```

### kube-scheduler[​](http://course.whatapk8s.net/docs/kubernetes-basic/kubernetes-structure#kube-scheduler "Direct link to kube-scheduler")

- 파드를 적절한 워커 노드에 배치하는 책임을 가진 컴포넌트입니다.
- 쿠버네티스 클러스터 내의 여러 노드 중, 새로 생성된 파드가 어느 노드에 실행될지 결정합니다.
- 파드의 요구사항과 각 노드의 현재 상태를 고려해 최적의 노드를 선택합니다.
    - 파드가 요청하는 CPU와 메모리와 비교하여 노드의 현재 가용 자원을 확인
    - 이미 사용 중인 포트와 충돌하지 않도록 노드의 포트 사용 상태도 고려
- 최종적으로 kube-scheduler는 조건을 만족하는 가장 적절한 노드에 파드를 스케줄링

### Controller Manager
- 클러스터의 상태를 지속적으로 모니터링하여 사용자가 원하는 상태(desired state)와 현재 상태(actual state)의 차이를 감지하고 필요한 조치를 취하여 두 상태를 일치시키는 역할을 수행
- 매니저도 여러개
	- `kube-controller-manager`
		- `Replication Controller`
		- `Endpoints Controller`
		- `Service Account & Token Controllers`
		- `Namespace Controller`
	- `cloud-controller-manager`
		-  `Node Controller`
		- `Route Controller`
		- `Service Controller`

## Worker Node
### Kubelet
- **노드 등록**: 워커 노드의 정보와 스펙을 kube-apiserver에 등록시킵니다.
- **파드 수명주기 관리**: API 서버에서 받아온 podSpec을 사용하여 파드를 생성, 업데이트 또는 삭제하는 작업합니다.
    - podSpec은 파드에서 실행할 컨테이너, CPU 및 메모리 등의 리소스 정보, 환경변수 등의 정보를 포함합니다.
- **컨테이너 런타임과의 통신**: 큐블렛은 컨테이너를 실행, 중지, 시작하기 위해 컨테이너 런타임(예: Docker, containerd)과 통신합니다.

### container runtime
- 파드 안에서 컨테이너를 실행시키기 위해서는 컨테이너 런타임이 필요합니다.
- 컨테이너 레지스트리에서 이미지를 가져오고, 컨테이너를 실행시키고, 컨테이너에 리소스를 할당하고, 또한 호스트 상에서의 컨테이너의 전반적인 라이프 사이클을 관리하는데 책임이 있습니다.

### kube-proxy[​](http://course.whatapk8s.net/docs/kubernetes-basic/kubernetes-structure#kube-proxy "Direct link to kube-proxy")

- 쿠버네티스 서비스 오브젝트와 관련된 네트워크 트래픽을 관리
- 클러스터 내의 파드 간 통신이나 외부로부터의 트래픽을 파드로 라우팅할 때 거칩니다.
- 서비스를 사용하면, 파드의 IP 주소와 포트를 직접 알 필요 없이 서비스의 IP와 DNS를 통해 파드에 접근할 수 있는데, 이러한 서비스 요청을 올바른 파드로 전달해주는 역할을 합니다.

---
# Networking
- 서비스, 로드밸런싱, 네트워킹
- 