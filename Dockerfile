# ---------- build: compila o executavel nativo (Linux) com GraalVM ----------
FROM ghcr.io/graalvm/native-image-community:25 AS build
WORKDIR /build

ARG MAVEN_VERSION=3.9.11
RUN microdnf install -y curl tar gzip findutils &&     curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" -o /tmp/maven.tar.gz &&     tar -xzf /tmp/maven.tar.gz -C /opt &&     ln -s "/opt/apache-maven-${MAVEN_VERSION}" /opt/maven &&     rm /tmp/maven.tar.gz
ENV PATH="/opt/maven/bin:${PATH}"

COPY pom.xml .
COPY domain/pom.xml domain/pom.xml
COPY spring/pom.xml spring/pom.xml

COPY domain/src domain/src
COPY spring/src spring/src

# profile "native" (pom do modulo spring) gera o executavel na fase package
RUN mvn -pl spring -am -Pnative -DskipTests clean package

# ---------- runtime ----------
FROM debian:bookworm-slim
WORKDIR /app
EXPOSE 8083
COPY --from=build /build/spring/target/app /app/app
ENTRYPOINT ["/app/app"]
