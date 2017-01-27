package android.johanmagnusson.se.utilitypanel.model;

public class Group {

    private String name;
    public String getName() {
        return name;
    }

    private String logo;
    public String getLogo() {
        return logo;
    }

    public Group(){
        // Default constructor required for calls to DataSnapshot.getValue(Group.class)
    }
}
