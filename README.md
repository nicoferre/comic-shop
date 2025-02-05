README
======
This is a preview version of the comic shop Technologies: Java 17 Spring boot 3.4.2

Pre-requisites:
- Install Java 17 (recommended with [sdkman](https://sdkman.io/install))
- Install [gradle](https://gradle.org/install/)

Endpoints
====

```bash

- Endpoint: GET /v1/api/comics
- Descripción: Recupera una lista de todos los cómics disponibles en la tienda.
```

```bash

- Endpoint: GET /v1/api/character/{name}
- Descripción: Obtiene la información de un personaje en la tienda proporcionando su nombre.
```

```bash

- Endpoint: GET /v1/api/comic/{UPC}/characters
- Descripción: Recupera un listado de personajes asociados a un UPC, ordenados por la cantidad de apariciones en los cómics.
```