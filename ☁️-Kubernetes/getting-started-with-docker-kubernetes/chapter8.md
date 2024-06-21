![](https://kubernetes.io/ko/docs/images/ingress.svg)

- 


# 질문
- 페이지 379
```
curl --resolve alicek106.example.com:32439:10.100.201.239 alicek106.example.com:32439/echo-hostname
curl: (7) Failed to connect to alicek106.example.com port 32439 after 19 ms: Couldn't connect to server

왜 안되는거지; 서비스 dns가 뭐지?
```



```
☁  chapter8-iirin [main] ⚡  kubectl apply -f 05-ingress-tls-k8s-latest.yaml
Error from server (InternalError): error when applying patch:
{"metadata":{"annotations":{"kubectl.kubernetes.io/last-applied-configuration":"{\"apiVersion\":\"networking.k8s.io/v1\",\"kind\":\"Ingress\",\"metadata\":{\"annotations\":{\"kubernetes.io/ingress.class\":\"nginx\",\"nginx.ingress.kubernetes.io/rewrite-target\":\"/\"},\"name\":\"iirin-ingress-example\",\"namespace\":\"default\"},\"spec\":{\"rules\":[{\"host\":\"alicek106.example.com\",\"http\":{\"paths\":[{\"backend\":{\"service\":{\"name\":\"hostname-service\",\"port\":{\"number\":80}}},\"path\":\"/echo-hostname\",\"pathType\":\"Prefix\"}]}}],\"tls\":[{\"hosts\":[\"alicek106.example.com\"],\"secretName\":\"tls-secret\"}]}}\n"}},"spec":{"tls":[{"hosts":["alicek106.example.com"],"secretName":"tls-secret"}]}}
to:
Resource: "networking.k8s.io/v1, Resource=ingresses", GroupVersionKind: "networking.k8s.io/v1, Kind=Ingress"
Name: "iirin-ingress-example", Namespace: "default"
for: "05-ingress-tls-k8s-latest.yaml": error when patching "05-ingress-tls-k8s-latest.yaml": Internal error occurred: failed calling webhook "validate.nginx.ingress.kubernetes.io": failed to call webhook: Post "https://ingress-nginx-controller-admission.ingress-nginx.svc:443/networking/v1/ingresses?timeout=10s": tls: failed to verify certificate: x509: certificate signed by unknown authority
```