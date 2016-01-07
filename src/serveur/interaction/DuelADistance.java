package serveur.interaction;

import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.personnages.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Constantes;

public class DuelADistance extends Interaction<VuePersonnage>{
	
	
	private boolean avecDef;			//booleen: si true on prend en compte l'effet de la defense de l'adversaire
	/**
	 * Cree une interaction de duel a distance
	 * @param arene arene
	 * @param attaquant attaquant
	 * @param defenseur defenseur
	 * @param def true-> prends en compte defense, false-> non
	 */
	
	public DuelADistance(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur, boolean def) {
		super(arene, attaquant, defenseur);
		this.avecDef = def;
	}
	
	@Override
	public void interagit() {
		try {
			Personnage pAttaquant = attaquant.getElement();				//init
			Personnage pDefenseur = defenseur.getElement();
			int forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE);
			int perteVie;
			int defenseDefenseur = pDefenseur.getCaract(Caracteristique.DEFENSE);
			if (avecDef) {							//si on veut prendre en compte la defense
				perteVie = forceAttaquant * (100 - defenseDefenseur) / 100;
			}
			else {									//s'il est immunise pas de perte de vie
				if(defenseDefenseur == 100) {
					perteVie = 0;
				}
				else {
				perteVie = forceAttaquant;
				}
			}
		
			
			// degats
			if (perteVie > 0) {
				arene.incrementeCaractElement(defenseur, Caracteristique.VIE, -perteVie);
				
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " degomme a distance ("
						+ perteVie + " points de degats) a " + Constantes.nomRaccourciClient(defenseur));
			}
			
			// initiative
			incrementeInitiative(defenseur);
			decrementeInitiative(attaquant);
			
			
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'une attaque : " + e.toString());
		}
		
	}

	/**
	 * Incremente l'initiative du defenseur en cas de succes de l'attaque. 
	 * @param defenseur defenseur
	 * @throws RemoteException
	 */
	private void incrementeInitiative(VuePersonnage defenseur) throws RemoteException {
		arene.incrementeCaractElement(defenseur, Caracteristique.INITIATIVE, 
				Constantes.INCR_DECR_INITIATIVE_DUEL);
	}
	
	/**
	 * Decremente l'initiative de l'attaquant en cas de succes de l'attaque. 
	 * @param attaquant attaquant
	 * @throws RemoteException
	 */
	private void decrementeInitiative(VuePersonnage attaquant) throws RemoteException {
		arene.incrementeCaractElement(attaquant, Caracteristique.INITIATIVE, 
				-Constantes.INCR_DECR_INITIATIVE_DUEL);
	}
	
}
