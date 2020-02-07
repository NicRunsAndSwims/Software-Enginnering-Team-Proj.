
package src;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class Absenteeism {
    
    private String ID;
    private long timeStamp;
    private double percentage;
    
    public Absenteeism(String ID, long TS, double percent){
        
        this.ID = ID;
        
        GregorianCalendar adjustTS = new GregorianCalendar();
        adjustTS.setTimeInMillis(TS);
        adjustTS.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        adjustTS.set(Calendar.HOUR_OF_DAY, 0);
        adjustTS.set(Calendar.MINUTE, 0);
        adjustTS.set(Calendar.SECOND, 0);
        
        this.timeStamp = adjustTS.getTimeInMillis();
        this.percentage =  percent;
        
        
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
    
    public String toString(){
        
        StringBuilder output = new StringBuilder();
        String startPayPeriod = new SimpleDateFormat("MM-dd-YYYY").format(this.getTimeStamp());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String percentageAsString = decimalFormat.format(percentage);
        
        
        output.append("#" + ID);
        output.append(" (Pay Period Starting " + startPayPeriod + "): ");
        output.append(percentageAsString + "%");
        
        return output.toString();
    }
    
}
