# L5 - Part IV - Deploy on IICT Kubernetes Cluster

{% hint style="info" %}
**You must  activate the VPN (even from the school) to be able to access the IICT Kubernetes cluster.**
{% endhint %}

## Subtask 4.1 Setup Kubectl



1. Log yourself on [https://rancher.iict.ch](https://rancher.iict.ch/dashboard/auth/login?local) /!\ Select "Use a local user" /!\ with the credentials given in cyberlearns (csv files) on the course page.
2. Once you are logged in, click on "iict-student" cluster and then on the right corner click on the icon with the name "Download KubeConfig". Copy the content and paste it to your machine into the file `~/.kube/config`. YOu may need to merge the file with the content already present from previous step.
3. To test that you can talk to the cluster try the command `kubectl get nodes`. You should get an output similar to:

```
NAME           STATUS   ROLES                      AGE    VERSION
node1-ens   Ready    controlplane,etcd,worker   85d   v1.24.9
node2-ens   Ready    controlplane,etcd,worker   85d   v1.24.9
node3-ens   Ready    controlplane,etcd,worker   85d   v1.24.9
node4-ens   Ready    controlplane,etcd,worker   85d   v1.24.9
```

```
[INPUT]
kubectl get nodes

[OUTPUT]
NAME        STATUS   ROLES                      AGE    VERSION
node1-ens   Ready    controlplane,etcd,worker   105d   v1.24.9
node2-ens   Ready    controlplane,etcd,worker   105d   v1.24.9
node3-ens   Ready    controlplane,etcd,worker   105d   v1.24.9
node4-ens   Ready    controlplane,etcd,worker   105d   v1.24.9

```

## Subtask 4.2 - Deploy the application

As you are all on the same cluster, to avoid one group to interact with resources of an other group we use the notion of **namespace** [namespace's documentation](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/). So we created one namespace for each group. In all next commands you'll need to give on which namespace the command belongs to and you do that with the parameter `-n <namespace>`. You can see [this link](https://kubernetes.io/docs/tasks/access-application-cluster/configure-access-multiple-clusters/) to add the config of the namespace directly on the config file to avoid the usage of -n. You will need to modify your deployment files. Indeed, we have a private registry at IICT to store docker images.

That means you have to change the parameter `image` in deployment files to these values:

* registry.iict.ch/cld/cld-docker-images/icclabcna/ccp2-k8s-todo-frontend
* registry.iict.ch/cld/cld-docker-images/icclabcna/ccp2-k8s-todo-api
* registry.iict.ch/cld/cld-docker-images/redis

<!---->

* [x] Deploy all components needed with the command `kubectl apply -f <file> -n l6grx` where x is the letter of your group.&#x20;

```
[INPUT]
kubectl apply -f redis-svc.yaml -n devopsteam7
kubectl apply -f redis-pod.yaml -n devopsteam7
kubectl apply -f api-svc.yaml -n devopsteam7
kubectl apply -f api-pod.yaml -n devopsteam7
kubectl apply -f frontend-svc.yaml -n devopsteam7
kubectl apply -f frontend-pod.yaml -n devopsteam7

[OUTPUT]
service/redis-svc created
deployment.apps/redis-deployment created
service/api-svc created
deployment.apps/api-deployment created
service/frontend-svc created
deployment.apps/frontend-deployment created

```

* [x] Then with the command `kubectl get all -n l6grx` you should have all information needed to connect to the frontend.

```
[INPUT]
kubectl get all -n devopsteam7

[OUTPUT]
NAME                                       READY   STATUS    RESTARTS   AGE
pod/api-deployment-7d9878b9d6-2ddp6        1/1     Running   0          2m18s
pod/api-deployment-7d9878b9d6-ncfdz        1/1     Running   0          2m18s
pod/frontend-deployment-64989bdcff-fr9t2   1/1     Running   0          2m3s
pod/frontend-deployment-64989bdcff-x5sp7   1/1     Running   0          2m3s
pod/redis-deployment-8656cb7577-929cz      1/1     Running   0          2m32s

NAME                   TYPE           CLUSTER-IP      EXTERNAL-IP      PORT(S)        AGE
service/api-svc        ClusterIP      10.43.242.236   <none>           8081/TCP       2m24s
service/frontend-svc   LoadBalancer   10.43.131.6     10.190.129.202   80:32718/TCP   2m7s
service/redis-svc      ClusterIP      10.43.29.253    <none>           6379/TCP       2m59s

NAME                                  READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/api-deployment        2/2     2            2           2m18s
deployment.apps/frontend-deployment   2/2     2            2           2m3s
deployment.apps/redis-deployment      1/1     1            1           2m32s

NAME                                             DESIRED   CURRENT   READY   AGE
replicaset.apps/api-deployment-7d9878b9d6        2         2         2       2m18s
replicaset.apps/frontend-deployment-64989bdcff   2         2         2       2m3s
replicaset.apps/redis-deployment-8656cb7577      1         1         1       2m32s

```

