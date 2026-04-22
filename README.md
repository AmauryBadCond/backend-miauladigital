# MiAulaDigital API

Backend en Ktor para tu app Compose Multiplatform.

## Ejecutar localmente

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/railway
DATABASE_USER=postgres
DATABASE_PASSWORD=postBad01
./gradlew run
```

En Railway puedes usar cualquiera de estas dos opciones:

```bash
DATABASE_URL=jdbc:postgresql://postgres:XJkhxCqYgZRnORzYElLBayChcLHQeXTc@postgres.railway.internal:5432/railway
DATABASE_USER=postgres
DATABASE_PASSWORD=postBad01
```

o bien las variables nativas de Railway/Postgres:

```bash
PGHOST=postgres.railway.internal
PGPORT=5432
PGDATABASE=railway
PGUSER=postgres
PGPASSWORD=postBad01
PGSSLMODE=require
```

Si quieres que el servidor cree tablas faltantes solo en desarrollo:

```bash
DATABASE_AUTO_CREATE_SCHEMA=true
```

## Endpoints base

- `GET /`
- `GET /health`
- `GET /api/users`
- `POST /api/users`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`
- `GET /api/classrooms`
- `POST /api/classrooms`
- `GET /api/classrooms/{id}`
- `PUT /api/classrooms/{id}`
- `DELETE /api/classrooms/{id}`
- `GET /api/classrooms/{id}/tasks`
- `GET /api/student-parents`
- `POST /api/student-parents`
- `DELETE /api/student-parents?studentId={uuid}&parentId={uuid}`
- `GET /api/students/{studentId}/parents`
- `GET /api/parents/{parentId}/students`
- `GET /api/tasks`
- `POST /api/tasks`
- `GET /api/tasks/{id}`
- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`

## Nota sobre UUID en PostgreSQL

Si en tu script quieres usar `gen_random_uuid()`, la extension correcta normalmente es:

```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;
```

# backend-miauladigital
# backend-miauladigital
# backend-miauladigital
