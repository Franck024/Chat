#!/bin/bash
#Compila il server
gcc server.c -o server -lpq -lpthread
#Esegui il server passandogli come argomento l'argomento passato allo script.
./server $1