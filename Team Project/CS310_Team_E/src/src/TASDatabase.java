package src;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

// Edited stuff


public class TASDatabase {
    
    Connection conn;
    PreparedStatement pstSelect, pstUpdate;
    ResultSet resultset;
    String query;
    ResultSetMetaData metadata;

    public TASDatabase(){ 

       try{


        String server = ("jdbc:mysql://localhost/tas");
        String username = "root";
        String password = "CS310";
        System.out.println("Connecting to " + server + "...");

        Class.forName("com.mysql.jdbc.Driver").newInstance();

        conn = DriverManager.getConnection(server, username, password);

        if(conn.isValid(0))
            System.out.println("Connection Successful");

        pstSelect = null;
        pstUpdate = null;
        resultset = null;
        metadata = null;
       }

        catch (Exception e) {
            System.err.println(e.toString());
        }
   }

    public Badge getBadge(String badgeID){
        try{
            
        
        query = "SELECT * FROM badge WHERE id = '" + badgeID + "'";
        pstSelect = conn.prepareStatement(query);
        pstSelect.execute();
        resultset = pstSelect.getResultSet();
        
        resultset.first();
        String idNum = resultset.getString(1);
        String name = resultset.getString(2);
        
        Badge b = new Badge(name, idNum);
        
        return b;
        }
        
        catch(Exception e){
            System.err.println("** getBadge: " + e.toString());
        }

        return null;
        
    }
   
    
    public Punch getPunch(int punchID){
        try{
            
        
        query = "SELECT * FROM punch WHERE id = '" + punchID + "'";
        pstSelect = conn.prepareStatement(query);
        pstSelect.execute();
        resultset = pstSelect.getResultSet();
        resultset.first();
        
        int dbPunchID = resultset.getInt(1);
        int termID = resultset.getInt(2);
        String badgeID = resultset.getString(3);
        Timestamp ts = resultset.getTimestamp(4);
        long longTS = ts.getTime();
        int punchType = resultset.getInt(5);
        Badge b = this.getBadge(badgeID);
        String otStamp = ts.toString();
        
        Punch p = new Punch(b, longTS, punchType, otStamp, dbPunchID, termID);
        
        return p;
        }
        
        catch(Exception e){
            System.err.println("** getPunch: " + e.toString());
        }

        return null;
    }
    
    public Shift getShift(int shift_num){
        

        try{
            query = "SELECT * FROM shift WHERE id = '" + shift_num + "'";
            pstSelect = conn.prepareStatement(query);
            pstSelect.execute();
            resultset = pstSelect.getResultSet();
        
            resultset.first();
            int shiftID = resultset.getInt(1);
            
            String description = resultset.getString(2);
            
            int dailyScheduleID = resultset.getInt(3);
            
            query = "SELECT * FROM dailyschedule WHERE id = '" + dailyScheduleID + "'";
            pstSelect = conn.prepareStatement(query);
            pstSelect.execute();
            resultset = pstSelect.getResultSet();
            resultset.first();
            
            Timestamp ts = resultset.getTimestamp(2);
            long start = ts.getTime();
             
            ts = resultset.getTimestamp(3);
            long stop = ts.getTime();
            
            int interval = resultset.getInt(4);
            int grace_period = resultset.getInt(5);
            int dock = resultset.getInt(6);
            
               
            ts = resultset.getTimestamp(7);
            long lunch_start = ts.getTime();
              
            ts = resultset.getTimestamp(8);
            long lunch_stop = ts.getTime();
            
            int lunch_deduct = resultset.getInt(9);
            
            DailySchedule defaultSchedule = new DailySchedule(start,stop,lunch_start,lunch_stop,interval,grace_period,dock,lunch_deduct);
            
            Shift returnShift = new Shift(shiftID, description, defaultSchedule);
            
            return returnShift;
        }
        
        catch(Exception e){
            System.err.println(e.toString());
        }

        return null;
    }
    
    public Shift getShift(Badge badge){
        
        try{
            query = "SELECT * FROM employee WHERE badgeid = '" + badge.getId() + "'";
            pstSelect = conn.prepareStatement(query);
            pstSelect.execute();
            resultset = pstSelect.getResultSet();      
            resultset.first();
  
            int shiftID = resultset.getInt(7);
            
            query = "SELECT * FROM shift WHERE id = '" + shiftID+ "'";
            pstSelect = conn.prepareStatement(query);
            pstSelect.execute();
            resultset = pstSelect.getResultSet();
            resultset.first();
    
            String description = resultset.getString(2);
            
            int dailyScheduleID = resultset.getInt(3);
              
            query = "SELECT * FROM dailyschedule WHERE id = '" + dailyScheduleID + "'";
            pstSelect = conn.prepareStatement(query);
            pstSelect.execute();
            resultset = pstSelect.getResultSet();
            resultset.first();
            
            Timestamp ts = resultset.getTimestamp(2);
            long start = ts.getTime();
             
            ts = resultset.getTimestamp(3);
            long stop = ts.getTime();
            
            int interval = resultset.getInt(4);
            int grace_period = resultset.getInt(5);
            int dock = resultset.getInt(6);
            
               
            ts = resultset.getTimestamp(7);
            long lunch_start = ts.getTime();
              
            ts = resultset.getTimestamp(8);
            long lunch_stop = ts.getTime();
            
            int lunch_deduct = resultset.getInt(9);
            
            DailySchedule defaultSchedule = new DailySchedule(start,stop,lunch_start,lunch_stop,interval,grace_period,dock,lunch_deduct);
            
            Shift returnShift = new Shift(shiftID, description, defaultSchedule);
            
            return returnShift;
            
        }
        
        catch(Exception e){
            System.err.println(e.toString());
        }
        
        return null;
    } 
    public Shift getShift( Badge b, long ts) {
        
        GregorianCalendar garry = TASLogic.makeCal(ts);
        ArrayList<PreparedStatement> selects = new ArrayList<>();
        
        try {
            
            // QUERY 1 
            
            query = "SELECT * FROM employee WHERE badgeid = ?";
            pstSelect = conn.prepareStatement(query);
            pstSelect.setString(1, b.getId());
            pstSelect.execute();
            resultset = pstSelect.getResultSet();
            resultset.first();
            
            int employeeShiftId = resultset.getInt(7); //"That gives us their shift id" -Tyler
            
            
            //QUERY 2
            
            query = "SELECT * FROM shift WHERE id = ?";
            pstSelect = conn.prepareStatement(query);
            pstSelect.setInt(1, employeeShiftId);
            pstSelect.execute();
            resultset = pstSelect.getResultSet();
            resultset.first();
            
            String description = resultset.getString(2);
            int dailyScheduleId = resultset.getInt(3);
            
            //QUERY 3
            
            query = "SELECT * FROM dailyschedule WHERE id = ?";
            pstSelect = conn.prepareStatement(query);
            pstSelect.setInt(1, dailyScheduleId);
            pstSelect.execute();
            resultset = pstSelect.getResultSet();
            resultset.first();
            
            Timestamp nts = resultset.getTimestamp(2);
            long start = nts.getTime();
             
            nts = resultset.getTimestamp(3);
            long stop = nts.getTime();
            
            int interval = resultset.getInt(4);
            int grace_period = resultset.getInt(5);
            int dock = resultset.getInt(6);
            
               
            nts = resultset.getTimestamp(7);
            long lunch_start = nts.getTime();
              
            nts = resultset.getTimestamp(8);
            long lunch_stop = nts.getTime();
            int lunchDeduct = resultset.getInt(9);
            
            DailySchedule dailySchedule = new DailySchedule(start, stop, lunch_start, lunch_stop, interval, grace_period, dock, lunchDeduct);
            
            Shift newShift = new Shift(dailyScheduleId, description, dailySchedule);
 
            Timestamp garryTS = new Timestamp(garry.getTimeInMillis());
            System.out.println(garryTS);
            
            // QUERY 4
            
            query = "SELECT * FROM scheduleoverride WHERE badgeid is null AND start <= ? AND end is null";
            pstSelect = conn.prepareStatement(query);
            pstSelect.setTimestamp(1, garryTS);
            selects.add(pstSelect);
           
            
            
            // QUERY 5
            
            query = "SELECT * FROM scheduleoverride WHERE badgeid is null AND start <= ? AND end >= ?";
            pstSelect = conn.prepareStatement(query);
            pstSelect.setTimestamp(1, garryTS);
            pstSelect.setTimestamp(2, garryTS);
            selects.add(pstSelect);
            
            
            
            //QUERY 6
            
            query = "SELECT * FROM scheduleoverride WHERE badgeid = ? AND end is null AND start <= ?";
            pstSelect = conn.prepareStatement(query);
            pstSelect.setString(1, b.getId());
            pstSelect.setTimestamp(2, garryTS);
            selects.add(pstSelect);
            
            
            // QUERY 7
            
            query = "SELECT * FROM scheduleoverride WHERE badgeid = ? AND start <= ? AND end >= ?";
            pstSelect = conn.prepareStatement(query);
            pstSelect.setString(1, b.getId());
            pstSelect.setTimestamp(2, garryTS);
            pstSelect.setTimestamp(3, garryTS);
            selects.add(pstSelect);
              
            for(PreparedStatement select : selects ){
                pstSelect = select;      
                pstSelect.execute();
                resultset = pstSelect.getResultSet();

                while(resultset.next()) {
     
                    int day = resultset.getInt(5);
                    int newDailyScheduleId = resultset.getInt(6);

                    DailySchedule overrideDailySchedule = this.createOverrideSchedule(newDailyScheduleId);
                    newShift.setOverride(overrideDailySchedule, day);
                    
                    newShift.printWorkSchedule();
    
                }
            }
 
            return newShift;     
        }
        catch(Exception e) { 
            System.err.println("GetShift" + e.toString());
        }

        return null;
        
    }
    public int insertPunch(Punch p){
        
        try {
            int punchTypeID, termId;
            String badgeID;

            // Time Paramterers
            String timeStamp = p.getStringTimestamp();

            // Badge Parameters
            badgeID = p.getBadgeID();
            
            // Punch/Terminal  Parameters 
            punchTypeID = p.getPunchType();
            termId = p.getTerminalid();
            
            

            // Put in in the database thingy
            query = "INSERT INTO punch (terminalid, badgeid, originaltimestamp, punchtypeid) VALUES (?, ?, ?, ?)";
            
            pstUpdate = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstUpdate.setInt(1, termId);
            pstUpdate.setString(2, badgeID);
            pstUpdate.setString(3, timeStamp);
            pstUpdate.setInt(4, punchTypeID);
                
                // Execute Update Query
                
            pstUpdate.executeUpdate(); 
            resultset = pstUpdate.getGeneratedKeys();
            resultset.first();
            
             return resultset.getInt(1);
        
        }
        catch(Exception e){
            System.err.println("** insertPunch: " + e.toString());
        }
        return 0;
    }
    
    public ArrayList getDailyPunchList(Badge b, long ts){
        //create ArrayList to hold punches for a given day
        try{
        ArrayList<Punch> punches = new ArrayList<>();
        String originalTimeStamp = new SimpleDateFormat("yyyy-MM-dd").format(ts);
        query = "SELECT * FROM punch WHERE badgeid = '" + b.getId() + "' ORDER BY originaltimestamp"  ;
        pstSelect = conn.prepareStatement(query);
        pstSelect.execute();
        resultset = pstSelect.getResultSet();
        resultset.first();
        
        while(resultset.next()){
            String timestamp = resultset.getString(4);
            if(timestamp.contains(originalTimeStamp) ){
                int dbPunchID = resultset.getInt(1);
                int termID = resultset.getInt(2);
                String badgeID = resultset.getString(3);
                Timestamp dbTS = resultset.getTimestamp(4);
                long longTS = dbTS.getTime();
                int punchType = resultset.getInt(5);
                String otStamp = dbTS.toString();

                Punch p = new Punch(b, longTS, punchType, otStamp, dbPunchID, termID);

                punches.add(p);
                }
        }
        
        
        
        return punches;
        }
        
        catch(Exception e){
            System.err.println("** getDailyPunchList: " + e.toString());
        }
        
        
        return null;
    }
    
    public ArrayList getPayPeriodPunchList(Badge b, long ts){
        final long MILLIS_IN_DAY = 86400000;
        ArrayList<Punch> payPeriodPunches = new ArrayList<>();
        
        GregorianCalendar g = TASLogic.makeCal(ts);
        
        //Find day of week
        
        
        //For each day in period, parse punches
        try{
            for (int i = 1; i < 8; i++){
                String day = new SimpleDateFormat("yyyy-MM-dd").format(g.getTimeInMillis());
                

                query = "SELECT * FROM punch WHERE badgeid = '" + b.getId() + "' ORDER BY originaltimestamp" ;
                pstSelect = conn.prepareStatement(query);
                pstSelect.execute();
                resultset = pstSelect.getResultSet();
                resultset.first();

                while(resultset.next()){
                    String timestamp = resultset.getString(4);
                    if(timestamp.contains(day)){
                        int dbPunchID = resultset.getInt(1);
                        int termID = resultset.getInt(2);
                        String badgeID = resultset.getString(3);
                        Timestamp dbTS = resultset.getTimestamp(4);
                        long longTS = dbTS.getTime();
                        int punchType = resultset.getInt(5);
                        String otStamp = dbTS.toString();

                        Punch p = new Punch(b, longTS, punchType, otStamp, dbPunchID, termID);
                        

                        payPeriodPunches.add(p);
                    }
                }
                g.setTimeInMillis(g.getTimeInMillis() + MILLIS_IN_DAY);
                
            }
            return payPeriodPunches;
        }
        catch(Exception e){
            System.err.println("** getPayPeriodPunches " + e.toString());
        }
        return null;
    }
    
    public Absenteeism getAbsenteeism(Badge b, long payPeriod){
        
        GregorianCalendar greg_three = new GregorianCalendar();
        greg_three.setTimeInMillis(payPeriod);
        greg_three.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        greg_three.set(Calendar.HOUR_OF_DAY, 0);
        greg_three.set(Calendar.MINUTE, 0);
        greg_three.set(Calendar.SECOND, 0);
        
        Timestamp newTS = new Timestamp(greg_three.getTimeInMillis());
        
        try{
            query = "SELECT * FROM absenteeism WHERE badgeid = ? AND payperiod = ?";
            pstSelect = conn.prepareStatement(query);
            pstSelect.setString(1, b.getId());
            pstSelect.setTimestamp(2, newTS);
            pstSelect.execute();
            resultset = pstSelect.getResultSet();
            resultset.first();
            
            
            String badgeID = resultset.getString(1);
            Timestamp dbts = resultset.getTimestamp(2);
            Double percentage = resultset.getDouble(3);

            Absenteeism a = new Absenteeism(badgeID, dbts.getTime(), percentage);

            return a;
            
            
            
        }
        catch(Exception e){
            System.err.println("** getAbsenteesim " + e.toString());
        }
        return null;
    }
    
    public void insertAbsenteeism(Absenteeism a){
      
        GregorianCalendar greg_three = new GregorianCalendar();
        greg_three.setTimeInMillis(a.getTimeStamp());
        greg_three.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        greg_three.set(Calendar.HOUR_OF_DAY, 0);
        greg_three.set(Calendar.MINUTE, 0);
        greg_three.set(Calendar.SECOND, 0);
        
        Timestamp newTS = new Timestamp(greg_three.getTimeInMillis());
        try{
            
            query = "DELETE FROM absenteeism WHERE badgeid = ? AND payperiod = ?";
            pstSelect = conn.prepareStatement(query);
            pstSelect.setString(1, a.getID());
            pstSelect.setTimestamp(2, newTS);
            pstSelect.execute();
            resultset = pstSelect.getResultSet();
            
            query = "INSERT INTO absenteeism (badgeid,payperiod,percentage) values (?,?,?)";
            pstUpdate = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstUpdate.setString(1, a.getID());
            pstUpdate.setTimestamp(2, newTS);
            pstUpdate.setDouble(3, a.getPercentage());
            pstUpdate.executeUpdate();
            System.out.println("INSERT");
            
        }
        
        catch(Exception e){
            System.err.println("**insertAbsenteeism " + e.toString());
        }   
    }
    
    public DailySchedule createOverrideSchedule(int newDailyScheduleId){
       
        try{
            
            query = "SELECT * FROM dailyschedule WHERE id = ?";
            pstSelect = conn.prepareStatement(query);
            pstSelect.setInt(1, newDailyScheduleId);
            pstSelect.execute();
            resultset = pstSelect.getResultSet();
            resultset.first();

            Timestamp nts = resultset.getTimestamp(2);
            long newStart = nts.getTime();

            nts = resultset.getTimestamp(3);
            long newStop = nts.getTime();

            int newInterval = resultset.getInt(4);
            int newGrace_period = resultset.getInt(5);
            int newDock = resultset.getInt(6);


            nts = resultset.getTimestamp(7);
            long newLunch_start = nts.getTime();

            nts = resultset.getTimestamp(8);
            long newLunch_stop = nts.getTime();
            int newLunchDeduct = resultset.getInt(9);

            DailySchedule overrideDailySchedule = new DailySchedule(newStart, newStop, newLunch_start, newLunch_stop, newInterval, newGrace_period, newDock, newLunchDeduct);
            

            return overrideDailySchedule;
        }
        
         catch(Exception e){
            System.err.println(e.toString());
        }
        
        return null;
    }
}
