package android.johanmagnusson.se.utilitypanel.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class Contact {

    private String name;
    public String getName() {
        return name;
    }

    private String phonenumber;
    public String getPhonenumber() {
        return phonenumber;
    }

    public Contact(){
        // Default constructor required for calls to DataSnapshot.getValue(Contact.class)
    }

    public Contact(String name, String phonenumber){
        this.name = name;
        this.phonenumber = phonenumber;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("phonenumber", phonenumber);

        return result;
    }
}
