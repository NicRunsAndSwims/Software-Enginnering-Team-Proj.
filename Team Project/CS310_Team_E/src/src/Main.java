
package src;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;




public class Main {
   
    public static void main(String[] args){
        
        TASDatabase db = new TASDatabase();
        
     
        GregorianCalendar gc = new GregorianCalendar();
        Badge b = db.getBadge("3282F212");
        
        /* PART ONE */
        
        /* Get Shift Object for Pay Period Starting 09-09-2018 (regular Shift 1 schedule) */
        
        gc.set(Calendar.DAY_OF_MONTH, 9);
        gc.set(Calendar.YEAR, 2018);
        gc.set(Calendar.MONTH, 8);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        
        Shift s = db.getShift(b, gc.getTimeInMillis());
        
        /* Retrieve Punch List #1 */
        
        ArrayList<Punch> p8 = db.getPayPeriodPunchList(b, gc.getTimeInMillis());
        
        /* Adjust Punches */
        
        for (Punch p : p8) {
            p.adjust(s);
        }
        
        /* Calculate Pay Period 09-09-2018 Absenteeism */
        
        double percentage = TASLogic.calculateAbsenteeism(p8, s);
        Absenteeism a8 = new Absenteeism(b.getId(), gc.getTimeInMillis(), percentage);
        
        System.out.println(a8);
        s.printWorkSchedule();
        System.out.println("\n\n"); 
        
    }
    
}
