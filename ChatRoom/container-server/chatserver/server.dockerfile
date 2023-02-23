FROM ubuntu:18.04
COPY . /HelloWorld
VOLUME /HelloWorld
WORKDIR /HelloWorld/
RUN apt-get update
RUN apt-get install -y gcc
RUN apt-get install -y libpq-dev
#Fai partire lo script che compila il server e fa partire il server sulla porta 17000 passata come argomento allo script.
CMD ["bash","compilaparti.sh","17000"]
EXPOSE 17000:17000