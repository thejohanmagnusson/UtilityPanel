package android.johanmagnusson.se.utilitypanel;

import android.johanmagnusson.se.utilitypanel.model.Contact;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    private TextView iconView;
    public TextView nameView;

    public ContactViewHolder(View itemView) {
        super(itemView);

        iconView = (TextView) itemView.findViewById(R.id.icon_text);
        nameView = (TextView) itemView.findViewById(R.id.contact_name);
    }

    public void bindToModel(Contact contact) {
        if(!contact.getName().isEmpty()) {
            String firstLetter = contact.getName().substring(0, 1);
            iconView.setText(firstLetter);
        }

        nameView.setText(contact.getName());
    }
}
