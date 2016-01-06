package serveur.interaction;

import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.personnages.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Constantes;

/**
 * Represente un duel entre deux personnages.
 *
 */
public class DuelBrulant extends Interaction<VuePersonnage> {
	
	/**
	 * Cree une interaction de duel avec un effet de brulure de NB_TOURS_BRULURE tours.
	 * @param arene arene
	 * @param attaquant attaquant
	 * @param defenseur defenseur
	 */
	public DuelBrulant(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
	}
	
	
	@Override
	//TODO
	/**
	 * 
	 * 
	 */
	public void interagit() {
		try {
			Personnage pAttaquant = attaquant.getElement();
			int forceAttaquant = (int)(pAttaquant.getCaract(Caracteristique.FORCE)*0.5);// 1/2 d'att
			Personnage pDefenseur = defenseur.getElement();
			int defenseDefenseur = pDefenseur.getCaract(Caracteristique.DEFENSE);
			int perteVie = forceAttaquant * (100 - defenseDefenseur) / 100;
		

			// degats
			if (perteVie >= 0) {
				arene.incrementeCaractElement(defenseur, Caracteristique.VIE, -perteVie);
				
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " enflamme ("
						+ perteVie + " points de degats) a " + Constantes.nomRaccourciClient(defenseur));
			}
			//brulure
			brulure(defenseur);
			// initiative
			incrementeInitiative(defenseur);
			decrementeInitiative(attaquant);
			
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'une attaque : " + e.toString());
		}
	}
		
	
	//TODO
	/**
	 * 
	 * @param defenseur
	 * @throws RemoteException
	 */
	
	private void brulure(VuePersonnage defenseur) throws RemoteException {
		arene.brulure(defenseur);
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