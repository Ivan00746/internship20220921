@echo off
copy .\DockerfileSparkCluster %SPARK_HOME%\Dockerfile
copy .\spark-defaults %SPARK_HOME%\conf\spark-defaults.conf
cd %SPARK_HOME%
docker build --rm -t spark-cluster-core .