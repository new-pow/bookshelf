# 커스텀리소스와 컨트롤러
## Desired State
- 명령형과 선언적 방식의 차이
-  명령어는 특정 YAML 파일이 최종적으로 완성돼야 하는 상태라는 것을 쿠버네티스에게 알려줄 뿐. 실제로 어떠한 동작을 취해야 하는지는 명시하지 않습니다.
- 선언형의 경우 바람직한 상태를 정의하면 컨트롤러는 현재 상태가 바람직한 상태가 되도록 만들것입니다.
- 쿠버네티스의 대부분의 상태는 etcd와 같은 분산 코디네이터에 저장돼 있습니다. 따라서 여러분이 정의하는 바람직한 상태 또한 etcd에 저장돼 있으며, 컨트롤러는 쿠버네티스 API 서버의 Watch API를 통해 etcd에 저장된 상태 데이터를 받아와 동작을 수행합니다.
- 쿠버네티스는 전체 구성의 복잡성을 줄이기 위해 컨트롤러 로직을 쿠버네티스 컨트롤러 매니저라는 하나의 컴포넌트에서 구현해 놓았습니다. `kube-system` 네임스페이스에서 파드로 실행됩니다.

## 커스텀 리소스
- 커스텀 리소스 또한 파드, 디플로이먼트, 서비스 등과 동일한 리소스의 한 종류로 간주
- 디플로이먼트, 서비스 등의 오브젝트의 묶음을 커스텀 리소스로 추상화함으로써 쿠버네티스 리소스를 묶어 놓은 패키지처럼 사용할 수도 있고, 쿠버네티스와 전혀 상관이 없는 로직을 커스텀 리소스와 연동할 수도 있습니다.
### 커스텀 리소스를 정의하기 위한 **CRD(C**니**stom** **Resource** **Definition)**
- 

