---
tags: [systems, kubernetes, container, jvm]
---

# 1주차. 왜 컨테이너인가
- 학습 기간 : 2026. 11. 20 ~ 2026. 11. 22
- 범위 : 선언형 · VM vs 컨테이너 · namespace/cgroups · 리눅스 기초 · 마이크로서비스/불변 인프라/12-Factor
- 남은 것 : 컨테이너 런타임 구조 (kubectl → kubelet → containerd → runc), 1주차 퀴즈

> 💭 = 처음 생각, ✅ = 정리된 답, ⚠️ = 흔한 오해

---

## 요약

### 1. 쿠버네티스의 핵심: 선언형
- 명령형은 "**무엇을 할지**"를 지시하고, 선언형은 "**어떤 상태여야 하는지**"를 선언한다.
- 컨트롤러가 **reconciliation loop**로 현재 상태와 원하는 상태를 계속 맞춘다.

### 2. VM vs 컨테이너
```
        VM                                  컨테이너
┌─────────┬─────────┐            ┌─────────┬─────────┐
│  App A  │  App B  │            │  App A  │  App B  │
├─────────┼─────────┤            ├─────────┼─────────┤
│ Libs    │ Libs    │            │ Libs    │ Libs    │ ← 이미지 (유저랜드)
├─────────┼─────────┤            └─────────┴─────────┘
│Guest OS │Guest OS │ ← 커널이 VM마다 하나씩    namespace(벽) + cgroups(계량기)
│ (커널)  │ (커널)  │            ┌───────────────────┐
├─────────┴─────────┤            │ Container Runtime │ (containerd/runc)
│    Hypervisor     │            ├───────────────────┤
├───────────────────┤            │  Host OS (커널 1개) │ ← 모두가 공유
│  Host OS / HW     │            ├───────────────────┤
└───────────────────┘            │     Hardware      │
                                 └───────────────────┘
```

| | VM | 컨테이너 |
|---|---|---|
| 가상화 대상 | 하드웨어 | OS |
| 커널 | VM마다 따로 | **호스트 커널 공유** |
| 정체 | 독립된 OS | **격리된 일반 프로세스** |
| 크기 · 시작 | GB · 분 단위 | MB · 초 단위 |
| 격리 강도 | 강함 | 상대적으로 약함 |
| 비유 | 단독주택 (집마다 보일러·전기 설비) | 아파트 (설비 공유, 벽과 세대별 계량기) |

- macOS는 커널이 달라서 Docker가 **리눅스 VM 안에서** 컨테이너를 실행한다. Mac에서는 두 구조를 함께 쓰는 셈.

### 3. namespace vs cgroups
| | namespace | cgroups |
|---|---|---|
| 역할 | 무엇이 **보이는가** (벽) | 얼마나 **쓰는가** (계량기·차단기) |
| 종류 | mnt, pid, net, ipc, uts, user | memory, cpu, io, pids |
| 확인 | `readlink /proc/self/ns/mnt` | `cat /sys/fs/cgroup/memory.max` |

- `docker run`만 해도 namespace는 **자동 생성**된다. 명령어에는 보이지 않는다.
- `/proc/meminfo`(= `free`)와 `os.cpu_count()`는 **격리되지 않는다.** 컨테이너 안에서도 호스트 값이 보인다.

### 4. 파일시스템: overlay와 bind mount
```
merged ("/")  = upperdir (쓰기 가능, 컨테이너 삭제 시 사라짐)
              + lowerdir (읽기 전용 이미지 레이어, 컨테이너끼리 공유)
```
- 수정은 원본을 두고 upperdir에 복사해서 한다 → **Copy-on-Write**
- `-v`는 격리된 mnt namespace 안에 호스트 경로를 **의도적으로 붙이는 bind mount**다. K8s의 PVC, hostPath도 같은 원리.

### 5. 마이크로서비스 → 불변 인프라 → 12-Factor
> 마이크로서비스로 서비스가 많아지고 → 불변 인프라로 교체 가능하게 만들고 → 12-Factor로 언제 죽어도 괜찮은 앱을 만들면 → 쿠버네티스가 배치·복구·확장을 자동화한다.

| | 가변 (옛 방식) | 불변 (컨테이너 방식) |
|---|---|---|
| 변경 방법 | 서버에 접속해서 고친다 | 새 이미지를 만들어 교체한다 |
| 결과 | 서버마다 상태가 달라짐 (눈송이 서버) | 모든 인스턴스가 이미지와 동일 |
| 롤백 | 뭘 고쳤는지 기억해서 되돌림 | 이전 이미지로 교체 |

**12-Factor** (2011년 Heroku 정리, 원칙 12개). 컨테이너와 특히 관련 깊은 5개:

| 원칙 | K8s에서 | MLOps 예시 |
|---|---|---|
| Config: 설정은 환경에 | ConfigMap, Secret, 환경변수 | 모델 경로·하이퍼파라미터를 환경변수로 |
| Logs: 로그는 stdout | `kubectl logs`, 노드 수집기 | 학습 loss를 stdout·실험 추적 도구로 |
| Processes: 무상태 | 상태는 DB/볼륨에 | 체크포인트는 PVC·오브젝트 스토리지에 |
| Disposability: 빠른 시작, 우아한 종료 | SIGTERM 처리, Probe | 종료 신호에 체크포인트 저장 |
| Backing services: 연결 리소스 | Service DNS | 피처 스토어·모델 레지스트리를 주소로 |

나머지 7개: Codebase, Dependencies, Build/release/run, Port binding, Concurrency, Dev/prod parity, Admin processes

---

## Q&A

### Q1. `docker run`을 3번 하는 것과 `replicas: 3` 선언은 뭐가 다른가?
💭 명령은 한 번 실행하고 끝나지만, 선언하면 K8s가 그 상태를 스스로 유지한다.
✅ 맞음. 이 동작이 **reconciliation loop**다. "현재 2개 ≠ 원하는 3개"를 감지하면 1개를 새로 띄운다.
- 보충: `docker run --restart=always`도 다시 살리지만 **같은 호스트에서만** 가능하다. 노드가 죽으면 K8s는 **다른 노드에** 띄운다.

### Q2. Ubuntu 호스트에서 CentOS 이미지를 실행하면 커널은? 잘 돌아가나?
💭 커널은 호스트 것을 쓰니까 안 되는 게 많을 것 같다. (인터페이스가 다를 거라고 생각)
✅ 커널은 호스트 것이 맞지만 **대부분 잘 돌아간다.**
```
┌──────────────────────────┐
│ 유저랜드 (glibc, 패키지)   │ ← 이미지 (CentOS)
├────────── syscall ───────┤ ← 안정적인 경계 ("We don't break userspace")
│ 커널                      │ ← 호스트 (Ubuntu)
└──────────────────────────┘
```
- 배포판의 차이(yum/apt, glibc, 설정 경로)는 **유저랜드**에 있고, 유저랜드는 이미지에 같이 들어 있다.
- ⚠️ 예외 ①: 세대 차이가 너무 크면 깨진다. 예: CentOS 6 이미지는 최신 커널에서 vsyscall 문제로 segfault
- ⚠️ 예외 ②: **GPU.** 드라이버는 호스트에, CUDA는 이미지에 있어서 버전 호환을 맞춰야 한다.

### Q3. 학습 컨테이너가 노드 메모리를 다 쓰지 못하게 막는 건?
✅ **cgroups.**
- 메모리 한도를 넘으면 커널 OOM Killer가 SIGKILL → Docker는 **exit 137**(128 + 9), K8s는 **`OOMKilled`**
- CPU는 압축 가능한 자원이라 죽지 않고 **느려진다(throttling).**

### Q4. 64MB 한도에서 100MB를 할당하면? (실습)
```bash
docker run --rm -m 64m python:3.12-alpine python -c "a = bytearray(100*1024*1024); print('ok')"; echo "exit=$?"
```
💭 `ok`가 나올 것이다.
✅ 실제 결과는 **exit=137**. `--memory-swap 64m`을 추가해도 137.
- `-m`만 주면 swap 포함 2배(128MB)까지 **허용**된다. 하지만 Docker VM의 **swap이 0**이라 실제로는 64MB가 한계였다. (`docker run --rm alpine free -m`으로 확인)
- ⚠️ 137은 "SIGKILL로 죽었다"는 뜻일 뿐이다. OOM인지는 상태 필드로 **확정**한다.
  ```bash
  docker inspect -f '{{.State.OOMKilled}} {{.State.ExitCode}}' <컨테이너>
  ```
  K8s에서는 `kubectl describe pod` → `Last State: Terminated, Reason: OOMKilled`

### Q5. CPU 제한으로 느려지면, 밀린 작업이 메모리에 쌓여 결국 OOM이 나지 않나?
✅ **작업 형태에 따라 다르다.**

| 형태 | 예시 | CPU 제한 시 |
|---|---|---|
| 닫힌 시스템 (일의 양 고정) | 배치 처리, 학습 epoch | 느려질 뿐, 메모리는 안 늘어남 |
| 열린 시스템 (일이 계속 들어옴) | API 서버, 추론 서버 | 도착 > 처리이면 큐 증가 → **메모리 증가 → OOM 가능** |

- Little's Law: `L(대기 수) = λ(도착률) × W(체류 시간)`. W가 커지면 L도 커진다.
- 실제로는 OOM보다 **타임아웃**, **Liveness Probe 실패로 인한 재시작**이 먼저 오는 경우가 많다 → 백프레셔(큐 제한, 429, rate limit) 필요
- MLOps: CPU 추론 서버는 평균 사용률이 낮아도 **CFS throttling으로 p99 지연이 튈 수 있다.**

### Q6. `free` 명령어는 뭔가?
✅ 메모리·swap 사용량을 보여 준다(`/proc/meminfo`를 읽음). 판단 기준은 **`available`** 열.

| 열 | 의미 |
|---|---|
| used | 프로세스가 쓰는 양 |
| free | 아무도 안 쓰는 양 |
| buff/cache | 파일 캐시. 필요하면 비워서 내준다 |
| **available** | 새 프로그램이 실제로 쓸 수 있는 양 |

- ⚠️ `/proc/meminfo`는 **격리되지 않는다.** `-m 64m` 컨테이너에서 `free`를 해도 **VM 전체 메모리**가 보인다. 진짜 한도는 `/sys/fs/cgroup/memory.max`
- 같은 문제: `os.cpu_count()`도 호스트 코어 수 → PyTorch `num_workers`를 과하게 띄워 OOM이 날 수 있다.

### Q7. "옛 JVM은 이 문제로 유명했다"는 무슨 뜻인가?
✅ JVM은 `-Xmx`가 없으면 **인식한 메모리의 25%**를 최대 힙으로 잡는다(`MaxRAMPercentage`).

| | 옛 JVM | 지금 JVM |
|---|---|---|
| 메모리를 읽는 곳 | `/proc/meminfo` (호스트 16GB) | cgroup 한도 (1GB) |
| 최대 힙 | 4GB → 한도 초과로 **OOMKilled** | 256MB |

| | `java.lang.OutOfMemoryError` | `OOMKilled` (137) |
|---|---|---|
| 누가 | JVM 자신 | 리눅스 커널 |
| 로그 | 스택트레이스·힙 덤프 | **없음** |

- 힙 외에 Metaspace, 스레드 스택, Direct Buffer, 코드 캐시도 쓴다 → 힙은 **60~75%** 정도 (측정해서 조정)

### Q8. 그게 바뀐 건 어느 버전부터인가?
| 변경 | 버전 |
|---|---|
| 실험 옵션 `UseCGroupMemoryLimitForHeap` | 8u131, JDK 9 |
| 컨테이너 인식 기본 켜짐 (`UseContainerSupport`, `MaxRAMPercentage`) | **JDK 10 / 8u191** |
| cgroup v2 인식 | **JDK 15 / 11.0.16 / 8u372** (완전 지원은 17+) |
| CPU 개수 계산에서 `requests` 무시 | **JDK 19** (17.0.5 / 11.0.17 백포트 추정, 미확인) |

- ⚠️ "Java 11이니까 괜찮다"는 위험. 11.0.15 이하를 cgroup v2 노드에서 돌리면 다시 호스트 메모리를 읽는다. **패치 버전까지** 확인.
  ```bash
  docker run --rm -m 1g eclipse-temurin:21-jre java -XshowSettings:vm -version
  ```

### Q9. 컨테이너의 `/`에 붙은 파일시스템은?
💭 모르겠다.
✅ **overlay.** 읽기 전용 이미지 레이어 위에 쓰기 가능한 층을 겹쳐 하나로 보여 준다(요약 4번 참고).
```bash
docker run --rm alpine sh -c "mount | grep ' / '"
```

### Q10. mnt namespace로 격리됐다는데 명령어에 안 보인다. `-v`로 호스트 폴더가 보이는 건?
💭 `$PWD`를 붙여서인가? namespace가 명령어에 안 보인다.
✅ namespace는 `docker run` 시 **자동 생성**되어 옵션에 안 나온다.
```bash
docker run --rm alpine readlink /proc/self/ns/mnt   # 실행할 때마다 숫자가 다름
```
- `$PWD`는 현재 경로를 담은 셸 변수일 뿐이고, 핵심은 **`-v`(bind mount)**다.
- 격리가 뚫린 게 아니라, 격리된 공간 안에 Docker가 **의도적으로 호스트 경로를 붙인 것**이다.
```
[Mac 폴더] ──파일 공유──▶ [Docker VM] ──bind mount──▶ [컨테이너 /data]
```

### Q11. 운영 Pod에 `exec`로 들어가 설정을 고치고 재시작하면?
✅ 수정 내용은 overlay의 **upperdir**에 있어서 재시작하면 **사라진다.**
→ 그래서 **불변 인프라**: 고치지 말고 새 이미지나 ConfigMap으로 갈아 끼운다. overlay가 이 원칙을 기술적으로 강제하는 셈이다.

### Q12. 왜 "12-Factor"인가?
✅ 원칙이 **12개**라서. 2011년 **Heroku**가 "클라우드에서 잘 돌아가는 앱의 공통점"을 정리했다. K8s(2014)보다 먼저 나왔지만 K8s가 기대하는 앱의 모습과 거의 같다.

---

## 🔍 토론거리
- ==? GPU 드라이버(호스트)와 CUDA(이미지) 버전 호환은 어떻게 관리하나?==
- ==? 서빙 Pod에 CPU limit을 거는가? throttling 지표를 보고 있나?==
- ==? 학습 이미지의 num_workers와 힙 크기는 cgroup 한도를 따르는가?==
- ==? 우리 JVM 서비스의 JDK 패치 버전과 노드 cgroup 버전은?==
- ==? MaxRAMPercentage=100이면 Java OOM과 OOMKilled 중 무엇이 먼저 오나?==
- ==? JDK 업그레이드 시 requests만 있고 limits 없는 Pod의 GC 스레드 수가 바뀌는지 확인했나?==
- ==? cgroups v2에는 네트워크 대역폭 컨트롤러가 없다. K8s에서 대역폭 제한은 어떻게 하나?==
- ==? 12-Factor는 MLOps(학습 Job, GPU, 대용량 데이터)에도 맞을까?==
- ==? 학습 Job이 3시간째 돌다 노드 장애로 죽었다. 어떤 원칙 2개를 지켰다면 처음부터 다시 안 돌려도 됐을까?==

## 📚 참고 자료
**한국어**
- [커널 cgroups와 namespaces로 보는 컨테이너 내부 (youngju.dev)](https://www.youngju.dev/blog/linux/2026-06-13-kernel-cgroups-namespaces-container-internals)
- [cgroups · namespaces · overlayfs · runc 딥다이브 (youngju.dev)](https://www.youngju.dev/transcribe/culture/2026-04-15-container-internals-cgroups-namespaces-overlayfs-runc-deep-dive-guide-2025)
- [JVM은 컨테이너 한도를 어떻게 보는가 (braindetox)](https://braindetox.kr/posts/jvm_container_resource_limits.html)
- [컨테이너 환경에서 Java Heap Memory 관리 (velog)](https://velog.io/@xgro/JAVA-heap-manage)
- [The Twelve-Factor App 한국어 소개 (GeekNews)](https://news.hada.io/topic?id=840)

**영어**
- [Containers the hard way: Gocker, a mini Docker in Go](https://unixism.net/2020/06/containers-the-hard-way-gocker-a-mini-docker-written-in-go/)
- [Why a Java pod gets OOMKilled in Kubernetes with a half-empty heap](https://itnext.io/why-a-java-pod-gets-oomkilled-in-kubernetes-with-a-half-empty-heap-e610e92dc9ee)
- [AWS: JVM memory, CPU and classpath best practices for Java containers](https://aws.amazon.com/blogs/containers/jvm-memory-cpu-and-classpath-best-practices-for-java-containers-on-aws/)
- [Red Hat: OpenJDK 8u372 feature: cgroup v2 support](https://developers.redhat.com/articles/2023/04/19/openjdk-8u372-feature-cgroup-v2-support)
- [The Twelve-Factor App](https://12factor.net/)
- 영상: Liz Rice, "Containers From Scratch"
