package client.controle.strategies;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.personnages.Brute;
import serveur.element.personnages.Personnage;
import serveur.element.potions.Potion;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class StrategieBrute implements IStrategie{
	
	/**
	 * Console permettant d'ajouter une phrase et de recuperer le serveur 
	 * (l'arene).
	 */
	protected Console console;
	
	protected int nbTours_paralysie = 0;
	
	protected boolean invincibiliteDeclenchee = false;
	protected boolean invincibiliteTerminee = false;

	/**
	 * Cree un personnage, la console associe et sa strategie.
	 * @param ipArene ip de communication avec l'arene
	 * @param port port de communication avec l'arene
	 * @param ipConsole ip de la console du personnage
	 * @param nom nom du personnage
	 * @param groupe groupe d'etudiants du personnage
	 * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position position initiale du personnage dans l'arene
	 * @param logger gestionnaire de log
	 */
	public StrategieBrute(String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger) {
		
		logger.info("Lanceur", "Creation de la console...");
		
		try {
			console = new Console(ipArene, port, ipConsole, this, 
					new Brute(nom, groupe, caracts), 
					nbTours, position, logger);
			logger.info("Lanceur", "Creation de la console reussie");
			
		} catch (Exception e) {
			logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
			e.printStackTrace();
		}
	}

	
	/** 
	 * Decrit la strategie.
	 * 
	 * La brute peut lancer une attaque paralysante une fois tous les 5 tours
	 * et lorsque sa vie descend a moins de 20 points, il devient invincible pendant NB_TOURS_INVINCIBILITE/2
	 *  
	 * Les methodes pour evoluer dans le jeu doivent etre les methodes RMI
	 * de Arene et de ConsolePersonnage. 
	 * @param voisins element voisins de cet element (elements qu'il voit)
	 * @throws RemoteException
	 */
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
		// arene
				IArene arene = console.getArene();
				
				// reference RMI de l'element courant
				int refRMI = 0;
				
				// position de l'element courant
				Point position = null;
				
				try {
					refRMI = console.getRefRMI();
					position = arene.getPosition(refRMI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				//si vie inferieure a 20, declenchement de l'invincibilite
				if( !(this.invincibiliteDeclenchee) && console.getPersonnage().getCaract(Caracteristique.VIE) <= 20){
					console.setPhrase("Je declenche mon invincibilite");
					arene.invincibilite((VuePersonnage)arene.vueFromRef(refRMI));
					this.invincibiliteDeclenchee = true;
				}
				
				
				if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
					console.setPhrase("J'erre...");
					arene.deplace(refRMI, 0, console.getPersonnage().getCaract(Caracteristique.DEPLACEMENT)); 
					
				} else {
					int refCible = Calculs.chercheElementProche(position, voisins);
					int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

					Element elemPlusProche = arene.elementFromRef(refCible);

					if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
																				// j'interagis directement
						if(elemPlusProche instanceof Potion) { // potion
							// ramassage
							console.setPhrase("Je ramasse une potion");
							arene.ramassePotion(refRMI, refCible);

						} else { // personnage
							if (this.nbTours_paralysie == 0) {
								this.nbTours_paralysie = 1;
								console.setPhrase("Je paralyse " + elemPlusProche.getNom());
								arene.lanceAttaqueParalysante(refRMI, refCible);//attaque paralysante
							}
							else {
								// duel
								console.setPhrase("Je marave " + elemPlusProche.getNom());
								arene.lanceAttaque(refRMI, refCible, true);
							}
						}
					} else if (this.nbTours_paralysie == 0) {
						if (elemPlusProche instanceof Personnage) {
							this.nbTours_paralysie = 1;
							console.setPhrase("Je paralyse " + elemPlusProche.getNom());
							arene.lanceAttaqueParalysante(refRMI, refCible);//attaque paralysante
						}	
					} else { // si voisins, mais plus eloignes
						// je vais vers le plus proche
						console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
						arene.deplace(refRMI, refCible, console.getPersonnage().getCaract(Caracteristique.DEPLACEMENT));
					}
				}
				if (this.nbTours_paralysie > 0) {
					if (this.nbTours_paralysie == 5) {
						this.nbTours_paralysie = 0;
						console.setPhrase("Je peux paralyser a nouveau");
					}
					else this.nbTours_paralysie++;
				}
				if(this.invincibiliteDeclenchee && !this.invincibiliteTerminee)
				{
					Personnage p = (Personnage)arene.elementFromRef(refRMI);
					if(p.getNbToursInvincibilite()==Constantes.NB_TOURS_INVINCIBILITE/2)
						p.setNbToursInvincibilite(Constantes.NB_TOURS_INVINCIBILITE);
				}
				arene.subirBrulure(refRMI);
				arene.subirParalysie(refRMI);
				arene.subirInvincibilite(refRMI);
				arene.subirDeplacementAccru(refRMI);
	}

}
