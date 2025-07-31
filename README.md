# SilkRoad E-commerce Platform (Backend)

![Immagine di Logo Aziendale](https://placehold.co/1200x300/003366/FFFFFF?text=SilkRoad+Backend&font=raleway)

[![Stato Build](https://img.shields.io/badge/build-passing-brightgreen?style=for-the-badge&logo=github)](https://github.com/giovannidadone/silkroad)
[![Licenza](https://img.shields.io/badge/license-MIT-blue?style=for-the-badge)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?style=for-the-badge&logo=spring)](https://spring.io/projects/spring-boot)

---

**SilkRoad Backend** è il motore server-side per la piattaforma di e-commerce SilkRoad. Sviluppato con Spring Boot, espone un set completo di API RESTful sicure, scalabili e performanti, progettate per essere consumate da qualsiasi applicazione client, come un'interfaccia web o mobile.

Questo repository contiene esclusivamente il codice sorgente del backend.

## Indice

1.  [Visione del Progetto](#visione-del-progetto)
2.  [Funzionalità Chiave](#funzionalità-chiave)
3.  [Architettura del Sistema](#architettura-del-sistema)
4.  [Stack Tecnologico](#stack-tecnologico)
5.  [Guida all'Installazione](#guida-allinstallazione)
    * [Prerequisiti](#prerequisiti)
    * [Configurazione del Database (MySQL)](#configurazione-del-database-mysql)
    * [Avvio dell'Applicazione](#avvio-dellapplicazione)
6.  [Struttura del Progetto](#struttura-del-progetto)
7.  [Endpoint API Principali](#endpoint-api-principali)
8.  [Sistema di Sicurezza](#sistema-di-sicurezza)
9.  [Applicazione Frontend](#applicazione-frontend)
10. [Contribuire](#contribuire)
11. [Licenza](#licenza)

---

## Visione del Progetto

L'obiettivo di **SilkRoad Backend** è fornire una solida spina dorsale per qualsiasi operazione di vendita al dettaglio online. L'architettura è stata pensata per essere modulare e disaccoppiata da qualsiasi interfaccia utente, garantendo che le API possano servire diverse piattaforme client in modo coerente e sicuro. La nostra priorità è la sicurezza dei dati, l'affidabilità delle transazioni e le performance.

---

## Funzionalità Chiave

La piattaforma espone funzionalità dedicate per diverse tipologie di utenti.

### Funzionalità Pubbliche (non autenticate)
-   Visualizzazione e ricerca dei prodotti nel catalogo.
-   Visualizzazione delle categorie di prodotti.
-   Registrazione di un nuovo account utente.
-   Login utente.

### Funzionalità per Clienti (autenticati con ruolo `USER`)
-   Gestione completa del proprio profilo (visualizzazione e modifica).
-   Gestione del carrello (aggiunta, rimozione, modifica quantità prodotti).
-   Processo di checkout e creazione di un nuovo ordine.
-   Visualizzazione dello storico dei propri ordini.

### Funzionalità per Amministratori (autenticati con ruolo `ADMIN`)
-   Accesso a funzionalità di gestione della piattaforma.
-   **Gestione Prodotti**: Operazioni CRUD complete sui prodotti.
-   **Gestione Categorie**: Operazioni CRUD complete sulle categorie, con supporto per gerarchie.
-   **Gestione Utenti**: Visualizzazione e gestione degli account utente.
-   **Gestione Ordini**: Monitoraggio e aggiornamento dello stato degli ordini di tutti gli utenti.

---

## Architettura del Sistema

SilkRoad adotta un'architettura a più livelli (multi-layered), standard per le applicazioni enterprise basate su Spring.

-   **Controller Layer (`controller`)**: Espone gli endpoint API RESTful. Gestisce le richieste HTTP in entrata, le valida e delega la logica di business al Service Layer.
-   **Service Layer (`service`)**: Contiene la logica di business principale dell'applicazione. Coordina le operazioni, gestisce le transazioni e interagisce con il Repository Layer.
-   **Repository Layer (`repository`)**: Responsabile della comunicazione con il database. Utilizza Spring Data JPA per astrarre le operazioni di accesso ai dati.
-   **Domain Model (`model`)**: Contiene le entità JPA che rappresentano la struttura dei dati del dominio.
-   **Security Layer (`security`)**: Gestisce l'autenticazione e l'autorizzazione utilizzando Spring Security e JSON Web Tokens (JWT).

![Immagine di Architettura Backend](https://placehold.co/800x450/E8E8E8/444444?text=Controller+%E2%86%92+Service+%E2%86%92+Repository+%E2%86%92+Database&font=raleway)

---

## Stack Tecnologico

-   **Framework**: Spring Boot 3
-   **Linguaggio**: Java 21
-   **Sicurezza**: Spring Security, JSON Web Token (JWT)
-   **Database**: Spring Data JPA, Hibernate
-   **Build Tool**: Apache Maven
-   **Database Supportati**: Progettato per database relazionali (es. MySQL, PostgreSQL).

---

## Guida all'Installazione

Seguire questi passaggi per configurare l'ambiente di sviluppo e avviare l'applicazione.

### Prerequisiti
-   **JDK 17** o superiore.
-   **Apache Maven** 3.6 o superiore.
-   Un'istanza di un database **MySQL 8** in esecuzione.

### Configurazione del Database (MySQL)
1.  Crea un nuovo database sulla tua istanza MySQL (es. `silkroad_db`).
2.  Apri il file `src/main/resources/application.properties`.
3.  Modifica le seguenti proprietà per farle corrispondere alla configurazione del tuo database:

    ```properties
    # Esempio per MySQL
    spring.datasource.url=jdbc:mysql://localhost:3306/silkroad_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    spring.datasource.username=iltuousername
    spring.datasource.password=latuapassword
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

    # Configurazione di Hibernate
    spring.jpa.hibernate.ddl-auto=update # 'update' per lo sviluppo, 'validate' o 'none' per la produzione
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
    ```

### Avvio dell'Applicazione
1.  Clona il repository:
    ```bash
    git clone [https://github.com/giovannidadone/silkroad.git](https://github.com/giovannidadone/silkroad.git)
    cd silkroad
    ```

2.  Compila il progetto e avvia l'applicazione utilizzando Maven:
    ```bash
    mvn spring-boot:run
    ```

3.  L'applicazione sarà in esecuzione all'indirizzo `http://localhost:8080`.

---

## Struttura del Progetto

La struttura del progetto segue le convenzioni standard di un'applicazione Spring Boot, promuovendo una chiara separazione dei compiti.


## 📁 Struttura del Progetto

```
project-security/
├── src/main/java/com/example/project_security/
│   ├── config/
│   │   └── CorsConfig.java           # Configurazione CORS
│   ├── controller/
│   │   ├── AdminController.java      # Gestione Admin
│   │   ├── UserController.java       # Gestione utenti e auth
│   │   ├── ProductController.java    # Gestione prodotti
│   │   ├── CategoryController.java   # Gestione categorie
│   │   ├── CartController.java       # Gestione carrello
│   │   ├── PublicController.java     # Gestione rotta pubblica
│   │   └── OrderController.java      # Gestione ordini
│   ├── dto/
│   │   ├── request/                  # DTO per richieste
│   │   └── response/                 # DTO per risposte
│   ├── exception/                    # Gestione errori centralizzata
│   ├── model/
│   │   ├── Utente.java              # Entità utente unificata
│   │   ├── Product.java             # Entità prodotto
│   │   ├── Category.java            # Entità categoria
│   │   ├── Cart.java                # Entità carrello
│   │   ├── CartItem.java            # Entità item del carrello
│   │   ├── OrderStatus.java         # Entità status dell'ordine
│   │   ├── OrderItem.java           # Entità item ordinato
│   │   ├── Role.java                # Entità ruoli
│   │   └── Order.java               # Entità ordine
│   ├── repository/                   # Repository JPA
│   ├── security/
│   │   ├── JwtAuthFilter.java       # Filtro JWT
│   │   ├── SecurityConfig.java      # Configurazione sicurezza
│   │   └── JwtService.java          # Servizio JWT
│   └── service/
│   │   ├── UserService.java         # Logica utenti
│   │   ├── CostumUserDetailsService.java      # Logica dettagli dei servizi
│   │   ├── OrderService.java        # Logica servizi
│   │   ├── ProductService.java      # Logica prodotti
│   │   ├── CategoryService.java      # Logica categorie prodotti
│   │   └── CartService.java         # Logica carrello
└── src/main/resources/
    └── application.properties        # Configurazione
```


---

## Endpoint API Principali

Il backend espone le seguenti API RESTful.

| Metodo | Endpoint                        | Descrizione                                  | Ruolo Richiesto |
|--------|---------------------------------|----------------------------------------------|-----------------|
| `POST` | `/api/public/register`          | Registra un nuovo utente.                    | Pubblico        |
| `POST` | `/api/public/login`             | Autentica un utente e restituisce un JWT.    | Pubblico        |
| `GET`  | `/api/public/products`          | Ottiene la lista di tutti i prodotti.        | Pubblico        |
| `GET`  | `/api/users/me`                 | Ottiene i dettagli dell'utente autenticato.  | `USER`          |
| `GET`  | `/api/cart`                     | Visualizza il carrello dell'utente.          | `USER`          |
| `POST` | `/api/orders`                   | Crea un nuovo ordine dal carrello.           | `USER`          |
| `POST` | `/api/admin/products`           | Aggiunge un nuovo prodotto.                  | `ADMIN`         |
| `PUT`  | `/api/admin/products/{id}`      | Aggiorna un prodotto esistente.              | `ADMIN`         |
| `GET`  | `/api/admin/orders`             | Cerca e visualizza tutti gli ordini.         | `ADMIN`         |
| `PUT`  | `/api/admin/orders/{id}/status` | Aggiorna lo stato di un ordine.              | `ADMIN`         |

---

## Sistema di Sicurezza

La sicurezza è gestita da **Spring Security**.
-   **Autenticazione**: Il sistema utilizza un approccio stateless basato su **JWT**. Al momento del login, viene generato un token firmato che il client deve includere nell'header `Authorization` (`Bearer <token>`) di tutte le richieste successive a endpoint protetti.
-   **Autorizzazione**: L'accesso agli endpoint è controllato tramite ruoli (`ROLE_USER`, `ROLE_ADMIN`). La configurazione in `SecurityConfig.java` definisce quali ruoli possono accedere a quali percorsi API.

---

## Applicazione Frontend

L'interfaccia utente (frontend) per questa piattaforma è sviluppata e mantenuta in un repository separato. È progettata per consumare le API esposte da questo backend.

-   **Repository Frontend**: [https://github.com/fabiomallardo/silkroad-frontend](https://github.com/fabiomallardo/silkroad-frontend)

---

## Autori

Questo progetto è stato sviluppato e mantenuto dai seguenti autori:
|           AUTORE            |    FRONT-END     |   BACKEND    |             GitHub                   |  
|-----------------------------|------------------|--------------|--------------------------------------|

-   **Massimiliano Cassia** - |       *x*        |              | - [massimilianocassia](https://github.com/Massyiwnl);
-   **Jacopo De Martino** -   |       *x*        |              | - [jacopodemartino](https://github.com/Jacopo-De-Martino);
-   **Giovanni Dadone** -     |       *x*        |      *x*     | - [giovannidadone](https://github.com/giovannidadone);
-   **Fabio Mallardo** -      |       *x*        |      *x*     |- [fabiomallardo](https://github.com/fabiomallardo).      

---

## Contribuire

Siamo aperti a contributi dalla community. Se desideri contribuire, per favore segui questi passaggi:
1.  Fai un fork del repository.
2.  Crea un nuovo branch per la tua feature (`git checkout -b feature/AmazingFeature`).
3.  Implementa le tue modifiche.
4.  Esegui il commit delle tue modifiche (`git commit -m 'Add some AmazingFeature'`).
5.  Fai il push sul tuo branch (`git push origin feature/AmazingFeature`).
6.  Apri una Pull Request.

---

## Licenza

Questo progetto è distribuito sotto la licenza MIT. Vedi il file `LICENSE` per maggiori dettagli.

**Nota Bene**: Questo progetto è stato creato a scopo puramente didattico e non è destinato all'uso in produzione.

