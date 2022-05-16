fuser -k 10500/tcp || true
fuser -k 10502/tcp || true

source production-identityprovider-env.sh

env SPRING_PROFILES_ACTIVE=prod \
  java -jar production-identityprovider/libs/identityprovider-0.0.1-SNAPSHOT.jar --spring.application.name=identity-provider --grpc.server.port=10502
