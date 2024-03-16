
# API Ingredients

| URI | Opération | MIME | Requête |Réponse|
|:---------:|:---------:|:---------:|:---------:|:---------:|
|   /ingredients   |   GET   | <- application/json |  | liste des ingrédients (l1) |
|   /ingredients/{id}   |   GET   | <- application/json |  | un ingrédient ou 404 |
|   /ingredients/{id}/name   |   GET   | <- text/plain |  | le nom de l'ingrédient ou 404 |
|   /ingredients   |   POST   | <-/-> application/json | Ingrédient (l2) | nouvel ingrédient ou 409 si il existe déjà |
|   /ingredients   |   DELETE   |  |  | |


# Corps des requêtes

**l1**

Un ingrédient comporte un id,nom et un prix. Sa représentation json est la suivante :

```json
{
  "id": 1,
  "name": "tomate",
  "price": 0.5
}
```

# Exemple

## Lister tous les ingrédients connu dans la base de données

#### GET /api/v1/ingredients

requête vers le serveur 

```json
GET /api/ingredients
[
  {
    "id": 1,
    "name": "tomate",
    "price": 0.5
  },
  {
    "id": 2,
    "name": "oignon",
    "price": 0.3
  }
]
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 | La requete c'est effectué corrctement  |

## Récupérer les détails de l'ingredient

#### GET /api/ingredients/{id}

Requête vers le serveur

```json
GET /api/ingredients/1
{
  "id": 1,
  "name": "tomate",
  "price": 0.5
}
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 | La requete c'est effectué corrctement  |
| 404 | L'ingrédient n'existe pas  |

## Récupérer le nom de l'ingrédient

#### GET /api/ingredients/{id}/name

Requête vers le serveur

```json
GET /api/ingredients/1/name
tomate
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 | La requete c'est effectué corrctement  |
| 404 | L'ingrédient n'existe pas  |

## Ajouter un ingrédient

#### POST /api/ingredients

Requête vers le serveur

```json
POST /api/ingredients
{
  "name": "tomate",
  "price": 0.5
}
```

reponse du serveur

```json
{
  "id": 1,
  "name": "tomate",
  "price": 0.5
}
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 201 CREATED | L'ingrédient a été ajouté avec succès  |
| 409 CONFLICT | L'ingrédient existe déjà  |

## Supprimer un ingrédient

#### DELETE /api/ingredients/{id}

Requête vers le serveur

```json
    DELETE /api/ingredients/1
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 OK | L'ingrédient a été supprimé avec succès  |
| 404 NOT FOUND | L'ingrédient n'existe pas  |


