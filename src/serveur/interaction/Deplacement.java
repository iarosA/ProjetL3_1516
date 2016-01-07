package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;

/**
 * Represente le deplacement d'un personnage.
 *
 */
public class Deplacement {

	/**
	 * Vue du personnage qui veut se deplacer.
	 */
	private VuePersonnage personnage;
	
	/**
	 * References RMI et vues des voisins (calcule au prealable). 
	 */
	private HashMap<Integer, Point> voisins;
	
	/**
	 * Cree un deplacement.
	 * @param personnage personnage voulant se deplacer
	 * @param voisins voisins du personnage
	 */
	public Deplacement(VuePersonnage personnage, HashMap<Integer, Point> voisins) { 
		this.personnage = personnage;

		if (voisins == null) {
			this.voisins = new HashMap<Integer, Point>();
		} else {
			this.voisins = voisins;
		}
	}

	/**
	 * Deplace ce sujet d'une case en direction de l'element dont la reference
	 * est donnee.
	 * Si la reference est la reference de l'element courant, il ne bouge pas ;
	 * si la reference est egale a 0, il erre ;
	 * sinon il va vers le voisin correspondant (s'il existe dans les voisins).
	 * @param refObjectif reference de l'element cible
	 */    
	public void seDirigeVers(int refObjectif, int distance) throws RemoteException {
		Point pvers;

		// on ne bouge que si la reference n'est pas la notre
		if (refObjectif != personnage.getRefRMI()) {
			
			// la reference est nulle (en fait, nulle ou negative) : 
			// le personnage erre
			if (refObjectif <= 0) { 
				pvers = Calculs.positionAleatoireArene();
						
			} else { 
				// sinon :
				// la cible devient le point sur lequel se trouve l'element objectif
				pvers = voisins.get(refObjectif);
			}
	
			// on ne bouge que si l'element existe
			if(pvers != null) {
				seDirigeVers(pvers, distance);
			}
		}
	}

	/**
	 * Deplace ce sujet d'une case en direction de la case donnee.
	 * @param objectif case cible
	 * @throws RemoteException
	 */
	public void seDirigeVers(Point objectif, int distance) throws RemoteException {
		Point cible = Calculs.restreintPositionArene(objectif); 
		
		// on cherche le point voisin vide
		Point dest = Calculs.meilleurPoint(personnage.getPosition(), cible, voisins, distance);
		
		if(dest != null) {
			personnage.setPosition(dest);
		}
	}
	
	/**
	 * Deplace ce sujet de dix cases en direction de l'element dont la reference
	 * est donnee.
	 * Si la reference est la reference de l'element courant, il ne bouge pas ;
	 * si la reference est egale a 0, il erre ;
	 * sinon il va vers le voisin correspondant (s'il existe dans les voisins).
	 * @param refObjectif reference de l'element cible
	 */  
	public void seTeleporteVers(int refObjectif, HashMap<Integer, Point> voisinsDest) throws RemoteException {
		Point pvers;

		// on ne bouge que si la reference n'est pas la notre
		if (refObjectif != personnage.getRefRMI()) {
			
			// la reference est nulle (en fait, nulle ou negative) : 
			// le personnage erre
			if (refObjectif <= 0) { 
				pvers = Calculs.positionAleatoireArene();
				seTeleporteVers(pvers, null);
			} else { 
				// sinon :
				// la cible devient le point sur lequel se trouve l'element objectif
				pvers = voisins.get(refObjectif);
				seTeleporteVers(pvers, voisinsDest);
			}
		}
	}
	
	/**
	 * Teleporte ce sujet devant la case donnee.
	 * @param objectif case cible
	 * @throws RemoteException
	 */
	public void seTeleporteVers(Point objectif, HashMap<Integer, Point> voisinsDest) throws RemoteException {
		Point cible = Calculs.restreintPositionArene(objectif); 
		if (voisinsDest == null) {
			personnage.setPosition(cible);
		}
		else {
			Point dest = null;
			ArrayList<Point> listePossibles = new ArrayList<Point>();		
			
			Point tempPoint;
			
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if ((i != 0) || (j != 0))  { // pas le point lui-meme
						tempPoint = new Point(objectif.x + i, objectif.y + j);
						if(Calculs.estDansArene(tempPoint)) {
							listePossibles.add(tempPoint);
						}
					}
				}
			}
			
			for (Point p : listePossibles) {
				if (Calculs.caseVide(p, voisinsDest)) {
					dest = p;
					break;
				}
			}
			if (dest == null) dest = Calculs.positionAleatoireArene();
		
			personnage.setPosition(dest);
		}
	}

}
