set JRE_HOME=C:\tools\Java\jdk1.8.0_45\jre
set JAVA_HOME=C:\tools\Java\jdk1.8.0_45
set PATH=%PATH%;C:\tools\Java\jdk8.0_45\bin;

set MAVEN_OPTS=-Xmx2G
mvn.bat clean install -DskipTests=true