package client.controle.strategies;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.personnages.Chimiste;
import serveur.element.potions.PotionParalysie;
import serveur.element.potions.PotionPoison;
import serveur.element.potions.PotionTeleportation;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;

public class StrategieChimiste implements IStrategie{
	
	/**
	 * Console permettant d'ajouter une phrase et de recuperer le serveur 
	 * (l'arene).
	 */
	protected Console console;

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
	public StrategieChimiste(String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger) {
		
		logger.info("Lanceur", "Creation de la console...");
		
		try {
			console = new Console(ipArene, port, ipConsole, this, 
					new Chimiste(nom, groupe, caracts), 
					nbTours, position, logger);
			logger.info("Lanceur", "Creation de la console reussie");
			
		} catch (Exception e) {
			logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
			e.printStackTrace();
		}
	}

	/** 
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
		
		arene.teleport(refRMI, 0);
		console.setPhrase("Je me teleporte");
		
		VuePersonnage moi = (VuePersonnage) arene.vueFromRef(refRMI);
		moi.executeAction();
		
		int choixPotion = Calculs.nombreAleatoire(1, 3);
		
		switch(choixPotion){
		case 1:
			console.setPhrase("Je depose du poison");
			arene.ajoutePotion(new PotionPoison("Poison", "G13", null), position);
			break;
		case 2:
			console.setPhrase("Je depose une potion de paralysie");
			arene.ajoutePotion(new PotionParalysie("Potion de Paralysie", "G13", null), position);
			break;
		case 3:
			console.setPhrase("Je depose une potion de teleportation");
			arene.ajoutePotion(new PotionTeleportation("Potion de Teleportation", "G13", null), position);
			break;
		}
		arene.subirBrulure(refRMI);
		arene.subirParalysie(refRMI);
		arene.subirInvincibilite(refRMI);
		arene.subirDeplacementAccru(refRMI);
	}

}
