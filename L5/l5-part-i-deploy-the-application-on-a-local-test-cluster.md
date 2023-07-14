# L5 - Part I - Deploy the application on a local test cluster

## Subtask 1.1 - Installation of Minikube

* Setup and update Minikube

```
[INPUT]
brew install minikube
[OUTPUT]
--
```

* Version of Minikube

```
[INPUT]
minikube version

[OUTPUT]
minikube version: v1.30.1
commit: 08896fd1dc362c097c925146c4a0d0dac715ace0
```

## Subtask 1.2 - Installation of Kubectl

* Setup and update Kubectl

Was installed with minikube on MacOS

* Version of Kubectl

```
[INPUT]
kubectl version --short

[OUTPUT]
Flag --short has been deprecated, and will be removed in the future. The --short output will become the default.
Client Version: v1.27.1
Kustomize Version: v5.0.1
Server Version: v1.26.3
```

## Subtask 1.3 Create a one-node cluster on your local machine

* Create and start a one-node-cluster

```
[INPUT]
minikube start

[OUTPUT] 
😄  minikube v1.30.1 on Darwin 13.3.1 (arm64)
✨  Using the docker driver based on existing profile
👍  Starting control plane node minikube in cluster minikube
🚜  Pulling base image ...
🔄  Restarting existing docker container for "minikube" ...
🐳  Preparing Kubernetes v1.26.3 on Docker 23.0.2 ...
🔗  Configuring bridge CNI (Container Networking Interface) ...
🔎  Verifying Kubernetes components...
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
    ▪ Using image docker.io/kubernetesui/dashboard:v2.7.0
    ▪ Using image docker.io/kubernetesui/metrics-scraper:v1.0.8
💡  Some dashboard features require the metrics-server addon. To enable all features please run:

        minikube addons enable metrics-server


🌟  Enabled addons: storage-provisioner, default-storageclass, dashboard
🏄  Done! kubectl is now configured to use "minikube" cluster and "default" namespace by default
```

* Examine the cluster

```
[INPUT]
kubectl cluster-info

[OUTPUT]
Kubernetes control plane is running at https://127.0.0.1:62009
CoreDNS is running at https://127.0.0.1:62009/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy

To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.
```

* View the nodes in the cluster

```
[INPUT]
kubectl get nodes

[OUTPUT]
NAME       STATUS   ROLES           AGE   VERSION
minikube   Ready    control-plane   35m   v1.26.3
```

## Subtask - OPTIONAL - Access the kubernetes dashboard

```
[INPUT]
minikube dashboard

[OUTPUT]
//TODO
```

## Substask 1.4 - Deploy the application

* Deploy the redis service and pod

```
[INPUT]
kubectl create -f redis-svc.yaml
kubectl create -f redis-pod.yaml
kubectl get all

[OUTPUT]
service/redis-svc created
pod/redis created
NAME        READY   STATUS    RESTARTS   AGE
pod/redis   1/1     Running   0          26s

NAME                 TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
service/kubernetes   ClusterIP   10.96.0.1       <none>        443/TCP    115s
service/redis-svc    ClusterIP   10.96.102.235   <none>        6379/TCP   32s
```

* Zoom in on a Kubernetes object and see much more detail

```
[INPUT]
kubectl describe po/redis

[OUTPUT]
Name:             redis
Namespace:        default
Priority:         0
Service Account:  default
Node:             minikube/192.168.49.2
Start Time:       Tue, 09 May 2023 17:07:34 +0200
Labels:           app=todo
                  component=redis
Annotations:      <none>
Status:           Running
IP:               10.244.0.10
IPs:
  IP:  10.244.0.10
Containers:
  redis:
    Container ID:  docker://1963007e6755cd63c03fccdeeaf865bae26f5629d63fa256e625f3c06f87ac9e
    Image:         redis
    Image ID:      docker-pullable://redis@sha256:ea30bef6a1424d032295b90db20a869fc8db76331091543b7a80175cede7d887
    Port:          6379/TCP
    Host Port:     0/TCP
    Args:
      redis-server
      --requirepass ccp2
      --appendonly yes
    State:          Running
      Started:      Tue, 09 May 2023 17:07:36 +0200
    Ready:          True
    Restart Count:  0
    Environment:    <none>
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-9jknq (ro)
Conditions:
  Type              Status
  Initialized       True 
  Ready             True 
  ContainersReady   True 
  PodScheduled      True 
Volumes:
  kube-api-access-9jknq:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   BestEffort
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type    Reason     Age   From               Message
  ----    ------     ----  ----               -------
  Normal  Scheduled  35s   default-scheduler  Successfully assigned default/redis to minikube
  Normal  Pulling    34s   kubelet            Pulling image "redis"
  Normal  Pulled     33s   kubelet            Successfully pulled image "redis" in 1.224197667s (1.224220875s including waiting)
  Normal  Created    33s   kubelet            Created container redis
  Normal  Started    33s   kubelet            Started container redis
  ```

```
[INPUT]
kubectl describe svc/redis-svc

[OUTPUT]
Name:              redis-svc
Namespace:         default
Labels:            component=redis
Annotations:       <none>
Selector:          app=todo,component=redis
Type:              ClusterIP
IP Family Policy:  SingleStack
IP Families:       IPv4
IP:                10.96.102.235
IPs:               10.96.102.235
Port:              redis  6379/TCP
TargetPort:        6379/TCP
Endpoints:         10.244.0.10:6379
Session Affinity:  None
Events:            <none>
```

* Deploy the TODO-API Service and Pod



```
[INPUT]
kubectl create -f api-svc.yaml
kubectl create -f api-pod.yaml
[OUTPUT]
service/api-svc created
pod/api created
```

* Verify that they are up and running on the correct ports

```
[INPUT]
kubectl get all 
[OUTPUT]
NAME           READY   STATUS    RESTARTS   AGE
pod/api        1/1     Running   0          16m
pod/redis      1/1     Running   0          31m

NAME                 TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE
service/api-svc      ClusterIP   10.101.144.180   <none>        8081/TCP   14m
service/kubernetes   ClusterIP   10.96.0.1        <none>        443/TCP    32m
service/redis-svc    ClusterIP   10.96.102.235    <none>        6379/TCP   31m
```

* Deploy the frontend Pod

```
[INPUT]
kubectl create -f frontend-pod.yaml

kubectl get all

[OUTPUT]
pod/frontend created

NAME           READY   STATUS    RESTARTS   AGE
[...]
pod/frontend   1/1     Running   0          9m52s
[...]

NAME                 TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE
service/api-svc      ClusterIP   10.101.144.180   <none>        8081/TCP   16m
service/kubernetes   ClusterIP   10.96.0.1        <none>        443/TCP    35m
service/redis-svc    ClusterIP   10.96.102.235    <none>        6379/TCP   34m
```

* What value must be set for the API-endpoint-URL?

```
http://api-svc:8081
```

* Verify the TODO Application

{% hint style="info" %}
To start port forwarding

```
kubectl port-forward pod_name local_port:pod_port
```
{% endhint %}

```
[INPUT]
kubectl port-forward frontend 9100:8080

[OUTPUT]
Forwarding from 127.0.0.1:9100 -> 8080
Forwarding from [::1]:9100 -> 8080
[...]
```
