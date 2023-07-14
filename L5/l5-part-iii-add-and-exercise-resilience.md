# L5 - Part III - Add and Exercise Resilience

## Subtask 3.1 - Add Deployments

* [x] Create a deployment version of your application (redis-deploy.yaml instead of redis-pod.yaml) and modify/extend them to contain the required deployment parameters

[redis-deploy.yaml](config/redis-deploy.yaml)
[redis-deploy.yaml](config/redis-deploy.yaml)
[redis-deploy.yaml](config/redis-deploy.yaml)

* [x] Make sure to have always 2 instances of the API and Frontend running.

```
[INPUT]
In the configuration file we set replicas to 2 for the frontend and the API and 1 for Redis. 

[OUTPUT]
-
```

* [x] Use only 1 instance for the Redis-Server. Why ?

Using multiple instances of redis would produce an incoherent state of the application. 

When multiples servers query multiple redis servers we produce different data on each one of them and that's not what we want. We want to keep a single state for our todo application. 

We did the same thing in our previous lab with Amazon RDS, we used one database to hold the data, so that for instance a user created while accessing one drupal server would also be available on the other drupal server.

* [x] Delete all application Pods and replace them with deployment versions

```
[INPUT]
kubectl delete pod/api pod/frontend pod/redis service/api-svc service/frontend-svc service/redis-svc
```

* [x] Verify that the application is still working and the Replica Sets are in place

```
[INPUT]
kubectl get all

[OUTPUT]
NAME                                      READY   STATUS    RESTARTS   AGE
pod/api-deployment-9fbdc5cff-4g68r        1/1     Running   0          12m
pod/api-deployment-9fbdc5cff-8ztbd        1/1     Running   0          12m
pod/frontend-deployment-7bfcf64b8-7874p   1/1     Running   0          13m
pod/frontend-deployment-7bfcf64b8-pqsh5   1/1     Running   0          13m
pod/redis-deployment-68748bc746-mcmzt     1/1     Running   0          13m

NAME                   TYPE           CLUSTER-IP   EXTERNAL-IP      PORT(S)        AGE
service/api-svc        ClusterIP      10.0.4.120   <none>           8081/TCP       13m
service/frontend-svc   LoadBalancer   10.0.10.95   34.155.206.234   80:32478/TCP   13m
service/kubernetes     ClusterIP      10.0.0.1     <none>           443/TCP        4h27m
service/redis-svc      ClusterIP      10.0.6.93    <none>           6379/TCP       13m

NAME                                  READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/api-deployment        2/2     2            2           12m
deployment.apps/frontend-deployment   2/2     2            2           13m
deployment.apps/redis-deployment      1/1     1            1           13m

NAME                                            DESIRED   CURRENT   READY   AGE
replicaset.apps/api-deployment-9fbdc5cff        2         2         2       12m
replicaset.apps/frontend-deployment-7bfcf64b8   2         2         2       13m
replicaset.apps/redis-deployment-68748bc746     1         1         1       13m
```

## Subtask 3.2 - Verify the functionality of the replica sets

* [x] What happens if you delete a Frontend or API Pod?

```
[INPUT]
kubectl delete pod/api-deployment-9fbdc5cff-4g68r

It will recreate a new pod to replace the deleted one.

[OUTPUT]
pod "api-deployment-9fbdc5cff-4g68r" deleted
```

* [x] How long does it take for the system to react?

```
[INPUT]
It was instantaneous (~ 5 seconds). You can't even tell the difference.

[OUTPUT]
-
```

* [x] What happens when you delete the Redis Pod?

```
[INPUT]
kubectl delete pod/redis-deployment-68748bc746-mcmzt

It is the same as the other pods. The deployment will always replace the pods if they are deleted. You have to delete the deployment itself for it to don't create a pod.

[OUTPUT]
 kubectl delete pod/redis-deployment-68748bc746-mcmzt
```

* [x] How can you change the number of instances temporarily to 3?

{% hint style="info" %}
Look for scaling in the deployment documentation
{% endhint %}

```
[INPUT]
kubectl scale deployment redis-deployment --replicas=3

[OUTPUT]
deployment.apps/api-deployment scaled
```

* [x] What autoscaling features are available?

```
[INPUT]
Kubernetes offer the following features :
- Horizontal scaling on pods with HorizontalPodAutoscalers (pod-based)
- Vertical scaling on pods (live metrics to set limits on container resources)
- Cluster scaling for nodes (changing the number of cluster nodes)

```

* [x] Which metrics are used?

```
[INPUT]
It uses the common metrics for scalling like :
- CPU utilization
- Memory Utilization
- Request Latency
- Request Rate
- Also custom metrics

```

* [x] How can you update a component?

{% hint style="info" %}
see "Updating a Deployment" in the deployment documentation
{% endhint %}

```
[INPUT]
kubectl apply -f <path-to-updated-config-file.yaml>

[OUTPUT]
Example with redis-deployment:
deployment.apps/redis-deployment created
```

## Subtask 3.3 (optional) - Put autoscaling in place and load-test it

* [x] On the GKE cluster deploy autoscaling on the Frontend with a target CPU utilization of 30% and number of replicas between 1 and 4. Load-test using JMeter.

```
[INPUT]
-

[OUTPUT]
-
```

## Clean up

At the end of the lab session:

* Delete the GKE cluster
* Delete the project
*   If you want to remove the Minikube Virtual Machine from your local machine, run

    ```
    $ minikube stop
    $ minikube delete
    ```





