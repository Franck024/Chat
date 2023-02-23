#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <pthread.h>
#include <netinet/in.h>
#include <netdb.h>
#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <signal.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include "postgresql/libpq-fe.h" 

#define MAX_CONN 100
#define MAX_CLIENT 30
#define USER_LUN 20
#define DIM_BUFF 1024
#define NOME_CHAT 20

struct  Client{
  char username[USER_LUN];
  int sd;
  char chat_corrente[NOME_CHAT];
  int stanza_numero;
  struct Client *partecipanti[MAX_CLIENT];
};

struct NodoLista{
  struct Client *client;
  struct NodoLista *succ;
};



////////////////////VARIABILI GLOBALI///////////////////
pthread_mutex_t clients_mutex = PTHREAD_MUTEX_INITIALIZER;
struct NodoLista *head = NULL;
PGconn *conn;
pthread_mutex_t conn_mutex = PTHREAD_MUTEX_INITIALIZER;


/////////////////////PROTITIPI////////////////
void *handle_connection(void *);
void rimuovi_client(struct Client *c);
void aggiungi_client(struct Client *c);
void manda_messaggio(char *msg,struct Client *c);
void retrieve_users_from_room(const char *room_name, struct Client *c);


//////////////////MAIN//////////////////////////
int main(int argc, char** argv) {
  
  if(argc < 2) {
    printf("Errore, nessuna porta specificata");
    exit(1);
  }

  printf("In attesa del server...\n");

  int port = atoi(argv[1]);
  pthread_t tid;
  int listen_sd, client_sd;
  struct sockaddr_in serv_addr, client_addr;

  serv_addr.sin_family = AF_INET;
  serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
  serv_addr.sin_port = htons(port);

  //Ignora segnali pipe
  signal(SIGPIPE, SIG_IGN);

  listen_sd = socket(AF_INET, SOCK_STREAM, 0);
  if(listen_sd < 0) {
    perror("Errore funzione socket()");
    exit(1);
  }

  if(bind(listen_sd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
    perror("Errore funzione bind()");
    exit(1);
  }

  if(listen(listen_sd, MAX_CONN) < 0) {
    perror("Errore funzione listen()");
    exit(1);
  }

  printf("Il server e' in ascolto sulla porta %s.\n", argv[1]);

  conn = PQconnectdb("dbname=lso host=progettolso.ddns.net user=admin password=password port=5432");

  if (PQstatus(conn) != CONNECTION_OK) {
    printf("Connessione al database fallita: %s\n", PQerrorMessage(conn));
    exit(1);
  }
  

  while(1) {

    socklen_t client_len = sizeof(client_addr);
    client_sd = accept(listen_sd, (struct sockaddr *) &client_addr, &client_len);
    if(client_sd < 0){
      perror("Errore funzione accept()");
      continue;
    }

    printf("Nuova Connessione.\n");

    int ret = pthread_create(&tid, NULL, handle_connection, (void *)&client_sd); 
    if(ret != 0){
      perror("Errore creazione thread");
    }
    continue;
 
  }
  PQfinish(conn);
  return 0;

}



void *handle_connection(void *arg) {
  char buffer_in[DIM_BUFF];
  char buffer_out[DIM_BUFF];
  char buffer_query[DIM_BUFF];

  struct Client *client = (struct  Client*)malloc(sizeof(struct Client ));
  client->sd = *((int *)arg);
  aggiungi_client(client);



  while(1) {

    memset(buffer_in, 0, sizeof(buffer_in));
    memset(buffer_out, 0, sizeof(buffer_out));
    memset(buffer_query, 0, sizeof(buffer_query));

    int n = recv(client->sd, buffer_in, sizeof(buffer_in), 0);
    if (n <=0) {
      /*fprintf(stderr,"Errore lettura da client: %s\n",strerror(errno));
      close(client->sd);
      pthread_exit(NULL);*/
      break;
    }

    char *request_type = strtok(buffer_in, " ");

    if (strcmp(request_type, "login") == 0) {

      char *username = strtok(NULL, " ");
      char *password = strtok(NULL, " ");


      sprintf(buffer_query, "SELECT * FROM Utente WHERE username='%s' AND password='%s'", username, password);
      pthread_mutex_lock(&conn_mutex);
      PGresult *res = PQexec(conn, buffer_query);
      pthread_mutex_unlock(&conn_mutex);
      if (PQntuples(res) > 0) {
     
        snprintf(buffer_out,sizeof(buffer_out),"%s¶%s¶%s\n","login_success",username,password);
        strcpy(client->username, username);
        memset(buffer_query, 0, sizeof(buffer_query));
      } else {
       
        sprintf(buffer_out, "login_failed\n");
        memset(buffer_query, 0, sizeof(buffer_query));
      }
      PQclear(res);


    }else if(strcmp(request_type, "sign_in") == 0) {

      char *username = strtok(NULL, " ");
      char *password = strtok(NULL, " ");

      sprintf(buffer_query, "SELECT * FROM Utente WHERE username='%s' AND password='%s'", username, password);
      pthread_mutex_lock(&conn_mutex);
      PGresult *res = PQexec(conn, buffer_query);
      pthread_mutex_unlock(&conn_mutex);
      if (PQntuples(res) > 0) {

        sprintf(buffer_out, "already_exists\n");
        memset(buffer_query, 0, sizeof(buffer_query));

      }else{

       sprintf(buffer_query, "INSERT INTO Utente (username,password) VALUES ('%s','%s')",username,password);
       pthread_mutex_lock(&conn_mutex);
       PGresult *res = PQexec(conn, buffer_query);
       pthread_mutex_unlock(&conn_mutex);
       if (PQresultStatus(res) == PGRES_COMMAND_OK) {
      
        sprintf(buffer_out, "sign_in_success\n");
        memset(buffer_query, 0, sizeof(buffer_query));
       } else {
        printf("Registrazione fallita");
        sprintf(buffer_out, "sign_in_failed\n");
        memset(buffer_query, 0, sizeof(buffer_query));
       }
       
      }
      PQclear(res);


    } else if (strcmp(request_type, "recupera_chat") == 0) {

     snprintf(buffer_query, sizeof(buffer_query),
"(SELECT  Stanza.nome,Stanza.numero, Messaggio.corpo, Messaggio.tempo_invio, messaggio.mittente "
"FROM Stanza INNER JOIN utentistanza ON stanza.numero=utentistanza.numerostanza "
"LEFT JOIN Messaggio ON Stanza.numero = Messaggio.stanza "
"WHERE utentistanza.username='%s' AND utentistanza.approvato=true AND Messaggio.tempo_invio = (SELECT MAX(tempo_invio) FROM Messaggio WHERE Messaggio.stanza = Stanza.numero) "
"ORDER BY messaggio.tempo_invio ASC) "
"union all "
	"(SELECT  stanza.nome,stanza.numero, messaggio.corpo, messaggio.tempo_invio, messaggio.mittente FROM stanza INNER JOIN utentistanza ON stanza.numero=utentistanza.numerostanza "
"LEFT JOIN messaggio ON stanza.numero=messaggio.stanza WHERE utentistanza.username='%s' AND utentistanza.approvato=true AND messaggio.corpo is null)", client->username, client->username); 
     pthread_mutex_lock(&conn_mutex);
     PGresult *res = PQexec(conn, buffer_query);
     pthread_mutex_unlock(&conn_mutex);
    
    if(PQresultStatus(res) == PGRES_TUPLES_OK){
      strcpy(buffer_out, "user_chat_retrieval_success¶");
      for (int i = 0; i < PQntuples(res); i++)
      {
       char *nome = PQgetvalue(res, i, 0);
       char *id = PQgetvalue(res, i, 1);
       char *corpo = PQgetvalue(res, i, 2);
       char *tempo = PQgetvalue(res, i, 3);
       char *mittente = PQgetvalue(res, i, 4);
  
        if(strlen(corpo)==0 || strlen(tempo) == 0 || strlen(mittente)==0){
         sprintf(buffer_out + strlen(buffer_out), "%s¶%s¶%s¶%s¶%s¶",id,nome,"null","null","null");

        }else{
         sprintf(buffer_out + strlen(buffer_out), "%s¶%s¶%s¶%s¶%s¶",id,nome,mittente,tempo,corpo);

        }
      }
        strcat(buffer_out, "\n");
      } else{
        sprintf(buffer_out, "failed_user_chat_retrieval\n");

      }
     PQclear(res);


    } else if (strcmp(request_type, "recupera_all_chat") == 0) {
  
      snprintf(buffer_query, sizeof(buffer_query),"SELECT s.* FROM Stanza s "); 
      pthread_mutex_lock(&conn_mutex);
      PGresult *res = PQexec(conn, buffer_query);
      pthread_mutex_unlock(&conn_mutex);

      if (PQresultStatus(res) == PGRES_TUPLES_OK){
        strcpy(buffer_out, "chat_retrieval_success¶");
        for (int i = 0; i < PQntuples(res); i++)
        {

         char *nome = PQgetvalue(res, i, 0);
         char *id = PQgetvalue(res, i, 1);

         sprintf(buffer_out + strlen(buffer_out), "%s¶%s¶", id,nome);

        }
        strcat(buffer_out, "\n");
      } else{
        sprintf(buffer_out, "failed_chat_retrieval\n");

      }
     PQclear(res);


    }else if (strcmp(request_type, "create_chat_add_users") == 0) {
     char *request_string = strtok(NULL, "");
     char *chat_room_name = strtok(request_string, "¶");
     char *users_list = strtok(NULL, ""); 

     sprintf(buffer_query, "INSERT INTO Stanza (nome) VALUES ('%s')", chat_room_name);
     pthread_mutex_lock(&conn_mutex);
     PGresult *res = PQexec(conn, buffer_query);
     pthread_mutex_unlock(&conn_mutex);
     if (PQresultStatus(res) != PGRES_COMMAND_OK) {
 
       sprintf(buffer_out, "create_chat_add_users_failed\n");
       PQclear(res);

      }else{
        
      char *username = strtok(users_list, " ");

      sprintf(buffer_query, "SELECT numero FROM Stanza WHERE nome='%s'", chat_room_name);
      pthread_mutex_lock(&conn_mutex);
      res = PQexec(conn,buffer_query);
      pthread_mutex_unlock(&conn_mutex);

     int chat_room_number;
     if (PQntuples(res) > 0) {
       chat_room_number = atoi(PQgetvalue(res, 0, 0));
       }else{
        printf("Errore recupero codice stanza: %s\n", PQerrorMessage(conn));
        sprintf(buffer_out, "create_chat_add_users_failed\n");
        continue;
      }
      PQclear(res);


     while (username != NULL) {
        username = strtok(NULL, " ");

       sprintf(buffer_query, "INSERT INTO utentistanza (numerostanza, username,approvato) VALUES (%d, '%s',true)", chat_room_number, username);
       pthread_mutex_lock(&conn_mutex);
       res = PQexec(conn, buffer_query);
       pthread_mutex_unlock(&conn_mutex);

       if (PQresultStatus(res) != PGRES_COMMAND_OK) {

         sprintf(buffer_out, "create_chat_add_users_failed\n");
         PQclear(res);
         break;
        }

       PQclear(res);
      }

       sprintf(buffer_query, "INSERT INTO utentistanza (numerostanza, username,approvato) VALUES (%d, '%s',true)", chat_room_number, client->username);
       pthread_mutex_lock(&conn_mutex);
       res = PQexec(conn, buffer_query);
       pthread_mutex_unlock(&conn_mutex);

       if (PQresultStatus(res) != PGRES_COMMAND_OK) {

         sprintf(buffer_out, "create_chat_add_users_failed\n");

        }else{

          sprintf(buffer_query, "UPDATE Stanza SET proprietario='%s' WHERE numero=%d", client->username, chat_room_number);

          PGresult *res = PQexec(conn, buffer_query);
          if (PQresultStatus(res) != PGRES_COMMAND_OK) {
          printf("Query failed: %s", PQerrorMessage(conn));
          sprintf(buffer_out, "create_chat_add_users_failed\n");

          }else{

          sprintf(buffer_out, "create_chat_add_users_success\n");

          }


        }
      }

       PQclear(res);

      
    }else if (strcmp(request_type, "add_user_to_chat") == 0) {


     int stanza_numero = atoi(strtok(NULL, " "));
     char *user_to_add = strtok(NULL, " ");

       sprintf(buffer_query, "UPDATE utentistanza SET approvato=true WHERE numerostanza=%d AND username='%s'",stanza_numero,user_to_add);
       pthread_mutex_lock(&conn_mutex);
       PGresult *res = PQexec(conn, buffer_query);
       pthread_mutex_unlock(&conn_mutex);

       if (PQresultStatus(res) == PGRES_COMMAND_OK) {

        sprintf(buffer_out, "user_added_to_chat\n");

       }else{

        sprintf(buffer_out, "add_user_failed\n");

       }

     PQclear(res);


    }else if (strcmp(request_type, "delete_request_user") == 0) {

     int stanza_numero = atoi(strtok(NULL, " "));
     char *user_to_add = strtok(NULL, " ");

      sprintf(buffer_query, "DELETE FROM utentistanza WHERE numerostanza =%d AND username = '%s'",stanza_numero,user_to_add);
      pthread_mutex_lock(&conn_mutex);
      PGresult *res = PQexec(conn, buffer_query);
      pthread_mutex_unlock(&conn_mutex);

      if (PQresultStatus(res) == PGRES_COMMAND_OK) {

        sprintf(buffer_out, "delete_request_user_success\n");

      }else{

        sprintf(buffer_out, "delete_request_user_failed\n");

      }

     PQclear(res);


    }else if (strcmp(request_type, "check_user")==0){
      char *username = strtok(NULL, " ");

      sprintf(buffer_query, "SELECT * FROM Utente WHERE username='%s'", username);
      pthread_mutex_lock(&conn_mutex);
      PGresult *res = PQexec(conn, buffer_query);
      pthread_mutex_unlock(&conn_mutex);
      if (PQntuples(res) > 0) {
     
        sprintf(buffer_out, "user_found¶");
        sprintf(buffer_out + strlen(buffer_out), "%s\n",username);
        memset(buffer_query, 0, sizeof(buffer_query));
      } else {
       
        sprintf(buffer_out, "user_not_found\n");
        memset(buffer_query, 0, sizeof(buffer_query));
      }
      PQclear(res);


    }else if (strcmp(request_type, "invia_richiesta")==0){

     int stanza_numero = atoi(strtok(NULL, " "));

     sprintf(buffer_query,"SELECT COUNT(*) FROM utentistanza WHERE username='%s' AND numerostanza='%d' AND approvato=true", client->username,stanza_numero);
     pthread_mutex_lock(&conn_mutex);
     PGresult *res = PQexec(conn, buffer_query);
     pthread_mutex_unlock(&conn_mutex);

     if (PQresultStatus(res) == PGRES_TUPLES_OK)
     {

      int count = atoi(PQgetvalue(res, 0, 0));
      if (count > 0) {
       sprintf(buffer_out, "user_already_in\n");
     
      }else{
        sprintf(buffer_query,"SELECT COUNT(*) FROM utentistanza WHERE username='%s' AND numerostanza='%d' AND approvato=false", client->username,stanza_numero);
        pthread_mutex_lock(&conn_mutex);
        PGresult *res = PQexec(conn, buffer_query);
        pthread_mutex_unlock(&conn_mutex);

        if (PQresultStatus(res) == PGRES_TUPLES_OK)
        {
        int count = atoi(PQgetvalue(res, 0, 0));
        PQclear(res);
         if (count > 0) {
         sprintf(buffer_out, "processing_request\n");
     
         }else{

         sprintf(buffer_query, "INSERT INTO utentistanza (numerostanza,username,approvato) VALUES ('%d','%s',false)",stanza_numero,client->username);
         pthread_mutex_lock(&conn_mutex);
         PGresult *res = PQexec(conn, buffer_query);
         pthread_mutex_unlock(&conn_mutex);

         if (PQresultStatus(res) == PGRES_COMMAND_OK)
         {

          sprintf(buffer_out, "request_sent\n");

         }else{
          sprintf(buffer_out, "request_not_sent\n");


          }
         }
        }

      }
     }
     PQclear(res);


    }else if (strcmp(request_type, "recupera_richieste")==0){

        sprintf(buffer_query,"SELECT utente.username, stanza.nome, stanza.numero FROM utente, stanza, utentistanza WHERE utente.username=utentistanza.username AND stanza.numero=utentistanza.numerostanza AND stanza.proprietario='%s' AND utentistanza.approvato=false",client->username);

        pthread_mutex_lock(&conn_mutex);
        PGresult *res = PQexec(conn, buffer_query);
        pthread_mutex_unlock(&conn_mutex);

        if (PQresultStatus(res) == PGRES_TUPLES_OK)
        {
          sprintf(buffer_out, "retrival_request_success¶");

          for (int i = 0; i < PQntuples(res); i++)
          {

           char *nome = PQgetvalue(res, i, 0);
           char *stanza = PQgetvalue(res, i, 1);
           char *id = PQgetvalue(res, i, 2);
           sprintf(buffer_out + strlen(buffer_out), "%s¶%s¶%s¶", nome,stanza,id);

          }
          strcat(buffer_out, "\n");

        }else{
          sprintf(buffer_out, "retrival_request_failed\n");
        }
        
        PQclear(res);


    }else if (strcmp(request_type, "recupera_messaggi")==0){

     int codice_chat = atoi(strtok(NULL, " "));
     sprintf(buffer_query, "SELECT Messaggio.corpo, Messaggio.tempo_invio, Messaggio.mittente FROM Messaggio WHERE stanza='%d'", codice_chat);\

     pthread_mutex_lock(&conn_mutex);
     PGresult *res = PQexec(conn, buffer_query);
     pthread_mutex_unlock(&conn_mutex);

     if (PQresultStatus(res) == PGRES_TUPLES_OK) {
     int num_tuple = PQntuples(res);

      client->stanza_numero = codice_chat;

      if (num_tuple > 0)
      {
        sprintf(buffer_out, "retrieval_message_success¶");
       for (int i = 0; i < num_tuple; i++)
       {
       sprintf(buffer_out + strlen(buffer_out), "%s¶%s¶%s¶", PQgetvalue(res, i, 0), PQgetvalue(res, i, 1), PQgetvalue(res, i, 2));

       }
        strcat(buffer_out, "\n");

      }else {
      sprintf(buffer_out, "no_message_found\n");
      }


      PQclear(res); 

      sprintf(buffer_query,"SELECT username FROM utentistanza WHERE numerostanza='%d' AND approvato=true AND username != '%s'",codice_chat,client->username);
      pthread_mutex_lock(&conn_mutex);
      PGresult *res = PQexec(conn, buffer_query);
      pthread_mutex_unlock(&conn_mutex);

      if (PQresultStatus(res) == PGRES_TUPLES_OK)
      {

        int n_rows = PQntuples(res);
         for (int i = 0; i < n_rows; i++)
         {       
            struct Client *partecipante = malloc(sizeof(struct Client));
            char *strtemp=PQgetvalue(res,i,0);
            strcpy(partecipante->username, strtemp);
            printf("L'username di partecipante è : %s \n ", partecipante->username);
            client->partecipanti[i] = partecipante;
         }

         struct NodoLista *current = head;
         while (current != NULL)
         {
          struct Client *utente = current->client;
          for (int i = 0; i < n_rows; i++)
          {
           if (strcmp(utente->username, client->partecipanti[i]->username) == 0)
           {
            client->partecipanti[i]->sd = utente->sd;
            printf("Sono nell'if, il valore di client->partecipanti->sd : %d, l'username è: %s \n",client->partecipanti[i]->sd,client->partecipanti[i]->username);
            }
          }
         current = current->succ;
         }

      }else{
        sprintf(buffer_out, "retrieval_users_failed\n");

      } 
      PQclear(res); 

     } else{
      sprintf(buffer_out, "retrieval_message_failed\n");
     }
    for(int i=0;i<MAX_CLIENT;i++){
      printf("Partecipante: %s \n", client->partecipanti[i]->username);
    }
  

    }else if (strcmp(request_type, "leave_chat")==0){
      client->stanza_numero = -1;

      for(int i=0; i<MAX_CLIENT; i++)
      {
      client->partecipanti[i] = NULL;
      }


    }else if (strcmp(request_type, "send_message") == 0) {
      
      char *messaggio;
      char *buffer_messaggio = malloc(100);
      int dim_buffer = 100; 
      int dim_messaggio = 0; 

      while ((messaggio = strtok(NULL, "\n")) != NULL) {
         dim_messaggio += strlen(messaggio);  
         if (dim_messaggio >= dim_buffer) {  
            dim_buffer *= 2;  
            buffer_messaggio = realloc(buffer_messaggio, dim_buffer); 
         }
         strcat(buffer_messaggio, messaggio);  
      }

      sprintf(buffer_query, "INSERT INTO messaggio (corpo, tempo_invio, mittente, stanza) VALUES ('%s', 'NOW()', '%s', '%d')", buffer_messaggio, client->username, client->stanza_numero);
 
      PGresult *res = PQexec(conn, buffer_query);
      if (PQresultStatus(res) == PGRES_COMMAND_OK) {


        manda_messaggio(buffer_messaggio,client);
        sprintf(buffer_out,"send_message_success\n");

      } else {

        sprintf(buffer_out, "send_message_failed\n");
      }
      PQclear(res);

    } else {

      sprintf(buffer_out, "invalid_request\n");
    }


    send(client->sd, buffer_out, strlen(buffer_out), 0);
    /*while(1){
      int n = send(client->sd, buffer_out, strlen(buffer_out), 0);
      if(n < 0){
        perror("Errore invio dati al client");
        continue;
      }

      break;
    } può dare problemi(?)*/ 


  }
  
  printf("%s esce dall'applicazione\n",client->username);
  rimuovi_client(client);
  close(client->sd);
  free(client);
  pthread_exit(0);
  

  return NULL;
}







//////////////////////GESTIONE CODA CLIENT/////////////////////////////

void aggiungi_client(struct Client *c) {
  pthread_mutex_lock(&clients_mutex);
  struct NodoLista **ptr = &head;
  while(*ptr != NULL) {
    ptr = &((*ptr)->succ);
  }
  (*ptr) = (struct NodoLista *)malloc(sizeof(struct NodoLista));
  (*ptr)->client = c;
  (*ptr)->succ = NULL;
  pthread_mutex_unlock(&clients_mutex);
}


void rimuovi_client(struct Client *c) {
  pthread_mutex_lock(&clients_mutex);
  struct NodoLista *ptr = head;
  struct NodoLista *pre = NULL;
  while(ptr != NULL && ptr->client != c) {
    pre = ptr;
    ptr = ptr->succ;
  }
  if(ptr != NULL) {
    if(ptr == head) {
      head = head->succ;
    }else {
      pre->succ = ptr->succ;
    }
    ptr->succ = NULL;
    ptr->client = NULL;
    free(ptr);
  }
  pthread_mutex_unlock(&clients_mutex);
}



void manda_messaggio(char *msg, struct Client *client){
  pthread_mutex_lock(&clients_mutex);

  int i = 0;
  while(client->partecipanti[i] != NULL) {
   send(client->partecipanti[i]->sd, msg, strlen(msg), 0);
   i++;
  }

  pthread_mutex_unlock(&clients_mutex);
}



void retrieve_users_from_room(const char *room_name, struct Client *c) {
    char query[DIM_BUFF];
    int num_rows, i;

    sprintf(query, "SELECT username FROM users WHERE room = '%s'", room_name);

    pthread_mutex_lock(&conn_mutex);
    PGresult *res = PQexec(conn, query);
    pthread_mutex_unlock(&conn_mutex);

    if (PQresultStatus(res) != PGRES_TUPLES_OK) {
        printf("Errore nella query: %s\n", PQerrorMessage(conn));
        return;
    }

    num_rows = PQntuples(res);

    for (i = 0; i < num_rows; i++) {
        char *username = PQgetvalue(res, i, 0);
        c->partecipanti[i] = (struct Client *) malloc(sizeof(struct Client));
        strcpy(c->partecipanti[i]->username, username);
    }
    PQclear(res);
}