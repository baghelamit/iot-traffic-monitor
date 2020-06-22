# IoT Traffic Management App using Helm Charts

This page has details on deploying [Fleet Management IoT App](https://github.com/datastax/datastax-iot-fleet-management) on [Kubernetes](https://kubernetes.io) using the `Helm Charts` feature. [Helm Charts](https://github.com/kubernetes/charts) can be used to deploy the app and its necessary dependencies on any configuration that customer prefers.

## Setup Requirements
### Install Helm: 2.8.0 or later
One can install helm by following [these instructions](https://github.com/kubernetes/helm#install).
Check the version of helm installed using the following command:
```
$ helm version
Client: &version.Version{SemVer:"v2.12.3", GitCommit:"eecf22f77df5f65c823aacd2dbd30ae6c65f186e", GitTreeState:"clean"}
Server: &version.Version{SemVer:"v2.12.3", GitCommit:"eecf22f77df5f65c823aacd2dbd30ae6c65f186e", GitTreeState:"clean"}
```

### Configuration
Any one of the following container/kubernetes runtime environments will work. Having 4+ CPUS and 10+GB of RAM across the cluster should help.

- [Minikube](https://kubernetes.io/docs/setup/minikube/) version 0.33+. *Tip* Use *minikube start --cpus 4 --memory 6144* option to increase the resource allowance.
- Any Kubernetes engine such as [Google Kubernetes Engine (GKE)](https://cloud.google.com/kubernetes-engine/) or [Pivotal Container Service (PKS)](https://pivotal.io/platform/pivotal-container-service) is also an option.

The [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/) tool should be made to point to one of these clusters.
Check the version using the following command:
```
$ kubectl version
Client Version: version.Info{Major:"1", Minor:"11", GitVersion:"v1.11.0", GitCommit:"91e7b4fd31fcd3d5f436da26c980becec37ceefe", GitTreeState:"clean", BuildDate:"2018-06-27T22:29:27Z", GoVersion:"go1.10.3", Compiler:"gc", Platform:"darwin/amd64"}
Server Version: version.Info{Major:"1", Minor:"11+", GitVersion:"v1.11.6-gke.2", GitCommit:"04ad69a117f331df6272a343b5d8f9e2aee5ab0c", GitTreeState:"clean", BuildDate:"2019-01-04T16:19:46Z", GoVersion:"go1.10.3b4", Compiler:"gc", Platform:"linux/amd64"}
```

### Clone the repositories
First, setup the required repositories:
```
mkdir -p ~/code
cd ~/code
git clone https://github.com/confluentinc/cp-helm-charts.git
git clone https://github.com/jatin7/datastax-db.git
git clone https://github.com/jatin7/datastax-iot-fleet-management.git
git clone https://github.com/jatin7/datastax-kafka-connector.git
```

These contain the helm charts need for the three main components: Kafka producer, DataStax DB sink and the current IoT App.

*Note*: We plan to pre-package these helm charts, so as to avoid all the git clones.

## Install DataStax DB
Install a DataStax DB cluster using [these instructions](https://docs.datastax.com/latest/deploy/kubernetes/helm-chart/).

Sample command to setup datastax-master and datatax-tserver [DataStax DB cluster](https://docs.datastax.com/latest/architecture/concepts/universe/) on minikube or a real cluster using `n1-standard-8` GKE like instances.
```
cd ~/code/datastax-db/cloud/kubernetes/helm/
* Using minikube
  helm install --name datastax-iot datastax --set resource.master.requests.cpu=0.1,resource.master.requests.memory=100Mi,resource.tserver.requests.cpu=0.1,resource.tserver.requests.memory=100Mi,replicas.master=1,replicas.tserver=1 --wait
* On Kubernetes engine
  helm install --name datastax-iot datastax --wait
```

These DataStax pods should look like:
```
$ kubectl get pods
NAME                                             READY     STATUS             RESTARTS   AGE
datastax-master-0                                      1/1       Running            0          26m
datastax-tserver-0                                     1/1       Running            0          25m
```

## Install Confluent Platform
Based on [CP-Kafka](https://github.com/confluentinc/cp-helm-charts/tree/master/charts/cp-kafka), the following brings up only the components needed for the IoT App.

```
cd ~/code/cp-helm-charts
helm install --name kafka-demo --set cp-kafka-rest.enabled=false,cp-kafka-connect.enabled=false,cp-kafka.brokers=1,cp-zookeeper.servers=1,cp-kafka.configurationOverrides.offsets.topic.replication.factor=1 . --wait
```

The same command applies to both minikube and hosted Kubernetes engines. These Kafka related pods should look as follows:
```
$ kubectl get pods
NAME                                             READY     STATUS             RESTARTS   AGE
kafka-demo-cp-kafka-0                            2/2       Running            0          1m
kafka-demo-cp-ksql-server-64ff7f5579-hhvqq       2/2       Running            2          1m
kafka-demo-cp-schema-registry-774589f75f-2pbtg   2/2       Running            1          1m
kafka-demo-cp-zookeeper-0                        2/2       Running            0          1m
...
```

## Setup dependecies
This section sets up the required dependencies such as Kafka connect jars and property files, KSQL streams, datastax DB tables needed by the IoT App.

### Kafka DataStax Connect Sink
The [datastax Connect Sink](https://github.com/DataStax/datastax-kafka-connector) related depedencies and properties files can be copied into the Kafka cluster using the following script:

```
cd ~/code/datastax-iot-fleet-management/iot-ksql-processor/resources/kubernetes/
./setup_yb_connect_sink.sh --kafka_helm_name kafka-demo
```
*Note*: If a name other than `kafka-demo` was used for Confluent platform, need to set that headless service info in the `kafka.connect.properties` and `kafka.ksql.connect.properties` files.

### Create origin Kafka topic
Run the following to create the root topic that simulates a fleet of different types of vehicles on the move and sending their stream of information.
```
kubectl exec -it kafka-demo-cp-kafka-0 -c cp-kafka-broker /usr/bin/kafka-topics -- --create --zookeeper kafka-demo-cp-zookeeper:2181 --replication-factor 1 --partitions 1 --topic iot-data-event
```
This needs to be done only once per Kafka cluster.

### Setup the KSQL streams/tables

First, create the ksql cli client pod using the example in the `cp-helm-charts` repo.

*Note*: First, edit this file to have the current kafka container name: *bootstrap-server=kafka-demo-cp-kafka:9092*
```
cd ~/code/cp-helm-charts/examples
kubectl apply -f ksql-demo.yaml --wait
```

Copy the stream setup commands files to the container and create the KSQL streams/tables needed by the IoT App.
```
kubectl cp ~/code/datastax-iot-fleet-management/iot-ksql-processor/setup_streams.ksql ksql-demo:/opt -c ksql

kubectl exec -it ksql-demo -c ksql ksql  http://kafka-demo-cp-ksql-server:8088  <<EOF
RUN SCRIPT '/opt/setup_streams.ksql';
exit
EOF
```

### Create CQL tables
The app uses Cassandra Query Language compatible [CQL](https://docs.datastax.com/latest/api/cassandra/) to store the data in datastax DB. Run the script to create the tables using:

```
kubectl cp ~/code/datastax-iot-fleet-management/resources/IoTData.cql datastax-tserver-0:/home/datastax
kubectl exec -it datastax-tserver-0 /home/datastax/bin/cqlsh -- -f /home/datastax/IoTData.cql
```

### Start the datastax Kafka Connect sink

Run the connect script for the origin table to be saved. The TTL set on that orign table will purge older data automatically.
```
kubectl exec -it kafka-demo-cp-kafka-0  -c cp-kafka-broker /usr/bin/connect-standalone -- /etc/kafka/kubernetes/kafka.connect.properties /etc/kafka/kubernetes/origin.sink.properties >& kafka_orgin_sink_out.txt
```

Similarly for the other aggregated tables, setup the sink connectors:
```
kubectl exec -it kafka-demo-cp-kafka-0  -c cp-kafka-broker /usr/bin/connect-standalone -- /etc/kafka/kubernetes/kafka.ksql.connect.properties /etc/kafka/kubernetes/total_traffic.sink.properties /etc/kafka/kubernetes/window_traffic.sink.properties /etc/kafka/kubernetes/poi_traffic.sink.properties >& kafka_ksql_sink_out.txt
```

## Start the IoT App

Grab the Kafka, Zookeeper and datastax-tserver headless service names:
```
$ kubectl get services
NAME                               TYPE           CLUSTER-IP    EXTERNAL-IP       PORT(S)                               AGE
kafka-demo-cp-kafka-headless       ClusterIP      None           <none>           9092/TCP                              1h
kafka-demo-cp-zookeeper-headless   ClusterIP      None           <none>           2888/TCP,3888/TCP                     1h
datastax-tservers                        ClusterIP      None           <none>           7100/TCP,9000/TCP,6379/TCP,9042/TCP   2h
```

Use these as the endpoints for services to start the IoT app
```
cd ~/code/datastax-iot-fleet-management/kubernetes/helm
helm install datastax-iot-helm --name iot-demo --set kafkaHostPort=kafka-demo-cp-kafka-headless:9092,zookeeperHostPort=kafka-demo-cp-zookeeper-headless:2181,yugabyteDBHost=datastax-tservers --wait
```

The pod for this app looks like
```
NAME                                             READY     STATUS             RESTARTS   AGE
iot-demo-datastax-iot-helm-fb6b7db6f-rwskv             2/2       Running            0          46s
```

This pod has two containers:
- One ingests data into the origin topic of `iot-data-event`, which also get transformed into other streams/tables via KSQL and published to new streams/tables.
- Other container reads the CQL tables from datastax DB and reports in the springboard based UI.

### Check the IoT App UI
When using minikube, run the following to expose the load balancer endpoints for datastax DB UI and the app UI respectively.
```
minikube service  iot-demo-datastax-iot-helm  --url
```

For non-minikube setups, one can use the `EXTERNAL-IP`:8080 from app's load balancer service to get the visual output of the fleet movement analytics.
```
$ kubectl get services
NAME                               TYPE           CLUSTER-IP     EXTERNAL-IP      PORT(S)                               AGE
iot-demo-datastax-iot-helm               LoadBalancer   10.7.254.87    104.198.9.175    8080:31557/TCP                        1m
```

This page auto refreshes every 5seconds or so,

## datastax DB Metrics UI
Independent of the app, the user can also monitor the server side IOPS and related metrics at the `datastax-master` UI endpoint. 

On minikube setup, the following command returns the url for master UI.
```
minikube service  datastax-master-ui --url
```

For Kubernetes engines, the `EXTERNAL-IP`:7000 will give the same info:
```
kubectl get services
NAME                               TYPE           CLUSTER-IP     EXTERNAL-IP      PORT(S)                               AGE
datastax-master-ui                       LoadBalancer   10.7.241.218   35.227.144.242   7000:30362/TCP                        1h
```

The `http://EXTERNAL-IP:7000/tablet-servers` page contains per server current read/write metrics as well as resource usage information.

One can scale the database cluster to spread the load to account for ever growing data size with streaming apps. This command scales the database servers to 4 pods.
```
cd ~/code/datastax-db/cloud/kubernetes/helm
helm upgrade datastax-iot datastax --set replicas.tserver=4 --wait
```
The read and write load from the app is distributed to the new added pods as well.


## Next Steps:
- Package datastax-kafka-sink jars more cleanly to reduce steps.
- Fork cp-helm-charts and add pre/post kubernetes hooks to ease yb sink connection setup into kafka broker.
- Get YB sink into https://github.com/Landoop/kafka-helm-charts/tree/master/charts.
