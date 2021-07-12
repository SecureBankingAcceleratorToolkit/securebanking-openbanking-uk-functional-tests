FROM gradle:7.1.1-jdk11

COPY src /opt/functional-tests/src
COPY build.gradle.kts /opt/functional-tests/build.gradle.kts
COPY settings.gradle.kts /opt/functional-tests/settings.gradle.kts
ADD https://raw.githubusercontent.com/ForgeCloud/ob-deploy-tools/master/update-hosts.sh /bin
RUN ["chmod", "+x", "/bin/update-hosts.sh"]

WORKDIR /opt/functional-tests

RUN ["gradle", "clean", "build", "-x", "test"]

ENTRYPOINT update-hosts.sh
