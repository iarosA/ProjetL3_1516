package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.personnages.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Constantes;

public class DuelParalysant extends Duel {
	
	/**
	 * Cree une interaction de duel Paralysant pendant NB_TOURS_PARALYSIE tours.
	 * les degats infliges a l'execution seront calcules de la maniere habituelle avec prise en compte de la DEFENSE
	 * Avec projection de l'adversaire.
	 * 
	 * @param arene arene
	 * @param attaquant attaquant
	 * @param defenseur defenseur
	 */
	public DuelParalysant(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur, true);
	}
	
	@Override
	public void interagit() {
		try {
			Personnage pAttaquant = attaquant.getElement();						//init
			int forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE);
			Personnage pDefenseur = defenseur.getElement();
			int defenseDefenseur = pDefenseur.getCaract(Caracteristique.DEFENSE); //on prend en compte la Defense
			int perteVie = forceAttaquant * (100 - defenseDefenseur) / 100;
		
			Point positionEjection = positionEjection(defenseur.getPosition(), attaquant.getPosition(), forceAttaquant);

			// ejection du defenseur
			defenseur.setPosition(positionEjection);

			// degats
			if (perteVie > 0) {
				arene.incrementeCaractElement(defenseur, Caracteristique.VIE, -perteVie);
				
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " colle une beigne paralysante ("
						+ perteVie + " points de degats) a " + Constantes.nomRaccourciClient(defenseur));
			}
			//paralysie
			paralysie(defenseur);
			// initiative
			incrementeInitiative(defenseur);
			decrementeInitiative(attaquant);
			
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'une attaque : " + e.toString());
		}
	}
	
	private void paralysie(VuePersonnage defenseur) throws RemoteException {
		arene.paralysie(defenseur);
	}
}
