package com.mpstmeacm;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.ArrayList;

public class MongoDBUtils {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static void initialize() throws IOException {
        String connectionString = Config.getConnectionString();
        String databaseName = "acm";

        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase(databaseName);
    }

    public static void storeUserDetails(String fName, String email, String departmentNo, String inviteCode) {
        MongoCollection<Document> userCollection = database.getCollection("users");

        Document user = new Document("fName", fName)
                .append("departmentNo", departmentNo)
                .append("inviteCode", inviteCode);

        CompletableFuture.runAsync(() -> userCollection.updateOne(
                new Document("_id", email),
                new Document("$set", user),
                new UpdateOptions().upsert(true)
        )).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    public static Document getUserDetailsByInviteCode(String inviteCode) throws ExecutionException, InterruptedException {
        MongoCollection<Document> userCollection = database.getCollection("users");

        CompletableFuture<List<Document>> future = CompletableFuture.supplyAsync(() -> {
            List<Document> results = new ArrayList<>();
            for (Document document : userCollection.find()) {
                if (inviteCode.equals(document.getString("inviteCode"))) {
                    results.add(document);
                }
            }
            return results;
        });

        List<Document> documents = future.get();
        return documents.isEmpty() ? null : documents.get(0);
    }
}
