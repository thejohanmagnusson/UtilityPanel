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

    private String phone;
    public String getPhone() {
        return phone;
    }

    public Contact(){
        // Default constructor required for calls to DataSnapshot.getValue(Contact.class)
    }

    public Contact(String name, String phone){
        this.name = name;
        this.phone = phone;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("phone", phone);

        return result;
    }
}
