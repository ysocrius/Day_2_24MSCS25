package org.example;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Scanner;

public class MongoDB_Bank {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "bank";
    private static final String COLLECTION_NAME = "bank_collection";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(CONNECTION_STRING)) {

            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            

            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
            

            createDefaultAccountIfNotExists(collection);
            
            boolean exit = false;
            while (!exit) {
                displayMenu();
                int choice = getUserChoice();
                
                switch (choice) {
                    case 1:
                        deposit(collection);
                        break;
                    case 2:
                        withdraw(collection);
                        break;
                    case 3:
                        viewBalance(collection);
                        break;
                    case 4:
                        System.out.println("Thank you for using MongoDB Bank. Goodbye!");
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    private static void displayMenu() {
        System.out.println("\n===== MongoDB Bank =====");
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. View Balance");
        System.out.println("4. Exit");
        System.out.print("Enter your choice (1-4): ");
    }
    
    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0; // Invalid choice
        }
    }
    
    private static void createDefaultAccountIfNotExists(MongoCollection<Document> collection) {

        long count = collection.countDocuments();
        
        if (count == 0) {

            Document account = new Document()
                    .append("accountId", "ACC001")
                    .append("accountHolder", "Default User")
                    .append("balance", 0.0)
                    .append("createdAt", System.currentTimeMillis());
            
            InsertOneResult result = collection.insertOne(account);
            System.out.println("Default account created with ID: " + result.getInsertedId());
        }
    }
    
    private static Document getDefaultAccount(MongoCollection<Document> collection) {
        return collection.find(Filters.eq("accountId", "ACC001")).first();
    }
    
    private static void deposit(MongoCollection<Document> collection) {
        System.out.print("Enter amount to deposit: $");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            
            if (amount <= 0) {
                System.out.println("Invalid amount. Amount must be positive.");
                return;
            }
            
            Document account = getDefaultAccount(collection);
            double currentBalance = account.getDouble("balance");
            double newBalance = currentBalance + amount;
            
            Bson filter = Filters.eq("accountId", "ACC001");
            Bson update = Updates.set("balance", newBalance);
            
            UpdateResult result = collection.updateOne(filter, update);
            
            if (result.getModifiedCount() > 0) {
                System.out.printf("Deposit successful. $%.2f added to your account.\n", amount);
                System.out.printf("New balance: $%.2f\n", newBalance);
            } else {
                System.out.println("Deposit failed. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format. Please enter a valid number.");
        }
    }
    
    private static void withdraw(MongoCollection<Document> collection) {
        System.out.print("Enter amount to withdraw: $");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            
            if (amount <= 0) {
                System.out.println("Invalid amount. Amount must be positive.");
                return;
            }
            
            Document account = getDefaultAccount(collection);
            double currentBalance = account.getDouble("balance");
            
            if (amount > currentBalance) {
                System.out.println("Insufficient funds. Your current balance is $" + currentBalance);
                return;
            }
            
            double newBalance = currentBalance - amount;
            
            Bson filter = Filters.eq("accountId", "ACC001");
            Bson update = Updates.set("balance", newBalance);
            
            UpdateResult result = collection.updateOne(filter, update);
            
            if (result.getModifiedCount() > 0) {
                System.out.printf("Withdrawal successful. $%.2f withdrawn from your account.\n", amount);
                System.out.printf("New balance: $%.2f\n", newBalance);
            } else {
                System.out.println("Withdrawal failed. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format. Please enter a valid number.");
        }
    }
    
    private static void viewBalance(MongoCollection<Document> collection) {
        Document account = getDefaultAccount(collection);
        
        if (account != null) {
            String accountId = account.getString("accountId");
            String accountHolder = account.getString("accountHolder");
            double balance = account.getDouble("balance");
            
            System.out.println("\n===== Account Details =====");
            System.out.println("Account ID: " + accountId);
            System.out.println("Account Holder: " + accountHolder);
            System.out.printf("Current Balance: $%.2f\n", balance);
        } else {
            System.out.println("Account not found.");
        }
    }
} 