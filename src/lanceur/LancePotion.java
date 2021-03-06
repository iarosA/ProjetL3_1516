package lanceur;

import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;

import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.potions.Potion;
import serveur.element.potions.PotionDeplacement;
import serveur.element.potions.PotionForce;
import serveur.element.potions.PotionInitiative;
import serveur.element.potions.PotionInvincibilite;
import serveur.element.potions.PotionParalysie;
import serveur.element.potions.PotionPoison;
import serveur.element.potions.PotionTeleportation;
import serveur.element.potions.PotionVie;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class LancePotion {
	
	private static String usage = "USAGE : java " + LancePotion.class.getName() + " [ port [ ipArene ] ]";

	public static void main(String[] args) {
		
		String groupe = "G13"; 
		
		//indice du nom de potion dans les arguments
		int i_potionArgs = 0;
		
		// init des arguments
		int port = Constantes.PORT_DEFAUT;
		String ipArene = Constantes.IP_DEFAUT;
		
		if (args.length > 0) {
			if (args[0].equals("--help") || args[0].equals("-h")) {
				ErreurLancement.aide(usage);
			}
			
			if (args.length > 3) {
				ErreurLancement.TROP_ARGS.erreur(usage);
			}
			
			if (!FenetreLanceurLocal.existeClassePotion(args[0])) {
				try {
					port = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					ErreurLancement.PORT_NAN.erreur(usage);
				}
				
				if (args.length > 1) {
					if (!FenetreLanceurLocal.existeClassePotion(args[1]))
						ipArene = args[1];
					else i_potionArgs = 1;
				}
				else {
					if (args.length > 2) {
						i_potionArgs = 2;
					}
					else i_potionArgs = -1;
				}
			}
		}
		else {
			System.out.println("Veuillez préciser au moins le type de potion à lancer.");
			System.exit(0);
		}
		
		// creation du logger
		LoggerProjet logger = null;
		try {
			logger = new LoggerProjet(true, "potion_"+ args[i_potionArgs] + "_" + groupe);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
		
		// lancement de la potion
		try {
			IArene arene = (IArene) java.rmi.Naming.lookup(Constantes.nomRMI(ipArene, port, "Arene"));

			logger.info("Lanceur", "Lancement de la potion " + args[i_potionArgs] + " sur le serveur...");
			
			// caracteristiques de la potion
			HashMap<Caracteristique, Integer> caractsPotion = new HashMap<Caracteristique, Integer>();
			
			caractsPotion.put(Caracteristique.VIE, Calculs.valeurCaracAleatoirePosNeg(Caracteristique.VIE));
			caractsPotion.put(Caracteristique.FORCE, Calculs.valeurCaracAleatoirePosNeg(Caracteristique.FORCE));
			caractsPotion.put(Caracteristique.INITIATIVE, Calculs.valeurCaracAleatoirePosNeg(Caracteristique.INITIATIVE));
			
			Point position = Calculs.positionAleatoireArene();
			
			//Par defaut on lance un potion aleatoire
			if (i_potionArgs == -1) {
				arene.ajoutePotion(new Potion("Potion", groupe, caractsPotion), position);
				logger.info("Lanceur", "Lancement de la potion reussi");
			}
			else {
				//Sinon on lance la potion passee en argument
				if (args[i_potionArgs].equals("Soin")) {
					arene.ajoutePotion(new PotionVie("Soin", groupe), position);
					logger.info("Lanceur", "Lancement de la potion soin reussi");
				}
				else if (args[i_potionArgs].equals("Rage")) {
					arene.ajoutePotion(new PotionForce("Rage", groupe), position);
					logger.info("Lanceur", "Lancement de la potion rage reussi");
				}
				else if (args[i_potionArgs].equals("Cola")) {
					arene.ajoutePotion(new PotionInitiative("Cola", groupe), position);
					logger.info("Lanceur", "Lancement de la potion cola reussi");
				}
				else if (args[i_potionArgs].equals("7Lieues")) {
					arene.ajoutePotion(new PotionDeplacement("7Lieues", groupe), position);
					logger.info("Lanceur", "Lancement de la potion 7lieues reussi");
				}
				else if (args[i_potionArgs].equals("Gel")) {
					arene.ajoutePotion(new PotionParalysie("Gel", groupe), position);
					logger.info("Lanceur", "Lancement de la potion gel reussi");
				}
				else if (args[i_potionArgs].equals("Arsenic")) {
					arene.ajoutePotion(new PotionPoison("Arsenic", groupe), position);
					logger.info("Lanceur", "Lancement de la potion arsenic reussi");
				}
				else if (args[i_potionArgs].equals("Vortex")) {
					arene.ajoutePotion(new PotionTeleportation("Vortex", groupe), position);
					logger.info("Lanceur", "Lancement de la potion vortex reussi");
				}
				else if (args[i_potionArgs].equals("Carapace")) {
					arene.ajoutePotion(new PotionInvincibilite("Carapace", groupe), position);
					logger.info("Lanceur", "Lancement de la potion carapace reussi");
				}
				else if (args[i_potionArgs].equals("Potion")) {
					arene.ajoutePotion(new Potion("Potion", groupe, caractsPotion), position);
					logger.info("Lanceur", "Lancement de la potion reussi");
				}
			}
			
		} catch (Exception e) {
			logger.severe("Lanceur", "Erreur lancement :\n" + e.getCause());
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
	}
}
