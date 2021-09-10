import java.rmi.*;

public interface HRInterface extends Remote{

    //method to depict available rooms which returns a string
    public String availableRooms() throws RemoteException;

    //method used to book a room with 3 parameters which returns a string
    public String bookRoom(String type, String number, String name) throws RemoteException;

    //method to display guests which also returns a string
    public String guests() throws RemoteException;

    //method to cancel rooms with 3 parameters which returns a string object
    public String cancelRoom(String type, String number, String name) throws RemoteException;
}