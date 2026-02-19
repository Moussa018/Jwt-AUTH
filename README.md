# 🔐 JWT Authentication Service

Service d'authentification REST API basé sur **Spring Boot 4** et **JWT**, avec une base de données **PostgreSQL**.

---

## 🚀 Technologies

- Java 17
- Spring Boot 4.0.2
- Spring Security 7
- JSON Web Token (JWT)
- PostgreSQL
- Docker & Docker Compose
- Gradle

---

## 📁 Structure du projet

```
src/
├── auth/
│   ├── AuthController.java       # Endpoints register & authenticate
│   ├── AuthService.java          # Logique métier
│   ├── AuthenticationRequest.java
│   ├── AuthenticationResponse.java
│   └── RegisterRequest.java
├── config/
│   ├── ApplicationConfig.java    # Beans Spring Security
│   ├── JwtService.java           # Génération & validation des tokens
│   ├── JwtAuthenticationFilter.java  # Filtre JWT sur chaque requête
│   └── SecurityConfiguration.java   # Configuration des routes
└── user/
    ├── User.java                 # Entité JPA
    ├── UserRepository.java
    └── Role.java
```

---

## ⚙️ Configuration

Modifier `src/main/resources/application.yaml` :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/jwt_auth
    username: postgres
    password: mysecretpassword

application:
  security:
    jwt:
      secret-key: votre_clé_secrète
      expiration: 86400000  # 24 heures en ms
```

---

## 🐳 Lancer avec Docker

```bash
# Démarrer l'application + PostgreSQL
docker-compose up --build

# En arrière-plan
docker-compose up --build -d

# Arrêter
docker-compose down
```

---

## 💻 Lancer en local

**Prérequis :** PostgreSQL installé et démarré

```bash
# Compiler
./gradlew bootJar

# Lancer
./gradlew bootRun
```

---

## 📡 Endpoints

| Méthode | URL | Description | Auth |
|---------|-----|-------------|------|
| POST | `/api/v1/auth/register` | Créer un compte | ❌ |
| POST | `/api/v1/auth/authenticate` | Se connecter | ❌ |
| GET | `/api/v1/demo-controller` | Route protégée | ✅ |

---

## 🧪 Exemples

**Register**
```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "secret123"
  }'
```

**Authenticate**
```bash
curl -X POST http://localhost:8081/api/v1/auth/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "secret123"
  }'
```

**Réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Accéder à une route protégée**
```bash
curl http://localhost:8081/api/v1/demo-controller \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## 🔒 Sécurité

- Les mots de passe sont hashés avec **BCrypt**
- Les tokens JWT expirent après **24 heures**
- Les routes sont protégées sauf `/api/v1/auth/**`
- Ne jamais committer les clés secrètes en production

---

## 👤 Auteur

**Moussa** — [GitHub](https://github.com/Moussa018)
