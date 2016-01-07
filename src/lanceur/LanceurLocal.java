/**
 * 
 */
package lanceur;

import javax.swing.SwingUtilities;

/**
 * Lance une fenetre de lanceur version locale
 */

public class LanceurLocal {
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				FenetreLanceurLocal fenetre = new FenetreLanceurLocal();
				fenetre.setVisible(true);
			}
		});
	}
}
