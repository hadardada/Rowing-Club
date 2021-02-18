package servlets.boats;
import bms.engine.boatsManagement.boat.Boat;
import com.google.gson.Gson;

public class BoatsParameter {
     String boatName;
     int idNum;
     boolean privateProperty;
     boolean status;
     String rowersNum;
     boolean singleOar;
     boolean wide;
     boolean helmsman;
     boolean coastal;
     String shortName;

     public BoatsParameter(){}

     public BoatsParameter (Boat boat){
         this.boatName = boat.getBoatName();
         this.idNum = boat.getSerialNum();
         this.privateProperty = boat.isPrivateProperty();
         this.status = boat.isOutOfOrder();
         this.shortName = boat.getBoatType().getShortName();
         //this.helmsman = boat.getBoatType().hasHelmsman();
         //this.rowersNum = boat.getBoatType().;
     }

 }