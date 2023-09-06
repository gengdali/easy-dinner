# 镜像java版本，这里有坑，你可以看着项目里的pom.xml文件确定自己的版本，如果有问题的话多尝试几个
FROM openjdk:17
  # VOLUME 指定了持久化文件(容器卷)目录为/tmp
VOLUME /tmp
  # 将jar包添加到容器中并更名为test2.jar
ADD easy-dinner-admin-1.0-SNAPSHOT.jar easy-dinner-admin-1.0-SNAPSHOT.jar
  # 暴露端口号
EXPOSE 8082
  # 容器启动命令
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/test2.jar"]