FROM gradle:6-jdk11

COPY src /opt/functional-tests/src
COPY build.gradle.kts /opt/functional-tests/build.gradle.kts
COPY settings.gradle.kts /opt/functional-tests/settings.gradle.kts
ADD https://raw.githubusercontent.com/ForgeCloud/ob-deploy-tools/master/update-hosts.sh /bin
RUN ["chmod", "+x", "/bin/update-hosts.sh"]

WORKDIR /opt/functional-tests

ENTRYPOINT [ "update-hosts.sh" ]
