#SAE 2.01 et 2.02
## Aventuriers du rails

**Cette partie se découpe en deux parties, représentées sur les deux branches de ce projet. Vous pouvez retrouver les deux consignes des projets ci-dessous**

## Premiere consigne
### Consignes générales
* Pour que le projet soit fini, vous devez implémenter correctement l'ensemble de méthodes levant une exception avec l'instruction `throw new RuntimeException("Méthode non implémentée !")`.
* **Les signatures des méthodes qui vous sont fournies doivent rester inchangées.**
* N'hésitez pas à _ajouter_ des fonctions utilitaires qui vous paraissent nécessaires. 
* Vous respecterez les bonnes pratiques en programmation objet vues en cours.  
* Le respect des conventions de nommage du langage Java est **impératif**.
* Votre base de code **doit être en permanence testée** et donc toujours être dans un état sain (le code compile et les tests passent). **Un programme qui ne compile pas ne doit en aucun cas être soumis avec `git commit`!**
* Comment tester votre programme ? Voici quelques consignes :

    1. En écrivant vos propres tests unitaires.  
    2. En exécutant les tests unitaires qui vous ont été fournis dans le repertoire `src/test/java` :
        * La plupart de ces tests sont annotés `@Disabled` et donc pour l'instant, ils sont ignorés.
        * Au fur et à mesure de l'avancement de votre projet vous activerez chaque test (en supprimant l'annotation `@Disabled`) et vérifierez que ce test passe.
        * **Attention** : n'essayez pas de passer tous les tests en même temps (principe _BabySteps_)
        * Ne faites pas de `git add/commit` tant que des tests non annotés `@Disabled` ne passent pas.
        * **Remarque** : soyez vigilants même si votre programme passe tous les tests, car en règle générale un programme n'est **jamais** suffisamment testé...
    
* Certaines précisions ou consignes pourront être ajoutées ultérieurement et vous en serez informés.
* Surveillez l'activité sur [le forum](https://piazza.com/class/kyo4oooauez252), les nouvelles informations y seront mentionnées. N'hésitez pas à y poser des questions, surtout si les réponses pourraient intéresser les autres équipes.

### Conseils concernant la gestion de version
* Chaque commit devrait être accompagné d'un message permettant de comprendre l'objet de la modification.
* Vos _commits_ doivent être les plus petits possibles. Un commit qui fait 10 modifications de code sans lien entre elles, devra être découpé en 10 _commits_.
* On vous conseille d'utiliser des _branches Git_ différentes lors du développement d'une fonctionnalité importante. Le nom de la branche doit être au maximum porteur de sens. Une fois que vous pensez que le code de la fonctionnalité est fini (tous les tests associés à celle-ci passent), vous fusionnerez le code de sa branche avec la branche `master`.

## Deuxieme consigne
### Consignes générales
* Pour que le projet soit fini, vous devez implémenter correctement l'ensemble des classes présentées sur le diagramme de classes.
* Dans vos classes, vous pouvez ajouter toutes les méthodes qui vous paraissent nécessaires. Pour ce qui est de la partie logique, si besoin, vous pouvez ajouter et exporter des propriétés, mais cela n'est pas obligatoire.
* Vous respecterez les bonnes pratiques en programmation objet vues en cours.
* Le respect des conventions de nommage du langage Java est **impératif**.
* Si vous souhaitez ajouter des tests unitaires, vous pouvez utiliser le framework [TestFX](https://github.com/TestFX/TestFX) qui permet de définir des tests sur une IHM en JavaFX, une dépendance sur une version du framework étant définie dans le pom du projet. Toutefois, cela peut représenter un investissement non négligeable, puisque nous n'avons pas eu l'occasion de voir cela pendant notre cours.
* Certaines précisions ou consignes pourront être ajoutées ultérieurement et vous en serez informés.
* Surveillez l'activité sur [le forum](https://piazza.com/class/l1z3nj7vsay33), les nouvelles informations y seront mentionnées. N'hésitez pas à y poser des questions, surtout si les réponses pourraient intéresser les autres équipes.

### Conseils concernant la gestion de version
* Chaque commit devrait être accompagné d'un message permettant de comprendre l'objet de la modification.
* Vos _commits_ doivent être les plus petits possibles. Un commit qui fait 10 modifications de code sans lien entre elles, devra être découpé en 10 _commits_.
* On vous conseille d'utiliser des _branches Git_ différentes lors du développement d'une fonctionnalité importante. Le nom de la branche doit facilement permettre de comprendre la fonctionnalité qui est implémentée. Une fois que vous pensez que le code de la fonctionnalité est fini (tous les tests associés à celle-ci passent), vous fusionnerez le code de sa branche avec la branche `master`.
