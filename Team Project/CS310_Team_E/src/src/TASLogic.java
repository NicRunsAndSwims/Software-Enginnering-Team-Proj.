
package src;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.json.simple.JSONValue;

public class TASLogic {
    
    public static int calculateTotalMinutes(ArrayList<Punch> punchList, Shift shift){
        
        int minutes = 0;
        boolean inPair = false;
        final int CLOCK_OUT = 0;
        final int CLOCK_IN = 1;
        final int TIME_OUT = 2;
        final int CONVERT_TO_MINUTES = 60000;
        Punch previousPunch = null;
        
        for(Punch p : punchList){
            if(inPair == false){
                if(p.getPunchType() == CLOCK_IN){
                    previousPunch = p;
                    inPair = true;
                }
            }
            
            else if(inPair == true){
                if(p.getPunchType() == CLOCK_OUT){
                    minutes += (int)(p.getATS() - previousPunch.getATS());
                    minutes = minutes / CONVERT_TO_MINUTES;
                    
                }
            }
        }
        
        if(minutes > shift.getLunchDeduct()){
            minutes -= shift.getLunchLength();
        }
        
        return minutes;
    }
    
    
    public static String getPunchListAsJSON(ArrayList<Punch> dailyPunchList){
        ArrayList<HashMap<String, String>> jsonData = new ArrayList();
        String results;
        
        for(Punch p : dailyPunchList){
            
            HashMap<String, String> punchData = new HashMap();
            
            punchData.put("id", String.valueOf(p.getPunchID()));
            punchData.put("badgeid", p.getBadgeID());
            punchData.put("terminalid", String.valueOf(p.getTerminalid()));
            punchData.put("punchtypeid", String.valueOf(p.getPunchType()));
            punchData.put("punchdata", p.getAdjustData());
            punchData.put("originaltimestamp", String.valueOf(p.getOTS()));
            punchData.put("adjustedtimestamp", String.valueOf(p.getATS()));
            
            jsonData.add(punchData);
        }
        
        results = JSONValue.toJSONString(jsonData);
        
        return results;
        
    }
    public static double calculateAbsenteeism(ArrayList<Punch> punchlist, Shift shift) {
       double totalAccuredTime = 0;
       double totalShiftTime = 0;
       double percent = 0.0;
       
       System.out.println("ArrayList size: " + punchlist.size());
       
       ArrayList<ArrayList<Punch>> dailyPunchLists = new ArrayList<>(); //ArrayList to contain parsed DailyPunchLists
       
      
       for(int i = 1; i < 8; i++){
           ArrayList<Punch> dayOfPunches = new ArrayList<>();
           for(int j = 0 ; j < punchlist.size(); j++){
               if(punchlist.get(j).getDayOfWeek() == i)
                   dayOfPunches.add(punchlist.get(j));
           }
           System.out.println(dayOfPunches);
           if(!dayOfPunches.isEmpty())
                dailyPunchLists.add(dayOfPunches);
            for(int k = 0; k < dayOfPunches.size(); k ++){
                System.out.println(dayOfPunches.get(k).toSrting());
            }    
            
       }
       
       for(int i = 0; i < dailyPunchLists.size(); i++){ //Loop through Complete dailyPunchLists and Calc total AccuredTime and totalShiftTime
          totalAccuredTime += TASLogic.calculateTotalMinutes(dailyPunchLists.get(i), shift); 
        }
       for(int j = Calendar.MONDAY; j < Calendar.SATURDAY; ++j){
              totalShiftTime += (shift.getShiftLength(j) - shift.getLunchLength(j));
        }
        
       System.out.println("Time actually worked: " + totalAccuredTime);
       System.out.println("Shift time in minutes: " + totalShiftTime);
             
       percent = 100 - ((totalAccuredTime/totalShiftTime)* 100); //Calc absenteeism as percentage\
       
       System.out.println("Percentage: " + percent);
  
       return percent;
    }
    
    public static String getPunchListPlusTotalsAsJSON(ArrayList<Punch> punchlist, Shift s){
        ArrayList<HashMap<String, String>> jsonData = new ArrayList();
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String results;
        double absenteeism = 0;
        int totalMinutes = 0;
        
        
        for(Punch p : punchlist){
            
            HashMap<String, String> punchData = new HashMap();
            
            punchData.put("id", String.valueOf(p.getPunchID()));
            punchData.put("badgeid", p.getBadgeID());
            punchData.put("terminalid", String.valueOf(p.getTerminalid()));
            punchData.put("punchtypeid", String.valueOf(p.getPunchType()));
            punchData.put("punchdata", p.getAdjustData());
            punchData.put("originaltimestamp", String.valueOf(p.getOTS()));
            punchData.put("adjustedtimestamp", String.valueOf(p.getATS()));
            
            jsonData.add(punchData);
        }
        
        ArrayList<ArrayList<Punch>> dailyPunchLists = new ArrayList<>(); //ArrayList to contain parsed DailyPunchLists

            for(int i = 1; i < 8; i++){
                ArrayList<Punch> dayOfPunches = new ArrayList<>();
                for(int j = 0 ; j < punchlist.size(); j++){
                    if(punchlist.get(j).getDayOfWeek() == i)
                        dayOfPunches.add(punchlist.get(j));
                }
                if(!dayOfPunches.isEmpty())
                     dailyPunchLists.add(dayOfPunches);
            }

            for(int i = 0; i < dailyPunchLists.size(); i++){ //Loop through Complete dailyPunchLists and Calc total AccuredTime and totalShiftTime
               totalMinutes += TASLogic.calculateTotalMinutes(dailyPunchLists.get(i), s);
            }
        
        
            HashMap<String, String> extraData = new HashMap();
            absenteeism = TASLogic.calculateAbsenteeism(punchlist, s);
            
            extraData.put("absenteeism", String.valueOf(decimalFormat.format(absenteeism)) + "%");
            extraData.put("totalminutes", String.valueOf(totalMinutes));
            
            jsonData.add(extraData);

        
        results = JSONValue.toJSONString(jsonData);
        
        return results;
    }
    public static GregorianCalendar makeCal(long ts) {
        
        //Create greg object
        GregorianCalendar g = new GregorianCalendar();
        g.setTimeInMillis(ts);
        g.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        g.set(Calendar.HOUR_OF_DAY, 0);
        g.set(Calendar.MINUTE, 0);
        g.set(Calendar.SECOND, 0);
        
        return g;
        
    }
}
