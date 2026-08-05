# Cube Knowledge Center

Spring Boot 2.7.18 / Java 8 backend, packaged as WAR for deployment to Oracle WebLogic.

## Folder structure

Modular-by-feature, not by layer — set up this way so each future module (leave,
asset, HR, etc.) drops in as its own self-contained package next to `auth` and
`knowledge`, instead of scattering across shared `entity/`, `service/`, `controller/`
folders.

```
com.example.cube
├── CubeApplication.java
├── common/                     # cross-cutting, used by every module
│   ├── config/SecurityConfig.java
│   ├── security/                (JwtUtil, JwtAuthenticationFilter, CustomUserDetailsService)
│   └── exception/                (GlobalExceptionHandler, ApiError, custom exceptions)
└── modules/
    ├── auth/                   # identity + RBAC — fully built out this round
    │   ├── entity/               (UserMaster, GroupMaster, UserGroup, ModuleMaster,
    │   │                          AccessMaster, GroupAccessTemplate, UserAccess, + composite IDs)
    │   ├── repository/
    │   ├── service/              (AuthService, AccessControlService, UserService,
    │   │                          GroupService, ModuleService, AccessService,
    │   │                          GroupAccessService, UserAccessService)
    │   ├── controller/           (Auth, User, Group, Module, Access, GroupAccess, UserAccess)
    │   └── dto/
    └── knowledge/               # from the previous round, untouched logic-wise
        ├── entity/ repository/ service/ controller/ dto/
```

A new module (e.g. `leave`) means adding `modules/leave/{entity,repository,service,controller,dto}`
and nothing else changes — `common/` stays shared, `AccessControlService` from `auth`
is reused for permission checks the same way `KnowledgeService` already does.

## Auth module — what's built, end to end

**Login / identity**
- `POST /api/v1/auth/login` — returns JWT
- `GET  /api/v1/users/me` — current user's profile + group

**RBAC administration** (every endpoint below is permission-gated through
`AccessControlService.hasPermission(username, "AUTH", <code>)` — same
override-then-template resolution as the architecture doc: a `USER_ACCESS` row
wins if present, otherwise it falls back to `GROUP_ACCESS_TEMPLATE`)

| Endpoint | Access code |
|---|---|
| `POST/GET /api/v1/users`, `GET/{id}`, `PATCH /{id}/status`, `PUT /{id}/group` | `MANAGE_USERS` |
| `POST/GET/PUT/DELETE /api/v1/groups[/{id}]` | `MANAGE_GROUPS` |
| `POST/GET/PUT/DELETE /api/v1/modules[/{id}]` | `MANAGE_MODULES` |
| `POST/GET/DELETE /api/v1/modules/{moduleId}/access[/{accessId}]` | `MANAGE_ACCESS` |
| `PUT /api/v1/groups/{groupId}/access/{accessId}` | `MANAGE_GROUP_ACCESS` |
| `PUT/DELETE /api/v1/users/{userId}/access/{accessId}` | `MANAGE_USER_ACCESS` |

These six access codes live under a new `AUTH` module row in `MODULE_MASTER`
(seeded in `sql/seed.sql`), fully separate from the `KNOWLEDGE` module's
`CREATE`/`APPROVE` codes — so RBAC administration is itself governed by RBAC.

Passwords are BCrypt-hashed via `PasswordEncoder` in `UserService.create` —
never take a raw password in from a request and store it directly.

## Not yet built (next steps)

- Attachment upload/download + OneDrive (Microsoft Graph) integration
- Elasticsearch indexing and keyword/title/description search endpoint
- Knowledge update/delete

## Run locally

```
mvn clean package
java -jar target/cube.war
```

Set `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` as env vars (see `application.properties`).

## Deploy to WebLogic

1. `mvn clean package` — produces `target/cube.war`
2. Deploy via WebLogic console or `weblogic.Deployer`, or drop into `autodeploy/`
3. App will be available under context root `/cube` (set in `weblogic.xml`)

## Seed data

Run `sql/schema.sql` then `sql/seed.sql`. `seed.sql` creates the ADMIN/EMPLOYEE/MANAGER
groups, the AUTH + KNOWLEDGE modules and their access codes, and wires ADMIN to full
AUTH access. It leaves user rows commented out — generate a BCrypt hash (e.g. via
`new BCryptPasswordEncoder().encode("password")`) before inserting the first
`USER_MASTER` row and its `USER_GROUP` link to the ADMIN group, so you have a way in.

## Note

This container has no network access to Maven Central, so `mvn clean package`
could not be run here to verify the build compiles — build it locally to confirm
before deploying.
