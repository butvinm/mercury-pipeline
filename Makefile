.PHONY: all dev deploy

SOURCES := $(shell find src -name '*.java')
JAR := target/pipeline-0.0.1-SNAPSHOT.jar

SERVER_USER ?=
SERVER_IP ?=
SERVER_SSH = "$(SERVER_USER)@$(SERVER_IP)"

all: dev

$(JAR): $(SOURCES)
	mvn clean package

deploy: $(JAR)
	scp $(JAR) $(SERVER_SSH):~/target
	scp Dockerfile $(SERVER_SSH):~
	ssh $(SERVER_SSH) "docker ps -a -q | xargs -r docker stop || true"
	ssh $(SERVER_SSH) "docker ps -a -q | xargs -r docker kill || true"
	ssh $(SERVER_SSH) "docker build -t pipeline ."
	ssh $(SERVER_SSH) "docker run -p 8080:8080 pipeline </dev/null >/var/log/root-backup.log 2>&1 &"

dev: $(JAR)
	docker build -t mercury.pipeline .
	docker run -p 8080:8080 mercury.pipeline:latest
