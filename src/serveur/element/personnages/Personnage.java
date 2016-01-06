/**
 * 
 */
package serveur.element.personnages;

import java.util.HashMap;

import serveur.element.Caracteristique;
import serveur.element.Element;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Un personnage: un element possedant des caracteristiques et etant capable
 * de jouer une strategie.
 * 
 */
public class Personnage extends Element {
	
	
	private static final long serialVersionUID = 1L;

	protected int nbToursInvincibilite = 0;
	protected int nbToursDeplacementAccru = 0;
	protected int nbToursBrulure = 0;
	protected int nbToursParalysie = 0;
	
	protected int sauvegardeDefense;
	protected int sauvegardeDepl;
	
	
	
	
	/**
	 * Cree un personnage avec un nom et un groupe.
	 * @param nom du personnage
	 * @param groupe d'etudiants du personnage
	 * @param caracts caracteristiques du personnage
	 */
	public Personnage(String nom, String groupe, HashMap<Caracteristique, Integer> caracts) {
		super(nom, groupe, caracts);
		this.sauvegardeDefense = this.getCaract(Caracteristique.DEFENSE);
		this.sauvegardeDepl = this.getCaract(Caracteristique.DEPLACEMENT);
	}
	
	/**
	 * Incremente la caracteristique donnee de la valeur donnee.
	 * Si la caracteristique n'existe pas, elle sera cree avec la valeur 
	 * donnee.
	 * @param c caracteristique
	 * @param inc increment (peut etre positif ou negatif)
	 */
	public void incrementeCaract(Caracteristique c, int inc) {		
		if(caracts.containsKey(c)) {
			caracts.put(c, Calculs.restreintCarac(c, caracts.get(c) + inc));
		} else {
			caracts.put(c, Calculs.restreintCarac(c, inc));
		}
	}
	
	/**
	 * Tue ce personnage en mettant son nombre de poins de vie a 0.
	 */
	public void tue() {
		caracts.put(Caracteristique.VIE, 0);
	}

	/**
	 * Teste si le personnage est vivant, i.e., son nombre de points de vie
	 * est strictement superieur a 0.
	 * @return vrai si le personnage est vivant, faux sinon
	 */
	public boolean estVivant() {
		Integer vie = caracts.get(Caracteristique.VIE);
		return vie != null && vie > 0;
	}
	
	public void brulure() {
		this.nbToursBrulure = 1;
	}
	
	public boolean subirBrulure() {
		if (this.nbToursBrulure > 0) {
			if (this.nbToursBrulure == Constantes.NB_TOURS_BRULURE)
				this.nbToursBrulure = 0;
			else
				this.nbToursBrulure++;
			if(getCaract(Caracteristique.DEFENSE)<100)
				incrementeCaract(Caracteristique.VIE, Constantes.EFFET_BRULURE);
			return true;
		}
		return false;
	}
	
	public void invincibilite() {
		this.nbToursInvincibilite = 1;
		if (getCaract(Caracteristique.DEFENSE) != 100)
		{
			this.sauvegardeDefense = getCaract(Caracteristique.DEFENSE);
			caracts.put(Caracteristique.DEFENSE, 100);
		}
	}
	
	public boolean subirInvincibilite() {
		if (this.nbToursInvincibilite > 0) {
			if (this.nbToursInvincibilite == Constantes.NB_TOURS_INVINCIBILITE)
			{
				this.nbToursInvincibilite = 0;
				caracts.put(Caracteristique.DEFENSE, sauvegardeDefense);
			}
			else
				this.nbToursInvincibilite++;
			return true;
		}
		return false;
	}
	
	public void paralysie() {
		if (nbToursDeplacementAccru != 0)				//on teste si le sujet a un deplacement accru
		{											//si oui, 
			nbToursDeplacementAccru = 0;			//on lui retire l'effet et on restaure le Deplacement d'origine
			caracts.put(Caracteristique.DEPLACEMENT, sauvegardeDepl);
		}
		this.nbToursParalysie = 1;
		if (getCaract(Caracteristique.DEPLACEMENT) != 0)
		{
			this.sauvegardeDepl = getCaract(Caracteristique.DEPLACEMENT);
			caracts.put(Caracteristique.DEPLACEMENT, 0);
		}
	}
	
	public boolean subirParalysie() {
		if (this.nbToursParalysie > 0) {
			if (this.nbToursParalysie == Constantes.NB_TOURS_PARALYSIE)
			{
				this.nbToursParalysie = 0;
				caracts.put(Caracteristique.DEPLACEMENT, sauvegardeDepl);
			}
			else
				this.nbToursParalysie++;
			return true;
		}
		return false;
	}
	
	public void deplacementAccru() {
		if (nbToursParalysie != 0)				//on teste si le sujet est paralyse
		{
			nbToursParalysie = 0;				//si oui, on lui retire l'effet et on restaure le Deplacement d'origine
			caracts.put(Caracteristique.DEPLACEMENT, sauvegardeDepl);
		}
		if(this.nbToursDeplacementAccru == 0)
			this.sauvegardeDepl = getCaract(Caracteristique.DEPLACEMENT);
		caracts.put(Caracteristique.DEPLACEMENT,
				getCaract(Caracteristique.DEPLACEMENT) + Constantes.EFFET_DEPLACEMENT_ACCRU);
		this.nbToursDeplacementAccru = 1;
	}
	
	public boolean subirDeplacementAccru() {
		if (this.nbToursDeplacementAccru > 0) {
			if (this.nbToursDeplacementAccru == Constantes.NB_TOURS_DEPLACEMENT_ACCRU)
			{
				this.nbToursDeplacementAccru = 0;
				caracts.put(Caracteristique.DEPLACEMENT, sauvegardeDepl);
			}
			else
				this.nbToursDeplacementAccru++;
			return true;
		}
		return false;
	}
}
