Bienvenue sur la documentation de l'API de Pizzaland. Elle est divisée en deux parties : la première partie décrit la base de données et la deuxième partie décrit les différents endpoints de l'API ainsi que les requêtes et réponses associées et quelques exemples.

# Table de la Base de Données

## Ingrédients

| Nom | Type | Description |
|:---:|:----:|:-----------:|
| ino | integer | Identifiant de l'ingrédient |
| iname | varchar(50) | Nom de l'ingrédient |
| iprice | decimal(5,2) | Prix de l'ingrédient |

Clé primaire : ino

## Pizzas

| Nom | Type | Description |
|:---:|:----:|:-----------:|
| pino | integer | Identifiant de la pizza |
| piname | varchar(50) | Nom de la pizza |
| pipate | varchar(50) | Pâte de la pizza |
| pibase | varchar(50) | Base de la pizza |

Clé primaire : pino

## Commandes

| Nom | Type | Description |
|:---:|:----:|:-----------:|
| cno | integer | Identifiant de la commande |
| cname | varchar(50) | Nom du client |
| cdate | bigint | Date de la commande |

Clé primaire : cno

## Contient (Ingrédient dans Pizza)

| Nom | Type | Description |
|:---:|:----:|:-----------:|
| pino | integer | Identifiant de la pizza |
| ino | integer | Identifiant de l'ingrédient |

Clé primaire : pino, ino
Clé étrangère : pino -> pizzas(pino) (Suppression en cascade)
Clé étrangère : ino -> ingrédients(ino) (Suppression en cascade)

## Liste (Pizza dans Commande)

| Nom | Type | Description |
|:---:|:----:|:-----------:|
| cno | integer | Identifiant de la commande |
| pino | integer | Identifiant de la pizza |
| pqte | integer | Nombre de pizza |

Clé primaire : cno, pino
Clé étrangère : cno -> commandes(cno) (Suppression en cascade)
Clé étrangère : pino -> pizzas(pino) (Suppression en cascade)

## SQL

```sql
DROP TABLE IF EXISTS pizzas CASCADE;
DROP TABLE IF EXISTS ingredients CASCADE;
DROP TABLE IF EXISTS commandes CASCADE;
DROP TABLE IF EXISTS contient;
DROP TABLE IF EXISTS liste;

CREATE TABLE ingredients (
    ino INTEGER,
    iname VARCHAR(255) UNIQUE,
    iprice DECIMAL(5,2),
    CONSTRAINT pk_ingredients PRIMARY KEY (ino)
);

CREATE TABLE pizzas (
    pino INTEGER,
    piname VARCHAR(255) UNIQUE,
    pipate VARCHAR(255),
    pibase VARCHAR(255),
    CONSTRAINT pk_pizzas PRIMARY KEY (pino)
);

CREATE TABLE commandes (
    cno INTEGER,
    cname VARCHAR(255),
    cdate BIGINT,
    CONSTRAINT pk_commandes PRIMARY KEY (cno)
);

CREATE TABLE contient (
    pino INTEGER,
    ino INTEGER,
    CONSTRAINT pk_contient PRIMARY KEY (pino, ino),
    CONSTRAINT fk_contient_pizzas FOREIGN KEY (pino) REFERENCES pizzas(pino)
        ON DELETE CASCADE,
    CONSTRAINT fk_contient_ingredients FOREIGN KEY (ino) REFERENCES ingredients(ino)
        ON DELETE CASCADE
);

CREATE TABLE liste (
    cno INTEGER,
    pino INTEGER,
    pqte INTEGER,
    CONSTRAINT pk_liste PRIMARY KEY (cno, pino),
    CONSTRAINT fk_liste_commandes FOREIGN KEY (cno) REFERENCES commandes(cno)
        ON DELETE CASCADE,
    CONSTRAINT fk_liste_pizzas FOREIGN KEY (pino) REFERENCES pizzas(pino)
        ON DELETE CASCADE
);
```

# Pizzaland API

## Ingrédients

| URI | Opération | MIME | Requête | Réponse |
|:---:|:---------:|:----:|:-------:|:-------:|
| /ingredients | GET | <- application/json | | Tous les ingrédients |
| /ingredients/{id} | GET | <- application/json | | Ingrédient (i1) ou 404 |
| /ingredients/{id}/name | GET | <- application/json | | Nom de l'ingrédient (i2) ou 404 |
| /ingredients | POST | <-/-> application/json | Ingrédient (i3) | Ingrédient ajouté ou 409 |
| /ingredients{id} | DELETE | <- application/json | | Ingrédient supprimé ou 404 |

### Corps des réponses/requêtes

#### i1

```json
{
  "ino": 1,
  "iname": "tomate",
  "iprice": 0.5
}
```

#### i2

```json
{
  "iname": "tomate"
}
```

#### i3

```json
{
  "iname": "tomate",
  "iprice": 0.5
}
```

### Exemples

#### Lister tous les ingrédients connu dans la base de données

Requête vers le serveur : GET /pizzalandapi/ingredients

Réponse du serveur :

```json
[
  {
    "ino": 1,
    "iname": "tomate",
    "iprice": 0.5
  },
  {
    "ino": 2,
    "iname": "oignon",
    "iprice": 0.3
  }
]
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La requête s'est effectuée correctement |

#### Récupérer les détails de l'ingredient

Requête vers le serveur : GET /pizzalandapi/ingredients/1

Réponse du serveur :

```json
{
  "ino": 1,
  "iname": "tomate",
  "iprice": 0.5
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La requête s'est effectuée correctement |
| 404 | L'ingrédient n'existe pas |

#### Récupérer le nom de l'ingrédient

Requête vers le serveur : GET /pizzalandapi/ingredients/1/name

```json
{
  "iname": "tomate"
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La requête s'est effectuée correctement |
| 404 | L'ingrédient n'existe pas |

#### Ajouter un ingrédient

Requête vers le serveur : POST /pizzalandapi/ingredients

```json
{
  "iname": "tomate",
  "iprice": 0.5
}
```

Reponse du serveur :

```json
{
  "iname": "tomate",
  "iprice": 0.5
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 201 | L'ingrédient a été ajouté avec succès |
| 409 | Un ingrédient avec le même nom existe déjà |

#### Supprimer un ingrédient

Requête vers le serveur : DELETE /pizzalandapi/ingredients/1

Reponse du serveur :

```json
{
  "ino": 1,
  "iname": "tomate",
  "iprice": 0.5
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | L'ingrédient a été supprimé avec succès |
| 404 | L'ingrédient n'existe pas |

## Pizzas

| URI | Opération | MIME | Requête |Réponse|
|:---:|:---------:|:----:|:-------:|:-----:|
| /pizzas | GET | <- application/json | | Toutes les pizzas |
| /pizzas/{id} | GET | <- application/json | | Pizza (p1) ou 404 |
| /pizzas/{id}/prixfinal | GET | <- application/json | | Prix final de la pizza (p2) ou 404 |
| /pizzas | POST | <-/-> application/json | Pizza (p3) | Pizza ajoutée ou 409 |
| /pizzas/{id} | POST | <- application/json | | Pizza avec ingrédient ajouté ou 404 |
| /pizzas/{id} | DELETE | | | Pizza supprimée ou 404 |
| /pizzas/{id}/{idIngredient} | DELETE | <- application/json | | Ingrédient supprimé de la pizza ou 404 |
| /pizzas/{id} | PATCH | <- application/json | Pizza (p3) Tout les champs ne sont pas obligatoire | Pizza modifiée ou 404 |
| /pizzas/{id} | PUT | <- application/json | Pizza (p3) | Pizza modifiée ou 404 |

### Corps des réponses/requêtes

#### p1

```json
{
  "pino": 1,
  "piname": "margarita",
  "ingredients": [
    {
      "id": 1,
      "name": "tomate",
      "price": 0.5
    }
  ],
  "price": 0.5,
  "pipate": "fine",
  "pibase": "tomate"
}
```

#### p2

```json
{
  "prixfinal": 0.5
}
```

#### p3

```json
{
  "piname": "margarita",
  "ingredients": [
    1
  ],
  "pipate": "fine",
  "pibase": "tomate"
}
```

### Exemple

#### Lister toutes les pizzas connu dans la base de données

Requête vers le serveur : GET /pizzalandapi/pizzas

```json
[
  {
    "pino": 1,
    "piname": "margarita",
    "ingredients": [
      {
        "id": 1,
        "name": "tomate",
        "price": 0.5
      }
    ],
    "price": 0.5,
    "pipate": "fine",
    "pibase": "tomate"
  }
]
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La requête s'est effectuée correctement |

#### Récupérer les détails de la pizza

Requête vers le serveur : GET /pizzalandapi/pizzas/1

```json
{
  "pino": 1,
  "piname": "margarita",
  "ingredients": [
    {
      "id": 1,
      "name": "tomate",
      "price": 0.5
    }
  ],
  "price": 0.5,
  "pipate": "fine",
  "pibase": "tomate"
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La requête s'est effectuée correctement |
| 404 | La pizza n'existe pas |

#### Récupérer le prix final de la pizza

Requête vers le serveur : GET /pizzalandapi/pizzas/1/prixfinal

```json
{
  "prixfinal": 0.5
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La requête s'est effectuée correctement |
| 404 | La pizza n'existe pas |

#### Ajouter une pizza

Requête vers le serveur : POST /pizzalandapi/pizzas

```json
{
  "piname": "margarita",
  "ingredients": [
    1
  ],
  "pipate": "fine",
  "pibase": "tomate"
}

Reponse du serveur :

```json
{
  "pino": 1,
  "piname": "margarita",
  "ingredients": [
    1
  ],
  "pipate": "fine",
  "pibase": "tomate"
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 201 | La pizza a été ajoutée avec succès |
| 409 | Une pizza avec le même nom existe déjà |

#### Ajouter un ingrédient à la pizza

Requête vers le serveur : POST /pizzalandapi/pizzas/1

```json
{
  "ingredients": [
    2,3
  ]
}
```

Reponse du serveur :

```json
{
  "pino": 1,
  "piname": "margarita",
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
    },
    {
      "id": 3,
      "name": "fromage",
      "price": 1
    }
  ],
  "price": 1.8,
  "pipate": "fine",
  "pibase": "tomate"
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | L'ingrédient a été ajouté avec succès |
| 404 | La pizza ou un des ingrédients n'existe pas |

#### Supprimer une pizza

Requête vers le serveur : DELETE /pizzalandapi/pizzas/1

Reponse du serveur :

```json
{
  "pino": 1,
  "piname": "margarita",
  "ingredients": [
    {
      "id": 1,
      "name": "tomate",
      "price": 0.5
    }
  ],
  "price": 0.5,
  "pipate": "fine",
  "pibase": "tomate"
}
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 | La pizza a été supprimée avec succès |
| 404 | La pizza n'existe pas |

#### Supprimer un ingrédient de la pizza

Requête vers le serveur : DELETE /pizzalandapi/pizzas/1/1

```json
{
  "ino": 1,
  "iname": "tomate",
  "iprice": 0.5
}
```

Codes de status HTTP

| Status | Description |
|:---------:|:---------:|
| 200 | L'ingrédient a été de la pizza avec succès |
| 404 | La pizza n'existe pas ou l'ingrédient n'existe pas dans la pizza |

#### Modifier un champs de la pizza

Requête vers le serveur : PATCH /pizzalandapi/pizzas/1

```json
{
  "piname": "reine",
  "pipate": "grosse"
}
```

Reponse du serveur :

```json
{
  "pino": 1,
  "piname": "reine",
  "ingredients": [
    {
      "id": 1,
      "name": "tomate",
      "price": 0.5
    }
  ],
  "price": 0.5,
  "pipate": "grosse",
  "pibase": "tomate"
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La pizza a été modifiée avec succès |
| 404 | La pizza ou un des ingrédients n'existe pas |

#### Modifier totalement la pizza

Requête vers le serveur : PUT /pizzalandapi/pizzas/1

```json
{
  "piname": "reine",
  "ingredients": [
    3
  ],
  "pipate": "grosse",
  "pibase": "tomate"
}
```

Reponse du serveur :

```json
{
  "pino": 1,
  "piname": "reine",
  "ingredients": [
    {
      "id": 3,
      "name": "fromage",
      "price": 1
    }
  ],
  "price": 1,
  "pipate": "grosse",
  "pibase": "tomate"
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La pizza a été modifiée avec succès |
| 404 | La pizza ou un des ingrédients n'existe pas |

## Commandes

| URI | Opération | MIME | Requête | Réponse |
|:---:|:---------:|:----:|:-------:|:-------:|
| /commandes | GET | <- application/json | | Toutes les commandes |
| /commandes/{id} | GET | <- application/json | | Commande (c1) ou 404 |
| /commandes/{id}/prixfinal | GET | <- application/json | | Prix final de la commande (c2) ou 404 |
| /commandes | POST | <-/-> application/json | Commande (c3) | Commande ajoutée |
| /commandes/{id} | POST | <-/-> application/json | Ligne commande (c4) | Commande avec pizza ajoutée ou 404 |
| /commandes/{id} | DELETE | | | Commande supprimée ou 404 |

### Corps des réponses/requêtes

#### c1

```json
{
  "cno": 1,
  "cname": "farid",
  "date": "17/01/2021",
  "pizzas": [
    {
      "pqte": 1,
      "pizza": {
        "pino": 1,
        "piname": "margarita",
        "ingredients": [
          {
            "id": 1,
            "name": "tomate",
            "price": 0.5
          }
        ],
        "price": 0.5,
        "pipate": "fine",
        "pibase": "tomate"
      }
    }
  ],
  "price": 0.5
}
```

Une commande comporte un id, un nom , un prix total , une liste de pizza, une date et un prix total . Sa représentation json est la suivante :

```json
{
  "id": 1,
  "name": "farid",
  "date": "2021-01-01",
  "price": 5,
  "pizzas": [
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
  ]
}
```

#### c2

```json
{
  "prixfinal": 0.5
}
```

#### c3

```json
{
  "cname": "farid",
  "pizzas": [
    {"pqte": 1, "pizza": 1}
  ]
}
```

#### c4

```json
[
  {"pqte": 1, "pizza": 1}
]
```

### Exemple

#### Lister toutes les commandes connu dans la base de données

Requête vers le serveur

```json
[
  {
    "cno": 1,
    "cname": "farid",
    "date": "17/01/2021",
    "pizzas": [
      {
        "pqte": 1,
        "pizza": {
          "pino": 1,
          "piname": "margarita",
          "ingredients": [
            {
              "id": 1,
              "name": "tomate",
              "price": 0.5
            }
          ],
          "price": 0.5,
          "pipate": "fine",
          "pibase": "tomate"
        }
      }
    ],
    "price": 0.5
  }
]
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La requête s'est effectuée correctement |

#### Récupérer les détails de la commande

Requête vers le serveur : GET /pizzalandapi/commandes/1

```json
{
  "cno": 1,
  "cname": "farid",
  "date": "17/01/2021",
  "pizzas": [
    {
      "pqte": 1,
      "pizza": {
        "pino": 1,
        "piname": "margarita",
        "ingredients": [
          {
            "id": 1,
            "name": "tomate",
            "price": 0.5
          }
        ],
        "price": 0.5,
        "pipate": "fine",
        "pibase": "tomate"
      }
    }
  ],
  "price": 0.5
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La requête s'est effectuée correctement |
| 404 | La commande n'existe pas |

#### Récupérer le prix final de la commande

Requête vers le serveur : GET /pizzalandapi/commandes/1/prixfinal

```json
{
  "prixfinal": 0.5
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La requête s'est effectuée correctement |
| 404 | La commande n'existe pas |

#### Ajouter une commande

Requête vers le serveur : POST /pizzalandapi/commandes

```json
{
  "cname": "farid",
  "pizzas": [
    {"pqte": 1, "pizza": 1}
  ]
}

Reponse du serveur :

```json
{
  "cname": "farid",
  "pizzas": [
    {"pqte": 1, "pizza": 1}
  ]
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 201 | La commande a été ajoutée avec succès |

#### Ajouter une pizza à la commande

Requête vers le serveur : POST /pizzalandapi/commandes/1

```json
[
  {
    "pqte": 3,
    "pizza": 1
  }
]
```

Reponse du serveur :

```json
{
  "cno": 1,
  "cname": "farid",
  "date": "17/01/2021",
  "pizzas": [
    {
      "pqte": 4,
      "pizza": {
        "pino": 1,
        "piname": "margarita",
        "ingredients": [
          {
            "id": 1,
            "name": "tomate",
            "price": 0.5
          }
        ],
        "price": 0.5,
        "pipate": "fine",
        "pibase": "tomate"
      }
    }
  ],
  "price": 2
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La pizza a été ajoutée avec succès |
| 404 | La commande ou une des pizzas n'existe pas |

#### Supprimer une commande

Requête vers le serveur : DELETE /pizzalandapi/commandes/1

Reponse du serveur :

```json
{
  "cno": 1,
  "cname": "farid",
  "date": "17/01/2021",
  "pizzas": [
    {
      "pqte": 1,
      "pizza": {
        "pino": 1,
        "piname": "margarita",
        "ingredients": [
          {
            "id": 1,
            "name": "tomate",
            "price": 0.5
          }
        ],
        "price": 0.5,
        "pipate": "fine",
        "pibase": "tomate"
      }
    }
  ],
  "price": 0.5
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La commande a été supprimée avec succès |
| 404 | La commande n'existe pas |

#### Supprimer une pizza de la commande

Requête vers le serveur : DELETE /pizzalandapi/commandes/1/1

```json
{
  "pino": 1,
  "piname": "margarita",
  "ingredients": [
    {
      "id": 1,
      "name": "tomate",
      "price": 0.5
    }
  ],
  "price": 0.5,
  "pipate": "fine",
  "pibase": "tomate"
}
```

Codes de status HTTP

| Status | Description |
|:------:|:-----------:|
| 200 | La pizza a été supprimée de la commande avec succès |
| 404 | La commande n'existe pas ou la pizza n'existe pas dans la commande |
