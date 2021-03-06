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
import utilitaires.Calculs;
import utilitaires.Constantes;

public class StrategieBrute implements IStrategie{
	
	/**
	 * Console permettant d'ajouter une phrase et de recuperer le serveur 
	 * (l'arene).
	 */
	protected Console console;
	
	protected int nbTours_paralysie = 0;		//pour le compteur de l'attaque paralysante
	

	/**
	 * 
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
	 * La brute tape FORT ! Tellement fort qu'elle paralyse son adversaire une fois sur 5
	 * elle se deplace de 1 et elle a 90 de DEF
	 * son attaque paralysante a une portee de DIST_MIN_INTERACTION+1
	 * et l'attaque normale a une portee de DIST_MIN_INTERACTION.
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
					
				} else {
					int refCible = Calculs.chercheElementProche(position, voisins);
					int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

					Element elemPlusProche = arene.elementFromRef(refCible);
					//si un elem est a portee de l'att paralysante, que l'att est disponible, et que c'est un personnage
					if (this.nbTours_paralysie == 0 && distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION + 1
							&& elemPlusProche instanceof Personnage ){
						this.nbTours_paralysie = 1;
						console.setPhrase("Je paralyse " + elemPlusProche.getNom());
						arene.lanceAttaqueParalysante(refRMI, refCible);//attaque paralysante
					}
					else if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
																				// j'interagis directement
						if(elemPlusProche instanceof Potion) { // potion
							// ramassage
							console.setPhrase("Je ramasse une potion");
							arene.ramassePotion(refRMI, refCible);

						} else { // personnage
							
							// duel
							console.setPhrase("Je marave " + elemPlusProche.getNom());
							arene.lanceAttaque(refRMI, refCible, true);

						}	
					} else { // si voisins, mais plus eloignes
						// je vais vers le plus proche
						console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
						arene.deplace(refRMI, refCible, console.getPersonnage().getCaract(Caracteristique.DEPLACEMENT));
					}
				}
				if (this.nbTours_paralysie > 0) {			//Compteur attaque paralysante
					if (this.nbTours_paralysie == 5) {
						this.nbTours_paralysie = 0;
						console.setPhrase("Je peux paralyser a nouveau");
					}
					else this.nbTours_paralysie++;
				}
				
				arene.subirBrulure(refRMI);
				arene.subirParalysie(refRMI);
				arene.subirInvincibilite(refRMI);
				arene.subirDeplacementAccru(refRMI);
	}

}
