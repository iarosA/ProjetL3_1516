package client.controle.strategies;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.personnages.Grenadier;
import serveur.element.personnages.Personnage;
import serveur.element.potions.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class StrategieGrenadier implements IStrategie{
	
	/**
	 * Console permettant d'ajouter une phrase et de recuperer le serveur 
	 * (l'arene).
	 */
	protected Console console;

	/**
	 * Cree un personnage, la console associe et sa strategie.
	 * 
	 * 
	 * @param ipArene ip de communication avec l'arene
	 * @param port port de communication avec l'arene
	 * @param ipConsole ip de la console du personnage
	 * @param nom nom du personnage
	 * @param groupe groupe d'etudiants du personnage
	 * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position position initiale du personnage dans l'arene
	 * @param logger gestionnaire de log
	 */
	public StrategieGrenadier(String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger) {
		
		logger.info("Lanceur", "Creation de la console...");
		
		try {
			console = new Console(ipArene, port, ipConsole, this, 
					new Grenadier(nom, groupe, caracts), 
					nbTours, position, logger);
			logger.info("Lanceur", "Creation de la console reussie");
			
		} catch (Exception e) {
			logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
			e.printStackTrace();
		}
	}

	
	/** 
	 * Le grenadier attaque au lance flamme lorsque l'adversaire est a moins de DISTANCE_MIN_INTERACTION * 3
	 * si le personnage est a moins de DISTANCE_MIN_INTERACTION * 5, il lance une attaque a distance.
	 * 
	 * Les methodes pour evoluer dans le jeu doivent etre les methodes RMI
	 * de Arene et de ConsolePersonnage. 
	 *
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
			
		} else {
			int refCible = Calculs.chercheElementProche(position, voisins);
			int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

			Element elemPlusProche = arene.elementFromRef(refCible);
			
			//si le grenadier est a proximite d'une potion, il la ramasse
			if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
				// j'interagis directement
				if(elemPlusProche instanceof Potion) { // potion
					// ramassage
					console.setPhrase("Je ramasse une potion");
					arene.ramassePotion(refRMI, refCible);

				}
				
			//si le grenadier est a 3 fois la distance minimale d'interaction, il lance une attaque brulante
			} 
			else if (distPlusProche <= 3*Constantes.DISTANCE_MIN_INTERACTION && elemPlusProche instanceof Personnage) { // personnage
				// duel
				console.setPhrase("Je brule " + elemPlusProche.getNom());
				arene.lanceAttaqueBrulante(refRMI, refCible);
				
			}
			//sinon, si le grenadier est a 5 fois la distance minimale d'interaction, il lance une attaque a distance
			else if(distPlusProche <= 5*Constantes.DISTANCE_MIN_INTERACTION && elemPlusProche instanceof Personnage) { // personnage
				// duel
				console.setPhrase("J'attaque " + elemPlusProche.getNom()+" a distance");
				arene.lanceAttaqueADist(refRMI, refCible, true);
				
			//sinon, il se deplace vers un voisin
			} else { // si voisins, mais plus eloignes
				// je vais vers le plus proche
				console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
				arene.deplace(refRMI, refCible, console.getPersonnage().getCaract(Caracteristique.DEPLACEMENT));
			}
		}
		arene.subirBrulure(refRMI);
		arene.subirParalysie(refRMI);
		arene.subirInvincibilite(refRMI);
		arene.subirDeplacementAccru(refRMI);
	}

}
