package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strings"

	"github.com/segmentio/kafka-go"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

type SMS struct {
	UserID      string `bson:"userId" json:"userId"`
	PhoneNumber string `bson:"phoneNumber" json:"phoneNumber"`
	Message     string `bson:"message" json:"message"`
	Status      string `bson:"status" json:"status"`
}

var collection *mongo.Collection

func main() {

	// MongoDB connection
	client, err := mongo.Connect(context.TODO(),
		options.Client().ApplyURI("mongodb://localhost:27017"))
	if err != nil {
		log.Fatal(err)
	}

	collection = client.Database("smsdb").Collection("messages")

	// Start Kafka consumer in background
	go consumeKafka()

	// HTTP route
	http.HandleFunc("/v1/messages", getMessages)
	http.HandleFunc("/internal/store", storeHandler)
	http.HandleFunc("/v1/user/", getUserMessages)

	fmt.Println("Go Server running on port 8081...")
	http.ListenAndServe(":8081", nil)
}

func consumeKafka() {

	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers: []string{"localhost:9092"},
		Topic:   "sms-topic",
		GroupID: "sms-group",
	})

	for {
		msg, err := reader.ReadMessage(context.Background())
		if err != nil {
			log.Fatal(err)
		}

		data := SMS{
			Message: string(msg.Value),
		}

		_, err = collection.InsertOne(context.TODO(), data)
		if err != nil {
			log.Fatal(err)
		}

		fmt.Println("Saved:", data.Message)
	}
}

func getMessages(w http.ResponseWriter, r *http.Request) {

	cursor, err := collection.Find(context.TODO(), bson.M{})
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}

	var results []SMS
	if err = cursor.All(context.TODO(), &results); err != nil {
		http.Error(w, err.Error(), 500)
		return
	}

	json.NewEncoder(w).Encode(results)
}

func storeHandler(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "Only POST allowed", http.StatusMethodNotAllowed)
		return
	}

	var data SMS

	err := json.NewDecoder(r.Body).Decode(&data)
	if err != nil {
		http.Error(w, "Invalid JSON", http.StatusBadRequest)
		return
	}

	// basic validation
	if data.UserID == "" || data.PhoneNumber == "" || data.Message == "" {
		http.Error(w, "Missing fields", http.StatusBadRequest)
		return
	}

	data.Status = "stored"

	_, err = collection.InsertOne(context.TODO(), data)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	fmt.Println("Stored via HTTP:", data)

	w.WriteHeader(http.StatusCreated)
	w.Write([]byte("stored"))
}

func getUserMessages(w http.ResponseWriter, r *http.Request) {

	path := r.URL.Path
	parts := strings.Split(path, "/")

	if len(parts) < 4 {
		http.Error(w, "Invalid URL", 400)
		return
	}

	userId := parts[3]

	filter := bson.M{"userId": userId}

	cursor, err := collection.Find(context.TODO(), filter)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}

	var results []SMS
	if err = cursor.All(context.TODO(), &results); err != nil {
		http.Error(w, err.Error(), 500)
		return
	}

	json.NewEncoder(w).Encode(results)
}