package app.com.chatapp.data;

public class FriendData {
    int userID;
    String strFirstName;
    String strLastName;
    String strFullName;
    String imgURL;
    String strEmail;
    int state;
    public void setFirstName(String strFirstName){this.strFirstName = strFirstName;}
    public String getFirstName(){return this.strFirstName;}

    public void setLastName(String strLastName){this.strLastName = strLastName;}
    public String getLastName(){return this.strLastName;}

    public void setEmail(String strEmail){this.strEmail = strEmail;}
    public String getEmail(){return this.strEmail;}

    public void setFullName(String strFullName){this.strFullName = strFullName;}
    public String getFullName(){return this.strFullName;}

    public void setImgURL(String imgURL){this.imgURL = imgURL;}
    public String getImgURL(){return this.imgURL;}

    public void setUserID(int id){this.userID = id;}
    public int getUserID(){return this.userID;}

    public void setState(int state){this.state = state;}
    public int getState(){return this.state;}
}
