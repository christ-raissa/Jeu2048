# Jeu2048

Jeu 2048 développé par les étudiants de la **Rochelle Université**, version améliorée.

##  Installation et configuration

###  Cloner le projet
git clone https://github.com/christ-raissa/Jeu2048.git

cd Jeu2048

## Créer sa branche de travail
On peut créer une branche avec ton nom ou fonctionnalité :

git checkout -b feature-tonNom

git push -u origin feature-tonNom

## Synchroniser son projet avant de coder
Avant de commencer à coder chaque jour, on peut d'abord commencer par : 

git checkout main

git pull origin main

git checkout feature-tonNom

git merge main

## Valider ses changements et les pousser sur GitHub
git add .

git commit -m "Description claire de vos modifications"

git push


## Fusionner dans main
Pour intégrer tes changements à main :

--> Aller sur le repository GitHub

--> Cliquer sur Compare & Pull Request

On vérifier que :
    Base = main
    compare = feature-tonNom
    
--> Cliquer sur Create Pull Request puis Merge

## Astuces
Pour voir les branches locales et distantes :

--> git branch -a

Pour changer de branche :

--> git checkout nom-branche

Pour résoudre un conflit (si Git te le demande) :

--> git merge main

## Corriger manuellement les fichiers conflictuels puis :
--> git add .

--> git commit -m "Résolution des conflits"

--> git push
