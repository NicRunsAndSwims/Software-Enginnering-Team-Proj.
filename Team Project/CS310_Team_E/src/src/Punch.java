package src;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;


public class Punch{
    
    public static final int INTERVAL = 15;
    public static final int GRACE_PERIOD = 5;
    public static final int CLOCK_OUT = 0;
    public static final int CLOCK_IN = 1;
    public static final int TIME_OUT = 2;
    private int punchType;
    private Date originalTimestamp;
    private Badge Badge;
    private String stringTimestamp, adjustData;
    public GregorianCalendar greg;
    private int punchID;
    private int termId; 
    private long ots;
    private long ats;
    
    
    
    public Punch(Badge inBadge, long ms, int typeID, String stringTimestamp, int punchId, int tID){
        
        this.punchType = typeID;
        this.ots= ms;
        this.stringTimestamp = stringTimestamp;
        this.punchID = punchId;
        this.termId = tID;
        this.Badge = inBadge;
        
        // Create Gregorian Calendar Object and name him Greg
        greg = new GregorianCalendar();
        greg.setTimeInMillis(ms);
        
    }
    
    public Punch(Badge b, int newTermID, int newPunchID){
        this.Badge = b;
        this.termId = newTermID;
        this.punchID = newPunchID;
        Date date = new Date();
        this.originalTimestamp = date;
        this.ots = date.getTime();
        this.stringTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.ots);
        greg = new GregorianCalendar();
        greg.setTimeInMillis(this.ots);
        
    }
    
  

    public String printOriginalTimestamp(){
        
        StringBuilder output = new StringBuilder("");
        
        output.append( "#"+ this.getBadgeID());
        
        if(this.getPunchType() == 0)
            output.append(" CLOCKED OUT: ");
        else if(this.getPunchType() == 1)
            output.append(" CLOCKED IN: ");
        else if(this.getPunchType() == 2)
            output.append(" TIMED OUT: ");
        
        switch(this.getDayOfWeek()){
            case 1:
                output.append("SUN ");
                break;
            case 2:
                output.append("MON ");
                break;
            case 3:
                output.append("TUE ");
                break;
            case 4:
                output.append("WED ");
                break;
            case 5:
                output.append("THU ");
                break;
            case 6:
                output.append("FRI ");
                break;
            case 7:
                output.append("SAT ");
                break;
                
        }
        
        String OTStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(ots);
        
        output.append(OTStamp);
        
        return output.toString();
    }
    
    public String printAdjustedTimestamp(){
        StringBuilder output = new StringBuilder("");
        
        output.append( "#"+ this.getBadgeID());
        
        if(this.getPunchType() == 0)
            output.append(" CLOCKED OUT: ");
        else if(this.getPunchType() == 1)
            output.append(" CLOCKED IN: ");
        else if(this.getPunchType() == 2)
            output.append(" TIMED OUT: ");
        
        switch(this.getDayOfWeek()){
            case 1:
                output.append("SUN ");
                break;
            case 2:
                output.append("MON ");
                break;
            case 3:
                output.append("TUE ");
                break;
            case 4:
                output.append("WED ");
                break;
            case 5:
                output.append("THU ");
                break;
            case 6:
                output.append("FRI ");
                break;
            case 7:
                output.append("SAT ");
                break;
                
        }
        
        String OTStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(ats);
        
        output.append(OTStamp + " (");
        
        output.append(this.getAdjustData() + ")");
        
        return output.toString();
    }
    
    public void adjust(Shift s){
        
        int dayOfWeek = this.getDayOfWeek();
        final int CONVERSION_TO_MILIS = 60000;
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
        
        GregorianCalendar OTSGC = new GregorianCalendar();
        OTSGC.setTimeInMillis(ots);
        
        String parsedOTS = new SimpleDateFormat("HH:mm:ss").format(ots);
        LocalTime OTS = LocalTime.parse(parsedOTS, formatter);
       
        long shiftStartinMilis = s.getStart_Time(dayOfWeek);
        String parsedStartTime = new SimpleDateFormat("HH:mm:ss").format(shiftStartinMilis);
        LocalTime ShiftStart = LocalTime.parse(parsedStartTime,formatter);
         
        long shiftStopinMilis = s.getStop_Time(dayOfWeek);
        String parsedStopTime = new SimpleDateFormat("HH:mm:ss").format(shiftStopinMilis);
        LocalTime ShiftStop = LocalTime.parse(parsedStopTime,formatter);
        
        
        
        long lunchStartinMilis = s.getLunch_Start(dayOfWeek);
        String parsedLunchStartTime = new SimpleDateFormat("HH:mm:ss").format(lunchStartinMilis);
        LocalTime ShiftLunchStart = LocalTime.parse(parsedLunchStartTime,formatter);
        
        
        long lunchStopinMilis = s.getLunch_Stop(dayOfWeek);
        String parsedLunchStopTime = new SimpleDateFormat("HH:mm:ss").format(lunchStopinMilis);
        LocalTime ShiftLunchStop = LocalTime.parse(parsedLunchStopTime,formatter);
  
      
        boolean isWeekEnd = false;
         
       if(OTSGC.get(Calendar.DAY_OF_WEEK) == 1 || OTSGC.get(Calendar.DAY_OF_WEEK) == 7) {
           
           isWeekEnd = true;
           
       }
        
       if(!isWeekEnd){
           
            // PUNCH IN
            if(this.punchType == 1){

                if((OTS.isBefore(ShiftStart)) && (OTS.isAfter(ShiftStart.minusMinutes(INTERVAL)))){ //CLOCK IN LESS THAN 15 MINS EARLY

                    Duration timeDifference = Duration.between(OTS, ShiftStart);
                    ats = ots + timeDifference.toMillis();
                    adjustData = "Shift Start";  

                }

                else if( OTS.isBefore(ShiftLunchStop) && OTS.isAfter(ShiftLunchStart)) { //CLOCK IN EARLY FROM LUNCH
                    
                    Duration timeDifference = Duration.between(OTS, ShiftLunchStop);
                    ats = ots + timeDifference.toMillis();
                    adjustData = "Lunch Stop";


                } 

                else if(OTS.isAfter(ShiftStart) && OTS.isBefore(ShiftStart.plusMinutes(GRACE_PERIOD))){ //CLOCK IN LESS THAN 5 MINS LATE
                    
                    Duration timeDifference = Duration.between(ShiftStart, OTS);
                    ats = ots - timeDifference.toMillis();
                    adjustData = "Shift Start";

                }

                else if(OTS.isAfter(ShiftStart.plusMinutes(GRACE_PERIOD)) && OTS.isBefore(ShiftStart.plusMinutes(INTERVAL))){  //CLOCK IN THAT LATE ENOUGH FOR A DOCK  
                   
                    Duration timeDifference = Duration.between(ShiftStart, OTS);
                    ats = (ots - timeDifference.toMillis()) + (INTERVAL * CONVERSION_TO_MILIS); 
                    adjustData = "Shift Dock";

                }

                else if(OTS.isAfter(ShiftLunchStop) && OTS.isBefore(ShiftLunchStop.plusMinutes(GRACE_PERIOD))){  //CLOCK IN WITHIN GRACE PERIOD FOR LUNCH
                    
                    Duration timeDifference = Duration.between(OTS, ShiftLunchStop);            
                    ats = ots - timeDifference.toMillis();   
                    adjustData = "Lunch Stop";

                }

                else if(OTS.isAfter(ShiftLunchStop.plusMinutes(GRACE_PERIOD)) && OTS.isBefore(ShiftLunchStop.plusMinutes(INTERVAL))){ //CLOCK IN FROM LUNCH THAT IS DOCKED
                    Duration timeDifference = Duration.between(ShiftLunchStop, OTS);
                    ats = (ots - timeDifference.toMillis()) + (INTERVAL * CONVERSION_TO_MILIS); 
                    adjustData = "Lunch Stop";
                }

                else if(OTS.getMinute() == 0 || (OTS.getMinute()) == 15 || OTS.getMinute() == 30 || OTS.getMinute() == 45){
                    OTSGC.set(Calendar.SECOND, 0);
                    ats = OTSGC.getTimeInMillis();
                    adjustData = "None";
                }

                else{
                    
                    LocalTime Interval0 = LocalTime.of(OTS.getHour(),0,0);
                    LocalTime Interval7_5 = LocalTime.of(OTS.getHour(), 7, 30);
                    LocalTime Interval22_5 = LocalTime.of(OTS.getHour(), 22,30);
                    LocalTime Interval37_5 = LocalTime.of(OTS.getHour(), 37,30);
                    LocalTime Interval52_5 = LocalTime.of(OTS.getHour(), 52,30);
                    LocalTime Interval60_0 = LocalTime.of(OTS.getHour() + 1,0,0);

                    if(OTS.isAfter(Interval0) && OTS.isBefore(Interval7_5)){
                        OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
                        OTSGC.set(Calendar.MINUTE, 0);
                        OTSGC.set(Calendar.SECOND, 0);
                        ats = OTSGC.getTimeInMillis();
                        adjustData = "Interval Round";   
                    }
                    else if(OTS.isAfter(Interval7_5) && OTS.isBefore(Interval22_5)){
                        OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
                        OTSGC.set(Calendar.MINUTE, 15);
                        OTSGC.set(Calendar.SECOND, 0);
                        ats = OTSGC.getTimeInMillis();
                        adjustData = "Interval Round";   

                    }
                    else if(OTS.isAfter(Interval22_5) && OTS.isBefore(Interval37_5)){
                        OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
                        OTSGC.set(Calendar.MINUTE, 30);
                        OTSGC.set(Calendar.SECOND, 0);
                        ats = OTSGC.getTimeInMillis();
                        adjustData = "Interval Round";   

                    }
                    else if(OTS.isAfter(Interval37_5) && OTS.isBefore(Interval52_5)){
                        OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
                        OTSGC.set(Calendar.MINUTE, 45);
                        OTSGC.set(Calendar.SECOND, 0);
                        ats = OTSGC.getTimeInMillis();
                        adjustData = "Interval Round";   

                    }
                    else if(OTS.isAfter(Interval52_5) && OTS.isBefore(Interval60_0)){
                        OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour() + 1);
                        OTSGC.set(Calendar.MINUTE, 0);
                        OTSGC.set(Calendar.SECOND, 0);
                        ats = OTSGC.getTimeInMillis();
                        adjustData = "Interval Round";   

                    }
                    
                    else if(OTS.equals(Interval7_5)){
                       OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
                       OTSGC.set(Calendar.MINUTE, 15);
                       OTSGC.set(Calendar.SECOND, 0);
                       ats = OTSGC.getTimeInMillis();
                       adjustData = "Interval Round";
                   }
                    else if(OTS.equals(Interval22_5)){
                       OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
                       OTSGC.set(Calendar.MINUTE, 30);
                       OTSGC.set(Calendar.SECOND, 0);
                       ats = OTSGC.getTimeInMillis();
                       adjustData = "Interval Round";
                   }
                    else if(OTS.equals(Interval37_5)){
                       OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
                       OTSGC.set(Calendar.MINUTE, 45);
                       OTSGC.set(Calendar.SECOND, 0);
                       ats = OTSGC.getTimeInMillis();
                       adjustData = "Interval Round";
                   }
                    else if(OTS.equals(Interval52_5)){
                       OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour() + 1);
                       OTSGC.set(Calendar.MINUTE, 0);
                       OTSGC.set(Calendar.SECOND, 0);
                       ats = OTSGC.getTimeInMillis();
                       adjustData = "Interval Round";
                   }
                    else if(OTS.equals(Interval60_0)){
                       ats = ots;
                       adjustData = "Interval Round";
                   }

                }

            }
        
        //PUNCH OUT
        if ( this.punchType == 0) {
            
            if ( OTS.isAfter(ShiftStop) && OTS.isBefore(ShiftStop.plusMinutes(INTERVAL))) { // ClOCK OUT LATE LESS THAN 15 MINS
                
                Duration timeDifference = Duration.between(ShiftStop, OTS);
                ats = ots - timeDifference.toMillis();
                adjustData = "Shift Stop";
            }
            
            else if( OTS.isAfter(ShiftLunchStart) && OTS.isBefore(ShiftLunchStop)) { //CLOCK OUT LATE FOR LUNCH
               
                Duration timeDifference = Duration.between(ShiftLunchStart, OTS);
                ats = ots - timeDifference.toMillis();
                adjustData = "Lunch Start";
            }
            
            else if(OTS.isBefore(ShiftStop) && OTS.isAfter(ShiftStop.minusMinutes(GRACE_PERIOD))){ //CLOCK OUT EARLY LESS THAN 5 MINS
               
                Duration timeDifference = Duration.between(OTS, ShiftStop);
                ats = ots + timeDifference.toMillis();                        
                adjustData = "Shift Stop";
            }
            
            else if((OTS.isBefore(ShiftStop.minusMinutes(GRACE_PERIOD)) && OTS.isAfter(ShiftStop.minusMinutes(INTERVAL))) || OTS.equals(ShiftStop.minusMinutes(INTERVAL))){ //CLOCK EARLY THAT IS DOCKED
                
                Duration timeDifference = Duration.between(OTS, ShiftStop );
                ats = (ots + timeDifference.toMillis()) - (INTERVAL * CONVERSION_TO_MILIS);
                adjustData = "Shift Dock";
            }
            
            else if(OTS.isBefore(ShiftLunchStart.minusMinutes(GRACE_PERIOD)) && OTS.isAfter(ShiftLunchStart.minusMinutes(INTERVAL))){
                
                Duration timeDifference = Duration.between(OTS, ShiftLunchStart);
                ats = (ots + timeDifference.toMillis()) - (INTERVAL * CONVERSION_TO_MILIS);
                adjustData = "Lunch Start";
            }
            else if(OTS.getMinute() == 0 || (OTS.getMinute()) == 15 || OTS.getMinute() == 30 || OTS.getMinute() == 45){
                    OTSGC.set(Calendar.SECOND, 0);
                    ats = OTSGC.getTimeInMillis();
                    adjustData = "None";
                }
           else{
           
                LocalTime Interval0 = LocalTime.of(OTS.getHour(),0,0);
           LocalTime Interval7_5 = LocalTime.of(OTS.getHour(), 7, 30);
           LocalTime Interval22_5 = LocalTime.of(OTS.getHour(), 22,30);
           LocalTime Interval37_5 = LocalTime.of(OTS.getHour(), 37,30);
           LocalTime Interval52_5 = LocalTime.of(OTS.getHour(), 52,30);
           LocalTime Interval60_0 = LocalTime.of(OTS.getHour() + 1,0,0);
           
           if(OTS.isAfter(Interval0) && OTS.isBefore(Interval7_5)){
               OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
               OTSGC.set(Calendar.MINUTE, 0);
               OTSGC.set(Calendar.SECOND, 0);
               ats = OTSGC.getTimeInMillis();
               adjustData = "Interval Round";   
           }
           else if(OTS.isAfter(Interval7_5) && OTS.isBefore(Interval22_5)){
               OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
               OTSGC.set(Calendar.MINUTE, 15);
               OTSGC.set(Calendar.SECOND, 0);
               ats = OTSGC.getTimeInMillis();
               adjustData = "Interval Round";   
               
           }
           else if(OTS.isAfter(Interval22_5) && OTS.isBefore(Interval37_5)){
               OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
               OTSGC.set(Calendar.MINUTE, 30);
               OTSGC.set(Calendar.SECOND, 0);
               ats = OTSGC.getTimeInMillis();
               adjustData = "Interval Round";   
               
           }
           else if(OTS.isAfter(Interval37_5) && OTS.isBefore(Interval52_5)){
               OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
               OTSGC.set(Calendar.MINUTE, 45);
               OTSGC.set(Calendar.SECOND, 0);
               ats = OTSGC.getTimeInMillis();
               adjustData = "Interval Round";   
               
           }
           else if(OTS.isAfter(Interval52_5) && OTS.isBefore(Interval60_0)){
               OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour() + 1);
               OTSGC.set(Calendar.MINUTE, 0);
               OTSGC.set(Calendar.SECOND, 0);
               ats = OTSGC.getTimeInMillis();
               adjustData = "Interval Round";   
               
           }
           else if(OTS.equals(Interval7_5)){
                OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
                OTSGC.set(Calendar.MINUTE, 15);
                OTSGC.set(Calendar.SECOND, 0);
                ats = OTSGC.getTimeInMillis();
                adjustData = "Interval Round";
            }
             else if(OTS.equals(Interval22_5)){
                OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
                OTSGC.set(Calendar.MINUTE, 30);
                OTSGC.set(Calendar.SECOND, 0);
                ats = OTSGC.getTimeInMillis();
                adjustData = "Interval Round";
            }
             else if(OTS.equals(Interval37_5)){
                OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
                OTSGC.set(Calendar.MINUTE, 45);
                OTSGC.set(Calendar.SECOND, 0);
                ats = OTSGC.getTimeInMillis();
                adjustData = "Interval Round";
            }
             else if(OTS.equals(Interval52_5)){
                OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour() + 1);
                OTSGC.set(Calendar.MINUTE, 0);
                OTSGC.set(Calendar.SECOND, 0);
                ats = OTSGC.getTimeInMillis();
                adjustData = "Interval Round";
            }
             else if(OTS.equals(Interval60_0)){
                ats = ots;
                adjustData = "Interval Round";
            }
               
            }
 
        }
        
        else if(this.punchType == 2){
            ats = ots - (ots - INTERVAL * CONVERSION_TO_MILIS);
            adjustData = "Shift Stop";
        }
        
       }
       else if(isWeekEnd){
            
           LocalTime Interval0 = LocalTime.of(OTS.getHour(),0,0);
           LocalTime Interval7_5 = LocalTime.of(OTS.getHour(), 7, 30);
           LocalTime Interval22_5 = LocalTime.of(OTS.getHour(), 22,30);
           LocalTime Interval37_5 = LocalTime.of(OTS.getHour(), 37,30);
           LocalTime Interval52_5 = LocalTime.of(OTS.getHour(), 52,30);
           LocalTime Interval60_0 = LocalTime.of(OTS.getHour() + 1,0,0);
           
           if(OTS.isAfter(Interval0) && OTS.isBefore(Interval7_5)){
               OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
               OTSGC.set(Calendar.MINUTE, 0);
               OTSGC.set(Calendar.SECOND, 0);
               ats = OTSGC.getTimeInMillis();
               adjustData = "Interval Round";   
           }
           else if(OTS.isAfter(Interval7_5) && OTS.isBefore(Interval22_5)){
               OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
               OTSGC.set(Calendar.MINUTE, 15);
               OTSGC.set(Calendar.SECOND, 0);
               ats = OTSGC.getTimeInMillis();
               adjustData = "Interval Round";   
               
           }
           else if(OTS.isAfter(Interval22_5) && OTS.isBefore(Interval37_5)){
               OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
               OTSGC.set(Calendar.MINUTE, 30);
               OTSGC.set(Calendar.SECOND, 0);
               ats = OTSGC.getTimeInMillis();
               adjustData = "Interval Round";   
               
           }
           else if(OTS.isAfter(Interval37_5) && OTS.isBefore(Interval52_5)){
               OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
               OTSGC.set(Calendar.MINUTE, 45);
               OTSGC.set(Calendar.SECOND, 0);
               ats = OTSGC.getTimeInMillis();
               adjustData = "Interval Round";   
               
           }
           else if(OTS.isAfter(Interval52_5) && OTS.isBefore(Interval60_0)){
               OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour() + 1);
               OTSGC.set(Calendar.MINUTE, 0);
               OTSGC.set(Calendar.SECOND, 0);
               ats = OTSGC.getTimeInMillis();
               adjustData = "Interval Round";   
               
           }
            else if(OTS.equals(Interval7_5)){
                OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
                OTSGC.set(Calendar.MINUTE, 15);
                OTSGC.set(Calendar.SECOND, 0);
                ats = OTSGC.getTimeInMillis();
                adjustData = "Interval Round";
            }
             else if(OTS.equals(Interval22_5)){
                OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
                OTSGC.set(Calendar.MINUTE, 30);
                OTSGC.set(Calendar.SECOND, 0);
                ats = OTSGC.getTimeInMillis();
                adjustData = "Interval Round";
            }
             else if(OTS.equals(Interval37_5)){
                OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour());
                OTSGC.set(Calendar.MINUTE, 45);
                OTSGC.set(Calendar.SECOND, 0);
                ats = OTSGC.getTimeInMillis();
                adjustData = "Interval Round";
            }
             else if(OTS.equals(Interval52_5)){
                OTSGC.set(Calendar.HOUR_OF_DAY, OTS.getHour() + 1);
                OTSGC.set(Calendar.MINUTE, 0);
                OTSGC.set(Calendar.SECOND, 0);
                ats = OTSGC.getTimeInMillis();
                adjustData = "Interval Round";
            }
             else if(OTS.equals(Interval60_0)){
                ats = ots;
                adjustData = "Interval Round";
            }
                
       }
       
    }

        // Getters
    

    public int getTerminalid() {
        return termId;
    }
    
    public Date getOriginalTimestamp(){
        return this.originalTimestamp;
    }
    
    public long getOTS(){
        return this.ots;
    }
    
    public long getATS(){
        return this.ats;
    }
    
    public int getPunchID() {
        return punchID;
    }

    public int getPunchType() {
        return this.punchType;
    }

    public String getStringTimestamp() {
        return this.stringTimestamp;
    }
      
    public String getBadgeID() {
        return Badge.getId();
    }
    
    public int getYear() {
        return greg.YEAR;
    }

    public int getMonth() {
        return greg.MONTH;
    }

    public int getDay() {
        return greg.DAY_OF_MONTH;
    }
    public int getDayOfWeek() {
        return greg.get(Calendar.DAY_OF_WEEK);
    }

    public int getHour() {
        return greg.HOUR_OF_DAY;
    }

    public int getMinute() {
        return greg.MINUTE;
    }

    public int getSecond() {
        return greg.SECOND;
    }

    public int getmSecond() {
        return greg.MILLISECOND;
    }

    public String getName() {
        return Badge.getName();
    }

    public GregorianCalendar getGreg() {
        return greg;
    }
    public String getAdjustData(){
        return adjustData;
    }
    
    
    
    // Setters
    

    public void setTermID(int termId) {
        this.termId = termId;
    }

    public void setPunchID(int punchID) {
        this.punchID = punchID;
    }

    public void setStringTimestamp(String stringTimestamp) {
        this.stringTimestamp = stringTimestamp;
    }

    public void setYear(int year) {
        greg.set(Calendar.YEAR, year);
    }

    public void setMonth(int month) {
        greg.set(Calendar.MONTH, month);
    }

    public void setDay(int day) {
        greg.set(Calendar.DAY_OF_YEAR, day);
    }
    public void setDayOfWeek(int dayOfWeek){
        greg.set(Calendar.DAY_OF_WEEK, dayOfWeek);
    }

    public void setHour(int hour) {
        greg.set(Calendar.HOUR_OF_DAY, hour);
    }

    public void setMinute(int minute) {
        greg.set(Calendar.MINUTE, minute);
    }

    public void setSecond(int second) {
        greg.set(Calendar.SECOND, second);
    }

    public void setmSecond(int mSecond) {
        greg.set(Calendar.MILLISECOND, mSecond);
    }

    public void setPunchType(int type) {
        this.punchType = type;
    }

    public void setGreg(GregorianCalendar greg) {
        this.greg = greg;
    }
    
    public String toSrting(){
        
        
        
        String OTS = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(ots);
        String ATS = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(ats);
        
        StringBuilder output = new StringBuilder();
        output.append("Type : " + this.punchType);
        output.append(" ID: " + this.punchID);
        output.append(" Badge: " + this.Badge);
        output.append(" OTS: " + OTS);
        output.append(" ATS " + ATS);
        output.append("  adjustData: " + this.adjustData);
        
        return output.toString();
    }
    
        
}
 

