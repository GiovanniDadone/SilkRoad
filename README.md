# 🛍️ Silk Road - E-Commerce Platform

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.8-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![React](https://img.shields.io/badge/React-18-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![TypeScript](https://img.shields.io/badge/TypeScript-5.8-007ACC?style=for-the-badge&logo=typescript&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-9.2-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![JHipster](https://img.shields.io/badge/JHipster-8.11.0-3E8ACC?style=for-the-badge&logo=jhipster&logoColor=white)

*Una moderna piattaforma e-commerce full-stack costruita con Spring Boot e React*

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen?style=flat-square)]()
[![License](https://img.shields.io/badge/license-UNLICENSED-red?style=flat-square)]()
[![Version](https://img.shields.io/badge/version-0.0.1--SNAPSHOT-blue?style=flat-square)]()

</div>

## 📋 Panoramica

**Silk Road** è una completa piattaforma e-commerce sviluppata utilizzando lo stack tecnologico JHipster. L'applicazione offre un'esperienza di shopping moderna con gestione completa di prodotti, ordini, carrelli e clienti.

### ✨ Caratteristiche Principali

- 🛒 **Gestione Catalogo Prodotti** - Creazione e categorizzazione prodotti
- 👥 **Sistema Clienti** - Registrazione e gestione profili utente  
- 🛍️ **Carrello Shopping** - Esperienza di shopping fluida e intuitiva
- 📦 **Gestione Ordini** - Tracciamento completo degli ordini con stati
- 🔐 **Autenticazione Sicura** - OAuth2/JWT con integrazione Keycloak
- 📱 **Design Responsive** - Interfaccia ottimizzata per tutti i dispositivi
- 🏢 **Admin Dashboard** - Pannello amministrativo completo

## 🏗️ Architettura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React Client  │────│  Spring Boot    │────│    MySQL DB     │
│   (Frontend)    │    │   (Backend)     │    │   (Database)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         │              ┌─────────────────┐              │
         └──────────────│   Keycloak      │──────────────┘
                        │ (Auth Server)   │
                        └─────────────────┘
```

### 🧩 Modello di Dominio

- **Product** - Gestione catalogo prodotti con categorie
- **Customer** - Profili clienti e informazioni personali
- **Cart/CartItem** - Sistema carrello persistente
- **Order/OrderItem** - Gestione ordini e cronologia acquisti  
- **Category** - Organizzazione gerarchica prodotti

## 🚀 Quick Start

### Prerequisiti

- **Java 17+** ☕
- **Node.js 22.15.0+** 📦
- **Docker & Docker Compose** 🐳
- **Maven 3.2.5+** 📋

### 🛠️ Installazione

1. **Clona il repository**
   ```bash
   git clone https://github.com/your-repo/silk-road.git
   cd silk-road
   ```

2. **Avvia i servizi Docker**
   ```bash
   npm run docker:db:up
   npm run docker:keycloak:up
   ```

3. **Installa le dipendenze**
   ```bash
   npm install
   ./mvnw install
   ```

4. **Avvia l'applicazione**
   ```bash
   # Avvio completo (frontend + backend)
   npm run start
   
   # Solo backend
   npm run backend:start
   
   # Solo frontend 
   npm run webapp:dev
   ```

5. **Accedi all'applicazione**
   - 🌐 **Frontend**: http://localhost:9000
   - ⚙️ **Backend API**: http://localhost:8080
   - 🔐 **Keycloak**: http://localhost:9080

## 🔧 Comandi Disponibili

### Backend
```bash
npm run backend:start          # Avvia il server Spring Boot
npm run backend:test           # Esegue i test backend
npm run backend:build-cache    # Cache delle dipendenze Maven
```

### Frontend
```bash
npm run webapp:dev             # Server di sviluppo frontend
npm run webapp:build           # Build di produzione
npm run test                   # Test frontend
npm run lint                   # Linting del codice
```

### Docker
```bash
npm run docker:db:up           # Avvia MySQL
npm run docker:keycloak:up     # Avvia Keycloak
npm run services:up            # Avvia tutti i servizi
```

### Produzione
```bash
npm run build                  # Build completo per produzione
npm run java:jar:prod          # JAR di produzione
npm run java:docker:prod       # Immagine Docker di produzione
```

## 📁 Struttura del Progetto

```
silk-road/
├── 📂 src/main/java/           # Backend Spring Boot
│   ├── 🏗️ config/             # Configurazioni
│   ├── 🎯 domain/              # Entità JPA
│   ├── 📦 repository/          # Repository JPA
│   ├── 🌐 web/rest/           # Controller REST
│   └── 🔐 security/           # Configurazione sicurezza
├── 📂 src/main/webapp/         # Frontend React
│   ├── ⚛️ app/                # Componenti React
│   ├── 🎨 content/            # Assets statici
│   └── 📱 i18n/               # Internazionalizzazione
├── 📂 src/test/               # Test (backend + frontend)
├── 📂 src/main/docker/        # Configurazioni Docker
└── 📂 webpack/                # Configurazione Webpack
```

## 🌟 Tecnologie Utilizzate

| Categoria | Tecnologie |
|-----------|------------|
| **Backend** | Spring Boot 3.4.8, Spring Security, Spring Data JPA, Liquibase |
| **Frontend** | React 18, TypeScript, Redux Toolkit, Bootstrap 5, Reactstrap |
| **Database** | MySQL 9.2, Liquibase per migrations |
| **Sicurezza** | OAuth2, JWT, Keycloak |
| **Build Tools** | Maven, Webpack, NPM |
| **Testing** | JUnit 5, Jest, TestContainers |
| **DevOps** | Docker, Docker Compose |

## 📚 API Documentation

L'applicazione include documentazione API integrata:

- **Swagger UI**: http://localhost:8080/swagger-ui/
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

## 🧪 Testing

```bash
# Test backend
npm run backend:unit:test

# Test frontend  
npm run test

# Test completi CI
npm run ci:backend:test
npm run ci:frontend:test
```

## 🔐 Autenticazione

L'applicazione utilizza **Keycloak** per l'autenticazione:

- **Admin**: `admin/admin`
- **User**: `user/user`

Configurazione realm disponibile in `src/main/docker/keycloak.yml`

## 📈 Profili di Esecuzione

- **dev** - Sviluppo con hot reload
- **prod** - Produzione ottimizzata
- **e2e** - Test end-to-end

## 🤝 Contribuire

1. Fork del progetto
2. Crea un feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit delle modifiche (`git commit -m 'Add some AmazingFeature'`)
4. Push al branch (`git push origin feature/AmazingFeature`)
5. Apri una Pull Request

## 📄 Licenza

Questo progetto è distribuito sotto licenza `UNLICENSED`.

## 🆘 Supporto

Per supporto e domande:

- 📧 Email: support@silkroad.example
- 📖 Documentazione: [JHipster Docs](https://www.jhipster.tech/)
- 🐛 Issue Tracker: [GitHub Issues](https://github.com/your-repo/silk-road/issues)

---

<div align="center">

**Fatto con ❤️ utilizzando JHipster**

[![JHipster](https://img.shields.io/badge/Generated%20by-JHipster-3E8ACC?style=flat&logo=jhipster)](https://www.jhipster.tech/)

</div>
