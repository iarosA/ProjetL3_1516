package client.controle.strategies;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface IStrategie {

	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException;
	
}
