fuser -k 10501/tcp || true

source production-portfolio-env.sh

env SPRING_PROFILES_ACTIVE=prod \
    java -jar production-portfolio/libs/portfolio-0.0.1-SNAPSHOT.jar \
    --server.port=10501 \
    --spring.application.name=portfolio \
    --grpc.client.identity-provider-grpc-server.address=static://127.0.0.1:10502 \
    --grpc.client.identity-provider-grpc-server.enableKeepAlive=true \
    --grpc.client.identity-provider-grpc-server.keepAliveWithoutCalls=true \
    --grpc.client.identity-provider-grpc-server.negotiationType=plaintext
