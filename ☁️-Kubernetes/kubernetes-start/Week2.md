# Pod 실습
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
- 배포한 파드를 외부에서 접속하게 하려면 service가 필요하다.
```
☁  ~  curl 10.244.2.2

# 한참 후에
curl: (28) Failed to connect to 10.244.2.2 port 80 after 75002 ms: Couldn't connect to server
```

- 노출해보기
```
☁  ~  kubectl expose pod nginx --type=NodePort --port=80
service/nginx exposed
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
- 그래서 사용하는 것
	- `kubectl create`
	- `kubectl apply`

```
kubectl create deployment deploy-nginx --image=nginx

kubectl scale deployment deploy-nginx --replicas=3
```


# Load balancer
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

---
# 쿠버네티스는 어떻게 구성되어 있나?
## 네임스페이스 namespce
- `default`
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

