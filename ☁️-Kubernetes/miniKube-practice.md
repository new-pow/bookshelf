# 👀 minikube 의  동작 방식
- 밑바닥에 컨테이너 런타임이 있고 (docker) minikube의 노드들은 하나하나가 프로세서 컨테이너로 돌아갑니다.
- 실제로 `minikube start` 하면 다음의 컨테이너가 실행되고 있습니다.
	- `minikube start` 시 보이는 안내문구
```
😄  Darwin 14.2.1 (arm64) 의 minikube v1.32.0
✨  기존 프로필에 기반하여 docker 드라이버를 사용하는 중
👍  minikube 클러스터의 minikube 컨트롤 플레인 노드를 시작하는 중
🚜  베이스 이미지를 다운받는 중 ...
🔄  Restarting existing docker container for "minikube" ...
🐳  쿠버네티스 v1.28.3 을 Docker 24.0.7 런타임으로 설치하는 중
🔗  Configuring bridge CNI (Container Networking Interface) ...
```

```
☁  ~  docker images
REPOSITORY                         TAG       IMAGE ID       CREATED         SIZE
gcr.io/k8s-minikube/kicbase        v0.0.42   62753ecb37c4   3 months ago   1.11GB
```

- Selfhosted, vanilla, native k8s 를 체험하기는 어려울 수 있습니다.
	- EKS, AKS, GKE, NKS, k8s 모두 가상머신 위에서 노드를 구현합니다.

# minikube 명령어는 약간 다릅니다


# ARM64 에서 실습하는 것의 한계
## 희소함
- 이 환경에서 minikube로 실습할 경우 ARM64 컨테이너를 불러와서 쓰게 되는데, 실제로 이 환경에서 동작하는 k8s 는 희소하다.

## 동작 방식이 약간 다름
- `minikube`는 `taint` 설정이 master node 에 걸려있지 않다.

---
그러나 M1, M2 밖에 없는 나는.. ㅜㅜ