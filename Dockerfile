FROM eclipse-temurin:17-alpine

RUN mkdir /opt/app
COPY ./build/install/crud /opt/app/

EXPOSE 8080

CMD ["/bin/sh", "/opt/app/bin/crud"]