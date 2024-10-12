# Stage 1: Build
FROM gradle:jdk17 as buildstage

# Prepare Gradle
WORKDIR /work
COPY gradle/ gradle/
COPY gradlew build.gradle.kts settings.gradle.kts gradle.properties ./

# Copy only the necessary build files for dependency resolution
COPY server/build.gradle.kts /work/server/
COPY shared/build.gradle.kts /work/shared/

# Preliminary gradle build to resolve dependencies and improve layer caching
RUN gradle build --no-daemon || true

# Copy source code
COPY shared/src /work/shared/src
COPY server/src /work/server/src

# Run final build
RUN gradle clean :server:installDist --no-daemon

# Stage 2: Run
FROM eclipse-temurin:17-jre

# Create a non-root user
RUN useradd --create-home healthssi



# Copy the built application from the build stage
COPY --from=buildstage /work/server/build/install/server /app
COPY --from=buildstage /work/server/src/main/resources/application-test.yaml /app/config/
COPY --from=buildstage /work/server/src/main/resources/application.yaml /app/config/
COPY --from=buildstage /work/server/src/main/resources/refdata/Articles_ALL_ALL_20240429170557.xml /app/config/
COPY --from=buildstage /work/server/src/main/resources/healthssi-privacy-policy_de.pdf /app/config/

# Standard setup for the runtime environment
WORKDIR /app
RUN chown -R healthssi:healthssi /app
USER healthssi

ENV CONFIG_FILE=config/application-test.yaml

EXPOSE 7004
ENTRYPOINT ["sh", "-c", "./bin/server -config=$CONFIG_FILE"]

# run in basedir of repo:
# docker buildx build -f server/Dockerfile -t healthwallet-pis:latest .
# docker tag healthwallet-pis:latest ctodeakai/healthwallet-pis:latest
# docker push ctodeakai/healthwallet-pis:latest

