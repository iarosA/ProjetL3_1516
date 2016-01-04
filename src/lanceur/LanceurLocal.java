/**
 * 
 */
package lanceur;

import javax.swing.SwingUtilities;

/**
 * @author iA
 *
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
