package org.example;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class MongoDBDocumentCreator {

    public static void main(String[] args) {
        // Connection string - update with your MongoDB connection details
        String connectionString = "mongodb://localhost:27017";
        
        // Create a MongoDB client
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            
            // Get the database
            MongoDatabase database = mongoClient.getDatabase("book_db");
            
            // Get the collection
            MongoCollection<Document> collection = database.getCollection("book_collection");

            // Create a list to hold multiple documents
            List<Document> documents = new ArrayList<>();

// Add first document
            documents.add(new Document()
                    .append("title", "Cloud Computing Fundamentals")
                    .append("author", "Sarah Johnson")
                    .append("year", 2023)
                    .append("pages", 275)
                    .append("categories", Arrays.asList("cloud", "computing", "aws"))
                    .append("available", true));

// Add second document
            documents.add(new Document()
                    .append("title", "Python Data Science")
                    .append("author", "Michael Chen")
                    .append("year", 2022)
                    .append("pages", 410)
                    .append("categories", Arrays.asList("python", "data science", "machine learning"))
                    .append("available", true));

// Add third document
            documents.add(new Document()
                    .append("title", "Web Development with React")
                    .append("author", "Lisa Rodriguez")
                    .append("year", 2023)
                    .append("pages", 320)
                    .append("categories", Arrays.asList("javascript", "react", "web"))
                    .append("available", false));

// Insert all documents at once
            InsertManyResult result = collection.insertMany(documents);

// Print number of inserted documents
            System.out.println("Documents inserted: " + result.getInsertedIds().size());
            
            // Display all documents after creation
            System.out.println("\n--- All Documents After Creation ---");
            displayAllDocuments(collection);
            
            // Update author name from "Sarah Johnson" to "Lana Kenson"
            UpdateResult updateResult = collection.updateOne(
                eq("author", "Sarah Johnson"), 
                set("author", "Lana Kenson")
            );
            
            System.out.println("\nDocuments updated: " + updateResult.getModifiedCount());
            
            // Verify the update by finding the document
            Document updatedDoc = collection.find(eq("author", "Lana Kenson")).first();
            if (updatedDoc != null) {
                System.out.println("Updated document: " + updatedDoc.toJson());
            } else {
                System.out.println("No document found with author 'Lana Kenson'");
            }
            
            // Display all documents after update
            System.out.println("\n--- All Documents After Update ---");
            displayAllDocuments(collection);
            
            // Delete the book "Web Development with React"
            DeleteResult deleteResult = collection.deleteOne(eq("title", "Web Development with React"));
            
            System.out.println("\nDocuments deleted: " + deleteResult.getDeletedCount());
            
            // Display all documents after deletion
            System.out.println("\n--- All Documents After Deletion ---");
            displayAllDocuments(collection);
            
            System.out.println("\nDocument operations completed successfully!");
        } catch (Exception e) {
            System.err.println("Error creating document: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to display all documents in a collection
     */
    private static void displayAllDocuments(MongoCollection<Document> collection) {
        FindIterable<Document> documents = collection.find();
        int count = 0;
        
        try (MongoCursor<Document> cursor = documents.iterator()) {
            while (cursor.hasNext()) {
                count++;
                Document doc = cursor.next();
                System.out.println(count + ". " + doc.toJson());
            }
        }
        
        if (count == 0) {
            System.out.println("No documents found in the collection.");
        }
    }
} 