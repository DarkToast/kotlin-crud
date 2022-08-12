FROM image-registry.openshift-image-registry.svc:5000/iot/eclipse-temurin:17-alpine

RUN mkdir /opt/app
COPY ./crud /opt/app/

EXPOSE 8080

CMD ["/bin/sh", "/opt/app/bin/crud"]