package serveur.interaction;

import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.personnages.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Constantes;

public class DuelADistance extends Interaction<VuePersonnage>{
	
	
	private boolean avecDef;
	/**
	 * Cree une interaction de duel.
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
			Personnage pAttaquant = attaquant.getElement();
			Personnage pDefenseur = defenseur.getElement();
			int forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE);
			int perteVie;
			int defenseDefenseur = pDefenseur.getCaract(Caracteristique.DEFENSE);
			if (avecDef) {
				perteVie = forceAttaquant * (100 - defenseDefenseur) / 100;
			}
			else {
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
				
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " dégomme à distance ("
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
