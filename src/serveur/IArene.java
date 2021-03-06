package serveur;

import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.controle.IConsole;
import serveur.element.Element;
import serveur.element.personnages.Personnage;
import serveur.element.potions.Potion;
import serveur.vuelement.VueElement;
import serveur.vuelement.VuePersonnage;

/**
 * Definit les methodes qui pourront s'appliquer a l'arene par le reseau.
 */
public interface IArene extends Remote {	
	
	/**************************************************************************
	 * Connexion et deconnexion, partie non commencee ou finie. 
	 **************************************************************************/
	
	/**
	 * Retourne une reference RMI libre pour un element.
	 * @return reference RMI inutilisee
	 */
	public int alloueRefRMI() throws RemoteException;
	
	/**
	 * Connecte un personnage a l'arene.
	 * @param refRMI reference RMI de l'element a connecter
	 * @param ipConsole ip de la console correspondant au personnage
	 * @param personnage personnage
	 * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position position courante
	 * @return vrai si l'element a ete connecte, faux sinon
	 * @throws RemoteException
	 */
	public boolean connecte(int refRMI, String ipConsole, 
			Personnage personnage, int nbTours, Point position) throws RemoteException;
	
	/**
	 * Deconnecte un element du serveur.
	 * @param refRMI reference RMI correspondant au personnage a deconnecter
	 * @param cause cause de la deconnexion
	 * @param phrase phrase a ecrire sur l'interface
	 * @throws RemoteException
	 */
	public void deconnecte(int refRMI, String cause, String phrase) throws RemoteException;

	/**
	 * Teste si la partie est finie.
	 * @return true si la partie est finie, false sinon
	 * @throws RemoteException
	 */
	public boolean estPartieFinie() throws RemoteException;

	/**
	 * Teste si la partie a commence.
	 * @return vrai si la partie a commence, faux sinon
	 * @throws RemoteException
	 */
	boolean estPartieCommencee() throws RemoteException;

	/**
	 * Ajoute une potion dans l'arene a n'importe quel moment en mode arene 
	 * libre.
	 * @param potion potion
	 * @param position position de la potion
	 * @throws RemoteException
	 */
	public void ajoutePotion(Potion potion, Point position) throws RemoteException;
	
	

	/**************************************************************************
	 * Accesseurs sur les elements du serveur. 
	 **************************************************************************/
	/**
	 * Permet de connaitre le nombre de tours restants
	 * @return nombre de tours restant
	 * @throws RemoteException
	 */
	public int getNbToursRestants() throws RemoteException ;

	/**
	 * Permet de savoir le nombre de tours ecoules
	 * @return nombre de tour ecoules
	 * @throws RemoteException
	 */
	public int getTour() throws RemoteException;

	/**
	 * Calcule la liste les voisins d'un element represente par sa reference
	 * RMI.
	 * @param refRMI reference de l'element dont on veut recuperer les voisins
	 * @return map des couples reference/coordonnees des voisins
	 * @throws RemoteException
	 */
	public HashMap<Integer, Point> getVoisins(int refRMI) throws RemoteException;

	/**
	 * Permet de recuperer une copie de l'element correspondant a la reference 
	 * RMI.
	 * @param refRMI reference RMI
	 * @return copie de l'element correspondant a la reference RMI donnee
	 * @throws RemoteException
	 */
	public Element elementFromRef(int refRMI) throws RemoteException;

	/**
	 * Permet de recuperer une copie de l'element correspondant a la console.
	 * @param console console
	 * @return copie de l'element correspondant a la console donnee
	 * @throws RemoteException
	 */
	public Element elementFromConsole(IConsole console) throws RemoteException;
	
	/**
	 * Renvoie la vue correspondant a la reference RMI donnee.
	 * @param refRMI reference RMI
	 * @return vue correspondante
	 */
	public VueElement<?> vueFromRef(int refRMI) throws RemoteException;
	
	/**
	 * Renvoie la vue correspondant a la console donnee.
	 * @param console console
	 * @return vue correspondante
	 * @throws RemoteException
	 */
	public VueElement<?> vueFromConsole(IConsole console) throws RemoteException;

	/**
	 * Permet de savoir la position d'un element
	 * @param refRMI reference de l'element
	 * @return position de l'element
	 * @throws RemoteException
	 */
	public Point getPosition(int refRMI) throws RemoteException;

	/**
	 * Modifie la phrase du personnage correspondant a la console donnee.
	 * @param refRMI reference RMI du personnage dont on doit modifier la phrase
	 * @param s nouvelle phrase
	 * @throws RemoteException
	 */
	public void setPhrase(int refRMI, String s) throws RemoteException;
	

	/**************************************************************************
	 * Gestion des interactions.
	 **************************************************************************/

	/**
	 * Execute le ramassage d'une potion par un personnage, si possible.
	 * Le ramassage echoue si une action a deja ete executee ce tour par ce 
	 * personnage, ou si la potion est trop loin du personnage.
	 * @param refRMI reference RMI du personnage voulant ramasser une potion
	 * @param refPotion reference RMI de la potion qui doit etre ramasse
	 * @return vrai si l'action a ete effectuee, faux sinon
	 * @throws RemoteException
	 */
	public boolean ramassePotion(int refRMI, int refPotion) throws RemoteException;
	
	/**
	 * Execute un duel entre le personnage correspondant a la console donnee 
	 * et l'adversaire correspondant a la reference RMI donnee.
	 * Le duel echoue si une action a deja ete executee a ce tour par 
	 * l'attaquant, si les personnages sont trop eloignes, si l'un des deux 
	 * n'est plus actif (mort)
	 * @param refRMI reference RMI de l'attaquant, qui demande un duel
	 * @param refAdv reference RMI du defenseur
	 * @param avecDef true pour prendre en compte la defense, false sinon
	 * @return vrai si l'action a bien eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean lanceAttaque(int refRMI, int refAdv, boolean avecDef) throws RemoteException;
	
	/**
	 * Lance une attaque paralysante qui immobilise NB_TOURS_PARALYSIE tours
	 * @param refRMI reference RMI de l'attaquant, qui demande un duel
	 * @param refRMIAdv reference RMI du defenseur
	 * @return vrai si l'action a bien eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean lanceAttaqueParalysante(int refRMI, int refRMIAdv) throws RemoteException;
	
	/**
	 * Lance une attaque brulante qui brule NB_TOURS_BRULURE tours en plus des degats
	 * @param refRMI reference RMI de l'attaquant, qui demande un duel
	 * @param refRMIAdv reference RMI du defenseur
	 * @return vrai si l'action a bien eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean lanceAttaqueBrulante(int refRMI, int refRMIAdv) throws RemoteException;
	
	/**
	 * Execute un duel a distance entre le personnage correspondant a la console donnee 
	 * et l'adversaire correspondant a la reference RMI donnee.
	 * Le duel echoue si une action a deja ete executee a ce tour par 
	 * l'attaquant, si les personnages sont trop eloignes, si l'un des deux 
	 * n'est plus actif (mort)
	 * @param refRMI reference RMI de l'attaquant, qui demande un duel
	 * @param refAdv reference RMI du defenseur
	 * @param avecDef true pour prendre en compte la defense, false sinon
	 * @return vrai si l'action a bien eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean lanceAttaqueADist(int refRMI, int refRMIAdv, boolean avecDef) throws RemoteException;
	
	/**
	 * Deplace le personnage correspondant a la console donne vers l'element 
	 * correspondant a la reference RMI cible.
	 * Le deplacement echoue si une action a deja ete executee a ce tour par 
	 * ce personnage.
	 * @param refRMI reference RMI du personnage voulant se deplacer
	 * @param refCible reference RMI de l'element vers lequel on veut se 
	 * deplacer, ou 0 si on veut se deplacer aleatoirement
	 * @return vrai si l'action a bien eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean deplace(int refRMI, int refCible, int distance) throws RemoteException;
	
	/**
	 * Deplace le personnage correspondant a la console donne vers le point 
	 * cible.
	 * Le deplacement echoue si une action a deja ete executee a ce tour par 
	 * ce personnage.
	 * @param refRMI reference RMI du personnage voulant se deplacer
	 * @param objectif point vers lequel on veut se deplacer
	 * @return vrai si l'action a bien eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean deplace(int refRMI, Point objectif, int distance) throws RemoteException;
	
	/**
	 * Ajoute une potion dans l'arene a n'importe quel moment en mode tournoi.
	 * @param potion potion
	 * @param position position de la potion
	 * @param motDePasse mot de passe administrateur
	 * @throws RemoteException
	 */
	public void lancePotion(Potion potion, Point position, String motDePasse) throws RemoteException;
	
	/**
	 * Rafraichit le nombre de tour en deplacement accru pour le personnage reference par refRMI
	 * @param refRMI La reference RMI du personnage
	 * @throws RemoteException
	 */
	void subirDeplacementAccru(int refRMI) throws RemoteException;

	/**
	 * Rafraichit le nombre de tour en invincibilite pour le personnage reference par refRMI
	 * @param refRMI La reference RMI du personnage
	 * @throws RemoteException
	 */
	void subirInvincibilite(int refRMI) throws RemoteException;

	/**
	 * Rafraichit le nombre de tour en paralysie pour le personnage reference par refRMI
	 * @param refRMI La reference RMI du personnage
	 * @throws RemoteException
	 */
	void subirParalysie(int refRMI) throws RemoteException;
	
	/**
	 * Rafraichit le nombre de tour en brulure pour le personnage reference par refRMI
	 * @param refRMI La reference RMI du personnage
	 * @throws RemoteException
	 */
	void subirBrulure(int refRMI) throws RemoteException;
	
	/**
	 * teleporte le personnage vers un point de l'arene
	 * @param refRMI La reference RMI du personnage
	 * @param objectif La coordonnee du point de destination dans l'arene
	 * @throws RemoteException
	 */
	public boolean teleport(int refRMI, Point objectif) throws RemoteException;

	/**
	 * teleporte le personnage vers un autre element
	 * @param refRMI La reference RMI du personnage
	 * @param refCible la reference RMI de l'element sur lequel le personnage va se teleporter
	 * @throws RemoteException
	 */
	public boolean teleport(int refRMI, int refCible) throws RemoteException;

	/**
	 * lance l'effet de brulure sur le personnage reference par vuePersonnage
	 * @param vuePersonnage la vue du personnage qui va subir la brulure
	 * @throws RemoteException
	 */
	void brulure(VuePersonnage vuePersonnage) throws RemoteException;

	/**
	 * lance l'effet de paralysie sur le personnage reference par vuePersonnage
	 * @param vuePersonnage la vue du personnage qui va subir la paralysie
	 * @throws refCible la reference RMI de l'element sur lequel le personnage va se teleporter
	 */
	void paralysie(VuePersonnage vuePersonnage) throws RemoteException;

	/**
	 * lance l'effet d'invincibilite sur le personnage reference par vuePersonnage
	 * @param vuePersonnage la vue du personnage qui va devenir invincible
	 * @throws RemoteException
	 */
	void invincibilite(VuePersonnage vuePersonnage) throws RemoteException;
	
	/**
	 * lance l'effet de deplacement accru sur le personnage reference par vuePersonnage
	 * @param vuePersonnage la vue du personnage dont le deplacement va augmenter
	 * @throws RemoteException
	 */
	void deplacemenAccru(VuePersonnage vuePersonnage) throws RemoteException;

	/**
	 * definit si le personnage reference par vueP a effectue une action ou non pour ce tour
	 * @param vueP la vue du personnage concerne
	 * @param b action effectuee ou non
	 */
	void setActionExecutee(VuePersonnage vueP, boolean b) throws RemoteException;
	

	

	/**************************************************************************
	 * Specifique au tournoi.
	 **************************************************************************/
	
	/**
	 * Verifie le mot de passe administrateur. 
	 * @param motDePasse mot de passe a verifier
	 * @return true si le mot de passe est ok, false sinon
	 * @throws RemoteException
	 */
	public boolean verifieMotDePasse(char[] motDePasse) throws RemoteException;

	/**
	 * Lance la partie.
	 * @param motDePasse mot de passe administrateur
	 * @throws RemoteException
	 */
	public void commencePartie(String motDePasse) throws RemoteException;

	/**
	 * Ejecte un joueur de la partie. 
	 * @param refRMI personnage
	 * @param motDePasse mot de passe administrateur
	 * @throws RemoteException
	 */
	public void ejectePersonnage(int refRMI, String motDePasse) throws RemoteException;

}

