import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HRImpl extends UnicastRemoteObject implements HRInterface{

    //The serializable class needs to have a final serialVersionUID
    private static final long serialVersionUID = -1113582265865921787L;

    //a 2d array to store num of rooms and its individual costs
    public int[][] availableRooms=
    {
        {30,50},
        {45,70},
        {25,80},
        {10,120},
        {5,150}
    };

    //a 2d array to store type and name o rooms
    public String[][] roomType=
    {
        {"A","single rooms"},
        {"B","double rooms"},
        {"C","twin rooms"},
        {"D","triple rooms"},
        {"E","quad rooms"}
    };

    //a dynamically allocated 2d array responsible for the reservations
    //115 is the sum of all rooms available
    //and 3 are the arguments that can be passed to its room
    public String[][] reservations=new String[115][3];
    int resIndex=0;//helpful variable to check whether there was an entry or not

    public HRImpl() throws RemoteException {
        super();
    }

    //implementation of function availableRooms
    @Override
    public String availableRooms() throws RemoteException{
        String availability="";
        for (int i = 0; i < availableRooms.length; i++) { 
            availability += availableRooms[i][0] + " rooms of type " + roomType[i][0]
                    + "(" + roomType[i][1] + ") are available, with price of: " + availableRooms[i][1]
                    + " euros per night.\n";
        }
        return availability; 
    }
    
    //implementation of function bookRoom
    @Override 
    public String bookRoom(String type, String number, String name) throws RemoteException{
        String total="";
        boolean book=false;
        int numOfRooms=Integer.parseInt(number);

        for(int i = 0; i<roomType.length; i++){

            if(roomType[i][0].equals(type)){
                if(availableRooms[i][0]>=numOfRooms){
                    for(int j=0; j < resIndex; j++){
                      if(reservations[j][0].equals(type) && reservations[j][2].equals(name)){
                            int numOfResRooms=Integer.parseInt(reservations[j][1]) + numOfRooms;
                            reservations[j][1]=String.valueOf(numOfResRooms);
                            availableRooms[i][0] -= numOfRooms;
                            total="Your booking was successful.\nThe total price is : "
                            + availableRooms[i][1] * numOfRooms
                            + "euros.";

                            book=true;                      
                        } 
                    
                    }

                if(!book){
                    reservations[resIndex][0]=type;
                    reservations[resIndex][1]=number;
                    reservations[resIndex][2]=name;

                    resIndex++;

                    availableRooms[i][0] -=numOfRooms;

                    total="Your booking was successful.\nThe total price is : "
                            + availableRooms[i][1] * numOfRooms
                            + " euros.";
                }
            }
                else if(availableRooms[i][0]<numOfRooms && availableRooms[i][0]!=0){
                    total="There are only " + availableRooms[i][0] + " rooms available.Do you want to book the existing ones [Y/N]?";
                }
                else if(availableRooms[i][0]==0){
                    total="No rooms available.";
                }            
            }
        }
        return total;
    }

    //implementation of function guests
    @Override
    public String guests() throws RemoteException{
        String guests="";

        if(resIndex==0){
            guests="There are no confirmed reservations at the moment";
        }
        else{
            guests="The list of reservation(s) is presented below:\n";

            for(int i=0; i<resIndex;i++){
                guests+= reservations[i][2] + " has "
                + reservations[i][1] + " rooms of type "
                + reservations[i][0] + ".\n";
            }
        }
        return guests;
    }

    //implementation of function cancelRooms
    @Override 
    public String cancelRoom(String type, String number, String name) throws RemoteException{
        String total="There was a problem at finding your reservation.";
        
        if(resIndex==0){
            total="There are no reservations to be canceled."; 
        }
        
        else{

        int numOfRooms=Integer.parseInt(number);

        for(int i=0; i<resIndex; i++){
            if(reservations[i][0].equals(type) && reservations[i][2].equals(name)){
                int numOfResRooms=Integer.parseInt(reservations[i][1]);

                if(numOfResRooms== numOfRooms){
                   
                    String [][] temp = new String[resIndex][4];
                    
                    int offset = 0;

                    for(int j=0; j < resIndex; j++){
                    
                        if(!(reservations[j][0].equals(type) && reservations[j][2].equals(name))){

                            temp[j+offset][0] = reservations[j][0];
                            temp[j+offset][1] = reservations[j][1];
                            temp[j+offset][2] = reservations[j][2];
                            temp[j+offset][3] = reservations[j][3];

                        }
                        else
                            offset--;
                                        
                    }

                    for (int j=0; j<resIndex; j++){

                        reservations[j][0] = temp[j][0];
                        reservations[j][1] = temp[j][1];
                        reservations[j][2] = temp[j][2];
                        reservations[j][3] = temp[j][3];

                    }

                    resIndex--;

                    for (int j=0; j < resIndex;j++){

                        if(roomType[j][0].equals(type))
                            availableRooms[j][0] += numOfRooms;
                    }

                    total = "You have canceled all rooms of type " +
                    type + " for the client " + name + ".";
                }

                else if (numOfResRooms > numOfRooms) {
                    numOfResRooms -= numOfRooms;
                    reservations[i][1] = String.valueOf(numOfResRooms);

                    for (int j=0; j < roomType.length; j++){

                        if(roomType[j][0].equals(type)){
                            availableRooms[j][0] += numOfRooms;
                        }
                    }

                    total = "You have canceled " + numOfRooms + " rooms of type " +
                    type + " for the client " + name + ".\nThe client now has " + numOfResRooms + " room(s) in his reservation.";

                }
                else
                    total = "Error! You are trying to cancel more rooms reservations than you have booked.";
                 }           

            }

        }

        return total;
    }
}
