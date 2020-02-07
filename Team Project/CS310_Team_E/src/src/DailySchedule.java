
package src;

public class DailySchedule {
    
    private long start; //Start Time in ms
    private long stop;
    private long lunch_start;
    private long lunch_stop;// End Time in ms
    private int interval;
    private int grace_period;
    private int dock;
    private int lunchDeduct;
    
    public DailySchedule(
            long start, 
            long stop, 
            long lunch_start, 
            long lunch_stop, 
            int interval, 
            int grace_period, 
            int dock, 
            int lunchDeduct){
        
        this.start = start;
        this.stop = stop;
        this.lunch_start = lunch_start;
        this.lunch_stop = lunch_stop;
        this.interval = interval;
        this.grace_period = grace_period;
        this.dock = dock;
        this.lunchDeduct = lunchDeduct;
        
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStop() {
        return stop;
    }

    public void setStop(long stop) {
        this.stop = stop;
    }

    public long getLunch_start() {
        return lunch_start;
    }

    public void setLunch_start(long lunch_start) {
        this.lunch_start = lunch_start;
    }

    public long getLunch_stop() {
        return lunch_stop;
    }

    public void setLunch_stop(long lunch_stop) {
        this.lunch_stop = lunch_stop;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getGrace_period() {
        return grace_period;
    }

    public void setGrace_period(int grace_period) {
        this.grace_period = grace_period;
    }

    public int getDock() {
        return dock;
    }

    public void setDock(int dock) {
        this.dock = dock;
    }

    public int getLunchDeduct() {
        return lunchDeduct;
    }

    public void setLunchDeduct(int lunchDeduct) {
        this.lunchDeduct = lunchDeduct;
    }
    
    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
        output.append("Start: " + this.getStart());
        return output.toString();
    }
    
    
}
