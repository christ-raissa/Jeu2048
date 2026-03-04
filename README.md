# Jeu2048

Jeu 2048 développé par les étudiants de la **Rochelle Université**, version améliorée.

##  Installation et configuration

###  Cloner le projet
git clone https://github.com/christ-raissa/Jeu2048.git

cd Jeu2048

## Créer sa branche de travail
On peut créer une branche avec ton nom ou fonctionnalité :

git checkout -b feature-tonNom

Par exemple :

Personne A → feature-core

Personne B → feature-ui

Personne C → feature-storage

git push -u origin feature-tonNom

## Afficher les branches locales et distances 
git branch -a 

## Commits fréquents à faire sur sa branche en local

git checkout -b tabranche    # crée et bascule sur la branche locale

git add .

git commit -m "Ajout de la grille 4x4 et logique des fusions"

git push -u origin feature-core  # pousse sur le remote


## Mettre à jour sa branche avant de fusionner

Avant de proposer de fusionner vers main, il faut intégrer les derniers changements de main :

git checkout tabranche

git pull origin main --rebase   

en utilisant -- merge cela permettrait de résoudre d’éventuels conflits avant de toucher à main.

## Fusionner sur la branche main

Quand la fonctionnalité est testée et stable, on fait un merge ou pull request vers main.

Dans GitHub, c’est souvent via **Pull Request**  pour revue avant de fusionner:

On vérifier que : **Base = main** et **compare = tabranche**
    

git checkout main

git pull origin main          # récupérer les dernières modifications

git merge tabranche        # fusionner la branche testée

git push origin main          # pousser main mise à jour



