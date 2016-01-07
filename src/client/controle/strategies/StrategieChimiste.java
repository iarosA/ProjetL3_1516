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
	 * Le chimiste n'est pas un personnage a part entiere, on le lance dans l'arene avec peu de vie, pas de defense
	 * il a la capacite de teleporter, et il pose une potion a chaque fois qu'il se teleporte.
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
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		arene.teleport(refRMI, 0);									//il se teleporte
		console.setPhrase("Je me teleporte");
		
		int choixPotion = Calculs.nombreAleatoire(1, 3);			//choisit une potion
		switch (choixPotion) {
		case 1: console.setPhrase("Je depose du poison");
				break;
		case 2: console.setPhrase("Je depose une potion de paralysie");
				break;
		case 3: console.setPhrase("Je depose une potion de teleportation");
				break;
		}
		
		VuePersonnage moi = (VuePersonnage) arene.vueFromRef(refRMI);
		moi.executeAction();
	
		arene.subirBrulure(refRMI);
		arene.subirParalysie(refRMI);
		arene.subirInvincibilite(refRMI);
		arene.subirDeplacementAccru(refRMI);
		
		position = arene.getPosition(refRMI);
		
		new ThreadChimiste(arene, choixPotion, position);			//et la pose
	}

	private class ThreadChimiste extends Thread {
		private IArene arene;
		private int choixPotion;
		private Point position;
		
		public ThreadChimiste(IArene arene, int choixPotion, Point pos) {
			super();
			this.setDaemon(true);
			this.arene = arene;
			this.choixPotion = choixPotion;
			this.position = pos;
			start();
		}
		
		public void run() {
			
			try {
				switch(choixPotion){
				case 1:
					arene.ajoutePotion(new PotionPoison("Arsenic", "Chimiste", new HashMap<Caracteristique, Integer>()), position);
					break;
				case 2:
					arene.ajoutePotion(new PotionParalysie("Gel", "Chimiste", new HashMap<Caracteristique, Integer>()), position);
					break;
				case 3:
					arene.ajoutePotion(new PotionTeleportation("Vortex", "Chimiste", new HashMap<Caracteristique, Integer>()), position);
					break;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
