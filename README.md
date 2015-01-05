match-api
===========

 - start server with ```./sbt run```
 - run tests with ```./sbt test```
    - test run configurations should include VM Parameter ```-Dconfig.file=src/main/resources/test.conf```
 - package fat jar with ```./sbt assembly```
 - run jar with ```java -jar $JAR```
    - override reference conf with ```java -jar $JAR -Dconfig.file=$CONF``` (confs merge with last-one-wins)
    - production & staging confs are supplied in ```src/main/resources``` with the following replaceable tokens:
        - ```$DATABASE_USERNAME$``` - replace this for the db username
        - ```$DATABASE_PASSWORD$``` - replace this for the db password