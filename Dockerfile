FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

COPY src /app/src
RUN javac -d /app/out $(find /app/src -name "*.java")

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/out /app/out

ENV MODE=apigateway
EXPOSE 9003 9004 9005 9006

ENTRYPOINT ["/bin/sh", "-c"]
CMD ["if [ \"$MODE\" = apigateway ]; then java -cp /app/out ApiGateway.main; else java -cp /app/out ConputadorDeBordo.main; fi"]
