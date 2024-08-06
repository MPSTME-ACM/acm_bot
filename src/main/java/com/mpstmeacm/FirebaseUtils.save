package com.mpstmeacm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseUtils {
    private static Firestore db;

    public static void initialize() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(Config.getFirebaseConfigPath());

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();
    }

    public static void storeUserDetails(String fName, String email, String departmentNo, String inviteCode) {
        DocumentReference userRef = db.collection("users").document(email);
        Map<String, Object> user = new HashMap<>();
        user.put("fName", fName);
        user.put("departmentNo", departmentNo);
        user.put("inviteCode", inviteCode);

        try {
            userRef.set(user).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static DocumentSnapshot getUserDetailsByInviteCode(String inviteCode) throws ExecutionException, InterruptedException {
        for (DocumentSnapshot document : db.collection("users").get().get().getDocuments()) {
            if (document.getString("inviteCode").equals(inviteCode)) {
                return document;
            }
        }
        return null;
    }
}
