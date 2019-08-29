package souza.edenilson.DataBase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Context;

public class AppDataBase {

    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference databaseReference;

    public static FirebaseDatabase GetInstance() {
        if(firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
        }
        return firebaseDatabase;
    }

    public static DatabaseReference GetReference() {
        if(databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    public static DatabaseReference GetReference(String tabela) {
        if(databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference(tabela);
        }
        return databaseReference;
    }

}
