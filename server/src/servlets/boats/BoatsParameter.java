package servlets.boats;
import bms.engine.boatsManagement.boat.Boat;

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
     int maxRowers;

     public BoatsParameter(){}

     public BoatsParameter (Boat boat){
         this.boatName = boat.getBoatName();
         this.idNum = boat.getSerialNum();
         this.privateProperty = boat.isPrivateProperty();
         this.status = boat.isOutOfOrder();
         this.shortName = boat.getBoatType().getShortName();
         this.maxRowers = boat.getMaxNumOfRowers();
         //this.helmsman = boat.getBoatType().hasHelmsman();
         //this.rowersNum = boat.getBoatType().;
     }

 }