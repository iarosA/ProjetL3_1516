package serveur.element.potions;

import java.util.HashMap;

import serveur.element.Caracteristique;

public class PotionTeleportation extends Potion {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur d'une potion avec un nom, le groupe
	 * Teleporte a une position aleatoire
	 * @param nom nom de la potion
	 * @param groupe groupe d'etudiants de la potion
	 * @param caracts caracteristiques de la potion
	 */
	public PotionTeleportation(String nom, String groupe) {
		super(nom, groupe, new HashMap<Caracteristique, Integer>());
		this.caracts.put(Caracteristique.VIE, 0);
		this.caracts.put(Caracteristique.FORCE, 0);
		this.caracts.put(Caracteristique.INITIATIVE, 0);
		this.caracts.put(Caracteristique.DEFENSE, 0);
		this.caracts.put(Caracteristique.DEPLACEMENT, 0);
		
	}
}
