package src;

public class Badge {
    
    private String name;
    private String id;
    
    public Badge(String n, String id){
        this.name = n;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
    
    public String toString(){
        String string = "";
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(this.getId() + " " + "(");
        sb.append(this.getName() + ")");
        string = sb.toString();
        
        return string;
    }
    
    
    
}
