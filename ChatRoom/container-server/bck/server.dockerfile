FROM ubuntu:18.04
COPY . /HelloWorld
VOLUME /HelloWorld
WORKDIR /HelloWorld/
RUN apt-get update
RUN apt-get install -y gcc
RUN apt-get install -y libpq-dev
#compila il server quando crei il container
RUN gcc server.c -o server -lpq -pthread
#fai partire il server passandogli come argomento la porta
CMD ["./server","17000"]
EXPOSE 17000:17000