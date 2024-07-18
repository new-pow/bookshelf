# 애플리케이션 배포를 위한 고급 설정
## 파드의 자원 사용량 제한
- 중요한 것
	- 클러스터 내부에서 컴퓨터 자원 활용률(Utilization)을 늘리는 것
	- 각 컨테이너의 자원 사용량을 적절히 제한해야 하며, 남는 자원을 어떻게 사용할 수 있을 지에 대한 전략이 필요합니다.
- yaml에 파드 자체에 자원 사용량을 명시적으로 설정할 수 있습니다.
- node 확인
```
kubectl describe node ip-172-31-30-28.us-west-2.compute.internal
...
```

### 메모리 사용
- `limit` : 파드 자원 사용량 최대 제한
- `Requests` : 적어도 컨테이너에게 보장되어야 하는 자원
	- 자원의 오버커밋을 가능하게 만드는 기능
	- 자원의 오버커밋 : 한정된 컴퓨팅 자원을 효율적으로 사용하기 위한 방법. 사용할 수 있는 자원보다 더 많은 양을 가상 머신이나 컨테이너에게 할당함으로써 전체 자원의 사용률을 높인다.
	- 쿠버네티스에서는 오버커밋을 통해 실제 물리 자원보다 더 많은 양의 자원을 할당하는 기능을 제공합니다.
	- Limit 또한 이에 해당합니다.
- 각 컨테이너가 사용을 보장받을 수 있는 경계선을 정합니다.
- 둘이 합치면
	- 최소한 Request 만큼의 메모리 사용은 보장되지만, 유휴 메모리자원이 있다면 최대 Limit 까지 사용할 수 있습니다.

### CPU 사용
- 메모리와 동일하게 limit, request 가있음.
- CPU 사용량에 경합이 발생하면 일시적으로 컨테이너 내부의 프로세스에 CPU 스로틀이 걸릴 뿐, 컨테이너 자체는 큰 문제가 발생하지 않습니다.
	- https://docs.aws.amazon.com/ko_kr/AmazonRDS/latest/AuroraUserGuide/ams-waits.cpu.html
- 압축 가능한 자원
- 참고 :
	- GDC 에서 리소스 경합 문제 해결
	- https://cloud.google.com/anthos/clusters/docs/on-prem/latest/troubleshooting/resource-contention?hl=ko

### QoS 클래스와 메모리 자원 사용량 제한 원리
- 메모리가 부족할 때
	- 프로세스의 메모리는 이미 데이터가 메모리에 적재되어 있어 CPU와 달리 메모리는 압축 불가능한 자원으로 취급합니다.
	- 쿠버네티스는 가용 메모리를 확보하기 위해 우선순위가 낮은 파드 또는 프로세스를 강제로 종료하도록 설계되어잇습니다.
	- 쿠버네티스에서는 이를 퇴거Eviction라고 표현합니다.
- 어떤 파드나 프로세스가 먼저 종료되어야 하는가?
	- 파드의 우선순위를 구분하기 위해 3가지 종류의 Quality Of Service 클래스를 명시적으로 파드에 설정합니다.
	- 파드의 컨테이너에 설정된 Limits와 Requests 의 값에 따라 내부적으로 우선순위를 계산합니다.
- Conditions
	- 각종 노드의 이상 상태 정보를 의미하는 값
	- MemoryPressure
		- 노드의 가용 메모리가 100Mi 이하일 때 발생하도록 kubelet에 설정되어 있다.
		- 발생할 시 쿠버네티스는 해당ㅊ 노드에서 실행 중이던 모든 파드에 대해 순위를 매긴 다음, **가장 우선순위가 낮은 파드를 다른 노드로 퇴거 Evict 시킵니다.**
	- MemoryPressure 상태를 감지하기 전에 급작스럽게 메모리 사용량이 많아질 경우
		- 리눅스 시스템의 OOM (OUt of memory) Killer 라는 기능이 **우선순위점수가 낮은 컨테이너의 프로세스를 강제로 종료해** 사용 가능한 메모리를 확보할 수 있습니다.
			- oom_score 값에 따라 종료할 프로세스를 선정합니다.
		- kubelet은 기본 OOM 점수가 -999입니다.
		- 프로세스가 메모리를 얼마나 더 많이 사용하고 있는지에 따라 프로세스의 최종 OOM 점수가 갱신됩니다.
#### QoS 클래스 종류
- Guaranteed
	- limits 와 requests 가 동일한 상태
	- 할당받은 자원을 아무런 문제 없이 사용할 수 있다.
	- 자원의 오버커밋이 허용되지 않기 때문에 할당 받은 자원의 사용을 안정적으로 보장 받을 수 있습니다.
	- OOM 점수 -997
		- 도커 데몬이나 kubelet의 프로세스와 거의 동일한 레벨이기 때문에 노드에서 메모리 고갈되더라도 해당 클래스의 파드나 프로세스가 강제로 종료되는 일은 거의 없다.
	- 만약 파드 내에 컨테이너가 여러개 존재한다면 모든 컨테이너의 requests와 limits의 값이 완전히 같아야만 파드가 guaranteed 로 분류됩니다.
- BestEffort
	- limits, requests 를 아무것도 설정하지 않았다면.
	- 노드에 유휴 자원이 있다면 제한없이 모든 자원을 사용할 수 있다. 그렇지만 보장받을 수 있는 자원도 없다.
	- 메모리를 많이 사용할 수록 우선순위가 낮아집니다.
- Burstable
	- limits > requests 로 설정된 파드.
	- requests에 지정된 자원만큼 사용을 보장받을 수 있지만, 상황에 따라서는 limits 까지 자원을 사용할 수 있습니다.
	- 메모리를 많이 사용할 수록 우선순위가 낮아집니다.
- OOM killer에 의해 파드 컨테이너 프로세스가 종료되면 해당 컨테이너는 파드의 재시작 정책에 따라 다시 시작됩니다.

### ResourceQuota와 LimitRange
- 쿠버네티스 오브젝트
- 네임스페이스의 자원 사용량을 제한하고, 자원 할당 기본값이나 범위를 설정합니다.
#### ResourceQuota
- 특정 네임스페이스에서 사용할 수 있는 자원 사용량의 합을 제한할 수 있습니다.
- 네임스페이스별 자원 사용량을 제한하기 위해, 쿠버네티스 클러스터의 자원 고갈을 막기 위해 사용합니다.
```
☁  w10 [main] ⚡  kubectl describe quota
Name:            iirin-resource-quota-example
Namespace:       default
Resource         Used  Hard
--------         ----  ----
limits.cpu       4     1500m
limits.memory    1Gi   1000Mi
requests.cpu     4     1
requests.memory  1Gi   500Mi
```

- 리소스 개수를 제한할 수 있습니다.
	- 디플로이먼트, 파드, 서비스, 시크릿, 컴피그맵, 퍼시스턴트 볼륨 클레임 등의 개수
	- NodePort 타입의 서비스 개수, LoadBalancer 타입의 서비스 개수
	- QoS 클래스 중에서 BestEffort 클래스에 속하는 파드의 개수
- resourceQuota에 limits, cpu나 limits, memory 등을 이용해 네임스페이스에서 사용 가능한 자원의 합을 설정했다면 파드를 생성할 때 반드시 해당 항목을 함께 정의해줘야 합니다.
#### LimitRange
- 특정 네임스페이스에서 할당되는 자원의 범위 또는 기본값을 지정할 수 있는 쿠버네티스 오브젝트
- 컨테이너에 자동으로 기본 Requests, Limits 값을 설정할 수 있습니다.
- 파드 또는 컨테이너의 CPU, 메모리, 퍼시스턴트 볼륨 클레임 스토리지 크기의 최솟값, 최댓값을 설정할 수 있습니다.
- 파드단위로 자원 사용량 범위를 제한할 수도 있습니다.
#### Admission Controller
- 어드미션 컨트롤러
	- 서비스 어카운트
	- 리소스쿼터
	- 리밋 래인지
- 두 단계의 어드미션 컨트롤러가 있다
	- 검증 validating 단계
	- 변형 mutating 단계
- API 요청을 검증하고 기본값등을 추가하여 API 데이터를 변형합니다.

## 쿠버네티스 스케줄링
- 스케줄링
	- 컨테이너를 생성하기 전에 특정 목적에 최대한 부합하는 워커 노드를 선택하는 작업
### 파드가 실제로 노드에 생성되기 전까지
- 스케줄링에 핵심적인 컴포넌트
	- kube-scheduler
	- etcd
- etcd
	- 분산 코디네이터의 일종
	- 클라우드 플랫폼 환경에서 여러 컴포넌트가 정상적으로 상호작용할 수 있도록 데이터를 조정하는 역할
	- API 서버 kube-apiserver 를 통해서만 접근할 수 있다.
- 과정
	- (인증인가 + 변형을 거친후)
	- API 서버가 etcd에 파드 데이터 저장 (node name 은 아직 없음)
	- kube-scheduler 가 API 서버 watch를 통해 nodeName이 없는 파드 데이터가 저장되었다고 전달 받음
	- kube-scheduler가 해당 파드를 할당할 적절한 노드를 선택 후 API 서버에 노드와 파드를 바인딩 요청
	- 파드의 nodeName 항목에 선택된 노드 이름이 설정됨
	- 각 노드에서 실행중인 kubelet이 watch를 통해 파드의 데이터에서 nodeName이 설정되었다는 소식을 받음
	- 해당하는 노드의 kubelet이 컨테이너 런타임을 통해 파드 생성
- ***watch***
	- 감시 요청을 보내면 API 서버는 변경 사항 스트림으로 응답합니다. 이러한 변경사항은 감시 요청의 매개변수로 지정한 resourceVersion 이후에 발생한 작업(예: 만들기, 삭제, 업데이트)의 결과를 항목별로 정리합니다. 전체 감시 메커니즘을 통해 클라이언트는 현재 상태를 가져온 다음 이벤트를 놓치지 않고 후속 변경 사항을 구독할 수 있습니다.
	https://kubernetes.io/docs/reference/using-api/api-concepts/
```
GET /api/v1/namespaces/test/pods?watch=1&resourceVersion=10245
---
200 OK
Transfer-Encoding: chunked
Content-Type: application/json

{
  "type": "ADDED",
  "object": {"kind": "Pod", "apiVersion": "v1", "metadata": {"resourceVersion": "10596", ...}, ...}
}
{
  "type": "MODIFIED",
  "object": {"kind": "Pod", "apiVersion": "v1", "metadata": {"resourceVersion": "11020", ...}, ...}
}
```

### 파드가 생성할 노드를 선택하는 스케줄링 과정
- 노드 필터링 : 필터링 후 노드 스코어링으로 전달됨
	- CPU, 메모리 Requests 만큼 가용자원이 있는가
	- 마스터 노드가 아닌가
	- status가 ready인가
- 노드 스코어링 : 미리 정의된 알고리즘 가중치에 따라 노드 점수 계산
	- 파드가 사용하는 도커 이미지가 이미 노드에 존재함 +
	- 노드의 가용 자원이 많음 +
> 주로 노드 필터링 단계 때 쓸 수 있는 설정을 사용함.


### Node Selector
- 파드 yaml 파일에 노드 이름을 직접 명시
	- 보편적으로 yaml 파일 사용하기 힘듬
- 노드 라벨 이용
	- 단일 파드에 대해 수행된다는 한계
	- deployment, replicaset의 경우 동일한 하나의 노드에 할당된다는 뜻은 아니다.
- 노드 목록
	- `ip-172-31-33-201.us-west-2.compute.internal`
	- `ip-172-31-30-28.us-west-2.compute.internal`
	- `ip-172-31-14-155.us-west-2.compute.internal`
- *만약 조건에 맞는 node가 없으면 어떻게하는거지?*

### Node Affinity
- requiredDuringSchedulingIgnoreDuringExecution
- preferredDuringSchedulingIgnoreDuringExecution
	- 만족하는 노드를 선호
	- weight : 얼마나 선호할지 가중치
- 그러나 이 방법은 파드가 할당된 뒤에 실행, 퇴거시에는 유효하지 않습니다.

### Pod Affinity
- 특정 조건을 만족하는 파드와 함께 실행되도록 스케줄링
- 그러나 이 라벨을 가지는 파드와 무조건 같은 노드에 할당하라는 뜻은 아니다.
	- 토폴로지키에 따라 범주가 나뉨. 만약 토폴로지키가 hostname이라면 토폴로지마다 노드 하나가 할당된다고 확신할 수 있으므로 노드를 지정할 수 있게됨.
- 같은 파드에 있어서 응답시간을 최대한 줄여야 하는 두 개의 파드를 동일한 가용영역 AZ 혹은 리전의 노드에 할당하는 경우 사용하기 좋음.
#### Pod Anti-affinity
- 특정 파드와 같은 토폴로지의 노드를 선택하지 않는 방법
- 고가용성을 보장하기 위해 파드를 여러 가용영역 또는 리전에 멀리 퍼뜨리는 전략

### Taints 와 Tolerations
```
Taints:             bong/my-taint=dirty:NoSchedule
```

```
☁  w11 [main] ⚡  kubectl taint node ip-172-31-30-28.us-west-2.compute.internal alicek106/iirin-taint=dirty:NoS
chedule
node/ip-172-31-30-28.us-west-2.compute.internal tainted
☁  w11 [main] ⚡  kubectl taint node ip-172-31-30-28.us-west-2.compute.internal alicek106/iirin-taint=dirty:NoSchedule-
node/ip-172-31-30-28.us-west-2.compute.internal untainted
```
- 노드에 얼룩이 있고, 파드는 이를 견딜수있는 tolerations 가 있어야 해당 노드에 할당될 수 있다.
- 마스터 노드에 파드가 할당되지 않는 이유도 taint 가 있기 때문입니다.
	- `:NoExcute`라는 효과의 taint를 허용할 수 있는 파드가 마스터 노드에서 실행되고 있습니다.
- 효과
	- `NoSchedule` 
	- `PreferNoSchedule`
	- `NoExcute` : 해당 노드에 스케줄링을 하지 않을 뿐 아니라 해당 노드에서 아예 파드를 실행할 수 없도록 설정
- 쿠버네티스는 특정 문제가 발생한 노드에 대해 자동으로 Taint를 추가합니다.

### Cordon
- 쿠버네티스에서 제공하는 명시적인 방법
- 더이상 파드가 스케줄링되지 않는다.
- 하지만 이미실행중인 파드에게는 영향이 없다.
- 해지 unCordon
### Drain
- 실행중인 pod들이 퇴거를 수행한다.
- 노드 유지보수등의 이유로 사용할 수 있다.
- 단 단일 파드로 생성된 파드가 있다면 drain이 실패할 수 있다.
	- `--force` 옵션을 함께 사용해야 한다.
### PodDistributionBudget
- 파드 개수 유지하기 위한 장치
- 숫자로도 되지만 비율로도 설정할 수 있다.
- pod 퇴거가 발생할 때, 특정 개수 혹은 특정 비율만큼의 파드는 정상적인 상태를 유지하도록 사용됩니다.

```
☁  w11 [main] ⚡  kubectl apply -f 06-simple-pdb-ex.yaml
error: resource mapping not found for name: "iirin-simple-pdb-example" namespace: "" from "06-simple-pdb-ex.yaml": no matches for kind "PodDisruptionBudget" in version "policy/v1beta1"
ensure CRDs are installed first
```

### 커스텀 스케줄러
- 소스코드로 아래를 구현하면 됩니다.
	- API 서버의 Watch API를 통해 새롭게 생성된 파드의 데이터를 받아옵니다.
	- 파드 데이터 중 nodeName이 설정되어있지 않으며, schedulerName 이 정해진 이름과 일치하는 지 검사합니다.
	- 필요에 따라 적절한 스케줄링 알고리즘을 수행합니다.
	- 바인딩 API요청을 통해 스케줄링된 노드의 이름을 파드 nodeName에 설정합니다.
- 쿠버네티스 스케줄러를 확장할 수 있습니다.


- 참고 [쿠버네티스가 리소스 요청 및 제한을 적용하는 방법](https://kubernetes.io/ko/docs/concepts/configuration/manage-resources-containers/#how-pods-with-resource-limits-are-run)
- 