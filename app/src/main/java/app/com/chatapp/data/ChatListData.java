package app.com.chatapp.data;

public class ChatListData {
    int contactID;
    String strFirstName;
    String strLastName;
    String imgURL;
    int msgCount;
    int maxID;
    String strLastDate;
    String strLastMessage;
    String strEmail;
    int state;

    public void setFirstName(String strFirstName){this.strFirstName = strFirstName;}
    public String getFirstName(){return this.strFirstName;}

    public void setLastName(String strLastName){this.strLastName = strLastName;}
    public String getLastName(){return this.strLastName;}

    public void setLastDate(String strLastDate){this.strLastDate = strLastDate;}
    public String getLastDate(){return this.strLastDate;}

    public void setEmail(String strEmail){this.strEmail = strEmail;}
    public String getEmail(){return this.strEmail;}

    public void setLastMessage(String strLastMessage){this.strLastMessage = strLastMessage;}
    public String getLastMessage(){return this.strLastMessage;}

    public void setImgURL(String imgURL){this.imgURL = imgURL;}
    public String getImgURL(){return this.imgURL;}

    public void setContactID(int id){this.contactID = id;}
    public int getContactID(){return this.contactID;}

    public void setMaxID(int maxID){this.maxID = maxID;}
    public int getmaxID(){return this.maxID;}

    public void setMsgCount(int msgCount){this.msgCount = msgCount;}
    public int getMsgCount(){return this.msgCount;}

    public void setState(int state){this.state = state;}
    public int getState(){return this.state;}
}
