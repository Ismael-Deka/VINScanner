package com.ismaelDeka.vinscanner;

import java.util.Arrays;

/**
 * Created by Ismael on 10/5/2017.
 */

public class CarCategoryFinder {
    private static final String[] AS_SYSTEM = {"Active Safety System","Driver Assist", "Adaptive Cruise Control", "Adaptive Headlights","Anti-lock Braking System (ABS)","Auto Brake / Auto Emergency Braking","Blind Spot Monitoring / Detection","Electronic Stability Control (ESC)","Traction Control","Forward Collision Warning","Lane Departure Warning","Lane Keep System","Rear Visibility Camera","Park Assist","TPMS"};
    private static final String[] ENGINE = {"Engine","Engine Number of Cylinders","Displacement (CC)","Displacement (CI)","Displacement (L)","Engine Stroke Cycles","Engine Model","Engine Power (KW)","Fuel Type - Primary","Valve Train Design","Engine Configuration","Fuel Type - Secondary","Fuel Delivery / Fuel Injection Type","Engine Brake (hp)","Cooling Type","Engine Brake (hp) up to","Electrification Level","Other Engine Info","Turbo","Top Speed (MPH)","Engine Manufacturer"};
    private static final String[] EXTERIOR ={"Exterior","Body Class","Doors","Windows","Wheel Base Type","Track Width","Bus Length (feet)","Bus Floor Configuration Type","Bus Type","Other Bus Info","Gross Vehicle Weight Rating","Bed Length (inches)","Curb Weight (pounds)","Wheel Base (inches)","Wheel Base (inches) up to","Custom Motorcycle Type","Motorcycle Suspension Type","Motorcycle Chassis Type","Other Motorcycle Info","Trailer Type Connection","Trailer Body Type","Trailer Length (feet)","Other Trailer Info","Bed Type","Cab Type","Number of Wheels","Wheel Size Front (inches)","Wheel Size Rear (inches)"};
    private static final String[] GENERAL ={"General","Destination Market","Make","Manufacturer Name","Model","Model Year","Plant City","Series","Trim","Vehicle Type","Plant Country","Plant Company Name","Plant State","Trim2","Series2","Note","Base Price ($)","Manufacturer Id","Cash For Clunkers"};
    private static final String[] INTERIOR ={"Interior","Entertainment System","Steering Location","Number of Seats","Number of Seat Rows"};
    private static final String[] BATTERY = {"Battery","Battery Info","Battery Type","Number of Battery Cells per Module","Battery Current (Amps)","Battery Voltage (Volts)","Battery Energy (KWh)","EV Drive Unit","Battery Current (Amps) up to","Battery Voltage (Volts) up to","Battery Energy (KWh) up to","Number of Battery Modules per Pack","Number of Battery Packs per Vehicle","Charger Level","Charger Power (KW)"};
    private static final String[] MECHANICAL = {"Mechanical","Brake System Type","Brake System Description","Drive Type","Axles","Axle Configuration","Transmission Style","Transmission Speeds"};
    private static final String[] PS_SYSTEM ={"Passive Safety System","Pretensioner","Seat Belts Type","Other Restraint System Info","Curtain Air Bag Locations","Seat Cushion Air Bag Locations","Front Air Bag Locations","Knee Air Bag Locations","Side Air Bag Locations"};

    public static String getCarCategory(String key){

        if( Arrays.asList(AS_SYSTEM).contains(key)){
            return AS_SYSTEM[0];
        }
        if( Arrays.asList(ENGINE).contains(key)){
            return ENGINE[0];
        }
        if( Arrays.asList(EXTERIOR).contains(key)){
            return EXTERIOR[0];
        }
        if( Arrays.asList(GENERAL).contains(key)){
            return GENERAL[0];
        }
        if( Arrays.asList(INTERIOR).contains(key)){
            return INTERIOR[0];
        }
        if( Arrays.asList(BATTERY).contains(key)){
            return BATTERY[0];
        }
        if( Arrays.asList(MECHANICAL).contains(key)){
            return MECHANICAL[0];
        }
        if( Arrays.asList(PS_SYSTEM).contains(key)){
            return PS_SYSTEM[0];
        }else {
            return "Other";
        }



    }

}
