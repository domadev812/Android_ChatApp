package app.com.chatapp.data;

/**
 * Created by ShangTao on 31/07/2017.
 */

public class MessageData {
    String strMessage;
    String strDeliverDate;
    int toID;
    int fromID;
    int state;

    public void setMessage(String strMessage){this.strMessage = strMessage;}
    public String getMessage(){return this.strMessage;}

    public void setDeliverDate(String strDeliverDate){this.strDeliverDate = strDeliverDate;}
    public String getDeliverDate(){return this.strDeliverDate;}

    public void setToID(int toID){this.toID = toID;}
    public int getToID(int toID){return this.toID;}

    public void setFromID(int fromID){this.fromID = fromID;}
    public int getFromID(){return this.fromID;}

    public void setState(int state){this.state = state;}
    public int getState(){return this.state;}
}
