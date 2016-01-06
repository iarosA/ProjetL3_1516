package client.controle.strategies;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.personnages.Assassin;
import serveur.element.personnages.Personnage;
import serveur.element.potions.Potion;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;


public class StrategieAssassin implements IStrategie{
	
	/**
	 * Console permettant d'ajouter une phrase et de recuperer le serveur 
	 * (l'arene).
	 */
	protected Console console;
	protected int nbTours_Teleport=0;
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
	public StrategieAssassin(String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger) {
		
		logger.info("Lanceur", "Creation de la console...");
		
		try {
			console = new Console(ipArene, port, ipConsole, this, 
					new Assassin(nom, groupe, caracts), 
					nbTours, position, logger);
			logger.info("Lanceur", "Creation de la console reussie");
			
		} catch (Exception e) {
			logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
			e.printStackTrace();
		}
	}
	
	/** L'assassin peut se teleporter DANS LE DOS de ses adversaires une fois tous les 5 tours et les attaquer
	 * sans prendre en compte leur defense, sinon il avance de 1 et l'attaque prend en compte la Defense.
	 * 
	 * Decrit la strategie.
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
			
		} else {
			int refCible = Calculs.chercheElementProche(position, voisins);
			int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

			Element elemPlusProche = arene.elementFromRef(refCible);

			if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION ) { // si suffisamment proches
																		// j'interagis directement
				if(elemPlusProche instanceof Potion) { // potion
														// ramassage
					console.setPhrase("Je ramasse une potion");
					arene.ramassePotion(refRMI, refCible);

				} else { 												// personnage
																		// duel
						console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
						arene.lanceAttaque(refRMI, refCible, true);
						}
				
			} else { // si voisins, mais plus eloignes
					// je vais vers le plus proche
				if(nbTours_Teleport==0 && elemPlusProche instanceof Personnage)
				{
									//lorque l'adversaire est hors de portée et que la 
									//teleportation a fini de charger, l'assassin se teleporte
									//et effectue une attaque simultanement ne prenant pas en compte la defense
									//de l'adversaire.
					console.setPhrase("Je me deplace furtivement dans le dos de" + elemPlusProche.getNom());
					arene.teleport(refRMI, refCible);
					this.nbTours_Teleport = 1;
					VuePersonnage moi = (VuePersonnage)arene.vueFromRef(refRMI);
					arene.setActionExecutee(moi, false);
					console.setPhrase("J'attaque furtivement " + elemPlusProche.getNom());
					arene.lanceAttaque(refRMI, refCible, false);
				}
				else
				{
					console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
					arene.deplace(refRMI, refCible, console.getPersonnage().getCaract(Caracteristique.DEPLACEMENT));
				}
				
			}
		}
		if(nbTours_Teleport > 0)			//gestion du compteur de teleportation
		{									//il s'incremente au fil des tours
			if(nbTours_Teleport==5)			//lorsqu'il est a 0 il le reste
				nbTours_Teleport = 0;
			else
				nbTours_Teleport++;
		}
		arene.subirBrulure(refRMI);
		arene.subirParalysie(refRMI);
		arene.subirInvincibilite(refRMI);
		arene.subirDeplacementAccru(refRMI);
	}

}
