# 쿠버네티스 리소스의 관리와 설정
## 7.1 네임스페이스 Namespace
- 리소스를 구분지어 관리할 수 있는 일종의 논리적인 그룹을 만들기 위한 오브젝트
- 목적별로, 조직별로 하나의 쿠버네티스 클러스터에 여러개의 가상 클러스터를 동시에 사용하는 것처럼 사용할 수 있다.
- 그러나 물리적으로 격리된 것은 아니다. 서로 다른 네임스페이스에서 생성된 파드가 같은 노드에 존재할 수 있다.
### 네임스페이스의 서비스 접근
- 같은 네임스페이스 안이라면 서비스 이름만으로 접근 가능
- 다른 네임스페이스 안이라면 서비스 이름만으로 접근 불가능
```
☁  chapter7-iirin [main] ⚡  kubectl run -i --tty --rm debug --image=alicek106/ubuntu:curl --restart=Never -- bash
If you don't see a command prompt, try pressing enter.
root@debug:/#
root@debug:/# curl iirin-hostname-svc-clusterip-ns:8080
curl: (6) Could not resolve host: iirin-hostname-svc-clusterip-ns
```
- {서비스이름}.{네임스페이스이름}.svc 로 접근 가능하다.
```
root@debug:/# curl iirin-hostname-svc-clusterip-ns.iirin-production.svc:8080
<!DOCTYPE html>
<meta charset="utf-8" />
<link rel="stylesheet" type="text/css" href="./css/layout.css" />
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">

<div class="form-layout">
	<blockquote>
	<p>Hello,  hostname-deployment-ns-5f5dc4467b-4dxf8</p>	</blockquote>
</div>
root@debug:/#
```

### 네임스페이스에 포함되는/포함되지 않는 오브젝트
- 포함되는 경우
```
kubectl api-resources --namespaced=true

NAME                        SHORTNAMES   APIVERSION                     NAMESPACED   KIND
bindings                                 v1                             true         Binding
configmaps                  cm           v1                             true         ConfigMap
endpoints                   ep           v1                             true         Endpoints
events                      ev           v1                             true         Event
limitranges                 limits       v1                             true         LimitRange
persistentvolumeclaims      pvc          v1                             true         PersistentVolumeClaim
pods                        po           v1                             true         Pod
podtemplates                             v1                             true         PodTemplate
replicationcontrollers      rc           v1                             true         ReplicationController
resourcequotas              quota        v1                             true         ResourceQuota
secrets                                  v1                             true         Secret
serviceaccounts             sa           v1                             true         ServiceAccount
services                    svc          v1                             true         Service
controllerrevisions                      apps/v1                        true         ControllerRevision
daemonsets                  ds           apps/v1                        true         DaemonSet
deployments                 deploy       apps/v1                        true         Deployment
replicasets                 rs           apps/v1                        true         ReplicaSet
statefulsets                sts          apps/v1                        true         StatefulSet
localsubjectaccessreviews                authorization.k8s.io/v1        true         LocalSubjectAccessReview
horizontalpodautoscalers    hpa          autoscaling/v2                 true         HorizontalPodAutoscaler
cronjobs                    cj           batch/v1                       true         CronJob
jobs                                     batch/v1                       true         Job
leases                                   coordination.k8s.io/v1         true         Lease
endpointslices                           discovery.k8s.io/v1            true         EndpointSlice
events                      ev           events.k8s.io/v1               true         Event
policyendpoints                          networking.k8s.aws/v1alpha1    true         PolicyEndpoint
ingresses                   ing          networking.k8s.io/v1           true         Ingress
networkpolicies             netpol       networking.k8s.io/v1           true         NetworkPolicy
poddisruptionbudgets        pdb          policy/v1                      true         PodDisruptionBudget
rolebindings                             rbac.authorization.k8s.io/v1   true         RoleBinding
roles                                    rbac.authorization.k8s.io/v1   true         Role
csistoragecapacities                     storage.k8s.io/v1              true         CSIStorageCapacity
securitygrouppolicies       sgp          vpcresources.k8s.aws/v1beta1   true         SecurityGroupPolicy
```

- 포함되지 않는 경우
```
kubectl api-resources --namespaced=false
NAME                              SHORTNAMES   APIVERSION                        NAMESPACED   KIND
componentstatuses                 cs           v1                                false        ComponentStatus
namespaces                        ns           v1                                false        Namespace
nodes                             no           v1                                false        Node
persistentvolumes                 pv           v1                                false        PersistentVolume
mutatingwebhookconfigurations                  admissionregistration.k8s.io/v1   false        MutatingWebhookConfiguration
validatingwebhookconfigurations                admissionregistration.k8s.io/v1   false        ValidatingWebhookConfiguration
customresourcedefinitions         crd,crds     apiextensions.k8s.io/v1           false        CustomResourceDefinition
apiservices                                    apiregistration.k8s.io/v1         false        APIService
selfsubjectreviews                             authentication.k8s.io/v1          false        SelfSubjectReview
tokenreviews                                   authentication.k8s.io/v1          false        TokenReview
selfsubjectaccessreviews                       authorization.k8s.io/v1           false        SelfSubjectAccessReview
selfsubjectrulesreviews                        authorization.k8s.io/v1           false        SelfSubjectRulesReview
subjectaccessreviews                           authorization.k8s.io/v1           false        SubjectAccessReview
certificatesigningrequests        csr          certificates.k8s.io/v1            false        CertificateSigningRequest
eniconfigs                                     crd.k8s.amazonaws.com/v1alpha1    false        ENIConfig
flowschemas                                    flowcontrol.apiserver.k8s.io/v1   false        FlowSchema
prioritylevelconfigurations                    flowcontrol.apiserver.k8s.io/v1   false        PriorityLevelConfiguration
ingressclasses                                 networking.k8s.io/v1              false        IngressClass
runtimeclasses                                 node.k8s.io/v1                    false        RuntimeClass
clusterrolebindings                            rbac.authorization.k8s.io/v1      false        ClusterRoleBinding
clusterroles                                   rbac.authorization.k8s.io/v1      false        ClusterRole
priorityclasses                   pc           scheduling.k8s.io/v1              false        PriorityClass
csidrivers                                     storage.k8s.io/v1                 false        CSIDriver
csinodes                                       storage.k8s.io/v1                 false        CSINode
storageclasses                    sc           storage.k8s.io/v1                 false        StorageClass
volumeattachments                              storage.k8s.io/v1                 false        VolumeAttachment
cninodes                          cnd          vpcresources.k8s.aws/v1alpha1     false        CNINode
```

# 컨피그맵
## 컨피그맵을 파드에서 어떻게 사용하나?
- 컨테이너의 환경 변수로 사용
- 파드 내부의 파일로 마운트해 사용

### 환경변수로 사용하는 예시
```
envFrom:
- configMapRef:
	name: log-level-configmap
- configMapRef:
	name: start-k8s
```

```
☁  chapter7-iirin [main] ⚡  kubectl exec iirin-container-env-example env
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
HOSTNAME=iirin-container-env-example
LOG_LEVEL=DEBUG
```

