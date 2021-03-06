package serveur.interaction;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.potions.PotionDeplacement;
import serveur.element.potions.PotionInvincibilite;
import serveur.element.potions.PotionParalysie;
import serveur.element.potions.PotionPoison;
import serveur.element.potions.PotionTeleportation;
import serveur.vuelement.VuePersonnage;
import serveur.vuelement.VuePotion;
import utilitaires.Constantes;

/**
 * Represente le ramassage d'une potion par un personnage.
 *
 */
public class Ramassage extends Interaction<VuePotion> {

	/**
	 * Cree une interaction de ramassage.
	 * @param arene arene
	 * @param ramasseur personnage ramassant la potion
	 * @param potion potion a ramasser
	 */
	public Ramassage(Arene arene, VuePersonnage ramasseur, VuePotion potion) {
		super(arene, ramasseur, potion);
	}

	@Override
	public void interagit() {
		try {
			logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " essaye de rammasser " + 
					Constantes.nomRaccourciClient(defenseur));
			
			// si le personnage est vivant
			if(attaquant.getElement().estVivant()) {
				
				Element potion = defenseur.getElement();
				
				
				//On teste si c'est une potion a effet
				if (potion instanceof PotionDeplacement) {
					arene.deplacemenAccru(attaquant);
				}
				else if (potion instanceof PotionInvincibilite) {
					arene.invincibilite(attaquant);
				}
				else if (potion instanceof PotionTeleportation) {
					arene.teleport(attaquant.getRefRMI(), 0);
				}
				else if (potion instanceof PotionPoison) {
					arene.brulure(attaquant);
				}
				else if (potion instanceof PotionParalysie) {
					arene.paralysie(attaquant);
				}
				else { //sinon potion a caracteristique

					// caracteristiques de la potion
					HashMap<Caracteristique, Integer> valeursPotion = defenseur.getElement().getCaracts();
					
					for(Caracteristique c : valeursPotion.keySet()) {
						arene.incrementeCaractElement(attaquant, c, valeursPotion.get(c));
					}
				}
				
				logs(Level.INFO, "Potion bue !");
				
				// test si mort
				if(!attaquant.getElement().estVivant()) {
					arene.setPhrase(attaquant.getRefRMI(), "Je me suis empoisonne, je meurs ");
					logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " vient de boire un poison... Mort >_<");
				}

				// suppression de la potion
				arene.ejectePotion(defenseur.getRefRMI());
				
			} else {
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " ou " + 
						Constantes.nomRaccourciClient(defenseur) + " est deja mort... Rien ne se passe");
			}
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'un ramassage : " + e.toString());
		}
	}
}
