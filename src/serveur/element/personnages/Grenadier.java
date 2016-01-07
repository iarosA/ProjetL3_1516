package serveur.element.personnages;

import java.util.HashMap;

import serveur.element.Caracteristique;

public class Grenadier extends Personnage {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Cree un personnage Grenadier avec un nom et un groupe.
	 * @param nom du personnage
	 * @param groupe d'etudiants du personnage
	 * @param caracts caracteristiques du personnage
	 */
	public Grenadier(String nom, String groupe, HashMap<Caracteristique, Integer> caracts) {
		super(nom, groupe, caracts);
		this.caracts.put(Caracteristique.VIE, 100);
		this.caracts.put(Caracteristique.FORCE, 50);
		this.caracts.put(Caracteristique.INITIATIVE, 50);
		this.caracts.put(Caracteristique.DEFENSE, 50);
		super.sauvegardeDefense = this.getCaract(Caracteristique.DEFENSE);
		super.sauvegardeDepl = this.getCaract(Caracteristique.DEPLACEMENT);

	}
}

