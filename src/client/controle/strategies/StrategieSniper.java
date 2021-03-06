package client.controle.strategies;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.personnages.Personnage;
import serveur.element.personnages.Sniper;
import serveur.element.potions.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class StrategieSniper implements IStrategie{
	
	/**
	 * Console permettant d'ajouter une phrase et de recuperer le serveur 
	 * (l'arene).
	 */
	protected Console console;
	
	protected int nbTours_snipe = 0; //compteur capacite speciale snipe

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
	public StrategieSniper(String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger) {
		
		logger.info("Lanceur", "Creation de la console...");
		
		try {
			console = new Console(ipArene, port, ipConsole, this, 
					new Sniper(nom, groupe, caracts), 
					nbTours, position, logger);
			logger.info("Lanceur", "Creation de la console reussie");
			
		} catch (Exception e) {
			logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
			e.printStackTrace();
		}
	}

	
	/** 
	 * Peut sniper (attaque a distance qui prend pas en compte la def) dans le champ de vision tous les 5 tours
	 * Attaque au corps a corps (duel) sinon
	 * 
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
		
		if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
			console.setPhrase("J'erre...");
			arene.deplace(refRMI, 0, console.getPersonnage().getCaract(Caracteristique.DEPLACEMENT)); 
			
		} else {				//s'il y a des voisins
			int refCible = Calculs.chercheElementProche(position, voisins);
			int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

			Element elemPlusProche = arene.elementFromRef(refCible);

			if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches pour un duel
				// j'interagis directement
				if(elemPlusProche instanceof Potion) { // potion
														// ramassage
					console.setPhrase("Je ramasse une potion");
					arene.ramassePotion(refRMI, refCible);

				} else { 								// personnage
					if (this.nbTours_snipe == 0) {		// si le sniper est charge
						this.nbTours_snipe = 1;
						console.setPhrase("Je snipe " + elemPlusProche.getNom());
						arene.lanceAttaqueADist(refRMI, refCible, false);//attaque transpercante
					}
					else {								//sinon attaque au couteau (duel)
						// duel
						console.setPhrase("Je fais un duel au couteau avec " + elemPlusProche.getNom());
						arene.lanceAttaque(refRMI, refCible, true);
					}
				}
			} else if (this.nbTours_snipe == 0 && elemPlusProche instanceof Personnage) {
				this.nbTours_snipe = 1;							//si le sniper est charge
				console.setPhrase("Je snipe " + elemPlusProche.getNom());
				arene.lanceAttaqueADist(refRMI, refCible, false);//attaque transpercante
					
			} else { // si voisins, mais plus eloignes (et que sniper pas charge)
				// je vais vers le plus proche
				console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
				arene.deplace(refRMI, refCible, console.getPersonnage().getCaract(Caracteristique.DEPLACEMENT));
			}
		}
		if (this.nbTours_snipe > 0) {			//compter recharge sniper
			if (this.nbTours_snipe == 5) {
				this.nbTours_snipe = 0;
				console.setPhrase("Je peux sniper a nouveau");
			}
			else this.nbTours_snipe++;
		}
		arene.subirBrulure(refRMI);
		arene.subirParalysie(refRMI);
		arene.subirInvincibilite(refRMI);
		arene.subirDeplacementAccru(refRMI);
	}

}
