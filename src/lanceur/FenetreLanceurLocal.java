/**
 * 
 */
package lanceur;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author iA
 *
 */
public class FenetreLanceurLocal extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel panel = new JPanel(); //panel principal
	
	public static String[] classesPerso = {"Personnage", "Assassin", "Brute", "Cavalier", "Chimiste", "Grenadier", "Sniper"};
	public static String[] classesPotion = {"Potion", "Arsenic", "Carapace", "Cola", "Gel", "Rage", "Soin", "Vortex", "7Lieues"};
	
	private int i_perso = 0; //indice personnage selectionne
	private int i_potion = 0; //indice potion selectionne
	
	private String message = new String(""); //message d'information
	
	private JButton persoM = new JButton("\u25C0"); //fleche gauche selection perso
	private JButton persoP = new JButton("\u25B6"); //fleche droite selection perso
	private JButton launchPerso = new JButton("Deployer"); //deploiement perso
	private JButton potionM = new JButton("\u25C0"); //fleche gauche selection potion
	private JButton potionP = new JButton("\u25B6"); //fleche gauche selection potion
	private JButton launchPotion = new JButton("Deployer"); //deploiement potion
	private JButton launchAreneIHM;
	
	private JLabel persoLabel; //label nom perso
	private JLabel potionLabel; //label nom potion
	private JLabel lanceurLabel = new JLabel("Lanceur Arene & IHM");
	private JLabel messageLabel; //label message
	
	private boolean estLance = false; //arene et IHM lances

	@SuppressWarnings("unused")
	private ThreadArene areneThread = null; //thread pour l'arene
	@SuppressWarnings("unused")
	private ThreadIHM IHMThread = null; //thread pour l'IHM

	
	/**
	 * CONSTRUCTEUR PAR DEFAUT
	 */
	public FenetreLanceurLocal() {
		//mise en place des listener
		this.persoM.addActionListener(this);
		this.persoP.addActionListener(this);
		this.launchPerso.addActionListener(this);
		this.potionM.addActionListener(this);
		this.potionP.addActionListener(this);
		this.launchPotion.addActionListener(this);
		
		this.lanceurLabel.setPreferredSize(new Dimension(260, 25));
		this.panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
		
		//creation fenetre
		this.setTitle("Lanceur Persos/Potions - Version Locale");
		this.setSize(400, 290);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setContentPane(panel);
		this.buildContentPanel();
	}
	
	
	/**
	 * CONTRUCTION PANEL PRINCIPAL
	 * à rapeller en cas de changements dans la fenetre
	 */
	private void buildContentPanel() {
		this.panel.removeAll();
		this.panel.setBackground(Color.white);
		
		//  Lancement Persos  \\
		this.persoLabel = new JLabel(classesPerso[this.i_perso], JLabel.CENTER);
		this.persoLabel.setPreferredSize(new Dimension(100, 50));
		//Visibilite des boutons
		if (this.i_perso == 0) {
			this.persoM.setEnabled(false);
		}
		else if (this.i_perso == classesPerso.length - 1) {
			this.persoP.setEnabled(false);
		}
		else {
			this.persoM.setEnabled(true);
			this.persoP.setEnabled(true);
		}
		//Panel
		JPanel persoPanel = new JPanel();
		persoPanel.add(this.persoM);
		persoPanel.add(this.persoLabel);
		persoPanel.add(this.persoP);
		persoPanel.add(this.launchPerso);
		
		
		//  Lancement Potions  \\
		this.potionLabel = new JLabel(classesPotion[this.i_potion], JLabel.CENTER);
		this.potionLabel.setPreferredSize(new Dimension(100, 50));	
		//Visibilite des boutons
		if (this.i_potion == 0) {
			this.potionM.setEnabled(false);
		}
		else if (this.i_potion == classesPotion.length - 1) {
			this.potionP.setEnabled(false);
		}
		else {
			this.potionM.setEnabled(true);
			this.potionP.setEnabled(true);
		}
		//Panel
		JPanel potionPanel = new JPanel();
		potionPanel.add(this.potionM);
		potionPanel.add(this.potionLabel);
		potionPanel.add(this.potionP);
		potionPanel.add(this.launchPotion);
		
		
		//  Version Locale  \\
		if (!this.estLance) {
			this.launchAreneIHM = new JButton("Lancer");
		}
		else {
			this.launchAreneIHM = new JButton("Arrêter");
		}
		this.launchAreneIHM.addActionListener(this);
		//Panel
		JPanel localPanel = new JPanel();
		localPanel.add(this.lanceurLabel);
		localPanel.add(this.launchAreneIHM);
	
		
		//  Panel Principal  \\
		this.messageLabel = new JLabel(message, JLabel.CENTER);
		this.messageLabel.setPreferredSize(new Dimension(360, 25));
		this.panel.add(persoPanel);
		this.panel.add(potionPanel);
		this.panel.add(localPanel);
		this.panel.add(messageLabel);
		
		this.setVisible(true);
	}
	
	
	/**
	 * LISTENER
	 * definit les actions lors d'appuis sur les bouttons
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == this.persoM) {
			this.i_perso--;
		}
		////////////////////////////
		else if (source == this.persoP) {
			this.i_perso++;
		}
		////////////////////////////
		else if (source == this.launchPerso) {
			if(!this.estLance) {
				this.message = "Lancez d'abord l'arene et l'IHM..";
			}
			else {
				new ThreadPerso(classesPerso[this.i_perso]);
				this.message = classesPerso[this.i_perso] + " deploye.";
			}
		}
		////////////////////////////
		else if (source == this.potionM) {
			this.i_potion--;
		}
		////////////////////////////
		else if (source == this.potionP) {
			this.i_potion++;
		}
		////////////////////////////
		else if (source == this.launchPotion) {
			if(!this.estLance) {
				this.message = "Lancez d'abord l'arene et l'IHM..";
			}
			else {
				new ThreadPotion(classesPotion[this.i_potion]);
				this.message = "Potion " + classesPotion[this.i_potion] + " deploye";
			}
		}
		////////////////////////////
		else if (source == this.launchAreneIHM) {
			if (!this.estLance) {
				//si pas lance on lance l'arene
				this.areneThread = new ThreadArene();
				try {
					//on attend 1 sec pour etre sur que l'arene s'est lancee
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				//puis on lance l'IHM
				this.IHMThread = new ThreadIHM();
				this.estLance = true;
				this.message = "Arene et IHM lances.";
			}
			else {
				//sinon on quitte tout
				System.exit(0);
			}
		}
		//apres un evenement on reconstruit la fenetre
		this.buildContentPanel();
	}
	
	
	// THREADS
	private class ThreadArene extends Thread {
		public ThreadArene() {
			super();
			start();
		}
		public void run() {
			LanceArene.main(new String[0]);
		}
	}
	private class ThreadIHM extends Thread {
		public ThreadIHM() {
			super();
			start();
		}
		
		public void run() {
			LanceIHM.main(new String[0]);
		}
	}
	private class ThreadPerso extends Thread {
		private String classePerso;
		
		public ThreadPerso(String classePerso) {
			super();
			this.classePerso = classePerso;
			start();
		}
		
		public void run() {
			String[] args = new String[1];
			args[0] = this.classePerso;
			LancePersonnage.main(args);
		}
	}
	private class ThreadPotion extends Thread {
		private String classePotion;
		
		public ThreadPotion(String classePotion) {
			super();
			this.classePotion = classePotion;
			start();
		}
		public void run() {
			String[] args = new String[1];
			args[0] = this.classePotion;
			LancePotion.main(args);
		}
	}
	
	public static boolean existeClassePerso(String nom) {
		for (String s : classesPerso) {
			if (s.equals(nom)) return true;
		}
		return false;
	}
	
	public static boolean existeClassePotion(String nom) {
		for (String s : classesPotion) {
			if (s.equals(nom)) return true;
		}
		return false;
	}
	
	
	// MAIN
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		FenetreLanceurLocal fenetre = new FenetreLanceurLocal();
	}	
}
