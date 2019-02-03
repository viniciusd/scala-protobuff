FROM hseeberger/scala-sbt

RUN mkdir -p /protobuff
RUN mkdir -p /protobuff/out

ADD ./* /protobuff/

WORKDIR /protobuff/

ENTRYPOINT ["sbt"]
