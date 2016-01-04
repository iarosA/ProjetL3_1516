package serveur.element.personnages;

import java.util.HashMap;

import serveur.element.Caracteristique;

public class Assassin extends Personnage {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Cree un personnage avec un nom et un groupe.
	 * @param nom du personnage
	 * @param groupe d'etudiants du personnage
	 * @param caracts caracteristiques du personnage
	 */
	public Assassin(String nom, String groupe, HashMap<Caracteristique, Integer> caracts) {
		super(nom, groupe, caracts);
		this.caracts.put(Caracteristique.VIE, 100);
		this.caracts.put(Caracteristique.FORCE, 40);
		this.caracts.put(Caracteristique.INITIATIVE, 80);
		this.caracts.put(Caracteristique.DEFENSE, 40);

	}
}

