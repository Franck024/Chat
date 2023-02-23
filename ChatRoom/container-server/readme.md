# Istruzioni sul container del server:
## Fare solo al primo avvio
* Creare prima il volume:
`docker volume create serverchatlso`
## Come far partire il container:
`docker-compose -p nomecontainer up`
## Documentazione:
Shell interattiva su docker compose: https://www.baeldung.com/ops/docker-compose-interactive-shell
## Informazioni:
* Fare partire il container sempre con lo stesso nome, altrimenti ogni volta ne crea uno diverso!
* Il sorgente del server che partirà sul container deve chiamarsi server.c e deve sostituire il file omonimo nella cartella chatserver.
* Nel volume serverchatlso si può spostare il file C del nuovo server, ad ogni avvio del container verrà compilato, per accedere al volume su windows explorer bisogna aprire nella schermata dove ci sono tutte le istanze di wsl installate nella cartella docker-desktop-data/data/docker/volumes/serverchatlso/_data
* Nella cartella bck c'è la versione del docker file che invece che compilare ogni volta il server ad ogni avvio del container lo compila solo la prima volta che viene creato il container, poi lo fa partire solo.
