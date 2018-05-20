set JRE_HOME=C:\tools\Java\jdk1.8.0_45\jre
set JAVA_HOME=C:\tools\Java\jdk1.8.0_45
set PATH=%PATH%;C:\tools\Java\jdk8.0_45\bin;

set MAVEN_OPTS=-Xmx4G
rem C:/tools/maven3.0.3/bin/mvn install -DskipTests=true
mvn clean install -DskipTests=true