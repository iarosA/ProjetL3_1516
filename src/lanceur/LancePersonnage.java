package lanceur;

import java.awt.Point;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

import client.controle.strategies.StrategieAssassin;
import client.controle.strategies.StrategieBrute;
import client.controle.strategies.StrategieCavalier;
import client.controle.strategies.StrategieChimiste;
import client.controle.strategies.StrategieGrenadier;
import client.controle.strategies.StrategiePersonnage;
import client.controle.strategies.StrategieSniper;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Lance une Console avec un Element sur l'Arene. 
 * A lancer apres le serveur, eventuellement plusieurs fois.
 */
public class LancePersonnage {
	
	private static String usage = "USAGE : java " + LancePersonnage.class.getName() + " [ port [ ipArene ] ] typePersonnage";

	public static void main(String[] args) {
		
		String groupe = "G13"; 
		
		// nombre de tours pour ce personnage avant d'etre deconnecte 
		// (30 minutes par defaut)
		// si negatif, illimite
		int nbTours = Constantes.NB_TOURS_PERSONNAGE_DEFAUT;
		
		//indice du nom de personnage dans les arguments
		int i_persoArgs = 0;
		
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
			
			if (!FenetreLanceurLocal.existeClassePerso(args[0])) {
				try {
					port = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					ErreurLancement.PORT_NAN.erreur(usage);
				}
			
				if (args.length > 1) {
					if (!FenetreLanceurLocal.existeClassePerso(args[1]))
						ipArene = args[1];
					else i_persoArgs = 1;
				}
				else {
					if (args.length > 2) {
						i_persoArgs = 2;
					}
					else i_persoArgs = -1;
				}
			}
		}
		else {
			System.out.println("Veuillez préciser au moins le type de personnage à lancer.");
			System.exit(0);
		}
		
		// creation du logger
		LoggerProjet logger = null;
		try {
			logger = new LoggerProjet(true, "personnage_" + args[i_persoArgs] + "_" + groupe);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
		
		// lancement du serveur
		try {
			String ipConsole = InetAddress.getLocalHost().getHostAddress();
			
			logger.info("Lanceur", "Creation d'un/une" + args[i_persoArgs] + "...");
			
			// caracteristiques du personnage
			HashMap<Caracteristique, Integer> caracts = new HashMap<Caracteristique, Integer>();
			// seule la force n'a pas sa valeur par defaut (pour Personnage)
			caracts.put(Caracteristique.FORCE, 
					Calculs.valeurCaracAleatoire(Caracteristique.FORCE)); 
			
			//Par defaut on lance un potion aleatoire
			Point position = Calculs.positionAleatoireArene();
			if (i_persoArgs == -1) {
				new StrategiePersonnage(ipArene, port, ipConsole, "Pesonnage", groupe, caracts, nbTours, position, logger);
				logger.info("Lanceur", "Creation d'un personnage reussie");
			}
			else {
				//Sinon on lance la potion passee en argument
				if (args[i_persoArgs].equals("Assassin")) {
					new StrategieAssassin(ipArene, port, ipConsole, "Assassin", groupe, caracts, nbTours, position, logger);
					logger.info("Lanceur", "Creation d'un assassin reussie");
				}
				else if (args[i_persoArgs].equals("Brute")) {
					new StrategieBrute(ipArene, port, ipConsole, "Brute", groupe, caracts, nbTours, position, logger);
					logger.info("Lanceur", "Creation d'une brute reussie");
				}
				else if (args[i_persoArgs].equals("Cavalier")) {
					new StrategieCavalier(ipArene, port, ipConsole, "Cavalier", groupe, caracts, nbTours, position, logger);
					logger.info("Lanceur", "Creation d'un cavalier reussie");
				}
				else if (args[i_persoArgs].equals("Chimiste")) {
					new StrategieChimiste(ipArene, port, ipConsole, "Chimiste", groupe, caracts, nbTours, position, logger);
					logger.info("Lanceur", "Creation d'un chimiste reussie");
				}
				else if (args[i_persoArgs].equals("Grenadier")) {
					new StrategieGrenadier(ipArene, port, ipConsole, "Grenadier", groupe, caracts, nbTours, position, logger);
					logger.info("Lanceur", "Creation d'un grenadier reussie");
				}
				else if (args[i_persoArgs].equals("Sniper")) {
					new StrategieSniper(ipArene, port, ipConsole, "Sniper", groupe, caracts, nbTours, position, logger);
					logger.info("Lanceur", "Creation d'un sniper reussie");
				}
				else if (args[i_persoArgs].equals("Personnage")){
					new StrategiePersonnage(ipArene, port, ipConsole, "Personnage", groupe, caracts, nbTours, position, logger);
					logger.info("Lanceur", "Creation d'un personnage reussie");
				}
			}
					
		} catch (Exception e) {
			logger.severe("Lanceur", "Erreur lancement :\n" + e.getCause());
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
	}
}
