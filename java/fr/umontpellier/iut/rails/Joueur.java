package fr.umontpellier.iut.rails;

import java.util.*;
import java.util.stream.Collectors;

public class Joueur {


    /**
     * Les couleurs possibles pour les joueurs (pour l'interface graphique)
     */
    public static enum Couleur {
        JAUNE, ROUGE, BLEU, VERT, ROSE
    }

    /**
     * Jeu auquel le joueur est rattaché
     */
    private final Jeu jeu;
    /**
     * Nom du joueur
     */
    private String nom;
    /**
     * CouleurWagon du joueur (pour représentation sur le plateau)
     */
    private Couleur couleur;
    /**
     * Nombre de gares que le joueur peut encore poser sur le plateau
     */
    private int nbGares;
    /**
     * Nombre de wagons que le joueur peut encore poser sur le plateau
     */
    private int nbWagons;
    /**
     * Liste des missions à réaliser pendant la partie
     */
    private List<Destination> destinations;
    /**
     * Liste des cartes que le joueur a en main
     */
    private List<CouleurWagon> cartesWagon;
    /**
     * Liste temporaire de cartes wagon que le joueur est en train de jouer pour
     * payer la capture d'une route ou la construction d'une gare
     */
    private List<CouleurWagon> cartesWagonPosees;
    /**
     * Score courant du joueur (somme des valeurs des routes capturées)
     */
    private int score;

    public Joueur(String nom, Jeu jeu, Joueur.Couleur couleur) {
        this.nom = nom;
        this.jeu = jeu;
        this.couleur = couleur;
        nbGares = 3;
        nbWagons = 45;
        cartesWagon = new ArrayList<>();
        cartesWagonPosees = new ArrayList<>();
        destinations = new ArrayList<>();
        score = 12; // chaque gare non utilisée vaut 4 points
    }

    public String getNom() {
        return nom;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public int getNbWagons() {
        return nbWagons;
    }

    public Jeu getJeu() {
        return jeu;
    }

    public List<CouleurWagon> getCartesWagonPosees() {
        return cartesWagonPosees;
    }

    public List<CouleurWagon> getCartesWagon() {
        return cartesWagon;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    /**
     * Attend une entrée de la part du joueur (au clavier ou sur la websocket) et
     * renvoie le choix du joueur.
     * <p>
     * Cette méthode lit les entrées du jeu ({@code Jeu.lireligne()}) jusqu'à ce
     * qu'un choix valide (un élément de {@code choix} ou de {@code boutons} ou
     * éventuellement la chaîne vide si l'utilisateur est autorisé à passer) soit
     * reçu.
     * Lorsqu'un choix valide est obtenu, il est renvoyé par la fonction.
     * <p>
     * Si l'ensemble des choix valides ({@code choix} + {@code boutons}) ne comporte
     * qu'un seul élément et que {@code canPass} est faux, l'unique choix valide est
     * automatiquement renvoyé sans lire l'entrée de l'utilisateur.
     * <p>
     * Si l'ensemble des choix est vide, la chaîne vide ("") est automatiquement
     * renvoyée par la méthode (indépendamment de la valeur de {@code canPass}).
     * <p>
     * Exemple d'utilisation pour demander à un joueur de répondre à une question
     * par "oui" ou "non" :
     * <p>
     * {@code
     * List<String> choix = Arrays.asList("Oui", "Non");
     * String input = choisir("Voulez vous faire ceci ?", choix, new ArrayList<>(), false);
     * }
     * <p>
     * <p>
     * Si par contre on voulait proposer les réponses à l'aide de boutons, on
     * pourrait utiliser :
     * <p>
     * {@code
     * List<String> boutons = Arrays.asList("1", "2", "3");
     * String input = choisir("Choisissez un nombre.", new ArrayList<>(), boutons, false);
     * }
     *
     * @param instruction message à afficher à l'écran pour indiquer au joueur la
     *                    nature du choix qui est attendu
     * @param choix       une collection de chaînes de caractères correspondant aux
     *                    choix valides attendus du joueur
     * @param boutons     une collection de chaînes de caractères correspondant aux
     *                    choix valides attendus du joueur qui doivent être
     *                    représentés par des boutons sur l'interface graphique.
     * @param peutPasser  booléen indiquant si le joueur a le droit de passer sans
     *                    faire de choix. S'il est autorisé à passer, c'est la
     *                    chaîne de caractères vide ("") qui signifie qu'il désire
     *                    passer.
     * @return le choix de l'utilisateur (un élément de {@code choix}, ou de
     * {@code boutons} ou la chaîne vide)
     */
    public String choisir(String instruction, Collection<String> choix, Collection<String> boutons,
                          boolean peutPasser) {
        // on retire les doublons de la liste des choix
        HashSet<String> choixDistincts = new HashSet<>();
        choixDistincts.addAll(choix);
        choixDistincts.addAll(boutons);

        // Aucun choix disponible
        if (choixDistincts.isEmpty()) {
            return "";
        } else {
            // Un seul choix possible (renvoyer cet unique élément)
            if (choixDistincts.size() == 1 && !peutPasser)
                return choixDistincts.iterator().next();
            else {
                String entree;
                // Lit l'entrée de l'utilisateur jusqu'à obtenir un choix valide
                while (true) {
                    jeu.prompt(instruction, boutons, peutPasser);
                    entree = jeu.lireLigne();
                    // si une réponse valide est obtenue, elle est renvoyée
                    if (choixDistincts.contains(entree) || (peutPasser && entree.equals("")))
                        return entree;
                }
            }
        }
    }

    /**
     * Affiche un message dans le log du jeu (visible sur l'interface graphique)
     *
     * @param message le message à afficher (peut contenir des balises html pour la
     *                mise en forme)
     */
    public void log(String message) {
        jeu.log(message);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format("=== %s (%d pts) ===", nom, score));
        joiner.add(String.format("  Gares: %d, Wagons: %d", nbGares, nbWagons));
        joiner.add("  Destinations: "
                + destinations.stream().map(Destination::toString).collect(Collectors.joining(", ")));
        joiner.add("  Cartes wagon: " + CouleurWagon.listToString(cartesWagon));
        return joiner.toString();
    }

    /**
     * @return une chaîne de caractères contenant le nom du joueur, avec des balises
     * HTML pour être mis en forme dans le log
     */
    public String toLog() {
        return String.format("<span class=\"joueur\">%s</span>", nom);
    }

    /**
     * Renvoie une représentation du joueur sous la forme d'un objet Java simple
     * (POJO)
     */
    public Object asPOJO() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("nom", nom);
        data.put("couleur", couleur);
        data.put("score", score);
        data.put("nbGares", nbGares);
        data.put("nbWagons", nbWagons);
        data.put("estJoueurCourant", this == jeu.getJoueurCourant());
        data.put("destinations", destinations.stream().map(Destination::asPOJO).collect(Collectors.toList()));
        data.put("cartesWagon", cartesWagon.stream().sorted().map(CouleurWagon::name).collect(Collectors.toList()));
        data.put("cartesWagonPosees",
                cartesWagonPosees.stream().sorted().map(CouleurWagon::name).collect(Collectors.toList()));
        return data;
    }

    /**
     * Propose une liste de cartes destinations, parmi lesquelles le joueur doit en
     * garder un nombre minimum n.
     * <p>
     * Tant que le nombre de destinations proposées est strictement supérieur à n,
     * le joueur peut choisir une des destinations qu'il retire de la liste des
     * choix, ou passer (en renvoyant la chaîne de caractères vide).
     * <p>
     * Les destinations qui ne sont pas écartées sont ajoutées à la liste des
     * destinations du joueur. Les destinations écartées sont renvoyées par la
     * fonction.
     *
     * @param destinationsPossibles liste de destinations proposées parmi lesquelles
     *                              le joueur peut choisir d'en écarter certaines
     * @param n                     nombre minimum de destinations que le joueur
     *                              doit garder
     * @return liste des destinations qui n'ont pas été gardées par le joueur
     */
    public List<Destination> choisirDestinations(List<Destination> destinationsPossibles, int n) {
        List<Destination> ecarte = new ArrayList<>();
        List<String> destinationsChoisis = new ArrayList<>();


        for (Destination dest : destinationsPossibles) {
            destinationsChoisis.add(dest.toString());
        }

        boolean verif = false;
        while (destinationsPossibles.size() > n && !verif) {
            String choix = this.choisir(
                    "Choisissez les destinations à retirer :",
                    destinationsChoisis,
                    destinationsChoisis,
                    true);

            boolean test = false;
            int i =0;
            while(!test && i<destinationsPossibles.size()){
                Destination dest = destinationsPossibles.get(i);
                if (choix.equals(String.valueOf(dest))) {
                    ecarte.add(dest);
                    destinationsPossibles.remove(dest);
                    destinationsChoisis.remove(String.valueOf(dest));
                    test = true;
                }
                i++;
            }

            if (choix.equals("")) {
                verif = true;
            }
        }
        this.destinations.addAll(destinationsPossibles);


        return ecarte;
    }

    public void piocherWagons(CouleurWagon c) {
        if (c == CouleurWagon.LOCOMOTIVE) {
            jeu.retirerCarteWagonVisible(c);
            this.cartesWagon.add(c);
        } else {
            if (c == CouleurWagon.GRIS) {
                this.cartesWagon.add(jeu.piocherCarteWagon());
            } else {
                jeu.retirerCarteWagonVisible(c);
                this.cartesWagon.add(c);
            }
            ArrayList<String> temp = new ArrayList<>();
            for (int i = 0; i < jeu.getCartesWagonVisibles().size(); i++) {
                if (jeu.getCartesWagonVisibles().get(i) != CouleurWagon.LOCOMOTIVE)
                    temp.add(jeu.getCartesWagonVisibles().get(i).name());
            }
            temp.add("GRIS");
            String choix = this.choisir(
                    "Piochez une seconde carte",
                    new ArrayList<>(temp),
                    new ArrayList<>(),
                    false);
            CouleurWagon c2 = CouleurWagon.valueOf(choix);
            if (choix.equals("GRIS")) {
                this.cartesWagon.add(jeu.piocherCarteWagon());
            } else {
                jeu.retirerCarteWagonVisible(c2);
                this.cartesWagon.add(c2);
            }

        }
    }

    public void poserWagon(Route route) {
        List<CouleurWagon> cartesADefausser = new ArrayList<>();
        Map<Integer, Integer> scoring = new HashMap<>();
        scoring.put(1, 1);
        scoring.put(2, 2);
        scoring.put(3, 4);
        scoring.put(4, 7);
        scoring.put(6, 15);
        scoring.put(8, 21);
        int compteurCartes = 0;
        boolean fini = true;
        String couleur = "GRIS";


        log(this.nom + " souhaite poser une route.");
        //Tunnel
        if (route instanceof Tunnel) {
            Tunnel t = (Tunnel) route;
            if (t.getCouleur() == CouleurWagon.GRIS) { //Tunnel gris
                couleur = "null";
                while (compteurCartes < route.getLongueur()) {
                    List<String> listeCartesWagon = new ArrayList<>();
                    for (CouleurWagon elt : this.cartesWagon) {
                        listeCartesWagon.add(elt.name());
                    }
                    String choix = this.choisir(
                            "Choisissez un wagon :",
                            listeCartesWagon,
                            new ArrayList<>(),
                            true);
                    if (choix.equals(CouleurWagon.LOCOMOTIVE.name())) {
                        cartesADefausser.add(CouleurWagon.LOCOMOTIVE);
                        this.cartesWagon.remove(CouleurWagon.LOCOMOTIVE);
                        compteurCartes++;
                        couleur=choix;
                    } else if (Collections.frequency(this.cartesWagon, CouleurWagon.valueOf(choix)) + compteurCartes + Collections.frequency(this.cartesWagon, CouleurWagon.LOCOMOTIVE) >= t.getLongueur() && couleur.equals("null")) {
                        couleur = choix;
                        cartesADefausser.add(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurCartes++;
                    } else if (choix.equals(couleur)) {
                        cartesADefausser.add(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurCartes++;
                    }
                }
            }
            //Tunnel de couleur
            else {
                String choix;

                while (compteurCartes < t.getLongueur() && fini) {
                    List<String> listeCartesWagon = new ArrayList<>();
                    for (CouleurWagon elt : this.cartesWagon) {
                        listeCartesWagon.add(elt.name());
                    }
                    choix = this.choisir(
                            "Choisissez les wagons à retirer :",
                            listeCartesWagon,
                            new ArrayList<>(),
                            true);
                    if (choix.equals("")) {
                        // le joueur a choisi une route
                        fini = false;
                        log("Aucune route n'a été choisie");
                    } else if (choix.equals(t.getCouleur().name()) || choix.equals(CouleurWagon.LOCOMOTIVE.name())) {

                        cartesADefausser.add(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurCartes++;
                    }
                }
            }
            ArrayList<CouleurWagon> cartesRevealsTunnels = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                cartesRevealsTunnels.add(jeu.piocherCarteWagon());
            }
            for (CouleurWagon cartesRevealsTunnel : cartesRevealsTunnels) {
                jeu.defausserCarteWagon(cartesRevealsTunnel);
            }
            ArrayList<CouleurWagon> test = new ArrayList<>(cartesRevealsTunnels);
            int compteurRemove = 0;
            for (int i = 0; i < test.size(); i++) {
                if (!cartesRevealsTunnels.get(i - compteurRemove).equals(CouleurWagon.valueOf(couleur)) && !cartesRevealsTunnels.get(i - compteurRemove).equals(t.getCouleur())) {
                    cartesRevealsTunnels.remove(i - compteurRemove);
                    compteurRemove++;
                }
            }
            while (compteurCartes < t.getLongueur() + cartesRevealsTunnels.size() && fini) {
                if (t.getCouleur().equals(CouleurWagon.GRIS)) {
                    if (Collections.frequency(this.cartesWagon, CouleurWagon.valueOf(couleur)) + Collections.frequency(this.cartesWagon, CouleurWagon.LOCOMOTIVE) < cartesRevealsTunnels.size()) {
                        fini = false;
                        break;
                    }
                } else {
                    if (Collections.frequency(this.cartesWagon, t.getCouleur()) + Collections.frequency(this.cartesWagon, CouleurWagon.LOCOMOTIVE) < cartesRevealsTunnels.size()) {
                        fini = false;
                        break;
                    }
                }
                String choix;
                List<String> listeCartesWagon = new ArrayList<>();
                for (CouleurWagon elt : this.cartesWagon) {
                    listeCartesWagon.add(elt.name());
                }
                log("Les cartes à retirer en plus sont : "+cartesRevealsTunnels.toString());
                choix = this.choisir(
                        "Retirez les cartes supplémentaires :",
                        listeCartesWagon,
                        new ArrayList<>(),
                        true);
                if (choix.equals("")) {
                    // le joueur a choisi une route
                    fini = false;
                    log("Aucune route n'a été choisie");
                } else if (choix.equals(t.getCouleur().name()) || choix.equals(CouleurWagon.LOCOMOTIVE.name()) || choix.equals(couleur)) {
                    cartesADefausser.add(CouleurWagon.valueOf(choix));
                    this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                    compteurCartes++;
                }
            }
        } //Ferry
        else if (route instanceof Ferry) {
            Ferry f = (Ferry) route;
            int compteurLocomotives = 0;
            ArrayList<CouleurWagon> copie = new ArrayList<>(this.cartesWagon);
            System.out.println(copie);
            int compteurRemove = 0;
            for (int i = 0; i < copie.size(); i++) {
                if (copie.get(i) == CouleurWagon.LOCOMOTIVE) {
                    compteurLocomotives++;
                    if (compteurLocomotives <= f.getNbLocomotives()) {
                        this.cartesWagon.remove(i - compteurRemove);
                        compteurRemove++;
                        cartesADefausser.add(CouleurWagon.LOCOMOTIVE);
                    }
                }
            }

            if (compteurLocomotives < f.getNbLocomotives()) {
                fini = false;
            } else if (f.getLongueur() > compteurLocomotives) {
                couleur = "null";
                while (compteurCartes + compteurRemove < route.getLongueur()) {
                    List<String> listeCartesWagon = new ArrayList<>();
                    for (CouleurWagon elt : this.cartesWagon) {
                        listeCartesWagon.add(elt.name());
                    }
                    String choix = this.choisir(
                            "Choisissez un wagon :",
                            listeCartesWagon,
                            new ArrayList<>(),
                            true);
                    if (choix.equals(CouleurWagon.LOCOMOTIVE.name())) {
                        cartesADefausser.add(CouleurWagon.LOCOMOTIVE);
                        this.cartesWagon.remove(CouleurWagon.LOCOMOTIVE);
                        compteurCartes++;
                    } else if (Collections.frequency(this.cartesWagon, CouleurWagon.valueOf(choix)) + compteurCartes + Collections.frequency(this.cartesWagon, CouleurWagon.LOCOMOTIVE) + compteurLocomotives >= f.getLongueur() && couleur.equals("null")) {
                        couleur = choix;
                        cartesADefausser.add(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurCartes++;
                    } else if (choix.equals(couleur)) {
                        cartesADefausser.add(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurCartes++;
                    }
                }
            }
            //Si fini = false → tu remets les cartes dans cartes défausser dans la main sinon tu les défausses
        } else { //Route normal
            if (route.getCouleur() == CouleurWagon.GRIS) { //Route normal grise
                couleur = "null";
                while (compteurCartes < route.getLongueur()) {
                    List<String> listeCartesWagon = new ArrayList<>();
                    for (CouleurWagon elt : this.cartesWagon) {
                        listeCartesWagon.add(elt.name());
                    }
                    String choix = this.choisir(
                            "Choisissez un wagon :",
                            listeCartesWagon,
                            new ArrayList<>(),
                            true);
                    if (choix.equals(CouleurWagon.LOCOMOTIVE.name())) {
                        cartesADefausser.add(CouleurWagon.LOCOMOTIVE);
                        this.cartesWagon.remove(CouleurWagon.LOCOMOTIVE);
                        compteurCartes++;
                    } else if (Collections.frequency(this.cartesWagon, CouleurWagon.valueOf(choix)) + compteurCartes + Collections.frequency(this.cartesWagon, CouleurWagon.LOCOMOTIVE) >= route.getLongueur() && couleur.equals("null")) {
                        couleur = choix;
                        cartesADefausser.add(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurCartes++;
                    } else if (choix.equals(couleur)) {
                        cartesADefausser.add(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurCartes++;
                    }
                }
            }
            //Route normale de couleur
            else {
                String choix;
                while (compteurCartes < route.getLongueur() && fini) {
                    List<String> listeCartesWagon = new ArrayList<>();
                    for (CouleurWagon elt : this.cartesWagon) {
                        listeCartesWagon.add(elt.name());
                    }
                    choix = this.choisir(
                            "Choisissez les wagons à retirer :",
                            listeCartesWagon,
                            new ArrayList<>(),
                            true);
                    if (choix.equals("")) {
                        // le joueur a choisi une route
                        fini = false;
                        log("Aucune route n'a été choisie");
                    } else if (choix.equals(route.getCouleur().name()) || choix.equals(CouleurWagon.LOCOMOTIVE.name())) {
                        cartesADefausser.add(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurCartes++;
                    }
                }
            }
        }
        if (!fini) {
            this.cartesWagon.addAll(cartesADefausser);
        } else {
            this.score += scoring.get(route.getLongueur());
            this.nbWagons -= route.getLongueur();
            route.setProprietaire(this);
            for (CouleurWagon couleurWagon : cartesADefausser) {
                jeu.defausserCarteWagon(couleurWagon);
            }
        }
        log(this.nom + " a poser une route entre "+route.getVille1()+" et "+route.getVille2());

    }

    public void poserGare(Ville ville) {
        int nbloc = Collections.frequency(this.cartesWagon, CouleurWagon.LOCOMOTIVE);

            if (this.nbGares == 3 && this.cartesWagon.size() >= 1) {
                log(this.nom + " veut poser une gare sur " + ville.getNom());
                List<String> listeCartesWagon = new ArrayList<>();
                for (CouleurWagon elt : this.cartesWagon) {
                    listeCartesWagon.add(elt.name());
                }
                String choix = this.choisir(
                        "Choisissez une carte wagon à retirer :",
                        listeCartesWagon,
                        new ArrayList<>(),
                        false);
                jeu.defausserCarteWagon(CouleurWagon.valueOf(choix));
                this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                ville.setProprietaire(this);
                this.nbGares--;
                this.score-=4;
            } else if (this.nbGares == 2 && this.cartesWagon.size() >= 2) {

                log(this.nom + " veut poser une gare sur " + ville.getNom());
                int compteurChoix = 0;
                String couleurType = "null";
                while (compteurChoix < 2) {
                    List<String> listeCartesWagon = new ArrayList<>();
                    for (CouleurWagon elt : this.cartesWagon) {
                        listeCartesWagon.add(elt.name());
                    }
                    String choix = this.choisir(
                            "Choisissez une carte wagon à retirer :",
                            listeCartesWagon,
                            new ArrayList<>(),
                            false);

                    if (choix.equals("LOCOMOTIVE")) {
                        jeu.defausserCarteWagon(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurChoix++;
                    } else if (couleurType.equals("null") && Collections.frequency(this.cartesWagon, CouleurWagon.valueOf(choix)) + nbloc > 1) {
                        couleurType = choix;
                        jeu.defausserCarteWagon(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurChoix++;
                    } else if (choix.equals(couleurType)) {
                        jeu.defausserCarteWagon(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurChoix++;
                    }
                }
                ville.setProprietaire(this);
                this.nbGares--;
                this.score-=4;

            } else if (this.nbGares == 1 && this.cartesWagon.size() >= 3) {


                log(this.nom + " veut poser une gare sur " + ville.getNom());
                int compteurChoix = 0;
                String couleurType = "null";
                while (compteurChoix < 3) {
                    List<String> listeCartesWagon = new ArrayList<>();
                    for (CouleurWagon elt : this.cartesWagon) {
                        listeCartesWagon.add(elt.name());
                    }
                    String choix = this.choisir(
                            "Choisissez une carte wagon à retirer :",
                            listeCartesWagon,
                            new ArrayList<>(),
                            false);

                    if (choix.equals("LOCOMOTIVE")) {
                        jeu.defausserCarteWagon(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurChoix++;
                    } else if (couleurType.equals("null") && Collections.frequency(this.cartesWagon, CouleurWagon.valueOf(choix)) + nbloc > 2) {
                        couleurType = choix;
                        jeu.defausserCarteWagon(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurChoix++;
                    } else if (choix.equals(couleurType)) {
                        jeu.defausserCarteWagon(CouleurWagon.valueOf(choix));
                        this.cartesWagon.remove(CouleurWagon.valueOf(choix));
                        compteurChoix++;
                    }
                }
                ville.setProprietaire(this);
                this.nbGares--;
                this.score-=4;
            }

            log(this.nom+" a poser une gare sur "+ville.getNom());
    }


    /**
     * Exécute un tour de jeu du joueur.
     * <p>
     * Cette méthode attend que le joueur choisisse une des options suivantes :
     * - le nom d'une carte wagon face visible à prendre ;
     * - le nom "GRIS" pour piocher une carte wagon face cachée s'il reste des
     * cartes à piocher dans la pile de pioche ou dans la pile de défausse ;
     * - la chaîne "destinations" pour piocher des cartes destination ;
     * - le nom d'une ville sur laquelle il peut construire une gare (ville non
     * prise par un autre joueur, le joueur a encore des gares en réserve et assez
     * de cartes wagon pour construire la gare) ;
     * - le nom d'une route que le joueur peut capturer (pas déjà capturée, assez de
     * wagons et assez de cartes wagon) ;
     * - la chaîne de caractères vide pour passer son tour
     * <p>
     * Lorsqu'un choix valide est reçu, l'action est exécutée (il est possible que
     * l'action nécessite d'autres choix de la part de l'utilisateur, comme "choisir
     * les cartes wagon à défausser pour capturer une route" ou
     * "construire une gare", "choisir les destinations à défausser", etc.)
     */
    public void jouerTour() {
        log("Au tour de "+this.nom);

        ArrayList<String> listeChoix = new ArrayList<>();

        //Liste des villes disponibles
        boolean couleurNecessaireGare = false;
        int i = 0;
        int nbloc = Collections.frequency(this.cartesWagon, CouleurWagon.LOCOMOTIVE);
        while (!couleurNecessaireGare && i < CouleurWagon.getCouleursSimples().size()) {
            if (Collections.frequency(this.cartesWagon, CouleurWagon.getCouleursSimples().get(i)) + nbloc > 3 - this.nbGares) {
                couleurNecessaireGare = true;
            }
            i++;
        }
        if (this.nbGares == 0) {
            couleurNecessaireGare = false;
        }
        ArrayList<Ville> villes = new ArrayList<>(jeu.getVilles());
        ArrayList<String> villesString = new ArrayList<>();
        if (couleurNecessaireGare) {
            for (Ville ville : villes) {
                if (ville.getProprietaire() == null)
                    villesString.add(ville.getNom());
            }
            listeChoix.addAll(villesString);
        }


        //Liste des routes disponibles
        ArrayList<Route> routes = new ArrayList<>(jeu.getRoutes());
        ArrayList<String> routesString = new ArrayList<>();
        boolean routeValide = false;
        for (Route route : routes) {
            if (route.getProprietaire() == null) {
                if(route.getLongueur()<=this.nbWagons){
                    if (!route.getCouleur().equals(CouleurWagon.GRIS)) {
                        if (Collections.frequency(this.cartesWagon, route.getCouleur()) + Collections.frequency(this.cartesWagon, CouleurWagon.LOCOMOTIVE) >= route.getLongueur()) {
                            routeValide = true;
                        }
                    } else {
                        if (route instanceof Ferry) {
                            if (Collections.frequency(this.cartesWagon, CouleurWagon.LOCOMOTIVE) >= ((Ferry) route).getNbLocomotives()) {
                                i = 0;
                                while (!routeValide && i < CouleurWagon.getCouleursSimples().size()) {
                                    if (Collections.frequency(this.cartesWagon, CouleurWagon.getCouleursSimples().get(i)) + Collections.frequency(this.cartesWagon, CouleurWagon.LOCOMOTIVE) >= route.getLongueur()) {
                                        routeValide = true;
                                    }
                                    i++;
                                }
                            }
                        } else {
                            i = 0;
                            while (!routeValide && i < CouleurWagon.getCouleursSimples().size()) {
                                if (Collections.frequency(this.cartesWagon, CouleurWagon.getCouleursSimples().get(i)) + Collections.frequency(this.cartesWagon, CouleurWagon.LOCOMOTIVE) >= route.getLongueur()) {
                                    routeValide = true;
                                }
                                i++;
                            }
                        }
                    }
                }
            }
            if (routeValide) {
                routesString.add(route.getNom());
                routeValide = false;
            }
        }

        //Liste des cartes
        ArrayList<String> cartesString = new ArrayList<>();
        ArrayList<CouleurWagon> cartes = new ArrayList<>(jeu.getCartesWagonVisibles());
        if (jeu.getPileCartesWagon().size() > 0) {
            cartes.add(CouleurWagon.GRIS);
        }

        for (CouleurWagon carte : cartes) {
            cartesString.add(carte.name());
        }

        listeChoix.addAll(cartesString);
        listeChoix.addAll(routesString);

        if(!jeu.getPileDestinations().isEmpty()){
            listeChoix.add("destinations");
        }

        //Choix
        String choix = this.choisir(
                "Quelle action voulez vous réaliser: ",
                listeChoix,
                new ArrayList<>(),
                true);

        System.out.println(choix);
        //Test sur le choix

        if (villesString.contains(choix)) {
            //Boucle et tu verifies sur quelle villes c'est egal
            for (Ville ville : villes) {
                if (choix.equals(ville.getNom())) {
                    this.poserGare(ville);
                }
            }
        } else if (cartesString.contains(choix)) {
            int compteur = 0;
            for (CouleurWagon carte : cartes) {
                if (choix.equals(carte.name()) && compteur == 0) {
                    log(this.nom + " souhaite piocher des cartes.");
                    this.piocherWagons(carte);
                    log(this.nom + " a fini de piocher.");
                    compteur++;
                }
            }
        } else if (routesString.contains(choix)) {
            for (Route route : routes) {
                if (choix.equals(route.getNom())) {
                    this.poserWagon(route);
                }
            }
        } else if (choix.equals("destinations")) {
            ArrayList<Destination> destinationsPossible = new ArrayList<>();
            for (i = 0; i < 3; i++) {
                if (!jeu.getPileDestinations().isEmpty()) {
                    destinationsPossible.add(this.jeu.piocherDestination());
                }
            }
            log(this.nom + " souhaite piocher des destinations.");
            this.jeu.getPileDestinations().addAll(this.choisirDestinations(destinationsPossible, 1));
        }
    }

    public int getNbGares() {
        return nbGares;
    }

    public int getScore() {
        return this.score;
    }
}
