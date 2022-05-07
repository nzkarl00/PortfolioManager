fuser -k 9500/tcp || true

source staging-identityprovider-env.sh

env SPRING_PROFILES_ACTIVE=staging \
  java -jar staging-identityprovider/libs/identityprovider-0.0.1-SNAPSHOT.jar --spring.application.name=identity-provider --grpc.server.port=9500
