
# API Ingredients

## Ingrédients

| URI | Opération | MIME | Requête |Réponse|
|:---------:|:---------:|:---------:|:---------:|:---------:|
|   /ingredients   |   GET   | <- application/json |  | liste des ingrédients (l1) |
|   /ingredients/{id}   |   GET   | <- application/json |  | un ingrédient ou 404 |
|   /ingredients/{id}/name   |   GET   | <- text/plain |  | le nom de l'ingrédient ou 404 |
|   /ingredients   |   POST   | <-/-> application/json | Ingrédient (l2) | nouvel ingrédient ou 409 si il existe déjà |
|   /ingredients{id}   |   DELETE   |  |  | |




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

## Pizzas

| URI | Opération | MIME | Requête |Réponse|
|:---------:|:---------:|:---------:|:---------:|:---------:|
|   /pizzas   |   GET   | <- application/json |  | liste des pizza (l1) |
|   /pizzas/{id}   |   GET   | <- application/json |  | une pizza ou 404 |
|   /pizzas   |   POST   | <-/-> application/json | Pizza (l2) | nouvelle pizza ou 409 si elle existe déjà |
|   /pizzas{id}   |   DELETE   |  |  | |
|   /pizzas/{id}/prixfinal   |   GET   | <- text/plain |  | le prix de la pizza ou 404 |
|   /pizzas/{id}/{idIngredient}   |   DELETE   | <- text/plain |  | suppression d'un ingredient de la pizza ou 404 |
|   /pizzas/{id}   |   PUT   | <- application/json |  | ajouter un ingrédient à la pizza ou 404 |
|  /pizzas/{id}   |   PATCH   | <- application/json |  | modifier une pizza ou 404 |


# Corps des requêtes

**l1**

Une pizza comporte un id, nom, un prix et une liste d'ingredients. Sa représentation json est la suivante :

```json
{
  "id": 1,
  "name": "margarita",
  "price": 5,
  "ingredients": [
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
}
```

# Exemple

## Lister toutes les pizzas connu dans la base de données

#### GET /api/pizzas

requête vers le serveur 

```json
GET /api/pizzas
[
  {
    "id": 1,
    "name": "margarita",
    "price": 5,
    "ingredients": [
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
  },
  {
    "id": 2,
    "name": "4 fromages",
    "price": 7,
    "ingredients": [
      {
        "id": 1,
        "name": "tomate",
        "price": 0.5
      },
      {
        "id": 3,
        "name": "fromage",
        "price": 1
      }
    ]
  }
]
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 | La requete c'est effectué corrctement  |

## Récupérer les détails de la pizza

#### GET /api/pizzas/{id}

Requête vers le serveur

```json
GET /api/pizzas/1
{
  "id": 1,
  "name": "margarita",
  "price": 5,
  "ingredients": [
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
}
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 | La requete c'est effectué corrctement  |
| 404 | La pizza n'existe pas  |


## Ajouter une pizza

#### POST /api/pizzas

Requête vers le serveur

```json
POST /api/pizzas
{
  "name": "margarita",
  "price": 5,
  "ingredients": [
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
}
```

reponse du serveur

```json
{
  "id": 1,
  "name": "margarita",
  "price": 5,
  "ingredients": [
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
}
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 CREATED | La pizza a été ajouté avec succès  |
| 409 CONFLICT | La pizza existe déjà  |


## Supprimer une pizza

#### DELETE /api/pizzas/{id}

Requête vers le serveur

```json
    DELETE /api/pizzas/1
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 OK | La pizza a été supprimé avec succès  |
| 404 NOT FOUND | La pizza n'existe pas  |

## Récupérer le prix final de la pizza

#### GET /api/pizzas/{id}/prixfinal

Requête vers le serveur

```json
GET /api/pizzas/1/prixfinal
5
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 | La requete c'est effectué corrctement  |
| 404 | La pizza n'existe pas  |

## Supprimer un ingrédient de la pizza

#### DELETE /api/pizzas/{id}/{idIngredient}

Requête vers le serveur

```json
    DELETE /api/pizzas/1/1
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 OK | L'ingrédient a été supprimé avec succès  |
| 404 NOT FOUND | La pizza ou l'ingrédient n'existe pas  |

## Ajouter un ingrédient à la pizza

#### PUT /api/pizzas/{id}

Requête vers le serveur

```json

PUT /api/pizzas/1
{
  "id": 3,
  "name": "fromage",
  "price": 1
}
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 OK | L'ingrédient a été ajouté avec succès  |
| 404 NOT FOUND | La pizza ou l'ingrédient n'existe pas  |

## Modifier une pizza

#### PATCH /api/pizzas/{id}

Requête vers le serveur

```json

PATCH /api/pizzas/1
{
  "name": "margarita",
  "price": 5,
  "ingredients": [
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
}
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 OK | La pizza a été modifié avec succès  |
| 404 NOT FOUND | La pizza n'existe pas  |





