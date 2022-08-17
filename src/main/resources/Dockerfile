FROM image-registry.openshift-image-registry.svc:5000/iot/eclipse-temurin:17-alpine

RUN mkdir /opt/app
COPY ./*/bin /opt/app/bin/
COPY ./*/lib /opt/app/lib/

EXPOSE 8080

CMD ["/bin/sh", "/opt/app/bin/crud"]
