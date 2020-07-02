# pod-informer-java
Lifted from rohanKanojia InformerDemo.java on github

try building with:

```mvn clean install```


and running with:

```java -jar target/pod-informer-java-1.0-SNAPSHOT-jar-with-dependencies.jar```


the below plugin dependency version is where it is because on windows i had to use snapshot build of jkube to make it work.

Then I just did:
```sh
mvn k8s:build
mvn k8s:resource
mvn k8s:apply
```
others may be able to make it work with the current published version on public repo.

```
<groupId>org.eclipse.jkube</groupId>
<artifactId>kubernetes-maven-plugin</artifactId>
<version>1.0.0-SNAPSHOT</version>
```
