![](imgs/SPARKplusKUBE.png)

# Spring Boot + Spark Standalone mode + Docker + Kubernetes practice

This module was created to explore the possibilities of integrating a Spring Boot web application with
Spark libraries that improve application performance on tasks that process large amounts of data.
In practice, Spark applications require high hardware performance, which can be provided by using
several (sometimes a plenty) computers united in a single cluster system. Spark supports different 
cluster systems mentioned in the TUTORIAL.md file of this branch.

    This implementation in order to simplify realization offers place all Kubernetes utils in ROOT 
    of module (.../SparkClusters) and use for informational purposes standard Windows PowerShell console 
    program integrated in IntelliJ IDEA IDE.

## Stages

Pre-requisites

1. Download JDK and add JAVA_HOME = <path_to_jdk_> as an environment variable.
2. Download Spark and add SPARK_HOME=<path_to_spark>. If you choose to download spark pre-built with particular 
version of hadoop, no need to download it explicitly in step 3. In this case it is suitable to assign 
HADOOP_HOME = %SPARK_HOME%
3. Download Hadoop and add HADOOP_HOME=<path_to_hadoop> and add %HADOOP_HOME%\bin to PATH variable. 
4. Download winutils.exe (for the same Hadoop version as above) and place it under %HADOOP_HOME%\bin.

<details><summary style="font-size: 18px">1. Jar file creation and first run</summary>

Inside the root module directory (.../SparkClusters), do a:

Build app jar file:

```shell
mvn clean install
```

Run the Spring Boot App (it requires a customized java 11 environment and local TCP ports 8080
and 4040 must be free):

```shell
cd ..; java -jar ./StandaloneApp/target/StandaloneApp-1.0.jar
```

Now go to [http://localhost:9090/](http://localhost:9090/) where you can choose Spark method to
execute:
- to test Spark SQL DataSet filter method - [http://localhost:9090/localStandalone/selectionReport](http://localhost:9090/localStandalone/selectionReport);
- to test Spark SQL DataSet sorting method - [http://localhost:9090/localStandalone/sortingReport](http://localhost:9090/localStandalone/sortingReport);

As a result, you will get reports with execution time dimension inside.

Spark UI service started at [http://localhost:9190/](http://localhost:9190/).

To stop application press "Ctrl + c".

</details>

<details><summary style="font-size: 18px">2. App within Docker environment</summary>

NB!  **Docker** daemon has to be running!

Docker image creation

```shell
docker build --rm -t spring-spark-app .
```

Checking the functionality of the app with start/stop docker container

```shell
docker run --name=spring-spark-app-test --rm -p 9090:9090 -p 9190:9190 spring-spark-app
```

Now go to [http://localhost:9090/](http://localhost:9090/) and [http://localhost:9190/](http://localhost:9190/)
to check the result.

NB! To release ports for later use, you need to stop the Docker container by enforce closing of
console window or with a command:
```shell
docker stop spring-spark-app-test
```
</details>

<details><summary style="font-size: 18px">3. Kubernetes environment setting</summary>

Download **Kind util**:

```shell
curl.exe -Lo kind.exe https://kind.sigs.k8s.io/dl/v0.17.0/kind-windows-amd64
```

Download **kubectl util**:

```shell
curl.exe -Lo kubectl.exe https://dl.k8s.io/release/v1.26.0/bin/windows/amd64/kubectl.exe
```

NB!  **Docker** daemon has to be running!

Create a Kind default cluster (this command will download needed 'kindest/node' image, if it doesn't exist
in local Docker repo):

```shell
./kind create cluster --name kube-cluster
```

More usage can be discovered with:
```shell
./kind create cluster --help
```

Optionally, delete redundant cluster:
```shell
./kind delete cluster --name <cluster-name>
```

    Optionally, you can collect all Kubernetes utils files (like kind.exe and kubectl.exe in custom folder and 
    add path of this folder to system environment variable PATH.

</details>

<details><summary style="font-size: 18px">4. Deploy app to Kubernetes Kind local environment</summary>

Loading an Image Into Your Cluster:
```shell
./kind load docker-image spring-spark-app:latest --name kube-cluster
```
<img src="./imgs/imagesTag_Note.png" width="600" alt="mount_volume">

You can get a list of images present in the cluster node by using command (where kube-cluster-control-plane
is the name of the Docker container, obtained by adding '-control-plane' to the cluster name defined 
in kind command create cluster with flag --name):
```shell
docker exec -it kube-cluster-control-plane crictl images
```
Output has to contain row:

    docker.io/library/spring-spark-app         latest               .............       ...MB

Kube pod creation and running with YAML files:
    kubectl apply -f SpringSparkK8SCommon.yaml
```shell
./kubectl apply -f SpringSparkK8SCommon.yaml
```

Let's check pod and service creation result inside Kubernetes cluster:
```shell
./kubectl get pods
```
Output has to contain rows (STATUS must be - Running):

    NAME                               READY   STATUS    RESTARTS   AGE
    ssk-test-deploy-56b55bcf4d-xkqqs   1/1     Running   0          ..s

```shell
./kubectl get services
```
Output has to contain rows:

    NAME               TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)                     AGE
    ssk-test-service   LoadBalancer   xx.xx.xx.xxx   <pending>     80:...../TCP,81:...../TCP   ..s

Kubernetes implies using of online connection to cluster with suppoerted API and CoreDNS within.
Get CoreDNS address we can with command:
```shell
./kubectl cluster-info
```
</details>
<details><summary style="font-size: 18px">5. Kubernetes Dashboard setting and access to application</summary>

The Dashboard UI is not deployed by default. To deploy it, run the following command:
```shell
./kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.6.1/aio/deploy/recommended.yaml
```

Dashboard only supports logging in with a Bearer Token. To create we need to create sample user:
```shell
./kubectl apply -f Dashboard-adminuser.yaml
```

Getting a Bearer Token:
```shell
./kubectl -n kubernetes-dashboard create token admin-user
```

To enable access to the Dashboard we use the kubectl command-line tool:
```shell
./kubectl proxy
```

Kubectl will make Dashboard available at 
[http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/](http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/)

Now copy the token and paste it into the 'Enter token field' on the login screen.
![](imgs/tokenForm.png)
NB! We need correct copy in buffer of this token, without row ending hidden chars. For these purposes,
to run the above commands, it may be suitable to change Shell application.

Success:
![](imgs/kubernetesGUI.png)

Connection with Kubernetes API is possible only with auth features (like tokens, certificates etc),
which don't supported by a browsers. To provide connection between pod in Kubernetes cluster and any browser we can use internal service of
**Kubectl util** (commands must be run in separated console windows).

NB! This commands may interrupt your internet connection!

For our Spring app port ('-n default' - is definition of namespace):
```shell
./kubectl port-forward svc/ssk-test-service 9090:80 -n default
```
![](imgs/springAppStart.png)

For SparkClusters application UI:
```shell
./kubectl port-forward svc/ssk-test-service 9190:81 -n default
```
![](imgs/sparkUI.png)

In Kubernetes, namespaces provides a mechanism for isolating groups of resources within a single cluster.
Names of resources need to be unique within a namespace, but not across namespaces. Namespace-based scoping
is applicable only for namespaced objects (e.g. Deployments, Services, etc) and not for cluster-wide objects
(e.g. StorageClass, Nodes, PersistentVolumes, etc).

Now go to [http://localhost:9090/](http://localhost:9090/) where you can choose Spark method to
execute:
- to test Spark SQL DataSet filter method - [http://localhost:9090/localStandalone/selectionReport](http://localhost:9090/localStandalone/selectionReport);
- to test Spark SQL DataSet sorting method - [http://localhost:9090/localStandalone/sortingReport](http://localhost:9090/localStandalone/sortingReport);

As a result, you will get reports with execution time dimension inside.

Spark UI service started at [http://localhost:9190/](http://localhost:9190/).
</details>

## Resources

Here are listed the essential resources needed for the implementation work:

* [Cluster Mode Overview](https://spark.apache.org/docs/latest/cluster-overview.html)
* [Running Spark on Kubernetes](https://spark.apache.org/docs/latest/running-on-kubernetes.html)
* [Kubernetes install Tools](https://kubernetes.io/docs/tasks/tools/)
* [Kubernetes API Overview](https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.20/#-strong-api-overview-strong-)

