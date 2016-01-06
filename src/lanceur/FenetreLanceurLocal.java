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
	
	private int i_perso = 0; //personnage selectionné
	private String s_potion = new String("Potion"); //potion selectionnée
	private String message = new String(""); //message d'information
	
	private JButton persoM = new JButton("\u25C0"); //fleche gauche selection perso
	private JButton persoP = new JButton("\u25B6"); //fleche droite selection perso
	private JButton launchPerso = new JButton("Déployer"); //déploiement perso
	private JButton potionM = new JButton("\u25C0"); //fleche gauche selection potion
	private JButton potionP = new JButton("\u25B6"); //fleche gauche selection potion
	private JButton launchPotion = new JButton("Déployer"); //deploiement potion
	private JButton launchAreneIHM;
	
	private JLabel persoLabel; //label nom perso
	private JLabel potionLabel; //label nom potion
	private JLabel lanceurLabel = new JLabel("Lanceur Arène & IHM");
	private JLabel messageLabel; //label message
	
	private boolean estLance = false; //arène et IHM lancé?

	private Thread areneThread; //thread pour l'arene
	private Thread IHMThread; //thread pour l'IHM

	
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
		
		//création fenêtre
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
		//Visibilité des bouttons
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
		this.potionLabel = new JLabel(this.s_potion, JLabel.CENTER);
		this.potionLabel.setPreferredSize(new Dimension(100, 50));	
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
	 * définit les actions lors d'appuis sur les bouttons
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
				Thread persoThread = new ThreadPerso(classesPerso[this.i_perso]);
				persoThread.start();
				this.message = classesPerso[this.i_perso] + " déployé.";
			}
		}
		////////////////////////////
		else if (source == this.potionM) {
			
		}
		////////////////////////////
		else if (source == this.potionP) {
			
		}
		////////////////////////////
		else if (source == this.launchPotion) {
			if(!this.estLance) {
				this.message = "Lancez d'abord l'arene et l'IHM..";
			}
			else {
				Thread potionThread = new ThreadPotion();
				potionThread.start();
				this.message = "Potion déployée";
			}
		}
		////////////////////////////
		else if (source == this.launchAreneIHM) {
			if (!this.estLance) {
				//si pas lancé on lance l'arene
				this.areneThread = new ThreadArene();
				this.areneThread.start();
				try {
					//on attend 1 sec pour être sûr que l'arène s'est lancée
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				//puis on lance l'IHM
				this.IHMThread = new ThreadIHM();
				this.IHMThread.start();
				this.estLance = true;
				this.message = "Arène et IHM lancés.";
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
		public void run() {
			LanceArene.main(new String[0]);
		}
	}
	private class ThreadIHM extends Thread {
		public void run() {
			LanceIHM.main(new String[0]);
		}
	}
	private class ThreadPerso extends Thread {
		private String classePerso;
		
		public ThreadPerso(String classePerso) {
			super();
			this.classePerso = classePerso;
		}
		
		public void run() {
			String[] args = new String[1];
			args[0] = this.classePerso;
			LancePersonnage.main(args);
		}
	}
	private class ThreadPotion extends Thread {
		public void run() {
			LancePotion.main(new String[0]);
		}
	}
	
	public static boolean existeClassePerso(String nom) {
		for (String s : classesPerso) {
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
