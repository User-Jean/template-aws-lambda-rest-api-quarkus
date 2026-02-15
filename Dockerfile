FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:25.0-java25 AS build

COPY --chown=quarkus:quarkus --chmod=0755 mvnw /code/mvnw
COPY --chown=quarkus:quarkus .mvn /code/.mvn
COPY --chown=quarkus:quarkus pom.xml /code/

USER quarkus
WORKDIR /code
RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.8.1:go-offline

COPY src /code/src
RUN ./mvnw -Pnative -DskipTests package

# Stage 2: runtime image compatible con AWS Lambda (custom runtime)
# La runtime de Lambda busca un ejecutable llamado /bootstrap
FROM public.ecr.aws/lambda/provided:al2 AS runtime

COPY --from=build /code/target/*-runner /var/runtime/bootstrap

ENV TZ=America/Latina

RUN chmod +x /var/runtime/bootstrap
CMD [ "bootstrap" ]